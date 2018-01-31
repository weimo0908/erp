package cn.kumiaojie.erp.entity;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 退货订单实体类
 * @author Stivechen
 *
 */
public class Returnorders {

	/**
	 * 订单三个状态
	 */
	public static final String TYPE_IN="1";//采购订单
	public static final String TYPE_OUT="2";//销售订单
	
	public static final String STATE_UNCHECK="1";//未审核
	public static final String STATE_CHECKED="2";//已审核
	public static final String STATE_OUT="3";//已出库
	public static final String STATE_IN="4";//已入库
	
	private Long uuid;//编号
	private java.util.Date createtime;//生成日期
	private String cretaeName;
	private String checkName;
	private String endtName;
	private String supplierName;
	private java.util.Date checktime;//检查日期
	private java.util.Date endtime;//结束日期
	private String type;//订单类型
	private Long creater;//下单员
	private Long checker;//审核员工编号
	private Long ender;//库管员
	private Long supplieruuid;//供应商及客户编号
	private Double totalmoney;//合计金额
	private String state;//订单状态
	private Long waybillsn;//运单号
	
	//退货订单详情
	private List<Returnorderdetail> returnorderdetails;
	
	//原订单编号
	@JSONField(serialize=false)
	private Orders orders;//原订单编号 t.oreders.oredersuuid=




	public Long getUuid() {
		return uuid;
	}




	public void setUuid(Long uuid) {
		this.uuid = uuid;
	}




	public java.util.Date getCreatetime() {
		return createtime;
	}




	public void setCreatetime(java.util.Date createtime) {
		this.createtime = createtime;
	}




	public String getCretaeName() {
		return cretaeName;
	}




	public void setCretaeName(String cretaeName) {
		this.cretaeName = cretaeName;
	}




	public String getCheckName() {
		return checkName;
	}




	public void setCheckName(String checkName) {
		this.checkName = checkName;
	}




	public String getEndtName() {
		return endtName;
	}




	public void setEndtName(String endtName) {
		this.endtName = endtName;
	}




	public String getSupplierName() {
		return supplierName;
	}




	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}




	public java.util.Date getChecktime() {
		return checktime;
	}




	public void setChecktime(java.util.Date checktime) {
		this.checktime = checktime;
	}




	public java.util.Date getEndtime() {
		return endtime;
	}




	public void setEndtime(java.util.Date endtime) {
		this.endtime = endtime;
	}




	public String getType() {
		return type;
	}




	public void setType(String type) {
		this.type = type;
	}




	public Long getCreater() {
		return creater;
	}




	public void setCreater(Long creater) {
		this.creater = creater;
	}




	public Long getChecker() {
		return checker;
	}




	public void setChecker(Long checker) {
		this.checker = checker;
	}




	public Long getEnder() {
		return ender;
	}




	public void setEnder(Long ender) {
		this.ender = ender;
	}




	public Long getSupplieruuid() {
		return supplieruuid;
	}

	public void setSupplieruuid(Long supplieruuid) {
		this.supplieruuid = supplieruuid;
	}




	public Double getTotalmoney() {
		return totalmoney;
	}




	public void setTotalmoney(Double totalmoney) {
		this.totalmoney = totalmoney;
	}




	public String getState() {
		return state;
	}




	public void setState(String state) {
		this.state = state;
	}




	public Long getWaybillsn() {
		return waybillsn;
	}




	public void setWaybillsn(Long waybillsn) {
		this.waybillsn = waybillsn;
	}




	public List<Returnorderdetail> getReturnorderdetails() {
		return returnorderdetails;
	}




	public void setReturnorderdetails(List<Returnorderdetail> returnorderdetails) {
		this.returnorderdetails = returnorderdetails;
	}




	public Orders getOrders() {
		return orders;
	}




	public void setOrders(Orders orders) {
		this.orders = orders;
	}
	
	
	
}
