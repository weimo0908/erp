package cn.kumiaojie.erp.web.action;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.kumiaojie.erp.biz.IReportBiz;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;

/**
 * 统计报表
 * @author Stivechen
 *
 */
public class ReportAction {

	//注入IReportBiz
	private IReportBiz reportBiz;
	
	public void setReportBiz(IReportBiz reportBiz) {
		this.reportBiz = reportBiz;
	}
	//创建页面接收的数据
	private Date startDate;
	private Date endDate;
	private String type;//1:进货量 2:销售量
	private Integer year;//查询年份
	private String month;//查询月份
	private Long storeuuid;//查询仓库编号
	private Long goodsuuid;//查询商品id
	
	
	public void setGoodsuuid(Long goodsuuid) {
		this.goodsuuid = goodsuuid;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}
	public Long getStoreuuid() {
		return storeuuid;
	}
	public void setStoreuuid(Long storeuuid) {
		this.storeuuid = storeuuid;
	}
	/**
	 * 销售/采购统计报表(以商品种类分组)
	 */
	public void ordersPieReport(){
		List ordersreportData = reportBiz.orderReport(startDate, endDate, type);
		BaseActionUtil.write(JSON.toJSONString(ordersreportData));
	}	
	
	/**
	 * 销售/采购统计报表(以商品名称分组)
	 */
	public void returnordersPieReport(){
		List returnordersReportData = reportBiz.returnordersReport(startDate, endDate, type);
		BaseActionUtil.write(JSON.toJSONString(returnordersReportData));
	}
	
	
	/**
	 * 采购/销售趋势分析
	 */
	public void trendReportWithAll(){
		//默认选择2017年
		if (null == year) {
			year=2017;
		}
		List<Map<String, Object>> trendReport = reportBiz.trendReport(year);
		BaseActionUtil.write(JSON.toJSONString(trendReport));
	}
	
	
	/**
	 * 仓库库存统计报表
	 */
	public void storedetailReport(){
		//默认选择仓库编号4年
		if (null == storeuuid) {
			storeuuid=4L;
		}
		List<Map<String, Object>> storedetailReport = reportBiz.storedetailReport(storeuuid);
		BaseActionUtil.write(JSON.toJSONString(storedetailReport));
	}
	
	
	
	/**
	 * 商品类型进出库价格总额比例
	 */
	public void stroeoperReport(){
		//设置初始化数据
		if (null == year) {
			year = 2017;
		}
		if (null==month||"全年".equals(month)) {
			month="0";
		}
		
		List<Map<String, Object>> stroeoperReport = reportBiz.stroeoperReport(storeuuid, year, Integer.parseInt(month));
		BaseActionUtil.write(JSON.toJSONString(stroeoperReport));
	}
	
	
	/**
	 * 盘盈盘亏，图表
	 */
	public void getInventoryReport(){
		List list = reportBiz.InventoryReport(storeuuid, new Integer(year), goodsuuid);
		BaseActionUtil.write(JSON.toJSONString(list));
//		this.write(list);
	}
	
}
