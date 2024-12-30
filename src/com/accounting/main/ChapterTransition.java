package com.accounting.main;

import javafx.animation.FadeTransition;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ChapterTransition {

    private Pane rootPane;
    private Scene scene;

    public ChapterTransition(Pane rootPane) {
        this.rootPane = rootPane;
        this.scene = rootPane.getScene();  // Get the scene directly from rootPane
    }

    public void displayChapterStartScreen(int chapterNumber) {
        if (scene == null) {
            System.out.println("Scene is still null! Cannot display chapter.");
            return;  // Prevent proceeding if the scene is still null
        }

        // Create a full-screen black overlay
        Pane chapterOverlay = new Pane();
        chapterOverlay.setStyle("-fx-background-color: black;");

        // Set the size of the overlay to match the scene's width and height
        chapterOverlay.setPrefSize(scene.getWidth(), scene.getHeight());

        // Create a text label for the chapter
        Text chapterText = new Text("Chapter " + chapterNumber);
        chapterText.setStyle("-fx-fill: white; -fx-font-size: 40px;");
        
        double sceneWidth = scene.getWidth();
        double sceneHeight = scene.getHeight();

        // Center text horizontally and vertically
        chapterText.setTranslateX(sceneWidth / 2 - chapterText.getLayoutBounds().getWidth() / 2);
        chapterText.setTranslateY(sceneHeight / 2);

        chapterOverlay.getChildren().add(chapterText);
        rootPane.getChildren().add(chapterOverlay);

        // Fade-in effect
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), chapterOverlay);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        // Fade-out effect after 5 seconds
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), chapterOverlay);
        fadeOut.setDelay(Duration.seconds(1.4)); 
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);

        fadeOut.setOnFinished(event -> rootPane.getChildren().remove(chapterOverlay));
        fadeOut.play();
    }
}
