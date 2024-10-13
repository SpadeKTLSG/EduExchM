package org.shop.admin.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "admin-show")
public interface AdminShowClient {
}
