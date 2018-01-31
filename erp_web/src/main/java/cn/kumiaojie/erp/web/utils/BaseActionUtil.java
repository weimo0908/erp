package cn.kumiaojie.erp.web.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;

import com.alibaba.fastjson.JSON;

public class BaseActionUtil {

	/**
	 * 增加前缀方法
	 * 
	 * @param jsonString
	 * @param prefix
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String mapJson(String jsonString, String prefix) {
		//反向json--->map
		Map<String, Object> oMap = JSON.parseObject(jsonString);
		//增加前缀后的nMap
		Map<String, Object> nMap = new HashMap<>();
		// 遍历旧的集合,添加前缀
		for (String key : oMap.keySet()) {
			// 判断当前值是否是实体类,是展开加工,不是跳过
			if (oMap.get(key) instanceof Map) {
				// 是map集合
				Map<String, Object> m2 = (Map<String, Object>) oMap.get(key);
				for (String key2 : m2.keySet()) {
					//加上两层前缀
					nMap.put(prefix + "." + key + "." + key2, m2.get(key2));
				}
			} else {
				nMap.put(prefix + "." + key, oMap.get(key));
			}
		}
		// 返回转换成新的字符串
		return JSON.toJSONString(nMap);
	}

	/**
	 * 返回提示消息通用方法
	 * 
	 * @param success
	 * @param message
	 */
	public static void returnAjax(Boolean success, String message) {
		Map<String, Object> rtn = new HashMap<>();
		rtn.put("success", success);
		rtn.put("message", message);
		write(JSON.toJSONString(rtn));
	}

	/**
	 * json转码
	 * 
	 * @param jsonString
	 */
	public static void write(String jsonString) {
		HttpServletResponse response = ServletActionContext.getResponse();
		response.setContentType("text/html;charset=utf-8");
		try {
			response.getWriter().write(jsonString);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
