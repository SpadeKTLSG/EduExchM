package org.shop.trade.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.trade.entity.Order;
import org.shop.trade.entity.dto.OrderAllDTO;
import org.shop.trade.entity.vo.OrderGreatVO;


public interface OrderService extends IService<Order> {

    //! Func


    /**
     * 查看一个订单详情
     */
    OrderGreatVO orderDetail(OrderAllDTO orderAllDTO);


    /**
     * 开启交易
     */
    void postOrderG(ProdLocateDTO prodLocateDTO);

    /**
     * 开启秒杀交易
     */
    void putOrderSeckillG(ProdLocateDTO prodLocateDTO);


    //! ADD


    //! DELETE


    /**
     * 关闭交易
     */
    void deleteOrderG(OrderAllDTO orderAllDTO);

    //! UPDATE

    /**
     * 卖家确认回复[1]
     */
    void sellerKnowAnswer(OrderAllDTO orderAllDTO);

    /**
     * 买家确认回复[2]
     */
    void buyerKnowAnswer(OrderAllDTO orderAllDTO);

    /**
     * 卖家确认关闭[3]
     */
    void sellerKnowClose(OrderAllDTO orderAllDTO);


    //! QUERY

    /**
     * 完整订单详情
     */
    OrderGreatVO getOrderG(OrderAllDTO orderAllDTO);


}
