package cn.kumiaojie.erp.web.action;

import java.util.List;

import javax.mail.MessagingException;

import com.alibaba.fastjson.JSON;

import cn.kumiaojie.erp.biz.IStoredetailBiz;
import cn.kumiaojie.erp.entity.StoreAlert;
import cn.kumiaojie.erp.entity.Storedetail;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;

/**
 * 商品Action
 * 
 * @author Stivechen
 *
 */
public class StoredetailAction extends BaseAction<Storedetail> {

	private IStoredetailBiz storedetailBiz;

	public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
		this.storedetailBiz = storedetailBiz;
		super.setBaseBiz(this.storedetailBiz);
	}

	// 发送库存预警邮件注入属性
	private String to;// 地址
	private String subject;// 标题
	private String text;// 正文

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	/**
	 * 获取库库存预警列表
	 */
	public void storealertList() {
		List<StoreAlert> storealertList = storedetailBiz.getStorealertList();
		BaseActionUtil.write(JSON.toJSONString(storealertList));
	}

	public void sendStorealertMail() {
		try {
			// 发送预警邮件
			storedetailBiz.sendStorealertMail(to, subject, text);
			BaseActionUtil.returnAjax(true, "库存预警邮件发送成功!");
		} catch (MessagingException m) {
			BaseActionUtil.returnAjax(false, "预警邮件构建失败!");
			m.printStackTrace();
		} catch (ERPException e1) {
			BaseActionUtil.returnAjax(false, e1.getMessage());
			e1.printStackTrace();
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "库存预警邮件发送失败!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 找到该仓库下的所有商品
	 */
	public void findGoodsInStore(){
		Long storeuuid = getId();
		List<Storedetail> list = storedetailBiz.findGoodsInStore(storeuuid);
		
		BaseActionUtil.write(JSON.toJSONString(list));
//		this.write(list);
	}

}
