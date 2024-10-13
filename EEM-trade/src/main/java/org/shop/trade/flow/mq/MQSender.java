package org.shop.trade.flow.mq;


import lombok.extern.slf4j.Slf4j;
import org.shop.trade.common.constant.RabbitMQConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * 消息发送者
 */
@Slf4j
@Component
public class MQSender {


    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     */
    public void sendSeckillMessage(String msg) {
        log.debug("MQ发送消息" + msg);
        rabbitTemplate.convertAndSend(RabbitMQConstant.EXCHANGE, msg);
    }

}
