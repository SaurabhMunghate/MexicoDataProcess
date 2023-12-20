package com.saurabh.demo;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PhoneNumberProcessor {
    public static void main(String[] args) {
        String inputFile = "/home/shatam-100/Cache/input_15may.txt";
        String outputFile = "/home/shatam-100/Cache/output_15may.txt";
        
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            
            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = processPhoneNumber(line);
                writer.write(processedLine);
                writer.newLine();
            }
            
            reader.close();
            writer.close();
            
            System.out.println("Phone numbers processed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static String processPhoneNumber(String line) {
        String processedLine = line;
        
        // Assuming the phone number is enclosed in square brackets "[PHONE]"
        if (line.contains("[PHONE]")) {
            // Extracting the phone number from the line
            String phoneNumber = line.substring(line.indexOf("[PHONE]") + 7);
            
            // Removing non-digit characters from the phone number
            phoneNumber = phoneNumber.replaceAll("[^0-9]", "");
            
            // Checking the length of the phone number
            if (phoneNumber.length() < 12 || phoneNumber.length() > 200) {
                phoneNumber = "0";  // Replacing invalid phone numbers with "0"
            }
            
            // Replacing the original phone number with the processed one
            processedLine = line.replace("[PHONE]", phoneNumber);
        }
        
        return processedLine;
    }
}
