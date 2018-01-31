package cn.kumiaojie.erp.web.action;

import cn.kumiaojie.erp.biz.IStoreoperBiz;
import cn.kumiaojie.erp.entity.Storeoper;

/**
 * 商品Action
 * 
 * @author Stivechen
 *
 */
public class StoreoperAction extends BaseAction<Storeoper>{

	private IStoreoperBiz storeoperBiz;

	public void setStoreoperBiz(IStoreoperBiz storeoperBiz) {
		this.storeoperBiz = storeoperBiz;
		super.setBaseBiz(this.storeoperBiz);
	}
	
	
}
