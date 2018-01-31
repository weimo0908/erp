package cn.kumiaojie.erp.biz;

import cn.kumiaojie.erp.entity.Orderdetail;

/**
 * 商品的业务抽象类
 * @author Stivechen
 *
 */
public interface IOrderdetailBiz extends IBaseBiz<Orderdetail> {

	/**
	 * 入库操作
	 * @param uuid订单详情uuid
	 * @param storeUuid仓库uuid
	 * @param empUuid库管员uuid
	 */
	public void doInStore(Long uuid, Long storeUuid, Long empUuid);

	/**
	 * 销售出库操作
	 * @param uuid订单详情uuid
	 * @param storeUuid仓库uuid
	 * @param empUuid库管员uuid
	 */
	public void doOutStore(Long uuid, Long storeUuid, Long empUuid);

}
