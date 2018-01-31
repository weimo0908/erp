package cn.kumiaojie.erp.web.action;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.redsun.bos.ws.Waybilldetail;

import cn.kumiaojie.erp.biz.IOrdersBiz;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Orderdetail;
import cn.kumiaojie.erp.entity.Orders;
import cn.kumiaojie.erp.entity.Returnorderdetail;
import cn.kumiaojie.erp.entity.Returnorders;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;
import cn.kumiaojie.erp.web.utils.GetLoginUser;

/**
 * 商品Action
 * 
 * @author Stivechen
 *
 */
public class OrdersAction extends BaseAction<Orders> {

	private IOrdersBiz ordersBiz;

	public void setOrdersBiz(IOrdersBiz ordersBiz) {
		this.ordersBiz = ordersBiz;
		super.setBaseBiz(this.ordersBiz);
	}

	// 订单提交属性
	private String json;

	public String getJson() {
		return json;
	}

	public void setJson(String json) {
		this.json = json;
	}

	/**
	 * 重写add方法
	 */
	public void add() {
		// 校验是否登录,uuid从登录session中获取
		Emp existUser = (Emp) GetLoginUser.getLoginUser();
		if (null == existUser) {
			BaseActionUtil.returnAjax(false, "对不起,您还没登录,请登录再操作");
			return;
		}
		try {
			// 获取提交的订单
			Orders orders = getT();
			// 设置订单创建者
			orders.setCreater(existUser.getUuid());
			// 获取订单明细
			List<Orderdetail> list = JSON.parseArray(json, Orderdetail.class);
			// 设置订单明细
			orders.setOrderDetails(list);
			// 保存订单
			ordersBiz.add(orders);
			BaseActionUtil.returnAjax(true, "订单添加成功!");
		} catch (Exception e) {
			e.printStackTrace();
			BaseActionUtil.returnAjax(false, "订单添加失败!");
		}

	}

	/**
	 * 审查流程(页面所在的订单uuid,登录的用户empUuid)
	 */
	public void doCheck() {
		// 确认审查员是否登录
		Emp checker = (Emp) GetLoginUser.getLoginUser();
		if (null == checker) {
			BaseActionUtil.returnAjax(false, "对不起,您还没登录,请登录再操作");
			return;
		}
		try {
			// 调用方法向biz传递
			ordersBiz.doCheck(this.getId(), checker.getUuid());
			BaseActionUtil.returnAjax(true, "审查成功!");
		} catch (ERPException e1) {
			BaseActionUtil.returnAjax(false, e1.getMessage());
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "审查失败!");
		}
	}

	/**
	 * 确认流程(页面所在的订单uuid,登录的用户empuuid)
	 */
	public void doStart() {
		// 核对确认员是否登录
		Emp starter = (Emp) GetLoginUser.getLoginUser();
		if (null == starter) {
			BaseActionUtil.returnAjax(false, "对不起,您还没登录,请登录再操作");
			return;
		}
		try {
			ordersBiz.doStart(this.getId(), starter.getUuid());
			BaseActionUtil.returnAjax(true, "确认成功!");
		} catch (ERPException e1) {
			BaseActionUtil.returnAjax(false, e1.getMessage());
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "确认失败!");
		}
	}

	/**
	 * 我的采购订单(让页面的url选择本方法)
	 */
	public void myListByPage() {
		// 查看查询条件是否为空,若为空则新创建查询条件
		if (null == getT1()) {
			this.setT1(new Orders());
		}
		// 获取正在登录的用户
		Emp loginUser = (Emp) GetLoginUser.getLoginUser();
		this.getT1().setCreater(loginUser.getUuid());
		super.getListByPage();
	}

	public void export() {
		String fileName = "Orders_" + getId() + ".xls";
		// 响应对象
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			response.setHeader("Content-Disposition", "attachment;fileName=" + new String(fileName.getBytes(), "ISO-8859-1"));
			ordersBiz.export(response.getOutputStream(), getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 提供运单查询的运单号
	private Long waybillsn;

	public Long getWaybillsn() {
		return waybillsn;
	}

	public void setWaybillsn(Long waybillsn) {
		this.waybillsn = waybillsn;
	}

	public void waybilldetailList() {
		// 判断waybillsn是否为空
		if ("".equals(waybillsn.toString().trim())) {
			return;
		}
		// 通过orderBiz调用查询方法
		List<Waybilldetail> waybilldetailList = ordersBiz.waybilldetailList(waybillsn);
		// 通过ajax向前端输入结果
		BaseActionUtil.write(JSON.toJSONString(waybilldetailList));
	}
	
	/**
	 * 找到可以退的订单
	 */
	public void findOrdersToReturn(){
		Orders orders = getT1();
		Map<String, Object> map = ordersBiz.findOrdersToReturn(orders,getPage(),getRows());
		String jsonString = JSON.toJSONString(map,SerializerFeature.DisableCircularReferenceDetect);
		BaseActionUtil.write(jsonString);
	}
}
