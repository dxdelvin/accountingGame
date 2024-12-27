package com.accounting.main;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class DialogueSystem {	

    private List<Dialogue> dialogues;
    private String brandName;

    // Modified constructor to accept a filename
    public DialogueSystem(String filename, String brandName) {
    	this.brandName = brandName;
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
    
    private void replaceBrandNameInDialogues() {
        for (Dialogue dialogue : dialogues) {
            if (dialogue.getText().contains("{BrandName}")) {
                String updatedText = dialogue.getText().replace("{BrandName}", brandName);
                dialogue.setText(updatedText);  // Update the text with the brand name
            }
        }
    }

    // Get a dialogue by ID
    public Dialogue getDialogue(int id) {
        return dialogues.stream()
                .filter(dialogue -> dialogue.getId() == id)
                .findFirst()
                .orElse(null);
    }

    // Helper class to match the root node of the JSON
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
