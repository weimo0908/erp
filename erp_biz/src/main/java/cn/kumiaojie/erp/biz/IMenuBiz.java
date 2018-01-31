package cn.kumiaojie.erp.biz;

import java.util.List;

import cn.kumiaojie.erp.entity.Menu;

public interface IMenuBiz extends IBaseBiz<Menu>{
	
	/**
	 * 查询用户下的菜单权限
	 * @param uuid
	 * @return
	 */
	List<Menu> getMenuByEmpuuid(Long uuid);
	
	/**
	 * 获取用户菜单
	 * @param uuid
	 * @return
	 */
	Menu readMenuByEmpuuid(Long uuid);
	
}
