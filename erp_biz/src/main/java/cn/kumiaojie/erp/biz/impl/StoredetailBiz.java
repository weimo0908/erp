package cn.kumiaojie.erp.biz.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import cn.kumiaojie.erp.biz.IStoredetailBiz;
import cn.kumiaojie.erp.dao.IGoodsDao;
import cn.kumiaojie.erp.dao.IStoreDao;
import cn.kumiaojie.erp.dao.IStoredetailDao;
import cn.kumiaojie.erp.entity.Goods;
import cn.kumiaojie.erp.entity.StoreAlert;
import cn.kumiaojie.erp.entity.Storedetail;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.utils.GetNameOrSaveUtil;
import cn.kumiaojie.erp.utils.MailUtil;

/**
 * StoredetailBiz实现类
 * 
 * @author Stivechen
 *
 */
public class StoredetailBiz extends BaseBiz<Storedetail> implements IStoredetailBiz {

	// 注入IStoredetailDao
	private IStoredetailDao storedetailDao;

	public void setStoredetailDao(IStoredetailDao storedetailDao) {
		this.storedetailDao = storedetailDao;
		// 继承父类的setBaseDao,并注入storedetail,让其具体的方法有具体的对象
		super.setBaseDao(this.storedetailDao);
	}

	// 注入IGoodsDao-->查询对应的商品名称
	private IGoodsDao goodsDao;

	// 注入IStoreDao
	private IStoreDao storeDao;

	public void setGoodsDao(IGoodsDao goodsDao) {
		this.goodsDao = goodsDao;
	}

	public void setStoreDao(IStoreDao storeDao) {
		this.storeDao = storeDao;
	}

	//发送邮件需要的属性
	private MailUtil mailUtil;
	/*private String to;
	private String subject;
	private String text;*/
	
	
	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}


	/**
	 * 重写分页方法(进行字段加工)
	 */
	public List<Storedetail> getListByPage(Storedetail t1, Storedetail t2, Object param, int firstResult, int maxResults) {
		//继承父类获取数据
		List<Storedetail> list = super.getListByPage(t1, t2, param, firstResult, maxResults);
		//进行加工
		Map<Long, String>goodsNameMap  = new HashMap<>();//商品名称集合	
		Map<Long, String>storeNameMap = new HashMap<>();//仓库名称集合
		for (Storedetail s : list) {
			s.setGoodsName(GetNameOrSaveUtil.getGoodsName(s.getGoodsuuid(), goodsNameMap, goodsDao));
			s.setStoreName(GetNameOrSaveUtil.getStoreName(s.getStoreuuid(), storeNameMap, storeDao));
		}
		
		return list;
	}

	/**
	 * 获取库存预警list
	 */
	@Override
	public List<StoreAlert> getStorealertList() {
		return storedetailDao.getStroealertList();
	}

	/**
	 * 发送邮件
	 * 
	 */
	@Override
	public void sendStorealertMail(String to ,String subject,String text) {
		//获取库存预警列表
		List<StoreAlert> list = storedetailDao.getStroealertList();
		//获取list的个数
		int size = null == list?0:list.size();
		//判断count
		if (size>0) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
				//对页面传输过来的数据加工:_Time:[time]	
				subject+="_Time:"+sdf.format(new Date());
				//仓库中已有[count]种商品需要进货了,请登录酷妙街ERP7.1系统查看
				text+=",酷妙街仓库中已有"+String.valueOf(size)+"种商品需要进货了,请登录酷妙街ERP7.1系统查看";
				//发送邮件
				mailUtil.sendMail(to, subject,text);
			} catch (MessagingException e) {
				e.printStackTrace();
				throw new ERPException("发用预警邮件失败!");
			}
		}else {
			throw new ERPException("没有需要发送预警邮件的商品");
		}
		
		
		
	}
	
	/**
	 * 找到该仓库中的商品
	 */
	@Override
	public List<Storedetail> findGoodsInStore(Long storeuuid) {
		Storedetail s1 = new Storedetail();
		s1.setStoreuuid(storeuuid);
//		List<Storedetail> list = storedetailDao.findAll(s1, null, null);
		List<Storedetail> list = storedetailDao.getList(s1, null, null);
		for (Storedetail storedetail : list) {
//			Goods goods = goodsDao.findById(storedetail.getGoodsuuid());
			Goods goods = goodsDao.get(storedetail.getGoodsuuid());
			storedetail.setGoodsName(goods.getName());
		}
		return list;
	}

}
