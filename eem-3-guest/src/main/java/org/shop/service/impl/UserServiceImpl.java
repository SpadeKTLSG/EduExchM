package org.shop.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.common.constant.MessageConstant;
import org.shop.common.constant.PasswordConstant;
import org.shop.common.constant.RedisConstant;
import org.shop.common.constant.SystemConstant;
import org.shop.common.context.UserHolder;
import org.shop.common.exception.*;
import org.shop.common.utils.MailUtil;
import org.shop.common.utils.RegexUtil;
import org.shop.entity.User;
import org.shop.entity.UserDetail;
import org.shop.entity.UserFunc;
import org.shop.entity.dto.*;
import org.shop.entity.vo.UserGreatVO;
import org.shop.entity.vo.UserVO;
import org.shop.mapper.UserMapper;
import org.shop.service.UserDetailService;
import org.shop.service.UserFuncService;
import org.shop.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    private final UserFuncService userFuncService;
    private final UserDetailService userDetailService;


//    private ProdService prodService;


    private final StringRedisTemplate stringRedisTemplate;


    //! Func

    @Override
    public String sendCodeG(String phone, HttpSession session) {

        // 1. 判断是否在一级限制条件内
        Boolean oneLevelLimit = stringRedisTemplate.opsForSet().isMember(RedisConstant.ONE_LEVERLIMIT_KEY + phone, "1");

        if (oneLevelLimit != null && oneLevelLimit) {
            return "!您需要等5分钟后再请求";
        }

        // 2. 判断是否在二级限制条件内
        Boolean twoLevelLimit = stringRedisTemplate.opsForSet().isMember(RedisConstant.TWO_LEVERLIMIT_KEY + phone, "1");

        if (twoLevelLimit != null && twoLevelLimit) {
            return "!您需要等20分钟后再请求";
        }


        // 3. 检查过去1分钟内发送验证码的次数
        long oneMinuteAgo = System.currentTimeMillis() - 60 * 1000;
        long count_oneminute = stringRedisTemplate.opsForZSet().count(RedisConstant.SENDCODE_SENDTIME_KEY + phone, oneMinuteAgo, System.currentTimeMillis());
        if (count_oneminute >= 1) {
            return "!距离上次发送时间不足1分钟, 请1分钟后重试";
        }

        // 4. 检查发送验证码的次数
        long fiveMinutesAgo = System.currentTimeMillis() - 5 * 60 * 1000;
        long count_fiveminute = stringRedisTemplate.opsForZSet().count(RedisConstant.SENDCODE_SENDTIME_KEY + phone, fiveMinutesAgo, System.currentTimeMillis());

        if (count_fiveminute % 3 == 2 && count_fiveminute > 5) {
            stringRedisTemplate.opsForSet().add(RedisConstant.TWO_LEVERLIMIT_KEY + phone, "1");
            stringRedisTemplate.expire(RedisConstant.TWO_LEVERLIMIT_KEY + phone, 20, TimeUnit.MINUTES);
            return "!请求过于频繁, 请20分钟后再请求"; // 发送了8, 11, 14, ...次，进入二级限制

        } else if (count_fiveminute == 5) {
            stringRedisTemplate.opsForSet().add(RedisConstant.ONE_LEVERLIMIT_KEY + phone, "1");
            stringRedisTemplate.expire(RedisConstant.ONE_LEVERLIMIT_KEY + phone, 5, TimeUnit.MINUTES);
            return "!5分钟内您已经发送了5次, 请等待5分钟后重试";  // 过去5分钟内已经发送了5次，进入一级限制
        }


        if (RegexUtil.isPhoneInvalid(phone)) throw new InvalidInputException(MessageConstant.PHONE_INVALID); //校验手机号

        Set<String> keys = stringRedisTemplate.keys(RedisConstant.LOGIN_USER_KEY_GUEST + phone + "*"); //删除之前的验证码
        if (keys != null) {
            stringRedisTemplate.delete(keys);
        }


        String code = MailUtil.achieveCode();//生成验证码: 自定义工具类生成验证码

        stringRedisTemplate.opsForValue().set(RedisConstant.LOGIN_CODE_KEY_GUEST + phone, code, RedisConstant.LOGIN_CODE_TTL_GUEST, TimeUnit.MINUTES);
        // 更新发送时间和次数
        stringRedisTemplate.opsForZSet().add(RedisConstant.SENDCODE_SENDTIME_KEY + phone, System.currentTimeMillis() + "", System.currentTimeMillis());

        return code; //调试环境: 返回验证码; 未来使用邮箱工具类发送验证码
    }


    public String loginG(UserLoginDTO userLoginDTO, HttpSession session) {

        //删除掉之前的所有登陆令牌
        Set<String> keys = stringRedisTemplate.keys(RedisConstant.LOGIN_USER_KEY_GUEST + "*");
        if (keys != null) {
            stringRedisTemplate.delete(keys);
        }

        //校验手机号
        String phone = userLoginDTO.getPhone();
        if (RegexUtil.isPhoneInvalid(phone)) throw new InvalidInputException(MessageConstant.PHONE_INVALID);

        //从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_CODE_KEY_GUEST + phone);
        String code = userLoginDTO.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) throw new InvalidInputException(MessageConstant.CODE_INVALID);

        //根据用户名查询用户
        User user = query().eq("account", userLoginDTO.getAccount()).one();
        if (user == null) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        //判断是否被锁定了
        UserFunc userFunc = userFuncService.getById(user.getId());
        if (Objects.equals(userFunc.getStatus(), UserFunc.BLOCK)) throw new BlockActionException(MessageConstant.ACCOUNT_LOCKED);


        // 随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        UserLocalDTO userDTO = BeanUtil.copyProperties(user, UserLocalDTO.class);
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));

        // 存储
        String tokenKey = RedisConstant.LOGIN_USER_KEY_GUEST + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        stringRedisTemplate.expire(tokenKey, RedisConstant.LOGIN_USER_TTL_GUEST, TimeUnit.MINUTES);

        return token;
    }


    @Override
    public void logoutG() {

        Set<String> keys = stringRedisTemplate.keys(RedisConstant.LOGIN_USER_KEY_GUEST + "*");        //删除掉之前本地的所有登陆令牌

        if (keys != null) {
            stringRedisTemplate.delete(keys);
        }
    }


    @Override
    public void doSignG() {

        Long userId = UserHolder.getUser().getId();

        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));  // 拼接key
        String key = RedisConstant.USER_SIGN_KEY + userId + keySuffix;
        int dayOfMonth = now.getDayOfMonth();
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);

    }


    @Override
    public int signCountG() {

        Long userId = UserHolder.getUser().getId();

        LocalDateTime now = LocalDateTime.now();
        String keySuffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));  // 拼接key
        String key = RedisConstant.USER_SIGN_KEY + userId + keySuffix;
        int dayOfMonth = now.getDayOfMonth();

        // 获取本月截止今天为止的所有的签到记录，返回的是一个十进制的数字 BITFIELD sign:5:202203 GET u14 0
        List<Long> result = stringRedisTemplate.opsForValue().bitField(
                key,
                BitFieldSubCommands.create()
                        .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth)).valueAt(0)
        );

        if (result == null || result.isEmpty()) { // 没有任何签到结果
            return 0;
        }

        Long num = result.get(0); // 获取签到结果

        if (num == null || num == 0) {
            return 0;
        }

        int count = 0;

        while ((num & 1) != 0) { // 判断这个bit位是否为0
            // 让这个数字与1做与运算,得到数字的最后一个bit位
            count++;
            num >>>= 1;// 把数字右移一位,抛弃最后一个bit位，继续下一个bit位
        }
        return count;
    }


    @Override
    @Transactional
    public void doCollectG(ProdLocateDTO prodLocateDTO) {

        Long userId = UserHolder.getUser().getId();

        Prod prod = prodService.getOne(Wrappers.<Prod>lambdaQuery()
                .eq(Prod::getUserId, prodLocateDTO.getUserId())
                .eq(Prod::getName, prodLocateDTO.getName()));

        if (prod == null) throw new SthNotFoundException(MessageConstant.OBJECT_NOT_ALIVE);

        //从ZSet判断是否已经收藏
        String key = RedisConstant.PROD_COLLECT_KEY + prodLocateDTO.getUserId() + ":" + prodLocateDTO.getName(); //拼接key = prod:collect:1:天选5PRO
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());

        //获取collections
        UserFunc userFunc = userFuncService.getById(userId);
        String collections = userFunc.getCollections();
        String prodNameInCollection = prodLocateDTO.getUserId() + ":" + prodLocateDTO.getName() + ","; // 拼接到collections字段中, 以逗号分隔.

        if (score == null) { //未收藏

            // 更新用户收藏collections字段, 将该商品的唯一定位ProdLocateDTO按照prodLocateDTO.getUserId() + ":" + prodLocateDTO.getName()
            if (collections == null) {
                collections = prodNameInCollection;
            } else {
                collections += prodNameInCollection;
            }

            userFunc.setCollections(collections);

            boolean isSuccess = userFuncService.updateById(userFunc);

            // 保存用户到set集合  zadd key value score
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().add(key, userId.toString(), System.currentTimeMillis());
            }

        } else { //已收藏

            // 更新用户收藏collections字段, 将该商品的id从collections字段中移除

            if (collections != null) {
                collections = collections.replace(prodNameInCollection, "");
            }

            userFunc.setCollections(collections);

            boolean isSuccess = userFuncService.updateById(userFunc);

            // 用户从Redis的set集合移除
            if (isSuccess) {
                stringRedisTemplate.opsForZSet().remove(key, userId.toString());
            }
        }
    }


    @Override
    public int collectCountG() {
        Long userId = UserHolder.getUser().getId();
        UserFunc userFunc = userFuncService.getById(userId);
        String collections = userFunc.getCollections();

        return collections == null ? 0 : collections.split(",").length;
    }


    @Override
    public Page<Prod> pageCollectG(Integer current) {

        Long userId = UserHolder.getUser().getId();
        UserFunc userFunc = userFuncService.getById(userId);
        String collections = userFunc.getCollections();
        if (collections == null) {
            return new Page<>();
        }

        String[] prodLocateDTOs = collections.split(",");
        List<Prod> prods = new ArrayList<>(); //收藏的商品列表

        for (String prodLocateDTO : prodLocateDTOs) {
            String[] split = prodLocateDTO.split(":"); //分割
            Long prodUserId = Long.parseLong(split[0]);
            String prodName = split[1];
            Prod prod = prodService.getOne(Wrappers.<Prod>lambdaQuery()
                    .eq(Prod::getUserId, prodUserId)
                    .eq(Prod::getName, prodName));
            prods.add(prod);
        }

        Page<Prod> page = new Page<>(); //手动分页
        page.setRecords(prods);
        page.setCurrent(current);
        page.setSize(SystemConstant.MAX_PAGE_SIZE);
        page.setTotal(prods.size());
        return page;
    }


    //! ADD


    @Override
    @Transactional
    public void registerG(UserLoginDTO userLoginDTO, HttpSession session) {

        String phone = userLoginDTO.getPhone();
        if (RegexUtil.isPhoneInvalid(phone)) throw new InvalidInputException(MessageConstant.PHONE_INVALID);

        //从redis获取验证码并校验
        String cacheCode = stringRedisTemplate.opsForValue().get(RedisConstant.LOGIN_CODE_KEY_GUEST + phone);
        String code = userLoginDTO.getCode();
        if (cacheCode == null || !cacheCode.equals(code)) throw new InvalidInputException(MessageConstant.CODE_INVALID);


        //校验账户是否已存在
        User userExist = query().eq("account", userLoginDTO.getAccount()).one();
        if (userExist != null) throw new AccountAlivedException(MessageConstant.ACCOUNT_ALIVED);


        //创建账户 + 账户具体信息 + 账户功能信息, 填充必须字段
        User user = User.builder()
                .account(userLoginDTO.getAccount())
                .password(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes())) //默认密码
                .phone(phone)
                .build();
        save(user);

        UserDetail userDetail = UserDetail.builder()
                .school("蚌埠坦克学院")
                .createTime(LocalDateTime.now())
                .introduce("这个人很懒，什么都没留下")
                .build();
        userDetailService.save(userDetail);

        UserFunc userFunc = UserFunc.builder()
                .credit(114L)
                .build();
        userFuncService.save(userFunc);
    }


    //! DELETE


    @Override
    public void deleteUserB() {
        UserLocalDTO userLocalDTO = UserHolder.getUser();

        //连续删除
        this.removeById(userLocalDTO.getId());
        userDetailService.removeById(userLocalDTO.getId());
        userFuncService.removeById(userLocalDTO.getId());
    }


    //! UPDATE


    @Override
    @Transactional
    public void putUserB(UserGreatDTO userGreatDTO) throws InstantiationException, IllegalAccessException {

        //联表选择性更新
        userGreatDTO.setPassword(DigestUtils.md5DigestAsHex(userGreatDTO.getPassword().getBytes())); //预先处理密码

        Optional<User> optionalUser = Optional.ofNullable(this.getOne(Wrappers.<User>lambdaQuery().eq(User::getAccount, userGreatDTO.getAccount())));
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        // 用Map存储DTO和Service
        Map<Object, IService> dtoServiceMap = new HashMap<>();
        dtoServiceMap.put(createDTOFromUserGreatDTO(userGreatDTO, UserAllDTO.class), this);
        dtoServiceMap.put(createDTOFromUserGreatDTO(userGreatDTO, UserFuncAllDTO.class), userFuncService);
        dtoServiceMap.put(createDTOFromUserGreatDTO(userGreatDTO, UserDetailAllDTO.class), userDetailService);

        dtoMapService(dtoServiceMap, optionalUser.get().getId(), optionalUser);
    }


    @Override
    public void putUserPasswordG(UserLoginDTO userLoginDTO) {

        if (this.getOne(Wrappers.<User>lambdaQuery().eq(User::getAccount, userLoginDTO.getAccount())) == null)
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        User user = User.builder()
                .account(userLoginDTO.getAccount())
                .password(DigestUtils.md5DigestAsHex(userLoginDTO.getPassword().getBytes()))
                .build();

        this.update(user, Wrappers.<User>lambdaUpdate().eq(User::getAccount, userLoginDTO.getAccount()));
    }


    //! QUERY


    @Override
    public UserVO getUser8EzIdA(Long id) {
        User user = this.getById(id);
        if (user == null) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }


    @Override
    public UserVO getUser8EzA(String account) {
        User user = this.getOne(Wrappers.<User>lambdaQuery().eq(User::getAccount, account));
        if (user == null) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }


    @Override
    public UserGreatVO getUser4MeG() {

        UserLocalDTO userLocalDTO = UserHolder.getUser();
        BeanUtils.copyProperties(this.getById(1L), userLocalDTO);

        if (this.getById(userLocalDTO.getId()) == null) throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);


        UserGreatVO userGreatVO;
        try {
            userGreatVO = dtoUtils.createAndCombineDTOs(UserGreatVO.class, userLocalDTO.getId(), UserAllDTO.class, UserDetailAllDTO.class, UserFuncAllDTO.class);
        } catch (Exception e) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }

        return userGreatVO;
    }


    @Override
    public Page<UserVO> searchUserB(String account, Integer current) {

        //分页展示模糊匹配的所有可能结果
        Page<User> page = this.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE), Wrappers.<User>lambdaQuery()
                .like(User::getAccount, account)
        );

        return (Page<UserVO>) page.convert(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        });
    }


    /**
     * 从UserGreatDTO创建DTO
     */
    private <T> T createDTOFromUserGreatDTO(UserGreatDTO userGreatDTO, Class<T> clazz) throws InstantiationException, IllegalAccessException {
        T dto = clazz.newInstance();
        BeanUtils.copyProperties(userGreatDTO, dto);
        return dto;
    }

}
