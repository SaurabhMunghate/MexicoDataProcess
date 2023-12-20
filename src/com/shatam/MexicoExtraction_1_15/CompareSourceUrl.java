package com.shatam.MexicoExtraction_1_15;


import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.database.connection.Connect;
import com.shatam.utils.U;

public class CompareSourceUrl extends Connect{
	//
	public static void main(String[] args) {
		String fileName="/home/chinmay/MexicoCache/Cache/Tiendeo-Supermercados.csv";
		new CompareSourceUrl().compareSourceUrls(fileName);
	}
	private HashMap<String , String> getSourceUrlData(){
		HashMap<String, String> dataSourceUrl=new HashMap<>();
		try {
			String sqlQuery="SELECT ID,_SOURCE_URL from dataset where _SOURCE_URL is not null";
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(sqlQuery);
			while (rs.next()) {
				dataSourceUrl.put(rs.getString("_SOURCE_URL").toLowerCase(), rs.getString("ID"));
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSourceUrl;
	}
	private HashMap<String , String> getPhoneData(){
		HashMap<String, String> dataSourceUrl=new HashMap<>();
		try {
			String sqlQuery="SELECT ID,PHONE from dataset where PHONE is not null";
			Statement stmt=conn.createStatement();
			ResultSet rs=stmt.executeQuery(sqlQuery);
			while (rs.next()) {
				dataSourceUrl.put(rs.getString("PHONE").replaceAll("^52-|-", ""), rs.getString("ID"));
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSourceUrl;
	}
	private void compareSourceUrls(String fileName) {
		try {
			List<String[]> writeData=new ArrayList<String[]>();
			List<String[]> writeDataUnique=new ArrayList<String[]>();
			List<String[]> fileData=U.readCsvFile(fileName);
			HashMap<String, String> dataSourceUrl=getSourceUrlData();
			HashMap<String, String> phoneData=getPhoneData();
			U.log(dataSourceUrl.size());
			U.log(phoneData.size());
			for (String[] row : fileData) {
				if (row[0].contains("ID")) {
					String out[]=new String[row.length+1];
					System.arraycopy(row, 0, out, 0, row.length);
					out[row.length]="ID_MATCHED";
					writeData.add(out);
					writeDataUnique.add(row);
					continue;
				}
//				String idMatched="";
				String phone=row[13];
				if (dataSourceUrl.containsKey(row[24].toLowerCase())) {
					String idMatched=dataSourceUrl.get(row[24].toLowerCase());
					String out[]=new String[row.length+1];
					System.arraycopy(row, 0, out, 0, row.length);
					out[row.length]=idMatched;
					writeData.add(out);
					continue;
				}else if (!U.isEmpty(phone)&&phoneData.containsKey(phone.replaceAll("^52", ""))) {
					String idMatched=phoneData.get(phone.replaceAll("^52", ""));
					String out[]=new String[row.length+1];
					System.arraycopy(row, 0, out, 0, row.length);
					out[row.length]=idMatched;
					writeData.add(out);
					continue;
//					if (row[0].equals("35")) {
//						U.log(phone.replaceAll("^52", ""));
//					}
				}
				
				writeDataUnique.add(row);
			}
			U.writeCsvFile(writeData, fileName.replace(".csv", "_Matched.csv"));
			U.writeCsvFile(writeDataUnique, fileName.replace(".csv", "_Unique.csv"));
		} catch (Exception e) {
		}
	}
}
