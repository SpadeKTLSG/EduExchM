package org.shop.trade.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.shop.trade.entity.remote.Prod;
import org.shop.trade.entity.remote.ProdFunc;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "supply")
public interface ProdClient {

    //Prod

    @GetMapping("/guest/prod/remote/Prod/getById/{id}")
    Prod getById(@PathVariable Long id);

    @PostMapping("/guest/prod/remote/Prod/updateById")
    void updateById(@RequestBody Prod prod);

    @GetMapping("/guest/prod/remote/Prod/getOne")
    Prod getOne(@RequestBody LambdaQueryWrapper<Prod> eq);

    //ProdFunc

    @GetMapping("/guest/prod/remote/ProdFunc/getById/{id}")
    ProdFunc getById_ProdFunc(@PathVariable Long id);

    @PostMapping("/guest/prod/remote/ProdFunc/updateById")
    void updateById_ProdFunc(@RequestBody ProdFunc prod);

    @GetMapping("/guest/prod/remote/ProdFunc/getOne")
    ProdFunc getOne_ProdFunc(@RequestBody LambdaQueryWrapper<ProdFunc> eq);

}
