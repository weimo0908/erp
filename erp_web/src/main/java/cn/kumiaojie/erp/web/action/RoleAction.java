package cn.kumiaojie.erp.web.action;

import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.kumiaojie.erp.biz.IRoleBiz;
import cn.kumiaojie.erp.entity.Role;
import cn.kumiaojie.erp.entity.Tree;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;

/**
 * 角色Action 
 * @author Administrator
 *
 */
public class RoleAction extends BaseAction<Role> {

	private IRoleBiz roleBiz;

	public void setRoleBiz(IRoleBiz roleBiz) {
		this.roleBiz = roleBiz;
		super.setBaseBiz(this.roleBiz);
	}
	
	//提供页面属性接收checkStr
	private String checkedStr;//ID字符串

	public String getCheckedStr() {
		return checkedStr;
	}
	public void setCheckedStr(String checkedStr) {
		this.checkedStr = checkedStr;
	}


	/**
	 *获取角色权限菜单 
	 */
	public void readRoleMenu(){
		List<Tree> roleMenu = roleBiz.readRoleMenu(getId());
		BaseActionUtil.write(JSON.toJSONString(roleMenu));
	}
	

	/**
	 * 更新角色权限菜单
	 */
	public void updateRoleMenus(){
		try {
			roleBiz.updateRoleMenus(getId(), checkedStr);
			BaseActionUtil.returnAjax(true, "保存角色权限菜单成功!");
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "保存角色权限菜单失败!");
			e.printStackTrace();
		}
	}
	
}
