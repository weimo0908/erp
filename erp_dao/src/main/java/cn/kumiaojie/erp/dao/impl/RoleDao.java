package cn.kumiaojie.erp.dao.impl;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IRoleDao;
import cn.kumiaojie.erp.entity.Role;

/**
 * 角色数据访问实现类 
 * @author Stivechen
 *
 */
public class RoleDao extends BaseDao<Role> implements IRoleDao {


	/**
	 * 离线查询条件
	 * @param role1
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Role role1,Role role2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Role.class);
		// 模糊查询(判断role1是否为空,不然进行后续步骤会报错)
		if(role1!=null){
			if(null != role1.getName() && role1.getName().trim().length()>0){
				detachedCriteria.add(Restrictions.like("name", role1.getName(), MatchMode.ANYWHERE));
			}

		}
		return detachedCriteria;
	}

	
	
}
