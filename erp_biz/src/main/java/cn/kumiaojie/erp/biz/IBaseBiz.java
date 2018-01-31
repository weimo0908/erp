package cn.kumiaojie.erp.biz;

import java.util.List;

public interface IBaseBiz<T> {

	/**
	 * 查询列表
	 * 
	 * @param t1
	 * @param t2
	 * @param param
	 * @return
	 */
	public List<T> getList(T t1, T t2, Object param);

	/**
	 * 分页查询
	 * 
	 * @param t1
	 * @param t2
	 * @param param
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	List<T> getListByPage(T t1, T t2, Object param, int firstResult, int maxResults);

	/**
	 * 查询总记录
	 * 
	 * @param t1
	 * @param t2
	 * @param param
	 * @return
	 */
	long getCount(T t1, T t2, Object param);

	/**
	 * 添加对象
	 * 
	 * @param t
	 */
	public void add(T t);

	/**
	 * 删除对象
	 * 
	 * @param uuid
	 */
	public void delete(Long uuid);

	/**
	 * 根据条件查询对象
	 * 
	 * @param uuid
	 * @return
	 */
	public T get(Long uuid);
	
	
	
	/**
	 * 
	 * @param uuid
	 * @return
	 */
	public T get(String uuid);

	/**
	 * 更新对象
	 * 
	 * @param t
	 * @throws Exception 
	 */
	public void update(T t) throws Exception;

	/**
	 * orderBy 根据传入字段排序后再分页查询
	 * @param t 实体类
	 * @param curPage 当前页
	 * @param maxRows 每页显示数量
	 * @param orderBy 根据传入字段排序
	 */
	List<T> searchWithPaging(T t1, T t2, Integer curPage, Integer maxRows, String orderBy);
	
	

}
