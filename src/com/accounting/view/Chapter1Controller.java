package com.accounting.view;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.accounting.main.Account;
import com.accounting.main.AccountingPanel;
import com.accounting.main.ChapterTransition;
import com.accounting.main.Dialogue;
import com.accounting.main.DialogueSystem;
import com.accounting.main.LearningImageManager;

public class Chapter1Controller {
	
	

    @FXML
    private AnchorPane anchorPane; // Root container (AnchorPane)
    @FXML
    private ImageView signBoard;  
    @FXML
    private ImageView stick4;   
    @FXML
    private ImageView stick2;     
    @FXML
    private ImageView stick3;    
    @FXML
    private ImageView stick1;     
    @FXML
    private ImageView extra;
    
    @FXML
    private TextField brandNameField;
    
    @FXML
    private Label dialogueLabel; // The label to display dialogue text
    @FXML
    private Button optionButton1; // First option button (Yes/No or Next)
    @FXML
    private Button optionButton2; // Second option button (Yes/No or unused)
    @FXML
    private TextField inputField; // Input field for user input

    private DialogueSystem dialogueSystem;
    private int currentDialogueId;
    
    @FXML
    private AnchorPane accountsPane;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView characterImage;


    @FXML
    private Button accountsButton;
    
    @FXML
    private VBox scrollableImageContainer;
    @FXML
    private ImageView zoomImageView;
    @FXML
    private Button closeZoomButton;
    private LearningImageManager learningImageManager;
    private boolean imagesAdded = false;
    
    
    private boolean isPaneOpen = false;
    
    private String brandName;
    
    @FXML
    private VBox accountingVBox;
    
