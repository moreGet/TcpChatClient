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
		// ���� ����
		Thread thread = new Thread(() -> {
			try {
				socket = new Socket(); // ���� ����
				socket.connect(new InetSocketAddress("localhost", serverPort), 5000); // ���� 5�� Ÿ�Ӿƿ�
				
				if (socket.isConnected()) { // ������ �Ǿ� �ٸ� �г��� ����
//					Connections.getConnections().add(socket); // socket List�� ����
					OutputStreamWriter osw = new OutputStreamWriter(socket.getOutputStream());
					BufferedWriter bw = new BufferedWriter(osw);
					
					bw.write("TEST001");
					bw.flush();
					bw.close(); // ��Ʈ�� �ݱ�	
				}
			} catch (Exception e) {	
				RootLayoutController.getInstance().printText("[ ���� ���� �Ұ� ]");				
				disconnectFromServer("���� ��û �ߴ�"); // ���� ��Ȳ ������ ���� �ݱ�
//				e.printStackTrace();
				return;
			}
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
	public void dataSender(String data) {		
		Thread thread = new Thread(() -> {
			try {
				// ��Ʈ�� �޾ƿ���
				OutputStreamWriter osw = null;
				BufferedWriter bw = null;
				osw = new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")); // �� �޾� �� ���
				bw = new BufferedWriter(osw);
				bw.write(data);
				bw.flush();
				
				RootLayoutController.getInstance().printText("[�޼��� ����] : " + data);
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
		// ��Ʈ�� �޾ƿ���
		InputStreamReader isr = null;
		BufferedReader br = null;
		
		try {
			isr = new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")); // �� �޾� �� ���
			br = new BufferedReader(isr);
		} catch (Exception e) {
			try {
				br.close(); // ��Ʈ�� �ݾ��ֱ�
				disconnectFromServer("[ ������ ������ �� �����ϴ�. ]");
			} catch (Exception e2) {}
		}
		
		while (true) {
			try {
				// �������� ���� �� ��� IOException ����
				String data = br.readLine();
				
				// Ȥ�� �������� QUIT MSG�� ȸ���� ���
				if (data.equals(ClientMessageTag.QUIT.getTag())) {
					throw new IOException();
				}
				
				RootLayoutController.getInstance().printText("[���� �޽���] : " + data);
			} catch (Exception e) {
				disconnectFromServer("[ ���� ��� �Ұ� ]");
				break;
			}
		}
	}
}
