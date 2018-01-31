package cn.kumiaojie.erp.utils;

import java.util.Map;

import cn.kumiaojie.erp.dao.IEmpDao;
import cn.kumiaojie.erp.dao.IGoodsDao;
import cn.kumiaojie.erp.dao.IStoreDao;
import cn.kumiaojie.erp.dao.ISupplierDao;

/**
 * 缓存名称或从缓存中读取工具类
 * @author Stivechen
 */
public class GetNameOrSaveUtil {

	/**
	 * 获取商品名称
	 * @param uuid 商品id
	 * @param goodsNameMap 传入商品名称集合
	 * @param goodsDao 
	 * @return goodsName 商品名字
	 */
	public static String getGoodsName(Long uuid, Map<Long, String> goodsNameMap, IGoodsDao goodsDao) {
		if (null == uuid) {
			return null;
		}
		String goodsName = goodsNameMap.get(uuid);
		if (null == goodsName) {
			goodsName = goodsDao.get(uuid).getName();
			goodsNameMap.put(uuid, goodsName);
		}
		return goodsName;
	}

	
	/**
	 * 获取仓库名称
	 * @param uuid 仓库id
	 * @param storeNameMap 传入仓库名称集合
	 * @param storeDao
	 * @return storeName 商品名称
	 */
	public static String getStoreName(Long uuid, Map<Long, String> storeNameMap, IStoreDao storeDao) {
		if (null == uuid) {
			return null;
		}
		String storeName = storeNameMap.get(uuid);
		if (null == storeName) {
			storeName = storeDao.get(uuid).getName();
			storeNameMap.put(uuid, storeName);
		}
		return storeName;
	}

	/**
	 * 获员工姓名
	 * @param uuid 员工id
	 * @param empNameMap 传入员工名字集合
	 * @param empDao
	 * @return empName 员工名称
	 */
	public static String getEmpName(Long uuid, Map<Long, String> empNameMap, IEmpDao empDao) {
		if (null == uuid) {
			return null;
		}
		// 从缓存中根据员工编号取出员工名称
		String empName = empNameMap.get(uuid);
		if (null == empName) {
			// 如果没有找员工的名称，则进行数据库查询
			empName = empDao.get(uuid).getName();
			// 存入缓存中
			empNameMap.put(uuid, empName);
		}
		return empName;
	}

	/**
	 * 获取供应商/客户名字
	 * @param uuid 供应商/客户id
	 * @param nameMap//传入供应商/客户名字集合
	 * @return supplierName 供应商/客户姓名
	 */
	public static String getSupplierName(Long uuid, Map<Long, String> supplierNameMap, ISupplierDao supplierDao) {
		// 先判断uuid是否为空
		if (null == uuid) {
			return null;
		}
		// 先从nameMap从获取
		String supplierName = supplierNameMap.get(uuid);
		if (null == supplierName) {
			// 没有值--->调用方法查询
			supplierName = supplierDao.get(uuid).getName();
			// 在缓存中保存一份
			supplierNameMap.put(uuid, supplierName);
		}
		return supplierName;
	}

}
