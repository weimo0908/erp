package cn.kumiaojie.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IReturnorderdetailDao;
import cn.kumiaojie.erp.entity.Returnorderdetail;

/**
 * 退货订单明细数据访问类
 * @author Administrator
 *
 */
public class ReturnorderdetailDao extends BaseDao<Returnorderdetail> implements IReturnorderdetailDao {

	/**
	 * 构建查询条件
	 * @param dep1
	 * @param dep2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Returnorderdetail returnorderdetail1,Returnorderdetail returnorderdetail2,Object param){
		DetachedCriteria detachedCriteria=DetachedCriteria.forClass(Returnorderdetail.class);
		if(returnorderdetail1!=null){
			if(null != returnorderdetail1.getGoodsname() && returnorderdetail1.getGoodsname().trim().length()>0){
				detachedCriteria.add(Restrictions.like("goodsname", returnorderdetail1.getGoodsname(), MatchMode.ANYWHERE));
			}
			if(null != returnorderdetail1.getState() && returnorderdetail1.getState().trim().length()>0){
				detachedCriteria.add(Restrictions.like("state", returnorderdetail1.getState(), MatchMode.ANYWHERE));
			}

		}
		return detachedCriteria;
	}

}
