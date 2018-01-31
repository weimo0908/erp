package cn.kumiaojie.erp.dao;

import cn.kumiaojie.erp.entity.Emp;

/**
 * 员工数据访问接口
 * 
 * @author Stivechen
 *
 */
public interface IEmpDao extends IBaseDao<Emp> {

	/**
	 * 用户登录校验
	 * 
	 * @param username
	 * @param pwd
	 * @return
	 */
	public Emp findByUsernameAndPwd(String username, String pwd);

	/**
	 * 更新密码
	 * 
	 * @param uuid
	 * @param newPwd
	 */
	public void updatePwd(Long uuid, String newPwd);

}
