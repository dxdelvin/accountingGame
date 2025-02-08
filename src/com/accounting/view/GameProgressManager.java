package com.accounting.view;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GameProgressManager {

    private static final String FILE_PATH = "gameProgress.txt"; // Default file path

    // Save game progress to a file
    public static void saveProgress(Map<String, String> data) {
        File file = new File(FILE_PATH);
        try {
            if (!file.exists()) {
                if (file.createNewFile()) {
                    System.out.println("File created successfully: " + file.getAbsolutePath());
                } else {
                    System.out.println("Failed to create the file.");
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    writer.write(entry.getKey() + "=" + entry.getValue());
                    writer.newLine();
                }
                System.out.println("Game progress saved successfully.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load game progress from a file
    public static Map<String, String> loadProgress() {
        Map<String, String> data = new HashMap<>();
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            System.out.println("No saved progress found.");
            return data; // Return empty data if the file doesn't exist
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    data.put(parts[0], parts[1]);
                }
            }
            System.out.println("Game progress loaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    // Get the next chapter to load based on the progress
    public static int getNextChapterToLoad() {
    	Map<String, String> progressData = loadProgress(); // Load saved progress

        if (progressData == null || progressData.isEmpty()) {
            return -1; // No saved progress
        }

        int highestChapter = 0;

        // Loop through saved data to find the highest unlocked chapter
        for (String key : progressData.keySet()) {
            if (key.startsWith("chapter")) { // Check for "chapterX" keys
                try {
                    int chapterNum = Integer.parseInt(key.replace("chapter", ""));
                    if (progressData.get(key).equalsIgnoreCase("true")) {
                        highestChapter = Math.max(highestChapter, chapterNum);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid chapter format: " + key);
                }
            }
        }

        return (highestChapter > 0) ? highestChapter + 1 : -1; // Return next chapter number
    }
}
