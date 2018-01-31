package cn.kumiaojie.erp.web.action;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;

import cn.kumiaojie.erp.biz.IOrdersBiz;
import cn.kumiaojie.erp.biz.IReturnorderdetailBiz;
import cn.kumiaojie.erp.biz.IReturnordersBiz;
import cn.kumiaojie.erp.biz.IStoredetailBiz;
import cn.kumiaojie.erp.dao.impl.OrdersDao;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Orderdetail;
import cn.kumiaojie.erp.entity.Orders;
import cn.kumiaojie.erp.entity.Returnorderdetail;
import cn.kumiaojie.erp.entity.Returnorders;
import cn.kumiaojie.erp.entity.Storedetail;
import cn.kumiaojie.erp.exception.ERPException;

/**
 * 退货订单Action 
 * @author Administrator
 *
 */
public class ReturnordersAction extends BaseAction<Returnorders> {

	private IReturnordersBiz returnordersBiz;
	private IOrdersBiz ordersBiz;
	private IReturnorderdetailBiz returnorderdetailBiz;
	private IStoredetailBiz storedetailBiz;
	

	
	public void setOrdersBiz(IOrdersBiz ordersBiz) {
		this.ordersBiz = ordersBiz;
	}

	public void setReturnorderdetailBiz(IReturnorderdetailBiz returnorderdetailBiz) {
		this.returnorderdetailBiz = returnorderdetailBiz;
	}

