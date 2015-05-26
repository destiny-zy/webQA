package cn.zy.websocket.message;

import java.util.ArrayList;
import java.util.List;

import cn.zy.entity.User;

public class UserListMessage extends Message {

	private List<User> users = new ArrayList<User>();

	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public UserListMessage(String type) {
		super(type);
	}
}
