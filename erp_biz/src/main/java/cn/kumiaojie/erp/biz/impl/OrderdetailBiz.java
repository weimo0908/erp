package cn.kumiaojie.erp.biz.impl;

import java.util.Date;
import java.util.List;

import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.redsun.bos.ws.impl.IWaybillWs;

import cn.kumiaojie.erp.biz.IOrderdetailBiz;
import cn.kumiaojie.erp.dao.IOrderdetailDao;
import cn.kumiaojie.erp.dao.IStoredetailDao;
import cn.kumiaojie.erp.dao.IStoreoperDao;
import cn.kumiaojie.erp.dao.ISupplierDao;
import cn.kumiaojie.erp.entity.Orderdetail;
import cn.kumiaojie.erp.entity.Orders;
import cn.kumiaojie.erp.entity.Storedetail;
import cn.kumiaojie.erp.entity.Storeoper;
import cn.kumiaojie.erp.entity.Supplier;
import cn.kumiaojie.erp.exception.ERPException;

/**
 * OrderdetailBiz实现类
 * 
 * @author Stivechen
 *
 */
public class OrderdetailBiz extends BaseBiz<Orderdetail> implements IOrderdetailBiz {

	// 注入IOrderdetailDao
	private IOrderdetailDao orderdetailDao;

	public void setOrderdetailDao(IOrderdetailDao orderdetailDao) {
		this.orderdetailDao = orderdetailDao;
		// 继承父类的setBaseDao,并注入orderdetail,让其具体的方法有具体的对象
		super.setBaseDao(this.orderdetailDao);
	}

