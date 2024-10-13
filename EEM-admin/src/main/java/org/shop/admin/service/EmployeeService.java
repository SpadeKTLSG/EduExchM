package org.shop.admin.service;


import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;
import org.shop.admin.entity.Employee;
import org.shop.admin.entity.dto.EmployeeAllDTO;
import org.shop.admin.entity.dto.EmployeeDTO;
import org.shop.admin.entity.dto.EmployeeLoginDTO;
import org.shop.admin.entity.vo.EmployeeVO;


public interface EmployeeService extends IService<Employee> {


    //! Func


    /**
     * 发送手机验证码
     *
     * @return 验证码
     */
    String sendCodeA(String phone, HttpSession session);


    /**
     * 登录
     *
     * @return Token
     */
    String loginA(EmployeeLoginDTO employeeLoginDTO, HttpSession session);


    /**
     * 注销
     */
    void logoutA();


    //! ADD


    /**
     * 新增一个员工
     */
    void postEmployeeA(EmployeeDTO employeeDTO);


    //! DELETE


    /**
     * 删除一个员工
     */
    void deleteEmployeeA(String account);


    //! UPDATE


    /**
     * 更新一个员工
     */
    void putEmployeeA(EmployeeAllDTO employeeAllDTO);


    //! QUERY


    /**
     * 获取一个员工
     */
    EmployeeVO getEmployeeA(String account);


}
