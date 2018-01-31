package cn.kumiaojie.erp.dao;

import java.util.List;

import cn.kumiaojie.erp.entity.StoreAlert;
import cn.kumiaojie.erp.entity.Storedetail;

/**
 * 仓库库存表数据访问接口
 * @author Stivechen
 *
 */
public interface IStoredetailDao extends IBaseDao<Storedetail>{
	
	/**
	 * 获取库存预警列表
	 * @return
	 */
	public List<StoreAlert> getStroealertList();
	
}
