package cn.kumiaojie.erp.dao.impl;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import cn.kumiaojie.erp.dao.IEmpDao;
import cn.kumiaojie.erp.entity.Emp;

/**
 * 员工数据访问实现类 
 * @author Stivechen
 *
 */
@SuppressWarnings("unchecked")
public class EmpDao extends BaseDao<Emp> implements IEmpDao {

	
	/**
	 * 离线查询条件
	 * @param emp1
	 * @param emp2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(Emp emp1,Emp emp2 , Object param) {
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(Emp.class);
		// 模糊查询(判断emp1是否为空,不然进行后续步骤会报错)
		if (null != emp1) {
			//根据登录名查询
			if (emp1.getUsername() !=null && emp1.getUsername().trim().length()>0) {
				detachedCriteria.add(Restrictions.like("username", emp1.getUsername(), MatchMode.ANYWHERE));
			}
			// 根据名字查询
			if (emp1.getName() != null && emp1.getName().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("name", emp1.getName(), MatchMode.ANYWHERE));
			}
			// 根据性别查询
			if (emp1.getGender()!= null) {
				detachedCriteria.add(Restrictions.eq("gender", emp1.getGender()));
			}
			// 根据邮箱查询
			if (emp1.getEmail()!= null && emp1.getEmail().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("email", emp1.getEmail(), MatchMode.ANYWHERE));
			}
			// 根据电话查询
			if (emp1.getTele()!= null && emp1.getTele().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("tele", emp1.getTele(), MatchMode.ANYWHERE));
			}
			// 根据联系地址查询
			if (emp1.getAddress()!= null && emp1.getAddress().trim().length() > 0) {
				detachedCriteria.add(Restrictions.like("address", emp1.getAddress(),MatchMode.ANYWHERE));
			}
			// 根据部门查询
			if (emp1.getDep()!= null && null !=emp1.getDep().getUuid()) {
				detachedCriteria.add(Restrictions.eq("dep", emp1.getDep()));
			}
			//起始日期查询
			if (emp1.getBirthday() !=null) {
				detachedCriteria.add(Restrictions.ge("birthday", emp1.getBirthday()));
			}
			
		}
		//第二个条件
		if (null != emp2) {
			if (emp2.getBirthday() != null) {
				detachedCriteria.add(Restrictions.le("birthday", emp2.getBirthday()));
			}
			if(null != emp2.getName() && emp2.getName().trim().length()>0){
				detachedCriteria.add(Restrictions.eq("name", emp2.getName()));
			}
		}
		
		return detachedCriteria;
	}

	@Override
	public Emp findByUsernameAndPwd(String username, String pwd) {
		String hql="from Emp where username=? and pwd =?";
		List<Emp> list = (List<Emp>) this.getHibernateTemplate().find(hql, username,pwd);
		if (list.size()>0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 修改密码
	 */
	@Override
	public void updatePwd(Long uuid, String newPwd) {
		String hql = "update Emp set pwd =? where uuid=? ";
		this.getHibernateTemplate().bulkUpdate(hql, newPwd,uuid);
	}

}
