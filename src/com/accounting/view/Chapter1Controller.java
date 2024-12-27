package com.accounting.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
    private AnchorPane accountsPane;

    @FXML
    private Button accountsButton;
    
    private boolean isPaneOpen = false;
    
    // Variables to hold game progress data
    private String brandName;
    private boolean chapter0;
    
    public void initialize() {
        // Read data from the gameProgress.txt file
        readGameProgressFromFile("gameProgress.txt");
        
        accountsPane.setTranslateX(-500); 
        isPaneOpen = false;
        accountsButton.setOnAction(event -> toggleAccountsPane());
    }

    private void readGameProgressFromFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split("=");
                if (data.length == 2) {
                    String key = data[0].trim();
                    String value = data[1].trim();

                    // Parse game progress data
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

    // Getter methods for brandName and chapter0 (if needed elsewhere in the game)
    public String getBrandName() {
        return brandName;
    }

    public boolean isChapter0() {
        return chapter0;
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
}
