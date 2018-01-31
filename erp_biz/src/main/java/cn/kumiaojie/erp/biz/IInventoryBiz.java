package cn.kumiaojie.erp.biz;
import java.util.List;
import java.util.Map;

import cn.kumiaojie.erp.entity.Inventory;
/**
 * 盘盈盘亏业务逻辑层接口
 * @author Administrator
 *
 */
public interface IInventoryBiz extends IBaseBiz<Inventory>{

	
	/**
	 * 
	 * @param empuuid
	 * @param inventoryID
	 * @throws Exception 
	 */
	void doCheck(Long empuuid, Long inventoryID) throws Exception;

	/**
	 * 
	 * @return
	 */
	List<Map<String,Object>> getYearList(Long storeuuid);

}

