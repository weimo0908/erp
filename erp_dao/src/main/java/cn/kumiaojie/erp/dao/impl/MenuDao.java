package cn.kumiaojie.erp.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import cn.kumiaojie.erp.dao.IMenuDao;
import cn.kumiaojie.erp.entity.Menu;

/**
 * 部门数据访问实现类 
 * @author Stivechen
 *
 */
@SuppressWarnings("unchecked")
public class MenuDao extends BaseDao<Menu> implements IMenuDao {


	/**
	 * 离线查询条件
	 * @param menu1
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Menu menu1,Menu menu2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Menu.class);
		return detachedCriteria;
	}
	
	/**
	 * 获取用户权限菜单
	 */
	@Override
	public List<Menu> getMenuByEmpuuid(Long uuid) {
		//五张表进行关联查询,用Hibernate的HQL查询语句
		String hql ="select m from Emp e join e.roles r join r.menus m where e.uuid=?";
		return (List<Menu>) this.getHibernateTemplate().find(hql, uuid);
	}

}
