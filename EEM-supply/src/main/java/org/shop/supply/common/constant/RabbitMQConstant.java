package org.shop.supply.common.constant;

/**
 * RabbitMQ常量
 */
public interface RabbitMQConstant {

    /**
     * 队列名称
     */
    String QUEUE = "seckill";

    /**
     * 交换机名称
     */
    String EXCHANGE = "seckill";


    /**
     * 交换机
     */
    String PROD_ES_EXCHANGE = "prod.topic";

    /**
     * 监听新增/修改的队列
     */
    String PROD_ES_INSERT_QUEUE = "prod.insert.queue";

    /**
     * 监听删除的队列
     */
    String PROD_ES_DELETE_QUEUE = "prod.delete.queue";

    /**
     * 新增/修改的RoutingKey
     */
    String PROD_ES_INSERT_KEY = "prod.insert";

    /**
     * 删除的RoutingKey
     */
    String PROD_ES_DELETE_KEY = "prod.delete";

}
