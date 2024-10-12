package org.shop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.entity.Voucher;
import org.shop.entity.dto.VoucherAllDTO;
import org.shop.entity.dto.VoucherLocateDTO;

import java.time.LocalDateTime;
import java.util.List;


public interface VoucherService extends IService<Voucher> {

    //! Func

    /**
     * 宣称(领取)优惠券
     */
    void claimVoucherG(VoucherLocateDTO voucherLocateDTO);

    /**
     * 获取到达失效时间的卷对象
     */
    List<Voucher> getOutdateOnesA(Integer status, LocalDateTime time);

    /**
     * 失效对应券对象
     */
    void ruinVoucherA(Voucher voucher);


    //! ADD

    /**
     * 新增秒杀券
     */
    void putSeckillVoucherA(VoucherAllDTO voucherAllDTO);

    /**
     * 新增普通券
     */
    void putVoucherA(VoucherAllDTO voucherAllDTO);

    //! DELETE

    /**
     * 使用卖方优惠券
     */
    Integer useVoucher4Seller(VoucherLocateDTO voucherLocateDTO);

    /**
     * 使用买方优惠券
     */
    boolean useVoucher4Buyer(VoucherLocateDTO voucherLocateDTO);


    //! QUERY


    /**
     * Name模糊搜索卷
     */
    Page<Voucher> searchVoucherB(String name, Integer current);
}
