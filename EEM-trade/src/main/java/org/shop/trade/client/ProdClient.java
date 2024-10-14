package org.shop.trade.client;

import org.shop.trade.entity.remote.Prod;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "supply")
public interface ProdClient {

    @GetMapping("/guest/prod/remote/getById/{id}")
    Prod getById(@PathVariable Long id);


    @PostMapping("/guest/prod/remote/updateById")
    void updateById(@RequestBody Prod prod);

}
