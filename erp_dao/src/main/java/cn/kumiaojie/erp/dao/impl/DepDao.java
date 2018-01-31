package cn.kumiaojie.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IDepDao;
import cn.kumiaojie.erp.entity.Dep;

/**
 * 部门数据访问实现类 
 * @author Stivechen
 *
 */
public class DepDao extends BaseDao<Dep> implements IDepDao {


	/**
	 * 离线查询条件
	 * @param dep1
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Dep dep1,Dep dep2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Dep.class);
		// 模糊查询(判断dep1是否为空,不然进行后续步骤会报错)
		if (null != dep1) {
			// 根据名字查询
			if (dep1.getName() != null && dep1.getName().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("name", dep1.getName(), MatchMode.ANYWHERE));
			}
			// 根据电话查询
			if (null != dep1.getTele() && dep1.getTele().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("tele", dep1.getTele(), MatchMode.ANYWHERE));
			}
		}
		
		if(null != dep2){
			if (null != dep2.getName() && dep2.getName().trim().length() > 0) {
				detachedCriteria.add(Restrictions.eq("name", dep2.getName()));
			}
		}
		return detachedCriteria;
	}

}
