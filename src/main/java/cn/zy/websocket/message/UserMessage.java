package cn.zy.websocket.message;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class UserMessage extends Message {

	private String username;
	private String avatarsrc;
	private Date date;
	private String content;

	public String getAvatarsrc() {
		return avatarsrc;
	}

	public void setAvatarsrc(String avatarsrc) {
		this.avatarsrc = avatarsrc;
	}

	@JsonFormat(pattern = "HH:mm:ss", timezone = "GMT+8")
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public UserMessage(String type) {
		super(type);
	}

}
