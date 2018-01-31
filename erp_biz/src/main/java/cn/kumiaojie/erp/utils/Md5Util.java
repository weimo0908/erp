package cn.kumiaojie.erp.utils;

import org.apache.shiro.crypto.hash.Md5Hash;

public class Md5Util {
	// 散列次数
	private static final int HASHITERATIONS = 2;

	/**
	 * 传入密码，和用户名进行加密
	 * @param salt 扰乱码（用户名）
	 * @param source 原密码
	 * @return md5.toString()
	 */
	public static String encryptByMd5(String source,String salt ) {
		Md5Hash md5 = new Md5Hash(source, salt, HASHITERATIONS);
		return md5.toString();
	}
}
