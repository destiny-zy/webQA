package webQA;

import org.junit.Test;

import cn.zy.util.Utils;

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
}
