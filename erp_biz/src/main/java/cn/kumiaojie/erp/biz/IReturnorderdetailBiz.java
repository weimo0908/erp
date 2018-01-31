package cn.kumiaojie.erp.biz;
import cn.kumiaojie.erp.entity.Returnorderdetail;
/**
 * 退货订单明细业务逻辑层接口
 * @author Administrator
 *
 */
public interface IReturnorderdetailBiz extends IBaseBiz<Returnorderdetail>{
	/**
	 * 采购订单退货出库
	 * @param returnorderdetail 退货订单详情
	 * @param uuid 用户登录id
	 */
	void doOutStore(Returnorderdetail returnorderdetail, Long uuid);
	
	/**
	 * 销售订单退货入库
	 * @param returnorderdetail  退货订单详情
	 * @param uuid  用户登录id
	 */
	void doInStore(Returnorderdetail returnorderdetail, Long uuid);
}

