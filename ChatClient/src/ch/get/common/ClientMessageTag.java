package ch.get.common;

public enum ClientMessageTag {

	// CLIENT STATUS
	CLIENT_STAN_BY(0, "접속 전"),
	CLIENT_CONNECTED(1, "서버 접속"),
	
	// CLIENT REQUEST
	QUIT(1000, "QUIT");
	
	private int row;
	private String tag;
	
	private ClientMessageTag(int row, String tag) {
		this.row = row;
		this.tag = tag;
	}
	
	public int getRow() {
		return row;
	}
	
	public String getTag() {
		return tag;
	}
}
