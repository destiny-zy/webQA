package cn.zy.websocket.message;

public class SelectMessage extends Message {

	private String result;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public SelectMessage(String type) {
		super(type);
	}
}
