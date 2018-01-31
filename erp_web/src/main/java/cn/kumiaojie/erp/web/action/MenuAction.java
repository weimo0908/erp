package cn.kumiaojie.erp.web.action;

import com.alibaba.fastjson.JSON;

import cn.kumiaojie.erp.biz.IMenuBiz;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Menu;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;
import cn.kumiaojie.erp.web.utils.GetLoginUser;

/**
 * 部门Action
 * 
 * @author Stivechen
 *
 */
public class MenuAction extends BaseAction<Menu>{

	private IMenuBiz menuBiz;

	public void setMenuBiz(IMenuBiz menuBiz) {
		this.menuBiz = menuBiz;
		super.setBaseBiz(this.menuBiz);
	}
	
	public void getMenuTree(){
		/*//获取其顶层级，即可获取所有的子层级
		Menu menu = menuBiz.get("0");
		//转换json并输出
		BaseActionUtil.write(JSON.toJSONString(menu, true));*/
		
		//得到当前的登录用户
		Emp emp = (Emp) GetLoginUser.getLoginUser();
		Menu menu = menuBiz.readMenuByEmpuuid(emp.getUuid());
		BaseActionUtil.write(JSON.toJSONString(menu));
	}
	
	
}
