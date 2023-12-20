package com.saurabh.demo;
import java.io.*;
import java.sql.*;

public class TequilaDBTakVal {
    public static void main(String[] args) throws SQLException, NumberFormatException, IOException {
        // Establish connection to the SQLite database
        Connection conn = DriverManager.getConnection("jdbc:sqlite:/home/shatam-100/CODE_Repository/Maxico/tequila.db_latest/tequila.db");

        // Read the ID values from the first CSV file
        String csvFile = "/home/shatam-100/Desktop/WaterView_CII/MexicoData_31March2023.csv";
        BufferedReader br = new BufferedReader(new FileReader(csvFile));
        String sql = "SELECT * FROM Dataset WHERE ID LIKE ?";

        String line;
        int i = 0;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            if(values[0].contains("ID"))continue;
            int id = Integer.parseInt(values[0]);

            // Query the database for data matching the ID value
//            String sql = "SELECT * FROM dataset WHERE id  = ?";
//            String sql = "SELECT * FROM Dataset WHERE ID LIKE ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.addBatch();
            ResultSet rs = pstmt.executeQuery();

            // Write the results to a new CSV file
            String outputCsvFile = "/home/shatam-100/Desktop/WaterView_CII/output_1.csv";
            FileWriter fw = new FileWriter(outputCsvFile, true);
            String st = "";
            while (rs.next()) {
//                fw.append(rs.getInt("id") + "," + rs.getString("INDUSTRY_SECTOR") + "," + rs.getString("SPANISH_INDUSTRY_SECTOR") +"ID	SIC_SUB	COMPANY_NAME	ADDRESS	NEIGHBORHOOD	CITY	STATE	ZIP	PHONE	FAX	URL	EMAIL	STATUS	CONTACT_PERSON	TITLE	LATITUDE	LONGITUDE	YEARS_IN_BIZ	EMP_COUNT_MIN	EMP_COUNT_MAX	ANNUAL_SALES_VOL_MIN	ANNUAL_SALES_VOL_MAX	CREATED_DATE	SCORE	HOURS_OF_OPERATION	LOCATION_SOURCE	QUALITY_SCORE	GEO_DISTANCE	GEO_ACCURACY_CODE	ADDRESS_VERIFICATION_CODE"+ "\n");
//            	fw.append(rs.getInt("ID") + "," + rs.getString("SIC_SUB") + "," + rs.getString("COMPANY_NAME") + "," + rs.getString("ADDRESS") + "," + rs.getString("NEIGHBORHOOD") + "," + rs.getString("CITY") + "," + rs.getString("STATE") + "," + rs.getString("ZIP") + "," + rs.getString("PHONE") + "," + rs.getString("FAX") + "," + rs.getString("URL") + "," + rs.getString("EMAIL") + "," + rs.getString("STATUS") + "," + rs.getString("TITLE") + "," + rs.getString("LATITUDE") + "," + rs.getString("LONGITUDE") + "," + rs.getString("YEARS_IN_BIZ") + "," + rs.getString("EMP_COUNT_MIN") + "," + rs.getString("EMP_COUNT_MAX")+ "," + rs.getString("ANNUAL_SALES_VOL_MIN")+ "," + rs.getString("ANNUAL_SALES_VOL_MAX")+ "," + rs.getString("CREATED_DATE")+ "," + rs.getString("SCORE")+ "," + rs.getString("HOURS_OF_OPERATION")+ "," + rs.getString("LOCATION_SOURCE")+ "," + rs.getString("QUALITY_SCORE")+ "," + rs.getString("GEO_DISTANCE") +"," + rs.getString("GEO_ACCURACY_CODE") +"," + rs.getString("ADDRESS_VERIFICATION_CODE")+ "\n");

//            	fw.write(st);
//            	st += rs.getInt("ID") + "," + rs.getString("INDUSTRY_SECTOR")+ "," + rs.getString("SPANISH_INDUSTRY_SECTOR")+ "," + rs.getString("SIC_MAJOR")+ "," + rs.getString("PRODUCT_DESC")+ "," + rs.getString("SPANISH_PRODUCT_DESC")+ "," + rs.getString("COMPANY_NAME")+ "," + rs.getString("ADDRESS")+ "," + rs.getString("NEIGHBORHOOD")+ "," + rs.getString("CITY")+ "," + rs.getString("STATE")+ "," + rs.getString("ZIP")+ "," + rs.getString("PHONE")+ "," + rs.getString("FAX")+ "," + rs.getString("URL")+ "," + rs.getString("EMAIL")+ "," + rs.getString("CONTACT_PERSON")+ "," + rs.getString("TITLE")+ "," + rs.getString("ANNUAL_SALES_VOL_MIN")+ "," + rs.getString("ANNUAL_SALES_VOL_MAX")+ "," + rs.getString("EMP_COUNT_MIN")+ "," + rs.getString("EMP_COUNT_MAX")+ "," + rs.getString("YEARS_IN_BIZ")+ "," + rs.getString("LONGITUDE")+ "," + rs.getString("LATITUDE")+ "," + rs.getString("ADDRESS_VERIFICATION_CODE")+ "," + rs.getString("GEO_ACCURACY_CODE")+ "," + rs.getString("GEO_DISTANCE")+ "," + rs.getString("QUALITY_SCORE")+ "," + rs.getString("LOCATION_SOURCE")+ "," + rs.getString("Hours_Of_Operation")+ "," + rs.getString("CREATED_DATE")+ "," + rs.getString("UPDATED_DATE")+ "," + rs.getString("DELETED_DATE")+ "," + rs.getString("_SCORE")+ "," + rs.getString("_SOURCE_URL")+ "," + rs.getString("_STATUS")+ "," + rs.getString("_LAST_UPDATED_BY")+ "," + rs.getString("_DELETED")+ "\n";

//            	fw.append(rs.getInt("ID") + "," + rs.getString("INDUSTRY_SECTOR")+ "," + rs.getString("SPANISH_INDUSTRY_SECTOR")+ "," + rs.getString("SIC_MAJOR")+ "," + rs.getString("PRODUCT_DESC")+ "," + rs.getString("SPANISH_PRODUCT_DESC")+ "," + rs.getString("COMPANY_NAME")+ "," + rs.getString("ADDRESS")+ "," + rs.getString("NEIGHBORHOOD")+ "," + rs.getString("CITY")+ "," + rs.getString("STATE")+ "," + rs.getString("ZIP")+ "," + rs.getString("PHONE")+ "," + rs.getString("FAX")+ "," + rs.getString("URL")+ "," + rs.getString("EMAIL")+ "," + rs.getString("CONTACT_PERSON")+ "," + rs.getString("TITLE")+ "," + rs.getString("ANNUAL_SALES_VOL_MIN")+ "," + rs.getString("ANNUAL_SALES_VOL_MAX")+ "," + rs.getString("EMP_COUNT_MIN")+ "," + rs.getString("EMP_COUNT_MAX")+ "," + rs.getString("YEARS_IN_BIZ")+ "," + rs.getString("LONGITUDE")+ "," + rs.getString("LATITUDE")+ "," + rs.getString("ADDRESS_VERIFICATION_CODE")+ "," + rs.getString("GEO_ACCURACY_CODE")+ "," + rs.getString("GEO_DISTANCE")+ "," + rs.getString("QUALITY_SCORE")+ "," + rs.getString("LOCATION_SOURCE")+ "," + rs.getString("Hours_Of_Operation")+ "," + rs.getString("CREATED_DATE")+ "," + rs.getString("UPDATED_DATE")+ "," + rs.getString("DELETED_DATE")+ "," + rs.getString("_SCORE")+ "," + rs.getString("_SOURCE_URL")+ "," + rs.getString("_STATUS")+ "," + rs.getString("_LAST_UPDATED_BY")+ "," + rs.getString("_DELETED")+ "\n");
//            	fw.append(rs + "\n");
            	System.out.println(""+i);i++;
            }
            fw.write(st);
            fw.flush();
            fw.close();
        }
        br.close();

        // Close the database connection
        conn.close();
        System.out.println("DATA added to csv");
    }
}
