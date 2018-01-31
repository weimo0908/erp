package cn.kumiaojie.erp.biz;

import java.util.List;

import cn.kumiaojie.erp.entity.Role;
/**
 * 角色业务层接口
 * @author Stivechen
 *
 */
import cn.kumiaojie.erp.entity.Tree;
public interface IRoleBiz extends IBaseBiz<Role>{
	
	/**
	 * 读取角色菜单
	 * @param uuid角色id
	 * @return
	 */
	List<Tree> readRoleMenu(Long uuid);
	
	/**
	 * 更新角色权限设置
	 * @param uuid 角色编号
	 * @param checkStr 勾选中的Id字符串,用,号隔开
	 */
	void updateRoleMenus(Long uuid,String checkStr);
	
}
