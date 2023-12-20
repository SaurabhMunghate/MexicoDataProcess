package com.saurabh.demo;
import java.io.*;
import java.util.*;

public class CsvTransformer {
    public static void main(String[] args) throws IOException {
        // open the first CSV file for reading
        BufferedReader reader = new BufferedReader(new FileReader("/home/shatam-100/Desktop/WaterView_CII/AllMexico_8_2_2_2001_3999.csv"));
        // create a list to store the data for the second file
        List<Map<String, String>> data = new ArrayList<>();
        // read the header row
//        String[] headers = reader.readLine().split(",");
        String headLine = "ID,SIC_SUB,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,FAX,URL,EMAIL,STATUS,CONTACT_PERSON,TITLE,LATITUDE,LONGITUDE,YEARS_IN_BIZ,EMP_COUNT_MIN,EMP_COUNT_MAX,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,CREATED_DATE,SCORE,HOURS_OF_OPERATION,LOCATION_SOURCE,QUALITY_SCORE,GEO_DISTANCE,GEO_ACCURACY_CODE,ADDRESS_VERIFICATION_CODE";
        
        String[] newHeaders = {
                "ID", "SIC_SUB", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD",
                "CITY", "STATE", "ZIP", "PHONE", "FAX", "URL", "EMAIL",
                "STATUS", "CONTACT_PERSON", "TITLE", "LATITUDE", "LONGITUDE",
                "YEARS_IN_BIZ", "EMP_COUNT_MIN", "EMP_COUNT_MAX",
                "ANNUAL_SALES_VOL_MIN", "ANNUAL_SALES_VOL_MAX", "CREATED_DATE",
                "SCORE", "HOURS_OF_OPERATION", "LOCATION_SOURCE", "QUALITY_SCORE",
                "GEO_DISTANCE", "GEO_ACCURACY_CODE", "ADDRESS_VERIFICATION_CODE"
        };

        String[] headers = headLine.split(",");
//        String[] headers = newHeaders;
        // iterate through each row in the first file
        String row;
        while ((row = reader.readLine()) != null) {
            // split the row into cells
            String[] cells = row.split(",");
            // extract the desired data from the cells and create a new map
            Map<String, String> newRow = new HashMap<>();
            newRow.put("ID", cells[0]);
            newRow.put("SIC_SUB", cells[4]);
            newRow.put("COMPANY_NAME", cells[7]);
            newRow.put("ADDRESS", cells[8]);
            newRow.put("NEIGHBORHOOD", cells[9]);
            newRow.put("CITY", cells[10]);
            newRow.put("STATE", cells[11]);
            newRow.put("ZIP", cells[12]);
            newRow.put("PHONE", cells[13]);
            newRow.put("FAX", cells[14]);
            newRow.put("URL", cells[15]);
            newRow.put("EMAIL", cells[16]);
            newRow.put("STATUS", cells[35]);
            newRow.put("CONTACT_PERSON", cells[17]);
            newRow.put("TITLE", cells[18]);
            newRow.put("LATITUDE", cells[26]);
            newRow.put("LONGITUDE", cells[25]);
            newRow.put("YEARS_IN_BIZ", cells[22]);
            newRow.put("EMP_COUNT_MIN", cells[20]);
            newRow.put("EMP_COUNT_MAX", cells[21]);
            newRow.put("ANNUAL_SALES_VOL_MIN", cells[19]);
            newRow.put("ANNUAL_SALES_VOL_MAX", cells[20]);
            newRow.put("CREATED_DATE", cells[31]);
            newRow.put("SCORE", cells[34]);
            newRow.put("HOURS_OF_OPERATION", cells[30]);
            newRow.put("LOCATION_SOURCE", cells[28]);
            newRow.put("QUALITY_SCORE", cells[27]);
            newRow.put("GEO_DISTANCE", cells[24]);
            newRow.put("GEO_ACCURACY_CODE", cells[27]);
            newRow.put("ADDRESS_VERIFICATION_CODE", cells[28]);
            // add the new map to the data list
//            System.out.println("add the new map to the data list");
//            System.out.println(newRow.size());
            data.add(newRow);
        }
        reader.close();
        System.out.println(data.size());

        // open the second CSV file for writing
        BufferedWriter writer = new BufferedWriter(new FileWriter("/home/shatam-100/Desktop/WaterView_CII/up2.csv"));
        // write the header row
//        for (int i = 0; i < headers.length; i++) {
//            if (i > 0) {
//                writer.write(",");
//            }
//            writer.write(headers[i]);
//        }
//        writer.newLine();
        // write the data rows
        int j = 0;int k = 0;
        System.out.println(headers.length);
        for (Map<String, String> rowMap : data) {
            for (int i = 0; i < headers.length; i++) {
//                if (i > 0) {
                    writer.write(",");
//                }
                String cell = rowMap.get(headers[i]);
//                System.out.println(cell);
//                if(cell.contains("www.burgerking.com.mx"))System.out.println(cell);
//                if(cell.isBlank())cell="-";
                if (cell != null) {
                    writer.write(cell);
//                    k++;
                }
//                if(i>=50);continue;
//                    System.out.println("C ::: "+i);
            }
//            writer.write("\n");
            writer.newLine();
//            System.out.println("C ::: "+j);j++;
        }
        writer.close();
//        System.out.println("K ::: "+k);
        
        System.out.println("Writer Done");
    }
}
