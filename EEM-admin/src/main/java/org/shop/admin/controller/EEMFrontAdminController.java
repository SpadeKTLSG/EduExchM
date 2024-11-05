package org.shop.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.admin.entity.Menu;
import org.shop.admin.entity.res.Result;
import org.shop.admin.service.impl.MenuServiceImpl;
import org.springframework.web.bind.annotation.*;

/**
 * 前端EEM-Front-Admin控制
 */
@Slf4j
@Tag(name = "EEM-Front-Admin", description = "管理员前端应用")
@RequestMapping("/admin/eemfront/")
@RestController
@RequiredArgsConstructor
public class EEMFrontAdminController {

    private final MenuServiceImpl menuService;

    //! Func


    //! ADD
    @PostMapping("/table/one")
    @Operation(summary = "增加菜单项目 ! 未实现")
    public Result addOneMenu(@RequestBody Menu menu) {
        //数据校验
        //menuService.addOne(menu);
        return Result.success(menu.getName() + "被驱逐了");    //未实现
    }

    //! DELETE

    @DeleteMapping("/table/{menuId}")
    @Operation(summary = "删除菜单项目")
    @Parameter(name = "menuId", description = "菜单ID", required = true)
    public Result delBasicTable(@PathVariable Long menuId) {
        menuService.delMenu(menuId);
        return Result.success();
    }
    //http://localhost:9999/admin/eemfront/table/{menuId}


    //! UPDATE

    @PutMapping("/table/one")
    @Operation(summary = "更新菜单项目 ! 未实现")
    public Result updateOneMenu(@RequestBody Menu menu) {
        //数据校验
        //menuService.updateOne(menu);
        return Result.success(menu.getName() + "被驱逐了");    //未实现
    }


    //! QUERY
    @GetMapping("/nav")
    @Operation(summary = "获取菜单")
    public Result getBasicNav() {
        return Result.success(menuService.listNav());
    }
    //http://localhost:9999/admin/eemfront/nav

    @GetMapping("/table")
    @Operation(summary = "获取菜单和按钮")
    public Result getBasicMenu() {
        return Result.success(menuService.listMenu());
    }
    //http://localhost:9999/admin/eemfront/table


    @GetMapping("/table/nobutton")
    @Operation(summary = "获取菜单树|增改")
    public Result getBasicMenuNoBotton() {
        return Result.success(menuService.listEZMenuNoButton());
    }
    //http://localhost:9999/admin/eemfront/table/nobutton


    @GetMapping("/table/info/{menuId}")
    @Operation(summary = "获取当前菜单信息|增")
    public Result getAMenuInfo(@PathVariable Long menuId) {
        return Result.success(menuService.getById(menuId));
    }
    //http://localhost:9999/admin/eemfront/table/info/{menuId}
}
