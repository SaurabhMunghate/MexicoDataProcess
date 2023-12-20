package com.chinmay.test;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.map.MultiValueMap;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.utils.DB;
import com.shatam.utils.FileUtil;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class CheckStandardFormat {
	private static Connection con;	
	private static Connection conOld;
	
	public static void main(String[] args) {
		String date="2019-03-";
		CheckStandardFormat check=new CheckStandardFormat();
//		check.processDbForMonth(date);
		check.processDMPFile("/home/mypremserver/Downloads/SkypeData/DB/mbd.mar,2019.update");
	}
	public CheckStandardFormat() {
		con=DBConnection.getConnection("/home/chinmay/Mexico/MexicoDataFiles/Db/", Path.TEQUILA_MAIN_DB_NAME);
		conOld=DBConnection.getConnection("/home/chinmay/Mexico/MexicoDataFiles/Db/Old_DB/", Path.TEQUILA_MAIN_DB_NAME);
	}
	private void processDMPFile(String fileName) {
		try {
			String header[]= {"ID","INCORRECT","CORRECT"};
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(header);
			//String fileData=FileUtil.readAllText(fileName);
			//List<String> records =new ArrayList<>(Arrays.asList(fileData.split("\n")));
			HashMap<String, String> dbRecords= getUpdatedDataFromDb();
			
			Set<String>keyset=dbRecords.keySet();
			U.log(keyset.size());
			for (String key : keyset) {
				String oldCompanyData=getDataFromOldDb(key);
				//if (!records.contains(key)) {
				String data[]=dbRecords.get(key).split(";;");
//				U.log(oldCompanyData+""+data[0]);
				if ((!oldCompanyData.equals(data[0]))&&oldCompanyData.equalsIgnoreCase(data[0])) {
					U.log(key+"\t"+oldCompanyData+"\t"+data[0]);
					String out[]= {key,dbRecords.get(key).split(";;")[0],oldCompanyData};
					writer.writeNext(out);
				}
					
				//}
			}
			U.log("COmpleted");
			FileUtil.writeAllText("/home/chinmay/Mexico/MexicoDataFiles/FilesMayBeWrong/Update_May_02.csv", sw.toString());
			writer.close();
			sw.close();
//			U.log(fileData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processDbForMonth(String date) {
		try {
			String header[]= {"ID","INCORRECT","CORRECT"};
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(header);
			HashMap<String, String> dbRecords= getUpdatedDataFromDb();
			U.log(dbRecords.size());
			Set<String> keySet=dbRecords.keySet();
			for (String key : keySet) {
				String dbComName=dbRecords.get(key);
				if (!dbComName.equals(U.toTitleCase(dbComName))) {
					String out[]= {key,dbComName,U.toTitleCase(dbComName)};
					writer.writeNext(out);
				}
			}
			FileUtil.writeAllText("/home/mypremserver/MexicoDataFiles/FilesMayBeWrong/May_2_1.csv", sw.toString());
			writer.close();
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				con.close();
				conOld.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private static HashMap<String, String> getUpdatedDataFromDb() {
		HashMap<String, String> dbRecords=new HashMap<String, String>();
		String query="SELECT COMPANY_NAME,UPDATED_DATE,ID FROM dataset where UPDATED_DATE > datetime(\"2019-04-02 00:00:00\")";
		Statement stmt;
		ResultSet rs;
		try {
			stmt = con.createStatement();
			rs= stmt.executeQuery(query);
			while (rs.next()) {
				String companyName=rs.getString("COMPANY_NAME");
				String id=rs.getString("ID");
				dbRecords.put(id, companyName+";;"+rs.getString("UPDATED_DATE"));
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return dbRecords;
	}
	private static String getDataFromOldDb(String id) {
//		HashMap<String, String> dbRecords=new HashMap<String, String>();
		String query="SELECT COMPANY_NAME,UPDATED_DATE FROM dataset where ID="+id;
		Statement stmt;
		ResultSet rs;
		try {
			stmt = conOld.createStatement();
			rs= stmt.executeQuery(query);
			if (rs.next()) {
				String companyName=rs.getString("COMPANY_NAME");
//				String id=rs.getString("ID");
//				dbRecords.put(id, companyName+";;"+rs.getString("UPDATED_DATE"));
				return companyName;
			}
			stmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
