package cn.zy.websocket.message;

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
