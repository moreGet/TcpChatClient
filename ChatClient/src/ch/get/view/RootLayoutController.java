package ch.get.view;

import java.net.URL;
import java.util.ResourceBundle;

import ch.get.client.ClientImpl;
import ch.get.util.LogTime;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RootLayoutController implements Initializable {

	private Stage primaryStage; 
	private static RootLayoutController instance;
	
	@FXML
	private Button connectBtn;
	@FXML
	private ScrollPane textArea;
//	private TextArea textArea;
	@FXML
	private TextField typingField;
	@FXML
	private VBox chatBox;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		instance = this;
		chatBox.getStyleClass().add("chatbox");

		// 리스너
		typingField.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
			if (KeyCode.ENTER.equals(event.getCode()) && !typingField.getText().isEmpty()) {
				String data = typingField.getText();
//				printText(data);
				printMyText(data);
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
//		System.out.println("===== LEFT =====");
		Label label = new Label(LogTime.getInstance().getTime() + str + "\r\n");	
		addChatBox(label, Pos.CENTER_LEFT);
	}

	public void printMyText(String str) {
//		System.out.println("===== RIGHT =====");
		Label label = new Label(str + " " + LogTime.getInstance().getTime() + "\r\n");
		addChatBox(label, Pos.CENTER_RIGHT);		
	}
	
	// chatBox
	private void addChatBox(Label chatLabel, Pos pos) {
		Platform.runLater(() -> {
			chatLabel.setAlignment(pos);
			chatLabel.setPrefWidth(chatBox.getWidth());
			
			chatBox.widthProperty().addListener((v) -> {
				chatLabel.setPrefWidth(chatBox.getWidth());
			});
			
			chatBox.getChildren().add(chatLabel);
		});
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
