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
        Map<String, String> progress = loadProgress();
        for (int i = 0; i <= 10; i++) { // Adjust the range if you have more chapters
            String chapterKey = "chapter" + i;
            if (progress.containsKey(chapterKey) && "true".equals(progress.get(chapterKey))) {
                return i + 1; // Return next chapter (i+1) to load
            }
        }
        return -1; // Default if no progress found
    }
}
