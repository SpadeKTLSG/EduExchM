package org.shop.supply.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.supply.common.constant.SystemConstant;
import org.shop.supply.common.context.UserHolder;
import org.shop.supply.entity.Prod;
import org.shop.supply.entity.dto.ProdGreatDTO;
import org.shop.supply.entity.dto.ProdLocateDTO;
import org.shop.supply.entity.res.Result;
import org.shop.supply.service.ProdCateService;
import org.shop.supply.service.ProdService;
import org.springframework.web.bind.annotation.*;

/**
 * 商品
 */
@Slf4j
@Tag(name = "Prod", description = "商品")
@RequestMapping("/guest/prod")
@RestController
@RequiredArgsConstructor
public class ProdController4Guest {


    private final ProdService prodService;
    private final ProdCateService prodCateService;


    //! Func


    /**
     * 优惠券触发商品状态修改
     */
    @PutMapping("/update/status/{func}")
    @Operation(summary = "优惠券触发商品状态修改")
    @Parameters({
            @Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true),
            @Parameter(name = "func", description = "功能", required = true)
    })
    public Result putProdStatusG(@RequestBody ProdLocateDTO prodLocateDTO, @PathVariable("func") Integer func) {
        prodService.putProdStatusG(prodLocateDTO, func);
        return Result.success();
    }
    //http://localhost:8086/guest/prod/update/status/0


    //! ADD

    /**
     * 用户添加商品
     * <p>需要审核修改status</p>
     */
    @PostMapping("/save")
    @Operation(summary = "用户添加商品")
    @Parameters(@Parameter(name = "prodGreatDTO", description = "商品添加DTO", required = true))
    public Result postProdG(@RequestBody ProdGreatDTO prodGreatDTO) {
        prodService.postProdG(prodGreatDTO);
        return Result.success();
    }
    //http://localhost:8086/guest/prod/save


    //! DELETE

    /**
     * 用户删除商品, 通过商品名
     * <p>需要判断有无开启交易</p>
     */
    @DeleteMapping("/delete/{name}")
    @Operation(summary = "用户删除商品")
    @Parameters(@Parameter(name = "name", description = "商品名", required = true))
    public Result deleteProdG(@PathVariable("name") String name) {
        prodService.deleteProdG(name);
        return Result.success();
    }
    //http://localhost:8086/guest/prod/delete


    //! UPDATE

    /**
     * 用户更新商品 联表选择性更新字段
     * <p>包括: 商品冻结/恢复</p>
     */
    @PutMapping("/update")
    @Operation(summary = "用户更新商品")
    @Parameters(@Parameter(name = "prodGreatDTO", description = "商品更新DTO", required = true))
    public Result putProdG(@RequestBody ProdGreatDTO prodGreatDTO) {
        try {
            prodService.putProdG(prodGreatDTO);
            return Result.success();
        } catch (RuntimeException | InstantiationException | IllegalAccessException e) {
            return Result.error(e.getMessage());
        }
    }
    //http://localhost:8086/guest/prod/update


    /**
     * 用户更新商品 联表选择性更新字段
     * <p>包括: 商品冻结/恢复</p>
     * !<p>缓存引入示例: 刷新对应对象缓存</p>
     */
    @PutMapping("/update/cache")
    @Operation(summary = "用户更新商品 - Cache")
    @Parameters(@Parameter(name = "prodGreatDTO", description = "商品更新DTO", required = true))
    public Result putProd8CG(@RequestBody ProdGreatDTO prodGreatDTO) {

        try {
            prodService.putProd8CG(prodGreatDTO);
            return Result.success();
        } catch (RuntimeException | InstantiationException | IllegalAccessException e) {
            return Result.error(e.getMessage());
        }
    }
    //http://localhost:8086/guest/prod/update/cache


    //! QUERY

    /**
     * 分页查询商品 分类 列表
     * <p>用于前端填表单</p>
     */
    @GetMapping("/category/page")
    @Operation(summary = "分页查询商品分类列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageCate(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodCateService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE)));
    }
    //http://localhost:8086/guest/prod/category/page


    /**
     * 分页查询自己的商品列表
     * <p>简单展示VO</p>
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询自己的商品列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageProd4Me(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE),
                Wrappers.<Prod>lambdaQuery()
                        .eq(Prod::getUserId, UserHolder.getUser().getId()))
        );
    }
    //http://localhost:8086/guest/prod/page


    /**
     * name查询自己单个商品详细信息
     * <p>联表查询VO</p>
     */
    @GetMapping("/get")
    @Operation(summary = "查询单个商品详细信息")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO"))
    public Result getProdG(@RequestBody ProdLocateDTO prodLocateDTO) {
        return Result.success(prodService.getProdG(prodLocateDTO));
    }
    //http://localhost:8086/guest/prod/get


    /**
     * name查询自己单个商品详细信息
     * <p>联表查询VO</p>
     * !<p>缓存引入示例*3 fix 缓存穿透, 缓存击穿</p>
     */
    @GetMapping("/get/cache")
    @Operation(summary = "查询单个商品详细信息 - Cache")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO"))
    public Result getProd8CG(@RequestBody ProdLocateDTO prodLocateDTO) {
        return Result.success(prodService.getProd8CG(prodLocateDTO));
    }
    //http://localhost:8086/guest/prod/get/cache


    /**
     * 根据分类查自己的商品列表分页(半联表)
     */
    @GetMapping("/category/prod/{cate}")
    @Operation(summary = "根据分类获得自己的对应商品列表")
    @Parameters(@Parameter(name = "cate", description = "分类名", required = true))
    public Result pageProd8CateG(@PathVariable("cate") String cate, @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.pageProd8CateG(cate, current));
    }
    //http://localhost:8086/guest/prod/category/prod/0


    /**
     * 分页查询所有商品列表(仅Prod表)
     */
    @GetMapping("/all/page")
    @Operation(summary = "分页查询所有商品列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageAllProd(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE)));
    }
    //http://localhost:8086/guest/prod/all/page


    /**
     * 分页查询一个分类下的所有商品列表(联表Prod + ProdCate)
     * <p>用于前端用户浏览</p>
     */
    @GetMapping("/cateall/page/{cate}")
    @Operation(summary = "分页查分类下所有商品列表")
    @Parameters(@Parameter(name = "cate", description = "分类名", required = true))
    public Result pageProdCateG(@PathVariable("cate") String cate, @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.pageProdCateG(cate, current));
    }
    //http://localhost:8086/guest/prod/cateall/page/人类


    /**
     * 按Name模糊搜索商品
     * <p>前端搜索框, 分页展示结果</p>
     * <p>加入搜索权重效果</p>
     */
    @GetMapping("/search/name")
    @Operation(summary = "按Name模糊搜索商品")
    @Parameters({
            @Parameter(name = "name", description = "商品名称", required = true),
            @Parameter(name = "current", description = "当前页", required = true)
    })
    public Result searchProd8EzG(@RequestParam("name") String name, @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(prodService.searchProd8EzG(name, current));
    }
    //http://localhost:8086/guest/prod/search/name


}
