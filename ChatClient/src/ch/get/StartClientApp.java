package ch.get;

import ch.get.client.ClientImpl;
import ch.get.common.Connections;
import ch.get.controller.WindowController;
import ch.get.util.LogTime;
import ch.get.view.RootLayoutController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class StartClientApp extends Application {

	public final static String TITLE_NAME = "ChatClient";
	private Stage primaryStage;
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		
		// Insert into heap memory
		Connections.getConnections();
		WindowController.getInstance(); // window controller
		ClientImpl.getInstance();
		LogTime.getInstance();
		
		// initLayOut
		initRoot();
		
		// 윈도우 X표시 클릭시 서버 자원 삭제
		primaryStage.setOnCloseRequest(event -> {
			
		});
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	// 메인 레이아웃 초기화
	public void initRoot() {
		try {
			FXMLLoader loader = new FXMLLoader(StartClientApp.class.getResource("view/RootLayout.fxml"));
			BorderPane borderPane = (BorderPane) loader.load();
			Scene scene = new Scene(borderPane);
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.setTitle(TITLE_NAME);
			primaryStage.show();
			
			RootLayoutController controller = loader.getController();
			controller.setApp(primaryStage);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
