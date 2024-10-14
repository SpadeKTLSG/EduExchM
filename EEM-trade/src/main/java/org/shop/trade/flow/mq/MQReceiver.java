package org.shop.trade.flow.mq;


import com.alibaba.fastjson.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.trade.client.ProdClient;
import org.shop.trade.common.constant.MessageConstant;
import org.shop.trade.common.constant.RabbitMQConstant;
import org.shop.trade.common.exception.BaseException;
import org.shop.trade.common.exception.TrashException;
import org.shop.trade.entity.Order;
import org.shop.trade.entity.OrderDetail;
import org.shop.trade.entity.remote.Prod;
import org.shop.trade.service.OrderDetailService;
import org.shop.trade.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


/**
 * 消息消费者
 */
@Slf4j
@Component
@Lazy(false) //解决懒加载问题
@RequiredArgsConstructor
public class MQReceiver {


    private final ProdClient prodClient;

    private final OrderService orderService;

    private final OrderDetailService orderDetailService;

    /**
     * 接收秒杀信息并执行后续下单流程
     */
    @Transactional
    @RabbitListener(queues = RabbitMQConstant.QUEUE)
    public void receiveSeckillMessage(String msg) {
        log.debug("MQ准备处理秒杀订单消息: " + msg);

        //取出消息并转换为订单对象
        Order order = JSON.parseObject(msg, Order.class);

        //定位订单内元素
        Long buyer_id = order.getBuyerId();
        Long seller_id = order.getSellerId();
        Long prod_id = order.getProdId();

        //查询可能存在的脏订单对象
        Long count = orderService.query()
                .eq("buyer_id", buyer_id)
                .eq("seller_id", seller_id)
                .eq("prod_id", prod_id)
                .count();

        //重复购买判定
        if (count > 0) {
            log.error("{}已购买过, 但是重复购买", buyer_id);
            return;
        }

        //执行扣减库存和更改订单详情等具体业务

        //创建参数对象
        Prod prod = prodClient.getById(prod_id);
        prod.setStock(prod.getStock() - 1); //库存减一

        OrderDetail orderDetail = OrderDetail.builder()
                .openTime(LocalDateTime.now())
                .build();


        try { //数据库操作: 插入订单和订单详情(联合对象), 更新商品库存
            orderService.save(order);
            orderDetailService.save(orderDetail);
            prodClient.updateById(prod);
            //cas乐观锁: 查询prod是否库存足够, 不够则抛出异常, 事务回滚无事发生
            if (prodClient.getById(prod_id).getStock() < 0) {
                throw new TrashException();
            }
        } catch (Exception e) {
            log.error("库存不足 {}", e.getMessage());
            throw new BaseException(MessageConstant.BLOCK_ACTION);
        }

        log.debug("恭喜, 一个秒杀逻辑订单创建成功!");
    }


}
