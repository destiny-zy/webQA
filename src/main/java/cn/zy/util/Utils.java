package cn.zy.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * request api
 * 
 * @author zy
 *
 */
public class Utils {
	public static final String appkey = "F1u4I3u1X315O1i0T2Z6TD1GPgAXftcCdVVHHJvg";

	public static String requestApi(String url) {
		String res = null;
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(url);
		get.addHeader("Content-Type", "text/html:charset=UTF-8");
		get.addHeader(
				"User-Agent",
				"Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET4.0E; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET4.0C");
		try {
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (response.getStatusLine().getStatusCode() == 200) {
				res = EntityUtils.toString(entity);
			}

			client.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	public static String getUrl(String word) {
		return "http://ltpapi.voicecloud.cn/analysis/?api_key=" + appkey
				+ "&pattern=all&text=" + word + "&format=json";
	}
}
