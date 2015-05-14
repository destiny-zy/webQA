package cn.zy.websocket;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import cn.zy.util.Utils;
import cn.zy.websocket.message.SelectMessage;
import cn.zy.websocket.message.UserListMessage;
import cn.zy.websocket.message.UserMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebsocketHandler extends TextWebSocketHandler {
	private static Logger log = LoggerFactory.getLogger(WebsocketHandler.class);
	private static final ConcurrentHashMap<String, WebSocketSession> lists = new ConcurrentHashMap<String, WebSocketSession>();
	String username = null;

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		log.info("ok连接上");
	}

	@Override
	protected void handleTextMessage(WebSocketSession session,
			TextMessage message) throws Exception {
		String rec = message.getPayload();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rec);
		switch (rootNode.get("type").asText()) {
		case "join":
			username = rootNode.get("username").asText();
			UserMessage m = new UserMessage("join");
			m.setUserName(username);
			lists.put(username, session);
			// 发送用户加入消息
			sendToAll(mapper.writeValueAsString(m));
			// 发送用户在线列表
			SendUserList();
			break;
		case "select":
			String word = rootNode.get("word").asText();
			String url = "http://v.juhe.cn/chengyu/query?key=" + Utils.appkey
					+ "&dtype=json&word=" + word;
			log.info(url);
			String res = Utils.requestApi(url);
			SelectMessage selectMessage = new SelectMessage("select");
			log.info(res);
			if (res != null) {
				JsonNode node = mapper.readTree(res);
				if (node.get("reason").asText().equals("success")) {
					selectMessage.setResult(node.get("result").get("chengyujs")
							.asText());
				} else {
					selectMessage.setResult(node.get("reason").asText());
				}
			}
			sendToAll(mapper.writeValueAsString(selectMessage));
			break;

		default:
			break;
		}

	}

	private void SendUserList() throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		UserListMessage m = new UserListMessage("userlist");
		List<String> list = m.getUsers();
		for (Entry<String, WebSocketSession> entry : lists.entrySet()) {
			list.add(entry.getKey());
		}
		sendToAll(mapper.writeValueAsString(m));
	}

	private void sendToAll(String to) {
		try {
			for (Entry<String, WebSocketSession> entry : lists.entrySet()) {
				WebSocketSession s = entry.getValue();
				if (s.isOpen())
					s.sendMessage(new TextMessage(to));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handleTransportError(WebSocketSession session,
			Throwable exception) throws Exception {
		session.close(CloseStatus.SERVER_ERROR);
	}

	@Override
	public void afterConnectionClosed(WebSocketSession session,
			CloseStatus status) throws Exception {
		lists.remove(username);
		ObjectMapper mapper = new ObjectMapper();
		UserMessage m = new UserMessage("leave");
		m.setUserName(username);
		// 发送离开消息
		sendToAll(mapper.writeValueAsString(m));
		SendUserList();
		log.info(username + "离开了");
	}
}
