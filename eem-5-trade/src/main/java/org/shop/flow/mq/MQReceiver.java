package org.shop.flow.mq;

import com.alibaba.fastjson.JSON;
import com.shop.common.exception.BaseException;
import com.shop.common.exception.TrashException;
import com.shop.pojo.entity.Order;
import com.shop.pojo.entity.OrderDetail;
import com.shop.pojo.entity.Prod;
import com.shop.serve.service.OrderDetailService;
import com.shop.serve.service.OrderService;
import com.shop.serve.service.ProdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.shop.common.constant.MessageConstant.BLOCK_ACTION;
import static com.shop.common.constant.RabbitMQConstant.QUEUE;


/**
 * 消息消费者
 */
@Slf4j
@Component
@Lazy(false) //解决懒加载问题
public class MQReceiver {


    private ProdService prodService;

    private OrderService orderService;

    private OrderDetailService orderDetailService;

    /**
     * 接收秒杀信息并执行后续下单流程
     */
    @Transactional
    @RabbitListener(queues = QUEUE)
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
        Prod prod = prodService.getById(prod_id);
        prod.setStock(prod.getStock() - 1); //库存减一

        OrderDetail orderDetail = OrderDetail.builder()
                .openTime(LocalDateTime.now())
                .build();


        try { //数据库操作: 插入订单和订单详情(联合对象), 更新商品库存
            orderService.save(order);
            orderDetailService.save(orderDetail);
            prodService.updateById(prod);
            //cas乐观锁: 查询prod是否库存足够, 不够则抛出异常, 事务回滚无事发生
            if (prodService.getById(prod_id).getStock() < 0) {
                throw new TrashException();
            }
        } catch (Exception e) {
            log.error("库存不足 {}", e.getMessage());
            throw new BaseException(BLOCK_ACTION);
        }

        log.debug("恭喜, 一个秒杀逻辑订单创建成功!");
    }


}
