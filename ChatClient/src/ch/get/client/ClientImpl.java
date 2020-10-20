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
		// ���� ����
		Thread thread = new Thread(() -> {
			try {
				socket = new Socket(); // ���� ����
				socket.connect(new InetSocketAddress("localhost", serverPort), 5000); // ���� 5�� Ÿ�Ӿƿ�
				
				if (socket.isConnected()) { // ������ �Ǿ� �ٸ� �г��� ����
					String message = "TEST";
					byte[] byteArr = message.getBytes("UTF-8");
					OutputStream os = socket.getOutputStream();
					os.write(byteArr);
					os.flush();
				}
			} catch (Exception e) {	
				RootLayoutController.getInstance().printText("[ ���� ���� �Ұ� ]");				
				disconnectFromServer("���� ��û �ߴ�"); // ���� ��Ȳ ������ ���� �ݱ�
//				e.printStackTrace();
				return;
			}
			
			dataReceiver();
		});
		
		thread.start(); // ���� ���� ������ ����
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
				RootLayoutController.getInstance().getConnectBtn().setDisable(false); // ���� ������ ��ư Ȱ��ȭ
			}
		}
	}

	@Override
	public void dataSender(String message) {		
		Thread thread = new Thread(() -> {
			try {
				// �޼��� ������
				byte[] byteArr = message.getBytes("UTF-8");
				OutputStream os = socket.getOutputStream();
				os.write(byteArr);
				os.flush();
				RootLayoutController.getInstance().printText("[�޼��� ����] : " + message);
			} catch (Exception e) {
				if (socket != null) {
					disconnectFromServer("[ ���� ��� �Ұ� ]");
				} else {
					RootLayoutController.getInstance().printText("[���� ������ �ʿ� �մϴ�]");
				}
			}
		});
		
		thread.start(); // ���� ������ ����
	}

	@Override
	public void dataReceiver() {
		while (true) {
			try {
				// Client ������ ������ ��� IOException �߻�
				byte[] byteArr = new byte[100];
				InputStream is = socket.getInputStream();
				
				// ������ ���� �� ��� IO����
				int readByteCount = is.read(byteArr);
				if (readByteCount == -1) { // ���� ���� �� ��� ���� �߻�
					throw new IOException();
				}

				String message = new String(byteArr, 0, readByteCount, "UTF-8");
				
				RootLayoutController.getInstance().printText("[���� �޽���] : " + message);
			} catch (Exception e) {
				disconnectFromServer("[ ���� ��� �Ұ� ]");
				break;
			}
		}
	}
}
