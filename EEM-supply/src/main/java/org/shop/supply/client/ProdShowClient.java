package org.shop.supply.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "prod-show")
public interface ProdShowClient {
}
