package cn.kumiaojie.erp.web.action;

import cn.kumiaojie.erp.biz.IStoreBiz;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Store;
import cn.kumiaojie.erp.web.utils.GetLoginUser;

/**
 * 仓库Action
 * 
 * @author Stivechen
 *
 */
public class StoreAction extends BaseAction<Store>{

	private IStoreBiz storeBiz;

	public void setStoreBiz(IStoreBiz storeBiz) {
		this.storeBiz = storeBiz;
		super.setBaseBiz(this.storeBiz);
	}
	
	//动态查询q
	private String q;
	

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	/**
	 * 显示当前登录用户的仓库,每个库管员都有一个属于自己的仓库
	 */
	public void myList(){
		//如果页面没传递查询条件t1--->自己创建一个
		if (null == getT1()) {
			setT1(new Store());
		}
		//获取登录员工的uuid
		Emp existUser = (Emp) GetLoginUser.getLoginUser();
		//此处不用判断是否为空?
//		if (null == existUser) {
//			BaseActionUtil.returnAjax(false, "对不起,您还没有登录,请登录后操作");
//		}
		getT1().setEmpuuid(existUser.getUuid());
		//调用baseaction中的list方法查询列表
		super.list();
	}
	
	//重写list方法
	@Override
	public void list() {
		if (null == getT1()) {
			setT1(new Store());
		}
		getT1().setName(q);
		super.list();
	}
	
}
