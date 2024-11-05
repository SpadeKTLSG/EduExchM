package org.shop.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    //! DELETE


    //! UPDATE

    @DeleteMapping("/table/{menuId}")
    @Operation(summary = "删除菜单项目")
    @Parameter(name = "menuId", description = "菜单ID", required = true)
    public Result delBasicTable(@PathVariable Long menuId) {
        menuService.delMenu(menuId);
        return Result.success();
    }
    //http://localhost:9999/admin/eemfront/table/{menuId}


    //! QUERY
    @GetMapping("/nav")
    @Operation(summary = "获取菜单")
    public Result getBasicNav() {
        return Result.success(menuService.listNav());
    }
    //http://localhost:9999/admin/eemfront/nav

    @GetMapping("/table")
    @Operation(summary = "获取菜单和按钮")
    public Result getBasicTable() {
        return Result.success(menuService.listMenu());
    }
    //http://localhost:9999/admin/eemfront/table
}
