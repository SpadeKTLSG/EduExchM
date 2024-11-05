package org.shop.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.shop.admin.entity.Menu;
import org.shop.admin.entity.vo.MenuVO;

import java.util.List;

public interface MenuService extends IService<Menu> {

    /**
     * 获取前端系统菜单列表, 不包含按钮
     */
    List<MenuVO> listNav();


    /**
     * 获取前端系统菜单列表
     */
    List<MenuVO> listMenu();

    /**
     * 通过父菜单id获得子菜单
     */
    List<Menu> listChildByParent(Long menuId);

    /**
     * 删除菜单项目
     */
    void delMenu(Long menuId);
}
