package cn.zy.websocket.message;

public class ChatMessage extends Message {

	private String content;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ChatMessage(String type) {
		super(type);
	}

}
