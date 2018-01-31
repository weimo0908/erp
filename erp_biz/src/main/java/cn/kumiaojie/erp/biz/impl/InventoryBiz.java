package cn.kumiaojie.erp.biz.impl;
import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.kumiaojie.erp.biz.IInventoryBiz;
import cn.kumiaojie.erp.dao.IInventoryDao;
import cn.kumiaojie.erp.dao.IStoredetailDao;
import cn.kumiaojie.erp.dao.IStoreoperDao;
import cn.kumiaojie.erp.entity.Inventory;
import cn.kumiaojie.erp.entity.Storedetail;
import cn.kumiaojie.erp.entity.Storeoper;
/**
 * 盘盈盘亏业务逻辑类
 * @author Administrator
 *
 */
public class InventoryBiz extends BaseBiz<Inventory> implements IInventoryBiz {

	private IInventoryDao inventoryDao;
	private IStoreoperDao storeoperDao;
	private IStoredetailDao storedetailDao;
	public void setInventoryDao(IInventoryDao inventoryDao) {
		this.inventoryDao = inventoryDao;
		super.setBaseDao(this.inventoryDao);
	}
	public void setStoreoperDao(IStoreoperDao storeoperDao) {
		this.storeoperDao = storeoperDao;
	}

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
	}
	
	/**
	 * 
	 * @param empuuid
	 * @param inventoryID
	 * @throws Exception
	 */
	@Override
	public void doCheck(Long empuuid, Long inventoryID) throws Exception {
//		Inventory inventory = inventoryDao.findById(inventoryID);
		Inventory inventory = inventoryDao.get(inventoryID);
		inventory.setChecker(empuuid);
		inventory.setChecktime(new Date());
		inventory.setState(Inventory.STATE_CHECKED);
		
		//更新数据库
		Storedetail storedetail = new Storedetail();
		storedetail.setStoreuuid(inventory.getStoreuuid());
		storedetail.setGoodsuuid(inventory.getGoodsuuid());
//		List<Storedetail> list = storedetailDao.findAll(storedetail, null, null);
		List<Storedetail> list = storedetailDao.getList(storedetail, null, null);
		storedetail = list.get(0);
		Long inStoreNum = storedetail.getNum();
		
		//更新storeoper
		Storeoper storeoper = new Storeoper();
		storeoper.setEmpuuid(empuuid);
		storeoper.setGoodsuuid(inventory.getGoodsuuid());
		storeoper.setOpertime(new Date());
		storeoper.setStoreuuid(inventory.getStoreuuid());
		Long num = inventory.getNum();
		storeoper.setNum(num);
		String type = inventory.getType();
		if(Inventory.TYPE_PROFIT.equals(type)){
			storeoper.setType(Storeoper.TYPE_IN);
			storedetail.setNum(inStoreNum+num);
		}else{
			storeoper.setType(Storeoper.TYPE_OUT);
			storedetail.setNum(inStoreNum-num);
		}
		storeoperDao.add(storeoper);
	}
	
	@Override
	public void update(Inventory inventory) throws Exception {
//		Inventory oldInventory = inventoryDao.findById(inventory.getUuid());
		Inventory oldInventory = inventoryDao.get(inventory.getUuid());
		oldInventory.setStoreuuid(inventory.getStoreuuid());
		oldInventory.setGoodsuuid(inventory.getGoodsuuid());
		oldInventory.setNum(inventory.getNum());
		oldInventory.setType(inventory.getType());
		oldInventory.setRemark(inventory.getRemark());
	}
	
	@Override
	public List<Map<String,Object>> getYearList(Long storeuuid) {
		return inventoryDao.getYearList(storeuuid);
	}
	
	
	
	
}
