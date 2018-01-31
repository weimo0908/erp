package cn.kumiaojie.erp.biz.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;

import com.redsun.bos.ws.Waybilldetail;
import com.redsun.bos.ws.impl.IWaybillWs;

import cn.kumiaojie.erp.biz.IOrdersBiz;
import cn.kumiaojie.erp.dao.IEmpDao;
import cn.kumiaojie.erp.dao.IOrdersDao;
import cn.kumiaojie.erp.dao.ISupplierDao;
import cn.kumiaojie.erp.dao.impl.EmpDao;
import cn.kumiaojie.erp.dao.impl.SupplierDao;
import cn.kumiaojie.erp.entity.Orderdetail;
import cn.kumiaojie.erp.entity.Orders;
import cn.kumiaojie.erp.entity.Returnorderdetail;
import cn.kumiaojie.erp.entity.Returnorders;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.utils.GetNameOrSaveUtil;

/**
 * OrdersBiz实现类
 * 
 * @author Stivechen
 *
 */
/**
 * @author Stivechen
 *
 */
public class OrdersBiz extends BaseBiz<Orders> implements IOrdersBiz {

	// 注入IOrdersDao
	private IOrdersDao ordersDao;

	public void setOrdersDao(IOrdersDao ordersDao) {
		this.ordersDao = ordersDao;
		// 继承父类的setBaseDao,并注入orders,让其具体的方法有具体的对象
		super.setBaseDao(this.ordersDao);
	}

	// 注入empDao.进行订单的操作
	private IEmpDao empDao;
	// 注入supplierDao,进行供应商操作
	private ISupplierDao supplierDao;

	public void setEmpDao(EmpDao empDao) {
		this.empDao = empDao;
	}

