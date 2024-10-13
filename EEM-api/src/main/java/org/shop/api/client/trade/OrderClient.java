package org.shop.api.client.trade;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "order")
public interface OrderClient {
}
