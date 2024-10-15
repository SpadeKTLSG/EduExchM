package org.shop.guest.common.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.guest.entity.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 自制BeanUtil工具类
 */
public class NewBeanUtil {

    public static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = Arrays.stream(pds)
                .map(PropertyDescriptor::getName)
                .filter(propertyName -> src.getPropertyValue(propertyName) == null)
                .collect(Collectors.toSet());

        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


    /**
     * 自制User DTO - Service映射服务
     */
    public static <T> void userDtoMapService(Map<Object, IService> dtoServiceMap, Long id, Optional<T> optionalProd) {

        //执行dtoServiceMap中的每个service -> dto 更新方法
        for (Map.Entry<Object, IService> entry : dtoServiceMap.entrySet()) {
            // 从Map中取出DTO和Service
            Object dto = entry.getKey();
            IService service = entry.getValue();

            // 判断nullPN
            String[] nullPN = getNullPropertyNames(dto);

            //取出对象, 根据nullPN进行选择性更新
            Object target = service.getOne(Wrappers.<User>lambdaQuery()
                    .eq(User::getId, id));
            BeanUtils.copyProperties(dto, target, nullPN);
            service.updateById(target);
        }
    }
}
