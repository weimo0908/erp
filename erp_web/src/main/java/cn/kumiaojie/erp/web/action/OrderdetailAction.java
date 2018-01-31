package cn.kumiaojie.erp.web.action;

import cn.kumiaojie.erp.biz.IOrderdetailBiz;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Orderdetail;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;
import cn.kumiaojie.erp.web.utils.GetLoginUser;

/**
 * 商品Action
 * 
 * @author Stivechen
 *
 */
public class OrderdetailAction extends BaseAction<Orderdetail> {

	private IOrderdetailBiz orderdetailBiz;

	public void setOrderdetailBiz(IOrderdetailBiz orderdetailBiz) {
		this.orderdetailBiz = orderdetailBiz;
		super.setBaseBiz(this.orderdetailBiz);
	}

	// 设置从页面接收storeuuid方法
	private Long storeuuid;
	public Long getStoreuuid() {
		return storeuuid;
	}
	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}

	public void doInStore() {
		Emp storeManager = (Emp) GetLoginUser.getLoginUser();
		if (null == storeManager) {
			BaseActionUtil.returnAjax(false, "对不起,您还没有登录,请登录后再操作");
			return;
		}
		try {
			// 封装数据,进行biz层业务
			orderdetailBiz.doInStore(this.getId(), storeuuid, storeManager.getUuid());// this.getId()需要页面传入订单编号
			BaseActionUtil.returnAjax(true, "入库成功!");
		} catch (ERPException e1) {
			BaseActionUtil.returnAjax(false, e1.getMessage());
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "入库失败!");
		}
	}

	public void doOutStore() {
		Emp storeManager = (Emp) GetLoginUser.getLoginUser();
		if (null == storeManager) {
			BaseActionUtil.returnAjax(false, "对不起,您还没登录,请登录后在操作");
			return;
		}
		try {
			//封装数据,进行biz层业务
			orderdetailBiz.doOutStore(this.getId(), storeuuid, storeManager.getUuid());
			BaseActionUtil.returnAjax(true, "出库成功!");
		}catch(ERPException e1){
			BaseActionUtil.returnAjax(false, e1.getMessage());
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "出库失败!");
		}
		
	}

}
