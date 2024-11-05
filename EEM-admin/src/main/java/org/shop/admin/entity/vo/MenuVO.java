package org.shop.admin.entity.vo;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 前端系统菜单
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "前端系统菜单VO")
public class MenuVO {

    /**
     * 主键 菜单ID
     */
    private Long menuId;

    /**
     * 父菜单ID，一级菜单为0
     */
    private Long parentId;

    /**
     * 父菜单名称 Tmodel
     */
    private String parentName;

    /**
     * 菜单名称
     */
    private String name;

    /**
     * 菜单URL
     */
    private String url;


    /**
     * 类型
     * <p>
     * 0：目录   1：菜单   2：按钮
     */
    private Integer type;


    /**
     * 排序
     */
    private Integer orderNum;

    /**
     * 存储区 Tmodel
     */
    private List<?> list;
}
