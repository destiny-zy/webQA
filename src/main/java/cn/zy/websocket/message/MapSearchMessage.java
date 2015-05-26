package cn.zy.websocket.message;

/*
 * 添加mapid属性
 */
public class MapSearchMessage extends UserMessage {
	private String mapid;
	private String resultid;
	private String location;
	private String keyword;

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getResultid() {
		return resultid;
	}

	public void setResultid(String resultid) {
		this.resultid = resultid;
	}

	public String getMapid() {
		return mapid;
	}

	public void setMapid(String mapid) {
		this.mapid = mapid;
	}

	public MapSearchMessage(String type) {
		super(type);
	}

}
