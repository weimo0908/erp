package cn.kumiaojie.erp.dao.impl;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IInventoryDao;
import cn.kumiaojie.erp.entity.Inventory;
/**
 * 盘盈盘亏数据访问类
 * @author Administrator
 *
 */
public class InventoryDao extends BaseDao<Inventory> implements IInventoryDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Inventory inventory1,Inventory inventory2,Object param){
		DetachedCriteria dc=DetachedCriteria.forClass(Inventory.class);
		if(inventory1!=null){
			if(null != inventory1.getType() && inventory1.getType().trim().length()>0){
				dc.add(Restrictions.eq("type", inventory1.getType()));
			}
			if(null != inventory1.getState() && inventory1.getState().trim().length()>0){
				dc.add(Restrictions.eq("state", inventory1.getState()));
			}
			if(null != inventory1.getRemark() && inventory1.getRemark().trim().length()>0){
				dc.add(Restrictions.eq("remark", inventory1.getRemark()));
			}
			if(inventory1.getCreatetime()!=null ){
				dc.add(Restrictions.gt("createtime", inventory1.getCreatetime()));
			}
		}
		if(inventory2 != null){
			if(inventory2.getCreatetime() !=null){
				dc.add(Restrictions.lt("createtime", inventory2.getCreatetime()));
			}
			if(inventory2.getChecktime() != null){
				dc.add(Restrictions.lt("checktime", inventory2.getChecktime()));
			}
		}
		return dc;
	}

	@Override
	public List<Map<String,Object>> getYearList(Long storeuuid) {
		String hql = "select new Map(year(i.checktime) as year) from Inventory i where i.storeuuid = ? group by year(i.checktime)";
		return (List<Map<String,Object>>) this.getHibernateTemplate().find(hql,storeuuid);
	}
	
	

}
