package cn.kumiaojie.erp.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 报表数据访问接口
 * @author Stivechen
 *
 */
public interface IReportDao {

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
	 * @return
	 */
	public List<Map<String, Object>> getSumMoney(int year,String type);
	
	/**
	 * 销售/采购退货订单报表(以商品名称分组)
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 * @param type 类型: 1进货 ; 2销售
	 * @return
	 */
	public List returnordersReport(Date startDate, Date endDate , String type);

	
	/**
	 * 仓库库存统计报表
	 * @param storeuuid 仓库编号
	 * @return 
	 */
	public List<Map<String, Object>> storedetailReport(Long uuid);
	
	
	/**
	 * 商品类型进出库价格总额比例
	 * @param type
	 * @param storeuuid
	 * @return
	 */
	public List<Map<String, Object>> stroeoperReport(String type,Long uuid , Integer year ,Integer month);
	
	
	/**
	 * 盘盈盘亏图表
	 * @param storeuuid
	 * @param type
	 * @param year
	 * @param goodsuuid
	 * @return
	 */
	List InventoryReport(Long storeuuid, String type, Integer year, Long goodsuuid);
}
