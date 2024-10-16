package org.shop.guest.client;

import org.shop.guest.entity.remote.Prod;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "supply", url = "http://localhost:10084")
public interface ProdClient {

    @GetMapping("/admin/prod/remote/getOne")
    Prod getOne(@RequestParam("userId") Long userId, @RequestParam("name") String name);

}
