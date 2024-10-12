package org.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.shop.common.context.UserHolder;
import org.shop.common.exception.*;
import org.shop.entity.Order;
import org.shop.entity.OrderDetail;
import org.shop.entity.dto.OrderAllDTO;
import org.shop.entity.vo.OrderGreatVO;
import org.shop.flow.mq.MQSender;
import org.shop.service.OrderDetailService;
import org.shop.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    /**
     * lua脚本对象
     */
    private static final DefaultRedisScript<Long> SECKILL_SCRIPT;

    static {
        //加载Lua脚本
        SECKILL_SCRIPT = new DefaultRedisScript<>();
        SECKILL_SCRIPT.setLocation(new ClassPathResource("seckill.lua")); //位置
        SECKILL_SCRIPT.setResultType(Long.class); //返回类型
    }


    private OrderDetailService orderDetailService;

    private ProdService prodService;

    private ProdFuncService prodFuncService;

    private UserFuncService userFuncService;

    private MQSender mqSender;

    private StringRedisTemplate stringRedisTemplate;
    /**
     * 令牌桶算法 限流
     * <p> 这里设置每秒放入10个令牌</p>
     */
    private RateLimiter rateLimiter = RateLimiter.create(10);

    //! Func

    @Override
    public OrderGreatVO orderDetail(OrderAllDTO orderAllDTO) {

        Order order = dtoFindEntity(orderAllDTO);

        OrderDetail orderDetail = orderDetailService.getOne(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getId, order.getId())
        );

        OrderGreatVO orderGreatVO = new OrderGreatVO();
        BeanUtils.copyProperties(order, orderGreatVO);
        BeanUtils.copyProperties(orderDetail, orderGreatVO);

        return orderGreatVO;
    }


    @Override
    @Transactional
    public void postOrderG(ProdLocateDTO prodLocateDTO) {
        String name = prodLocateDTO.getName();
        Long userId = prodLocateDTO.getUserId();

        if (name == null || userId == null) throw new BadArgsException(BAD_ARGS);


        Prod prod = prodService.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, name)
                .eq(Prod::getUserId, userId)
        );

        if (prod == null || prod.getStock() <= 0) throw new SthNotFoundException(OBJECT_NOT_ALIVE);


        ProdFunc prodFunc = prodFuncService.getOne(new LambdaQueryWrapper<ProdFunc>()
                .eq(ProdFunc::getId, prod.getId())
        );
        if (!Objects.equals(prodFunc.getStatus(), ProdFunc.NORMAL)) throw new BadArgsException(BAD_ARGS);      //审核未通过的商品不可交易


        //创建订单流程
        prod.setStock(prod.getStock() - 1); //库存减一

        Long buyer_id = UserHolder.getUser().getId();
        Long seller_id = prod.getUserId();
        Long prod_id = prod.getId();

        //查询可能存在的旧订单对象
        Long count = this.query()
                .eq("buyer_id", buyer_id)
                .eq("seller_id", seller_id)
                .eq("prod_id", prod_id)
                .count();

        if (count > 0) throw new BlockActionException(ORDER_STATUS_ERROR); //重复购买判定

        //构造订单对象并存储
        Order order = Order.builder()
                .buyerId(buyer_id)
                .sellerId(seller_id)
                .prodId(prod_id)
                .status(Order.WAITCHECK) //模拟: 买家开启交易后忽略传递时间, 直接进入等待卖家确认状态
                .build();
        this.save(order);

        OrderDetail orderDetail = OrderDetail.builder()
                .openTime(LocalDateTime.now())
                .build();
        orderDetailService.save(orderDetail);

        prodService.updateById(prod);
    }


    /**
     * ! 秒杀下单流程
     */
    @Override
    public void putOrderSeckillG(ProdLocateDTO prodLocateDTO) {

        // 用令牌桶算法进行限流
        /*
        原理: 令牌桶算法是一种限流算法，它的原理是系统会以一个恒定的速度往桶里放入令牌，而请求需要拿到令牌才能被处理，如果桶里没有令牌，那么请求就会被限流。
        令牌桶算法既能够将所有的请求平均分布到时间区间内，又能接受服务器能够承受范围内的突发请求是目前使用较为广泛的一种限流算法
         */
        //timeout: 1000 代表 1秒内没有拿到令牌则抛出异常
        if (!rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) throw new NetWorkException(NETWORK_ERROR); //如果没有拿到令牌, 则抛出网络环境异常

        // 执行流程

        // 找到对应Prod
        String name = prodLocateDTO.getName();
        Long userId = prodLocateDTO.getUserId();

        if (name == null || userId == null) throw new BadArgsException(BAD_ARGS);


        Prod prod = prodService.getOne(new LambdaQueryWrapper<Prod>()
                .eq(Prod::getName, name)
                .eq(Prod::getUserId, userId)
        );
        if (prod == null) throw new SthNotFoundException(OBJECT_NOT_ALIVE);

        ProdFunc prodFunc = prodFuncService.getOne(new LambdaQueryWrapper<ProdFunc>()
                .eq(ProdFunc::getId, prod.getId())
        );
        if (!Objects.equals(prodFunc.getStatus(), ProdFunc.NORMAL)) throw new BlockActionException(BLOCK_ACTION);//审核未通过的商品不可交易


        // 构造输入参数
        Long meId = UserHolder.getUser().getId();


        //利用Lua脚本进行 判断库存是否充足 + 判断用户是否下单 以及业务逻辑
        Long r = stringRedisTemplate.execute(
                SECKILL_SCRIPT,
                Collections.emptyList(),
                prod.getId().toString(),
                meId.toString()
        );

        //判断结果
        int result = 0;
        if (r != null) {
            result = r.intValue();
        }

        if (result == 1) throw new SthNotFoundException(OBJECT_NOT_ALIVE);
        if (result == 2) throw new SthHasCreatedException(ORDER_STATUS_ERROR);

        //构造订单对象(后续还要补充其详细字段)
        Long buyer_id = UserHolder.getUser().getId();
        Long seller_id = prod.getUserId();
        Long prod_id = prod.getId();

        Order order = Order.builder()
                .buyerId(buyer_id)
                .sellerId(seller_id)
                .prodId(prod_id)
                .status(Order.WAITCHECK) //模拟: 买家开启交易后忽略传递时间, 直接进入等待卖家确认状态
                .build();

        //保存订单入MQ
        mqSender.sendSeckillMessage(JSON.toJSONString(order));
    }

    //! ADD


    //! DELETE

    @Override
    @Transactional
    public void deleteOrderG(OrderAllDTO orderAllDTO) {
        Order order1 = dtoFindEntity(orderAllDTO);

        //保留式删除, 将订单状态置为5, 同时将订单详情的checkoutTime置为当前时间
        order1.setStatus(Order.STOP);
        this.updateById(order1);

        OrderDetail orderDetail = orderDetailService.getOne(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getId, order1.getId())
        );
        orderDetail.setCheckoutTime(LocalDateTime.now());

        orderDetailService.updateById(orderDetail);
    }


    @Override
    @Transactional
    public void sellerKnowAnswer(OrderAllDTO orderAllDTO) {
        Order order1 = dtoFindEntity(orderAllDTO);
        //限制上一个状态为等待卖家确认
        if (!Objects.equals(order1.getStatus(), Order.WAITCHECK)) throw new BlockActionException(BLOCK_ACTION);
        order1.setStatus(Order.TALKING);
        this.updateById(order1);
    }


    @Override
    @Transactional
    public void buyerKnowAnswer(OrderAllDTO orderAllDTO) {
        Order order1 = dtoFindEntity(orderAllDTO);
        //限制上一个状态为交涉中
        if (!Objects.equals(order1.getStatus(), Order.TALKING)) throw new BlockActionException(BLOCK_ACTION);
        order1.setStatus(Order.EXCHANGING);
        this.updateById(order1);
    }


    @Override
    @Transactional
    public void sellerKnowClose(OrderAllDTO orderAllDTO) {
        Order order1 = dtoFindEntity(orderAllDTO);
        //限制上一个状态为正在交易
        if (!Objects.equals(order1.getStatus(), Order.EXCHANGING)) throw new BlockActionException(BLOCK_ACTION);
        order1.setStatus(Order.OVER);
        this.updateById(order1);

        //完成交易
        OrderDetail orderDetail = orderDetailService.getOne(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getId, order1.getId())
        );

        orderDetail.setCheckoutTime(LocalDateTime.now());//订单详情的checkoutTime置为当前时间
        orderDetailService.updateById(orderDetail);

        //修改三方字段: 买家和卖家的对应交易次数+1
        UserFunc buyerFunc = userFuncService.getById(order1.getBuyerId());
        UserFunc sellerFunc = userFuncService.getById(order1.getSellerId());
        buyerFunc.setGains(buyerFunc.getGains() + 1);
        sellerFunc.setSolds(buyerFunc.getSolds() + 1);

        userFuncService.updateById(buyerFunc);
        userFuncService.updateById(sellerFunc);
    }

    //! QUERY


    @Override
    public OrderGreatVO getOrderG(OrderAllDTO orderAllDTO) {
        Order order = dtoFindEntity(orderAllDTO);

        OrderDetail orderDetail = orderDetailService.getOne(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getId, order.getId())
        );

        OrderGreatVO orderGreatVO = new OrderGreatVO();
        BeanUtils.copyProperties(order, orderGreatVO);
        BeanUtils.copyProperties(orderDetail, orderGreatVO);
        return orderGreatVO;
    }


    /**
     * 根据订单DTO查找订单实体
     */
    private Order dtoFindEntity(OrderAllDTO orderAllDTO) {
        Long sellerId = orderAllDTO.getSellerId();
        Long buyerId = orderAllDTO.getBuyerId();

        if (sellerId == null || buyerId == null) throw new BadArgsException(BAD_ARGS);

        Order order1 = this.getOne(new LambdaQueryWrapper<Order>() //三个ID唯一确认订单
                .eq(Order::getBuyerId, orderAllDTO.getBuyerId())
                .eq(Order::getSellerId, orderAllDTO.getSellerId())
                .eq(Order::getProdId, orderAllDTO.getProdId())
        );

        if (order1 == null) throw new SthNotFoundException(OBJECT_NOT_ALIVE);
        return order1;
    }


}
