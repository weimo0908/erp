package cn.kumiaojie.erp.biz.impl;
import java.util.Date;
import java.util.List;

import cn.kumiaojie.erp.biz.IReturnorderdetailBiz;
import cn.kumiaojie.erp.dao.IReturnorderdetailDao;
import cn.kumiaojie.erp.dao.impl.OrdersDao;
import cn.kumiaojie.erp.dao.impl.ReturnordersDao;
import cn.kumiaojie.erp.dao.impl.StoreoperDao;
import cn.kumiaojie.erp.entity.Returnorderdetail;
import cn.kumiaojie.erp.entity.Returnorders;
import cn.kumiaojie.erp.entity.Storeoper;
/**
 * 退货订单明细业务逻辑类
 * @author Administrator
 *
 */
public class ReturnorderdetailBiz extends BaseBiz<Returnorderdetail> implements IReturnorderdetailBiz {

	private IReturnorderdetailDao returnorderdetailDao;
	private StoreoperDao storeoperDao;
	private OrdersDao ordersDao;
	private ReturnordersDao returnordersDao;
	
	
	


	
	public void setStoreoperDao(StoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}
	public void setOrdersDao(OrdersDao ordersDao) {
		this.ordersDao = ordersDao;
	}
	public void setReturnordersDao(ReturnordersDao returnordersDao) {
		this.returnordersDao = returnordersDao;
	}
	public void setReturnorderdetailDao(IReturnorderdetailDao returnorderdetailDao) {
		this.returnorderdetailDao = returnorderdetailDao;
		super.setBaseDao(returnorderdetailDao);
	}
	/**
	 * 退货详细订单出库
	 */
	@Override
	public void doOutStore(Returnorderdetail returnorderdetail, Long uuid) {
		
		//更新退货订单详情状态
		returnorderdetail.setEnder(uuid);
		returnorderdetail.setEndtime(new Date());;
		returnorderdetail.setState(Returnorderdetail.STATE_OUT);
		//新建库存记录对象,添加记录
		Storeoper storeoper = new Storeoper();
		storeoper.setEmpuuid(uuid);
		storeoper.setGoodsuuid(returnorderdetail.getGoodsuuid());
		storeoper.setNum(returnorderdetail.getNum());
		storeoper.setType(Returnorders.TYPE_OUT);
		storeoper.setStoreuuid(returnorderdetail.getStoreuuid());
		storeoper.setOpertime(new Date());
		storeoperDao.add(storeoper);
		returnorderdetailDao.update(returnorderdetail);
		
		
		Returnorders returnorders2 = returnorderdetail.getReturnorders();
		List<Returnorderdetail> list2 = returnorders2.getReturnorderdetails();
		boolean flag = true;
		//判断退货订单详情是否已经全都出库
		for (Returnorderdetail rorderdetail : list2) {
			if(rorderdetail.getState().equals("1")){
				flag = false;
			}
		}
		//若都出库,则将订单详情状态改变为出库,设置出库人和时间
		if(flag){
			returnorders2.setState(Returnorders.STATE_OUT);
			returnorders2.setEnder(uuid);
			returnorders2.setEndtime(new Date());
			}
		}
	/**
	 * 退货详细订单入库
	 */
	public void doInStore(Returnorderdetail returnorderdetail, Long uuid){
		returnorderdetail.setEnder(uuid);
		returnorderdetail.setEndtime(new Date());
		returnorderdetail.setState(Returnorderdetail.STATE_IN);
		//新建库存记录对象,添加记录
		Storeoper storeoper = new Storeoper();
		storeoper.setEmpuuid(uuid);
		storeoper.setGoodsuuid(returnorderdetail.getGoodsuuid());
		storeoper.setNum(returnorderdetail.getNum());
		storeoper.setType(Returnorders.TYPE_IN);
		storeoper.setStoreuuid(returnorderdetail.getStoreuuid());
		storeoper.setOpertime(new Date());
		storeoperDao.add(storeoper);
		returnorderdetailDao.update(returnorderdetail);
		
		
		Returnorders returnorders2 = returnorderdetail.getReturnorders();
		boolean flag = true;
		//判断退货订单详情是否已经都入库
		for (Returnorderdetail rorderdetail : returnorders2.getReturnorderdetails()) {
			if(rorderdetail.getState().equals("1")){
				flag = false;
			}
		}
		//若都出库,则将订单详情状态改变为出库,设置出库人和时间
		if(flag){
				returnorders2.setState(Returnorders.STATE_IN);
				returnorders2.setEnder(uuid);
				returnorders2.setEndtime(new Date());
		}
	}
	
}