	public void setSupplierDao(SupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	//注入客户端的IWaybillWs
	private IWaybillWs waybillWs;
	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	/**
	 * 重写add方法
	 */
	public void add(Orders orders) {
		
		/*=====代码级别的权限控制=====*/
		//获取主题
		Subject subject = SecurityUtils.getSubject();
		//根据页面传进来的type类型进行判断权限
		if (!Orders.TYPE_IN.equals(orders.getType())) {
			//与"我的采购订单"不符合
			if (!subject.isPermitted("我的采购订单")) {
				throw new ERPException("对不起,您的权限不足!");
			}
		}else if (!Orders.TYPE_OUT.equals(orders.getType())) {
			//与"我的采购订单"不符合
			if (!subject.isPermitted("我的采购订单")) {
				throw new ERPException("对不起,您的权限不足!");
			}
		}else {
			throw new ERPException("非法请求参数!");
		}
		
		/*=====添加订单操作=====*/
		
		// 订单类型由页面决定
		// 设置订单的状态:未审核
		orders.setState(Orders.STATE_CREATE);
		// 设置订单时间
		orders.setCreatetime(new Date());
		// 计算总金额
		double totalMoney = 0;
		for (Orderdetail orderdetail : orders.getOrderDetails()) {
			totalMoney += orderdetail.getMoney();
			// 同时设置orderdetail的状态-->未入库
			orderdetail.setState(Orderdetail.STATE_NOT_IN);
			// 设置订单详情所在的订单
			orderdetail.setOrders(orders);
		}
		// 设置总金额
		orders.setTotalmoney(totalMoney);
		// 保存订单(同时也会保存了订单详情)
		ordersDao.add(orders);
	}

	/**
	 * 重写getListByPage方法
	 * 增加个层级审核员工姓名的缓存,库存管理员姓名缓存
	 * 调用工具类:GetNameOrSaveUtil
	 */
	public List<Orders> getListByPage(Orders orders1, Orders orders2, Object param, int firstResult, int maxResults) {
		// 获取分页结果(截获--->加工)
		List<Orders> ordersList = super.getListByPage(orders1, orders2, param, firstResult, maxResults);
		// 缓存empName集合
		Map<Long, String> empNameMap = new HashMap<Long, String>();
		// 缓存supplierName集合
		Map<Long, String> supplierNameMap = new HashMap<Long, String>();
		// 编辑加工
		for (Orders o : ordersList) {
			// 调用GetNameOrSaveUtil工具类
			// emp
			o.setCreaterName(GetNameOrSaveUtil.getEmpName(o.getCreater(), empNameMap, empDao));
			o.setCheckerName(GetNameOrSaveUtil.getEmpName(o.getChecker(), empNameMap, empDao));
			o.setStarterName(GetNameOrSaveUtil.getEmpName(o.getStarter(), empNameMap, empDao));
			o.setEnderName(GetNameOrSaveUtil.getEmpName(o.getEnder(), empNameMap, empDao));
			// supplier
			o.setSupplierName(GetNameOrSaveUtil.getSupplierName(o.getSupplieruuid(), supplierNameMap, supplierDao));
		}
		// 返回加工后的
		return ordersList;
	}

	/**
	 * 订单审查(修改审查员,审查时间,流程状态)
	 */
	@Override
	@RequiresPermissions("采购订单审核")
	public void doCheck(Long uuid, Long empUuid) {
		// 获取订单详情(从dao层获取)
		Orders orders = ordersDao.get(uuid);
		// 已审查(不等于未审查的都视为'已审查')
		if (!Orders.STATE_CREATE.equals(orders.getState())) {
			throw new ERPException("对不起,订单已经审查过了,请选择未审查的");
		}
		// 更新审查员
		orders.setChecker(empUuid);
		// 更新审查时间
		orders.setChecktime(new Date());
		// 更新流程状态
		orders.setState(Orders.STATE_CHECK);
		// 之后会自动保存
	}

	/*
	 * 订单确认(修改审查员,审查时间,流程状态)
	 */
	@Override
	@RequiresPermissions("采购订单确认")
	public void doStart(Long uuid, Long empUuid) {
		Orders orders = ordersDao.get(uuid);
		// 要先判断是否审核过
		if (!Orders.STATE_CHECK.equals(orders.getState())) {
			// 已确认过
			throw new ERPException("对不起,订单已经确认过了,请选择为确认的");
		}
		// 更新审查员
		orders.setStarter(empUuid);
		// 更新确认时间
		orders.setStarttime(new Date());
		// 更新流程状态
		orders.setState(Orders.STATE_START);
		// 之后会自动保存(事务提交)
	}

	/**
	 * 根据导出订单详情
	 */
	@Override
	public void export(OutputStream os, Long uuid) {
		// 创建一个工作簿
		HSSFWorkbook wb = new HSSFWorkbook();
		// 获取订单
		Orders orders = ordersDao.get(uuid);
		// 获取订单详情
		List<Orderdetail> details = orders.getOrderDetails();
		// 设置表名
		String sheetName = "";
		if (Orders.TYPE_IN.equals(orders.getType())) {
			sheetName = "采 购 订 单";
		}
		if (Orders.TYPE_OUT.equals(orders.getType())) {
			sheetName = "销 售 订 单";
		}
		// 建立一个工作表
		HSSFSheet sheet = wb.createSheet(sheetName);
		// 创建一行,从0开始
		HSSFRow row = sheet.createRow(0);
		// 创建表格但愿的样式
		HSSFCellStyle style_content = wb.createCellStyle();
		style_content.setBorderBottom(BorderStyle.THIN);// 下边框
		style_content.setBorderTop(BorderStyle.THIN);// 上边框
		style_content.setBorderRight(BorderStyle.THIN);// 右边框
		style_content.setBorderLeft(BorderStyle.THIN);// 左边框
		// 对齐方式:水平剧中
		style_content.setAlignment(HorizontalAlignment.CENTER);
		// 垂直居中
		style_content.setVerticalAlignment(VerticalAlignment.CENTER);

		// 创建字体样式
		HSSFFont content_font = wb.createFont();
		content_font.setFontName("微软雅黑");
		// 设置字体大小
		content_font.setFontHeightInPoints((short) 12);
		style_content.setFont(content_font);

		// 设置日期格式
		HSSFCellStyle style_date = wb.createCellStyle();
		// 把style_content里样式复制到style_date
		style_date.cloneStyleFrom(style_content);
		HSSFDataFormat dataFormat = wb.createDataFormat();
		style_date.setDataFormat(dataFormat.getFormat("yyyy-MM-dd hh:mm:ss"));

		// 标题样式
		HSSFCellStyle style_title = wb.createCellStyle();
		style_title.setAlignment(HorizontalAlignment.CENTER);// 标题居中
		style_title.setVerticalAlignment(VerticalAlignment.CENTER);// 垂直居中
		// 字体样式
		HSSFFont title_font = wb.createFont();
		title_font.setFontName("黑体");
		title_font.setFontHeightInPoints((short) 18);
		// 加粗
		title_font.setBold(true);
		style_title.setFont(title_font);

		// 合并相应的单元格
		// 标题
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
		// 供应商
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 3));
		// 订单明细
		sheet.addMergedRegion(new CellRangeAddress(7, 7, 0, 3));

