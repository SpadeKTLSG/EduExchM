package org.shop.trade.controller;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.trade.common.constant.SystemConstant;
import org.shop.trade.entity.Order;
import org.shop.trade.entity.dto.OrderAllDTO;
import org.shop.trade.entity.res.Result;
import org.shop.trade.service.OrderService;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制
 */
@Slf4j
@Tag(name = "Order", description = "订单")
@RequestMapping("/admin/order")
@RestController
@RequiredArgsConstructor
public class OrderController4Admin {

    private final OrderService orderService;

    //! Client
    @GetMapping("/remote/getOne")
    public Order getOne(@RequestParam("prodId") Long prodId) {

        return orderService.getOne(Wrappers.<Order>lambdaQuery()
                .eq(Order::getProdId, prodId)
                .ne(Order::getStatus, Order.OVER) //已经完成的交易不算
                .ne(Order::getStatus, Order.STOP) //已经撤销的交易不算
        );
    }

    //! Func


    //! ADD
    //禁止

    //! DELETE
    //禁止

    //! UPDATE
    //禁止

    //! QUERY

    /**
     * 分页查看所有订单列表
     */
    @GetMapping("/listall")
    @Operation(summary = "查看所有订单列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageOrderA(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(orderService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE)));
    }
    //http://localhost:8085/admin/order/listall


    /**
     * 查看一个订单详情
     * <p>联表</p>
     */
    @GetMapping("/detail")
    @Operation(summary = "查看一个订单详情")
    @Parameters(@Parameter(name = "orderAllDTO", description = "订单详情DTO", required = true))
    public Result getOrderA(@RequestBody OrderAllDTO orderAllDTO) {
        return Result.success(orderService.orderDetail(orderAllDTO));
    }
    //http://localhost:8085/admin/order/detail


}

