package org.shop.guest.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.common.constant.SystemConstant;
import org.shop.supply.entity.dto.UserGreatDTO;
import org.shop.supply.entity.res.Result;
import org.shop.supply.entity.vo.UserVO;
import org.shop.supply.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;


/**
 * 员工用户控制
 */
@Slf4j
@Tag(name = "User", description = "用户控制")
@RequestMapping("/admin/user")
@RestController
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;


    //! Func


    //! ADD
    //管理员手动添加用户: 禁止


    //! DELETE
    //管理员手动删除用户: 禁止


    //! UPDATE

    /**
     * 选择性更新用户信息
     * <p>包治百病!</p>
     */
    @PutMapping("/update")
    @Operation(summary = "更新用户信息")
    @Parameters(@Parameter(name = "userGreatDTO", description = "User update DTO", required = true))
    public Result putUserA(@RequestBody UserGreatDTO userGreatDTO) {
        try {
            userService.putUserB(userGreatDTO);
            return Result.success();
        } catch (RuntimeException | InstantiationException | IllegalAccessException e) {
            return Result.error(e.getMessage());
        }
    }
    //http://localhost:8085/admin/user/update


    //! QUERY

    /**
     * Account查用户
     */
    @GetMapping("/{account}")
    @Operation(summary = "Account查用户")
    @Parameters(@Parameter(name = "account", description = "用户账号", required = true))
    public Result getUserA(@PathVariable("account") String account) {
        return Result.success(userService.getUser8EzA(account));
    }
    //http://localhost:8085/admin/user/cwxtlsg


    /**
     * ID查用户
     */
    @GetMapping("/specify/{id}")
    @Operation(summary = "ID查用户")
    @Parameters(@Parameter(name = "id", description = "用户ID", required = true))
    public Result getUser8IdA(@PathVariable("id") Long id) {
        return Result.success(userService.getUser8EzIdA(id));
    }
    //http://localhost:8085/admin/user/specify/1


    /**
     * 分页查全部用户
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageUser8EzA(@RequestParam(value = "current", defaultValue = "1") Integer current) {

        return Result.success(userService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE)).convert(user -> {
            UserVO userVO = new UserVO();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }));
    }
    //http://localhost:8085/admin/user/page


    /**
     * Account模糊搜索用户
     * <p>前端搜索框, 分页展示结果</p>
     */
    @GetMapping("/search/account")
    @Operation(summary = "Account模糊搜索用户")
    @Parameters({
            @Parameter(name = "account", description = "用户账号", required = true),
            @Parameter(name = "current", description = "当前页", required = true)
    })
    public Result searchUserA(@RequestParam("account") String account, @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(userService.searchUserB(account, current));
    }
    //http://localhost:8085/admin/user/search/account?account=Store&current=1

}
