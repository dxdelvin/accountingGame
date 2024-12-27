package com.accounting.view;

import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.accounting.view.WelcomeScreen;


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
    
    @FXML
    private AnchorPane accountsPane;

    @FXML
    private Button accountsButton;

    private boolean isPaneOpen = false;

    // List to hold dialogues and character images
    private List<Dialogue> dialogues = new ArrayList<>();
    private int currentDialogueIndex = 0;

    private int objectsInRedBox = 0; // Track objects in the red box
    private boolean isStoreConfirmed = false; // Track if store design is confirmed

    private boolean allObjectsInRedBox = false; // Flag to track if all objects are in the red box
    private boolean isConfirmationShown = false; // Flag to track if confirmation dialogue has been shown

    private Stage primaryStage;

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
        
        accountsPane.setTranslateX(-500); 
        isPaneOpen = false;
        accountsButton.setOnAction(event -> toggleAccountsPane());
        
        
    }
    
    private Boolean movement = true;

    @FXML
    private void nextDialogue() {
        if (currentDialogueIndex < dialogues.size() - 1) {
            currentDialogueIndex++;
            displayCurrentDialogue();
        }

        if (currentDialogueIndex == 6) {
        	while(movement == true) {        		
        		dialogueBox.setTranslateX(dialogueBox.getTranslateX() - 120);
        		movement = false;
        	}
        	animateRedBox(redBorderBox);
            redBorderBox.setVisible(true);

            stick1.setDisable(false);
            stick2.setDisable(false);
            stick3.setDisable(false);
            stick4.setDisable(false);
            signBoard.setDisable(false);
        }

        if (allObjectsInRedBox && !isConfirmationShown) {
            characterDialogue.setText("Click to Confirm Your Store!");
            
        }
        if (currentDialogueIndex == 6 && allObjectsInRedBox) {
        	System.out.println("7th dialogue going on");
            dialogues.add(new Dialogue("Now lets Focus in Building Your Brand Name ('press enter')", new Image(getClass().getResource("/Images/Store/Char@2x.png").toExternalForm())));
            currentDialogueIndex++;
            displayCurrentDialogue();
            isConfirmationShown = true;
            makeObjectsNonMovable();
            positionBrandNameField();
        }
    }
    
    private void animateRedBox(Rectangle redBox) {
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(200), redBox);
        fadeTransition.setFromValue(1.0); // Start with full opacity
        fadeTransition.setToValue(0.0);   // Fade to invisible
        fadeTransition.setCycleCount(2);  // Fade in and out
        fadeTransition.setAutoReverse(true); // Reverse the animation
        fadeTransition.play();
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
    
    private Set<ImageView> objectsInBox = new HashSet<>();
    private void initializeDraggable(ImageView object) {
        object.setOnMousePressed(event -> {
            if (!isStoreConfirmed) {
                object.setUserData(new double[]{event.getSceneX() - object.getLayoutX(), event.getSceneY() - object.getLayoutY()});
            }
        });

        object.setOnMouseDragged(event -> {
            if (!isStoreConfirmed) {
                double[] offset = (double[]) object.getUserData();
                object.setLayoutX(event.getSceneX() - offset[0]);
                object.setLayoutY(event.getSceneY() - offset[1]);

                
            }
        });

        object.setOnMouseReleased(event -> {
            if (!isStoreConfirmed && isInsideRedBox(object)) {
                // Check if the object is already in the box
                if (!objectsInBox.contains(object)) {
                    objectsInRedBox++; // Increment count only for new objects
                    objectsInBox.add(object); // Mark the object as inside the box
                }
                checkCompletion();
            } else if (objectsInBox.contains(object) && !isInsideRedBox(object)) {
                // If an object is moved out of the box, remove it from the set and decrement the counter
                objectsInBox.remove(object);
                objectsInRedBox--;
                checkCompletion();
            }
        });
    }

    private void checkCompletion() {
        if (objectsInRedBox == 5) {
            allObjectsInRedBox = true;
            if (!isConfirmationShown) {
                characterDialogue.setText("Click to Confirm Your Store!");
            }
        } else {
            allObjectsInRedBox = false;
            characterDialogue.setText("Complete Your Design!");
        }
    }

    private void makeObjectsNonMovable() {
        stick1.setOnMouseDragged(null);
        stick2.setOnMouseDragged(null);
        stick3.setOnMouseDragged(null);
        stick4.setOnMouseDragged(null);
        signBoard.setOnMouseDragged(null);
    }

    private boolean isInsideRedBox(ImageView object) {
        return redBorderBox.getBoundsInParent().contains(object.getBoundsInParent());
    }
    
    
    private void toggleAccountsPane() {
        if (isPaneOpen) {
            // Slide the pane out
            accountsPane.setTranslateX(-500);
            accountsButton.setText("Open Accounts");
        } else {
            // Slide the pane in
            accountsPane.setTranslateX(0);
            accountsButton.setText("Close Accounts");
        }
        isPaneOpen = !isPaneOpen;
    }

    
    
    @FXML
    private TextField brandNameField;

    private boolean isBrandNameEntered = false;
    
    
    private void positionBrandNameField() {
        // Get the position of the signBoard
        double signBoardX = signBoard.getLayoutX();
        double signBoardY = signBoard.getLayoutY();

        // Adjust the position for the brandNameField
        // Offset can be used to fine-tune the placement on the signBoard
        double offsetX = 10; // Optional: Adjust for alignment
        double offsetY = signBoard.getFitHeight() / 2 - 10; // Center the field

        // Set the position of the brandNameField
        brandNameField.setLayoutX(signBoardX + offsetX);
        brandNameField.setLayoutY(signBoardY + offsetY - 35);

        // Adjust the width of the brandNameField to match the signBoard
        brandNameField.setPrefWidth(signBoard.getFitWidth() - 20); // Optional^
        brandNameField.setDisable(false);
        brandNameField.setPromptText("Enter Name");

        // Limit text to 12 characters
        brandNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 12) {
                brandNameField.setText(oldValue); // Revert to the old value if input exceeds 12 characters
            }
        });
    }

    private void checkSignBoardLocation() {
        if (isInsideRedBox(signBoard) && currentDialogueIndex == 7) {
            // Display the dialogue for entering the brand name
        	System.out.println("Signboard is inside the red box!");
            characterDialogue.setText("Hey, let's give your business a cool name to remember!");
            positionBrandNameField();
            brandNameField.setDisable(false); // Enable the field
            brandNameField.requestFocus(); // Focus the field
            brandNameField.setVisible(true);
            brandNameField.setOnAction(event -> handleBrandNameInput());
        }
    }
    
    @FXML
    private void handleBrandNameInput() {
        String brandName = brandNameField.getText().trim();

        if (brandName.isEmpty()) {
            characterDialogue.setText("Hey, you need to write your brand name!");
        } else {
            characterDialogue.setText("Great Name Choice!");
            isBrandNameEntered = true;
            
            dialogues.add(new Dialogue("Sehr Toll! Lets sell some LIMONADE!", new Image(getClass().getResource("/Images/Store/charHappy.png").toExternalForm())));
            currentDialogueIndex++;
            displayCurrentDialogue();
            

            // Lock all objects and disable further editing of the field
            brandNameField.setDisable(true); // Optionally disable after input
            saveLayoutData(brandName);
        }
    }
    
    private void saveLayoutData(String brandName) {
        Map<String, String> layoutData = new HashMap<>();
        layoutData.put("Brand Name", brandName);
        layoutData.put("Stick1", stick1.getLayoutX() + ", " + stick1.getLayoutY());
        layoutData.put("Stick2", stick2.getLayoutX() + ", " + stick2.getLayoutY());
        layoutData.put("Stick3", stick3.getLayoutX() + ", " + stick3.getLayoutY());
        layoutData.put("Stick4", stick4.getLayoutX() + ", " + stick4.getLayoutY());
        layoutData.put("SignBoard", signBoard.getLayoutX() + ", " + signBoard.getLayoutY());
        
        layoutData.put("chapter0", "true");

        // Debugging output (Optional)
        layoutData.forEach((key, value) -> System.out.println(key + ": " + value));

        // Save data using GameProgressManager
        GameProgressManager.saveProgress(layoutData);
        this.primaryStage = (Stage) rootPane.getScene().getWindow();
        try {
            Pane nextRoot = FXMLLoader.load(getClass().getResource("/FXML/chapter1.fxml"));
            Scene nextScene = new Scene(nextRoot, WelcomeScreen.screenWidth, WelcomeScreen.screenHeight);
            primaryStage.setScene(nextScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load chapter1.fxml");
        }
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
