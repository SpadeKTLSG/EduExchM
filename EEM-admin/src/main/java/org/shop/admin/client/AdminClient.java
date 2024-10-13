package org.shop.admin.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "admin")
public interface AdminClient {


    @PostMapping("/admin")
    String admin();

}
