package cn.kumiaojie.test.dao;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.kumiaojie.erp.dao.IGoodstypeDao;
import cn.kumiaojie.erp.entity.Goodstype;

public class GoodstypeDaoTest {

	/**
	 * 成功测试
	 */
	@Test
	public void Test01(){
		ApplicationContext ac =new ClassPathXmlApplicationContext("classpath*:applicationContext_*.xml");
		IGoodstypeDao goodstypeDao = (IGoodstypeDao) ac.getBean("goodstypeDao");
		Goodstype goodstype= goodstypeDao.get(1L);
		System.out.println(goodstype.getName());
	}
}
