package com.shatam.UpdateRecords_1_15;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.database.connection.Connect;
import com.shatam.utils.U;

public class CheckForRestaurantGuruRecords extends Connect {
	// _SOURCE_URL like "https://restaurantguru.com/%"
	static String HEADER[] = { "ID", "SIC_SUB", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD", "CITY", "STATE", "ZIP",
			"PHONE", "FAX", "URL", "EMAIL", "STATUS", "CONTACT_PERSON", "TITLE", "LATITUDE", "LONGITUDE",
			"YEARS_IN_BIZ", "EMP_COUNT_MIN", "EMP_COUNT_MAX", "ANNUAL_SALES_VOL_MIN", "ANNUAL_SALES_VOL_MAX",
			"CREATED_DATE", "SCORE", "HOURS_OF_OPERATION", "LOCATION_SOURCE", "QUALITY_SCORE", "GEO_DISTANCE",
			"GEO_ACCURACY_CODE", "ADDRESS_VERIFICATION_CODE" };
	public CheckForRestaurantGuruRecords() {
		super();
	}
	public static void main(String[] args) throws SQLException {
		new CheckForRestaurantGuruRecords().processRecord();
	}
	public void processRecord() throws SQLException {
		List<String[]> rowData = U.readCsvFile("/home/chinmay/MexicoCache/Cache/Restaurant Guru_0_100.csv");
		Statement stmt = conn.createStatement();
		List<String[]> writeLines=new ArrayList<>();
		writeLines.add(HEADER);
		for (String[] row : rowData) {
			// for (int i = 0; i < row.length; i++) {
			// U.log(i+" "+row[i]);
			// }
			if (row[22].trim().length() != 0) {
				String query = "select * from dataset where _SOURCE_URL='" + row[25]+"'";
				ResultSet rs = stmt.executeQuery(query);
				while (rs.next()) {
					String out[] = new String[HEADER.length];
					out[0] = rs.getString("ID");
					out[24] = row[22];
					writeLines.add(out);
				}
				rs.close();
			}
			// 22 25
//			break;
		}
		 U.writeCsvFile(writeLines, "/home/chinmay/MexicoCache/Cache/Restaurant Guru_0_100_Update.csv");
	        stmt.close();
	}
}
