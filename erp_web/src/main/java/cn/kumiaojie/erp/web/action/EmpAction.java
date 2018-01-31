package cn.kumiaojie.erp.web.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;

import cn.kumiaojie.erp.biz.IEmpBiz;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Tree;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.web.utils.BaseActionUtil;
import cn.kumiaojie.erp.web.utils.GetLoginUser;

/**
 * 部门Action
 * 
 * @author Stivechen
 *
 */
public class EmpAction extends BaseAction<Emp> {

	private IEmpBiz empBiz;

	public void setEmpBiz(IEmpBiz empBiz) {
		this.empBiz = empBiz;
		super.setBaseBiz(this.empBiz);
	}

	// 接收修改密码属性
	private String newPwd;// 旧密码
	private String oldPwd;// 新密码
	// 不需要接收uuid，因为用seesion中获取当前的用户的密码

	public String getNewPwd() {
		return newPwd;
	}
	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
	}
	public String getOldPwd() {
		return oldPwd;
	}
	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
	}

	//模糊检索模式remote
	private String q;
	
	public String getQ() {
		return q;
	}
	public void setQ(String q) {
		this.q = q;
	}
	
	//更新用户角色checkedStr
	private String checkedStr;
	public String getCheckedStr() {
		return checkedStr;
	}
	public void setCheckedStr(String checkedStr) {
		this.checkedStr = checkedStr;
	}
	
	// 文件上传
	private File file;// 上传文件
	private String fileFileName;// 文件名称
	private String fileContentType;// 上传文件类型

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getFileContentType() {
		return fileContentType;
	}

	public void setFileContentType(String fileContentType) {
		this.fileContentType = fileContentType;
	}
	
	
	/**
	 * 修改用户密码
	 */
	public void updatePwd(){
		//从session中获取登录的用户对象
		Emp loginUser = (Emp) GetLoginUser.getLoginUser();
		//判断是否存在
		if (null == loginUser) {
			BaseActionUtil.returnAjax(false, "您还没登录，请登录后再操作！");
			return;
		}
		try {
			//已经登录，传递修改密码
			this.empBiz.updatePwd(loginUser.getUuid(), oldPwd, newPwd);
			BaseActionUtil.returnAjax(true, "密码修改成功！");
		} catch(ERPException e1){
			//自定义异常
			e1.printStackTrace();
			BaseActionUtil.returnAjax(false, e1.getMessage());
		} catch (Exception e2) {
			//系统异常
			e2.printStackTrace();
			BaseActionUtil.returnAjax(false, "修改密码失败！");
		}
		
	}
	
	/**
	 * 重置员工密码
	 */
	public void updatePwd_reset(){
		
		try {
			//getId()从baseAction中调用
			empBiz.updatePwd_reset(getId(), newPwd);
			BaseActionUtil.returnAjax(true, "重置密码成功!");
		} catch (Exception e) {
			e.printStackTrace();
			BaseActionUtil.returnAjax(false, "重置密码失败!");
		}
		
	}
	
	/* 
	 * 自动检索
	 */
	@Override
	public void list() {
		if (null == getT1()) {
			setT1(new Emp());
		}
		//设置查询条件
		getT1().setName(q);
		super.list();
	}
	
	
	
	/**
	 * 读取用户角色
	 */
	public void readEmpRoles(){
		List<Tree> roles = empBiz.readEmpRoles(getId());
		BaseActionUtil.write(JSON.toJSONString(roles));
	}
	
	
	/**
	 * 更新用户角色
	 */
	public void updateEmpRoles(){
		try {
			empBiz.updateEmpRoles(getId(), checkedStr);
			BaseActionUtil.returnAjax(true, "更新成功!");
		} catch (Exception e) {
			BaseActionUtil.returnAjax(false, "更新失败!");
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取角色
	 */
	public void showRole() {
		// 获取主题
		Subject subject = SecurityUtils.getSubject();
		// 获取当前登录的用户
		Emp emp = (Emp) subject.getPrincipal();
		//用当前的用户获取角色树
		String empRoleNames = empBiz.getEmpRoleNames(emp.getUuid());
		if (null != empRoleNames) {
			BaseActionUtil.returnAjax(true, empRoleNames);
		}else {
			BaseActionUtil.returnAjax(false, "您还未设置角色,请联系部门主管");
		}
	}
	
	public void export(){
		String filename = "查询员工.xls";
		Emp login = (Emp) SecurityUtils.getSubject().getPrincipal();
		String loginUser = login.getName();
		HttpServletResponse response = ServletActionContext.getResponse();
		try {
			response.setHeader("Content-Disposition",  "attachment;filename=" + new String(filename.getBytes(),"ISO-8859-1"));		
				empBiz.doExport(response.getOutputStream(), getT1(),loginUser);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 上传数据问件
	 * @throws ParseException 
	 */
	public void doImport() throws ParseException{
		//判断文件类型
		if (!"application/vnd.ms-excel".equals(fileContentType)) {
			//文件类型不对
			BaseActionUtil.returnAjax(false, "导入的文件必须是excel文件!");
			return;
		}
		try {
			empBiz.doImport(new FileInputStream(file));
			BaseActionUtil.returnAjax(true, "导入数据成功!");
		} catch (ERPException e) {
			BaseActionUtil.returnAjax(false, e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			BaseActionUtil.returnAjax(false, "导入数据失败!");
			e.printStackTrace();
		}
	}
	
}
