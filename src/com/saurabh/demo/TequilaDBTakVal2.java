package com.saurabh.demo;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class TequilaDBTakVal2 {
    public static void main(String[] args) throws SQLException, NumberFormatException, IOException {
 
        Connection conn = DriverManager.getConnection("/home/shatam-100/CODE_Repository/Maxico/tequila.db_latest/tequila.db");

    	// Read the ID values from the CSV file and store them in a list
    	String csvFile = "/home/shatam-100/Down/AllMexico_7_43889_.csv";
    	List<Integer> ids = new ArrayList<>();
    	BufferedReader br = new BufferedReader(new FileReader(csvFile));
    	String line;
    	while ((line = br.readLine()) != null) {
    	    String[] values = line.split(",");
    	    if (values[0].contains("ID")) continue;
    	    int id = Integer.parseInt(values[0]);
    	    ids.add(id);
    	}

    	// Query the database for all the IDs at once using the IN operator
    	String sql = "SELECT * FROM dataset WHERE id IN (" + 
    	    ids.stream().map(Object::toString).collect(Collectors.joining(",")) + ")";
    	PreparedStatement pstmt = conn.prepareStatement(sql);
    	ResultSet rs = pstmt.executeQuery();

    	// Write the results to a new CSV file
    	String outputCsvFile = "/home/shatam-100/Desktop/WaterView_CII/up2.csv";
    	try (FileWriter fw = new FileWriter(outputCsvFile)) {
    	    while (rs.next()) {
    	        fw.append(rs.getInt("id") + "," + rs.getString("column1") + "," + rs.getString("column2") + "\n");
    	    }
    	}

    }
}

