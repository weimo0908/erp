package cn.kumiaojie.erp.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IStoredetailDao;
import cn.kumiaojie.erp.entity.StoreAlert;
import cn.kumiaojie.erp.entity.Storedetail;

/**
 * 商品数据访问实现类 
 * @author Stivechen
 *
 */
@SuppressWarnings("unchecked")
public class StoredetailDao extends BaseDao<Storedetail> implements IStoredetailDao {


	/**
	 * 离线查询条件
	 * @param storedetail1
	 * @param storedetail2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Storedetail storedetail1,Storedetail storedetail2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Storedetail.class);
		// 模糊查询(判断storedetail1是否为空,不然进行后续步骤会报错)
		if(storedetail1!=null){
			//根据商品编号查询
			if(null != storedetail1.getGoodsuuid()){
				detachedCriteria.add(Restrictions.eq("goodsuuid", storedetail1.getGoodsuuid()));
			}
			//根据仓库编号查询
			if(null != storedetail1.getStoreuuid()){
				detachedCriteria.add(Restrictions.eq("storeuuid", storedetail1.getStoreuuid()));
			}
		}
		return detachedCriteria;
	}

	/**
	 * 获取库存预警列表
	 */
	@Override
	public List<StoreAlert> getStroealertList() {
		return (List<StoreAlert>) getHibernateTemplate().find("from StoreAlert where storenum<outnum");
	}

}
