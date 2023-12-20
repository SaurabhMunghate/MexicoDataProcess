package com.tequila.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.database.connection.DBConnection;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class CopyTableToTable {
	public static void main(String[] args) {
		CopyTableToTable copy = new CopyTableToTable();
		copy.copyTableToTable();
		copy.disconnect();
	}

	private static final String TABLE_COPY_TO ="dataset";
	
	private static final String TABLE_COPY_FROM ="datasetold";
	
	private static final String INSERT_QUERY = "INSERT INTO "+TABLE_COPY_TO+"(ID,INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"
			+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,LONGITUDE,LATITUDE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_LAST_UPDATED,_DELETED,_DELETED_DATE) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String COPY_QUERY = "select * from "+TABLE_COPY_FROM;
	
	private static final String MAIN_DB_NAME = "tequila.db";
	
	Connection conn = null;
	
	public CopyTableToTable() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
	}
	
	public void copyTableToTable(){
		U.log("Start copying table to table");
		PreparedStatement pstmt = null;
		
		int updateCount[];
		int i = 0;
		try{
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(COPY_QUERY);
			
			
			pstmt = conn.prepareStatement(INSERT_QUERY);
			conn.setAutoCommit(false);

			while(rs.next()){
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
				
				pstmt.setString(27, rs.getString("_SCORE"));
				pstmt.setString(28, rs.getString("_SOURCE_URL"));
				pstmt.setString(29, rs.getString("_STATUS"));
				pstmt.setString(30, rs.getString("_LAST_UPDATED_BY"));
				pstmt.setString(31, rs.getString("_LAST_UPDATED"));
				pstmt.setString(32, rs.getString("_DELETED"));
				pstmt.setString(33, rs.getString("_DELETED_DATE"));
				
				pstmt.addBatch();
				
				if ((++i % 10000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");

				}
			}//eof while
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
			conn.commit();
			System.out.println("Commit the batch");
			System.out.println("exit");
			rs.close();
			stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			conn.setAutoCommit(true);
			pstmt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void disconnect(){
		try{
			if(conn != null ||!conn.isClosed()){
				conn.close();
				conn = null;
				U.log("Connection is closed.");
			}
		}catch(SQLException e){
			e.printStackTrace();			
		}
	}

}
