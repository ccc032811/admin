package com.neefull.fsp.web.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neefull.fsp.web.common.authentication.ShiroRealm;
import com.neefull.fsp.web.common.entity.MenuTree;
import com.neefull.fsp.web.common.utils.TreeUtil;
import com.neefull.fsp.web.system.entity.Menu;
import com.neefull.fsp.web.system.entity.ServeMenu;
import com.neefull.fsp.web.system.mapper.MenuMapper;
import com.neefull.fsp.web.system.service.IMenuService;
import com.neefull.fsp.web.system.service.IServeService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author pei.wang
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements IMenuService {


    @Autowired
    private ShiroRealm shiroRealm;
    @Autowired
    private IServeService serveService;

    @Override
    public List<Menu> findUserPermissions(String username) {
        return this.baseMapper.findUserPermissions(username);
    }

    @Override
    public List<Menu> findUserPermissionList(String userName) {

        return this.baseMapper.findUserPermissionList(userName);
    }

    @Override
    public void deleteMenuByName(String name) {
        this.baseMapper.deleteMenuByName(name);
    }

    @Override
    public Menu selectByName(String name) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("menu_name",name);
        return this.baseMapper.selectOne(queryWrapper);

    }

    @Override
    @Transactional
    public void addMenu(ServeMenu serveMenu) {
        Menu menu = new Menu();
        menu.setParentId((long) 247);
        menu.setMenuName(serveMenu.getName());
        menu.setPerms(serveMenu.getPerms());
        menu.setType("0");
        menu.setCreateTime(new Date());
        save(menu);
    }

    @Override
    public MenuTree<Menu> findUserMenus(String username) {
        List<Menu> menus = this.baseMapper.findUserMenus(username);
        for(int i = menus.size()-1;i>=0;i--){
            ServeMenu serveMenu = serveService.queryServeByName(menus.get(i).getMenuName());
            if(serveMenu!=null&&serveMenu.getStatus()!=1){
                menus.remove(menus.get(i));
            }
        }
        List<MenuTree<Menu>> trees = this.convertMenus(menus);
        MenuTree<Menu> menuMenuTree = TreeUtil.buildMenuTree(trees);
        return menuMenuTree;
    }

    @Override
    public MenuTree<Menu> findMenus(Menu menu) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(menu.getMenuName())) {
            queryWrapper.lambda().like(Menu::getMenuName, menu.getMenuName());
        }
        queryWrapper.lambda().orderByAsc(Menu::getOrderNum);
        List<Menu> menus = this.baseMapper.selectList(queryWrapper);
        List<MenuTree<Menu>> trees = this.convertMenus(menus);

        return TreeUtil.buildMenuTree(trees);
    }

    @Override
    public List<Menu> findMenuList(Menu menu) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(menu.getMenuName())) {
            queryWrapper.lambda().like(Menu::getMenuName, menu.getMenuName());
        }
        queryWrapper.lambda().orderByAsc(Menu::getMenuId).orderByAsc(Menu::getOrderNum);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional
    public void createMenu(Menu menu) {
        menu.setCreateTime(new Date());
        this.setMenu(menu);
        this.baseMapper.insert(menu);
    }


    @Override
    @Transactional
    public void updateMenu(Menu menu) {
        menu.setModifyTime(new Date());
        this.setMenu(menu);
        this.baseMapper.updateById(menu);

        shiroRealm.clearCache();
    }

    @Override
    @Transactional
    public void deleteMeuns(String menuIds) {
        String[] menuIdsArray = menuIds.split(StringPool.COMMA);
        for (String menuId : menuIdsArray) {
            // 递归删除这些菜单/按钮
            this.baseMapper.deleteMenus(menuId);
        }

        shiroRealm.clearCache();
    }



    private List<MenuTree<Menu>> convertMenus(List<Menu> menus) {
        List<MenuTree<Menu>> trees = new ArrayList<>();
        menus.forEach(menu -> {
            MenuTree<Menu> tree = new MenuTree<>();
            tree.setId(String.valueOf(menu.getMenuId()));
            tree.setParentId(String.valueOf(menu.getParentId()));
            tree.setTitle(menu.getMenuName());
            tree.setIcon(menu.getIcon());
            tree.setHref(menu.getUrl());
            tree.setData(menu);
            trees.add(tree);
        });
        return trees;
    }

    private void setMenu(Menu menu) {
        if (menu.getParentId() == null)
            menu.setParentId(Menu.TOP_NODE);
        if (Menu.TYPE_BUTTON.equals(menu.getType())) {
            menu.setUrl(null);
            menu.setIcon(null);
        }
    }
}
