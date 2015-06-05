package cn.zy.websocket;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import cn.zy.entity.User;
import cn.zy.util.Utils;
import cn.zy.websocket.message.MapSearchMessage;
import cn.zy.websocket.message.MapTransitMessage;
import cn.zy.websocket.message.UserListMessage;
import cn.zy.websocket.message.UserMessage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WebsocketHandler extends AbstractWebSocketHandler {
	private static Logger log = LoggerFactory.getLogger(WebsocketHandler.class);
	private static final ConcurrentHashMap<User, WebSocketSession> lists = new ConcurrentHashMap<User, WebSocketSession>();
	private static final AtomicInteger count = new AtomicInteger();
	private static final AtomicInteger mapidCount = new AtomicInteger();
	User user;
	String home = "";
	String destination = "";

	@Override
	public void afterConnectionEstablished(WebSocketSession session)
			throws Exception {
		log.info("ok连接上");
	}

	@Override
	protected void handleTextMessage(WebSocketSession session, TextMessage m) {
		try {
			String rec = m.getPayload();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rec);
			String message = "";
			switch (rootNode.get("type").asText()) {
			case "join":
				String username = rootNode.get("username").asText();
				user = new User();
				user.setUsername(username);
				user.setUserid(count.incrementAndGet());
				lists.put(user, session);
				// 发送用户加入消息
				sendToAll(getRobotMessage("欢迎用户" + username + "加入！"));
				// 发送用户在线列表
				sendUserList();
				break;
			case "chatToOne":
				// 如果这个用户在线就发送,如果@的自己直接返回
				String target = rootNode.get("target").asText();
				if (user.getUsername().equals(target))
					return;
				User u = hasUser(target);
				if (u != null) {
					message = getUserMessage(user, "@" + target + ":"
							+ rootNode.get("word").asText());
					sendToOne(lists.get(u), message);
				} else {
					message = getRobotMessage("该用户当前不在线");
					sendToOne(session, message);
				}
				break;
			case "chatToAll":
				message = getUserMessage(user, rootNode.get("word").asText());
				sendToAllExceptThis(message, session);
				break;
			case "select":
				String word = rootNode.get("word").asText();
				String pre = "@" + user.getUsername() + ":";
				sendToAllExceptThis(getUserMessage(user, "@机器人:" + word),
						session);
				if (!destination.equals("")) {
					String res = Utils.requestApi(Utils.getUrl(word));
					JsonNode root = mapper.readTree(res);
					Iterator<JsonNode> ite = root.path(0).path(0).elements();
					while (ite.hasNext()) {
						JsonNode next = ite.next();
						if (next.path("ne").asText().contains("Ni")
								|| next.path("ne").asText().contains("Ns")
								|| next.path("ne").asText().contains("Nh")
								|| (next.path("cont").asText().contains("路") && next
										.path("pos").asText().equals("n"))) {
							home += next.path("cont").asText();
						}
					}
					if (!home.equals("")) {
						sendToAll(getMapTransitMessage(
								home,
								destination,
								pre
										+ "从"
										+ home
										+ "到"
										+ destination
										+ "的<a onclick=\"gongjiao()\">公交</a>路线如下,您也可以选择<a onclick=\"buxing()\">步行</a>或者<a onclick=\"jiache()\">驾车</a>路线。"));
						destination = "";
						home = "";
					} else {
						sendToAll(getRobotMessage(pre
								+ "sorry,不明白您说的意思,请告诉我正确的地理位置……"));
					}

					// 包含去到等关键字
				} else if (word.contains("去") || word.contains("到")) {
					Integer quid = 0;
					String res = Utils.requestApi(Utils.getUrl(word));
					JsonNode root = mapper.readTree(res);
					Iterator<JsonNode> ite = root.path(0).path(0).elements();
					// 记录ID
					while (ite.hasNext()) {
						JsonNode next = ite.next();
						if (next.path("cont").asText().equals("去")
								|| next.path("cont").asText().equals("到")) {
							quid = next.path("id").asInt();
						}
					}
					ite = root.path(0).path(0).elements();
					while (ite.hasNext()) {
						JsonNode next = ite.next();
						if (next.path("id").asInt() < quid) {
							if (next.path("ne").asText().contains("Ni")
									|| next.path("ne").asText().contains("Ns")
									|| next.path("ne").asText().contains("Nh")
									|| (next.path("cont").asText()
											.contains("路") && next.path("pos")
											.asText().equals("n"))) {
								home += next.path("cont").asText();
							}
						}

						if (next.path("id").asInt() > quid) {
							if (next.path("ne").asText().contains("Ni")
									|| next.path("ne").asText().contains("Ns")
									|| next.path("ne").asText().contains("Nh")
									|| (next.path("cont").asText()
											.contains("路") && next.path("pos")
											.asText().equals("n"))) {
								destination += next.path("cont").asText();
							}
						}
					}

					if (home.equals("") && !destination.equals("")) {
						sendToAll(getRobotMessage(pre + "您是要去" + destination
								+ "吗？请告诉我您现在的地址，我将告诉您如何到达目的地"));
					} else if (home.equals("") && destination.equals("")) {
						sendToAll(getRobotMessage(pre
								+ "sorry,不明白您说的意思,请告诉我正确的地理位置……"));
					} else {
						sendToAll(getMapTransitMessage(
								home,
								destination,
								pre
										+ "从"
										+ home
										+ "到"
										+ destination
										+ "的<a onclick=\"gongjiao()\">公交</a>路线如下,您也可以选择<a onclick=\"buxing()\">步行</a>或者<a onclick=\"jiache()\">驾车</a>路线。"));
						home = "";
						destination = "";
					}
				} else if (destination.equals("") && home.equals("")
						&& !isSearchNearby(word)) {
					String res = Utils.requestApi(Utils.getUrl(word));
					JsonNode root = mapper.readTree(res);
					Iterator<JsonNode> ite = root.path(0).path(0).elements();
					while (ite.hasNext()) {
						JsonNode next = ite.next();
						if (next.path("ne").asText().contains("Ni")
								|| next.path("ne").asText().contains("Ns")
								|| next.path("ne").asText().contains("Nh")
								|| (next.path("cont").asText().contains("路") && next
										.path("pos").asText().equals("n"))) {
							destination += next.path("cont").asText();
						}
					}
					if (!destination.equals(""))
						sendToAll(getRobotMessage(pre + "您是要去" + destination
								+ "吗？请告诉我您现在的地址，我将告诉您如何到达目的地"));
					else {
						sendToAll(getRobotMessage(pre
								+ "sorry,不明白您说的意思,请问您要去哪里？……"));
					}

				} else if (isSearchNearby(word)) {
					String res = Utils.requestApi(Utils.getUrl(word));
					JsonNode root = mapper.readTree(res);
					Iterator<JsonNode> ite = root.path(0).path(0).elements();
					String location = "";
					String keyword = "";
					while (ite.hasNext()) {
						JsonNode next = ite.next();
						System.out.println(next.path("ne").asText());
						System.out.println(next.path("pos").asText());
						if (next.path("ne").asText().contains("Ni")
								|| next.path("ne").asText().contains("Ns")) {
							location += next.path("cont").asText();
						}
						if (next.path("ne").asText().equals("O")
								&& next.path("pos").asText().equals("n")) {
							keyword = next.path("cont").asText();
						}
					}
					if (location.equals("")) {
						sendToAll(getRobotMessage(pre
								+ "sorry,不明白您说的意思,请告诉我正确的地理位置……"));
					} else {
						sendToAll(getMapSearchMessage(location, keyword, pre
								+ location + "附近的" + keyword));
					}
					// 其余信息
				} else {
					sendToAll(getRobotMessage(pre + "sorry,不明白您说的意思,请问您要去哪里？……"));
				}

				break;
			case "avatar":
				String avatar = rootNode.get("avatar").asText();
				ByteArrayInputStream in = new ByteArrayInputStream(
						avatar.getBytes());
				BufferedOutputStream out = new BufferedOutputStream(
						new FileOutputStream("d:\\sockimg\\a.jpg"));
				byte[] buf = new byte[1024];
				while (in.read(buf) != -1) {
					out.write(buf);
				}
				in.close();
				out.close();
				break;
			default:
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private User hasUser(String target) {
		for (User user : lists.keySet()) {
			if (user.getUsername().equals(target)) {
				return user;
			}
		}
		return null;
	}

	private void sendToAllExceptThis(String to, WebSocketSession session) {
		try {
			for (Entry<User, WebSocketSession> entry : lists.entrySet()) {
				WebSocketSession s = entry.getValue();
				if (s.isOpen() && !s.equals(session))
					s.sendMessage(new TextMessage(to));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void sendToOne(WebSocketSession session, String message)
			throws IOException {
		session.sendMessage(new TextMessage(message));
	}

	private String getUserMessage(User user, String content)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		UserMessage m = new UserMessage("chat");
		m.setUsername(user.getUsername());
		m.setDate(new Date());
		m.setAvatarsrc("/assets/admin/layout/img/avatar3.jpg");
		m.setContent(content);
		return mapper.writeValueAsString(m);
	}

	private String getMapTransitMessage(String home, String destination,
			String content) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		MapTransitMessage m = new MapTransitMessage("maptransit");
		m.setAvatarsrc("/assets/admin/layout/img/avatar1.jpg");
		m.setDate(new Date());
		m.setHome(home);
		m.setDestination(destination);
		m.setUsername("机器人");
		m.setResultid("resultid" + mapidCount.incrementAndGet());
		m.setMapid(user.getUsername() + mapidCount.incrementAndGet());
		m.setContent(content);
		return mapper.writeValueAsString(m);
	}

	private String getMapSearchMessage(String location, String keyword,
			String content) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		MapSearchMessage m = new MapSearchMessage("mapsearch");
		m.setAvatarsrc("/assets/admin/layout/img/avatar1.jpg");
		m.setDate(new Date());
		m.setLocation(location);
		m.setKeyword(keyword);
		m.setUsername("机器人");
		m.setResultid("resultid" + mapidCount.incrementAndGet());
		m.setMapid(user.getUsername() + mapidCount.incrementAndGet());
		m.setContent(content);
		return mapper.writeValueAsString(m);
	}

	private String getRobotMessage(String content)
			throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		UserMessage m = new UserMessage("chat");
		m.setUsername("机器人");
		m.setDate(new Date());
		m.setAvatarsrc("/assets/admin/layout/img/avatar1.jpg");
		m.setContent(content);
		return mapper.writeValueAsString(m);
	}

	private void sendUserList() throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		UserListMessage m = new UserListMessage("userlist");
		List<User> list = m.getUsers();
		for (Entry<User, WebSocketSession> entry : lists.entrySet()) {
			list.add(entry.getKey());
		}
		sendToAll(mapper.writeValueAsString(m));
	}

	private void sendToAll(String to) throws IOException {
		for (Entry<User, WebSocketSession> entry : lists.entrySet()) {
			WebSocketSession s = entry.getValue();
			if (s.isOpen())
				s.sendMessage(new TextMessage(to));
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
		lists.remove(user);
		// 发送离开消息
		sendToAll(getRobotMessage("用户" + user.getUsername() + "离开了！"));
		sendUserList();
		log.info(user.getUsername() + "离开了");
	}

	private Boolean isSearchNearby(String word) {
		if (word.contains("附近") || word.contains("周围") || word.contains("周边")) {
			return true;
		}
		return false;
	}
}
