package cn.kumiaojie.erp.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import cn.kumiaojie.erp.dao.IReportDao;

/**
 * 报表数据访问实体类
 * @author Stivechen
 *	不继承baseDao,减少耦合性
 */
@SuppressWarnings("unchecked")
public class ReportDao extends HibernateDaoSupport implements IReportDao {

	/**
	 * 销售订单报表
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 */
	@Override
	public List orderReport(Date startDate, Date endDate, String type) {
		//投影查询
		String hql = "select new Map(gt.name as name,sum(od.money) as y) "
				+ "from Goodstype gt, Goods g, Orders o, Orderdetail od "
				+ "where g.goodstype=gt and od.orders=o and od.goodsuuid=g.uuid ";
		//判断type类型1:进货统计;2:销售统计
		if ("1".equals(type)) {
			//采购统计
			hql +="and o.type ='1' ";
		}
		if ("2".equals(type)) {
			//销售统计
			hql +="and o.type ='2' ";
		}
		//添加日期查询条件
		List<Date> queryParms = new ArrayList<Date>();
		if (null != startDate) {
			hql +="and o.createtime >=? ";
			queryParms.add(startDate);
		}
		if (null != endDate) {
			hql +="and o.createtime <=? ";
			queryParms.add(endDate);
		}
		hql +="group by gt.name";
		//判断数据是否为空
		if (queryParms.size()>0) {
			return this.getHibernateTemplate().find(hql,queryParms.toArray(new Object[]{}));
		}
		return this.getHibernateTemplate().find(hql);
	}

	/**
	 * 趋势图
	 */
	@Override
	public List<Map<String, Object>> getSumMoney(int year ,String type) {
		//判断采购:type=1;销售:type=2
		String hql ="";
		if (type =="1") {
			//采购
			hql="select new Map(month(o.createtime) as name, sum(od.money) as y)"
				+ "from Orderdetail od, Orders o "
				+ "where od.orders=o "
				+ "and o.type=? "
				+ "and year(o.createtime)=? "
				+ "group by month(o.createtime)";
		}
		
		if (type=="2") {
			//销售
			hql="select new Map(month(o.createtime) as name, sum(od.money) as h)"
					+ "from Orderdetail od, Orders o "
					+ "where od.orders=o "
					+ "and o.type=? "
					+ "and year(o.createtime)=? " 
					+ "group by month(o.createtime)";
		}
			
		return (List<Map<String, Object>>) getHibernateTemplate().find(hql, type,year);
	}

	/**
	 * 退货订单报表(以商品名称分组)
	 * @param startDate 起始日期
	 * @param endDate 结束日期
	 */
	@Override
	public List returnordersReport(Date startDate, Date endDate, String type) {
		//投影查询	"y"为higtcharts需要的类型
		
		String hql = "select new Map(g.name as name,sum(rdl.money) as y) "
				+ "from  Goods g, Returnorders ro, Returnorderdetail rdl "
				+ "where rdl.returnorders=ro and rdl.goodsuuid=g.uuid ";
		//判断type类型1:进货统计;2:销售统计
		if ("1".equals(type)) {
			//采购统计
			hql +="and ro.type ='1' ";
		}
		if ("2".equals(type)) {
			//销售统计
			hql +="and ro.type ='2' ";
		}
		//添加日期查询条件
		List<Date> queryParms = new ArrayList<Date>();
		if (null != startDate) {
			hql +="and ro.createtime >=? ";
			queryParms.add(startDate);
		}
		if (null != endDate) {
			hql +="and ro.createtime <=? ";
			queryParms.add(endDate);
		}
		hql +="group by g.name";
		//判断数据是否为空
		if (queryParms.size()>0) {
			return this.getHibernateTemplate().find(hql,queryParms.toArray(new Object[]{}));
		}
		return this.getHibernateTemplate().find(hql);
	}

	/**
	 * 仓库库存统计报表
	 */
	@Override
	public List<Map<String, Object>> storedetailReport(Long uuid) {
		//查询语句
		/**
		 * select gt.name as "商品名称",sd.num as "数量" 
			from goods g,goodstype gt , storedetail sd
			where g.uuid = sd.goodsuuid
			and g.goodstypeuuid = gt.uuid
			and sd.storeuuid =8
		 */
		 String hql="select new Map(gt.name as name, sum(sd.num) as y) "
				+ "from Goods g, Goodstype gt, Storedetail sd "
				+ "where g.goodstype=gt "
				+ "and g.uuid = sd.goodsuuid "
				+ "and sd.storeuuid =? "
				+ "group by gt.name";
		
		return (List<Map<String, Object>>) this.getHibernateTemplate().find(hql, uuid);
	}

	/**
	 * 商品类型进出库价格总额比例
	 * type类型1:采购统计;2:销售统计
	 */
	@Override
	public List<Map<String, Object>> stroeoperReport(String type, Long uuid, Integer year ,Integer month) {
		/**
		 * 查询语句
		 * select gt.name ,nvl(sum(so.num*g.inprice),0)
		from storeoper so , goods g ,goodstype gt
		where g.goodstypeuuid = gt.uuid
		and so.goodsuuid = g.uuid
		and so.type =1
		and storeuuid =4
		and to_date(so.opertime,'yyyy/mm')='2017-5'
		group by gt.name
		 */
		//投影查询
		String hql ="";
		//判断类型type类型1:采购统计;2:销售统计
		if ("1"==type) {
			//y
			hql = "select new Map(gt.name as name,sum(so.num*g.inprice) as y) "
					+ "from Goodstype gt, Goods g, Storeoper so "
					+ "where g.goodstype=gt and so.goodsuuid = g.uuid "
					+ "and so.type ='1' ";
		}
		if ("2"==type) {
			//h
			hql = "select new Map(gt.name as name,sum(so.num*g.inprice) as h) "
					+ "from Goodstype gt, Goods g, Storeoper so "
					+ "where g.goodstype=gt and so.goodsuuid = g.uuid "
					+ "and so.type ='2' ";
		}
			
		//添加参数
		List<Object> queryParms = new ArrayList<Object>();
		//仓库编号
		if (null != uuid) {
			hql+="and storeuuid =? ";
			queryParms.add(uuid);
		}
		//年份
		if (null != year) {
			hql +="and year(so.opertime)=? ";
			queryParms.add(year);
		}
		//月份
		if (0 != month) {
			hql +="and month(so.opertime)=? ";
			queryParms.add(month);
		}
		
		
		hql +="group by gt.name";
		//判断数据是否为空
		if (queryParms.size()>0) {
			return (List<Map<String, Object>>) this.getHibernateTemplate().find(hql,queryParms.toArray(new Object[]{}));
		}
		return (List<Map<String, Object>>) this.getHibernateTemplate().find(hql);
	}
	

	/**
	 * 盘盈盘亏图表
	 */
	@Override
	public List InventoryReport(Long storeuuid, String type, Integer year, Long goodsuuid) {
		ArrayList<Object> list = new ArrayList<>();
		list.add(storeuuid);
		list.add(type);
		list.add(year);
		String hql = "select new Map(sum(i.num) as y, month(i.checktime) as month) "
				+ "from Inventory i where i.storeuuid = ? and i.type = ? and year(i.checktime) = ? ";
		if(goodsuuid != null){
			hql+="and i.goodsuuid = ? ";
			list.add(goodsuuid);
		}
		hql += "group by month(i.checktime)";
		
//		return this.getHibernateTemplate().find(hql,storeuuid,type,year,goodsuuid);
		return this.getHibernateTemplate().find(hql,list.toArray());
	}
	
}
