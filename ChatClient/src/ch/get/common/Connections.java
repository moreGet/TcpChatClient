package ch.get.common;

import java.net.Socket;
import java.util.ArrayList;

public class Connections {

	private static ArrayList<Socket> CONNECTIONS;
	
	private Connections() {
		Connections.CONNECTIONS = new ArrayList<>();
	};
	
	public Connections getInstance() {
		return LazyHoloder.inst;
	}
	
	private static class LazyHoloder {
		private static final Connections inst = new Connections();
	}
	
	public static ArrayList<Socket> getConnections() {
		return CONNECTIONS;
	}
}
