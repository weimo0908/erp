package cn.kumiaojie.erp.dao;

import java.util.List;
import java.util.Map;

import cn.kumiaojie.erp.entity.Inventory;
/**
 * 盘盈盘亏数据访问接口
 * @author Administrator
 *
 */
public interface IInventoryDao extends IBaseDao<Inventory>{

	List<Map<String,Object>> getYearList(Long storeuuid);

}
