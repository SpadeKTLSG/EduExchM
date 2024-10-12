package org.shop.common.utils;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shop.pojo.entity.User;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 自制BeanUtil工具类
 */
public class NewBeanUtil {

    /**
     * 获取所有的属性值为空属性名数组
     */
    public static String[] getNullPropertyNames(Object source) {

        PropertyDescriptor[] pds = PropertyUtils.getPropertyDescriptors(source); // Apache Commons BeanUtils

        return Stream.of(pds)
                .filter(pd -> { // 过滤出值为空的属性
                    try {
                        return pd.getReadMethod().invoke(source) == null; // 通过反射获取属性值
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(PropertyDescriptor::getName)
                .toArray(String[]::new);
    }

    /**
     * 自制DTO - Service映射服务
     */
    public static <T> void dtoMapService(Map<Object, IService> dtoServiceMap, Long id, Optional<T> optionalProd) {
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
