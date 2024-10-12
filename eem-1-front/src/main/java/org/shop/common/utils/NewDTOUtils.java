package org.shop.common.utils;

import com.shop.serve.service.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;

/**
 * 自制需要Service的工具类
 */
@Component
@Slf4j
public class NewDTOUtils {

    // 需要注入各类依赖, 需要不时更新

    private ProdService prodService;

    private ProdFuncService prodFuncService;

    private UserService userService;

    private UserFuncService userFuncService;

    private UserDetailService userDetailService;


    /**
     * 创建并组合DTOs
     * <p>劲啊, 联表查询组合特攻</p>
     */
    public <VO> VO createAndCombineDTOs(Class<VO> voClass, Object id, Class<?>... dtoClasses) throws Exception {
        VO vo = voClass.getDeclaredConstructor().newInstance();

        for (Class<?> dtoClass : dtoClasses) {
            Object dto = dtoClass.getDeclaredConstructor().newInstance();

            switch (dtoClass.getSimpleName()) {
                case "ProdAllDTO" -> {
                    BeanUtils.copyProperties(prodService.getById((Long) id), dto);
                }
                case "ProdFuncAllDTO" -> {
                    BeanUtils.copyProperties(prodFuncService.getById((Long) id), dto);
                }
                case "UserAllDTO" -> {
                    BeanUtils.copyProperties(userService.getById((Long) id), dto);
                }
                case "UserFuncAllDTO" -> {
                    BeanUtils.copyProperties(userFuncService.getById((Long) id), dto);
                }
                case "UserDetailAllDTO" -> {
                    BeanUtils.copyProperties(userDetailService.getById((Long) id), dto);
                }
            }

            // 获得DTO和VO的所有属性
            PropertyDescriptor[] dtoProperties = PropertyUtils.getPropertyDescriptors(dto);
            PropertyDescriptor[] voProperties = PropertyUtils.getPropertyDescriptors(vo);

            // 只赋值非空属性
            for (PropertyDescriptor dtoProperty : dtoProperties) {
                for (PropertyDescriptor voProperty : voProperties) {
                    if (dtoProperty.getName().equals(voProperty.getName()) && dtoProperty.getReadMethod().invoke(dto) != null) {
                        voProperty.getWriteMethod().invoke(vo, dtoProperty.getReadMethod().invoke(dto));
                    }
                }
            }
        }

        return vo;
    }

}
