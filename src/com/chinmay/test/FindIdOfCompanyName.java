package com.chinmay.test;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class FindIdOfCompanyName {
	String fileName="/home/mypremserver/MexicoDataFiles/Mar/9_MAr/CompanyNamesMismatch_)20_withAdd.csv";
	public static void main(String[] args) {
		FindIdOfCompanyName start=new FindIdOfCompanyName();
		start.process();
		start.close();
	}
	private void process() {
		try {
			String HEADER[]= {"ID","Company_Name","Corrected_Company_Name"};
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int i=0;
			PreparedStatement pstmt=conn.prepareStatement("SELECT ID FROM dataset where COMPANY_NAME =?");
			List<String[]>fileRecords=U.readCsvFile(fileName);
			for (String[] records : fileRecords) {
				String incorrectCompanyName=records[0]/*.length()<records[1].length()?records[0]:records[1]*/;
				String correctCompanyName=records[1]/*.length()>records[1].length()?records[0]:records[1]*/;
				if ((correctCompanyName.contains(")")&&!correctCompanyName.contains("("))||(!correctCompanyName.contains(")")&&correctCompanyName.contains("("))) {
					String temp=incorrectCompanyName;
					incorrectCompanyName=correctCompanyName;
					correctCompanyName=temp;
				}
				U.log((i++)+"======================================================================");
				U.log(correctCompanyName);
				U.log(incorrectCompanyName);
				pstmt.setString(1, incorrectCompanyName);
				ResultSet rs=pstmt.executeQuery();
				while (rs.next()) {
					String out[]= {rs.getString("ID"),incorrectCompanyName,correctCompanyName};
					writer.writeNext(out);
				}
				rs.close();
//				break;
			}
			pstmt.close();
			FileUtil.writeAllText(fileName.replace(".csv", "_forUpdate.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void close() {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	Connection conn=null;
	public FindIdOfCompanyName() {
		try {
			conn=DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
