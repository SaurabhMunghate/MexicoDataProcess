package com.tequila.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.database.connection.Connect;
import com.shatam.utils.DB;
import com.shatam.utils.U;

public class UpdateScore extends Connect{
	
	public UpdateScore() {
		super();
	}
	public UpdateScore(Connection conn) {
		super(conn);
	}
	public UpdateScore(String path, String dbName) {
		super(path,dbName);
	}
	
	public static void main(String[] args) {
		UpdateScore scoreUpdate = new UpdateScore();
		scoreUpdate.updateScore(ScoreTequila.MAIN_TABLE);
//		scoreUpdate.updateScore(ScoreTequila.DELETED_TABLE);
//		scoreUpdate.updateScore(ScoreTequila.UPDATED_TABLE);
		
//		scoreUpdate.updateScore(UpdateScore.MAIN_TABLE);
//		scoreUpdate.updateScore(UpdateScore.DELETED_TABLE);
//		scoreUpdate.updateScore(UpdateScore.UPDATED_TABLE);
		
		scoreUpdate.disconnect();
	}

	public static final String MAIN_TABLE = "dataset";
	public static final String UPDATED_TABLE = "updatedDataset";
	public static final String DELETED_TABLE = "deletedDataset";
	public static final String ALL_TABLE = "_ALL_TABLE_";
	
	private static String table = null;
	
/*	public static void updateScore(Connection conn, ScoreTequila scoreTequila){
	}
	
	public static void updateScore(Connection conn, String table){
		setConnection(conn);
		updateScore(table);
	}*/
	
	public void updateScore(ScoreTequila scoreTequila){
		boolean flag = false;
		switch (scoreTequila) {
			case MAIN_TABLE:
				setTable(scoreTequila.getTable());
				flag = true;
				break;
			case UPDATED_TABLE:
				setTable(scoreTequila.getTable());
				flag = true;
				break;
			case DELETED_TABLE:
				setTable(scoreTequila.getTable());
				flag = true;
				break;
			default: U.log("None operation happend on the database...");
		}
		if(flag){
			update();
		}
	}
	
	public void updateScore(ScoreTequila scoreTequila, List<Integer> idList){
		boolean flag = false;
		switch (scoreTequila) {
			case MAIN_TABLE:
				setTable(scoreTequila.getTable());
				flag = true;
				break;
			case UPDATED_TABLE:
				setTable(scoreTequila.getTable());
				flag = true;
				break;
			case DELETED_TABLE:
				setTable(scoreTequila.getTable());
				flag = true;
				break;
			default: U.log("None operation happend on the database...");
		}
		if(flag){
			update(idList);
		}
	}
	
	public void updateScoreAll(ScoreTequila scoreTequila){
		boolean flag = false;
		switch (scoreTequila) {
			case ALL_TABLE:
				flag = true;
				break;
			default: U.log("None operation happend on the database...");
		}
		
		if(flag){
			setTable(ScoreTequila.MAIN_TABLE.getTable());
			update();
			
			setTable(ScoreTequila.UPDATED_TABLE.getTable());
			update();

			setTable(ScoreTequila.DELETED_TABLE.getTable());
			update();
		}
	}
	
	public void updateScore(String table){
		boolean flag = false;
		if(table != null){
			setTable(table);
			if(table.equals(MAIN_TABLE) || table.equals(UPDATED_TABLE) || table.equals(DELETED_TABLE))flag = true;
			else{
				U.log("None operation happend on the database...");
			}
		}
		if(flag){
//			U.log(getScoreQuery());
			update();
		}
	}
	
	public void updateScore(String table, List<Integer> idList){
		boolean flag = false;
		if(table != null){
			setTable(table);
			if(table.equals(MAIN_TABLE) || table.equals(UPDATED_TABLE) || table.equals(DELETED_TABLE))flag = true;
			else{
				U.log("None operation happend on the database...");
			}
		}
		if(flag){
//			U.log(getScoreQuery());
			update(idList);
		}
	}

