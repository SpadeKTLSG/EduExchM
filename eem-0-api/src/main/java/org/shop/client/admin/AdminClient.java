package org.shop.client.admin;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "admin")
public interface AdminClient {


}
