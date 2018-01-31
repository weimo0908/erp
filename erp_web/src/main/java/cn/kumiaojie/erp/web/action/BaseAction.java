package cn.kumiaojie.erp.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

import cn.kumiaojie.erp.biz.IBaseBiz;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;

/**
 * Action的通用层
 * @author Stivechen
 * 抽取了一些通用的方法到BaseActionUtil中
 * @param <T>
 */
public class BaseAction<T> {

	// 注入BaseBiz
	private IBaseBiz<T> baseBiz;

	public void setBaseBiz(IBaseBiz<T> baseBiz) {
		this.baseBiz = baseBiz;
	}

	/* ===============属性驱动=============== */

	// 属性驱动条件查询
	private T t1;
	private T t2;
	private Object param;

	public T getT1() {
		return t1;
	}

	public void setT1(T t1) {
		this.t1 = t1;
	}

	public T getT2() {
		return t2;
	}

	public void setT2(T t2) {
		this.t2 = t2;
	}

	public Object getParam() {
		return param;
	}

	public void setParam(Object param) {
		this.param = param;
	}

	// 分页查询
	private int page;// 当前页码
	private int rows;// 每页显示记录数

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	// 添加/修改
	private T t;

	public T getT() {
		return t;
	}

	public void setT(T t) {
		this.t = t;
	}

	// 根据id查询/删除
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/* ===============属性驱动=============== */

	/**
	 * 分页查询
	 */
	public void getListByPage() {
		// 计算起始页数
		int firstResult = (page - 1) * rows;
		List<T> list = baseBiz.getListByPage(t1, t2, param, firstResult, rows);
		// 封装总记录数
		long count = baseBiz.getCount(t1, t2, param);
		// 创建map集合封装数据
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("total", count);
		map.put("rows", list);
		// 禁用循环引用保护DisableCircularReferenceDetect
		//WriteMapNullValue:在map中空字段以null形式写出(也可以在前端处理)
		String jsonString = JSON.toJSONString(map,SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect);
		BaseActionUtil.write(jsonString);
	}

	/**
	 * 条件查询
	 */
	public void list() {
		List<T> list = baseBiz.getList(t1, t2, param);
		// 去保护
		String jsonString = JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect);
		BaseActionUtil.write(jsonString);
	}

	/**
	 * 新增
	 */
	public void add() {
		try {
			// 添加成功
			baseBiz.add(t);
			BaseActionUtil.returnAjax(true, "添加成功!");
		} catch (Exception e) {
			// 添加失败
			e.printStackTrace();
			BaseActionUtil.returnAjax(false, "添加失败!");
		}
	}

	/**
	 * 删除
	 */
	public void delete() {
		try {
			// 删除成功
			baseBiz.delete(id);
			BaseActionUtil.returnAjax(true, "删除成功!");
		} catch (Exception e) {
			// 删除失败
			e.printStackTrace();
			BaseActionUtil.returnAjax(false, "删除失败!");
		}
	}

	/**
	 * 查询单个对象
	 */
	public void get() {
		T t = baseBiz.get(id);
		// 转换为json.需要转换格式,--->t.name
		// 也要进行日期转换
		String jsonString = JSON.toJSONStringWithDateFormat(t, "yyyy-MM-dd");
		// 调用写出方法,进行写出
		BaseActionUtil.write(BaseActionUtil.mapJson(jsonString, "t"));

	}

	/**
	 * 更新
	 */
	public void update() {
		try {
			// 更新成功
			baseBiz.update(t);
			BaseActionUtil.returnAjax(true, "更新成功!");
		} catch (Exception e) {
			e.printStackTrace();
			BaseActionUtil.returnAjax(false, "更新失败!");
		}
	}

	
}
