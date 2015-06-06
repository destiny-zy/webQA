package webQA;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import cn.zy.util.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Testapi {

	@Test
	public void ss() {
		try {
			String ss = "http://ltpapi.voicecloud.cn/analysis/?api_key=F1u4I3u1X315O1i0T2Z6TD1GPgAXftcCdVVHHJvg&pattern=all&text=南开大学到北京大学&format=json";
			// String res =
			// Request.Get(ss).execute().returnContent().asString();
			String res = Utils.requestApi(ss);
			System.out.println(res);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(res);
			Iterator<JsonNode> ite = root.get(0).get(0).elements();

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
