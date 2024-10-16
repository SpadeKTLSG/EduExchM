package org.shop.supply.flow.mq;

import org.shop.supply.common.constant.RabbitMQConstant;
import org.shop.supply.service.ProdService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 商品消息队列服务
 */
@Component
public class ProdMqService {


    @Autowired
    private ProdService prodService;

    /**
     * 监听Prod新增或修改的业务
     * <p>
     * 注意, 由于实际上新增是用的MP - save, 这里只是作为演示
     */
    @RabbitListener(queues = RabbitMQConstant.PROD_ES_INSERT_QUEUE)
    public void listenProdInsertOrUpdate(Long id) {
        prodService.insertById(id);
    }

    /**
     * 监听Prod删除的业务
     * <p>
     * 注意, 由于实际上删除是用的MP - remove, 这里只是作为演示
     */
    @RabbitListener(queues = RabbitMQConstant.PROD_ES_DELETE_QUEUE)
    public void listenProdDelete(Long id) {
        prodService.deleteById(id);
    }
}
