package org.shop.trade.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.trade.common.constant.SystemConstant;
import org.shop.trade.entity.dto.VoucherAllDTO;
import org.shop.trade.entity.res.Result;
import org.shop.trade.service.VoucherService;
import org.springframework.web.bind.annotation.*;

/**
 * 优惠券控制
 */
@Slf4j
@Tag(name = "Voucher", description = "优惠券")
@RequestMapping("/admin/voucher")
@RestController
@RequiredArgsConstructor
public class VoucherController {


    private final VoucherService voucherService;

    //! Func


    //! ADD

    /**
     * 新增秒杀券
     */
    @PostMapping("/add/seckill")
    @Operation(summary = "新增秒杀券")
    @Parameters(@Parameter(name = "voucherAllDTO", description = "优惠券添加DTO", required = true))
    public Result putSeckillVoucherA(@RequestBody VoucherAllDTO voucherAllDTO) {
        voucherService.putSeckillVoucherA(voucherAllDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/voucher/add/seckill


    /**
     * 新增普通券
     */
    @PostMapping("/add")
    @Operation(summary = "新增普通券")
    @Parameters(@Parameter(name = "voucherAllDTO", description = "优惠券添加DTO", required = true))
    public Result putVoucherA(@RequestBody VoucherAllDTO voucherAllDTO) {
        voucherService.putVoucherA(voucherAllDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/voucher/add


    //! DELETE
    //禁止


    //! UPDATE
    //禁止


    //! QUERY

    /**
     * 分页查询软件的优惠券列表(全部信息)
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询软件的优惠券列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageVoucher(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(voucherService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE)));
    }
    //http://localhost:8085/admin/voucher/page


    /**
     * Name搜索卷
     */
    @GetMapping("/search/name")
    @Operation(summary = "Name模糊搜索卷")
    @Parameters({
            @Parameter(name = "name", description = "卷名", required = true),
            @Parameter(name = "current", description = "当前页", required = true)
    })
    public Result searchVoucherB(@RequestParam("name") String name, @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(voucherService.searchVoucherB(name, current));
    }
    //http://localhost:8085/admin/voucher/search/name?name=卷&current=1
}
