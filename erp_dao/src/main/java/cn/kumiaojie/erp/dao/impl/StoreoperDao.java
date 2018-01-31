package cn.kumiaojie.erp.dao.impl;

import java.util.Calendar;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IStoreoperDao;
import cn.kumiaojie.erp.entity.Storeoper;

/**
 * 仓库操作记录表数据访问实现类 
 * @author Stivechen
 *
 */
public class StoreoperDao extends BaseDao<Storeoper> implements IStoreoperDao {


	/**
	 * 离线查询条件
	 * @param storeoper1
	 * @param storeoper2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Storeoper storeoper1,Storeoper storeoper2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Storeoper.class);
		// 模糊查询(判断storeoper1是否为空,不然进行后续步骤会报错)
		if(storeoper1!=null){
			//根据类型查询
			if(null != storeoper1.getType() && storeoper1.getType().trim().length()>0){
				detachedCriteria.add(Restrictions.eq("type", storeoper1.getType()));
			}
			//根据库管员查询
			if (null != storeoper1.getEmpuuid()) {
				detachedCriteria.add(Restrictions.eq("empuuid", storeoper1.getEmpuuid()));
			}
			//商品查询
			if (null != storeoper1.getGoodsuuid()) {
				detachedCriteria.add(Restrictions.eq("goodsuuid", storeoper1.getGoodsuuid()));
			}
			//仓库
			if (null != storeoper1.getStoreuuid()) {
				detachedCriteria.add(Restrictions.eq("storeuuid", storeoper1.getStoreuuid()));
			}
			//操作时间
			if (null != storeoper1.getOpertime()) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(storeoper1.getOpertime());
				cal.set(Calendar.HOUR, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				detachedCriteria.add(Restrictions.ge("opertime",cal.getTime()));
			}
		}
		if (null !=storeoper2) {
			if (null != storeoper2.getOpertime()) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(storeoper2.getOpertime());
				cal.set(Calendar.HOUR, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MILLISECOND, 999);
				detachedCriteria.add(Restrictions.le("opertime",cal.getTime()));
			}
		}
		
		return detachedCriteria;
	}

}
