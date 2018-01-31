package cn.kumiaojie.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.ISupplierDao;
import cn.kumiaojie.erp.entity.Supplier;

/**
 * 商品数据访问实现类 
 * @author Stivechen
 *
 */
public class SupplierDao extends BaseDao<Supplier> implements ISupplierDao {


	/**
	 * 离线查询条件
	 * @param supplier1
	 * @param supplier2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Supplier supplier1,Supplier supplier2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Supplier.class);
		// 模糊查询(判断supplier1是否为空,不然进行后续步骤会报错)
		if (null != supplier1) {
			// 根据名字查询
			if (supplier1.getName() != null && supplier1.getName().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("name", supplier1.getName(), MatchMode.ANYWHERE));
			}
			// 根据联系地址查询
			if (supplier1.getAddress() != null && supplier1.getAddress().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("address", supplier1.getAddress(), MatchMode.ANYWHERE));
			}
			// 根据联系人查询
			if (supplier1.getContact() != null && supplier1.getContact().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("conctact", supplier1.getContact(), MatchMode.ANYWHERE));
			}
			// 根据联系电话查询
			if (supplier1.getTele() != null && supplier1.getTele().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("tele", supplier1.getTele(), MatchMode.ANYWHERE));
			}
			// 根据邮箱地址查询
			if (supplier1.getEmail() != null && supplier1.getEmail().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("email", supplier1.getEmail(), MatchMode.ANYWHERE));
			}
			
			//区分供应商还是客户
			if(null != supplier1.getType()&& supplier1.getType().trim().length()>0){
				detachedCriteria.add(Restrictions.eq("type", supplier1.getType()));
			}
		}
		
		//根据名称进行精确查找
		if (null != supplier2) {
			if (null != supplier2.getName()&&supplier2.getName().trim().length()>0) {
				detachedCriteria.add(Restrictions.eq("name", supplier2.getName()));
			}
		}
		
		return detachedCriteria;
	}

}
