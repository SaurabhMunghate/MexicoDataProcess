package com.tequila.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;

import com.database.connection.Connect;
import com.shatam.utils.U;

public class DataValidateReport extends Connect{

//	private final static String TODAY_DATE = "2018-11-22";
	
	private final static String STARTING_DATE = "2021-07-08";
	
	private final static String ENDING_DATE = "2021-07-12";	
	
	private final static String FLAG = "1"; // 1 or 0 or null
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		DataValidateReport r1 = new DataValidateReport();
//		r1.report(STARTING_DATE);
//		r1.report("2018-11-22", null);
		r1.report(STARTING_DATE, ENDING_DATE);
		r1.disconnect(); 
		long end = System.currentTimeMillis();
		U.log("Total time to execute in ms :"+(end-start));	
	}
	
	private String[]  values = {
		"SIC_SUB", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD", "CITY", "STATE", "ZIP", "PHONE", "FAX", "URL", "EMAIL", "CONTACT_PERSON",
		"TITLE","ANNUAL_SALES_VOL_MIN", "ANNUAL_SALES_VOL_MAX", "EMP_COUNT_MIN", "EMP_COUNT_MAX", "YEARS_IN_BIZ","GEO","IS_SEARCHABLE"
	};
	
	private Statement stmt = null;
	
	@Deprecated
	/**
	 * This method is used generate report of dataV table.
	 * @param todayDate : Provide the specified date.
	 */
	private void report(String todayDate){
		U.log(">> Starting Date :: "+todayDate);
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			for(String value :  values){
				execute(value, stmt, rs, todayDate);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(stmt != null)
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	/**
	 * This method is used generate report of dataV table.
	 * @param startingDate : Provide the starting date.
	 * @param endingDate : Provide the ending date. This date can be either <strong>empty</strong> or <strong>not empty</strong> or <strong>null</strong>.
	 */
	private void report(String startingDate, String endingDate){
		if(startingDate == null){
			U.log("Starting date must not be null.");
			return;
		}
		if(startingDate.isEmpty()){
			U.log("Starting date must not be empty.");
			return;
		}

		if(endingDate == null) endingDate = "";

		if(endingDate.isEmpty())
			endingDate = startingDate;
		
		U.log(">> From Date :: "+startingDate+"\t till date :: "+endingDate);
		List<String> dates = U.dateRange(startingDate, endingDate);
		
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			for(String value :  values){
//				execute(value, stmt, rs, dates);
//				execute(value, stmt, rs, dates, true);
				DataV dataV = _execute(value, stmt, rs, dates, true);
				U.log(dataV.getFieldName()+"\t\t:: "+dataV.getCount());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(stmt != null)
			try {
				stmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	@Deprecated
	/**
	 * This method is used to execute the query that find verified the field from dataV table that had been validated.<br>
	 * It will give the count of particular table field where its value(FLAG) set to '1' for particular date time.
	 * @param value : It's table field name.
	 * @param stmt : Statement that interface the current database.
	 * @param rs : ResultSet that fetched resulted rows.
	 * @param todayDate : String that contain specified date.
	 */
	private void execute(String value, Statement stmt, ResultSet rs, String todayDate){
		try {
			
			String query = "select count("+value+") from dataV where "+ value;
			if(FLAG == null)
				query = query + " is " + FLAG;	
			else
				query = query + " = " + FLAG;
			
			if(!value.equals("IS_SEARCHABLE"))
				query = query + " AND " + value + "_DATE LIKE '" + todayDate + "%'";
			else
				query = query + " AND " + values[values.length-2] + "_DATE LIKE '" + todayDate + "%'";

			rs = stmt.executeQuery(query);
			U.log(value+"\t\t:: "+rs.getObject(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	@Deprecated
	/**
	 * This method is used to execute the query that find verified the field from dataV table that had been validated.<br>
	 * It will give the count of particular table field where its value(FLAG) set to '1' for particular date time.
	 * @param value : It's table field name.
	 * @param stmt : Statement that interface the current database.
	 * @param rs : ResultSet that fetched resulted rows.
	 * @param dates : List of dates from specified start date to end date.
	 */
	private void execute(String value, Statement stmt, ResultSet rs, List<String> dates){		
		try {			
			String query = "select count("+value+") from dataV where "+ value;
			
			if(FLAG == null)
				query = query + " is " + FLAG;	
			else
				query = query + " = " + FLAG;
			
			//if data field is not IS_SEARCHABLE
			if(!value.equals("IS_SEARCHABLE")){
				query = query + " AND " + value + "_DATE LIKE '" + dates.get(0) + "%'";
				if(dates.size() > 1){
					for(int i = 1; i < dates.size(); i++){
						query = query + " OR " + value + "_DATE LIKE '" + dates.get(i) + "%'";
					}
				}
			}else{
				//if data field is IS_SEARCHABLE
				query = query + " AND " + values[values.length-2] + "_DATE LIKE '" + dates.get(0) + "%'";
				if(dates.size() > 1){
					for(int i = 1; i < dates.size(); i++){
						query = query + " OR " + values[values.length-2] + "_DATE LIKE '" + dates.get(i) + "%'";
					}
				}
			}

			rs = stmt.executeQuery(query);
			U.log(value+"\t\t:: "+rs.getObject(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * This method is used to execute the query that find verified the field from dataV table that had been validated.<br>
	 * It will give the count of particular table field where its value(FLAG) set to '1' for particular date time.
	 * @param value : It's table field name.
	 * @param stmt : Statement that interface the current database.
	 * @param rs : ResultSet that fetched resulted rows.
	 * @param dates : List of dates from specified start date to end date.
	 * @param ignoreDeletedId : It is a flag that indicate for either ignore those verified fields that had been removed from dataset table.<br>
	 * <p>{@code True} for ignore verified field count,<br>
	 * {@code False} for do not ignore verified field count.</p>
	 */
	private void execute(String value, Statement stmt, ResultSet rs, List<String> dates, boolean ignoreDeletedId){		
		try {			
			String query = "select count("+value+") from dataV where "+ value;
			
			if(FLAG == null)
				query = query + " is " + FLAG;	
			else
				query = query + " = " + FLAG;
			
			//if data field is not IS_SEARCHABLE
			if(!value.equals("IS_SEARCHABLE")){
				query = query + " AND " + value + "_DATE LIKE '" + dates.get(0) + "%'";
				if(dates.size() > 1){
					for(int i = 1; i < dates.size(); i++){
						query = query + " OR " + value + "_DATE LIKE '" + dates.get(i) + "%'";
					}
				}
			}else{
				//if data field is IS_SEARCHABLE
				query = query + " AND " + values[values.length-2] + "_DATE LIKE '" + dates.get(0) + "%'";
				if(dates.size() > 1){
					for(int i = 1; i < dates.size(); i++){
						query = query + " OR " + values[values.length-2] + "_DATE LIKE '" + dates.get(i) + "%'";
					}
				}
			}
			if(ignoreDeletedId){
				query = query + " AND ID NOT IN (SELECT ID FROM dataV WHERE NOT EXISTS(SELECT ID FROM dataset WHERE dataV.ID = dataset.ID))";
			}
			rs = stmt.executeQuery(query);
			U.log(value+"\t\t:: "+rs.getObject(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	/**
	 * This method is used to execute the query that find verified the field from dataV table that had been validated.<br>
	 * It will give the count of particular table field where its value(FLAG) set to '1' for particular date time.
	 * @param value : It's table field name.
	 * @param stmt : Statement that interface the current database.
	 * @param rs : ResultSet that fetched resulted rows.
	 * @param dates : List of dates from specified start date to end date.
	 * @param ignoreDeletedId : It is a flag that indicate for either ignore those verified fields that had been removed from dataset table.<br>
	 * <p>{@code True} for ignore verified field count,<br>
	 * {@code False} for do not ignore verified field count.</p>
	 * @return : <Code>DataV<code> that holds the field and its count which has been verified. 
	 */
	private DataV _execute(String value, Statement stmt, ResultSet rs, List<String> dates, boolean ignoreDeletedId){
		DataV dataV = null;
		try {			
			String query = "select count("+value+") from dataV where "+ value;
			
			if(FLAG == null)
				query = query + " is " + FLAG;	
			else
				query = query + " = " + FLAG;
			
			//if data field is not IS_SEARCHABLE
			if(!value.equals("IS_SEARCHABLE")){
				query = query + " AND " + value + "_DATE LIKE '" + dates.get(0) + "%'";
				if(dates.size() > 1){
					for(int i = 1; i < dates.size(); i++){
						query = query + " OR " + value + "_DATE LIKE '" + dates.get(i) + "%'";
					}
				}
			}else{
				//if data field is IS_SEARCHABLE
				query = query + " AND " + values[values.length-2] + "_DATE LIKE '" + dates.get(0) + "%'";
				if(dates.size() > 1){
					for(int i = 1; i < dates.size(); i++){
						query = query + " OR " + values[values.length-2] + "_DATE LIKE '" + dates.get(i) + "%'";
					}
				}
			}
			if(ignoreDeletedId){
				query = query + " AND ID NOT IN (SELECT ID FROM dataV WHERE NOT EXISTS(SELECT ID FROM dataset WHERE dataV.ID = dataset.ID))";
			}
			rs = stmt.executeQuery(query);
//			U.log(value+"\t\t:: "+rs.getObject(1));
			dataV = new DataV(value,(Integer)rs.getObject(1));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return dataV;
	}
	
	private class DataV{
		private String fieldName = null;
		private int count = 0;
		
		DataV(String fieldName, int count){
			this.fieldName = fieldName;
			this.count = count;
		}
		public String getFieldName(){
			return fieldName;
		}
		public int getCount(){
			return count;
		}
		public void setFieldName(String fieldName){
			this.fieldName = fieldName;
		}
		public void setCount(int count){
			this.count = count;
		}
	}
}
