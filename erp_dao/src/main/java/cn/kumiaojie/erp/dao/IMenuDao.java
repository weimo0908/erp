package cn.kumiaojie.erp.dao;

import java.util.List;

import cn.kumiaojie.erp.entity.Menu;

/**
 * 部门数据访问接口
 * @author Stivechen
 *
 */
public interface IMenuDao extends IBaseDao<Menu>{
	
	/**
	 * 查询用户下的权限菜单
	 * @param uuid
	 * @return
	 */
	List<Menu> getMenuByEmpuuid(Long uuid);
}
