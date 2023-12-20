package com.chinmay.test;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class CreateFileForUpdate {
	Connection conn;

	public CreateFileForUpdate() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
	}

	public static void main(String[] args) {
		String fileName = "/home/chinmay/Mexico/LightBox/noaddressnum_WITHOUT_URL_Maybe_Duplicate.csv";
		CreateFileForUpdate start = new CreateFileForUpdate();
		start.processData(fileName);
		start.close();

	}

	public void close() {
		try {
			conn.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private void processData(String fileName) {
		try {
			List<String[]> fileData = U.readCsvFile(fileName);
			List<String[]> outData = new ArrayList<String[]>();
			int count = 0;
			String sqlQuery = "select ID,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,URL from dataset where ID=";
			Statement stmt = conn.createStatement();
			String header[] = { "DMP_ID", "ID", "SIC_SUB", "COMPANYNAME", "COMPANYNAME_DB", "ADDRESS", "ADDRESS_DB",
					"NEIGHBORHOOD", "NEIGHBORHOOD_DB", "CITY", "CITY_DB", "STATE", "STATE_DB", "ZIP", "ZIP_DB", "PHONE",
					"FAX", "EMAIL", "LONGITUDE", "LATITUDE" };
			outData.add(header);
			for (String[] rowData : fileData) {
				// U.log(rowData[8]);
				if (rowData[0].contains("_dmp_id"))
					continue;
				String dmpId = rowData[0].split("_")[1];
				if (rowData[rowData.length - 1].equals(dmpId))
					continue;
				// U.log(Arrays.toString(rowData));
				count++;
				ResultSet rs = stmt.executeQuery(sqlQuery + rowData[rowData.length - 1]);

				while (rs.next()) {
					if (!rs.getString("ADDRESS").equals(rowData[8])) {
						String out[] = new String[rowData.length];

						int i = 0;

						out[i++] = rowData[0];// DMPID
						
						out[i++] = rs.getString("ID");// DBID
						
						out[i++] = rowData[4];// SIC_SUB

						out[i++] = rowData[7];// COMPANYNAME
						out[i++] = rs.getString("COMPANY_NAME");// DBCOMPANYNAME

						out[i++] = rowData[8];// ADDRESS
						out[i++] = rs.getString("ADDRESS");// DBADDRESS

						out[i++] = rowData[9];// NEIGHBORHOOD
						out[i++] = rs.getString("NEIGHBORHOOD");// DBNEIGHBORHOOD

						out[i++] = rowData[10];// CITY
						out[i++] = rs.getString("CITY");// DBCITY

						out[i++] = rowData[11];// STATE
						out[i++] = rs.getString("STATE");// DBSTATE

						out[i++] = rowData[12];// ZIP
						out[i++] = rs.getString("ZIP");// DBZIP
						
						out[i++] = rowData[13];//
						
						out[i++] = rowData[14];//
						
						out[i++] = rowData[16];//
						
						out[i++] = rowData[rowData.length - 3];//
						
						out[i++] = rowData[rowData.length - 2];//
						
						outData.add(out);
						
						count++;
					}
				}
				rs.close();
				// break;
			}
			stmt.close();
			U.writeCsvFile(outData, fileName.replace(".csv", "_Exact_Match_1.csv"));
			U.log(outData.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}