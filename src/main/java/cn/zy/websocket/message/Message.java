package cn.zy.websocket.message;

/*
 * 消息基类
 * 消息类型:type
 */
public class Message {

	private String type;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Message(String type) {
		super();
		this.type = type;
	}

}
