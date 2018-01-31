package cn.kumiaojie.erp.biz.impl;

import java.util.List;
import java.util.Map;

import cn.kumiaojie.erp.biz.IBaseBiz;
import cn.kumiaojie.erp.dao.IBaseDao;
import cn.kumiaojie.erp.dao.IStoreDao;

public class BaseBiz<T> implements IBaseBiz<T> {

	// 数据访问层注入
	private IBaseDao<T> baseDao;

	public void setBaseDao(IBaseDao<T> baseDao) {
		this.baseDao = baseDao;
	}

	/*
	 * 分页查询
	 */
	@Override
	public List<T> getListByPage(T t1, T t2, Object param, int firstResult, int maxResults) {
		return baseDao.getListByPage(t1, t2, param, firstResult, maxResults);
	}

	/**
	 * 查询总记录数
	 */
	@Override
	public long getCount(T t1, T t2, Object param) {
		return baseDao.getCount(t1, t2, param);
	}

	/**
	 * 添加对象
	 */
	@Override
	public void add(T t) {
		baseDao.add(t);
	}

	/**
	 * 删除对象
	 */
	@Override
	public void delete(Long uuid) {
		baseDao.delete(uuid);
	}

	/**
	 * 根据条件获取对象
	 */
	@Override
	public T get(Long uuid) {
		return baseDao.get(uuid);
	}
	
	@Override
	public T get(String uuid) {
		return baseDao.get(uuid);
	}

	
	/* 
	 * 更新对象
	 */
	@Override
	public void update(T t) throws Exception{
		baseDao.update(t);
	}

	/**
	 * 列表查询对象
	 */
	@Override
	public List<T> getList(T t1, T t2, Object param) {
		return baseDao.getList(t1, t2, param);
	}

	/**
	 * 分页，t,t2可以作为范围查询
	 */
	@Override
	public List<T> searchWithPaging(T t1,T t2, Integer curPage, Integer maxRows,String orderBy) {
		return baseDao.searchWithPaging(t1,t2, curPage, maxRows,orderBy);
	}
}