		// 创建矩阵 11行 4列
		int rowCount = details.size() + 9;
		for (int i = 2; i <= rowCount; i++) {
			// 创建行
			row = sheet.createRow(i);
			for (int j = 0; j < 4; j++) {
				// 设置创建单元格和样式
				row.createCell(j).setCellStyle(style_content);
			}
		}

		// 必须现有创建的行和单元格
		// 创建标题单元格
		HSSFCell titleCell = sheet.createRow(0).createCell(0);
		// 设置标题样式
		titleCell.setCellStyle(style_title);
		titleCell.setCellValue(sheetName);

		sheet.getRow(2).getCell(0).setCellValue("供应商");
		sheet.getRow(3).getCell(0).setCellValue("下单日期");
		sheet.getRow(4).getCell(0).setCellValue("审核日期");
		sheet.getRow(5).getCell(0).setCellValue("采购日期");
		if (Orders.TYPE_IN.equals(orders.getType())) {
			sheet.getRow(6).getCell(0).setCellValue("入库日期");
		}
		if (Orders.TYPE_OUT.equals(orders.getType())) {
			sheet.getRow(6).getCell(0).setCellValue("出库日期");
		}
		sheet.getRow(3).getCell(2).setCellValue("经办人");
		sheet.getRow(4).getCell(2).setCellValue("经办人");
		sheet.getRow(5).getCell(2).setCellValue("经办人");
		sheet.getRow(6).getCell(2).setCellValue("经办人");

		sheet.getRow(7).getCell(0).setCellValue("订单明细");

		sheet.getRow(8).getCell(0).setCellValue("商品名称");
		sheet.getRow(8).getCell(1).setCellValue("数量");
		sheet.getRow(8).getCell(2).setCellValue("价格(元)");
		sheet.getRow(8).getCell(3).setCellValue("金额(元)");

		// 设置行高
		// 标题行高
		sheet.getRow(0).setHeight((short) 1000);
		// 其余内容的行高
		for (int i = 2; i <= rowCount; i++) {
			sheet.getRow(i).setHeight((short) 500);
		}

		// 设置列宽
		for (int i = 0; i < 4; i++) {
			sheet.setColumnWidth(i, 6000);
		}

		// 缓存供应商编号与员工的名称,key=供应商编号;value=供应商名称
		Map<Long, String> supplierNameMap = new HashMap<Long, String>();
		// 设置供应商
		sheet.getRow(2).getCell(1).setCellValue(GetNameOrSaveUtil.getSupplierName(orders.getSupplieruuid(), supplierNameMap, supplierDao));

		// 订单详情(日期)
		sheet.getRow(3).getCell(1).setCellStyle(style_date);
		sheet.getRow(4).getCell(1).setCellStyle(style_date);
		sheet.getRow(5).getCell(1).setCellStyle(style_date);
		sheet.getRow(6).getCell(1).setCellStyle(style_date);
		// 判断是否有内容
		if (null != orders.getCreatetime()) {
			sheet.getRow(3).getCell(1).setCellValue(orders.getCreatetime());
		}
		if (null != orders.getChecktime()) {
			sheet.getRow(4).getCell(1).setCellValue(orders.getChecktime());
		}
		if (null != orders.getStarttime()) {
			sheet.getRow(5).getCell(1).setCellValue(orders.getStarttime());
		}
		if (null != orders.getEndtime()) {
			sheet.getRow(6).getCell(1).setCellValue(orders.getEndtime());
		}

