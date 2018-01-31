package cn.kumiaojie.erp.exception;

/**
 * 自定义异常，用来处理Biz层出现的异常
 * @author Stivechen
 *
 */
public class ERPException extends RuntimeException{
	
	public ERPException(String massage){
		super(massage);
	}
	
}
