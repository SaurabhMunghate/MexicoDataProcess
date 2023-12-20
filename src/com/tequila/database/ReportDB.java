package com.tequila.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.database.connection.Connect;
import com.shatam.utils.U;
/**
 * This class is used for weekly report information on Tequila Project. 
 * @author Sawan
 *
 */
public class ReportDB extends Connect {

	public static void main(String[] args) {
		ReportDB r1 = new ReportDB();
		r1.report();
		r1.disconnect();
	}
	private String[]  values = {"SIC_MAJOR","COMPANY_NAME","ADDRESS", "NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","EMAIL","CONTACT_PERSON","TITLE",
			"ANNUAL_SALES_VOL_MIN","ANNUAL_SALES_VOL_MAX","EMP_COUNT_MIN","EMP_COUNT_MAX","YEARS_IN_BIZ","HOURS_OF_OPERATION","LOCATION_SOURCE","QUALITY_SCORE","GEO_DISTANCE"
			,"GEO_ACCURACY_CODE","ADDRESS_VERIFICATION_CODE","LONGITUDE","LATITUDE"
	};
	
	private Statement stmt = null;
	
	private void report(){
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs  = stmt.executeQuery("select count(*) from dataset");
			U.log(">>> Total count : "+rs.getObject(1));
			for(String value :  values){
				execute(value, stmt, rs);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if(rs != null)
			try {
				rs.close();
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
	
	private void execute(String value, Statement stmt, ResultSet rs){
		try {
			
//			U.log ("QUERY === select count("+value+") from dataset where "+ value +" is not null or " + value+ " !=\"\"");
			
			rs = stmt.executeQuery("select count("+value+") from dataset where "+ value +" is not null or " + value+ " !=\"\"");
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
}
