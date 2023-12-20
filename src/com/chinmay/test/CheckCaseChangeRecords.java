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
import com.shatam.utils.U;

public class CheckCaseChangeRecords {
	public static void main(String[] args) {
		String newDbPath="/home/chinmay/Mexico/MexicoDataFiles/Db/";
		String oldDbPath="/home/chinmay/Mexico/MexicoDataFiles/Db/Old_DB/";
		Connection newConn=DBConnection.getConnection(newDbPath, "tequila.db");
		Connection oldConn=DBConnection.getConnection(oldDbPath, "tequila.db");
		String sqlQueryNew="select ID,COMPANY_NAME from dataset where UPDATED_DATE like \"2019-04-%\" AND UPDATED_DATE not like \"2019-04-01%\"";
		HashMap<String, String> companyNameData=new HashMap<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		try {
			Statement stmt=newConn.createStatement();
			ResultSet rs=stmt.executeQuery(sqlQueryNew);
			while (rs.next()) {
				companyNameData.put(rs.getString("ID"), rs.getString("COMPANY_NAME"));
			}
			PreparedStatement pstmt=oldConn.prepareStatement("SELECT COMPANY_NAME from dataset where ID=?");
			String header[]= {"ID","UPDATED_COMPANY_NAME","OLD_COMPANY_NAME"};
			writer.writeNext(header);
			for (String key : companyNameData.keySet()) {
				String newComName=companyNameData.get(key);
				pstmt.setString(1, key);
				rs=pstmt.executeQuery();
				String oldComName=rs.getString("COMPANY_NAME");
				if ((!newComName.equals(oldComName))&&newComName.equalsIgnoreCase(oldComName)) {
					String out[]= {key,newComName,oldComName};
					writer.writeNext(out);
				}
			}
			pstmt.close();
			FileUtil.writeAllText(newDbPath+"RecordsWithCaseChange"+U.getTodayDateWith()+".csv", sw.toString());
			writer.close();
			sw.close();
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
