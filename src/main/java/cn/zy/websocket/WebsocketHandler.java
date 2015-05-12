package cn.zy.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import cn.zy.util.Utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebsocketHandler extends TextWebSocketHandler {
	private static Logger log = LoggerFactory.getLogger(WebsocketHandler.class);

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		log.info("ok连接上");

	}

	@Override
	protected void handleTextMessage(WebSocketSession session,
			TextMessage message) throws Exception {
		String word = message.getPayload();
		String url = "http://v.juhe.cn/chengyu/query?key=" + Utils.appkey
				+ "&dtype=json&word=" + word;
		log.info(url);
		String res = Utils.requestApi(url);
		log.info(res);
		if (res != null) {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node = mapper.readTree(res);
			if (node.get("reason").asText().equals("success")) {
				session.sendMessage(new TextMessage(node.get("result")
						.get("chengyujs").asText()));
			} else {
				session.sendMessage(new TextMessage(node.get("reason").asText()));
			}
		}

	}

	@Override
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		session.close(CloseStatus.SERVER_ERROR);
	}

}
