package cn.kumiaojie.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IGoodsDao;
import cn.kumiaojie.erp.entity.Goods;

/**
 * 商品数据访问实现类 
 * @author Stivechen
 *
 */
public class GoodsDao extends BaseDao<Goods> implements IGoodsDao {


	/**
	 * 离线查询条件
	 * @param goods1
	 * @param goods2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Goods goods1,Goods goods2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Goods.class);
		// 模糊查询(判断goods1是否为空,不然进行后续步骤会报错)
		if (null != goods1) {
			// 根据名字查询
			if (goods1.getName() != null && goods1.getName().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("name", goods1.getName(), MatchMode.ANYWHERE));
			}
			// 根据产地查询
			if (goods1.getOrigin()!= null && goods1.getOrigin().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("origin", goods1.getOrigin(), MatchMode.ANYWHERE));
			}
			// 根据厂家查询
			if (goods1.getProducer()!= null && goods1.getProducer().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("origin", goods1.getProducer(), MatchMode.ANYWHERE));
			}
			// 根据计量单位查询
			if (goods1.getUnit()!= null && goods1.getUnit().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("unit", goods1.getUnit(), MatchMode.ANYWHERE));
			}
			// 根据商品类型查询
			if (goods1.getGoodstype()!= null && goods1.getGoodstype().getUuid() != null) {
				detachedCriteria.add(Restrictions.eq("goodstype", goods1.getGoodstype()));
			}
			// 根据商品进价查询
			if (goods1.getInprice()!= null) {
				detachedCriteria.add(Restrictions.ge("inprice", goods1.getInprice()));
			}
			// 根据商品售价查询
			if (goods1.getOutprice()!= null) {
				detachedCriteria.add(Restrictions.ge("outprice", goods1.getOutprice()));
			}
			
		}
		//第二个条件
		if (null != goods2) {
			//小于进价
			if (null != goods2.getInprice() ) {
				detachedCriteria.add(Restrictions.le("inprice", goods2.getInprice()));
			}
			//小于售价
			if (null != goods2.getOutprice()) {
				detachedCriteria.add(Restrictions.le("outprice", goods2.getOutprice()));
			}
			//根据名称精确查询
			if (null != goods2.getName() && !"".equals(goods2.getName())) {
				detachedCriteria.add(Restrictions.eq("name", goods2.getName()));
			}
			//根据厂家精确查询
			if (null != goods2.getProducer() && !"".equals(goods2.getProducer())) {
				detachedCriteria.add(Restrictions.eq("producer", goods2.getProducer()));
			}
		}
		return detachedCriteria;
	}

}
