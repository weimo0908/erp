package cn.kumiaojie.erp.biz.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.core.io.ClassPathResource;

import cn.kumiaojie.erp.biz.IEmpBiz;
import cn.kumiaojie.erp.dao.IDepDao;
import cn.kumiaojie.erp.dao.IEmpDao;
import cn.kumiaojie.erp.dao.IRoleDao;
import cn.kumiaojie.erp.entity.Dep;
import cn.kumiaojie.erp.entity.Emp;
import cn.kumiaojie.erp.entity.Role;
import cn.kumiaojie.erp.entity.Tree;
import cn.kumiaojie.erp.exception.ERPException;
import cn.kumiaojie.erp.utils.Md5Util;
import net.sf.jxls.transformer.XLSTransformer;
import redis.clients.jedis.Jedis;

/**
 * EmpBiz实现类
 * 
 * @author Stivechen
 *
 */
public class EmpBiz extends BaseBiz<Emp> implements IEmpBiz {

	// 注入IEmpDao
	private IEmpDao empDao;
	public void setEmpDao(IEmpDao empDao) {
		this.empDao = empDao;
		// 继承父类的setBaseDao,并注入emp,让其具体的方法有具体的对象
		super.setBaseDao(this.empDao);
	}

	// 注入roleDao
	private IRoleDao roleDao;

	public void setRoleDao(IRoleDao roleDao) {
		this.roleDao = roleDao;
	}

	//注入jedis
	private Jedis jedis;

	public void setJedis(Jedis jedis) {
		this.jedis = jedis;
	}
	
	//注入depDao
	private IDepDao depDao;
	public void setDepDao(IDepDao depDao) {
		this.depDao = depDao;
	}

	/**
	 * 重写add,新注册员工自动生成加密代码
	 */
	public void add(Emp emp) {
		// 对象中没有密码,则默认以名字作为密码
		String encryptByMd5 = Md5Util.encryptByMd5(emp.getUsername(), emp.getUsername());
		// 保存到对象中，覆盖原来的
		emp.setPwd(encryptByMd5);
		// 保存对象
		empDao.add(emp);
	}

	/**
	 * 登录校验
	 * 抽取了md5加密方法
	 */
	@Override
	public Emp findByUsernameAndPwd(String username, String pwd) {
		// 查询前对密码进行加密
		String encryptByMd5 = Md5Util.encryptByMd5(pwd, username);
		return empDao.findByUsernameAndPwd(username, encryptByMd5);
	}

	/**
	 * 更新密码
	 */
	@Override
	public void updatePwd(Long uuid, String oldPwd, String newPwd) {
		Emp emp = empDao.get(uuid);
		// 对原有密码进行核对
		String pwd = emp.getPwd();// 原有密码
		if (!Md5Util.encryptByMd5(oldPwd, emp.getUsername()).equals(pwd)) {
			// 不匹配
			throw new ERPException("原密码不正确");
		}
		// 正确后修改密码
		empDao.updatePwd(uuid, Md5Util.encryptByMd5(newPwd, emp.getUsername()));
	}

	/**
	 * 管理员重置密码
	 */
	@Override
	public void updatePwd_reset(Long uuid, String newPwd) {
		// 先查询
		Emp emp = empDao.get(uuid);
		// 加密后重置新的密码
		String encryptByMd5 = Md5Util.encryptByMd5(newPwd, emp.getUsername());
		// 更新
		empDao.updatePwd(uuid, encryptByMd5);
	}

	/**
	 * 读取用户角色
	 */
	@Override
	public List<Tree> readEmpRoles(Long uuid) {
		// 穿件角色树,加工后返回
		List<Tree> treeList = new ArrayList<Tree>();
		// 获取用户信息
		Emp emp = empDao.get(uuid);
		// 得到对应的角色list
		List<Role> empRoles = emp.getRoles();
		// 获取所有的角色
		List<Role> allRoles = roleDao.getList(null, null, null);
		// 创建单个Tree用来接收用户下的每个角色
		Tree t1 = null;
		for (Role r : allRoles) {
			t1 = new Tree();
			t1.setId(String.valueOf(r.getUuid()));//加入角色id
			t1.setText(r.getName());//加入角色名称
			//根据查询出来的用户角色判断是否有对应的,让其选择
			if (empRoles.contains(r)) {
				t1.setChecked(true);
			}
			//挂到角色大树上
			treeList.add(t1);
		}
		return treeList;
	}

