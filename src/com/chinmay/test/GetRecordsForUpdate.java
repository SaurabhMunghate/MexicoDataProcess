package com.chinmay.test;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class GetRecordsForUpdate {
	static Connection conn;
	public static void main(String[] args) {
		try {
			String searchTerm="'S";
			String replaceTerm="'s";
			HashSet<String> companyNames=new HashSet<>();
			conn=DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
//			conn=DBConnection.getConnection("/home/chinmay/Canada/ryeDatabase/", "canadaDB.db");
			String sqlQuery="SELECT COMPANY_NAME from dataset group by COMPANY_NAME";
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(sqlQuery);
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			while (rs.next()) {
				String company_Name=rs.getString("COMPANY_NAME");
//				U.log(company_Name);
				if (company_Name.contains(searchTerm)) {
					U.log(company_Name);
					companyNames.add(company_Name);
				}
				
			}
			for (String companyName : companyNames) {
				String sqlQuery2="SELECT ID FROM dataset where COMPANY_NAME=\""+companyName+"\"";
//				sqlQuery
				ResultSet rs1=stmt.executeQuery(sqlQuery2);
				while (rs1.next()) {
					String out[]= {rs1.getString("ID"),companyName,companyName.replaceAll(searchTerm, replaceTerm)};
					writer.writeNext(out);
				}
			}
			FileUtil.writeAllText("/home/chinmay/Mexico/MexicoDataFiles/FilesMayBeWrong/CompanyNameUpdate_"+searchTerm+"_.csv", sw.toString());
			writer.close();
			rs.close();
//			StringBuffer str=new Str
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}
