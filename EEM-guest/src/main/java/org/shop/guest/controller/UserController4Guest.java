package org.shop.guest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.guest.common.context.UserHolder;
import org.shop.guest.entity.User;
import org.shop.guest.entity.UserFunc;
import org.shop.guest.entity.dto.UserGreatDTO;
import org.shop.guest.entity.dto.UserLoginDTO;
import org.shop.guest.entity.remote.ProdLocateDTO;
import org.shop.guest.entity.res.Result;
import org.shop.guest.service.UserFollowService;
import org.shop.guest.service.UserFuncService;
import org.shop.guest.service.UserService;
import org.springframework.web.bind.annotation.*;


/**
 * 用户
 */
@Slf4j
@Tag(name = "User", description = "用户")
@RequestMapping("/guest/user")
@RestController
@RequiredArgsConstructor
public class UserController4Guest {


    private final UserService userService;
    private final UserFuncService userFuncService;
    private final UserFollowService userFollowService;


    //! Client

    @GetMapping("/remote/User/getById/{id}")
    User getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping("/remote/User/updateById")
    void updateById(@RequestBody User user) {
        userService.updateById(user);
    }


    @GetMapping("/remote/UserFunc/getById/{id}")
    UserFunc getById_UserFunc(@PathVariable Long id) {
        return userFuncService.getById(id);
    }


    @PostMapping("/remote/UserFunc/updateById")
    void updateById_UserFunc(@RequestBody UserFunc userFunc) {
        userFuncService.updateById(userFunc);
    }


    //! Func


    /**
     * 发送验证码
     */
    @PostMapping("code")
    @Operation(summary = "发送手机验证码")
    @Parameters(@Parameter(name = "phone", description = "手机号", required = true))
    public Result sendCodeG(@RequestParam("phone") String phone, HttpSession session) {
        String mes = userService.sendCodeG(phone, session);
        //如果是!开头的字符串，说明发送失败, 去除!后返回
        if (mes.startsWith("!")) return Result.error(mes.substring(1));
        return Result.success(mes);
    }
    //http://localhost:10083/guest/user/code?phone=15985785169


    /**
     * 登录功能
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    @Parameters(@Parameter(name = "userLoginDTO", description = "用户登录DTO", required = true))
    public Result loginG(@RequestBody UserLoginDTO userLoginDTO, HttpSession session) {
        return Result.success(userService.loginG(userLoginDTO, session));
    }
    //http://localhost:10083/guest/user/login


    /**
     * 登出功能
     */
    @PostMapping("/logout")
    @Operation(summary = "退出")
    @Parameters(@Parameter(name = "无", description = "无", required = true))
    public Result logoutG() {
        userService.logoutG();
        return Result.success();
    }
    //http://localhost:10083/guest/user/logout


    /**
     * 获取当前用户
     */
    @GetMapping("/me")
    @Operation(summary = "获取当前用户")
    public Result whoAmI() {
        return Result.success(userService.getUser8EzIdA(UserHolder.getUser().getId()).getAccount());
    }
    //http://localhost:10083/guest/user/me


    //*---- 关注 ----


    /**
     * 关注功能
     */
    @PutMapping("follow/{id}/{isFollow}")
    @Operation(summary = " 用户关注")
    @Parameters({@Parameter(name = "id", description = "被关注用户id", required = true), @Parameter(name = "isFollow", description = "是否关注", required = true)})
    public Result followG(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFollow) {
        userFollowService.follow(followUserId, isFollow);
        return Result.success();
    }
    //http://localhost:10083/guest/user/follow/2/true


    /**
     * 是否关注
     */
    @GetMapping("follow/ornot/{id}")
    @Operation(summary = "是否关注")
    @Parameters(@Parameter(name = "id", description = "被关注用户id", required = true))
    public Result isFollowG(@PathVariable("id") Long followUserId) {

        return Result.success(userFollowService.isFollow(followUserId));
    }
    //http://localhost:10083/guest/user/follow/ornot/2


    /**
     * 查询共同关注
     */
    @GetMapping("follow/share/{id}")
    @Operation(summary = "关注的人")
    @Parameters(@Parameter(name = "id", description = "用户id", required = true))
    public Result shareFollowG(@PathVariable("id") Long id) {
        return Result.success(userFollowService.shareFollow(id));
    }
    //http://localhost:10083/guest/user/follow/share/2


