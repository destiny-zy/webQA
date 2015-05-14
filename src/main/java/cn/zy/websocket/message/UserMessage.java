package cn.zy.websocket.message;

public class UserMessage extends Message {

	private String userName;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public UserMessage(String type) {
		super(type);
	}

}
