package cn.kumiaojie.erp.biz;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表业务接口
 *
 */
public interface IReportBiz {
	
	/**
	 * 销售/采购订单报表(以商品种类分组)
	 * @param startDate
	 * @param endDate
	 * @param type 类型:1进货;2销售
	 * @return
	 */
	public List orderReport(Date startDate, Date endDate , String type);
	
	/**
	 * 趋势图
	 * @param year
	 * @param type
	 * @return
	 */
	public List<Map<String, Object>> trendReport(int year);
	
	
	/**
	 * 销售/采购退货订单报表(以商品名称分组)
	 * @param startDate 
	 * @param endDate
	 * @param type 类型:1进货;2销售
	 * @return
	 */
	public List returnordersReport(Date startDate, Date endDate , String type);
	
	/**
	 * 仓库库存统计报表
	 * @param uuid 仓库编号
	 * @return
	 */
	public List<Map<String, Object>> storedetailReport(Long uuid);
	
	
	/**
	 * 商品类型进出库价格总额比例
	 */
	public List<Map<String, Object>> stroeoperReport(Long uuid ,  Integer year ,Integer month);
	
	
	
	
	/**
	 * 商品盘盈盘亏
	 * @param storeuuid
	 * @param year
	 * @param goodsuuid
	 * @return
	 */
	List InventoryReport(Long storeuuid, Integer year, Long goodsuuid);
	
}
