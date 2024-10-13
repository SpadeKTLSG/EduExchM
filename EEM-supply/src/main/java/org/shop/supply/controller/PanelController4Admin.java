package org.shop.supply.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.supply.common.constant.SystemConstant;
import org.shop.supply.entity.dto.ProdLocateDTO;
import org.shop.supply.entity.res.Result;
import org.shop.supply.service.HotsearchService;
import org.shop.supply.service.RotationService;
import org.shop.supply.service.UpshowService;
import org.springframework.web.bind.annotation.*;

/**
 * 管理面板
 */
@Slf4j
@Tag(name = "Panel", description = "管理面板")
@RequestMapping("/admin/panel")
@RestController
@RequiredArgsConstructor
public class PanelController4Admin {

    private final HotsearchService hotsearchService;
    private final RotationService rotationService;
    private final UpshowService upshowService;


    //* -- hotsearch热搜 --


    //! ADD

    /**
     * 添加热搜
     */
    @PostMapping("/hotsearch/add")
    @Operation(summary = "添加热搜")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result add2Hotsearch(@RequestBody ProdLocateDTO prodLocateDTO) {
        hotsearchService.add2Hotsearch(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/panel/hotsearch/add


    //! DELETE

    /**
     * 删除热搜
     */
    @DeleteMapping("/hotsearch/delete")
    @Operation(summary = "删除热搜")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result remove4Hotsearch(@RequestBody ProdLocateDTO prodLocateDTO) {
        hotsearchService.remove4Hotsearch(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/panel/hotsearch/delete


    /**
     * 清空热搜
     */
    @DeleteMapping("/hotsearch/clear")

    @Operation(summary = "清空热搜")
    public Result clearAllHotsearch() {
        hotsearchService.clearAllHotsearch();
        return Result.success();
    }
    //http://localhost:8085/admin/panel/hotsearch/clear


    //! UPDATE

    // 禁止


    //! QUERY

    /**
     *
     */
    @GetMapping("/hotsearch/query/page")
    @Operation(summary = "分页查询热搜")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result queryHotsearchPage(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(hotsearchService.page(new Page<>(current, SystemConstant.DEFAULT_PAGE_SIZE)));
    }
    //http://localhost:8085/admin/panel/hotsearch/query/page


    //* -- rotation轮播 --


    //! ADD

    /**
     * 添加轮播
     * <p></p>
     */
    @PostMapping("/rotation/add")
    @Operation(summary = "添加轮播")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result add2Rotation(@RequestBody ProdLocateDTO prodLocateDTO) {
        rotationService.add2Rotation(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/panel/rotation/add


    //! DELETE

    /**
     * 删除轮播
     */
    @DeleteMapping("/rotation/delete")
    @Operation(summary = "删除轮播")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result remove4Rotation(@RequestBody ProdLocateDTO prodLocateDTO) {
        rotationService.remove4Rotation(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/panel/rotation/delete


    //! UPDATE

    // 禁止


    //! QUERY

    /**
     * 分页查询轮播
     */
    @GetMapping("/rotation/query/page")
    public Result queryRotationPage(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(rotationService.page(new Page<>(current, SystemConstant.DEFAULT_PAGE_SIZE)));
    }
    //http://localhost:8085/admin/panel/rotation/query/page


    //* -- upshow提升 --

    //! ADD

    /**
     * 添加提升
     */
    @PostMapping("/upshow/add")
    @Operation(summary = "添加提升")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result add2Upshow(@RequestBody ProdLocateDTO prodLocateDTO) {
        upshowService.add2Upshow(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/panel/upshow/add

    //! DELETE

    /**
     * 删除提升
     */
    @DeleteMapping("/upshow/delete")
    @Operation(summary = "删除提升")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result remove4Upshow(@RequestBody ProdLocateDTO prodLocateDTO) {
        upshowService.remove4Upshow(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/panel/upshow/delete

    //! UPDATE

    // 禁止


    //! QUERY

    /**
     * 分页查询提升
     */
    @GetMapping("/upshow/query/page")
    @Operation(summary = "分页查询提升")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result queryUpshowPage(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(upshowService.page(new Page<>(current, SystemConstant.DEFAULT_PAGE_SIZE)));
    }
    //http://localhost:8085/admin/panel/upshow/query/page


}
