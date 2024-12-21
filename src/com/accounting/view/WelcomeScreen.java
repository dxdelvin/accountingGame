package com.accounting.view;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WelcomeScreen {
    private Scene scene;
    private int screenWidth = 1280;
    private int screenHeight = 720;

    public WelcomeScreen(Stage primaryStage) {
        
        Pane layout = new Pane();
        layout.setStyle("-fx-background-color: linear-gradient(to bottom, #19479e, #76839c);");

        // Grass (Green Rectangle at Bottom)
        javafx.scene.shape.Rectangle grass = new javafx.scene.shape.Rectangle(0, screenHeight - 120, screenWidth, 120);
        grass.setFill(Color.web("#009c4d"));
        layout.getChildren().add(grass);

        javafx.scene.shape.Rectangle grass2 = new javafx.scene.shape.Rectangle(0, screenHeight - 150, screenWidth, 30);
        grass2.setFill(Color.web("#135e34"));
        layout.getChildren().add(grass2);
        javafx.scene.shape.Rectangle snow = new javafx.scene.shape.Rectangle(0, screenHeight - 150, screenWidth, 5);
        snow.setFill(Color.web("#ffffff"));
        snow.setOpacity(0);
        layout.getChildren().add(snow);

        // Snowflake Effect
        createSnowflakes(layout);

        // Logo Image
        Image logoImage = new Image(getClass().getResource("/Images/Logo.png").toExternalForm());
        
        ImageView logoView = new ImageView(logoImage);
        logoView.setFitWidth(650);
        logoView.setPreserveRatio(true);
        logoView.setCache(true);
        logoView.setSmooth(true);
        logoView.setOpacity(0);
        
        DropShadow glowEffect = new DropShadow();
        glowEffect.setColor(Color.WHITE);   
        glowEffect.setRadius(150);          
        glowEffect.setSpread(0.05);         

        logoView.setEffect(glowEffect);

        layout.getChildren().add(logoView);

        layout.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            logoView.setLayoutX((newWidth.doubleValue() - logoView.getFitWidth()) / 2);
        });

        layout.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            logoView.setLayoutY((newHeight.doubleValue() - logoView.getFitHeight()) / 2 - 420);
        });

        // Text Below the Image
        Text subtitle = new Text("AN ACCOUNTING GAME");
        Font poppinsRegular = Font.loadFont(getClass().getResource("/Fonts/Poppins-Bold.ttf").toExternalForm(), 28);
        subtitle.setFont(poppinsRegular);
        subtitle.setFill(Color.BLACK);
        subtitle.setLayoutX(screenWidth - 350); // Position horizontally
        subtitle.setLayoutY(screenHeight - 10); // Position vertically just above the green rectangle
        layout.getChildren().add(subtitle);

        // Fade-In Animation for the Logo
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(3), logoView);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        FadeTransition fadeSnow = new FadeTransition(Duration.seconds(5), snow);
        fadeSnow.setFromValue(0);
        fadeSnow.setToValue(1);

        // Zoom-In Animation for the Logo
        ScaleTransition zoomIn = new ScaleTransition(Duration.seconds(2.8), logoView);
        zoomIn.setFromX(0.5);
        zoomIn.setFromY(0.5);
        zoomIn.setToX(1.1);
        zoomIn.setToY(1.1);
        ScaleTransition zoomOut = new ScaleTransition(Duration.seconds(1.5), logoView);
        zoomOut.setFromX(1.1);
        zoomOut.setFromY(1.1);
        zoomOut.setToX(1.0);
        zoomOut.setToY(1.0);

        // Play Animations
        zoomIn.setOnFinished(event -> {
            zoomOut.play();
        });

        fadeIn.play();
        fadeSnow.play();
        zoomIn.play();

        
        
        
        Button nextButton = new Button("Start Game");
        nextButton.setLayoutX(screenWidth/2-90);
        nextButton.setLayoutY(screenHeight - 192);
        nextButton.setStyle("-fx-font-size: 2.5em; -fx-background-color: #ffffff; -fx-font-family: Poppins;");
        nextButton.setOnAction(event -> {
            try {
                
                Pane nextRoot = FXMLLoader.load(getClass().getResource("/FXML/StoreSetup.fxml"));
                Scene nextScene = new Scene(nextRoot, screenWidth, screenHeight);
                primaryStage.setScene(nextScene);
            } catch (IOException e) {
            	e.printStackTrace();
            	System.out.println(e.getMessage());
                System.out.println("StoreSetup FXML cant be loaded :(");
            }
        });

        layout.getChildren().add(nextButton);

        // Set the Scene
        scene = new Scene(layout, screenWidth, screenHeight);
    }

    // Method to create the snowflake effect
    private void createSnowflakes(Pane layout) {
        Random random = new Random();
        int SNOWFLAKE_COUNT = 200; 

        // List to hold snowflakes
        List<Circle> snowflakes = new ArrayList<>();

        for (int i = 0; i < SNOWFLAKE_COUNT; i++) {
            Circle snowflake = new Circle();
            snowflake.setRadius(0 + random.nextInt(4));  
            snowflake.setFill(Color.web("#ffffff", 0.5));  

            // Randomize starting position for snowflakes
            double startX = random.nextDouble() * screenWidth;
            double startY = random.nextDouble() * screenHeight;

            snowflake.setLayoutX(startX);
            snowflake.setLayoutY(startY);

            // Add the snowflake to the layout
            layout.getChildren().add(snowflake);
            snowflakes.add(snowflake);
            snowflake.toBack();

            // Create a TranslateTransition for each snowflake (to make it fall)
            TranslateTransition fall = new TranslateTransition(Duration.seconds(5 + random.nextInt(10)), snowflake);
            fall.setFromY(startY);
            fall.setToY(screenHeight + snowflake.getRadius()); // Move off the screen at the bottom
            fall.setInterpolator(javafx.animation.Interpolator.LINEAR);
            fall.setCycleCount(TranslateTransition.INDEFINITE); // Infinite cycle to keep falling

            // Reset snowflake position once it moves off-screen
            fall.setOnFinished(event -> {
                // Randomize the X position again and set it to the top
                snowflake.setLayoutY(-snowflake.getRadius());
                snowflake.setLayoutX(random.nextDouble() * screenWidth);
                fall.play();  // Restart the fall animation
            });

            // Start the animation
            fall.play();
        }
        
        
    
    }

    public Scene getScene() {
        return scene;
    }
}
