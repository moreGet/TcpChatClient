package ch.get.client;

public interface Client {

	public void connectToServer();
	public void disconnectFromServer(String msg);
	public void dataSender(String data);
	public void dataReceiver();
}
