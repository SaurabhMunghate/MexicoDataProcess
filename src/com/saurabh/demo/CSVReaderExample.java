package com.saurabh.demo;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVReaderExample {
    public static void main(String[] args) {
        String csvFilePath = "/home/shatam-100/Cache/temp.csv";
        String columnName = "PHONE";

        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath))) {
            // Read all rows from the CSV file
            List<String[]> rows = reader.readAll();

            // Get the header row
            String[] headers = rows.get(0);

            // Find the index of the PHONE column
            int columnIndex = -1;
            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equals(columnName)) {
                    columnIndex = i;
                    break;
                }
            }

            if (columnIndex != -1) {
                // Filter out rows with phone numbers length less than 12
                List<String[]> filteredRows = new ArrayList<>();
                filteredRows.add(headers);

                for (int i = 1; i < rows.size(); i++) {
                    String[] row = rows.get(i);
                    if (row[columnIndex].length() >= 12) {
                    	System.out.println(row[columnIndex]);
                        
                    }
                    filteredRows.add(row);
                }

                // Write the filtered rows back to a new CSV file
                try (FileWriter writer = new FileWriter("/home/shatam-100/Cache/Untitled 1.csv")) {
                    for (String[] filteredRow : filteredRows) {
                        writer.write(String.join(",", filteredRow));
//                    	 writer.write(filteredRow.toString());
                        writer.write("\n");
                    }
                    System.out.println("Filtered file created successfully.");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Column name not found.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
