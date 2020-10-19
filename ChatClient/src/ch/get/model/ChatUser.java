package ch.get.model;

import java.net.Socket;

public class ChatUser {

	private String name;
	private Socket socket;

	// 필수 인자
	public ChatUser(String name, Socket socket) {
		this.name = name;
		this.socket = socket;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Socket getSocket() {
		return socket;
	}
	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
//	private ChatUser() {}
//	
//	public static class Builder {
//		private String name;
//		private Socket socket;
//		
//		public Builder setName(String name) {
//			this.name = name;
//			return this;
//		}
//		
//		public Builder setSocket(Socket socket) {
//			this.socket = socket;
//			return this;
//		}
//		
//		public ChatUser build() {
//			ChatUser chatUser = new ChatUser();
//			chatUser.name = name;
//			chatUser.socket = socket;
//			return chatUser;
//		}
//	}
}