package cn.kumiaojie.erp.biz;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import com.redsun.bos.ws.Waybilldetail;

import cn.kumiaojie.erp.entity.Orders;

/**
 * 商品的业务抽象类
 * @author Stivechen
 *
 */
public interface IOrdersBiz extends IBaseBiz<Orders> {
	// 增加doCheck
	public void doCheck(Long uuid, Long empUuid);

	// 增加doStart
	public void doStart(Long uuid, Long empUuid);

	/**
	 * 导出订单
	 * @param os
	 * @param uuid
	 */
	public void export(OutputStream os, Long uuid);

	/**
	 * 根据运单号查询运单信息
	 * @param sn
	 * @return
	 */
	public List<Waybilldetail> waybilldetailList(Long sn);

	/**
	 * 传入，type，supplieruuid
	 * @param orders
	 * @return
	 */
	public Map<String,Object> findOrdersToReturn(Orders orders,Integer page,Integer rows);
}
