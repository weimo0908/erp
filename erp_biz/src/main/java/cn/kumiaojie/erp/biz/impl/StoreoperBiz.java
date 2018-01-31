package cn.kumiaojie.erp.biz.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kumiaojie.erp.biz.IStoreoperBiz;
import cn.kumiaojie.erp.dao.IEmpDao;
import cn.kumiaojie.erp.dao.IGoodsDao;
import cn.kumiaojie.erp.dao.IStoreDao;
import cn.kumiaojie.erp.dao.IStoreoperDao;
import cn.kumiaojie.erp.entity.Storeoper;
import cn.kumiaojie.erp.utils.GetNameOrSaveUtil;

/**
 * StoreoperBiz实现类
 * 
 * @author Stivechen
 *
 */
public class StoreoperBiz extends BaseBiz<Storeoper> implements IStoreoperBiz {

	// 注入IStoreoperDao
	private IStoreoperDao storeoperDao;
	//注入igoodsDao
	private IGoodsDao goodsDao;
	//注入iempdao
	private IEmpDao empDao;
	//注入storedao
	private IStoreDao storeDao;
	
	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
		//继承父类的setBaseDao,并注入storeoper,让其具体的方法有具体的对象
		super.setBaseDao(this.storeoperDao);
	}

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	/* 
	 * 重写getLitByPage
	 */
	public List<Storeoper> getListByPage(Storeoper storeoper1, Storeoper storeoper2, Object param, int firstResult, int maxResults){
		//获取,加工
		List<Storeoper> list = super.getListByPage(storeoper1, storeoper2, param, firstResult, maxResults);
		//缓存goodsName集合
		Map<Long, String> goodsNameMap = new HashMap<>();
		//缓存empName集合
		Map<Long, String> empNameMap = new HashMap<>();
		//缓存storeName集合
		Map<Long, String> storeNameMap = new HashMap<>();
		for (Storeoper s : list) {
			//调用GetNameOrSaveUtil工具类
			s.setEmpName(GetNameOrSaveUtil.getEmpName(s.getEmpuuid(), empNameMap, empDao));
			s.setGoodsName(GetNameOrSaveUtil.getGoodsName(s.getGoodsuuid(), goodsNameMap, goodsDao));
			s.setStoreName(GetNameOrSaveUtil.getStoreName(s.getStoreuuid(), storeNameMap, storeDao));
		}
		return list;
		
	}
	
	
	
	
}