	/**
	 * 更新用角色
	 * @param uuid 页面传入用户id
	 * @param checkedStr 页面传入被选中的角色String字段
	 */
	@Override
	public void updateEmpRoles(Long uuid, String checkedStr) {
		//获取用户信息
		Emp emp = empDao.get(uuid);
		//清空用户下原有的角色
		emp.setRoles(new ArrayList<Role>());
		//对checked进行去","处理
		String[] ids = checkedStr.split(",");
		//创建新的Role对象
		Role newRoles = null;
		for (String id : ids) {
			newRoles = roleDao.get(Long.valueOf(id));
			//设置用户的角色
			emp.getRoles().add(newRoles);
		}
		
		try {
			//更改用户角色后清除jedis中的缓存,让其重新加载最新的
			jedis.del("menuList_" + uuid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getEmpRoleNames(Long uuid) {
		//创建用户角色返回的StringBuilder
		StringBuilder sb = new StringBuilder();
		//获取用户对象
		Emp emp = empDao.get(uuid);
		//获取用户的角色
		List<Role> empRoles = emp.getRoles();
		//获取所有的角色
		List<Role> allRoles = roleDao.getList(null, null, null);
		//循环遍历
		for(Role r : allRoles){
			if (empRoles.contains(r)) {
				//如果有.则加入
				sb.append(r.getName());
				sb.append("  ");
			}
		}
		return sb.toString().trim();
	}
	
	public void doExport(OutputStream os,Emp t1,String userName ){
		List<Emp> empList = empDao.getList(t1, null, null);
		//显示性别名称
		Map<Long,String> genderNameMap = new HashMap<Long,String>();
		genderNameMap.put((long) 1, "男");
		genderNameMap.put((long) 2, "女");
		//显示部门名称
		Map<Long,String> depNameMap = new HashMap<Long,String>();
		for (Emp emp : empList) {
			depNameMap.put(emp.getUuid(), emp.getDep().getName());
		}
		//显示数据
		Map<String,Object> dataMap = new HashMap<String,Object>();
		//日期格式
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		//导出当前时间和导出人
		Date date = new Date();
		String dateNow = sdf.format(date);
		dataMap.put("date", dateNow);
		dataMap.put("userName", userName);
		dataMap.put("emp", empList);
		dataMap.put("depName",depNameMap);
		dataMap.put("sdf", sdf);
		dataMap.put("gender", genderNameMap);
		 HSSFWorkbook wk = null;
		 
	        try {
	            wk = new HSSFWorkbook(new ClassPathResource("export_emp.xls").getInputStream());
	            XLSTransformer transformer = new XLSTransformer();
	           
	            transformer.transformWorkbook(wk, dataMap);
	            wk.write(os);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }finally{
	            if(null != wk){
	                try {
	                    wk.close();
	                } catch (IOException e) {
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
	            }
	        }
	
	}
	
	public void doImport(InputStream is) throws IOException, ParseException{
		HSSFWorkbook wb =null;
		//将字符串性别,转换成gender
		Map<String,Long> genderMap = new HashMap<String,Long>();
		genderMap.put("男", (long) 1);
		genderMap.put("女", (long) 2);
		try {
			wb = new HSSFWorkbook(is);
			HSSFSheet sheet = wb.getSheetAt(0);
			//读取数据
			//读取最后一行行号
			int lastRow = sheet.getLastRowNum();
			Emp emp = null;
			for (int i = 2; i <= lastRow; i++) {
				emp = new Emp();
				//通过员工姓名来判断员工是否存在
				emp.setName(sheet.getRow(i).getCell(2).getStringCellValue());				
				List<Emp> list = empDao.getList(null,emp,null);
				if (list.size()>0) {
					emp = list.get(0);
				}
				emp.setUsername(sheet.getRow(i).getCell(1).getStringCellValue());
				emp.setName(sheet.getRow(i).getCell(2).getStringCellValue());
				emp.setGender(genderMap.get(sheet.getRow(i).getCell(3).getStringCellValue()));
				emp.setEmail(sheet.getRow(i).getCell(4).getStringCellValue());
				//根据格式选择不同方法
				try {
					emp.setTele(sheet.getRow(i).getCell(5).getStringCellValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					emp.setTele(Math.round(sheet.getRow(i).getCell(5).getNumericCellValue())+"");
					
				}
				emp.setAddress(sheet.getRow(i).getCell(6).getStringCellValue());
				
				//判断不同日期格式,不同添加方法
				try {
					emp.setBirthday(sheet.getRow(i).getCell(7).getDateCellValue());
				} catch (Exception e) {
					emp.setBirthday(format(sheet.getRow(i).getCell(7).getStringCellValue()));
				}
				//通过部门名称获取部门
				String depName =sheet.getRow(i).getCell(8).getStringCellValue();
				if (depName != null || depName.trim().length() > 0) {
					Dep dep = new Dep();
					dep.setName(sheet.getRow(i).getCell(8).getStringCellValue());
					List<Dep> result = depDao.getList(dep, null, null);
					if (result.size() > 0) {
						emp.setDep(result.get(0));
					}else{
						emp.setDep(null);
					}					
				}
				//如果用户不存在则添加
				if (list.size() == 0) {
					empDao.add(emp);
				}
			}		
		}finally{
			if(null != wb){
				try {
					wb.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
		
	//时间格式转换成Date
	private static Date format(String str) throws ParseException  {
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
	    System.out.println(str);
	    Date result = sdf.parse(str);
	    return result;
	    
	}
	
	
	
	
	
}
