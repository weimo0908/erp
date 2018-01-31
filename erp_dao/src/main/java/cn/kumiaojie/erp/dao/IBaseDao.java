package cn.kumiaojie.erp.dao;

import java.util.List;


/**
 * BaseDao接口
 * @param <T>
 */
public interface IBaseDao<T> {

	
	/**添加
	 * @param t
	 */
	public void add(T t);
	
	/**通过编号获取单个对象
	 * @param uuid
	 * @return
	 */
	public T get(Long uuid);
	
	
	/**
	 * 通过String类型的编号返回对象
	 * 方法重载
	 * @param uuid
	 * @return
	 */
	public T get(String uuid);
	
	/**更新对象
	 * @param t
	 */
	public void update(T t);
	
	/**删除对象
	 * @param uuid
	 */
	public void delete(Long uuid);

	/**
	 * 获取对象列表
	 * @param t1
	 * @param t2
	 * @param param
	 * @return
	 */
	public List<T> getList(T t1,T t2, Object param);
	
	/**
	 * 按分页获取对象
	 * @param t1
	 * @param t2
	 * @param param
	 * @param firstResult
	 * @param maxResults
	 * @return
	 */
	public List<T> getListByPage(T t1,T t2, Object param,int firstResult,int maxResults) ;
	
	/**
	 * 获取对象总记录数
	 * @param t1
	 * @param t2
	 * @param param
	 * @return
	 */
	public long getCount(T t1 ,T t2, Object param);

	/**
	 * orderBy 根据传入字段排序后再分页查询
	 * @param t 实体类
	 * @param curPage 当前页
	 * @param maxRows 每页显示数量
	 * @param orderBy 根据传入字段排序
	 */
	List<T> searchWithPaging(T t1, T t2, Integer curPage, Integer maxRows, String orderBy);

}