		// 缓存员工编号员工姓名
		Map<Long, String> empNameMap = new HashMap<Long, String>();
		sheet.getRow(3).getCell(3).setCellValue(GetNameOrSaveUtil.getEmpName(orders.getCreater(), empNameMap, empDao));
		sheet.getRow(4).getCell(3).setCellValue(GetNameOrSaveUtil.getEmpName(orders.getChecker(), empNameMap, empDao));
		sheet.getRow(5).getCell(3).setCellValue(GetNameOrSaveUtil.getEmpName(orders.getStarter(), empNameMap, empDao));
		sheet.getRow(6).getCell(3).setCellValue(GetNameOrSaveUtil.getEmpName(orders.getEnder(), empNameMap, empDao));

		// 设置订单明细内容
		int index = 0;
		Orderdetail od = null;
		for (int i = 9; i < rowCount; i++) {
			od = details.get(index);
			// 创建行
			row = sheet.getRow(i);
			row.getCell(0).setCellValue(od.getGoodsname());
			row.getCell(1).setCellValue(od.getNum());
			row.getCell(2).setCellValue(od.getPrice());
			row.getCell(3).setCellValue(od.getMoney());
			index++;
		}

		//设置合计
		sheet.getRow(rowCount).getCell(0).setCellValue("合计");
		sheet.getRow(rowCount).getCell(3).setCellValue(orders.getTotalmoney());
		
