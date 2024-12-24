package com.accounting.view;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class StoreSetupController {

    @FXML
    private Label characterDialogue;

    @FXML
    private StackPane dialogueBox;

    @FXML
    private ImageView characterImage;

    @FXML
    private ImageView stick1;
    @FXML
    private ImageView stick2;
    @FXML
    private ImageView stick3;
    @FXML
    private ImageView stick4;

    @FXML
    private ImageView signBoard;

    @FXML
    private Rectangle redBorderBox;

    @FXML
    private AnchorPane rootPane;

    // List to hold dialogues and character images
    private List<Dialogue> dialogues = new ArrayList<>();
    private int currentDialogueIndex = 0;

    private int objectsInRedBox = 0; // Track objects in the red box
    private boolean isStoreConfirmed = false; // Track if store design is confirmed

    @FXML
    public void initialize() {
        // Populate dialogues
        dialogues.add(new Dialogue("Hi Kid Chef! (Click here to Continue)", new Image(getClass().getResource("/Images/Store/Char@2x.png").toExternalForm())));
        dialogues.add(new Dialogue("You wanna sell some limonade?", new Image(getClass().getResource("/Images/Store/Char@2x.png").toExternalForm())));
        dialogues.add(new Dialogue("And wanna earn viellll GELDDDD", new Image(getClass().getResource("/Images/Store/Char@2x.png").toExternalForm())));
        dialogues.add(new Dialogue("COOL!", new Image(getClass().getResource("/Images/Store/Char@2x.png").toExternalForm())));
        dialogues.add(new Dialogue("So Can You see Your Store Equipments", new Image(getClass().getResource("/Images/Store/Char@2x.png").toExternalForm())));
        dialogues.add(new Dialogue("I Found them from Garage", new Image(getClass().getResource("/Images/Store/Char@2x.png").toExternalForm())));
        dialogues.add(new Dialogue("So Click and Drag to Red Box And Start Building It!", new Image(getClass().getResource("/Images/Store/Char@2x.png").toExternalForm())));

        // Set the initial dialogue and character image
        displayCurrentDialogue();

        stick1.setDisable(true);
        stick2.setDisable(true);
        stick3.setDisable(true);
        stick4.setDisable(true);
        signBoard.setDisable(true);

        initializeDraggable(stick1);
        initializeDraggable(stick2);
        initializeDraggable(stick3);
        initializeDraggable(stick4);
        initializeDraggable(signBoard);

        // Set up the label's on-click behavior
        characterDialogue.setOnMouseClicked(event -> nextDialogue());
    }

    private boolean allObjectsInRedBox = false; // Flag to track if all objects are in red box

    @FXML
    private void nextDialogue() {
        // Proceed to the next dialogue if not at the last one
        if (currentDialogueIndex < dialogues.size() - 1) {
            currentDialogueIndex++;
            displayCurrentDialogue();
        }

        // Special handling for dialogue index 6
        if (currentDialogueIndex == 6) {
            dialogueBox.setTranslateX(dialogueBox.getTranslateX() - 120);
            redBorderBox.setVisible(true);

            // Enable the objects for dragging
            stick1.setDisable(false);
            stick2.setDisable(false);
            stick3.setDisable(false);
            stick4.setDisable(false);
            signBoard.setDisable(false);
        }

        // After all objects are in the red box, show confirmation
        if (allObjectsInRedBox && !isStoreConfirmed) {
            characterDialogue.setText("Congrats on Your New Store!");
            isStoreConfirmed = true;
            makeObjectsNonMovable(); // Lock objects
        }

        // Hide the dialogue box if no more dialogues are left
        if (currentDialogueIndex >= dialogues.size()) {
            characterDialogue.setVisible(false);
            dialogueBox.setVisible(false);
        }
    }

    private void displayCurrentDialogue() {
        if (!dialogues.isEmpty()) {
            Dialogue currentDialogue = dialogues.get(currentDialogueIndex);
            animateDialogue(currentDialogue.getText());
            characterImage.setImage(currentDialogue.getCharacterImage());
            characterDialogue.setVisible(true);
        }
    }

    private void animateDialogue(String text) {
        characterDialogue.setText("");
        FadeTransition fadeIn = new FadeTransition(Duration.millis(100), characterDialogue);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setOnFinished(event -> characterDialogue.setText(text));
        fadeIn.play();
    }

    private void initializeDraggable(ImageView object) {
        object.setOnMousePressed(event -> {
            object.setUserData(new double[]{event.getSceneX() - object.getLayoutX(), event.getSceneY() - object.getLayoutY()});
        });

        object.setOnMouseDragged(event -> {
            if (!isStoreConfirmed) {
                double[] offset = (double[]) object.getUserData();
                object.setLayoutX(event.getSceneX() - offset[0]);
                object.setLayoutY(event.getSceneY() - offset[1]);
            }
        });

        object.setOnMouseReleased(event -> {
            if (isInsideRedBox(object)) {
                objectsInRedBox++;
                checkCompletion();
            } else {
                objectsInRedBox = Math.max(objectsInRedBox - 1, 0);
                allObjectsInRedBox = false;
            }
        });
    }

    private void checkCompletion() {
        if (objectsInRedBox == 5) {
            allObjectsInRedBox = true;
            characterDialogue.setText("Confirm Your Store Design!");
        } else {
            allObjectsInRedBox = false;
            characterDialogue.setText("Complete Your Design!");
        }
    }

    private void makeObjectsNonMovable() {
        stick1.setDisable(true);
        stick2.setDisable(true);
        stick3.setDisable(true);
        stick4.setDisable(true);
        signBoard.setDisable(true);
    }

    private boolean isInsideRedBox(ImageView object) {
        return redBorderBox.getBoundsInParent().contains(object.getBoundsInParent());
    }

    // Inner class to represent dialogue
    private static class Dialogue {
        private final String text;
        private final Image characterImage;

        public Dialogue(String text, Image characterImage) {
            this.text = text;
            this.characterImage = characterImage;
        }

        public String getText() {
            return text;
        }

        public Image getCharacterImage() {
            return characterImage;
        }
    }
}
