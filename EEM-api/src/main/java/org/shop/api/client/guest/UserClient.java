package org.shop.api.client.guest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "guest", url = "http://user-service-url")
public interface UserClient {


    @GetMapping("/api/user")
    String getUser(@RequestParam("id") Long id);

}
