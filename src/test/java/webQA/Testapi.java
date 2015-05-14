package webQA;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import cn.zy.util.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Testapi {

	@Test
	public void ss() {
		try {
			String ss = "http://v.juhe.cn/chengyu/query?key=b8b928a2b236bb27dd6716c3e2b094b9&dtype=json&word=天涯海角";
			// String res =
			// Request.Get(ss).execute().returnContent().asString();
			String res = Utils.requestApi(ss);
			System.out.println(res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void s1() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> map = new HashMap<String, String>();
		map.put("type", "log");
		map.put("message", "用户:加入！");
		System.out.println(mapper.writeValueAsString(map));

	}
}
