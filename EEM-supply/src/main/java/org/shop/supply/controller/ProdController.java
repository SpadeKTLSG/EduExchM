package org.shop.supply.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.supply.common.constant.SystemConstant;
import org.shop.supply.entity.dto.ProdCateAllDTO;
import org.shop.supply.entity.dto.ProdLocateDTO;
import org.shop.supply.entity.res.Result;
import org.shop.supply.service.ProdCateService;
import org.shop.supply.service.ProdService;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制
 */
@Slf4j
@Tag(name = "Prod", description = "商品")
@RequestMapping("/admin/prod")
@RestController
@RequiredArgsConstructor
public class ProdController {


    private final ProdService prodService;


    private final ProdCateService prodCateService;


    //! Func

    /**
     * 管理员审核单件商品 -> name + userId 确定唯一商品
     * Update 状态字段
     * <p>联表修改</p>
     */
    @PutMapping("/check")
    @Operation(summary = "管理员审核单件商品")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result checkA(@RequestBody ProdLocateDTO prodLocateDTO) {
        prodService.checkA(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/prod/check


    /**
     * 管理员冻结单件商品 -> name + userId 确定唯一商品
     */
    @PutMapping("/freeze")
    @Operation(summary = "管理员冻结单件商品")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result freezeA(@RequestBody ProdLocateDTO prodLocateDTO) {
        prodService.freezeA(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/prod/freeze


    /**
     * 管理员分页查看需要审核商品
     * <p>联表分页</p>
     */
    @GetMapping("/page2Check")
    @Operation(summary = "管理员分页查看需要审核商品")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result page2CheckA(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.page2CheckA(current));
    }
    //http://localhost:8085/admin/prod/page2Check


    //! ADD

    /**
     * 添加商品分类
     */
    @PostMapping("/cate/save")
    @Operation(summary = "添加商品分类")
    @Parameters(@Parameter(name = "prodCateAllDTO", description = "商品分类DTO", required = true))
    public Result postCateA(@RequestBody ProdCateAllDTO prodCateAllDTO) {
        prodCateService.postCateA(prodCateAllDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/prod/cate/save


    //! DELETE

    /**
     * 管理员删除一件商品 -> name + userId 确定唯一商品
     * <p>联表删除</p>
     */
    @DeleteMapping("/delete/one")
    @Operation(summary = "管理员删除一件商品")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result deleteProdA(@RequestBody ProdLocateDTO prodLocateDTO) {
        prodService.deleteProdA(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/prod/delete/one


    /**
     * 删除商品分类
     */
    @DeleteMapping("/cate/delete")
    @Operation(summary = "删除商品分类")
    @Parameters(@Parameter(name = "prodCateAllDTO", description = "商品分类DTO", required = true))
    public Result deleteCateA(@RequestBody ProdCateAllDTO prodCateAllDTO) {
        prodCateService.deleteCateA(prodCateAllDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/prod/cate/delete


    //! UPDATE
    // 管理员修改商品信息 : 不允许

    /**
     * 修改商品分类
     */
    @PutMapping("/cate/update")
    @Operation(summary = "修改商品分类")
    @Parameters(@Parameter(name = "prodCateAllDTO", description = "商品分类DTO", required = true))
    public Result putCateA(@RequestBody ProdCateAllDTO prodCateAllDTO) {
        prodCateService.putCateA(prodCateAllDTO);
        return Result.success();
    }


    //! QUERY

    /**
     * 查具体商品信息 -> name + userId 确定唯一商品
     */
    @GetMapping("/one")
    @Operation(summary = "查具体商品信息")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result getProd8EzA(@RequestBody ProdLocateDTO prodLocateDTO) {
        return Result.success(prodService.getProd8EzA(prodLocateDTO));
    }
    //http://localhost:8085/admin/prod/one


    /**
     * 分页查询所有商品分类
     */
    @GetMapping("/cate/page")
    @Operation(summary = "分页查询所有商品分类")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageCateA(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE)));
    }
    //http://localhost:8085/admin/prod/cate/page


    /**
     * 分页查询所有商品列表
     * <p>联表分页</p>
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询所有商品")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageProdA(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.pageProdA(current));
    }
    //http://localhost:8085/admin/prod/page


    /**
     * 按Name模糊搜索商品
     * <p>前端搜索框, 分页展示结果</p>
     */
    @GetMapping("/search/name")
    @Operation(summary = "按Name模糊搜索商品")
    @Parameters({
            @Parameter(name = "name", description = "商品名称", required = true),
            @Parameter(name = "current", description = "当前页", required = true)
    })
    public Result searchProdA(@RequestParam("name") String name, @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.searchProdA(name, current));
    }
    //http://localhost:8085/admin/prod/search/name?name=天&current=1

}
