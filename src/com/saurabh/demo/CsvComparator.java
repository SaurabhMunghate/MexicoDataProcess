package com.saurabh.demo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CsvComparator {
  
  public static void main(String[] args) {
    
    String csvFile1 = "csv1.csv"; // first CSV file
    String csvFile2 = "csv2.csv"; // second CSV file
    String csvFile3 = "csv3.csv"; // third CSV file
    
    String line = ""; // current line in CSV file
    String delimiter = ","; // delimiter used in CSV file
    
    try (BufferedReader br1 = new BufferedReader(new FileReader(csvFile1));
         BufferedReader br2 = new BufferedReader(new FileReader(csvFile2));
         FileWriter writer = new FileWriter(csvFile3)) {
      
      // Read and skip headers in CSV files
      String header1 = br1.readLine();
      String header2 = br2.readLine();
      
      // Write header to third CSV file
      writer.write(header2 + "\n");
      
      // Read data rows from CSV files
      while ((line = br2.readLine()) != null) {
        
        // Split row data into columns
        String[] columns2 = line.split(delimiter);
        
        // Check if data matches in first CSV file
        boolean dataMatches = false;
        while ((line = br1.readLine()) != null) {
          String[] columns1 = line.split(delimiter);
          if (columns1[8].equals(columns2[8]) && // ADDRESS
              columns1[9].equals(columns2[9]) && // NEIGHBORHOOD
              columns1[10].equals(columns2[10]) && // CITY
              columns1[11].equals(columns2[11]) && // STATE
              columns1[12].equals(columns2[12])) { // ZIP
            dataMatches = true;
            break;
          }
        }
        br1.close(); // close first CSV file
        
        // Write data to third CSV file if data doesn't match
        if (!dataMatches) {
          writer.write(line + "\n");
        }
        
        // Reset reader for first CSV file
//        br1 = new BufferedReader(new FileReader(csvFile1));
        br1.readLine(); // skip header
        
      }
      
      System.out.println("CSV comparison successful.");
      
    } catch (IOException e) {
      System.err.println("Error comparing CSV files: " + e.getMessage());
    }
    
  }
  
}
