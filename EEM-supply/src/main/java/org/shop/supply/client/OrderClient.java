package org.shop.supply.client;

import org.shop.supply.entity.remote.Order;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "trade", url = "http://localhost:10085")
public interface OrderClient {

    //Order

    @GetMapping("/admin/order/remote/getOne")
    Order getOne(@RequestParam("prodId") Long prodId);

}
