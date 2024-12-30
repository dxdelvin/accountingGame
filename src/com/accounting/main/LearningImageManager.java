package com.accounting.main;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.LinkedList;

public class LearningImageManager {

    private LinkedList<String> shownImages; // Tracks images in descending order
    private VBox scrollableImageContainer; // A container for displaying scrollable images
    private ImageView zoomImageView;       // For zoom functionality

    // Constructor
    public LearningImageManager(VBox scrollableImageContainer, ImageView zoomImageView) {
        this.shownImages = new LinkedList<>();
        this.scrollableImageContainer = scrollableImageContainer;
        this.zoomImageView = zoomImageView;
        this.zoomImageView.setVisible(false); // Initially hidden
    }

 // Show a learning image when triggered by a dialogue
    public void triggerImage(String imageFilename) {
//    	/Images/LearningImage/LearningImage101.png
        // Resolve the full path to the image using toExternalForm
        String fullImagePath = getClass().getResource("/Images/Store/Char@2x.png").toExternalForm();
        System.out.println(fullImagePath);
        



        // Check if the image has already been shown
        if (!shownImages.contains("/Images/Store/Char@2x.png")) {
            shownImages.addFirst("/Images/Store/Char@2x.png"); // Add to the top of the list (latest first)
            updateScrollableImages(); // Refresh the scrollable image view
        }
        
        Image image = new Image("/Images/Store/Char@2x.png");
        zoomImage(image);
    }


    // Update the scrollable container with the current list of images
    private void updateScrollableImages() {
        scrollableImageContainer.getChildren().clear(); // Clear previous images
        for (String imageFilename : shownImages) {
            Image image = new Image(getClass().getResource(imageFilename).toExternalForm());
            System.out.println(getClass().getResource(imageFilename));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100); // Thumbnail size
            imageView.setPreserveRatio(true);

            // Add click listener for zoom functionality
            imageView.setOnMouseClicked(event -> zoomImage(image));

            scrollableImageContainer.getChildren().add(imageView);
            
            
        }
    }

    // Display an image in full size on click
    private void zoomImage(Image image) {
        zoomImageView.setImage(image);
        zoomImageView.setVisible(true);

        // Close zoom view on click
        zoomImageView.setOnMouseClicked(event -> zoomImageView.setVisible(false));
    }

    // Get the list of shown images (for any additional use)
    public LinkedList<String> getShownImages() {
        return shownImages;
    }
}
