package org.shop.trade.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.shop.trade.common.constant.SystemConstant;
import org.shop.trade.common.context.UserHolder;
import org.shop.trade.entity.Order;
import org.shop.trade.entity.dto.OrderAllDTO;
import org.shop.trade.entity.remote.ProdLocateDTO;
import org.shop.trade.entity.res.Result;
import org.shop.trade.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单
 *
 * @author SK
 * @date 2024/06/03
 */
@Slf4j
@Tag(name = "Order", description = "订单")
@RequestMapping("/guest/order")
@RestController
public class OrderController4Guest {


    @Autowired
    private OrderService orderService;


    //! Func

    //支付模块: 委托外部支付平台进行支付(未实现)


    /**
     * 用户开启交易
     */
    @PostMapping("/start")
    @Operation(summary = "用户开启交易")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result postOrderG(@RequestBody ProdLocateDTO prodLocateDTO) {
        //? 压测: 插入用户数据, 同时关闭拦截器
//        UserHolder.saveUser(UserLocalDTO.builder().id(1L).account("StoreMan").build());
        orderService.postOrderG(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8086/guest/order/start


    /**
     * 用户开启秒杀交易
     * !<p>秒杀流程</p>
     */
    @PostMapping("/start/seckill")
    @Operation(summary = "用户开启交易(秒杀)")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result putOrderSeckillG(@RequestBody ProdLocateDTO prodLocateDTO) {
        //? 压测: 插入用户数据, 同时关闭拦截器
//        UserHolder.saveUser(UserLocalDTO.builder().id(1L).account("StoreMan").build());
        orderService.putOrderSeckillG(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8086/guest/order/start/seckill


    //! ADD


    //! DELETE


    /**
     * 终止当前交易(任何阶段)(买家或是卖家)
     */
    @DeleteMapping("/stop")
    @Operation(summary = "终止当前交易")
    @Parameters(@Parameter(name = "orderAllDTO", description = "订单DTO", required = true))
    public Result deleteOrderG(@RequestBody OrderAllDTO orderAllDTO) {
        orderService.deleteOrderG(orderAllDTO);
        return Result.success();
    }
    //http://localhost:8086/guest/order/stop


    //! UPDATE


    /**
     * 卖家确认, 之后进入交涉中状态
     */
    @PutMapping("/confirm/seller/answer")
    @Operation(summary = "卖家确认")
    @Parameters(@Parameter(name = "orderAllDTO", description = "订单DTO", required = true))
    public Result sellerKnowAnswer(@RequestBody OrderAllDTO orderAllDTO) {
        orderService.sellerKnowAnswer(orderAllDTO);
        return Result.success();
    }
    //http://localhost:8086/guest/order/confirm/seller/answer


    /**
     * 双方交涉完毕后买家确认, 之后进入正在交易状态
     */
    @PutMapping("/confirm/buyer/answer")
    @Operation(summary = "交涉完毕买家确认")
    @Parameters(@Parameter(name = "orderAllDTO", description = "订单DTO", required = true))
    public Result buyerKnowAnswer(@RequestBody OrderAllDTO orderAllDTO) {
        orderService.buyerKnowAnswer(orderAllDTO);
        return Result.success();
    }
    //http://localhost:8086/guest/order/confirm/buyer/answer


    /**
     * 买家确认交易后自行与卖家交易, 交易完成后卖方确认交易完成
     * <p>双方交易完成, 之后进入交易完成状态(封存关闭交易)</p>
     */
    @PutMapping("/confirm/seller/close")
    @Operation(summary = "卖家确认交易完成")
    @Parameters(@Parameter(name = "orderAllDTO", description = "订单DTO", required = true))
    public Result sellerKnowClose(@RequestBody OrderAllDTO orderAllDTO) {
        orderService.sellerKnowClose(orderAllDTO);
        return Result.success();
    }
    //http://localhost:8086/guest/order/confirm/seller/close


    //! QUERY

    /**
     * 分页查看自己的订单列表, 简要信息
     * <p>模拟购物车效果</p>
     */
    @GetMapping("/list")
    @Operation(summary = "分页查看自己的订单列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageOrder4MeG(@RequestParam(value = "current", defaultValue = "1") Integer current) {

        Long userId = UserHolder.getUser().getId();


        return Result.success(orderService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE),
                Wrappers.<Order>lambdaQuery()
                        .eq(Order::getBuyerId, userId)
                        .or()
                        .eq(Order::getSellerId, userId)
        ));
    }
    //http://localhost:8086/guest/order/list


    /**
     * 查看一个订单详情
     * <p>联表</p>
     */
    @GetMapping("/detail")
    @Operation(summary = "查看一个订单详情")
    @Parameters(@Parameter(name = "orderAllDTO", description = "订单DTO", required = true))
    public Result getOrderG(@RequestBody OrderAllDTO orderAllDTO) {
        return Result.success(orderService.getOrderG(orderAllDTO));
    }
    //http://localhost:8086/guest/order/detail


    /**
     * 计数关于自己的各种状态的订单
     * <p>用于前端展示</p>
     */
    @GetMapping("/status/count/{status}")
    @Operation(summary = "计数自己各种状态的订单")
    @Parameters(@Parameter(name = "status", description = "订单状态", required = true))
    public Result getOrderStatus8Count4FG(@PathVariable Integer status) {

        return Result.success(orderService.count(new LambdaQueryWrapper<Order>()
                .eq(Order::getStatus, status)
                .and(i -> i.eq(Order::getBuyerId, UserHolder.getUser().getId()).or().eq(Order::getSellerId, UserHolder.getUser().getId()))
        ));
    }
    //http://localhost:8086/guest/order/status/count/1


}
