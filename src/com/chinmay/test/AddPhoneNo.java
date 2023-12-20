package com.chinmay.test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;

import com.database.connection.DBConnection;
import com.shatam.utils.U;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class AddPhoneNo {
	static Connection conn=null;
	public AddPhoneNo() {
		try {
			conn= DBConnection.getConnection("/home/chinmay/Mexico/MexicoDataFiles/Database/", "tequila.db");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void close() {
		try {
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws SQLException {
		AddPhoneNo start=new AddPhoneNo();
//		start.startProcess();
		start._loadallData();
		start.close();
	}
	public void _loadallData() throws SQLException {
		String sqlQuery="select ID,COMPANY_NAME,ADDRESS,CITY,STATE,ZIP from dataset where PHONE is null";
		String sqlQuery1="SELECT ID,COMPANY_NAME,ADDRESS,CITY,STATE,ZIP,PHONE,FAX,URL,EMAIL,YEARS_IN_BIZ from dataset where PHONE is not null";
		Statement stmt=conn.createStatement();
		ArrayList<String[]> dataOutputList=new ArrayList<>();
		ResultSet rs= stmt.executeQuery(sqlQuery);
		MultiValueMap alldata=new MultiValueMap();
		while(rs.next()) {
			String strKey=rs.getString("ADDRESS")+";;"+rs.getString("CITY");
			String strVal=rs.getString("ID")+";;"+rs.getString("STATE")+";;"+rs.getString("ZIP")+";;"+rs.getString("COMPANY_NAME");
			alldata.put(strKey.toLowerCase(), strVal);
		}
		rs.close();
		rs= stmt.executeQuery(sqlQuery1);
		HashSet<String> uniqueSet=new HashSet<>();
		while(rs.next()) {
			String strKey=rs.getString("COMPANY_NAME")+";;"+rs.getString("CITY");
			if (alldata.containsKey(strKey.toLowerCase())) {
				List<String> data=(List<String>) alldata.get(strKey.toLowerCase());
//				String add[]=
				for (String d : data) {
					String arrVal[]=d.split(";;");
					String arrKey[]=strKey.split(";;");
					int simScore=FuzzySearch.tokenSortRatio(arrVal[3], rs.getString("COMPANY_NAME"));
					if(simScore >= 80)
					if (uniqueSet.add(arrVal[0])) {
						String out[]= {arrVal[0],arrKey[0],arrVal[3],arrKey[1],arrVal[1],arrVal[2],rs.getString("ID"),rs.getString("COMPANY_NAME"),rs.getString("ADDRESS"),rs.getString("CITY"),rs.getString("STATE"),rs.getString("ZIP"),rs.getString("PHONE"),rs.getString("FAX"),rs.getString("URL"),rs.getString("EMAIL"),rs.getString("YEARS_IN_BIZ"), ""+simScore};//,,,,,
						dataOutputList.add(out);
					}
				}
			}
		}
		U.writeCsvFile(dataOutputList, "/home/chinmay/Mexico/MexicoDataFiles/Database/TestFolder/Phone_Mar_02.csv");
		rs.close();
		stmt.close();
	}
	public void startProcess() {
		String sqlQuery="select ID,COMPANY_NAME,ADDRESS,CITY,STATE,ZIP from dataset where PHONE is null";
		try {
		ArrayList<String[]> dataOutputList=new ArrayList<>();
		Statement stmt=conn.createStatement();
		ResultSet rs= stmt.executeQuery(sqlQuery);
		int i=0;
		PreparedStatement psmt=conn.prepareStatement("SELECT ID,COMPANY_NAME,ADDRESS,CITY,STATE,ZIP,PHONE,FAX,URL,EMAIL,YEARS_IN_BIZ from dataset where COMPANY_NAME=? AND ADDRESS=? AND CITY=? AND PHONE is not null");
		while(rs.next()) {
			psmt.setString(1, rs.getString("COMPANY_NAME"));
			psmt.setString(2, rs.getString("ADDRESS"));
			psmt.setString(3, rs.getString("CITY"));
//			psmt.setString(4, rs.getString(""));
			ResultSet sr1=psmt.executeQuery();
			while (sr1.next()) {
				U.log((i++)+" -> "+rs.getString("ID")+" --> "+sr1.getString("ID"));
				String out[]= {rs.getString("ID"),rs.getString("COMPANY_NAME"),rs.getString("ADDRESS"),rs.getString("CITY"),rs.getString("STATE"),rs.getString("ZIP"),sr1.getString("ID"),sr1.getString("COMPANY_NAME"),sr1.getString("ADDRESS"),sr1.getString("CITY"),sr1.getString("STATE"),sr1.getString("ZIP"),sr1.getString("PHONE"),sr1.getString("FAX"),sr1.getString("URL"),sr1.getString("EMAIL"),sr1.getString("YEARS_IN_BIZ")};//,,,,,
				dataOutputList.add(out);
			}
			sr1.close();
		}
		U.writeCsvFile(dataOutputList, "/home/chinmay/Mexico/MexicoDataFiles/Database/TestFolder/Phone_Feb_11.csv");
		rs.close();
		psmt.close();
		stmt.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
