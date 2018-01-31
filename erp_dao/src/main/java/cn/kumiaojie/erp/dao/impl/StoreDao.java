package cn.kumiaojie.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IStoreDao;
import cn.kumiaojie.erp.entity.Store;

/**
 * 仓库数据访问实现类 
 * @author Stivechen
 *
 */
public class StoreDao extends BaseDao<Store> implements IStoreDao {


	/**
	 * 离线查询条件
	 * @param store1
	 * @param store2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Store store1,Store store2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Store.class);
		// 模糊查询(判断store1是否为空,不然进行后续步骤会报错)
		if(store1!=null){
			if(null != store1.getName() && store1.getName().trim().length()>0){
				detachedCriteria.add(Restrictions.like("name", store1.getName(), MatchMode.ANYWHERE));
			}
			//根据员工编号查询
			if(null != store1.getEmpuuid()){
				detachedCriteria.add(Restrictions.eq("empuuid", store1.getEmpuuid()));
			}

		}
		return detachedCriteria;
	}

}
