package ch.get.view;

import java.net.URL;
import java.util.ResourceBundle;

import ch.get.client.ClientImpl;
import ch.get.util.LogTime;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class RootLayoutController implements Initializable {

	private Stage primaryStage; 
	private static RootLayoutController instance;
	
	@FXML
	private Button connectBtn;
	@FXML
	private TextArea textArea;
	@FXML
	private TextField typingField;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		
		// 리스너
		typingField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (KeyCode.ENTER.equals(event.getCode()) && !typingField.getText().isEmpty()) {
				String data = typingField.getText();
				printText(data);
				ClientImpl.getInstance().dataSender(data);
				typingField.clear();
			}
		});
	}
	
	@FXML
	private void connectToServer() {
		RootLayoutController.getInstance().getConnectBtn().setDisable(true); // 일단 버튼 비활성화
		ClientImpl.getInstance().connectToServer();
	}
	
	// root TextArea
	public void printText(String str) {
		textArea.appendText(LogTime.getInstance().getTime() + str + "\r\n");
	}

	public void setApp(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
	
	public static RootLayoutController getInstance() {
		return instance;
	}
	
	public Button getConnectBtn() {
		return connectBtn;
	}
}
