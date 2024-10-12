package org.shop.flow.mq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.shop.common.constant.RabbitMQConstant.EXCHANGE;

/**
 * 消息发送者
 */
@Slf4j
@Component
public class MQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送秒杀信息
     */
    public void sendSeckillMessage(String msg) {
        log.debug("MQ发送消息" + msg);
        rabbitTemplate.convertAndSend(EXCHANGE, msg);
    }

}
