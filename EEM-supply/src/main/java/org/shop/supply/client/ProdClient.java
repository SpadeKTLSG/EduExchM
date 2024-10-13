package org.shop.supply.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "prod")
public interface ProdClient {
}
