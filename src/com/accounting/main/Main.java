package com.accounting.main;
	
import com.accounting.view.WelcomeScreen;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		WelcomeScreen welcome = new WelcomeScreen(primaryStage);
        primaryStage.setScene(welcome.getScene());
        primaryStage.setTitle("Die Accounting Spiel");
        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
