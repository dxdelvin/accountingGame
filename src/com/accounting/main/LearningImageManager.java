package com.accounting.main;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.LinkedList;

public class LearningImageManager {

    private LinkedList<String> shownImages = new LinkedList<>();
    private VBox scrollableImageContainer;
    private ImageView zoomImageView;

    public LearningImageManager(VBox scrollableImageContainer, ImageView zoomImageView) {
        this.scrollableImageContainer = scrollableImageContainer;
        this.zoomImageView = zoomImageView;
        this.zoomImageView.setVisible(false);
    }

    public void triggerImage(String imageFilename) {
        if (!shownImages.contains(imageFilename)) {
            shownImages.addFirst(imageFilename);
            updateScrollableImages();
        }

        // Check if the resource exists
        if (getClass().getResourceAsStream(imageFilename) == null) {
            System.out.println("Image not found: " + imageFilename);
            return; // Stop further execution if the image is not found
        }

        Image image = new Image(getClass().getResourceAsStream(imageFilename));
        zoomImage(image);
    }

    private void updateScrollableImages() {
        scrollableImageContainer.getChildren().clear();
        int index = shownImages.size();
        for (String imageFilename : shownImages) {
            Image image = new Image(getClass().getResource(imageFilename).toExternalForm());
            StackPane thumbnail = createThumbnail(image,index--); // Now we get a StackPane
            scrollableImageContainer.getChildren().add(thumbnail);
        }
    }

    private StackPane createThumbnail(Image image, int index) {
        // Create an ImageView to display the image
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(250);  // Set width of the thumbnail
        imageView.setPreserveRatio(true);  // Maintain aspect ratio
        
        // Create a black border around the image using a Rectangle
        Rectangle border = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        border.setFill(Color.BLACK);
        border.setStroke(Color.BLACK);
        border.setStrokeWidth(5);  // Border width

        // Create a StackPane to hold the image and border
        StackPane thumbnailContainer = new StackPane();
        thumbnailContainer.getChildren().addAll(border, imageView);

        // Add top and bottom spacing using padding
        thumbnailContainer.setPadding(new Insets(10, 0, 20, 0)); // top: 10px, bottom: 20px, left/right: 0px
        
        // Add hover effect (optional)
        thumbnailContainer.setOnMouseEntered(event -> thumbnailContainer.setStyle("-fx-background-color: rgba(0, 0, 0, 0.1);"));
        thumbnailContainer.setOnMouseExited(event -> thumbnailContainer.setStyle("-fx-background-color: transparent;"));

        // Add click event to zoom the image when clicked
        imageView.setOnMouseClicked(event -> zoomImage(image));

        // Create a Text node for the numbering (1, 2, 3, etc.)
        Text numberText = new Text(Integer.toString(index));
        numberText.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        numberText.setFill(Color.BLACK);  // Make the number visible on the image

        // Position the number at the top-left corner of the thumbnail
        StackPane.setAlignment(numberText, Pos.TOP_LEFT);
        StackPane.setMargin(numberText, new Insets(5, 0, 0, 5));  // Add padding for the number from top-left corner

        // Add the number to the thumbnail container
        thumbnailContainer.getChildren().add(numberText);

        return thumbnailContainer; // Return the StackPane containing the image, border, and number
    }




    private void zoomImage(Image image) {
        zoomImageView.setImage(image);
        zoomImageView.setVisible(true);
        zoomImageView.toFront();
        zoomImageView.setOnMouseClicked(event -> {
            zoomImageView.setVisible(false);
            zoomImageView.setImage(null);  // Reset the image when closing the zoom view
        });
    }
}
