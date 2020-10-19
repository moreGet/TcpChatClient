package ch.get.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import ch.get.common.ClientMessageTag;
import ch.get.view.RootLayoutController;
import javafx.application.Platform;

public class ClientImpl implements Client {
	
	private Socket socket;
	private int serverPort = 10000;
	
	private ClientImpl() {}
	
	private static class LazyHolder {
		private static final ClientImpl inst = new ClientImpl();
	}
	
	public static ClientImpl getInstance() {
		return LazyHolder.inst;
	}
	
	@Override
	public void connectToServer() {
		// 서버 접속
		Thread thread = new Thread(() -> {
			try {
				socket = new Socket(); // 소켓 생성
				socket.connect(new InetSocketAddress("localhost", serverPort), 5000); // 접속 5초 타임아웃
				
				if (socket.isConnected()) { // 연결이 되었 다면 닉네임 전송
//					Connections.getConnections().add(socket); // socket List에 적재
					OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
					BufferedWriter bw = new BufferedWriter(osw);
					
					bw.write("TEST001");
					bw.flush();
					bw.close(); // 스트림 닫기	
				}
			} catch (Exception e) {	
				RootLayoutController.getInstance().printText("[ 서버 접속 불가 ]");				
				disconnectFromServer("연결 요청 중단"); // 에러 상황 에서는 소켓 닫기
//				e.printStackTrace();
				return;
			}
		});
		
		thread.start(); // 서버 접속 스레드 생성
	}

	@Override
	public void disconnectFromServer(String msg) {
		if (!socket.isClosed() && socket != null) {
			try {
				socket.close();
				Platform.runLater(() -> {
					RootLayoutController.getInstance().printText("[ " + msg + " ]");
				});
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				RootLayoutController.getInstance().getConnectBtn().setDisable(false); // 연결 종료후 버튼 활성화
			}
		}
	}

	@Override
	public void dataSender(String data) {		
		Thread thread = new Thread(() -> {
			try {
				// 스트림 받아오기
				OutputStreamWriter osw = null;
				BufferedWriter bw = null;
				osw = new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")); // 못 받아 올 경우
				bw = new BufferedWriter(osw);
				bw.write(data);
				bw.flush();
				
				RootLayoutController.getInstance().printText("[메세지 전송] : " + data);
			} catch (Exception e) {
				if (socket != null) {
					disconnectFromServer("[ 서버 통신 불가 ]");
				} else {
					RootLayoutController.getInstance().printText("[서버 접속이 필요 합니다]");
				}
			}
		});
		
		thread.start(); // 수신 스레드 생성
	}

	@Override
	public void dataReceiver() {
		// 스트림 받아오기
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			isr = new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")); // 못 받아 올 경우
			br = new BufferedReader(isr);
		} catch (Exception e) {
			try {
				br.close(); // 스트림 닫아주기
				disconnectFromServer("[ 소켓을 참조할 수 없습니다. ]");
			} catch (Exception e2) {}
		}
		
		while (true) {
			try {
				// 서버에서 종료 할 경우 IOException 실행
				String data = br.readLine();
				
				// 혹은 서버에서 QUIT MSG를 회신할 경우
				if (data.equals(ClientMessageTag.QUIT.getTag())) {
					throw new IOException();
				}
				
				RootLayoutController.getInstance().printText("[받은 메시지] : " + data);
			} catch (Exception e) {
				disconnectFromServer("[ 서버 통신 불가 ]");
				break;
			}
		}
	}
}
