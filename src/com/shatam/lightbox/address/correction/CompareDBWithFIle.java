package com.shatam.lightbox.address.correction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.database.connection.DBConnection;
import com.shatam.utils.Field;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Validator;

public class CompareDBWithFIle {
	public final String FILEPATH = "/home/chinmay/Mexico/LightBox/noaddressnum.txt";
	Connection conn = null;

	public static void main(String[] args) {
		CompareDBWithFIle comp = new CompareDBWithFIle();
		comp.compareDB();
		comp.close();
		//comp.recordsWithWebsite();
		//comp.reportOfAddress();
		//comp.reportOfFile();
	}

	private void reportOfAddress() {
		List<String[]> fileData = readFile(FILEPATH);
		//int[] countData=new int[fileData.get(0).length];
		int count=0;
		for (String[] rowData : fileData) {
			try {
				Validator.city(Field.CITY, rowData[10]);
				Validator.state(Field.STATE, rowData[11]);
				Validator.zip(Field.ZIP, rowData[12]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(rowData[8].endsWith("S/N"))
				count++;
		}
		U.log(count);
	}
	
	private void reportOfFile() {
		List<String[]> fileData = readFile(FILEPATH);
		int[] countData=new int[fileData.get(0).length];
		for (String[] rowData : fileData) {
			if(rowData[0].contains("_dmp_id"))continue;
			for (int i = 0; i < rowData.length; i++) {
				if(rowData[i].length()>0) {
					countData[i]++;
				}
			}
		}
		for (int i = 0; i < countData.length; i++) {
			U.log(fileData.get(0)[i]+" \t "+countData[i]);
		}
		U.log(Arrays.toString(countData));
	}

	private void recordsWithWebsite() {
		// TODO Auto-generated method stub
		List<String[]> fileData = readFile(FILEPATH);
		List<String[]> outdata = new ArrayList<>();
		List<String[]> outdataWithoutWebs = new ArrayList<>();
		for (String[] rowData : fileData) {
			if(rowData[0].contains("_dmp_id")) {
				outdataWithoutWebs.add(rowData);
				outdata.add(rowData);
				continue;
			}
			U.log(rowData[15]);
			if(rowData[15].length()!=0) {
				outdata.add(rowData);
			}else {
				outdataWithoutWebs.add(rowData);
			}
		}
		U.writeCsvFile(outdata, FILEPATH.replaceAll(".txt", "_WITH_URL.csv"));
		U.writeCsvFile(outdataWithoutWebs, FILEPATH.replaceAll(".txt", "_WITHOUT_URL.csv"));
	}

	public CompareDBWithFIle() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
	}
	public void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	String querySB="select * from dataset where ID=?";
	PreparedStatement pstmt=null;
	ResultSet rs=null; 
	public void compareDB() {
		List<String[]> fileData = readFile(FILEPATH);
		U.log("Processing Data....");
		List<String[]> not_Updated_Data = new ArrayList<>();
		List<String[]> updated_Data = new ArrayList<>();
		try {
			int count=0,notmatched=0,matched=0;
			pstmt=conn.prepareStatement(querySB);
			String header[]= {"_DMP_ID","DB_ID","MAJOR_SIC","SUB_SIC","COMPANY_NAME","ADDRESS_FILE","ADDRESS_DB","NEIGHBOURHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","EMAIL","LONGITUDE","LATITUDE","UPDATED_DATE","SOURCE_URL"};
			updated_Data.add(header);
			for (String[] rowData : fileData) {
				if(rowData[0].contains("_dmp_id"))
					continue;
				//U.log(count+" "+rowData[0]);
				String dbID=rowData[0].split("_")[1];
//				U.log(querySB+dbID);
				count++;
//				if(count==1000) {
//					U.log(count);
//					break;
//				}
				pstmt.setString(1, dbID);
//				pstmt.setString(2, rowData[8]);
				rs=pstmt.executeQuery();
				String out[]=new String[header.length];
				while(rs.next()) {
//					U.log(rs.getObject("ADDRESS"));
					if(!(rowData[8].equals(rs.getObject("ADDRESS"))&& rowData[7].equals(rs.getObject("COMPANY_NAME")))){
						notmatched++;
					}else {
						matched++;
					}
//					if(rowData[8].equals(rs.getObject("ADDRESS"))&& rowData[7].equals(rs.getObject("COMPANY_NAME"))) {
//						not_Updated_Data.add(rowData);
//					}else if(rowData[7].equals(rs.getObject("COMPANY_NAME"))){
//						int i=0;
//						out[i++]=rowData[0];
//						out[i++]=rs.getString("ID");
//						out[i++]=rowData[3];
//						out[i++]=rowData[4];
//						out[i++]=rowData[7];
//						out[i++]=rowData[8];
//						out[i++]=rs.getString("ADDRESS");
//						out[i++]=rowData[9];
//						out[i++]=rowData[10];
//						out[i++]=rowData[11];
//						out[i++]=rowData[12];
//						out[i++]=rowData[13];
//						out[i++]=rowData[14];
//						out[i++]=rowData[15];
//						out[i++]=rowData[16];
//						out[i++]=rowData[24];
//						out[i++]=rowData[25];
//						out[i++]=rs.getString("UPDATED_DATE");
//						out[i++]=rs.getString("_SOURCE_URL");
//						updated_Data.add(out);
//					}
				}
			}
			U.log(notmatched);
			U.log(matched);
			//U.writeCsvFile(not_Updated_Data, FILEPATH.replaceAll(".txt", "_NOT_UPDATED_1000.csv"));
			//U.writeCsvFile(updated_Data, FILEPATH.replaceAll(".txt", "_UPDATED_1000.csv"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public List<String[]> readFile(String fileName) {
		List<String[]> outData = new ArrayList<String[]>();
		try {
			U.log("Reading File....");
			List<String> readData	=Files.readAllLines(Paths.get(fileName));
			for (String str : readData) {
				String[] putStr=str.split("\\|");
				outData.add(putStr);
			}
			U.log("File Reading Complete....");
		} catch (IOException e) {
			e.printStackTrace();
		}
//		try {
//			U.log(fileName);
//			FileInputStream fstream = new FileInputStream(fileName);1
//			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
//
//			String strLine;
//
//			//Read File Line By Line
//			while ((strLine = br.readLine()) != null)   {
//			  // Print the content on the console
//			  System.out.println (strLine);
//			  break;
//			}
//
//			//Close the input stream
//			fstream.close();
//
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return outData;
	}
}