	public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
		this.storedetailBiz = storedetailBiz;
	}
	private String  json;//获取前端传来的json格式数据
	
	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}


	public void setReturnordersBiz(IReturnordersBiz returnordersBiz) {
		this.returnordersBiz = returnordersBiz;
		super.setBaseBiz(returnordersBiz);
	}
	
	/**
	 * 根据供应商编号获取相关联的orders
	 */
	public void getInReturnOrders(){
		Orders orders = new Orders();
		orders.setSupplieruuid(getId());
		orders.setState(Orders.STATE_END);
		orders.setType(Orders.TYPE_IN);
		try {
			List<Orders> orderList = ordersBiz.getList(orders,null,null);
			write(JSON.toJSONString(orderList));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	/**
	 * 根据供应商编号获取相关联的orders
	 */
	
	public void getOutReturnOrders(){
		Orders orders = new Orders();
		
		orders.setSupplieruuid(getId());
		orders.setState(Orders.STATE_OUT);
		orders.setType(Orders.TYPE_OUT);
		
		try {
			List<Orders> listByPage = ordersBiz.getList(orders,null,null);
			write(JSON.toJSONString(listByPage));
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
		
	/**
	 * 根据供应商编号获取对应的所有采购并且已经入库的订单
	 */
	public void getInReturnOrderdetail(){
		try {
			List<Orderdetail> returnOrderdetail = returnordersBiz.getInReturnOrderdetail(getId());
			write(JSON.toJSONString(returnOrderdetail));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getOutReturnOrderdetail(){
		try {
			List<Orderdetail> returnOrderdetail = returnordersBiz.getOutReturnOrderdetail(getId());
			write(JSON.toJSONString(returnOrderdetail));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 添加采购退货订单
	 */
	public void addOut() {
		Emp loginUser = getLoginUser();
		if(loginUser == null){
			write(ajaxReturn(false, "亲,您还没有登录!"));
		}
		List<Returnorderdetail> rorderdetailList = JSON.parseArray(json,Returnorderdetail.class);
		//拿到suppileruuid, 其关联的原订单的id ,type
		Returnorders returnorders = getT();
		
		returnorders.setReturnorderdetails(rorderdetailList);

		
		Double money= 0.0;
		for (Returnorderdetail r : rorderdetailList) {
			money = money + r.getReturnMoney();
			try {
				r.setMoney(r.getReturnMoney());
				returnorderdetailBiz.add(r);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		returnorders.setTotalmoney(money);
		try {
			returnordersBiz.addOut(returnorders,loginUser.getUuid());
			write(ajaxReturn(true, "添加成功!"));
		} catch (Exception e) {
			write(ajaxReturn(false, "添加失败!"));
			e.printStackTrace();
		}
	
	
	}
	
	
	public void addIn() {
		Emp loginUser = getLoginUser();
		if(loginUser == null){
			write(ajaxReturn(false, "亲,您还没有登录!"));
		}
		List<Returnorderdetail> rorderdetailList = JSON.parseArray(json,Returnorderdetail.class);
		//拿到suppileruuid, 其关联的原订单的id ,type
		Returnorders returnorders = getT();
		
		returnorders.setReturnorderdetails(rorderdetailList);

		
		Double money= 0.0;
		for (Returnorderdetail r : rorderdetailList) {
			money = money + r.getReturnMoney();
			try {
				r.setMoney(r.getReturnMoney());
				returnorderdetailBiz.add(r);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		returnorders.setTotalmoney(money);
		try {
			returnordersBiz.addIn(returnorders,loginUser.getUuid());
			write(ajaxReturn(true, "添加成功!"));
		} catch (Exception e) {
			write(ajaxReturn(false, "添加失败!"));
			e.printStackTrace();
		}
	
	
	}
	/**
	 * 审核退货订单
	 */
	public void doCheck(){
		Emp loginUser = getLoginUser();
		try {
			returnordersBiz.doCheck(getId(),loginUser.getUuid());
			write(ajaxReturn(true, "审核成功!"));
		} catch (Exception e) {
			write(ajaxReturn(false, "审核失败!"));
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 退货订单出库
	 */
	public void doOutStore(){
		Emp loginUser = getLoginUser();
		Returnorderdetail returnorderdetail = returnorderdetailBiz.get(getId());
		Long goodsuuid = returnorderdetail.getGoodsuuid();
		Long storeuuid = returnorderdetail.getStoreuuid();
		Storedetail storedetail = new Storedetail();
		storedetail.setGoodsuuid(goodsuuid);
		storedetail.setStoreuuid(returnorderdetail.getStoreuuid());
		List<Storedetail> storedetailList = storedetailBiz.getList(storedetail, null,null);
		//订单入库改变仓库数量(减少),并且判断是否等于0
		try {
			if(storedetailList.size() > 0){
				Long storeNum =storedetailList.get(0).getNum();
				if(storeNum == 0){
					throw new ERPException("商品库存不足!");
				}
				Long returnNum = returnorderdetail.getNum();
				storedetail.setNum(storeNum - returnNum ); 
			}
			returnorderdetailBiz.doOutStore(returnorderdetail, loginUser.getUuid());
			write(ajaxReturn(true, "出库成功!"));
		}catch(ERPException e){
			write(ajaxReturn(false,e.getMessage()));
		} 
		catch (Exception e) {
			write(ajaxReturn(false, "出库失败!"));
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 销售订单入库
	 */
	public void doInStore(){
		Emp loginUser = getLoginUser();
		Returnorderdetail returnorderdetail = returnorderdetailBiz.get(getId());
		Long goodsuuid = returnorderdetail.getGoodsuuid();
		Storedetail storedetail = new Storedetail();
		storedetail.setGoodsuuid(goodsuuid);
		List<Storedetail> storedetailList = storedetailBiz.getList(storedetail, null,null);
		//订单入库改变仓库数量(添加)
		if(storedetailList.size() > 0){
			storedetail =storedetailList.get(0);
			Long storeNum = storedetailList.get(0).getNum();
			Long returnNum = returnorderdetail.getNum();
			storedetail.setNum(storeNum + returnNum ); 
		}
		try {
			returnorderdetailBiz.doInStore(returnorderdetail, loginUser.getUuid());
			write(ajaxReturn(true, "入库成功!"));
		} catch (Exception e) {
			write(ajaxReturn(false, "入库失败!"));
			e.printStackTrace();
		}
		
	}
	/**
	 * 抽取一个ajax返回添加信息的方法
	 * @param b
	 * @param string
	 * @return
	 */
	public  String ajaxReturn(boolean flag, String message) {
		Map map = new HashMap();
		map.put("success",flag);
		map.put("message",message);
		String jsonString = JSON.toJSONString(map);
		return jsonString;
	}


	/**
	 * 
	 * 抽取一个json对象响应到页面的方法
	 * @param jsonString
	 */
	public  void write(String jsonString) {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		try {
			//将数据发送到页面
			response.getWriter().print(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取session中的登录对象
	 * @return
	 */
	public Emp getLoginUser(){
		return (Emp) SecurityUtils.getSubject().getPrincipal();
	}
	
	
	

}
