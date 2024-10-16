package org.shop.trade.client;

import org.shop.trade.entity.remote.Prod;
import org.shop.trade.entity.remote.ProdFunc;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "supply", url = "http://localhost:10084")
public interface ProdClient {

    //Prod

    @GetMapping("/guest/prod/remote/Prod/getById/{id}")
    Prod getById(@PathVariable Long id);

    @PostMapping("/guest/prod/remote/Prod/updateById")
    void updateById(@RequestBody Prod prod);

    @GetMapping("/guest/prod/remote/Prod/getOne")
    Prod getOne(@RequestParam("userId") Long userId, @RequestParam("name") String name);

    //ProdFunc

    @GetMapping("/guest/prod/remote/ProdFunc/getById/{id}")
    ProdFunc getById_ProdFunc(@PathVariable Long id);

    @PostMapping("/guest/prod/remote/ProdFunc/updateById")
    void updateById_ProdFunc(@RequestBody ProdFunc prod);


}
