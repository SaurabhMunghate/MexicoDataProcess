package com.saurabh.demo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class CSVMatcher {
    public static void main(String[] args) {
        String firstCsvFile = "/home/shatam-100/ResidentialWaterView/riversideCounty1-500 (copy).csv";
        String secondCsvFile = "/home/shatam-100/ResidentialWaterView/Riverside_County_Data1 (copy).csv";

        Set<String> firstColumnData = getColumnData(firstCsvFile, 0);
        Set<String> secondColumnData = getColumnData(secondCsvFile, 1);

        System.out.println(secondColumnData.size());
        System.out.println(firstColumnData.size());
        
        // Find the matching data
//        Set<String> matchedData = new HashSet<>(firstColumnData);
//        matchedData.retainAll(secondColumnData);

        // Print the matched data
//        for (String item : matchedData) {
//            System.out.println(item);
//        }
    }

    private static Set<String> getColumnData(String csvFile, int columnIndex) {
        Set<String> columnData = new HashSet<>();
        String line;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length > columnIndex) {
                    columnData.add(columns[columnIndex]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return columnData;
    }
}
