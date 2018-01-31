package cn.kumiaojie.erp.entity;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 角色实体类
 * 
 * @author Stivechen
 *
 */
public class Role {
	private Long uuid;// 编号
	private String name;// 名称
	
	@JSONField(serialize = false)
	private List<Menu> menus;// 角色下的权限菜单
	
	@JSONField(serialize = false)
	private List<Emp> emps;//角色下的用户列表

	public List<Emp> getEmps() {
		return emps;
	}

	public void setEmps(List<Emp> emps) {
		this.emps = emps;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public Long getUuid() {
		return uuid;
	}

	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}