package cn.kumiaojie.erp.biz;

import java.util.List;

import cn.kumiaojie.erp.entity.StoreAlert;
import cn.kumiaojie.erp.entity.Storedetail;

/**
 * 仓库库存表的业务抽象类
 * @author Stivechen
 *
 */
public interface IStoredetailBiz extends IBaseBiz<Storedetail>{	
	
	/**
	 * 库存预警
	 * @return
	 */
	public List<StoreAlert> getStorealertList();
	
	
	/**
	 * 发送邮件业务接口
	 * @param to 邮件地址
	 * @param subject 邮件标题
	 * @param text 邮件正文
	 * @throws Exception
	 */
	public void sendStorealertMail(String to ,String subject,String text) throws Exception;
	
	/**
	 * 传入store的uuid，返回该仓库中的所有商品，并查出其名字
	 * @return
	 */
	List<Storedetail> findGoodsInStore(Long storeuuid);
	
}
