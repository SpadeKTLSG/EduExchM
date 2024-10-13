package org.shop.trade.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * RabbitMQ配置
 */
@Configuration
@EnableRabbit
public class RabbitMQTopicConfig {


    /**
     * 队列
     */
    @Bean
    public Queue myQueue() {
        return new Queue(QUEUE);
    }

    /**
     * 交换机
     */
    @Bean
    public FanoutExchange easyExchange() {
        return new FanoutExchange(EXCHANGE);
    }

    /**
     * 绑定
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(myQueue()).to(easyExchange());
    }

}
