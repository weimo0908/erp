package cn.kumiaojie.erp.biz.impl;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;

import cn.kumiaojie.erp.biz.IMenuBiz;
import cn.kumiaojie.erp.dao.IMenuDao;
import cn.kumiaojie.erp.entity.Menu;
import redis.clients.jedis.Jedis;

/**
 * MenuBiz实现类
 * 
 * @author Stivechen
 *
 */
public class MenuBiz extends BaseBiz<Menu> implements IMenuBiz {

	// 注入IMenuDao
	private IMenuDao menuDao;

	public void setMenuDao(IMenuDao menuDao) {
		this.menuDao = menuDao;
		// 继承父类的setBaseDao,并注入menu,让其具体的方法有具体的对象
		super.setBaseDao(this.menuDao);
	}

	//注入Jedis
	private Jedis jedis;
	
	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}

	/**
	 * 查询用户下的菜单权限
	 * 引入jedis缓存技术
	 * @return	menuList
	 */
	@Override
	public List<Menu> getMenuByEmpuuid(Long uuid) {
		//1.尝试用缓存中取出menuList
		String menuListJson = jedis.get("menuList_"+uuid);
		//创建返回对象
		List<Menu> menuList = null;
		//判断是否有值
		if (null != menuListJson) {
			//有值
			//System.out.println("从缓存中取值出来..");
			//反转成对象
			menuList= JSON.parseArray(menuListJson, Menu.class);
		}else {
			//缓存中没有--->从数据库中查询出来
			//System.out.println("从数据库从查询..");
			menuList = menuDao.getMenuByEmpuuid(uuid);
			//存一份到缓存中
			jedis.set("menuList_"+uuid, JSON.toJSONString(menuList));
		}
		
		return menuList;
	}

	/**
	 * 获取用户菜单
	 */
	@Override
	public Menu readMenuByEmpuuid(Long uuid) {
		// 获取所有的菜单(原始菜单,做模板)
		Menu rootMenu = menuDao.get("0");
		// 获取当前用户的菜单(都是二级菜单)
		List<Menu> empMenu = menuDao.getMenuByEmpuuid(uuid);
		// 复制原始菜单,进行加工(最大级别菜单栏)
		Menu menu = cloneMenu(rootMenu);
		// 创建一,二级菜单接收器
		Menu _m1 = null;
		Menu _m2 = null;
		// 循环一级菜单
		for (Menu m1 : rootMenu.getMenus()) {
			_m1 = cloneMenu(m1);
			// 循环二级菜单
			for (Menu m2 : m1.getMenus()) {
				//判断用户是否包含了这个二级菜单
				if (empMenu.contains(m2)) {
					_m2 = cloneMenu(m2);//克隆
					//在一级菜单中加入二级菜单
					_m1.getMenus().add(_m2);
				}
			}
			//如果有_m1中有_m2,加入到menu中
			if (_m1.getMenus().size()>0) {
				menu.getMenus().add(_m1);
			}
			
		}

		return menu;
	}

	/**
	 * 复制菜单
	 * @param src 目标菜单
	 * @return 新菜单
	 */
	private Menu cloneMenu(Menu src) {
		Menu newMenu = new Menu();
		newMenu.setIcon(src.getIcon());
		newMenu.setMenuid(src.getIcon());
		newMenu.setMenuname(src.getMenuname());
		newMenu.setUrl(src.getUrl());
		// 新建一个菜单列表,这个很关键!
		newMenu.setMenus(new ArrayList<Menu>());
		return newMenu;
	}

}