    private boolean chapter0;
    
    
    public void initialize() {
        readGameProgressFromFile("gameProgress.txt");
        accountsPane.setTranslateX(-500); 
        isPaneOpen = false;
        accountsButton.setOnAction(event -> toggleAccountsPane());
        
        dialogueSystem = new DialogueSystem("chapter1.json", brandName, characterImage); // Load the dialogue JSON
        currentDialogueId = 1; 
        displayCurrentDialogue();
        
        zoomImageView.setVisible(false);
        learningImageManager = new LearningImageManager(scrollableImageContainer, zoomImageView);
        
        Platform.runLater(() -> {
            Scene scene = rootPane.getScene();
            if (scene != null) {
                ChapterTransition chapterTransition = new ChapterTransition(rootPane);
                chapterTransition.displayChapterStartScreen(1);  
            } else {
                System.out.println("Scene is still null! Cannot display chapter.");
            }
        });
       
    }
    private void readGameProgressFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
            	if (line.startsWith("Brand Name=")) {
                    String brandName = line.substring("Brand Name=".length());
                    brandNameField.setText(brandName); 
                    
                }
                String[] data = line.split("=");
                if (data.length == 2) {
                    String key = data[0].trim();
                    String value = data[1].trim();

                    
                    switch (key) {
                        case "Brand Name":
                            brandName = value;
                            break;
                        case "chapter0":
                        	chapter0 = Boolean.parseBoolean(value);
                        	break;
                        case "SignBoard":
                            setPosition(signBoard, value);
                            break;
                        case "Stick4":
                            setPosition(stick4, value);
                            break;
                        case "Stick2":
                            setPosition(stick2, value);
                            break;
                        case "Stick3":
                            setPosition(stick3, value);
                            break;
                        case "Stick1":
                            setPosition(stick1, value);
                            break;
                        default:
                            System.out.println("Unknown key: " + key);
                            break;
                    }
                    double signBoardX = signBoard.getLayoutX();
                    double signBoardY = signBoard.getLayoutY();

                    double offsetX = 10; 
                    double offsetY = signBoard.getFitHeight() / 2 - 10;

                    brandNameField.setLayoutX(signBoardX + offsetX);
                    brandNameField.setLayoutY(signBoardY + offsetY - 35);

                    brandNameField.setPrefWidth(signBoard.getFitWidth() - 20); 
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setPosition(ImageView imageView, String position) {
        String[] coords = position.split(",");
        if (coords.length == 2) {
            try {
                double x = Double.parseDouble(coords[0].trim());
                double y = Double.parseDouble(coords[1].trim());
                imageView.setLayoutX(x);
                imageView.setLayoutY(y);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void displayCurrentDialogue() {
        Dialogue currentDialogue = dialogueSystem.getDialogue(currentDialogueId);
        
        
        if (currentDialogue == null) {
            return; // Handle error case if dialogue is not found
        }

        dialogueLabel.setText(currentDialogue.getText()); 
        
        if (currentDialogue.getType().equals("Normal")) {
            optionButton1.setText("Next");
            optionButton2.setVisible(false); 
            inputField.setVisible(false); 
        } else if (currentDialogue.getType().equals("YesNo")) {
            optionButton1.setText(currentDialogue.getOptions()[0]); 
            optionButton2.setText(currentDialogue.getOptions()[1]);
            optionButton2.setVisible(true); 
            inputField.setVisible(false); 
        } else if (currentDialogue.getType().equals("Input")) {
            optionButton1.setText("Submit");
            optionButton2.setVisible(false); 
            inputField.setVisible(true); 
        }
    }

    @FXML
    private void onOption1Clicked() {
        Dialogue currentDialogue = dialogueSystem.getDialogue(currentDialogueId);

        if (currentDialogue.getType().equals("Input")) {
            String input = inputField.getText().trim(); // Get and trim the input from the user

            if (input.isEmpty()) {
                dialogueLabel.setText("Enter Value: " + currentDialogue.getText()); // Prompt user if input is empty
                return;
            }

            // Check if the input matches the correct answer
            if (input.equals(currentDialogue.getCorrectAnswer())) {
                System.out.println("Correct input: " + input);

                // Move to the nextIdCorrect if input is correct
                currentDialogueId = currentDialogue.getNextIdCorrect(); 
            } else {
                System.out.println("Incorrect input: " + input);

                // Move to the nextIdIncorrect if input is incorrect
                currentDialogueId = currentDialogue.getNextIdIncorrect();
            }
        }
        moveToNextDialogue(currentDialogue); 
    }



    @FXML
    private void onOption2Clicked() {
        Dialogue currentDialogue = dialogueSystem.getDialogue(currentDialogueId);
        String selectedOption = optionButton2.getText(); // Get the selected option (Yes/No)
        Map<String, Integer> nextMap = (Map<String, Integer>) currentDialogue.getNextId();
        currentDialogueId = nextMap.get(selectedOption); // Update to the next dialogue based on the option
        displayCurrentDialogue(); // Update the dialogue display
    }

    // This method moves to the next dialogue (based on the current dialogue's nextId)
    private String question = "Answer the Question:";
	private Stage primaryStage;
    private void moveToNextDialogue(Dialogue currentDialogue) {
    	System.out.println(currentDialogueId);
    	System.out.println(DialogueSystem.isDialoguePaused);
    	if (DialogueSystem.isDialoguePaused) {
    		if(!isPaneOpen) {
        		toggleAccountsPane();
        	}
    		dialogueLabel.setText(question + " " + currentDialogue.getText());
            return;  
        }
        if (currentDialogue.getNextId() instanceof Integer) {
            currentDialogueId = (Integer) currentDialogue.getNextId(); // Move to the next dialogue if it's just an ID
        } else if (currentDialogue.getNextId() instanceof Map) {
            Map<String, Integer> nextMap = (Map<String, Integer>) currentDialogue.getNextId();
            
            // Assuming the selected option is stored in the button text
            String selectedOption = optionButton1.getText(); // Or another logic for selecting option
            Integer nextDialogueId = nextMap.get(selectedOption);

            // Check if the next dialogue ID exists in the map
            if (nextDialogueId != null) {
                currentDialogueId = nextDialogueId; // Update to the next dialogue based on selected option
            } else {
                // Handle the case where the option isn't valid
                System.out.println("Selected option is not valid: " + selectedOption);
                return;
            }
        }
        handleDialogueTrigger(currentDialogueId);
        displayCurrentDialogue(); // Update the dialogue view after changing the ID
    }


  
    private void toggleAccountsPane() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), accountsPane);

        if (isPaneOpen) {
            transition.setToX(-500);
            accountsButton.setText("Open Accounts");
        } else {
            transition.setToX(0);
            accountsButton.setText("Close Accounts");
        }
        transition.play();  
        isPaneOpen = !isPaneOpen;
    }
    
    private void loadAccountingPanel(ObservableList<Account> defaultAccounts, ObservableList<Account> draggableAccounts) {
        // Clear the VBox before adding the accounting panel to prevent duplication
        accountingVBox.getChildren().clear();

        AccountingPanel accountingPanel = new AccountingPanel(defaultAccounts, draggableAccounts);

        accountingVBox.getChildren().add(accountingPanel);

        System.out.println("Accounting panel loaded with custom accounts.");
    }


    public void handleDialogueTrigger(int dialogueId) {
        switch (dialogueId) {
            case 9:
                learningImageManager.triggerImage("/Images/LearningImage/LearningImage101.png");
                break;
            case 20:
            	learningImageManager.triggerImage("/Images/LearningImage/LearningImage102.png");
            	break;
            case 24:
            	if(!isPaneOpen) {
            		toggleAccountsPane();
            	}
            	ObservableList<Account> defaultAccounts1 = FXCollections.observableArrayList(
//                        new Account("Bank", "Asset", 1200.0),
//                        new Account("Loans", "Liability", 1200.0)
                    );

                    ObservableList<Account> draggableAccounts1 = FXCollections.observableArrayList(
                        new Account("Cash", "Asset", 5),
                        new Account("Original Investment", "Liability", 5)
                    );

                    loadAccountingPanel(defaultAccounts1, draggableAccounts1);
                    question = "Put Cash and Original Investment at Right Place!";
                    
//                learningImageManager.triggerImage("/Images/LearningImage/LearningImage102.png");
                break;
            case 44:
            	if(!isPaneOpen) {
            		toggleAccountsPane();
            	}
            	ObservableList<Account> defaultAccounts2 = FXCollections.observableArrayList(
            			new Account("Cash", "Asset", 5),
                        new Account("Original Investment", "Liability", 5)
                    );

                    ObservableList<Account> draggableAccounts2 = FXCollections.observableArrayList(
                        new Account("Cash", "Asset", 10),
                        new Account("Notes Payable", "Liability", 10)
                    );

                    loadAccountingPanel(defaultAccounts2, draggableAccounts2);
                    question = "Update Cash and Notes Payable at Right Place!";
            	break;
            	
            case 41:
            	learningImageManager.triggerImage("/Images/LearningImage/LearningImage103.png");
            	break;
            	
            
            case 52:
                updateStoreImage("/Images/Store/pappuLemon.png");
                extra.setVisible(true);
                break;
            case 53:
                updateStoreImage("/Images/Store/pappuSugar.png");
                extra.setVisible(true);
                break;
            case 54:
                updateStoreImage("/Images/Store/pappuWater.png");
                extra.setVisible(true);
                break;
            case 55:
            	extra.setVisible(false); 
                break;
            case 57:
                updateStoreImage("/Images/Store/pappuLemon.png");
                extra.setVisible(true);
                break;
            case 58:
            case 59:
            	extra.setVisible(false); 
                break;
            case 60:
                updateStoreImage("/Images/Store/pappuSugar.png");
                extra.setVisible(true);
                break;
            case 61:
            case 62:
            	extra.setVisible(false); 
                break;
            case 67:
            	learningImageManager.triggerImage("/Images/LearningImage/LearningImage104.png");
            	break;
            case 72:
            	if(!isPaneOpen) {
            		toggleAccountsPane();
            	}
            	ObservableList<Account> defaultAccounts3 = FXCollections.observableArrayList(
            			new Account("Cash", "Asset", 15),
                        new Account("Original Investment", "Liability", 5),
                        new Account("Notes Payable", "Liability", 10)
                    );

                    ObservableList<Account> draggableAccounts3 = FXCollections.observableArrayList(
                        new Account("Cash", "Asset", -12),
                        new Account("Inventory", "Asset", 12)
                    );

                    loadAccountingPanel(defaultAccounts3, draggableAccounts3);
                    question = "Update Inventory and Cash at Right Place!";
                    break;
            case 77:
            	learningImageManager.triggerImage("/Images/LearningImage/LearningImage105.png");
            	break;
            default:
                break;
            case 84:
            	saveLayoutData();
        }
    }


    private void updateStoreImage(String imagePath) {
        if (extra != null) {
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            extra.setImage(image);
            extra.setVisible(true); // Ensure the image is visible
        } else {
            System.err.println("Error: storeImageView is not initialized!");
        }
    }

    public void showZoomedImage(Image image) {
        zoomImageView.setImage(image);
        zoomImageView.setVisible(true);
        closeZoomButton.setVisible(true);
    }

    @FXML
    public void closeZoomView(MouseEvent event) {
        zoomImageView.setVisible(false);
        closeZoomButton.setVisible(false);
    }
    
    private void saveLayoutData() {
        // Load existing progress to avoid overwriting previous data
        Map<String, String> oldData = GameProgressManager.loadProgress(); 
        if (oldData == null) {
            oldData = new HashMap<>();
        }

        // Create a LinkedHashMap to maintain order (chapter1 first)
        Map<String, String> layoutData = new LinkedHashMap<>();
        
        // Put chapter1 at the top
        layoutData.put("chapter1", "true");

        // Add all the old data back, preserving previous entries
        layoutData.putAll(oldData);

        // Debugging to check the saved data order
        layoutData.forEach((key, value) -> System.out.println(key + ": " + value));

        // Save the updated progress
        GameProgressManager.saveProgress(layoutData);

        // Load the next scene (chapter 2)
        this.primaryStage = (Stage) rootPane.getScene().getWindow();
        try {
            Pane nextRoot = FXMLLoader.load(getClass().getResource("/FXML/chapter2.fxml"));
            Scene nextScene = new Scene(nextRoot, WelcomeScreen.screenWidth, WelcomeScreen.screenHeight);
            primaryStage.setScene(nextScene);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to load chapter2.fxml");
        }
    }


    
    
}