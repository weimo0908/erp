package cn.kumiaojie.erp.utils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * 发送邮件工具类
 * @author Stivechen
 *
 */
public class MailUtil {
	private JavaMailSender javaMailSender;// 交给spring 发送
	private String from;// 交给spring配置发件人


	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public void sendMail(String to, String subject, String text) throws MessagingException {
		// 1.创建邮件信息
		MimeMessage message = javaMailSender.createMimeMessage();
		// 2.使用spring 邮件工具类
		MimeMessageHelper helper = new MimeMessageHelper(message);
		// 3.收件人
		helper.setTo(to);
		// 4.发件人
		helper.setFrom(from);
		// 5.设置邮件的标题
		helper.setSubject(subject);
		// 6.邮件的内容
		helper.setText(text);
		// 7.发送邮件
		javaMailSender.send(message);
	}

}
