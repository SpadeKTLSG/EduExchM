package org.shop.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.shop.admin.common.constant.MessageConstant;
import org.shop.admin.common.exception.BlockActionException;
import org.shop.admin.entity.Menu;
import org.shop.admin.entity.vo.MenuVO;
import org.shop.admin.mapper.MenuMapper;
import org.shop.admin.service.MenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {


    @Override
    public List<MenuVO> listNav() {
        List<Menu> res = this.list(new LambdaQueryWrapper<Menu>()
                .ne(Menu::getType, 2)
                .orderByAsc(Menu::getOrderNum)
        );

        Map<Long, List<Menu>> menuMap = res.stream()
                .sorted(Comparator.comparing(Menu::getOrderNum))
                .collect(Collectors.groupingBy(Menu::getParentId));

        List<Menu> rootMenu = menuMap.get(0L);
        if (CollectionUtil.isEmpty(rootMenu)) {
            return Collections.emptyList();
        }

        //补充T字段
        List<MenuVO> rootMenuVO = rootMenu.stream().map(menu -> {
            MenuVO menuVO = new MenuVO();
            BeanUtils.copyProperties(menu, menuVO);
            menuVO.setList(menuMap.get(menu.getMenuId()));
            return menuVO;
        }).collect(Collectors.toList());

        rootMenuVO.forEach(menuVO -> {
            List<Menu> childMenus = menuMap.get(menuVO.getMenuId());
            if (childMenus != null) {
                List<MenuVO> childMenuVOs = childMenus.stream().map(childMenu -> {
                    MenuVO childMenuVO = new MenuVO();
                    BeanUtils.copyProperties(childMenu, childMenuVO);
                    childMenuVO.setParentName("null"); // 手动设置 parentName = null 字段适配前端
                    return childMenuVO;
                }).collect(Collectors.toList());
                menuVO.setList(childMenuVOs);
            }
        });

        return rootMenuVO;
    }

    @Override
    public List<MenuVO> listMenu() {
        List<Menu> res = this.list(new LambdaQueryWrapper<Menu>()
                .orderByAsc(Menu::getOrderNum)
        );
        List<MenuVO> resVO = res.stream().map(menu -> {
            MenuVO menuVO = new MenuVO();
            BeanUtils.copyProperties(menu, menuVO);
            return menuVO;
        }).toList();
        return resVO;
    }

    @Override
    public List<Menu> listChildByParent(Long menuId) {
        return this.list(new LambdaQueryWrapper<Menu>()
                .select(Menu::getMenuId, Menu::getName)
                .eq(Menu::getParentId, menuId)
        );
    }

    @Override
    public void delMenu(Long menuId) {
        List<Menu> menuList = this.listChildByParent(menuId);        //有子菜单或按钮时候不能删除
        if (!menuList.isEmpty()) throw new BlockActionException(MessageConstant.BLOCK_ACTION);
        this.removeById(menuId);
    }
}
