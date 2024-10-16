package org.shop.supply.config;


import org.shop.supply.common.constant.RabbitMQConstant;
import org.springframework.amqp.core.*;
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
        return new Queue(RabbitMQConstant.QUEUE);
    }

    @Bean
    public Queue insertQueue() {
        return new Queue(RabbitMQConstant.PROD_ES_INSERT_QUEUE, true);
    }

    @Bean
    public Queue deleteQueue() {
        return new Queue(RabbitMQConstant.PROD_ES_DELETE_QUEUE, true);
    }

    /**
     * 交换机
     */
    @Bean
    public FanoutExchange easyExchange() {
        return new FanoutExchange(RabbitMQConstant.EXCHANGE);
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(RabbitMQConstant.PROD_ES_EXCHANGE, true, false);
    }

    /**
     * 绑定
     */
    @Bean
    public Binding binding() {
        return BindingBuilder.bind(myQueue()).to(easyExchange());
    }


    @Bean
    public Binding insertQueueBinding() {
        return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(RabbitMQConstant.PROD_ES_INSERT_KEY);
    }

    @Bean
    public Binding deleteQueueBinding() {
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(RabbitMQConstant.PROD_ES_DELETE_KEY);
    }

}
