package ch.get.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

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
					String message = "TEST";
					byte[] byteArr = message.getBytes("UTF-8");
					OutputStream os = socket.getOutputStream();
					os.write(byteArr);
					os.flush();
				}
			} catch (Exception e) {	
				RootLayoutController.getInstance().printText("[ 서버 접속 불가 ]");				
				disconnectFromServer("연결 요청 중단"); // 에러 상황 에서는 소켓 닫기
//				e.printStackTrace();
				return;
			}
			
			dataReceiver();
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
	public void dataSender(String message) {		
		Thread thread = new Thread(() -> {
			try {
				// 메세지 보내기
				byte[] byteArr = message.getBytes("UTF-8");
				OutputStream os = socket.getOutputStream();
				os.write(byteArr);
				os.flush();
				RootLayoutController.getInstance().printText("[메세지 전송] : " + message);
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
		while (true) {
			try {
				// Client 비정상 종료일 경우 IOException 발생
				byte[] byteArr = new byte[100];
				InputStream is = socket.getInputStream();
				
				// 비정상 종료 일 경우 IO예외
				int readByteCount = is.read(byteArr);
				if (readByteCount == -1) { // 정상 종료 일 경우 예외 발생
					throw new IOException();
				}

				String message = new String(byteArr, 0, readByteCount, "UTF-8");
				
				RootLayoutController.getInstance().printText("[받은 메시지] : " + message);
			} catch (Exception e) {
				disconnectFromServer("[ 서버 통신 불가 ]");
				break;
			}
		}
	}
}