	public void updateScoreAll(String table){
		boolean flag = false;
		if(table != null){
			if(table.equals(ALL_TABLE))flag = true;
			else{
				U.log("None operation happend on the database...");
			}
		}
		if(flag){
			setTable(MAIN_TABLE);
			update();

			setTable(UPDATED_TABLE);
			update();

			setTable(DELETED_TABLE);
			update();
		}
	}

	
	private void update(){
		U.log("Start updating score .....");
		U.log(getScoreQuery());

		try {
			conn.setAutoCommit(false);
			int i = DB.executeUpdate(getScoreQuery(), conn);
			if(i>0){
				U.log("*** Update Score for table "+getTable());
			}
			
			ResultSet rs  = DB.executeQuery("select count(*) from "+getTable(), conn);
			while(rs.next()){
				U.log("> Count for table '"+getTable()+"' is :"+rs.getString(1));
			}
			rs.close();
			
			conn.commit();
			conn.setAutoCommit(true);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void setTable(String tempTable){
		table = tempTable;
	}
	private static String getTable(){
		return table;
	}
	
/*	private static void setConnection(Connection conn){
		new Connect(conn) {	};
	}*/
	private static String getScoreQuery(){
		return 	"update "+ getTable() + " set _SCORE = ((("+
				"(case when ID is \"\" then 0 else 100 end)+" +
				"(case when SIC_SUB is \"\" or SIC_SUB is null then 0 else 100 end)+" +
				"(case when COMPANY_NAME is \"\" or COMPANY_NAME is null then 0 else 100 end)+" +
				"(case when ADDRESS is \"\" or ADDRESS is null then 0 else 100 end)+" +
				"(case when CITY is \"\" or CITY is null then 0 else 100 end)+" +
				"(case when NEIGHBORHOOD is \"\" or NEIGHBORHOOD is null then 0 else 50 end)+" +
				"(case when STATE is \"\" or STATE is null then 0 else 100 end)+" +
				"(case when ZIP is \"\" or ZIP is null then 0 else 100 end)+" +
				"(case when PHONE is \"\" or PHONE is null then 0 else 50 end)+" +
				"(case when FAX is \"\" or FAX is null then 0 else 50 end)+" +
				"(case when URL is \"\" or URL is null then 0 else 25 end)+" +
				"(case when EMAIL is \"\" or EMAIL is null then 0 else 50 end)+" +
				"(case when CONTACT_PERSON is \"\" or CONTACT_PERSON is null then 0 else 10 end)+" +
				"(case when TITLE is \"\" or TITLE is null then 0 else 10 end)+" +
				"(case when ANNUAL_SALES_VOL_MIN is null or ANNUAL_SALES_VOL_MIN is \"\" then 0 else 5 end)+" +
				"(case when EMP_COUNT_MIN is null or EMP_COUNT_MIN is \"\" then 0 else 5 end)+" +
				"(case when YEARS_IN_BIZ is null or YEARS_IN_BIZ is \"\" then 0 else 5 end)+" +
				"(case when LONGITUDE is null or LONGITUDE is \"\" then 0 else 50 end)+" +
				"(case when LATITUDE is null or LATITUDE is \"\" then 0 else 50 end))*100)/1060)";
	}
	
	private void update(List<Integer> idList){
		U.log("Start updating score .....");
		int updateCount[];
		int i = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(getScoreQuery()+" where ID=?");
			
			conn.setAutoCommit(false);
			for(int id : idList){
				pstmt.setInt(1, id);
				pstmt.addBatch();

				if ((++i % 10000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated its score: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");
				}
			}//eof for
			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated its score: "+ updateCount.length + "\t" + i);
			conn.commit();
			System.out.println("Commit the batch");
			conn.setAutoCommit(true);
			System.out.println("Update scoring done....");

			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
/*	public void disconnect(){
		try{
			if(conn != null && !conn.isClosed()){
				conn.close();
				conn = null;
				U.log("Connection is closed.");
			}
		}catch(SQLException e){
			e.printStackTrace();			
		}
	}
*/
	
}
