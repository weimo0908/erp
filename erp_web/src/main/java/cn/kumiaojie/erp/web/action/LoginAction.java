package cn.kumiaojie.erp.web.action;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;

public class LoginAction {

	// 注入用户登录属性
	private String username;// 用户名
	private String pwd;// 密码

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}


	/**
	 * 用户登录校验
	 */
	public void checkUser() {
		/*
		 * 原有方法 try { //调用业务层返回对象 Emp existUser =
		 * empBiz.findByUsernameAndPwd(username, pwd); //判断是否存在 if (null
		 * ==existUser) { //用户名和密码不一致 BaseActionUtil.returnAjax(false,
		 * "对不起，用户名与密码不匹配！"); return; }else { //用户名存在
		 * ActionContext.getContext().getSession().put(LOGINUSER, existUser);
		 * BaseActionUtil.returnAjax(true, ""); } } catch (Exception e) {
		 * e.printStackTrace(); BaseActionUtil.returnAjax(false, "登录失败，请稍后再试！");
		 * }
		 */

		try {
			// 1.创建令牌,身份证明
			UsernamePasswordToken upt = new UsernamePasswordToken(username, pwd);
			// 2.获取主题subject:封装当前用户的一些操作
			Subject subject = SecurityUtils.getSubject();
			// 3.执行login
			subject.login(upt);
			// 成功登录
			BaseActionUtil.returnAjax(true, "");
		} catch (AuthenticationException a) {
			BaseActionUtil.returnAjax(false, "对不起，用户名与密码不匹配！");
		} catch (Exception e) {
			e.printStackTrace();
			BaseActionUtil.returnAjax(false, "登录失败!");
		}

	}

	/**
	 * 显示用户登录姓名
	 */
	public void showName() {
		/*
		 * Emp user = (Emp)
		 * ActionContext.getContext().getSession().get(LOGINUSER); if (null
		 * !=user) { //登录成功显示用户姓名 BaseActionUtil.returnAjax(true,
		 * user.getName()); }else { //登录失败，显示空字符串就好
		 * BaseActionUtil.returnAjax(false, ""); }
		 */

		// 获取主题
		Subject subject = SecurityUtils.getSubject();
		// 获取登录对象
		Emp emp = (Emp) subject.getPrincipal();
		// 判断是否有用户名
		if (null != emp) {
			// 登录成功显示用户名
			BaseActionUtil.returnAjax(true, emp.getName());
		} else {
			// 登录不成功
			BaseActionUtil.returnAjax(false, "");
		}
	}

	/**
	 * 注销用户登录
	 */
	public void loginOut() {
		/* ActionContext.getContext().getSession().remove(LOGINUSER); */
		// 获取主角,登出
		SecurityUtils.getSubject().logout();
	}

	
}
