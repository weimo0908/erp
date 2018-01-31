package cn.kumiaojie.erp.dao.impl;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import cn.kumiaojie.erp.dao.IBaseDao;

@SuppressWarnings("unchecked")
public class BaseDao<T> extends HibernateDaoSupport implements IBaseDao<T> {
	
	// 引入泛型初阶类型
	private Class<T> entityClass;

	public BaseDao() {
		// 获取对象对应的父类类型
		Type baseDaoClass = this.getClass().getGenericSuperclass();
		// 转成带参数,即泛型的类型
		ParameterizedType pt = (ParameterizedType) baseDaoClass;
		// 获取其参数泛型的泛型数组
		Type[] types = pt.getActualTypeArguments();
		// 获取第一个泛型类型
		entityClass = (Class<T>) types[0];
	}

	/**
	 * 查询列表
	 */
	@Override
	public List<T> getList(T t1, T t2, Object param) {
		DetachedCriteria detachedCriteria = getDetachedCriteria(t1, t2, param);
		return (List<T>) this.getHibernateTemplate().findByCriteria(detachedCriteria);
	}

	/**
	 * 分页查询
	 */
	@Override
	public List<T> getListByPage(T t1, T t2, Object param, int firstResult, int maxResults) {
		DetachedCriteria detachedCriteria = getDetachedCriteria(t1, t2, param);
		return (List<T>) this.getHibernateTemplate().findByCriteria(detachedCriteria, firstResult, maxResults);
	}

	/**
	 * 查询总记录数
	 */
	@Override
	public long getCount(T t1, T t2, Object param) {
		DetachedCriteria detachedCriteria = getDetachedCriteria(t1, t2, param);
		detachedCriteria.setProjection(Projections.rowCount());
		List<Long> count = (List<Long>) this.getHibernateTemplate().findByCriteria(detachedCriteria);
		return count.get(0);
		
	}

	/**
	 * 添加对象
	 */
	@Override
	public void add(T t) {
		this.getHibernateTemplate().save(t);
	}

	/**
	 * 更新对象
	 */
	@Override
	public void update(T t) {
		this.getHibernateTemplate().update(t);
	}

	/**
	 * 查询单个对象
	 */
	@Override
	public T get(Long uuid) {
		return this.getHibernateTemplate().get(entityClass, uuid);
	}
	
	/**
	 * 查询单个对象
	 */
	@Override
	public T get(String uuid) {
		return this.getHibernateTemplate().get(entityClass, uuid);
	}
	
	

	/**
	 * 删除对象
	 */
	@Override
	public void delete(Long uuid) {
		// 先查询,变成持久化
		T t = this.getHibernateTemplate().get(entityClass, uuid);
		// 删除
		this.getHibernateTemplate().delete(t);
	}

	/**
	 * 离线查询条件,由具体实现类进行重写
	 * @param t1
	 * @param t2
	 * @param param
	 * @return
	 */
	public DetachedCriteria getDetachedCriteria(T t1, T t2, Object param) {
		return null;
	}
	
	/**
	 * orderBy 根据传入字段排序后再分页查询
	 * @param t 实体类
	 * @param curPage 当前页
	 * @param maxRows 每页显示数量
	 * @param orderBy 根据传入字段排序
	 */
	@Override
	public List<T> searchWithPaging(T t1, T t2,Integer curPage, Integer maxRows,String orderBy) {
		DetachedCriteria criteria = this.getDetachedCriteria(t1,t2,null);
		//排序
		criteria.addOrder(Order.asc(orderBy));
		return (List<T>) this.getHibernateTemplate().findByCriteria(criteria, (curPage-1)*maxRows, maxRows);
	}
}
