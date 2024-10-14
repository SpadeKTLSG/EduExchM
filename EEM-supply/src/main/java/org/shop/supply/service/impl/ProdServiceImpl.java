package org.shop.supply.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.supply.client.OrderClient;
import org.shop.supply.common.constant.MessageConstant;
import org.shop.supply.common.constant.RedisConstant;
import org.shop.supply.common.constant.ServiceConstant;
import org.shop.supply.common.constant.SystemConstant;
import org.shop.supply.common.context.UserHolder;
import org.shop.supply.common.exception.AccountNotFoundException;
import org.shop.supply.common.exception.BadArgsException;
import org.shop.supply.common.exception.SthHasCreatedException;
import org.shop.supply.common.exception.SthNotFoundException;
import org.shop.supply.entity.Prod;
import org.shop.supply.entity.ProdCate;
import org.shop.supply.entity.ProdFunc;
import org.shop.supply.entity.dto.*;
import org.shop.supply.entity.remote.Order;
import org.shop.supply.entity.res.RedisData;
import org.shop.supply.entity.vo.ProdAllVO;
import org.shop.supply.entity.vo.ProdGreatVO;
import org.shop.supply.mapper.ProdMapper;
import org.shop.supply.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.shop.supply.common.utils.NewBeanUtil.prodDtoMapService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod> implements ProdService {

    /**
     * 线程池[缓存击穿问题], 完成重构缓存
     */
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);

    private final ProdFuncService prodFuncService;
    private final ProdCateService prodCateService;
    private final UpshowService upshowService;
    private final RotationService rotationService;
    private final HotsearchService hotsearchService;


    private final OrderClient orderClient;
    private final ProdMapper prodMapper;

    //! Func

    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void checkA(ProdLocateDTO prodLocateDTO) {

        Prod prod = this.getOne(new LambdaQueryWrapper<Prod>()// 找到对应商品id, 通过id找到另一张表UserFunc, 修改状态字段
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId())
        );

        ProdFunc prodFunc = prodFuncService.getOne(new LambdaQueryWrapper<ProdFunc>()
                .eq(ProdFunc::getId, prod.getId())
        );

        prodFunc.setStatus(ProdFunc.NORMAL);
        prodFuncService.updateById(prodFunc);
    }


    @Override
    public void freezeA(ProdLocateDTO prodLocateDTO) {

        Prod prod = this.getOne(new LambdaQueryWrapper<Prod>()// 找到对应商品id, 通过id找到另一张表UserFunc, 修改状态字段
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId())
        );

        ProdFunc prodFunc = prodFuncService.getOne(new LambdaQueryWrapper<ProdFunc>()
                .eq(ProdFunc::getId, prod.getId())
        );

        prodFunc.setStatus(ProdFunc.FROZEN);
        prodFuncService.updateById(prodFunc);

        //同时需要将商品从首页提升榜单和首页轮播图中移除
        if (!Objects.equals(prodFunc.getShowoffStatus(), ProdFunc.BASIC)) {
            if (Objects.equals(prodFunc.getShowoffStatus(), ProdFunc.SENIOR)) {
                upshowService.remove4Upshow(prodLocateDTO);
            } else {
                upshowService.remove4Upshow(prodLocateDTO);
                rotationService.remove4Rotation(prodLocateDTO);
            }
        }

    }


    @Override
    public Page<ProdGreatVO> page2CheckA(Integer current) {

        Page<ProdFunc> prodFuncPage = prodFuncService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE),
                new LambdaQueryWrapper<ProdFunc>().eq(ProdFunc::getStatus, 0));

        List<ProdGreatVO> mergedList = new ArrayList<>();

        for (ProdFunc prodFunc : prodFuncPage.getRecords()) {
            Prod prod = this.getById(prodFunc.getId());
            if (prod != null) {
                ProdGreatVO prodGreatVO = new ProdGreatVO();
                BeanUtils.copyProperties(prod, prodGreatVO);
                BeanUtils.copyProperties(prodFunc, prodGreatVO);
                mergedList.add(prodGreatVO);
            }
        }

        Page<ProdGreatVO> mergedPage = new Page<>(current, SystemConstant.MAX_PAGE_SIZE);
        mergedPage.setRecords(mergedList);
        mergedPage.setTotal(mergedList.size());

        return mergedPage;
    }


    @Override
    public List<ProdFunc> getOutdateProdA(LocalDateTime time) {

        List<ProdFunc> prodList2Check = prodFuncService.query() //需要保证其ShowoffEndtime存在!
                .isNotNull("showoff_endtime") //这里是为了保证不会出现空指针异常
                .list();

        prodList2Check.removeIf(prodFunc -> prodFunc.getShowoffEndtime().isAfter(time));   //需要手动取出来判断是否过期

        return prodList2Check;
    }


    @Override
    public List<ProdFunc> getHotProdA() {

        List<ProdFunc> prodList2Check = prodFuncService
                .query()
                .orderByDesc("visit")
                .last("limit " + SystemConstant.MAX_PAGE_SIZE)
                .list();

        return prodList2Check;
    }


    @Override
    public void cooldownUpshowProdA(ProdFunc prodFunc) {

        prodFunc.setShowoffStatus(0);
        prodFunc.setShowoffEndtime(LocalDateTime.now()); //只能设置为现在而不是null否则报错

        UpshowAllDTO upshowAllDTO = UpshowAllDTO.builder()
                .prodId(prodFunc.getId())
                .name(this.query().eq("id", prodFunc.getId()).one().getName())
                .build();

        prodFuncService.updateById(prodFunc);
        upshowService.remove4Upshow(upshowAllDTO);
    }


    @Override
    public void cooldownRotationProdA(ProdFunc prodFunc) {

        prodFunc.setShowoffStatus(0);
        prodFunc.setShowoffEndtime(LocalDateTime.now()); //只能设置为现在而不是null否则报错

        RotationAllDTO rotationAllDTO = RotationAllDTO.builder()
                .prodId(prodFunc.getId())
                .name(this.query().eq("id", prodFunc.getId()).one().getName())
                .build();

        prodFuncService.updateById(prodFunc);
        rotationService.remove4Rotation(rotationAllDTO);
    }


    @Override
    public void add2HotSearchA(ProdFunc prodFunc) {

        HotsearchAllDTO hotsearchAllDTO = HotsearchAllDTO.builder()
                .visit(prodFunc.getVisit())
                .prodId(prodFunc.getId())
                .name(this.query().eq("id", prodFunc.getId()).one().getName())
                .build();

        hotsearchService.add2Hotsearch(hotsearchAllDTO);
    }


    //! ADD

    @Override
    @Transactional
    public void postProdG(ProdGreatDTO prodGreatDTO) {
        if (this.query().eq("name", prodGreatDTO.getName()).count() > 0) throw new SthHasCreatedException(MessageConstant.OBJECT_HAS_ALIVE);

        Prod prod = new Prod();
        ProdFunc prodFunc = new ProdFunc();

        BeanUtils.copyProperties(prodGreatDTO, prod);
        BeanUtils.copyProperties(prodGreatDTO, prodFunc);

        prod.setUserId(UserHolder.getUser().getId());

        this.save(prod);
        prodFuncService.save(prodFunc);

        //还需要添加Redis Key
        stringRedisTemplate.opsForValue().set(RedisConstant.SECKILL_STOCK_KEY + prod.getId(), prod.getStock().toString());
    }


    //! DELETE


    @Override
    public void deleteProdA(ProdLocateDTO prodLocateDTO) {
        this.remove(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId())
        );
    }


    @Override
    @Transactional
    public void deleteProdG(String name) {
        Prod prod = this.getOne(Wrappers.<Prod>lambdaQuery().eq(Prod::getName, name));
        if (prod == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        //需要判断是否有已经开启的交易
        Order order = orderClient.getOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getProdId, prod.getId())
                .ne(Order::getStatus, Order.OVER) //已经完成的交易不算
                .ne(Order::getStatus, Order.STOP) //已经撤销的交易不算
        );

        if (order != null) throw new SthHasCreatedException(MessageConstant.ORDER_STATUS_ERROR);

        prodFuncService.removeById(prod.getId());
        this.removeById(prod.getId());

        //还需要删除Redis Key
        stringRedisTemplate.delete(RedisConstant.SECKILL_STOCK_KEY + prod.getId());
    }


    //! UPDATE


    @Override
    @Transactional
    public void putProdG(ProdGreatDTO prodGreatDTO) throws InstantiationException, IllegalAccessException {
        // 联表选择性更新
        Optional<Prod> optionalProd = Optional.ofNullable(this.getOne(Wrappers.<Prod>lambdaQuery().eq(Prod::getName, prodGreatDTO.getName())));
        if (optionalProd.isEmpty()) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        optionalProd.get().setUserId(UserHolder.getUser().getId());//这里商品的userId是自己

        Map<Object, IService> dtoServiceMap = new HashMap<>();
        dtoServiceMap.put(createDTOFromProdGreatDTO(prodGreatDTO, ProdAllDTO.class), this);
        dtoServiceMap.put(createDTOFromProdGreatDTO(prodGreatDTO, ProdFuncAllDTO.class), prodFuncService);

        prodDtoMapService(dtoServiceMap, optionalProd.get().getId(), optionalProd);
    }


    @Override
    public void putProd8CG(ProdGreatDTO prodGreatDTO) throws InstantiationException, IllegalAccessException {

        // 联表选择性更新
        Optional<Prod> optionalProd = Optional.ofNullable(this.getOne(Wrappers.<Prod>lambdaQuery().eq(Prod::getName, prodGreatDTO.getName())));
        if (optionalProd.isEmpty()) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        optionalProd.get().setUserId(UserHolder.getUser().getId());//这里商品的userId是自己

        Map<Object, IService> dtoServiceMap = new HashMap<>();
        dtoServiceMap.put(createDTOFromProdGreatDTO(prodGreatDTO, ProdAllDTO.class), this);
        dtoServiceMap.put(createDTOFromProdGreatDTO(prodGreatDTO, ProdFuncAllDTO.class), prodFuncService);

        prodDtoMapService(dtoServiceMap, optionalProd.get().getId(), optionalProd);
        //当然, 使用反射会造成性能损失, 这里直接写死用join也是可以的(另见getUser4MeG)

        //包含缓存的更新逻辑, 在更新数据库之后更新缓存(正常流程后)

        String key = RedisConstant.CACHE_PROD_KEY + prodGreatDTO.getUserId() + ":" + prodGreatDTO.getName();
        stringRedisTemplate.delete(key);
    }


    //! QUERY


    @Override
    public Prod getProd8EzA(ProdLocateDTO prodLocateDTO) {
        return this.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId())
        );
    }


    @Override
    public Page<ProdGreatVO> pageProdA(Integer current) {

        Page<Prod> prodPage = this.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE));
        Page<ProdFunc> prodFuncPage = prodFuncService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE));

        // 存储合并后的结果
        List<ProdGreatVO> mergedList = new ArrayList<>();

        for (int i = 0; i < prodPage.getRecords().size(); i++) { //合并两个Page的Records
            Prod prod = prodPage.getRecords().get(i);
            ProdFunc prodFunc = prodFuncPage.getRecords().get(i);

            ProdGreatVO prodGreatVO = new ProdGreatVO();
            BeanUtils.copyProperties(prod, prodGreatVO);
            BeanUtils.copyProperties(prodFunc, prodGreatVO);
            mergedList.add(prodGreatVO);
        }

        Page<ProdGreatVO> mergedPage = new Page<>(current, SystemConstant.MAX_PAGE_SIZE);
        mergedPage.setRecords(mergedList);
        mergedPage.setTotal(prodPage.getTotal() + prodFuncPage.getTotal());

        return mergedPage;
    }


    @Override
    public Page<ProdGreatVO> searchProdA(String name, Integer current) {

        Page<Prod> prodPage = this.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE),
                Wrappers.<Prod>lambdaQuery()
                        .like(Prod::getName, name));


        List<Long> ids = new ArrayList<>();     //通过prodPage中的id找到对应的ProdFunc对象
        for (Prod prod : prodPage.getRecords()) {
            ids.add(prod.getId());
        }

        Page<ProdFunc> prodFuncPage = prodFuncService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE),
                Wrappers.<ProdFunc>lambdaQuery()
                        .in(ProdFunc::getId, ids));


        // 存储合并后的结果
        List<ProdGreatVO> mergedList = new ArrayList<>();

        for (int i = 0; i < prodPage.getRecords().size(); i++) { //合并两个Page的Records
            Prod prod = prodPage.getRecords().get(i);
            ProdFunc prodFunc = prodFuncPage.getRecords().get(i);

            ProdGreatVO prodGreatVO = new ProdGreatVO();
            BeanUtils.copyProperties(prod, prodGreatVO);
            BeanUtils.copyProperties(prodFunc, prodGreatVO);
            mergedList.add(prodGreatVO);
        }

        Page<ProdGreatVO> mergedPage = new Page<>(current, SystemConstant.MAX_PAGE_SIZE);
        mergedPage.setRecords(mergedList);
        mergedPage.setTotal(prodPage.getTotal() + prodFuncPage.getTotal());

        return mergedPage;
    }


    @Override
    @Transactional
    public void putProdStatusG(ProdLocateDTO prodLocateDTO, Integer func) {

        String name = prodLocateDTO.getName();
        Long userId = prodLocateDTO.getUserId();

        if (name == null || userId == null) throw new BadArgsException(MessageConstant.BAD_ARGS);

        Prod prod = this.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, name)
                .eq(Prod::getUserId, userId)
        );

        if (prod == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        ProdFunc prodFunc = prodFuncService.getOne(new LambdaQueryWrapper<ProdFunc>()
                .eq(ProdFunc::getId, prod.getId())
        );


        //商品修改
        prodFunc.setShowoffStatus(func);
        prodFunc.setShowoffEndtime(LocalDateTime.now().plusDays(ServiceConstant.UPSHOW_LEVEL_TTL[func]));
        prodFuncService.updateById(prodFunc);

        //展示提升增加
        if (func >= 1) {
            UpshowAllDTO upshowAllDTO = UpshowAllDTO.builder()
                    .prodId(prod.getId())
                    .name(prod.getName())
                    .build();
            upshowService.add2Upshow(upshowAllDTO);
        }

        if (func == 2) {
            RotationAllDTO rotationAllDTO = RotationAllDTO.builder()
                    .prodId(prod.getId())
                    .name(prod.getName())
                    .picture(prod.getImages())
                    .weight(prodFunc.getWeight())
                    .build();
            rotationService.add2Rotation(rotationAllDTO);
        }
    }


    @Override
    @Transactional
    public ProdGreatVO getProdG(ProdLocateDTO prodLocateDTO) {

        Prod prod = this.getOne(Wrappers.<Prod>lambdaQuery()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId()));


        if (prod == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        //视为一次对具体商品的浏览, 记录浏览量到Redis
        String productKey = RedisConstant.USER_VO_KEY + prod.getId();

        // 使用HyperLogLog记录用户id -> 浏览商品记录
        stringRedisTemplate.opsForHyperLogLog().add(productKey, UserHolder.getUser().getId().toString());

        // 统计该商品浏览量
        Long count = stringRedisTemplate.opsForHyperLogLog().size(productKey);

        // 更新商品浏览量, 同时提升权重
        ProdFunc prodFunc = prodFuncService.getOne(Wrappers.<ProdFunc>lambdaQuery().eq(ProdFunc::getId, prod.getId()));
        prodFunc.setVisit(prodFunc.getVisit() + count);
        prodFunc.setWeight(prodFunc.getWeight() + count * SystemConstant.DEFAULT_WEIGHT);
        prodFuncService.updateById(prodFunc);

        ProdGreatVO prodGreatVO;

/*        try {
            prodGreatVO = dtoUtils.createAndCombineDTOs(ProdGreatVO.class, prod.getId(), ProdAllDTO.class, ProdFuncAllDTO.class);
        } catch (Exception e) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }*/
        prodGreatVO = prodMapper.selectJoinOne(ProdGreatVO.class, new MPJLambdaWrapper<Prod>()
                .selectAll(Prod.class)
                .selectAll(ProdFunc.class)
                .leftJoin(ProdFunc.class, ProdFunc::getId, Prod::getId)
                .eq(Prod::getId, prod.getId()));


        return prodGreatVO;
    }


    /**
     * 通过缓存查询商品, 解决缓存穿透, 缓存击穿等问题
     */
    @Override
    public ProdGreatVO getProd8CG(ProdLocateDTO prodLocateDTO) {

        // 这里是双'主键'情况不能用prodLocateDTO直接查, 需要用prodLocateDTO的name和userId拼接改造CacheClient

        //? 1 - fix缓存穿透 (use 空对象)
//        Prod prod = queryProdWithBlankObject(prodLocateDTO);


        //? 2 - fix缓存击穿 + 穿透 (use 互斥锁 + 空对象) [采用]
        Prod prod = queryProdWithMutex(prodLocateDTO);


        //? 3 - fix缓存击穿 (use 逻辑过期) [不采用]

/*        try {   //调试: 预热对应商品缓存
            saveProd2Redis(prodLocateDTO, 114514L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Prod prod = queryProdWithLogicalExpire(prodLocateDTO);*/


        //? 以下为通用余下流程

        if (prod == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        //视为一次对具体商品的浏览, 记录浏览量到Redis
        String productKey = RedisConstant.USER_VO_KEY + prod.getId();

        // 使用HyperLogLog记录用户id -> 浏览商品记录
        stringRedisTemplate.opsForHyperLogLog().add(productKey, UserHolder.getUser().getId().toString());

        // 统计该商品浏览量
        Long count = stringRedisTemplate.opsForHyperLogLog().size(productKey);

        // 更新商品浏览量, 同时提升权重
        ProdFunc prodFunc = prodFuncService.getOne(Wrappers.<ProdFunc>lambdaQuery().eq(ProdFunc::getId, prod.getId()));
        prodFunc.setVisit(prodFunc.getVisit() + count);
        prodFunc.setWeight(prodFunc.getWeight() + count * SystemConstant.DEFAULT_WEIGHT);
        prodFuncService.updateById(prodFunc);

        ProdGreatVO prodGreatVO;

/*       try {
            prodGreatVO = dtoUtils.createAndCombineDTOs(ProdGreatVO.class, prod.getId(), ProdAllDTO.class, ProdFuncAllDTO.class);
        } catch (Exception e) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }*/

        prodGreatVO = prodMapper.selectJoinOne(ProdGreatVO.class, new MPJLambdaWrapper<Prod>()
                .selectAll(Prod.class)
                .selectAll(ProdFunc.class)
                .leftJoin(ProdFunc.class, ProdFunc::getId, Prod::getId)
                .eq(Prod::getId, prod.getId()));

        return prodGreatVO;
    }

    /**
     * 查询商品 通过设置逻辑过期方法 -> 解决缓存击穿
     */
    private Prod queryProdWithLogicalExpire(ProdLocateDTO prodLocateDTO) {
        String locateKey = prodLocateDTO.getUserId() + ":" + prodLocateDTO.getName();
        String keyProd = RedisConstant.CACHE_PROD_KEY + locateKey;  //构建Prod的Key

        String prodJson = stringRedisTemplate.opsForValue().get(keyProd);//执行查询

        if (StrUtil.isBlank(prodJson)) return null; //为空直接返回null

        // 命中则使用一个RedisData的R对象来存储数据和过期时间
        RedisData redisData = JSONUtil.toBean(prodJson, RedisData.class);

        //RedisData取出Data ->  JSON -> Prod
        Prod prod = JSONUtil.toBean((JSONObject) redisData.getData(), Prod.class);

        // 判断是否过期
        if (LocalDateTime.now().isBefore(redisData.getExpireTime())) return prod; //未过期直接返回Prod

        // 过期则尝试获取互斥锁
        String keyProdLock = RedisConstant.LOCK_PROD_KEY + locateKey; //构建Prod的Lock Key
        boolean flag = tryLock(keyProdLock);

        if (flag) { //获取到了锁
            //开启独立线程
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try {
                    // 重建缓存
                    this.saveProd2Redis(prodLocateDTO, RedisConstant.ACTIVE_PROD_TTL);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    unlock(keyProdLock);
                }
            });
            return prod; //处理完毕返回Prod
        }

        //没获取到锁也直接返回Prod
        return prod;
    }


    /**
     * 保存商品到Redis
     *
     * @param prodLocateDTO 商品定位DTO
     * @param expirSeconds  过期时间
     */
    public void saveProd2Redis(ProdLocateDTO prodLocateDTO, Long expirSeconds) throws InterruptedException {

        String keyProd = RedisConstant.CACHE_PROD_KEY + prodLocateDTO.getUserId() + ":" + prodLocateDTO.getName();

        //数据库查询 : MP的lambdaQuery查询
        Prod prod = this.getOne(Wrappers.<Prod>lambdaQuery()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId()));

        Thread.sleep(RedisConstant.LOCK_PROD_FAIL_WT * 4); // 模拟重建缓存耗时

        //包装 RedisData 对象
        RedisData redisData = new RedisData();
        redisData.setData(prod);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expirSeconds));

        stringRedisTemplate.opsForValue().set(keyProd, JSONUtil.toJsonStr(redisData)); //因为是手动判断过期, 所以不需要设置TTL
    }


    /**
     * 查询商品 通过设置互斥锁方法 -> 解决缓存击穿 + 缓存穿透
     */
    private Prod queryProdWithMutex(ProdLocateDTO prodLocateDTO) {
        String locateKey = prodLocateDTO.getUserId() + ":" + prodLocateDTO.getName();
        String keyProd = RedisConstant.CACHE_PROD_KEY + locateKey;  //构建Prod的Key

        String prodJson = stringRedisTemplate.opsForValue().get(keyProd);//执行查询

        if (StrUtil.isNotBlank(prodJson)) { //不为空就把json转为Prod返回, 表示拿到了prod
            return JSONUtil.toBean(prodJson, Prod.class);
        }

        if (prodJson != null) { //查询到了, 但是是空字符串, 意味着是缓存空数据, 返回null
            return null;
        }


        // 实现在高并发的情况下缓存击穿缓存重建
        Prod prod;
        String keyProdLock = RedisConstant.LOCK_PROD_KEY + locateKey; //构建Prod的Lock Key
        try {
            // 尝试获取锁
            boolean flag = tryLock(keyProdLock);

            while (!flag) {
                Thread.sleep(RedisConstant.LOCK_PROD_FAIL_WT); // 获取失败则等待后重试
                return queryProdWithMutex(prodLocateDTO); //递归调用
            }
            //获取成功执行重建操作

            //查数据库流程: MP的lambdaQuery查询
            prod = this.getOne(Wrappers.<Prod>lambdaQuery()
                    .eq(Prod::getName, prodLocateDTO.getName())
                    .eq(Prod::getUserId, prodLocateDTO.getUserId()));

            if (prod == null) { //还查不到就要进行缓存穿透的空数据设置
                stringRedisTemplate.opsForValue().set(keyProd, "", RedisConstant.CACHE_NULL_TTL, TimeUnit.MINUTES); //设置TTL - NULL
                return null;
            }

            //查到了就要进行缓存设置
            String jsonStr = JSONUtil.toJsonStr(prod);
            stringRedisTemplate.opsForValue().set(keyProd, jsonStr, RedisConstant.CACHE_PROD_TTL, TimeUnit.MINUTES); //设置TTL - PROD

            //返回查询到的Prod
            return prod;

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            unlock(keyProdLock); //业务结束释放锁
        }
    }


    /**
     * 缓存击穿的互斥锁实现 - 加锁
     */
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", RedisConstant.LOCK_PROD_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);       //避免返回值为null使用了BooleanUtil工具类
    }


    /**
     * 缓存击穿的互斥锁实现 - 解锁
     */
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }


    /**
     * 查询商品 通过设置空对象方法 -> 解决缓存穿透
     */
    private Prod queryProdWithBlankObject(ProdLocateDTO prodLocateDTO) {
        //针对传入的prodLocateDTO进行Key构建: 选择name和userId作为Key, userId.toString() : name, 加上前缀
        String key = RedisConstant.CACHE_PROD_KEY + prodLocateDTO.getUserId() + ":" + prodLocateDTO.getName();

        String prodJson = stringRedisTemplate.opsForValue().get(key);//执行查询

        if (StrUtil.isNotBlank(prodJson)) { //不为空就把json转为Prod返回, 表示拿到了prod
            return JSONUtil.toBean(prodJson, Prod.class);
        }

        if (prodJson != null) { //查询到了, 但是是空字符串, 意味着是缓存空数据, 返回null
            return null;
        }

        //实现在高并发的情况下缓存穿透设置空对象

        //查数据库流程: MP的lambdaQuery查询
        Prod prod = this.getOne(Wrappers.<Prod>lambdaQuery()
                .eq(Prod::getName, prodLocateDTO.getName())
                .eq(Prod::getUserId, prodLocateDTO.getUserId()));


        if (prod == null) { //还查不到就要进行缓存穿透的空数据设置
            stringRedisTemplate.opsForValue().set(key, "", RedisConstant.CACHE_NULL_TTL, TimeUnit.MINUTES); //设置TTL - NULL
            return null;
        }

        //查到了就要进行缓存设置
        String jsonStr = JSONUtil.toJsonStr(prod);
        stringRedisTemplate.opsForValue().set(key, jsonStr, RedisConstant.CACHE_PROD_TTL, TimeUnit.MINUTES); //设置TTL - PROD

        //返回查询到的Prod
        return prod;
    }


    @Override
    public Page<Prod> pageProd8CateG(String cate, Integer current) {

        ProdCate prodCate = prodCateService.getOne(Wrappers.<ProdCate>lambdaQuery()
                .eq(ProdCate::getName, cate));

        if (prodCate == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        Long id = prodCate.getId();

        return this.page(
                new Page<>(current, SystemConstant.MAX_PAGE_SIZE),
                Wrappers.<Prod>lambdaQuery().eq(Prod::getCategoryId, id));
    }


    @Override
    public Page<Prod> pageProdCateG(String cate, Integer current) {

        ProdCate prodCate = prodCateService.getOne(Wrappers.<ProdCate>lambdaQuery()
                .eq(ProdCate::getName, cate));

        if (prodCate == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        Long id = prodCate.getId();

        return this.page(
                new Page<>(current, SystemConstant.MAX_PAGE_SIZE),
                Wrappers.<Prod>lambdaQuery().eq(Prod::getCategoryId, id));
    }


    @Override
    public Page<ProdAllVO> searchProd8EzG(String name, Integer current) {

        //只需要返回ProdVO
        Page<Prod> page = this.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE), Wrappers.<Prod>lambdaQuery()
                .like(Prod::getName, name)
        );

        //根据ProdFunc的权重值排序
        //ProdFunc需要通过Prod的id找到, 所以需要先找到id
        List<Long> ids = new ArrayList<>();
        for (Prod prod : page.getRecords()) {
            ids.add(prod.getId());
        }

        List<ProdFunc> prodFuncList = prodFuncService.list(Wrappers.<ProdFunc>lambdaQuery()
                .in(ProdFunc::getId, ids)
                .orderByDesc(ProdFunc::getWeight)
        );

        //按照prodFuncList的顺序重新排序Prod的Records
        for (int i = 0; i < page.getRecords().size(); i++) {
            page.getRecords().set(i, this.getById(prodFuncList.get(i).getId()));
        }

        //转换为ProdAllVO, 并返回
        return (Page<ProdAllVO>) page.convert(prod -> {
            ProdAllVO prodAllVO = new ProdAllVO();
            BeanUtils.copyProperties(prod, prodAllVO);
            return prodAllVO;
        });
    }


    /**
     * 从ProdGreatDTO创建DTO
     */
    private <T> T createDTOFromProdGreatDTO(ProdGreatDTO prodGreatDTO, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T dto = clazz.newInstance();
        BeanUtils.copyProperties(prodGreatDTO, dto);
        return dto;
    }
}
