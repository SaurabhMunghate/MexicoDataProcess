/**
 * @author Sawan
 * @date 18 Sept 2018
 */
package com.tequila.database.validator;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.database.connection.Connect;
import com.shatam.utils.U;

public class UpdateValidateTable extends Connect{

	public UpdateValidateTable() {
		super();
	}
	public UpdateValidateTable(String dbPath, String dbName) {
		super(dbPath,dbName);
	}
	/**
	 * This method is used to set the flag for Phone which is '1' for validated phone for particular record.<br> '0' for invalidated phone.<br> 
	 * 'null' represent that record is not validated yet.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updatePhone(List<Integer> idList){
		String updateQuery = "update dataV set PHONE=?, PHONE_DATE=? where ID=? and PHONE is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating phone numbers vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updatePhone()
	
	/**
	 * This method is used to set the flag for Year In Biz which is '1' for validated phone for particular record.<br> '0' for invalidated Year In Biz.<br> 
	 * 'null' represent that record is not validated yet.
	 * @param idList
	 */
	public void updateYearInBiz(List<Integer> idList){
		String updateQuery = "update dataV set YEARS_IN_BIZ=?, YEARS_IN_BIZ_DATE=? where ID=? and YEARS_IN_BIZ is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating year in biz vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateYearInBiz()
	
	/**
	 * This method is used to set the flag for Year In Biz which is '1' for validated phone for particular record.<br> '0' for invalidated Year In Biz.<br> 
	 * 'null' represent that record is not validated yet.
	 * @param idList
	 */
	public void updateHoursOfOperation(List<Integer> idList){
		String updateQuery = "update dataV set Hours_Of_Operation=?, Hours_Of_Operation_DATE=? where ID=? and Hours_Of_Operation is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating year in biz vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateYearInBiz()
	public void updateLocationSource(List<Integer> idList){
		String updateQuery = "update dataV set LOCATION_SOURCE=?, LOCATION_SOURCE_DATE=? where ID=? and LOCATION_SOURCE is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating year in biz vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	
	public void updateQualityScore(List<Integer> idList){
		String updateQuery = "update dataV set LOCATION_SOURCE=?, LOCATION_SOURCE_DATE=? where ID=? and LOCATION_SOURCE is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating year in biz vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void updateGeoDistance(List<Integer> idList){
		String updateQuery = "update dataV set LOCATION_SOURCE=?, LOCATION_SOURCE_DATE=? where ID=? and LOCATION_SOURCE is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating year in biz vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void updateGeoAccuracyCode(List<Integer> idList){
		String updateQuery = "update dataV set LOCATION_SOURCE=?, LOCATION_SOURCE_DATE=? where ID=? and LOCATION_SOURCE is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating year in biz vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public void updateAddressVerificationCode(List<Integer> idList){
		String updateQuery = "update dataV set LOCATION_SOURCE=?, LOCATION_SOURCE_DATE=? where ID=? and LOCATION_SOURCE is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating year in biz vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	/**
	 * This method is used to set the flag for Zip validation. 
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateZip(List<Integer> idList){
		String updateQuery = "update dataV set ZIP=?, ZIP_DATE=? where ID=? and ZIP is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating zip vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateZip()
	
	/**
	 * This method is used to set the flag for City validation.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateCity(List<Integer> idList){
		String updateQuery = "update dataV set CITY=?, CITY_DATE=? where ID=? and CITY is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating city vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateCity()
	
	/**
	 * This method is used to set the flag for State validation.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateState(List<Integer> idList){
		String updateQuery = "update dataV set STATE=?, STATE_DATE=? where ID=? and STATE is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating state vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateState()
	
	/**
	 * This method is used to set the flag for Address validation.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateAddress(List<Integer> idList){
		String updateQuery = "update dataV set ADDRESS=?, ADDRESS_DATE=? where ID=? and ADDRESS is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating address vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateAddress()
	
	/**
	 * This method is used to set the flag for Address all fields validation.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateAddressAll(List<Integer> idList){
		String updateQuery = "update dataV set ADDRESS=?,ADDRESS_DATE=?,NEIGHBORHOOD=?,NEIGHBORHOOD_DATE=?,CITY=?,CITY_DATE=?,STATE=?,STATE_DATE=?,ZIP=?,ZIP_DATE=? where ID=?";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating address all fields vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){
				String date = U.getTodayDate();
				
				pstmt.setInt(1, 1); //ADDRESS
				pstmt.setString(2, date); //ADDRESS_DATE
				pstmt.setInt(3, 1); //NEIGHBORHOOD
				pstmt.setString(4, date); //NEIGHBORHOOD_DATE
				pstmt.setInt(5, 1); //CITY
				pstmt.setString(6, date); //CITY_DATE
				pstmt.setInt(7, 1); //STATE
				pstmt.setString(8, date); //STATE_DATE
				pstmt.setInt(9, 1); //ZIP
				pstmt.setString(10, date); //ZIP_DATE
				pstmt.setInt(11, id); //ID
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateAddressAll()
	
	/**
	 * This method is used to set the flag for Address all fields validation except zip field.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateAddressAllExceptZip(List<Integer> idList){
		String updateQuery = "update dataV set ADDRESS=?,ADDRESS_DATE=?,NEIGHBORHOOD=?,NEIGHBORHOOD_DATE=?,CITY=?,CITY_DATE=?,STATE=?,STATE_DATE=? where ID=?";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating address all fields validation except zip field here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){
				String date = U.getTodayDate();
				
				pstmt.setInt(1, 1); //ADDRESS
				pstmt.setString(2, date); //ADDRESS_DATE
				pstmt.setInt(3, 1); //NEIGHBORHOOD
				pstmt.setString(4, date); //NEIGHBORHOOD_DATE
				pstmt.setInt(5, 1); //CITY
				pstmt.setString(6, date); //CITY_DATE
				pstmt.setInt(7, 1); //STATE
				pstmt.setString(8, date); //STATE_DATE
				pstmt.setInt(9, id); //ID
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateAddressAllExceptZip()
	
	/**
	 * This method is used to set the flag for Address all fields validation except neighborhood field.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateAddressAllExceptNeighborhood(List<Integer> idList){
		String updateQuery = "update dataV set ADDRESS=?,ADDRESS_DATE=?,CITY=?,CITY_DATE=?,STATE=?,STATE_DATE=?,ZIP=?,ZIP_DATE=? where ID=?";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating address all fields validation except neighborhood field here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){
				String date = U.getTodayDate();
				
				pstmt.setInt(1, 1); //ADDRESS
				pstmt.setString(2, date); //ADDRESS_DATE
				pstmt.setInt(3, 1); //CITY
				pstmt.setString(4, date); //CITY_DATE
				pstmt.setInt(5, 1); //STATE
				pstmt.setString(6, date); //STATE_DATE
				pstmt.setInt(7, 1); //ZIP
				pstmt.setString(8, date); //ZIP_DATE
				pstmt.setInt(9, id); //ID
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateAddressAllExceptNeighborhood()
	
	/**
	 * This method is used to set the flag for Address all fields validation except neighborhood and zip fields.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateAddressExceptNeighborhoodAndZip(List<Integer> idList){
		String updateQuery = "update dataV set ADDRESS=?,ADDRESS_DATE=?,CITY=?,CITY_DATE=?,STATE=?,STATE_DATE=? where ID=?";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating address all fields validation except neighborhood and zip fields here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){
				String date = U.getTodayDate();
				
				pstmt.setInt(1, 1); //ADDRESS
				pstmt.setString(2, date); //ADDRESS_DATE
				pstmt.setInt(3, 1); //CITY
				pstmt.setString(4, date); //CITY_DATE
				pstmt.setInt(5, 1); //STATE
				pstmt.setString(6, date); //STATE_DATE
				pstmt.setInt(7, id); //ID
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateAddressExceptNeighborhoodAndZip()
	
	/**
	 * This method is used to set the flag for Neighborhood validation.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateNeighborhood(List<Integer> idList){
		String updateQuery = "update dataV set NEIGHBORHOOD=?, NEIGHBORHOOD_DATE=? where ID=? and NEIGHBORHOOD is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating neighborhood vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateNeighborhood()
	
	/**
	 * This method is used to set the flag for fax validation. 
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateFax(List<Integer> idList){
		String updateQuery = "update dataV set FAX=?, FAX_DATE=? where ID=? and FAX is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating fax numbers vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateFax()
	
	/**
	 * This method is used to set the flag for Email validation. 
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateEmail(List<Integer> idList){
		String updateQuery = "update dataV set EMAIL=?, EMAIL_DATE=? where ID=? and EMAIL is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating emails vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 10000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateEmails()
	
	/**
	 * This method is used to set the flag for Contact Person validation. 
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateContactPerson(List<Integer> idList){
		String updateQuery = "update dataV set CONTACT_PERSON=?, CONTACT_PERSON_DATE=? where ID=? and CONTACT_PERSON is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating CONTACT_PERSON vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 10000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateContactPerson()
	
	/**
	 * This method is used to set the flag for Title validation. 
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateTitle(List<Integer> idList){
		String updateQuery = "update dataV set TITLE=?, TITLE_DATE=? where ID=? and TITLE is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating Title(designation) vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 10000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateTitle()
	
	/**
	 * This method is used to set the flag for EMP_COUNT_MIN validation. 
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateMinEmpCount(List<Integer> idList){
		String updateQuery = "update dataV set EMP_COUNT_MIN=?, EMP_COUNT_MIN_DATE=? where ID=? and EMP_COUNT_MIN is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating EMP_COUNT_MIN vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 10000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateMinEmpCount()

	/**
	 * This method is used to set the flag for EMP_COUNT_MAX validation.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateMaxEmpCount(List<Integer> idList){
		String updateQuery = "update dataV set EMP_COUNT_MAX=?, EMP_COUNT_MAX_DATE=? where ID=? and EMP_COUNT_MAX is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating EMP_COUNT_MAX vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 10000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateMaxEmpCount()

	/**
	 * This method is used to set the flag for ANNUAL_SALES_VOL_MIN validation.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateMinAnnualSales(List<Integer> idList){
		String updateQuery = "update dataV set ANNUAL_SALES_VOL_MIN=?, ANNUAL_SALES_VOL_MIN_DATE=? where ID=? and ANNUAL_SALES_VOL_MIN is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating ANNUAL_SALES_VOL_MIN vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 10000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateMinAnnualSales()
	
	/**
	 * This method is used to set the flag for ANNUAL_SALES_VOL_MAX validation.
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateMaxAnnualSales(List<Integer> idList){
		String updateQuery = "update dataV set ANNUAL_SALES_VOL_MAX=?, ANNUAL_SALES_VOL_MAX_DATE=? where ID=? and ANNUAL_SALES_VOL_MAX is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating ANNUAL_SALES_VOL_MAX vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 10000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateMaxAnnualSales()
	
	/**
	 * This method is used to set the flag for Sic Sub validation. 
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateSicSub(List<Integer> idList){
		String updateQuery = "update dataV set SIC_SUB=?, SIC_SUB_DATE=? where ID=? and SIC_SUB is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating sic sub vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateSicSub()
	
	/**
	 * This method is used to set the flag for Company Name validation. 
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateCompanyName(List<Integer> idList){
		String updateQuery = "update dataV set COMPANY_NAME=?, COMPANY_NAME_DATE=? where ID=? and COMPANY_NAME is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating company name vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateCompanyName()
	
	/**
	 * This method is used to set the flag for URL validation. 
	 * @param idList :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateUrl(List<Integer> idList){
		String updateQuery = "update dataV set URL=?, URL_DATE=? where ID=? and URL is null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating url vaidation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateUrl()
	
	/**
	 * This method is used to set the flag for Geo validation. 
	 * @param idLis :- Is list of integer that containing the ID's of validated field.
	 */
	public void updateGeo(List<Integer> idList){
		String updateQuery = "update dataV set GEO=?, GEO_DATE=?, IS_SEARCHABLE=? where ID=? and GEO is null";
		//"UPDATE dataV set GEO=?, GEO_DATE=? where ID = (select ID from dataset as A where A.ID = dataV.ID and dataV.ID=?)";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start updating geo validation here .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setInt(1, 1);
				pstmt.setString(2, U.getTodayDate());
				pstmt.setInt(3, 1);
				pstmt.setInt(4, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof updateGeo()
	
	/**
	 * This method is used to set URL into null at dataV table for being invalid at dataset table.
	 * @param idList :- Is list of integer that containing the ID's of being invalid field.
	 */
	public void deleteUrl(List<Integer> idList){
		String updateQuery = "update dataV set URL=?, URL_DATE=? where ID=? and URL is not null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start deleting url vaidation here i.e. url=null  .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setString(1, null);
				pstmt.setString(2, null);
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof deleteUrl()
	
	/**
	 * This method is used to set EMAIL into null at dataV table for being invalid at dataset table.
	 * @param idList :- Is list of integer that containing the ID's of being invalid field.
	 */
	public void deleteEmail(List<Integer> idList){
		String updateQuery = "update dataV set EMAIL=?, EMAIL_DATE=? where ID=? and EMAIL is not null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start deleting email vaidation here i.e. email=null .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){

				pstmt.setString(1, null);
				pstmt.setString(2, null);
				pstmt.setInt(3, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof deleteEmail()
	
	/**
	 * This method is used to set PHONE into null at dataV table for being invalid at dataset table.
	 * @param idList :- Is list of integer that containing the ID's of being invalid field.
	 */
	public void deletePhone(List<Integer> idList){
		String updateQuery = "update dataV set PHONE = null, PHONE_DATE = null where ID=? and PHONE is not null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start deleting PHONE vaidation here i.e. PHONE=null .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){
				pstmt.setInt(1, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof deletePhone()
	
	/**
	 * This method is used to set FAX into null at dataV table for being invalid at dataset table.
	 * @param idList :- Is list of integer that containing the ID's of being invalid field.
	 */
	public void deleteFax(List<Integer> idList){
		String updateQuery = "update dataV set FAX = null, FAX_DATE = null where ID=? and FAX is not null";
		PreparedStatement pstmt = null;
		
		int x = 0;
		int updateCount[];
		U.log("*** Start deleting FAX vaidation here i.e. FAX=null .....");
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){
				pstmt.setInt(1, id);
				
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}				
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
	}//eof deleteFax()
	
}
