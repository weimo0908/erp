package cn.kumiaojie.erp.web.filter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

/**
 * 自定义授权过滤器
 * @author Stivechen
 *
 */
public class ErpAuthorizationFilter extends AuthorizationFilter {

	@Override
	protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
		// 获取主题
		Subject subject = getSubject(request, response);
		//提取perms
		String[] perms = (String[]) mappedValue;
		//进行判断,根据结果判断
		//若为空直接不过滤?
		if (null == perms || perms.length == 0 ) {
			return true;
		}
		//不为空和null时候
		if (null != perms && perms.length > 0) {
			//循环遍历
			for (String p : perms) {
				//只要有一个权限,就返回true
				if (subject.isPermitted(p)) {
					return true;
				}
			}
		}
		return false;
	}

}
