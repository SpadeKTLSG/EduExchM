package org.shop.supply.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "supply")
public interface ProdClient {
}
