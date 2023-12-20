package com.chinmay.test;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;

public class CoorectCompanyName {
	public static void main(String[] args) {
		String newDbPath="/home/chinmay/Mexico/MexicoDataFiles/Db/";
		String oldDbPath="/home/chinmay/Mexico/MexicoDataFiles/Db/Old_DB/";
//		String fileName="/home/chinmay/Downloads/FileForTesting_4_Apr_10_1.csv";
		String sqlQueryNew="select ID,COMPANY_NAME,UPDATED_DATE from dataset where COMPANY_NAME like \"%S.C.L.\" AND UPDATED_DATE like \"2019-04-%\"";
		Connection newConn=DBConnection.getConnection(newDbPath, "tequila.db");
		Connection oldConn=DBConnection.getConnection(oldDbPath, "tequila.db");
		HashMap<String, String> companyNameData=new HashMap<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		try {
			Statement stmt=newConn.createStatement();
			ResultSet rs=stmt.executeQuery(sqlQueryNew);
			while (rs.next()) {
				companyNameData.put(rs.getString("ID"), rs.getString("COMPANY_NAME"));
			}
			rs.close();
			stmt.close();
			PreparedStatement pstmt=oldConn.prepareStatement("SELECT COMPANY_NAME from dataset where ID=?");
			String header[]= {"ID","UPDATED_COMPANY_NAME","OLD_COMPANY_NAME","CORRECT_COMPANY_NAME"};
			writer.writeNext(header);
			for (String key : companyNameData.keySet()) {
				String newComName=companyNameData.get(key);
				pstmt.setString(1, key);
				rs=pstmt.executeQuery();
				String oldComName=rs.getString("COMPANY_NAME");
				String out[]= {key,newComName,oldComName,newComName.replace(" S.C.L.", " S.C.")};
				writer.writeNext(out);
			}
			FileUtil.writeAllText("/home/chinmay/Mexico/MexicoDataFiles/FilesMayBeWrong/CompanyNameCorrectionSCL.csv", sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
