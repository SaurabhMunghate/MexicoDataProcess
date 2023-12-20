
package com.saurabh.demo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CSVProcessor {
    
    public static void replaceShortNumbers(String csvFile) {
        String tempFile = "/home/shatam-100/Cache/textFile.txt";
        String line;

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            
            while ((line = reader.readLine()) != null) {
                String[] row = line.split(",");
                for (int i = 0; i < row.length; i++) {
//                    try {
                        String number = row[i];
                        if (String.valueOf(number).length() < 12) {
//                            row[i] = "REPLACEMENT";
                            System.out.println(number);
                        }
//                    } catch (NumberFormatException e) {
//                        // Ignore non-numeric values
//                    }
                }
                writer.write(String.join(",", row));
                writer.newLine();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Rename the temp file to the original filename
        File temp = new File(tempFile);
        File original = new File(csvFile);
        original.delete();
        temp.renameTo(original);
    }

    public static void main(String[] args) {
        // Replace 'input.csv' with the path to your CSV file
        replaceShortNumbers("/home/shatam-100/Cache/temp.csv");
    }
}
