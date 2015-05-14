package cn.zy.websocket.message;

import java.util.ArrayList;
import java.util.List;

public class UserListMessage extends Message {

	private List<String> users = new ArrayList<String>();

	public List<String> getUsers() {
		return users;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public UserListMessage(String type) {
		super(type);
	}
}
