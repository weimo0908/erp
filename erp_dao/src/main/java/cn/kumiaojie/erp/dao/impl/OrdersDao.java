package cn.kumiaojie.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IOrdersDao;
import cn.kumiaojie.erp.entity.Orders;

/**
 * 商品数据访问实现类 
 * @author Stivechen
 *
 */
public class OrdersDao extends BaseDao<Orders> implements IOrdersDao {

	/**
	 * 离线查询条件
	 * @param orders1
	 * @param orders2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Orders orders1, Orders orders2, Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Orders.class);
		// 模糊查询(判断orders1是否为空,不然进行后续步骤会报错)
		if (null != orders1) {
			if (null != orders1.getType() && orders1.getType().trim().length() > 0) {
				detachedCriteria.add(Restrictions.eq("type", orders1.getType()));
			}
			if (null != orders1.getState() && orders1.getState().trim().length() > 0) {
				detachedCriteria.add(Restrictions.eq("state", orders1.getState()));
			}
			//查询我的订单
			if (null != orders1.getCreater()) {
				detachedCriteria.add(Restrictions.eqOrIsNull("creater", orders1.getCreater()));
			}
			
			//根据供应商或客户查询
			if (null != orders1.getSupplieruuid() && orders1.getState().trim().length() > 0) {
				detachedCriteria.add(Restrictions.eq("supplieruuid", orders1.getSupplieruuid()));
			}
			
			//根据库管员查询
			if (null != orders1.getEnder()) {
				detachedCriteria.add(Restrictions.eq("ender", orders1.getEnder()));
			}
			
			//下单日期
			if (null != orders1.getCreatetime()) {
				detachedCriteria.add(Restrictions.ge("createtime", orders1.getCreatetime()));
			}
			if(orders1.getUuid() !=null && !"".equals(orders1.getUuid())){
				detachedCriteria.add(Restrictions.eq("uuid", orders1.getUuid()));
			}
		}
		//另一个时间
		if (null != orders2) {
			if (null != orders2.getCreatetime()) {
				detachedCriteria.add(Restrictions.le("createtime", orders2.getCreatetime()));
			}
		}
		return detachedCriteria;
	}

}
