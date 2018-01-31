package cn.kumiaojie.erp.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kumiaojie.erp.biz.IStoreBiz;
import cn.kumiaojie.erp.dao.IEmpDao;
import cn.kumiaojie.erp.dao.IStoreDao;
import cn.kumiaojie.erp.entity.Store;
import cn.kumiaojie.erp.utils.GetNameOrSaveUtil;

/**
 * StoreBiz实现类
 * 
 * @author Stivechen
 *
 */
public class StoreBiz extends BaseBiz<Store> implements IStoreBiz {

	// 注入IStoreDao
	private IStoreDao storeDao;
	//注入empDao
	private IEmpDao empDao;
	
	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
		//继承父类的setBaseDao,并注入store,让其具体的方法有具体的对象
		super.setBaseDao(this.storeDao);
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}



	/**
	 * 重写getListByPage方法
	 */
	public List<Store> getListByPage(Store t1, Store t2, Object param, int firstResult, int maxResults) {
		//获取list--->转换仓库管理员名字
		List<Store> storeList = super.getListByPage(t1, t2, param, firstResult, maxResults);
		Map<Long, String> managerNameMap = new HashMap<Long,String>();
		for (Store s : storeList) {
			s.setManagerName(GetNameOrSaveUtil.getEmpName(s.getEmpuuid(), managerNameMap, empDao));
		}
		return storeList;
	}
	
	
}
