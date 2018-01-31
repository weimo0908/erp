package cn.kumiaojie.erp.biz.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.kumiaojie.erp.biz.IReportBiz;
import cn.kumiaojie.erp.dao.IGoodsDao;
import cn.kumiaojie.erp.dao.IGoodstypeDao;
import cn.kumiaojie.erp.dao.IReportDao;
import cn.kumiaojie.erp.dao.IStoredetailDao;
import cn.kumiaojie.erp.entity.Goods;
import cn.kumiaojie.erp.entity.Goodstype;
import cn.kumiaojie.erp.entity.Inventory;
import cn.kumiaojie.erp.entity.Storedetail;

/**
 * 报表业务实体类
 * @author Stivechen
 *
 */
public class ReportBiz implements IReportBiz {

	// 注入IReportDao
	public IReportDao reportDao;
	public IGoodstypeDao goodstypeDao;
	private IGoodsDao goodsDao;
	private IStoredetailDao storedetailDao;
	
	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}
	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}
	public void setReportDao(IReportDao reportDao) {
		this.reportDao = reportDao;
	}
	public void setGoodstypeDao(IGoodstypeDao goodstypeDao) {
		this.goodstypeDao = goodstypeDao;
	}
	
	//设置两个个全局变量
	private Long storeuuid;
	private Integer year;


	/**
	 * 采购/销售饼状图
	 */
	@Override
	public List orderReport(Date startDate, Date endDate, String type) {
		return reportDao.orderReport(startDate, endDate, type);
	}

	/**
	 * 采购/销售退货饼状图
	 */
	@Override
	public List returnordersReport(Date startDate, Date endDate, String type) {
		return reportDao.returnordersReport(startDate, endDate, type);
	}
	
	
	/**
	 * 直线图
	 */
	@Override
	public List<Map<String, Object>> trendReport(int year) {
		// 要求:[{"name":4,"y":50135,"h":2229},{"name":5,"y":20000,"h":23429}]
		// 采购 type =1
		// [{"name":1,"y":50135},{"name":2,"y":20000}]
		List<Map<String, Object>> pList = complementData(year, "1", "y");
		// 销售type =2
		// [{"name":1,"h":50135},{"name":2,"h":20000}]
		List<Map<String, Object>> sList = complementData(year, "2", "h");

		// 加工
		List<Map<String, Object>> resultList = new ArrayList<>();//返回的数组
		
		Map<String, Object> yhData = null;//接收的新集合
		//循环遍历12个月,也就是原来数组的长度,从0开始
		for (int i = 0; i < 12; i++) {
			yhData=new HashMap<>();
			yhData.put("name", pList.get(i).get("name"));//添加月份
			yhData.put("y", pList.get(i).get("y"));//y的数据-->采购
			yhData.put("h", sList.get(i).get("h"));//h的数据-->销售
			resultList.add(yhData);
		}
		return resultList;
	}

	/**
	 * 获取初步数据(x轴为月份)
	 * @param year
	 * @param type
	 * @param symbol
	 * @return
	 */
	private List<Map<String, Object>> complementData(int year, String type, String symbol) {
		// 对数据进行补充(有月份会缺少)[{"name":4,"y":50135},{"name":5,"y":20000}]
		List<Map<String, Object>> list = reportDao.getSumMoney(year, type);
		// 返回的数据reData
		List<Map<String, Object>> reData = new ArrayList<Map<String, Object>>();
		// 从list中提取的Map集合key =month value={"name":4,"y":50135}
		Map<String, Map<String, Object>> yearData = new HashMap<String, Map<String, Object>>();
		// 把list集合的数据放到yearData集合,然后进行判断
		for (Map<String, Object> month : list) {
			yearData.put(month.get("name") + "", month);
		}
		// 补充缺少的月份(monthData即将会添加到reData中返回)
		Map<String, Object> monthData = null;
		for (int i = 1; i <= 12; i++) {
			// 获取value={"name":4,"y":50135}
			monthData = yearData.get(i + "");
			// 判断是否有值
			if (null == monthData) {
				// 没有值,补充一个
				monthData = new HashMap<String, Object>();
				monthData.put("name", i + "月");
				monthData.put(symbol, 0);
			} else {
				monthData.put("name", i + "月");// key重复时候,会替换
			}
			reData.add(monthData);
		}
		return reData;
	}

	/**
	 * 仓库库存统计报表
	 */
	@Override
	public List<Map<String, Object>> storedetailReport(Long storeuuid) {
		
		// 对数据进行补充(有商品种类)[{"name":"数码家电","y":2000},{"name":"美食特产","y":20000}]
		List<Map<String, Object>> list = reportDao.storedetailReport(storeuuid);
		// 返回的数据reData
		List<Map<String, Object>> reData = new ArrayList<Map<String, Object>>();
		// 从list中提取的Map集合key =month value={"name":"数码家电","y":2000}
		Map<String, Map<String, Object>> typeData = new HashMap<String, Map<String, Object>>();
		// 把list集合的数据放到yearData集合,然后进行判断
		for (Map<String, Object> type : list) {
			typeData.put(type.get("name") + "", type);
		}
		//获取全部的商品类型
		List<Goodstype> allType = goodstypeDao.getList(null, null, null);
		// 补充缺少的月份(monthData即将会添加到reData中返回)
		Map<String, Object> allTypeData = null;
		for (Goodstype gt:allType) {
			// 获取value={"name":"数码家电","y":2000}
			allTypeData = typeData.get(gt.getName());
			// 判断是否有值
			if (null == allTypeData) {
				// 没有值,补充一个
				allTypeData = new HashMap<String, Object>();
				allTypeData.put("name", gt.getName());
				allTypeData.put("y", "null");
			} else {
				allTypeData.put("name", gt.getName());// key重复时候,会替换
			}
			reData.add(allTypeData);
		}
		return reData;
	}
	
	/**
	 * 商品类型进出库价格总额比例
	 */
	@Override
	public List<Map<String, Object>> stroeoperReport(Long uuid,  Integer year ,Integer month) {
		// 要求:[{"name":4,"y":50135,"h":2229},{"name":5,"y":20000,"h":23429}]
		// 采购 type =1
		// {"name":"数码家电","y":2000}
		List<Map<String, Object>> pList = manageData("1", uuid, year, month,"y");
		// 销售type =2
		// {"name":"数码家电","h":2000}
		List<Map<String, Object>> sList = manageData("2", uuid, year, month,"h");

		// 加工
		List<Map<String, Object>> resultList = new ArrayList<>();//返回的数组
		
		Map<String, Object> yhData = null;//接收的新集合
		
		//循环遍历其中一个(因为类型的循序都一样)
		for (int i = 0; i < pList.size(); i++) {
			yhData=new HashMap<>();
			yhData.put("name", pList.get(i).get("name"));//种类名称
			yhData.put("y", pList.get(i).get("y"));//y的数据-->采购
			yhData.put("h", sList.get(i).get("h"));//h的数据-->销售
			resultList.add(yhData);
		}
		
		return resultList;
	}

	/**
	 * 获取初步数据(x轴为对象名称)
	 * @param year
	 * @param type
	 * @param symbol
	 * @return
	 */
	private List<Map<String, Object>> manageData(String type, Long uuid,  Integer year ,Integer month,String symbol){
		// 对数据进行补充(有商品种类)[{"name":"数码家电","y":2000},{"name":"美食特产","y":20000}]
		List<Map<String, Object>> list = reportDao.stroeoperReport(type, uuid, year, month);
		// 返回的数据reData
		List<Map<String, Object>> reData = new ArrayList<Map<String, Object>>();
		// 从list中提取的Map集合key =month value={"name":"数码家电","y":2000}
		Map<String, Map<String, Object>> typeData = new HashMap<String, Map<String, Object>>();
		// 把list集合的数据放到yearData集合,然后进行判断
		for (Map<String, Object> t : list) {
			typeData.put(t.get("name") + "", t);
		}
		//获取全部的商品类型
		List<Goodstype> allType = goodstypeDao.getList(null, null, null);
		// 补充缺少的月份(monthData即将会添加到reData中返回)
		Map<String, Object> allTypeData = null;
		for (Goodstype gt:allType) {
			// 获取value={"name":"数码家电","y":2000}
			allTypeData = typeData.get(gt.getName());
			// 判断是否有值
			if (null == allTypeData) {
				// 没有值,补充一个
				allTypeData = new HashMap<String, Object>();
				allTypeData.put("name", gt.getName());
				allTypeData.put(symbol, 0);
			} else {
				allTypeData.put("name", gt.getName());// key重复时候,会替换
			}
			reData.add(allTypeData);
		}
		return reData;
	}
	
	/**
	 * 查找盘盈盘亏图表
	 * @param storeuuid
	 * @param year
	 * @param goodsuuid
	 * @return
	 * 
	 */
	@Override //[{month=4, y=20},{month=5, y=30}]  month是Integer   y是Long
	public List InventoryReport(Long storeuuid, Integer year, Long goodsuuid) {
		this.storeuuid = storeuuid;
		this.year = year;
		ArrayList<Map<String, Object>> list = new ArrayList<>();
		
		if(goodsuuid == null){
			Storedetail storedetail1 = new Storedetail();
			storedetail1.setStoreuuid(storeuuid);
//			List<Storedetail> sd_list = storedetailDao.findAll(storedetail1, null, null);
			List<Storedetail> sd_list = storedetailDao.getList(storedetail1, null, null);
			//拿到该仓库下所有商品的uuid
			for (Storedetail sd : sd_list) {
				Long sd_goodsuuid = sd.getGoodsuuid();
				Map<String, Object> goodsReportMap = getGoodsReport(sd_goodsuuid);//LONG不能转为Integer
				list.add(goodsReportMap);
			}
		}else{
			list.add(getGoodsReport(goodsuuid));
		}
//		拿到
//		   [{
//            name: 'Tokyo',
//            data: [{"month":"1月","y":0},{"month":"2月","y":0},{"month":"3月","y":0}]
//     	   },
//		   {
//            name: 'Tokyo',
//            data: [{"month":"1月","y":0},{"month":"2月","y":0},{"month":"3月","y":0}]
//     	   }]
		
		return list;
	}
	
	/**
	 * 查找盘盈盘亏商品图表
	 * 传入goodsuuid 可以得到
	 * 		 {
	 *         name: '商品名',
	 *         data: [{"month":"1月","y":0},{"month":"2月","y":0},{"month":"3月","y":0}]
	 *    	 }
	 * @param goodsuuid
	 * @return
	 */
	private Map<String,Object> getGoodsReport(Long goodsuuid){
		//盘亏的集合
		HashMap<Integer, Object> month_y01 = new HashMap<>();
		//盘盈的集合
		HashMap<Integer, Object> month_y02 = new HashMap<>();
		
		//盘亏
		List<Map<String,Object>> list01 = reportDao.InventoryReport(storeuuid, Inventory.TYPE_DEFICIT, year, goodsuuid);
		for (Map<String, Object> map : list01) {
			month_y01.put((Integer)map.get("month"), map.get("y"));
		}
		//循环遍历12月
		for(int i = 1;i<=12;i++){
			if(month_y01.get(i)==null){
				month_y01.put(i,0);
			}else{
				month_y01.put(i,((Long)month_y01.get(i)).intValue()*-1);
			}
		}
		
		//盘盈
		List<Map<String,Object>> list02 = reportDao.InventoryReport(storeuuid, Inventory.TYPE_PROFIT, year, goodsuuid);
		for (Map<String, Object> map : list02) {
			month_y02.put((Integer)map.get("month"), map.get("y"));
		}
		list02.clear();
		//循环遍历12月
		HashMap<String, Object> map = null;
		for(int i = 1;i<=12;i++){
			map = new HashMap<>();
			map.put("month", i+"月");
			if(month_y02.get(i)==null){
				map.put("y",month_y01.get(i));//有值的时候是Long ，没值的时候是Integer
			}else{
				map.put("y", ((Long)month_y02.get(i)).intValue()+(Integer)month_y01.get(i));
			}
			
			//拿到[{"month":"1月","y":0},{"month":"2月","y":0},{"month":"3月","y":0}]
			list02.add(map);
		}
		
		HashMap<String, Object> final_map = new HashMap<>();
//		Goods goods = goodsDao.findById(goodsuuid);
		Goods goods = goodsDao.get(goodsuuid);
//		拿到
//		{
//            name: 'Tokyo',
//            data: [{"month":"1月","y":0},{"month":"2月","y":0},{"month":"3月","y":0}]
//      }
		final_map.put("name", goods.getName());
		final_map.put("data", list02);
		
		return final_map;
	}
	
}
