package cn.kumiaojie.erp.biz.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kumiaojie.erp.biz.IReturnordersBiz;
import cn.kumiaojie.erp.dao.IReturnordersDao;
import cn.kumiaojie.erp.dao.impl.EmpDao;
import cn.kumiaojie.erp.dao.impl.OrdersDao;
import cn.kumiaojie.erp.dao.impl.SupplierDao;
import cn.kumiaojie.erp.entity.Orderdetail;
import cn.kumiaojie.erp.entity.Orders;
import cn.kumiaojie.erp.entity.Returnorders;
/**
 * 退货订单业务逻辑类
 * @author Administrator
 *
 */
public class ReturnordersBiz extends BaseBiz<Returnorders> implements IReturnordersBiz {
	private IReturnordersDao returnordersDao;
	private OrdersDao ordersDao;
	private EmpDao empDao;
	private SupplierDao supplierDao;
	
	
	



	public void setOrdersDao(OrdersDao ordersDao) {
		this.ordersDao = ordersDao;
	}


	public void setEmpDao(EmpDao empDao) {
		this.empDao = empDao;
	}


	public void setSupplierDao(SupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}


	public void setReturnordersDao(IReturnordersDao returnordersDao) {
		this.returnordersDao = returnordersDao;
		super.setBaseDao(returnordersDao);
	}


	@Override
	public List<Orderdetail> getInReturnOrderdetail(Long uuid) {
		Orders orders = new Orders();
		orders.setUuid(uuid);
		
		List<Orders> list = ordersDao.getList(orders,null,null);
		
		List<Orderdetail> orderdetailList = new ArrayList<Orderdetail>();
		
		if(list.size() > 0){
			for ( Orderdetail o: list.get(0).getOrderDetails()) {
				orderdetailList.add(o);
			}
			return orderdetailList;
		}
		return null;
	}
	
	
	@Override
	public List<Orderdetail> getOutReturnOrderdetail(Long uuid) {
		//添加对象构建条件
		Orders orders = new Orders();
		orders.setUuid(uuid);
		List<Orders> list = ordersDao.getList(orders,null,null);
		//创建一个新对象
		List<Orderdetail> orderdetailList = new ArrayList<Orderdetail>();
		//获取查询订单对象
		if(list.size() > 0){
			for ( Orderdetail o: list.get(0).getOrderDetails()) {
				orderdetailList.add(o);
			}
			return orderdetailList;
		}
		return null;
	}
	
	
	
	
	/**
	 * 添加采购退货订单
	 */
	@Override
	public void addIn(Returnorders t,Long uuid) {
		//设置属性
		t.setCreater(uuid);
		t.setCreatetime(new Date());
		t.setType(Returnorders.TYPE_IN);
		t.setState(Returnorders.STATE_UNCHECK);
		//持久化状态
		returnordersDao.add(t);
	}
	
	/**
	 * 添加销售退货订单
	 */
	public void addOut(Returnorders t,Long uuid) {
		//设置属性
		t.setCreater(uuid);
		t.setCreatetime(new Date());
		t.setType(Returnorders.TYPE_OUT);
		t.setState(Returnorders.STATE_UNCHECK);
		//持久对对象
		returnordersDao.add(t);
	}
	
	@Override
	public void doCheck(Long id,Long empUuid) {
		//获取该订单
		Returnorders returnorders = returnordersDao.get(id);
		//设置条件
		returnorders.setChecker(empUuid);
		returnorders.setChecktime(new Date());
		returnorders.setState(Returnorders.STATE_CHECKED);
		returnordersDao.update(returnorders);
	}
	/**
	 * 分页查询功能
	 */
	@Override
	public List<Returnorders> getListByPage(Returnorders t1, Returnorders t2, Object param, int firstResult, int rows) {
		//获取订单分页结果(先拿出来,将查询的结果做一个缓存,提高效率)
				List<Returnorders> returnordersList = super.getListByPage(t1, t2, param, firstResult, rows);
				//创建一个集合来装载员工名称,当做缓存
				Map<Long,String> empNameMap = new HashMap<Long,String>();
				//创建一个集合来装在供应商名称,当做缓存
				Map<Long,String> supplierNameMap = new HashMap<Long,String>();
				//遍历订单
				for (Returnorders o : returnordersList) {
					//设置订单创建人
					o.setCretaeName(getEmpName(o.getCreater(), empNameMap));
					//设置订单审核人
					o.setCheckName(getEmpName(o.getChecker(), empNameMap));
					//设置订单出库人
					o.setEndtName(getEmpName(o.getEnder(), empNameMap));
					//设置供应商名称
					o.setSupplierName(getSupplierName(o.getSupplieruuid(), supplierNameMap));
				}
				return returnordersList;
	}
	/**
	 * 获取用户名称
	 * @param uuid
	 * @param empNameMap
	 * @return
	 */
	private String getEmpName(Long uuid,Map<Long,String> empNameMap){
		//判断uuid是否为空
		if(uuid == null){
			return null;
		}
		//若存在该uuid 的话就获取该员工名称
		String empName = empNameMap.get(uuid);
		//判断集合看其中是否存在该员工名称
		if(empName == null){
			//若位空则代表没有该员工名称的缓存应该添加进去
			//利用empDao查询到该uuid对应的员工名称
			empName = empDao.get(uuid).getName();
			//添加进缓存集合中
			empNameMap.put(uuid, empName);
		}
		return empName;
	}
	
	/**
	 * 获取供应商名称
	 * @param uuid
	 * @param supplierNameMap
	 * @return
	 */
	private String getSupplierName(Long uuid,Map<Long,String> supplierNameMap){
		//判断uuid是否为空
		if(uuid == null){
			return null;
		}
		//不为空则根据uuid获取供应商名称
		String supplierName = supplierNameMap.get(uuid);
		//判断该名称是否存在
		if(supplierName == null){
			//不存在则放置如缓存中
			 supplierName = supplierDao.get(uuid).getName();
			 //将该名称放置如集合中
			 supplierNameMap.put(uuid, supplierName);
		}
		//将该名称返回
		return supplierName;
	}
}
