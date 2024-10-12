package org.shop.client.trade;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "voucher")
public interface VoucherClient {
}
