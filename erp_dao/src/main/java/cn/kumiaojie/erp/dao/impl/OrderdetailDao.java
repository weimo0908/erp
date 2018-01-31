package cn.kumiaojie.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IOrderdetailDao;
import cn.kumiaojie.erp.entity.Orderdetail;

/**
 * 仓库库存数据访问实现类 
 * @author Stivechen
 *
 */
public class OrderdetailDao extends BaseDao<Orderdetail> implements IOrderdetailDao {

	/**
	 * 离线查询条件
	 * @param orderdetail1
	 * @param orderdetail2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Orderdetail orderdetail1, Orderdetail orderdetail2, Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Orderdetail.class);
		// 模糊查询(判断orderdetail1是否为空,不然进行后续步骤会报错)
		if (null != orderdetail1) {
			if (null != orderdetail1.getGoodsname() && orderdetail1.getGoodsname().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("goodsname", orderdetail1.getGoodsname(), MatchMode.ANYWHERE));
			}
			if (null != orderdetail1.getState() && orderdetail1.getState().trim().length() > 0) {
				detachedCriteria.add(Restrictions.eq("state", orderdetail1.getState()));
			}
			//查询对应的订单
			if (null != orderdetail1.getOrders() &&null!= orderdetail1.getOrders().getUuid()) {
				detachedCriteria.add(Restrictions.eq("orders", orderdetail1.getOrders()));
			}
			
		}
		return detachedCriteria;
	}

}
