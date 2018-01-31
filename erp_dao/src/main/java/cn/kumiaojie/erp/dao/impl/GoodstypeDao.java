package cn.kumiaojie.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IGoodstypeDao;
import cn.kumiaojie.erp.entity.Goodstype;

/**
 * 商品数据访问实现类 
 * @author Stivechen
 *
 */
public class GoodstypeDao extends BaseDao<Goodstype> implements IGoodstypeDao {


	/**
	 * 离线查询条件
	 * @param goodstype1
	 * @param goodstype2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Goodstype goodstype1,Goodstype goodstype2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Goodstype.class);
		// 模糊查询(判断goodstype1是否为空,不然进行后续步骤会报错)
		if (null != goodstype1) {
			// 根据名字查询
			if (goodstype1.getName() != null && goodstype1.getName().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("name", goodstype1.getName(), MatchMode.ANYWHERE));
			}
		}
		//根据名称精确查询
		if (null != goodstype2) {
			if (null != goodstype2.getName() && !"".equals(goodstype2.getName())) {
				detachedCriteria.add(Restrictions.eq("name", goodstype2.getName()));
			}
		}
		return detachedCriteria;
	}

}
