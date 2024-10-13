package org.shop.guest.client;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.shop.guest.entity.remote.Prod;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "supply")
public interface ProdClient {

    @GetMapping("/admin/prod/remote/getOne")
    Prod getOne(@RequestBody LambdaQueryWrapper<Prod> eq);

}
