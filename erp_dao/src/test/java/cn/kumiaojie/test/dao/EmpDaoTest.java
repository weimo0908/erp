package cn.kumiaojie.test.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.kumiaojie.erp.dao.IEmpDao;
import cn.kumiaojie.erp.entity.Emp;

public class EmpDaoTest {

	/**
	 * 成功测试
	 */
	@Test
	public void Test01(){
		ApplicationContext ac =new ClassPathXmlApplicationContext("classpath*:applicationContext_*.xml");
		IEmpDao empDao = (IEmpDao) ac.getBean("empDao");
		Emp emp = empDao.get(2L);
		System.out.println(emp.getName());
	}
}
