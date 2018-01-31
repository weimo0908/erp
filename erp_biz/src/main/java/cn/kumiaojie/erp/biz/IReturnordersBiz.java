package cn.kumiaojie.erp.biz;
import java.util.List;

import cn.kumiaojie.erp.entity.Orderdetail;
import cn.kumiaojie.erp.entity.Returnorders;
/**
 * 退货订单业务逻辑层接口
 * @author Administrator
 */
public interface IReturnordersBiz extends IBaseBiz<Returnorders>{

	
	/**
	 * 根据供应商编号获取所有的已经入库时采购的订单
	 * @param uuid
	 * @return
	 */
	List<Orderdetail> getInReturnOrderdetail(Long uuid);
	
	/**
	 * 根据供应商编号获取所有的已经入库时采购的订单
	 * @param uuid
	 * @return
	 */
	List<Orderdetail> getOutReturnOrderdetail(Long uuid);
	
	/**
	 * 退货订单添加
	 * @param o
	 * @param uuid
	 */
	void addIn(Returnorders o,Long uuid);
	/**
	 * 退货订单添加
	 * @param o
	 * @param uuid
	 */
	void addOut(Returnorders o,Long uuid);
	/**
	 * 退货订单审核
	 * @param id
	 */
	void doCheck(Long id,Long empUuid);
}

