package org.shop.admin.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.common.constant.SystemConstant;
import org.shop.supply.entity.dto.EmployeeAllDTO;
import org.shop.supply.entity.dto.EmployeeDTO;
import org.shop.supply.entity.dto.EmployeeLoginDTO;
import org.shop.supply.entity.res.Result;
import org.shop.supply.entity.vo.EmployeeVO;
import org.shop.supply.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 员工控制
 */
@Slf4j
@Tag(name = "Employee", description = "员工")
@RequestMapping("/admin/employee")
@RestController
@RequiredArgsConstructor
public class EmployeeController {


    private final EmployeeService employeeService;


    //! Func

    /**
     * 发送手机验证码
     */
    @PostMapping("code")
    @Operation(summary = "发送手机验证码")
    @Parameters(@Parameter(name = "phone", description = "手机号", required = true))
    public Result sendCodeA(@RequestParam("phone") String phone, HttpSession session) {
        return Result.success(employeeService.sendCodeA(phone, session));
    }
    //http://localhost:8085/admin/employee/code?phone=15985785169


    /**
     * 登录
     */
    @PostMapping("/login")
    @Operation(summary = "登录")
    @Parameters(@Parameter(name = "employeeLoginDTO", description = "员工登录DTO", required = true))
    public Result loginA(@RequestBody EmployeeLoginDTO employeeLoginDTO, HttpSession session) {
        return Result.success(employeeService.loginA(employeeLoginDTO, session));
    }
    //http://localhost:8085/admin/employee/login


    /**
     * 注销
     */
    @PostMapping("/logout")
    @Operation(summary = "退出")
    public Result logoutA() {
        employeeService.logoutA();
        return Result.success();
    }
    //http://localhost:8085/admin/employee/logout


    //! ADD

    /**
     * 新增员工
     */
    @PostMapping("/save")
    @Operation(summary = "新增员工")
    @Parameters(@Parameter(name = "employeeDTO", description = "员工DTO", required = true))
    public Result postEmployeeA(@RequestBody EmployeeDTO employeeDTO) {
        employeeService.postEmployeeA(employeeDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/employee/save


    //没有手动注册功能, 由系统实现


    //! DELETE

    /**
     * 通过员工账号删除员工
     */
    @DeleteMapping("/delete/{account}")
    @Operation(summary = "删除员工")
    @Parameters(@Parameter(name = "account", description = "员工账号", required = true))
    public Result deleteEmployeeA(@PathVariable("account") String account) {
        employeeService.deleteEmployeeA(account);
        return Result.success();
    }
    //http://localhost:8085/admin/employee/delete


    //! UPDATE

    /**
     * 选择性更新员工信息
     */
    @PutMapping("/update")
    @Operation(summary = "选择性更新员工信息")
    @Parameters(@Parameter(name = "employee", description = "员工", required = true))
    public Result putEmployeeA(@RequestBody EmployeeAllDTO employeeAllDTO) {
        employeeService.putEmployeeA(employeeAllDTO);
        return Result.success();
    }
    //http://localhost:8085/admin/employee/update


    //! QUERY

    /**
     * Account查员工
     */
    @GetMapping("/{account}")
    @Operation(summary = "Account查员工")
    @Parameters(@Parameter(name = "account", description = "员工账号", required = true))
    public Result getEmployeeA(@PathVariable("account") String account) {
        return Result.success(employeeService.getEmployeeA(account));
    }
    //http://localhost:8085/admin/employee/Account查员工


    /**
     * 分页查全部员工
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询")
    @Parameters(@Parameter(name = "current", description = "当前页", required = true))
    public Result pageEmployeeA(@RequestParam(value = "current", defaultValue = "1") Integer current) {

        return Result.success(employeeService.page(new Page<>(current, SystemConstant.MAX_PAGE_SIZE)).convert(employee -> {
            EmployeeVO employeeVO = new EmployeeVO();
            BeanUtils.copyProperties(employee, employeeVO);
            return employeeVO;
        }));
    }
    //http://localhost:8085/admin/employee/page
}
