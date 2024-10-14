package org.shop.supply.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.shop.supply.entity.remote.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trade")
public interface OrderClient {

    //Order

    @GetMapping("/admin/order/remote/getOne")
    Order getOne(@RequestBody LambdaQueryWrapper<Order> ne);


}