		//描写完毕,写输出流
		try {
			wb.write(os);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				wb.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * 根据订单号查询订单详情
	 */
	@Override
	public List<Waybilldetail> waybilldetailList(Long sn) {
		return waybillWs.waybilldetailList(sn);
	}
	
	/**
	 * 找到可以退的订单
	 */
	public Map<String,Object> findOrdersToReturn(Orders orders, Integer page,Integer rows){
		//调用整合方法
		List<Orders> list = mergeOrders(orders,page,rows);
		
		HashMap<Long, String> empMap = new HashMap<>();
		HashMap<Long, String> supplierMap = new HashMap<>();
		for (Orders o : list) {
//			拿员工的名字
			o.setCreaterName(GetNameOrSaveUtil.getEmpName(o.getCreater(), empMap, empDao));
			o.setCheckerName(GetNameOrSaveUtil.getEmpName(o.getChecker(), empMap,empDao));
			o.setStarterName(GetNameOrSaveUtil.getEmpName(o.getStarter(), empMap,empDao));
			o.setEnderName(GetNameOrSaveUtil.getEmpName(o.getEnder(), empMap,empDao));
			
//			拿供应商的名字
			o.setSupplierName(GetNameOrSaveUtil.getSupplierName(o.getSupplieruuid(), supplierMap, supplierDao));
		}
		
		Long total = ordersDao.getCount(orders,null,null);
		HashMap<String, Object> map = new HashMap<>();
		map.put("total", total);
		map.put("rows", list);
		return map;
	}
	
	/**
	 * 整合
	 * @return
	 */
	private List<Orders> mergeOrders(Orders orders,Integer page,Integer rows){
		////采购: 0:未审核 1:已审核, 2:已确认, 3:已入库....销售：0:未出库 1:已出库
		List<Orders> list = null;
		List<Orders> list01 = null;
		
		String orders_state01 = null;
		String orders_state02 = null;
		String od_state01 = null;
		
		//如果是采购退货
		if(Orders.TYPE_IN.equals(orders.getType())){
			//查询已确认订单
			orders_state01 = Orders.STATE_START;
			//查询已出库订单
			orders_state02 = Orders.STATE_END;
			
			//只需要已入库的 原订单详情
			od_state01 = Orderdetail.STATE_IN;
			
		}else if(Orders.TYPE_OUT.equals(orders.getType())){
		//如果是销售退货
			//查询未出库的订单
			orders_state01 = Orders.STATE_NOT_OUT;
			//查询已出库订单
			orders_state02 = Orders.STATE_OUT;
			
			//只需要已出库的 原订单详情
			od_state01 = Orderdetail.STATE_OUT;
			
		}
		
			
		orders.setState(orders_state01);
		//拿到已经确认过订单，因为已确认的订单中可以有一部分详情是已入库的
		list01 = ordersDao.searchWithPaging(orders, null, page, rows, "uuid");
		//循环遍历orders把没有已入库产品的订单剔除
		Iterator<Orders> iterator = list01.iterator();
		while(iterator.hasNext()){
			//作为判断订单中是否数量全部为0的依据
			boolean hasnum = false;
			Orders o = iterator.next();
			
			//装退货的商品和数量
			HashMap<Long, Long> rtnGoodsMap = null;
			//如果该原订单有退货订单
			if(o.getReturnorders() != null){
				//拿到该orders下所有的returnorders中的商品详情，放入rtnGoods中
				rtnGoodsMap = new HashMap<>();
				List<Returnorders> rtnList = o.getReturnorders();
				for (Returnorders returnorders : rtnList) {
					for(Returnorderdetail returnDetail : returnorders.getReturnorderdetails()){
						Long goodsuuid = returnDetail.getGoodsuuid();
						//因为再添加的时候就已经做盘判断保证 returnorderdetail中商品是唯一的
						rtnGoodsMap.put(goodsuuid, returnDetail.getNum());
					}
				}
			}else{
				//如果原订单没有退货订单，则把hasnum不用去判断删除
				hasnum = true;
				rtnGoodsMap = null;
			}
			
			boolean flag = false;
			
			//遍历该orders下的所有orderdetail
			for(Orderdetail od : o.getOrderDetails()){
				if(od_state01.equals(od.getState())){
					flag = true;
					//说明有退货订单
					if(rtnGoodsMap != null){
						Long goodsuuid = od.getGoodsuuid();
						//如果该 详情有与之有关的退货的详情
						if(rtnGoodsMap.containsKey(goodsuuid)){
							od.setNum(od.getNum()-rtnGoodsMap.get(goodsuuid));
						}
						if(od.getNum()!=0){
							hasnum = true;
						}
					}
				}
			}
			
			//如果该原订单中没有已入库的商品,则删除,如果原订单详情中所有商品都已经被退货，则删除
			if(flag == false || hasnum == false){
				iterator.remove();
			}
		}
		
		//拿到所有已入库的订单
		orders.setState(orders_state02);
		list = ordersDao.searchWithPaging(orders, null, page, rows, "uuid");
		ListIterator<Orders> iterator2 = list.listIterator();
//		for (Orders orders_in : list) {
		while(iterator2.hasNext()){
			Orders orders_in = iterator2.next();
			boolean hasNum = false;
		
			//装退货的商品和数量
			HashMap<Long, Long> rtnGoodsMap = null;
			//如果该订单下面有退货订单
			if(orders_in.getReturnorders() != null && orders_in.getReturnorders().size()>0){
				//拿到该orders下所有的returnorders中的商品详情，放入rtnGoods中
				rtnGoodsMap = new HashMap<>();
				List<Returnorders> rtnList = orders_in.getReturnorders();
				for (Returnorders returnorders : rtnList) {
					for(Returnorderdetail returnDetail : returnorders.getReturnorderdetails()){
						Long goodsuuid = returnDetail.getGoodsuuid();
						//因为再添加的时候就已经做盘判断保证 returnorderdetail中商品是唯一的
						rtnGoodsMap.put(goodsuuid, returnDetail.getNum());
					}
				}
				
				//遍历其下面的 详情 
				for(Orderdetail od : orders_in.getOrderDetails()){
					Long goodsuuid = od.getGoodsuuid();
					//如果有其详情有退货的详情，则减去其数量
					if(rtnGoodsMap.containsKey(goodsuuid)){
						od.setNum(od.getNum()-rtnGoodsMap.get(goodsuuid));
					}
					if(od.getNum()!=0){
						hasNum = true;
					}
				}
				if(hasNum == false){
					iterator2.remove();
				}
			}
		}
//		}
		
		//把两种订单合并
		list.addAll(list01);
			
		return list;
	}

}
