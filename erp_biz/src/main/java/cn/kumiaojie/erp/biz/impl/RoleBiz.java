package cn.kumiaojie.erp.biz.impl;

import java.util.ArrayList;
import java.util.List;

import cn.kumiaojie.erp.biz.IRoleBiz;
import cn.kumiaojie.erp.dao.IMenuDao;
import cn.kumiaojie.erp.dao.IRoleDao;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Menu;
import cn.kumiaojie.erp.entity.Role;
import cn.kumiaojie.erp.entity.Tree;
import cn.kumiaojie.erp.exception.ERPException;
import redis.clients.jedis.Jedis;

/**
 * RoleBiz实现类
 * 
 * @author Stivechen
 *
 */
public class RoleBiz extends BaseBiz<Role> implements IRoleBiz {

	// 注入IRoleDao
	private IRoleDao roleDao;
	// 引入menuDao
	private IMenuDao menuDao;

	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
		// 继承父类的setBaseDao,并注入role,让其具体的方法有具体的对象
		super.setBaseDao(this.roleDao);
	}

	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
	}
	
	//注入Jedis---->更新角色权限时候需要删除
	private Jedis jedis;
	
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	/**
	 * 获取角色权限
	 * @param uuid选中的角色id
	 */
	@Override
	public List<Tree> readRoleMenu(Long uuid) {
		// 分析easyui的Tree数据要求的结构后发现其最外层是List数组(TreeList)
		List<Tree> treeList = new ArrayList<Tree>();
		// 角色下的权限菜单只有二级菜单
		// 获取角色信息
		Role role = roleDao.get(uuid);
		// 获取角色下的菜单
		List<Menu> roleMenus = role.getMenus();
		// 获取跟菜单对象,然后相互匹配,
		Menu rootMenu = menuDao.get("0");
		Tree t1 = null;// 一级菜单
		Tree t2 = null;// 二级菜单
		// 开始循环rootMunu
		for (Menu m1 : rootMenu.getMenus()) {
			t1 = new Tree();
			t1.setId(m1.getMenuid());// 设置一级菜单id
			t1.setText(m1.getMenuname());// 设置一级菜单名称
			// 还不知到是否有children菜单
			// 循环二级菜单
			for (Menu m2 : m1.getMenus()) {
				t2 = new Tree();
				t2.setId(m2.getMenuid());// 设置二级菜单id
				t2.setText(m2.getMenuname());// 设置二级菜单名字
				// 判断roleMenus中是否包含对应的m2菜单
				if (roleMenus.contains(m2)) {
					t1.setChecked(true);
				}
				// 一级菜单里添加二级菜单
				t1.getChildren().add(t2);
			}
			treeList.add(t1);
		}
		return treeList;
	}

	/**
	 * 更新角色权限设置
	 * @param uuid选中的角色id
	 * @param checkStr二级菜单字符串
	 */
	@Override
	public void updateRoleMenus(Long uuid, String checkedStr) {
		// 根据选中的uuid获得对应的role
		Role role = roleDao.get(uuid);
		// 清楚role角色下的所有菜单权限(重新赋予)
		role.setMenus(new ArrayList<Menu>());
		//前端已经判断了checkStr不为空
		String[] ids = checkedStr.split(",");
		Menu menu = null;
		//遍历数组进行角色菜单添加
		for (String id : ids) {
			//根据id通过menuDao获取真实的菜单编号 :即字符串--->menu对象
			menu = menuDao.get(id);
			//保存角色
			role.getMenus().add(menu);
		}
		
		//通过role反查有那些emp用了这个角色,逐个删除
		List<Emp> empList = role.getEmps();
		//防止删除空的,报出异常
		try {
			for (Emp emp : empList) {
				//清除每个用户下的jedis缓存
				jedis.del("menuList_" + emp.getUuid());
			} 
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
