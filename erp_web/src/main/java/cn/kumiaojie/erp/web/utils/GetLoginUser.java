package cn.kumiaojie.erp.web.utils;

import org.apache.shiro.SecurityUtils;

import com.opensymphony.xwork2.ActionContext;

import cn.kumiaojie.erp.web.action.LoginAction;

public class GetLoginUser {

	/**
	 * 获取session中的对象
	 * 需要强转对象
	 * @return
	 */
	public static Object getLoginUser(){
		
		/*return ActionContext.getContext().getSession().get(login);*/
		
		return SecurityUtils.getSubject().getPrincipal();
	}
}