	// 注入StoredetailDao仓库库存
	private IStoredetailDao storedetailDao;
	// 注入StoreoperDao仓库库存记录
	private IStoreoperDao storeoperDao;

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}

	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}

	// 注入物流查询IWaybillWs
	private IWaybillWs waybillWs;
	private ISupplierDao supplierDao;

	public void setWaybillWs(IWaybillWs waybillWs) {
		this.waybillWs = waybillWs;
	}

	public void setSupplierDao(ISupplierDao supplierDao) {
		this.supplierDao = supplierDao;
	}

	/**
	 * 入库操作
	 * 需要参数uuid:订单编号
	 * storeuuid:仓库编号
	 * empuuid:库管员编号
	 */
	@Override
	@RequiresPermissions("采购订单入库")
	public void doInStore(Long uuid, Long storeuuid, Long empUuid) {

		/* =========第一步:更新订单详情========= */
		// 1.从数据库中获取订单详情
		Orderdetail odetail = orderdetailDao.get(uuid);
		// 2.判断是否是未入库的(未确认的不会显示在这里,静态页面传参已经过滤掉)
		if (!Orderdetail.STATE_NOT_IN.equals(odetail.getState())) {
			throw new ERPException("对不起,不能重复入库!");
		}
		// 判断是否是确认的
		Orders isStartOrders = odetail.getOrders();
		if (!Orders.STATE_START.equals(isStartOrders.getState())) {
			throw new ERPException("对不起,订单还未确认!");
		}
		// 3.修改订单详情状态
		odetail.setState(Orderdetail.STATE_IN);
		// 4.更改入库时间
		odetail.setEndtime(new Date());
		// 5.更新库管员
		odetail.setEnder(empUuid);
		// 6.设置仓库
		odetail.setStoreuuid(storeuuid);

		/* =========第二步:更新仓库库存详情========= */
		/* 思路:先构建条件,查询仓库是否有同样的商品(同一个storeUuid,和同一个goodsUuid) */
		Storedetail queryParam = new Storedetail();
		queryParam.setGoodsuuid(odetail.getGoodsuuid());// 从订单详情中拿到商品id
		queryParam.setStoreuuid(storeuuid);// 仓库编号
		// 查询仓库库存表数据,最后需要得到仓库库存对象,进行操作,所以用geilist
		List<Storedetail> list = storedetailDao.getList(queryParam, null, null);
		// 得到的是一个list集合,判断是否为空
		if (list.size() > 0) {
			// 有则累加
			long num = 0;
			if (null != list.get(0).getNum()) {
				// 有记录
				num = list.get(0).getNum().longValue();
			}
			list.get(0).setNum(num + odetail.getNum());// 加上原来的
			// 自动保存了
		} else {
			// 为空--->没有则创建
			queryParam.setNum(odetail.getNum());
			// 保存对象
			storedetailDao.add(queryParam);
		}

		/* =========第三步:添加仓库操作记录========= */
		Storeoper newLog = new Storeoper();
		// 加入库管员id
		newLog.setEmpuuid(empUuid);
		// 仓库编号
		newLog.setStoreuuid(storeuuid);
		// 加入操作时间
		newLog.setOpertime(odetail.getEndtime());
		// 记录商品编号
		newLog.setGoodsuuid(odetail.getGoodsuuid());
		// 商品数量
		newLog.setNum(odetail.getNum());
		// 操作行为:入库
		newLog.setType(Storeoper.STORE_IN);
		// 最后保存记录
		storeoperDao.add(newLog);// 因为没有从数据库查过,所以不会自动更新,要手动更新

		/* =========第四步:是否更新订单状态(完成还是进行中)========= */
		// 1.先判断全部订单明细项是否已经入库(必须是全部入库之后)
		Orderdetail queryDetail = new Orderdetail();// 新建一个对象,构建查询条件
		// 得到正在操作的订单对象
		Orders orders = odetail.getOrders();
		// 给查询条件赋订单对象(需要确定是哪个订单下的)
		queryDetail.setOrders(orders);
		// 设置查询条件要查询的订单详情状态
		queryDetail.setState(Orderdetail.STATE_NOT_IN);// 未入库
		// 调用count的方法查询,不需要拿到具体的对象
		long count = orderdetailDao.getCount(queryDetail, null, null);
		if (count == 0) {
			// 所有的都已经入库:更新订单状态,关闭订单
			// 设置订单状态
			orders.setState(Orders.STATE_END);
			// 设置完成时间
			orders.setEndtime(odetail.getEndtime());
			// 设置库管员
			orders.setEnder(empUuid);
		}

	}

	
	/**
	 * 销售操作
	 * 需要参数uuid:订单编号
	 * storeuuid:仓库编号
	 * empuuid:库管员编号
	 */
	@Override
	@RequiresPermissions("销售订单出库")
	public void doOutStore(Long uuid, Long storeuuid, Long empUuid) {

		/* =========第一步更新商品明细========= */
		// 1,获取商品明细
		Orderdetail odetail = orderdetailDao.get(uuid);
		// 2,判断订单状态,一定是要未出库的
		if (!Orderdetail.STATE_NOT_OUT.equals(odetail.getState())) {
			throw new ERPException("对不起,该订单明细已经出库");
		}
		// 3,修改订单状态
		odetail.setState(Orderdetail.STATE_OUT);// 出库
		odetail.setEndtime(new Date());// 设置出库日期
		odetail.setEnder(empUuid);// 设置出库员
		odetail.setStoreuuid(storeuuid);// 哪个仓库
		

		/* =========第二步更新库存========= */
		// 1,构建查询条件:storeuuid,goodsuuid(查询仓库有是否有)
		Storedetail queryParam = new Storedetail();
		queryParam.setGoodsuuid(odetail.getGoodsuuid());
		queryParam.setStoreuuid(storeuuid);
		// 2,先查询库存中有没有
		List<Storedetail> list = storedetailDao.getList(queryParam, null, null);
		// 3,判断是否有
		if (list.size() > 0) {
			// 有就取出对象,然后跟销售数量相减,
			Storedetail storedetail = list.get(0);
			Long num = storedetail.getNum() - odetail.getNum();
			if (num < 0) {
				throw new ERPException("该商品库存不足,请先进货");
			}
			// 有库存,再保存
			storedetail.setNum(num);
		} else {
			throw new ERPException("仓库中无该商品!");
		}

		/* =========第三步更新库存变更记录(只有上面两步骤都正确,才会进行)========= */
		Storeoper newLog = new Storeoper();
		newLog.setEmpuuid(empUuid);// 出库员
		newLog.setGoodsuuid(odetail.getGoodsuuid());
		newLog.setNum(odetail.getNum());
		newLog.setOpertime(odetail.getEndtime());
		newLog.setStoreuuid(storeuuid);
		newLog.setType(Storeoper.STORE_OUT);
		// 保存数据
		storeoperDao.add(newLog);

		/* =========第第四步是否更新销售订单的状态(判断为主,只有订单详情都已经出库)========= */
		// 1,构建查询条件(订单编号,订单详情状态)
		Orderdetail queryDetail = new Orderdetail();
		queryDetail.setState(Orderdetail.STATE_NOT_OUT);// 未出库
		Orders orders = odetail.getOrders();
		queryDetail.setOrders(orders);// 订单编号
		// 2,查询得到数量
		long count = orderdetailDao.getCount(queryDetail, null, null);
		if (count == 0) {
			// 3已经全部出库,修改订状态
			orders.setEnder(empUuid);// 出库员
			orders.setEndtime(odetail.getEndtime());// 结束时间
			orders.setState(Orders.STATE_OUT);

			/*//查询订单发送的客户信息
			/*Supplier supplier = supplierDao.get(orders.getSupplieruuid());
			//在线预约下单,获取订单号,填充信息
			//在全部都出库的情况下,再次构建查询订单详情条件
			Orderdetail qp = new Orderdetail();
			qp.setOrders(orders);
			List<Orderdetail> list2  = orderdetailDao.getList(qp, null, null);
			//设置运单详情缓存
			StringBuilder info = new StringBuilder();
			//遍历添加到info中
			for (Orderdetail orderdetail : list2) {
				//设置运单详情info
				info.append(orderdetail.getGoodsname()+" ");
			}*/
//			System.out.println("详情"+info.toString());
			//第一个参数是下单公司编号,在此默认用1表示,剩余参数分别是地址,客户名称,联系电话,订单说明.
			/*Long waybillSn = waybillWs.addWaybill(1L, supplier.getAddress(), supplier.getContact(), supplier.getTele(), info.toString().trim());
			//清空info缓存
			info.delete(0, info.length());
			//更新运单号
			orders.setWaybillsn(waybillSn);*/
			
		}
	}

}
