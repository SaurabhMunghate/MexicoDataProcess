package com.sawan.demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.database.connection.DBConnection;
import com.shatam.utils.U;

public class ValidateEmailAddressCanadaDB {
	static String DBpath="/home/chinmay/Canada/ryeDatabase/";
	static String dbName="canadaDB.db";
	static String dbquery="SELECT * FROM dataset where SIC_MAJOR=50 AND EMAIL is not NULL";
	static String rows[]= {"ID", "INDUSTRY_SECTOR", "FRENCH_INDUSTRY_SECTOR", "SIC_MAJOR", "SIC_SUB", "PRODUCT_DESC", "FRENCH_PRODUCT_DESC", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD", "CITY", "STATE", "ZIP", "PHONE", "FAX", "URL", "EMAIL", "CONTACT_PERSON", "TITLE", "ANNUAL_SALES_VOL", "EMP_COUNT", "OPENED_IN", "LONGITUDE", "LATITUDE"};
	public static void main(String[] args) {
		try {
			startPorcess();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	static int j=0;
	static ArrayList<String[]> outData=new ArrayList<>();
	static HashSet<String[]> outData10K=new HashSet<>();
	static ArrayList<String[]> invalidData=new ArrayList<>();
	private static void startPorcess() throws SQLException {
		Connection conn=DBConnection.getConnection(DBpath, dbName);
		Statement stmt=conn.createStatement();
		ResultSet rs=stmt.executeQuery(dbquery);
		while(rs.next()) {
			
			String emaildata=rs.getString("EMAIL");
			List<String> invalidEmail = U.invalidEmailList(emaildata);
			if(invalidEmail == null||invalidEmail.isEmpty()){
					String out[]=new String[rows.length];
					for (int i = 0; i < out.length; i++)
						out[i]=rs.getString(i+1);
					j++;
					outData.add(out);
					if (outData10K.size()<10001) {
						String aout[]=new String[3];
						aout[0]=rs.getString("ID");
						aout[1]=rs.getString("COMPANY_NAME");
						aout[2]=rs.getString("EMAIL");
						outData10K.add(out);
					}
					if (j%100==0) {
						U.log("->> "+j);
					}
			}else {
				String out[]=new String[rows.length];
				for (int i = 0; i < out.length; i++)
					out[i]=rs.getString(i+1);
//				j++;
				invalidData.add(out);
			}
		}
		U.writeCsvFile(new ArrayList<String[]>(outData10K), "/home/chinmay/Canada/ryeDatabase/SampleMail10KReocrds.csv");
		U.writeCsvFile(outData, "/home/chinmay/Canada/ryeDatabase/SampleMailReocrds.csv");
		U.writeCsvFile(invalidData, "/home/chinmay/Canada/ryeDatabase/SampleMailReocrdsInvalid.csv");
		rs.close();
		stmt.close();
		conn.close();
	}
}
