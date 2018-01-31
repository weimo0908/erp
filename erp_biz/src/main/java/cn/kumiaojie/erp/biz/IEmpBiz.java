package cn.kumiaojie.erp.biz;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.List;

import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Tree;

public interface IEmpBiz extends IBaseBiz<Emp>{

	/**
	 * 用户登录确认
	 * @param username
	 * @param pwd
	 * @return
	 */
	public Emp findByUsernameAndPwd(String username,String pwd);
	
	/**
	 * 更新密码
	 */
	public void updatePwd(Long uuid , String oldPwd,String newPwd);
	
	/**
	 * 管理员重置密码
	 * 不需要知道原有密码,只有管理员才能修改
	 */
	public void updatePwd_reset(Long uuid,String newPwd);
	
	/**
	 * 读取用户角色
	 * @param uuid
	 * @return
	 */
	List<Tree> readEmpRoles(Long uuid);
	
	/**
	 * 更新用户角色
	 * @param uuid
	 * @param checkedStr
	 */
	void updateEmpRoles(Long uuid ,String checkedStr);
	
	/**
	 * 获取用户的角色名称
	 * @param uuid 用户uuid
	 * @return
	 */
	String getEmpRoleNames(Long uuid);
	
	/**
	 * 员工数据导出
	 * @param os
	 * @param t1
	 */
	public void doExport(OutputStream os,Emp t1 ,String userName);
	/**
	 * 员工数据导入
	 * @param is
	 * @throws IOException
	 * @throws ParseException
	 */
	public void doImport(InputStream is)throws IOException, ParseException ;
	
}
