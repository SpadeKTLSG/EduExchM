package org.shop.guest.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "guest")
public interface UserClient {

}
