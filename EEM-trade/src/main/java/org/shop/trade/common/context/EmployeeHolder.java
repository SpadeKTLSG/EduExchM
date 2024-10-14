package org.shop.trade.common.context;


import org.shop.trade.entity.remote.EmployeeLocalDTO;

/**
 * 管理员上下文
 */
public class EmployeeHolder {


    /**
     * 管理员ThreadLocal
     */
    private static final ThreadLocal<EmployeeLocalDTO> employeeTL = new ThreadLocal<>();

    /**
     * 保存用户
     */
    public static void saveEmployee(EmployeeLocalDTO employeeLocalDTO) {
        employeeTL.set(employeeLocalDTO);
    }

    /**
     * 获取用户
     */
    public static EmployeeLocalDTO getEmployee() {
        return employeeTL.get();
    }

    /**
     * 移除用户
     */
    public static void removeEmployee() {
        employeeTL.remove();
    }
}
