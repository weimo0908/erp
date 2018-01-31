package cn.kumiaojie.erp.job;

import cn.kumiaojie.erp.biz.IStoredetailBiz;

/**
 * 自动发送邮件
 * @author Stivechen
 *
 */
public class MailJob {
	//注入IStoredetailBiz

	private IStoredetailBiz storedetailBiz;

	public void setStoredetailBiz(IStoredetailBiz storedetailBiz) {
		this.storedetailBiz = storedetailBiz;
	}
	
	//自动发送邮件必须属性
	private String to;
	private String subject;
	private String text;
	public void setTo(String to) {
		this.to = to;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public void setText(String text) {
		this.text = text;
	}


	/**
	 * 发送预警邮件调用的方法
	 * 定时发送以默认的方式
	 * 
	 */
	public void sendStorealertMail(){
		try {
			storedetailBiz.sendStorealertMail(to, subject, text);
			System.out.println("发送邮件!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
