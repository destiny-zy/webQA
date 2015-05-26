package cn.zy.websocket.message;

/*
 * 添加mapid属性
 */
public class MapTransitMessage extends UserMessage {
	private String mapid;
	private String resultid;
	private String home;
	private String destination;

	public String getHome() {
		return home;
	}

	public void setHome(String home) {
		this.home = home;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
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

	public MapTransitMessage(String type) {
		super(type);
	}

}
