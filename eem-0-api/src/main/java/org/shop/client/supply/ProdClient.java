package org.shop.client.supply;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "prod")
public interface ProdClient {
}
