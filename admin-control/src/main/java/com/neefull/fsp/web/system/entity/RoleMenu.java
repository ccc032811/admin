package com.neefull.fsp.web.system.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @author pei.wang
 */
@Data
@TableName("typt_role_menu")
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = -5200596408874170216L;
    /**
     * 角色ID
     */
    @TableField("ROLE_ID")
    private Long roleId;

    /**
     * 菜单/按钮ID
     */
    @TableField("MENU_ID")
    private Long menuId;


}