    //*---- 签到 ----


    /**
     * 签到
     */
    @PostMapping("/sign/add")
    @Operation(summary = "签到")
    public Result doSignG() {
        userService.doSignG();
        return Result.success();
    }
    //http://localhost:10083/guest/user/sign/add


    /**
     * 签到次数统计
     */
    @GetMapping("/sign/count")
    @Operation(summary = "签到次数")
    public Result signCountG() {
        return Result.success(userService.signCountG());
    }
    //http://localhost:10083/guest/user/sign/count


    //*---- 收藏 ----


    /**
     * 收藏   (Add/Delete)
     * <p>Redis区分收藏还是取消收藏</p>
     */
    @PutMapping("/collect")
    @Operation(summary = "收藏/取消收藏")
    @Parameters(@Parameter(name = "prodLocateDTO", description = "商品定位DTO", required = true))
    public Result doCollectG(@RequestBody ProdLocateDTO prodLocateDTO) {
        userService.doCollectG(prodLocateDTO);
        return Result.success();
    }
    //http://localhost:10083/guest/user/collect


    /**
     * 收藏数量统计
     */
    @GetMapping("/collect/count")
    @Operation(summary = "收藏次数")
    public Result collectCountG() {
        return Result.success(userService.collectCountG());
    }
    //http://localhost:10083/guest/user/collect/count


    /**
     * 分页收藏列表
     * <(Prod)>
     */
    @GetMapping("/collect/page")
    @Operation(summary = "收藏列表")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageCollectG(@RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(userService.pageCollectG(current));
    }
    //http://localhost:10083/guest/user/collect/page


    //! ADD


    /**
     * 注册功能 需要注册三张表
     */
    @PostMapping("/register")
    @Operation(summary = "注册")
    @Parameters(@Parameter(name = "userLoginDTO", description = "用户登录DTO", required = true))
    public Result registerG(@RequestBody UserLoginDTO userLoginDTO, HttpSession session) {
        userService.registerG(userLoginDTO, session);
        return Result.success();
    }
    //http://localhost:10083/guest/user/register


    //! DELETE


    /**
     * 注销自己
     */
    @DeleteMapping("/delete")
    @Operation(summary = "销号")
    public Result deleteUserB() {
        userService.deleteUserB();
        return Result.success();
    }
    //http://localhost:10083/guest/user/delete


    //! UPDATE

    /**
     * 选择性更新用户信息 包治百病!
     */
    @PutMapping("/update")
    @Operation(summary = "选择性更新用户信息")
    @Parameters(@Parameter(name = "userGreatDTO", description = "User update DTO", required = true))
    public Result putUserB(@RequestBody UserGreatDTO userGreatDTO) {
        try {
            userService.putUserB(userGreatDTO);
            return Result.success();
        } catch (RuntimeException | InstantiationException | IllegalAccessException e) {
            return Result.error(e.getMessage());
        }
    }
    //http://localhost:10083/guest/user/update


    /**
     * 修改密码
     * <p>可以用上面的选择更新替代</p>
     */
    @PutMapping("/update/code")
    @Operation(summary = "修改密码")
    @Parameters(@Parameter(name = "userLoginDTO", description = "User update DTO", required = true))
    public Result putUserPasswordG(@RequestBody UserLoginDTO userLoginDTO) {
        userService.putUserPasswordG(userLoginDTO);
        return Result.success();
    }
    //http://localhost:10083/guest/user/update/code


    //! QUERY


    /**
     * 查自己全部信息
     */
    @GetMapping("/info")
    @Operation(summary = "查用户自己全部信息")
    public Result getUser4MeG() {
        return Result.success(userService.getUser4MeG());
    }
    //http://localhost:10083/guest/user/info


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
    public Result searchUserB(@RequestParam("account") String account, @RequestParam(value = "current", defaultValue = "1") Integer current) {
        return Result.success(userService.searchUserB(account, current));
    }
    //http://localhost:10083/guest/user/search/account?account=Store&current=1


}
