package org.shop.trade.client;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "trade")
public interface OrderClient {


}
