package cn.kumiaojie.erp.entity;

import java.util.List;

/**
 * 菜单实体类
 * 
 * @author Stivechen
 *
 */
public class Menu {
	private String menuid;// 菜单ID
	private String menuname;// 菜单名字
	private String icon;// 图标
	private String url;// url连接
	// 自关联
	private List<Menu> menus;// 子菜单

	public String getMenuid() {
		return menuid;
	}

	public void setMenuid(String menuid) {
		this.menuid = menuid;
	}

	public String getMenuname() {
		return menuname;
	}

	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

}
