package com.neefull.fsp.web.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neefull.fsp.web.system.entity.Menu;
import com.neefull.fsp.web.system.entity.ServeMenu;
import com.neefull.fsp.web.system.mapper.ServeMapper;
import com.neefull.fsp.web.system.service.IMenuService;
import com.neefull.fsp.web.system.service.IRoleMenuService;
import com.neefull.fsp.web.system.service.IServeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: chengchengchu
 * @Date: 2020/2/10  14:32
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class ServceServiceImpl extends ServiceImpl<ServeMapper, ServeMenu> implements IServeService {

    @Autowired
    private ServeMapper serveMapper;
    @Autowired
    private IMenuService menuService;
    @Autowired
    private IRoleMenuService roleMenuService;


    @Override
    public IPage<ServeMenu> getServeMenus(ServeMenu serveMenu) {
        Page<ServeMenu> page = new Page<>(serveMenu.getPageNum(),serveMenu.getPageSize());
        IPage<ServeMenu> serveMenuIPage = serveMapper.getServeMenu(page,serveMenu);
        return serveMenuIPage;

    }

    @Override
    @Transactional
    public void updateStatusById(ServeMenu serveMenu) {
        if(serveMenu.getStatus()==2){
            serveMenu.setAlive(0);
        }
        serveMapper.updateById(serveMenu);
//        serveMapper.updateStatusById(serveMenu);
    }

    @Override
    @Transactional
    public void addServeMenu(ServeMenu serveMenu) {
        serveMenu.setStatus(0);
        this.save(serveMenu);
        menuService.addMenu(serveMenu);

    }

    @Override
    @Transactional
    public void deleteServeMenu(String[] ids) {
        List<String> list = Arrays.asList(ids);
        for (String idStr : list) {
            ServeMenu serveMenu = serveMapper.selectById(Integer.parseInt(idStr));
            Menu menu = menuService.selectByName(serveMenu.getName());
            roleMenuService.deleteRoleByMenuId(menu.getMenuId());
            menuService.deleteMenuByName(serveMenu.getName());
        }
        serveMapper.deleteBatchIds(list);
    }

    @Override
    public List<ServeMenu> queryServe() {
        List<ServeMenu> serveMenuList = serveMapper.queryServe();

        return serveMenuList;
    }

    @Override
    public ServeMenu queryServeByName(String menuName) {
        QueryWrapper<ServeMenu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",menuName);
        ServeMenu serveMenu = serveMapper.selectOne(queryWrapper);
        return serveMenu;
    }

    @Override
    public List<ServeMenu> getAllServeMenus() {

        List<ServeMenu> serveMenuList = serveMapper.getAllServeMenus();
        return serveMenuList;
    }


}
