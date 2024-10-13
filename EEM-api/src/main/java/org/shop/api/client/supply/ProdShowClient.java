package org.shop.api.client.supply;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "prod-show")
public interface ProdShowClient {
}
