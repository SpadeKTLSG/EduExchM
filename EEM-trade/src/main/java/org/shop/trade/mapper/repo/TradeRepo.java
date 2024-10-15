package org.shop.trade.mapper.repo;


import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.shop.trade.entity.Voucher;
import org.shop.trade.mapper.VoucherMapper;
import org.springframework.stereotype.Component;

/**
 * 抽取DAO层
 */
@Component
@RequiredArgsConstructor
public class TradeRepo {

    private final VoucherMapper voucherMapper;


    public Voucher findByVoucherName_UserId(String name, Long id) {
        return voucherMapper.selectOne(Wrappers.<Voucher>lambdaQuery().eq(Voucher::getName, name).eq(Voucher::getUserId, id));
    }

}
