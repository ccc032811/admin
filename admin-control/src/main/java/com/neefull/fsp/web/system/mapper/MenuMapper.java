package com.neefull.fsp.web.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neefull.fsp.web.system.entity.Menu;

import java.util.List;

/**
 * @author pei.wang
 */
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 查找用户权限集
     *
     * @param username 用户名
     * @return 用户权限集合
     */
    List<Menu> findUserPermissions(String username);

    List<Menu> findUserPermissionList(String userName);

    /**
     * 查找用户菜单集合
     *
     * @param username 用户名
     * @return 用户菜单集合
     */
    List<Menu> findUserMenus(String username);

    /**
     * 递归删除菜单/按钮
     *
     * @param menuId menuId
     */
    void deleteMenus(String menuId);


    /**根据名字删除
     * @param name
     */
    void deleteMenuByName(String name);
}
