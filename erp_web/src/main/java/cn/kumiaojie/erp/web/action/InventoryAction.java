package cn.kumiaojie.erp.web.action;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.shiro.SecurityUtils;

import com.alibaba.fastjson.JSON;

import cn.kumiaojie.erp.biz.IEmpBiz;
import cn.kumiaojie.erp.biz.IGoodsBiz;
import cn.kumiaojie.erp.biz.IInventoryBiz;
import cn.kumiaojie.erp.biz.IStoreBiz;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Goods;
import cn.kumiaojie.erp.entity.Inventory;
import cn.kumiaojie.erp.entity.Store;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;

/**
 * 盘盈盘亏Action 
 * @author Administrator
 *
 */
public class InventoryAction extends BaseAction<Inventory> {

	private IInventoryBiz inventoryBiz;
	private IGoodsBiz goodsBiz;
	private IStoreBiz storeBiz;
	private IEmpBiz empBiz;
	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
	}
	public void setInventoryBiz(IInventoryBiz inventoryBiz) {
		this.inventoryBiz = inventoryBiz;
		super.setBaseBiz(this.inventoryBiz);
	}
	public void setGoodsBiz(IGoodsBiz goodsBiz) {
		this.goodsBiz = goodsBiz;
	}
	public void setStoreBiz(IStoreBiz storeBiz) {
		this.storeBiz = storeBiz;
	}

	@Override
	public void add() {
		Emp emp = (Emp)SecurityUtils.getSubject().getPrincipal();
		Inventory inventory = getT();
		inventory.setCreatetime(new Date());
		inventory.setCreater(emp.getUuid());
		inventory.setState(Inventory.STATE_UNCHEKCKED);
		try {
			inventoryBiz.add(inventory);
			BaseActionUtil.returnAjax(true, "登记成功");
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "登记成功");
			e.printStackTrace();
		}
	}
	
	@Override
	public void update() {
		try {
			inventoryBiz.update(getT());
			BaseActionUtil.returnAjax(true, "修改成功");
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "修改失败");
			e.printStackTrace();
		}
	}
	
	public void search() {
		HashMap<Serializable, String> goodsMap = new HashMap<>();
		HashMap<Serializable, String> storeMap = new HashMap<>();
		HashMap<Serializable, String> empMap = new HashMap<>();
		List<Inventory> list = inventoryBiz.searchWithPaging(getT1(), getT2(), getPage(), getRows(), "uuid");
		for (Inventory inventory : list) {
			inventory.setGoodsName(findGoodsName(inventory.getGoodsuuid(),goodsMap));
			inventory.setStoreName(findStoreName(inventory.getStoreuuid(), storeMap));
			inventory.setCreaterName(findEmpName(inventory.getCreater(), empMap));
			inventory.setCheckerName(findEmpName(inventory.getChecker(), empMap));
		}
		HashMap<String, Object> map = new HashMap<>();
//		Long total = inventoryBiz.countWithPaging(getT1(), getT2(), null);
		Long total = inventoryBiz.getCount(getT1(), getT2(), null);
		map.put("total", total);
		map.put("rows", list);
		BaseActionUtil.write(JSON.toJSONString(map));
//		this.write(map);
	}
	
	public void doCheck(){
		try {
			Long uuid = getId();
			Emp emp = (Emp)SecurityUtils.getSubject().getPrincipal();
			inventoryBiz.doCheck(emp.getUuid(),uuid);
			BaseActionUtil.returnAjax(true, "审核成功");
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "审核失败");
			e.printStackTrace();
		}
	}
	
	private String findGoodsName(Long uuid,Map<Serializable, String> map){
		String name = null;
		//先判断有没有uuid
		if(uuid!= null) {
			if(map.containsKey(uuid)){
				name = map.get(uuid);
			}else{
				//如果map缓存里没有，就去查
//				Goods goods = goodsBiz.findById(uuid);
				Goods goods = goodsBiz.get(uuid);
				if(goods != null){
					name = goods.getName();
					map.put(uuid, name);
				}
			}
		}
		return name;
	}
	
	private String findStoreName(Long uuid,Map<Serializable, String> map){
		String name = null;
		//先判断有没有uuid
		if(uuid!= null) {
			if(map.containsKey(uuid)){
				name = map.get(uuid);
			}else{
				//如果map缓存里没有，就去查
//				Store store = storeBiz.findById(uuid);
				Store store = storeBiz.get(uuid);
				if(store != null){
					name = store.getName();
					map.put(uuid, name);
				}
			}
		}
		return name;
	}
	
	//如果不传入map的话， 在括号里循环一次，map就没了，这缓存相当于没用
	private String findEmpName(Long uuid,Map<Serializable, String> map){
		String name = null;
		//先判断有没有uuid
		if(uuid!= null) {
			if(map.containsKey(uuid)){
				name = map.get(uuid);
			}else{
				//如果map缓存里没有，就去查
//				Emp emp = empBiz.findById(uuid);
				Emp emp = empBiz.get(uuid);
				if(emp != null){
					name = emp.getName();
					map.put(uuid, name);
				}
			}
		}
		return name;
	}
	
	public void getYearList(){
		Long storeuuid = getId();
		ArrayList<Map<String,Object>> yearList = new ArrayList<>();
		List<Map<String,Object>> list = inventoryBiz.getYearList(storeuuid);
		
		BaseActionUtil.write(JSON.toJSONString(list));
	}
}
