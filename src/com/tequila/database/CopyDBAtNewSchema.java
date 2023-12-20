package com.tequila.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.database.connection.DBConnection;
import com.shatam.utils.DateFormat;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class CopyDBAtNewSchema {
	
	public static void main(String[] args) {
		CopyDBAtNewSchema add = new CopyDBAtNewSchema();
//		add.loadMainDB();
//		add.copyDatasetToDataset();
//		add.copyDatasetToUpdatedAndDeletedDataset();
		
		add.copyTableToModifiedSchemaTable("select * from dataset", INSERT_QUERY);
		add.copyTableToModifiedSchemaTable("select * from updatedDataset", INSERT_QUERY_AT_UPDATED_DATASET);
		add.copyTableToModifiedSchemaTable("select * from deletedDataset", INSERT_QUERY_AT_DELETED_DATASET);
	}
	
/*	private static final String insertQuery = "INSERT INTO dataset(ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"
			+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,LONGITUDE,LATITUDE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_LAST_UPDATED) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
*/	

	private static final String INSERT_QUERY = "INSERT INTO dataset(ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,"
+ "PHONE,FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,LONGITUDE,LATITUDE,"
+ "CREATED_DATE,UPDATED_DATE,DELETED_DATE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_DELETED) "
+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String INSERT_QUERY_AT_UPDATED_DATASET = "INSERT INTO updatedDataset(ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,"
+ "PHONE,FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,LONGITUDE,LATITUDE,"
+ "CREATED_DATE,UPDATED_DATE,DELETED_DATE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_DELETED) "
+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	private static final String INSERT_QUERY_AT_DELETED_DATASET = "INSERT INTO deletedDataset(ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,"
+ "PHONE,FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,LONGITUDE,LATITUDE,"
+ "CREATED_DATE,UPDATED_DATE,DELETED_DATE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_DELETED) "
+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";


	private static final String OLD_DB_PATH = "/home/glady/MexicoCache/database/MAY-2018/30_May/";
	
	private static final String NEW_DB_PATH = "/home/glady/MexicoCache/database/MAY-2018/New/";
	
	
	private void copyTableToModifiedSchemaTable(String query,String insertQuery){
		
		Connection conn1 = DBConnection.getConnection(OLD_DB_PATH, "tequila.db");
		Connection conn2 = DBConnection.getConnection(NEW_DB_PATH, "tequila.db");
		
		U.log("Query ::"+query);
		U.log("Start inserting data to another table here ...");
		
		PreparedStatement pstmt = null;
		
		int insertedCount[];
		int i = 0;
		try{
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			
			pstmt = conn2.prepareStatement(insertQuery);
			conn2.setAutoCommit(false);

			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();
			
			while(rs.next()){
				
				for(int col = 1; col <= columns; ++col){
					pstmt.setString(col,rs.getString(col));
		        }
				pstmt.addBatch();

				if ((++i % 10000) == 0) {
					insertedCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ insertedCount.length + "\t" + i);
					conn2.commit();
					System.out.println("Commit the batch");
				}
			}

			insertedCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ insertedCount.length + "\t" + i);
			conn2.commit();
			System.out.println("Commit the batch");
			System.out.println("exit");

			pstmt.close();
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn2.setAutoCommit(true);
			conn2.close();
			conn1.close();
			U.log("Connection is closed.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//eof copyTableToModifiedSchemaTable()
	
/*	private void loadMainDB(){
		
		HashMap<String,long[]> annualSalesMap = loadAnnualSales();
		HashMap<String,long[]> empCountMap = loadEmpCount();
		HashMap<String,String> correctDateMap = loadCorrectYearsInBiz();
		
		
		Connection conn1 = DBConnection.getConnection(Path.TEQUILA_DB_PATH, "tequilaFinal.db");
		Connection conn2 = DBConnection.getConnection(NEW_DB_PATH, "tequila.db");
		
		String query = "select * from dataset";
		
		PreparedStatement pstmt = null;
		
		int updateCount[];
		int i = 0;
		try{
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			
			pstmt = conn2.prepareStatement(insertQuery);
			conn2.setAutoCommit(false);

			while(rs.next()){
				
			//	if(i == 100)break;
				pstmt.setString(1,rs.getString("ID"));
				pstmt.setString(2, rs.getString("INDUSTRY_SECTOR"));
				pstmt.setString(3, rs.getString("SPANISH_INDUSTRY_SECTOR"));
				pstmt.setString(4, rs.getString("SIC_MAJOR"));
				pstmt.setString(5, rs.getString("SIC_SUB"));
				pstmt.setString(6, rs.getString("PRODUCT_DESC"));
				pstmt.setString(7, rs.getString("SPANISH_PRODUCT_DESC"));
				pstmt.setString(8, rs.getString("COMPANY_NAME"));
				pstmt.setString(9, rs.getString("ADDRESS"));
				pstmt.setString(10, rs.getString("NEIGHBORHOOD"));
				pstmt.setString(11, rs.getString("CITY"));
				pstmt.setString(12, rs.getString("STATE"));
				pstmt.setString(13, rs.getString("ZIP"));
				pstmt.setString(14, rs.getString("PHONE"));
				pstmt.setString(15, rs.getString("FAX"));
				pstmt.setString(16, rs.getString("URL"));
				pstmt.setString(17, rs.getString("EMAIL"));
				pstmt.setString(18, rs.getString("CONTACT_PERSON"));
				pstmt.setString(19, rs.getString("TITLE"));
				
				//ANNUAL_SALES_VOL
				String annualSalesVol = rs.getString("ANNUAL_SALES_VOL");
				long[] salesVol= annualSalesMap.get(annualSalesVol);
				String minAnnualSales = null;
				String maxAnnualSales = null;

				if(salesVol != null){
					if(salesVol[0] != 0){
						minAnnualSales = String.valueOf(salesVol[0]);
					}else{
						minAnnualSales = null;
					}
					
					if(salesVol[1] != 0){
						maxAnnualSales = String.valueOf(salesVol[1]);
					}else{
						maxAnnualSales = null;
					}				
				}else{
					minAnnualSales = null;
					maxAnnualSales = null;
				}
				
				pstmt.setString(20, minAnnualSales);
				pstmt.setString(21, maxAnnualSales);
				
				//EMP_COUNT
				String empCount = rs.getString("EMP_COUNT");
				long[] empCountVal = empCountMap.get(empCount);
				String minEmpCount = null;
				String maxEmpCount = null;

				if(empCountVal != null){
					if(empCountVal[0] != 0){
						minEmpCount = String.valueOf(empCountVal[0]);
					}else{
						minEmpCount = null;
					}
					
					if(empCountVal[1] != 0){
						maxEmpCount = String.valueOf(empCountVal[1]);
					}else{
						maxEmpCount = null;
					}					
				}else{
					minEmpCount = null;
					maxEmpCount = null;
				}
				pstmt.setString(22, minEmpCount);
				pstmt.setString(23, maxEmpCount);
				
				//YEARS_IN_BIZ
				String yearsInBiz = rs.getString("YEARS_IN_BIZ");
				String correctYearsInBiz = null;
				if(!yearsInBiz.isEmpty()){
					correctYearsInBiz = correctDateMap.get(yearsInBiz);					
				}else{
					correctYearsInBiz = null;
				}
				pstmt.setString(24, correctYearsInBiz);
				
				String longitude = rs.getString("LONGITUDE");
				if(longitude.isEmpty()) longitude = null;
				pstmt.setString(25, longitude);
				
				String latitude = rs.getString("LATITUDE");
				if(latitude.isEmpty()) latitude = null;
				pstmt.setString(26, latitude);
				
				pstmt.setString(27, rs.getString("_SCORE"));
				pstmt.setString(28, rs.getString("_SOURCE_URL"));
				pstmt.setString(29, rs.getString("_STATUS"));
				pstmt.setString(30, rs.getString("_LAST_UPDATED_BY"));
				pstmt.setString(31, rs.getString("_LAST_UPDATED"));

				pstmt.addBatch();
						
				if ((++i % 20000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn2.commit();
					System.out.println("Commit the batch");

				}
				
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
			conn2.commit();
			System.out.println("Commit the batch");
			System.out.println("exit");
			
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn2.setAutoCommit(true);
			pstmt.close();
			conn2.close();
			conn1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/	
	private void copyDatasetToDataset(){
	
		Connection conn1 = DBConnection.getConnection(OLD_DB_PATH, "tequila.db");
		Connection conn2 = DBConnection.getConnection(NEW_DB_PATH, "tequila.db");
		
		String query = "select * from dataset";
		
		PreparedStatement pstmt = null;
		
		int updateCount[];
		int i = 0;
		try{
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			
			pstmt = conn2.prepareStatement(INSERT_QUERY);
			conn2.setAutoCommit(false);

			String createdDate = null;
			String deletedDate = null;
			while(rs.next()){
			//	if(i == 100)break;
				pstmt.setString(1,rs.getString("ID"));
				pstmt.setString(2, rs.getString("INDUSTRY_SECTOR"));
				pstmt.setString(3, rs.getString("SPANISH_INDUSTRY_SECTOR"));
				pstmt.setString(4, rs.getString("SIC_MAJOR"));
				pstmt.setString(5, rs.getString("SIC_SUB"));
				pstmt.setString(6, rs.getString("PRODUCT_DESC"));
				pstmt.setString(7, rs.getString("SPANISH_PRODUCT_DESC"));
				pstmt.setString(8, rs.getString("COMPANY_NAME"));
				pstmt.setString(9, rs.getString("ADDRESS"));
				pstmt.setString(10, rs.getString("NEIGHBORHOOD"));
				pstmt.setString(11, rs.getString("CITY"));
				pstmt.setString(12, rs.getString("STATE"));
				pstmt.setString(13, rs.getString("ZIP"));
				pstmt.setString(14, rs.getString("PHONE"));
				pstmt.setString(15, rs.getString("FAX"));
				pstmt.setString(16, rs.getString("URL"));
				pstmt.setString(17, rs.getString("EMAIL"));
				pstmt.setString(18, rs.getString("CONTACT_PERSON"));
				pstmt.setString(19, rs.getString("TITLE"));
				
				pstmt.setString(20, rs.getString("ANNUAL_SALES_VOL_MIN"));
				pstmt.setString(21, rs.getString("ANNUAL_SALES_VOL_MAX"));
				
				pstmt.setString(22, rs.getString("EMP_COUNT_MIN"));
				pstmt.setString(23, rs.getString("EMP_COUNT_MAX"));
				
				pstmt.setString(24, rs.getString("YEARS_IN_BIZ"));
				

				pstmt.setString(25, rs.getString("LONGITUDE"));

				pstmt.setString(26, rs.getString("LATITUDE"));
				
				pstmt.setString(27, createdDate);
				
				String updatedDate = rs.getString("_LAST_UPDATED");
				if(updatedDate != null && updatedDate.trim().isEmpty()) updatedDate = null;
				pstmt.setString(28, updatedDate);
				
				pstmt.setString(29, deletedDate);				
				pstmt.setString(30, rs.getString("_SCORE"));
				
				String sourceUrl = rs.getString("_SOURCE_URL");
				if(sourceUrl != null && sourceUrl.trim().isEmpty()) sourceUrl = null;
				pstmt.setString(31, sourceUrl);
				
				String status = rs.getString("_STATUS");
				if(status != null && status.trim().isEmpty())status = null;
				pstmt.setString(32, status);
				
				String updatedBy = rs.getString("_LAST_UPDATED_BY");
				if(updatedBy != null && updatedBy.trim().isEmpty()) updatedBy = null;
				
				pstmt.setString(33, updatedBy);
				pstmt.setString(34, rs.getString("_DELETED"));

				pstmt.addBatch();
				
				if ((++i % 10000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn2.commit();
					System.out.println("Commit the batch");

				}
				
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
			conn2.commit();
			System.out.println("Commit the batch");
			System.out.println("exit");
			
			pstmt.close();
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn2.setAutoCommit(true);
			conn2.close();
			conn1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void copyDatasetToUpdatedAndDeletedDataset(){
		
		Connection conn1 = DBConnection.getConnection(OLD_DB_PATH, "tequila.db");
		Connection conn2 = DBConnection.getConnection(NEW_DB_PATH, "tequila.db");
		
		String query = "select * from deleteddataset";
		
//		String query = "select * from updateddataset";
		
		PreparedStatement pstmt = null;
		
		int updateCount[];
		int i = 0;
		try{
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			
			pstmt = conn2.prepareStatement(INSERT_QUERY_AT_DELETED_DATASET);
//			pstmt = conn2.prepareStatement(INSERT_QUERY_AT_UPDATED_DATASET);
			conn2.setAutoCommit(false);

			String createdDate = null;
			
			while(rs.next()){
				
			//	if(i == 100)break;
				pstmt.setString(1,rs.getString("ID"));
				pstmt.setString(2, rs.getString("INDUSTRY_SECTOR"));
				pstmt.setString(3, rs.getString("SPANISH_INDUSTRY_SECTOR"));
				pstmt.setString(4, rs.getString("SIC_MAJOR"));
				pstmt.setString(5, rs.getString("SIC_SUB"));
				pstmt.setString(6, rs.getString("PRODUCT_DESC"));
				pstmt.setString(7, rs.getString("SPANISH_PRODUCT_DESC"));
				pstmt.setString(8, rs.getString("COMPANY_NAME"));
				pstmt.setString(9, rs.getString("ADDRESS"));
				pstmt.setString(10, rs.getString("NEIGHBORHOOD"));
				pstmt.setString(11, rs.getString("CITY"));
				pstmt.setString(12, rs.getString("STATE"));
				pstmt.setString(13, rs.getString("ZIP"));
				pstmt.setString(14, rs.getString("PHONE"));
				pstmt.setString(15, rs.getString("FAX"));
				pstmt.setString(16, rs.getString("URL"));
				pstmt.setString(17, rs.getString("EMAIL"));
				pstmt.setString(18, rs.getString("CONTACT_PERSON"));
				pstmt.setString(19, rs.getString("TITLE"));
				
				pstmt.setString(20, rs.getString("ANNUAL_SALES_VOL_MIN"));
				pstmt.setString(21, rs.getString("ANNUAL_SALES_VOL_MAX"));
				
				pstmt.setString(22, rs.getString("EMP_COUNT_MIN"));
				pstmt.setString(23, rs.getString("EMP_COUNT_MAX"));
				
				//YEARS_IN_BIZ
				String yearsInBiz = rs.getString("YEARS_IN_BIZ");
				String correctYearsInBiz = null;
				if(yearsInBiz != null){
					correctYearsInBiz = Util.match(yearsInBiz, "\\d{4}");		
				}else{
					correctYearsInBiz = null;
				}				
				pstmt.setString(24, correctYearsInBiz);

				pstmt.setString(25, rs.getString("LONGITUDE"));

				pstmt.setString(26, rs.getString("LATITUDE"));
				
				pstmt.setString(27, createdDate);
				
				String updatedDate = rs.getString("_LAST_UPDATED");
				if(updatedDate != null && updatedDate.trim().isEmpty()) updatedDate = null;
				
				pstmt.setString(28, updatedDate);
				
				String deletedDate = rs.getString("_DELETED_DATE");
				if(deletedDate != null && deletedDate.trim().isEmpty()) deletedDate = null;
				pstmt.setString(29, deletedDate);
				
				pstmt.setString(30, rs.getString("_SCORE"));
				
				String sourceUrl = rs.getString("_SOURCE_URL");
				if(sourceUrl != null && sourceUrl.trim().isEmpty()) sourceUrl = null;
				pstmt.setString(31, sourceUrl);
				
				String status = rs.getString("_STATUS");
				if(status != null && status.trim().isEmpty())status = null;
				pstmt.setString(32, status);
				
				String updatedBy = rs.getString("_LAST_UPDATED_BY");
				if(updatedBy != null && updatedBy.trim().isEmpty()) updatedBy = null;
				
				pstmt.setString(33, updatedBy);
				pstmt.setString(34, rs.getString("_DELETED"));

				pstmt.addBatch();
				
				if ((++i % 10000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn2.commit();
					System.out.println("Commit the batch");

				}
				
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
			conn2.commit();
			System.out.println("Commit the batch");
			System.out.println("exit");
			
			pstmt.close();
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn2.setAutoCommit(true);
			conn2.close();
			conn1.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
/*	private HashMap<String,long[]> loadAnnualSales(){
		HashMap<String,long[]> annualSalesMap = new HashMap<>();
		int i = 0;
		String filePath = "/home/glady/MexicoCache/database/AnnualSales_Formatted.csv";
		try(BufferedReader br =  new BufferedReader(new FileReader(filePath));)
		{
			String line = null;
			String [] nextLine = null;
			while((line = br.readLine()) != null){
				if(i++ == 0)continue;
				
				nextLine = line.split("\t");
				if(nextLine[2].equals("null")){
					nextLine[2] = "0";
				}
				if(nextLine[1].equals("null")){
					nextLine[1] = "0";
				}
				annualSalesMap.put(nextLine[0], new long[]{
					Long.parseLong(nextLine[1]),
					Long.parseLong(nextLine[2])
				});
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
		return annualSalesMap;
	}
	
	private HashMap<String,long[]> loadEmpCount(){
		HashMap<String,long[]> empCountMap = new HashMap<>();
		int i = 0;
		String filePath = "/home/glady/MexicoCache/database/EMP_Count_Formatted.csv";
		try(BufferedReader br =  new BufferedReader(new FileReader(filePath));)
		{
			String line = null;
			String [] nextLine = null;
			while((line = br.readLine()) != null){
				if(i++ == 0)continue;
				
				nextLine = line.replace("\"", "").split("\t");
				if(nextLine[2].equals("null")){
					nextLine[2] = "0";
				}
				if(nextLine[1].equals("null")){
					nextLine[1] = "0";
				}
				empCountMap.put(nextLine[0], new long[]{
					Long.parseLong(nextLine[1].trim()),
					Long.parseLong(nextLine[2].trim())
				});
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
		return empCountMap;
	}

	private HashMap<String,String> loadCorrectYearsInBiz(){
		HashSet<String> dateSet = new HashSet<>();
		String query1 = "select years_in_biz from dataset where years_in_biz!='' group by years_in_biz";
		

		try(Connection conn1 = DBConnection.getConnection(Path.TEQUILA_DB_PATH, "tequilaFinal.db");
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query1);)
		{
			while(rs.next()){
				dateSet.add(rs.getString("years_in_biz"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Size of set::"+dateSet.size());
		
		int count=0;
		HashMap<String,String> correctDateMap = new HashMap<>();
		for(String inputDate : dateSet){
			if(!DateFormat.validateDate(inputDate)){
				count++;
				String newDate = DateFormat.correctDate(inputDate);				
				if(DateFormat.validateDate(newDate)){
					//U.log(inputDate +"\t\t"+DateFormat.getDate(newDate));
					correctDateMap.put(inputDate,DateFormat.getDate(newDate));
				}else{
					U.errLog(inputDate+"\t\t"+newDate);
				}
			}			
		}
		U.log("Count of date is not in proper format ::"+count);
		U.log("Size of corrected date map ::"+correctDateMap.size());
		
		return correctDateMap;
	}//eof checkYearInBizAtDB();
*/	

}
