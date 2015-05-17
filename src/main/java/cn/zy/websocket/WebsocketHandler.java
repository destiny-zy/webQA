package cn.zy.websocket;

import java.io.IOException;
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
import cn.zy.websocket.message.ChatMessage;
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
		case "chatToOne":
			ChatMessage chat = new ChatMessage("chat");
			ChatMessage chatself = new ChatMessage("chat");
			String target = rootNode.get("target").asText();
			String content = rootNode.get("word").asText();
			if (lists.containsKey(target)) {
				chat.setContent("用户" + username + "对你说：" + content);
				chatself.setContent("你对" + target + "说：" + content);
				sendToOne(session, mapper.writeValueAsString(chatself));
				sendToOne(lists.get(target), mapper.writeValueAsString(chat));
			} else {
				chat.setContent("该用户当前不在线");
				sendToOne(session, mapper.writeValueAsString(chat));
			}
			break;
		case "chatToAll":
			ChatMessage chat1 = new ChatMessage("chat");
			chat1.setContent("用户" + username + "对所有人说："
					+ rootNode.get("word").asText());
			sendToAll(mapper.writeValueAsString(chat1));
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
				String pre = "@" + username + ":";
				JsonNode node = mapper.readTree(res);
				if (node.get("reason").asText().equals("success")) {
					selectMessage.setResult(pre
							+ node.get("result").get("chengyujs").asText());
				} else {
					selectMessage.setResult(pre + node.get("reason").asText());
				}
			}
			sendToAll(mapper.writeValueAsString(selectMessage));
			break;

		default:
			break;
		}

	}

	private void sendToOne(WebSocketSession session, String message) {
		try {
			session.sendMessage(new TextMessage(message));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
