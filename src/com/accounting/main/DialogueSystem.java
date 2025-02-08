package com.accounting.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class DialogueSystem {

    private List<Dialogue> dialogues;
    private String brandName;
    private ImageView characterImage;

    // Modified constructor to accept a filename and an ImageView for the character
    public DialogueSystem(String filename, String brandName, ImageView characterImage) {
        this.brandName = brandName;
        this.characterImage = characterImage;
        loadDialogues(filename);
    }

    // Load dialogues from the JSON file
    private void loadDialogues(String filename) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            File jsonFile = new File("bin/dialogues/" + filename); // Use the provided filename
            DialogueList dialogueList = objectMapper.readValue(jsonFile, DialogueList.class);
            dialogues = dialogueList.getDialogues();
            replaceBrandNameInDialogues();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Replace placeholder "{BrandName}" in dialogues with the actual brand name
    private void replaceBrandNameInDialogues() {
        for (Dialogue dialogue : dialogues) {
            if (dialogue.getText().contains("{BrandName}")) {
                String updatedText = dialogue.getText().replace("{BrandName}", brandName);
                dialogue.setText(updatedText);  // Update the text with the brand name
            }
        }
    }
    
    public static boolean isDialoguePaused = false;

    public Dialogue getDialogue(int id) {
    	if (isDialoguePaused) {
            System.out.println("Dialogue is paused. Cannot proceed.");
        }
        Dialogue dialogue = dialogues.stream()
                .filter(d -> d.getId() == id)
                .findFirst()
                .orElse(null);

        if (dialogue != null && dialogue.getCharacterImagePath() != null) {
            updateCharacterImage(dialogue.getCharacterImagePath());
        }

        return dialogue;
    }
    
    public static void pauseDialogue() {
        isDialoguePaused = true;
    }

    public static void resumeDialogue() {
        isDialoguePaused = false;
    }

    
    private void updateCharacterImage(String imagePath) {
        try {
            Image image = new Image(getClass().getResource(imagePath).toExternalForm());
            characterImage.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading character image: " + imagePath);
            e.printStackTrace();
        }
    }
    
    private static class DialogueList {
        private List<Dialogue> dialogues;

        public List<Dialogue> getDialogues() {
            return dialogues;
        }

        public void setDialogues(List<Dialogue> dialogues) {
            this.dialogues = dialogues;
        }
    }
}
