package com.saurabh.demo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CSVNumberReplacement {
    
    public static void replaceShortNumbers(String csvFile) {
        String tempFile = "/home/shatam-100/Cache/output.csv";
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                for (int i = 0; i < row.length; i++) {
                    if (row[i].matches("\\d+") && row[i].length() < 12) {
                        row[i] = "REPLACEMENT";
                    }
                }
                writer.write(String.join(",", row));
                writer.newLine();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Rename the temp file to the original filename
        java.io.File temp = new java.io.File(tempFile);
        java.io.File original = new java.io.File(csvFile);
        original.delete();
        temp.renameTo(original);
    }

    public static void main(String[] args) {
        // Replace 'input.csv' with the path to your CSV file
        replaceShortNumbers("/home/shatam-100/Cache/temp.csv");
    }
}
