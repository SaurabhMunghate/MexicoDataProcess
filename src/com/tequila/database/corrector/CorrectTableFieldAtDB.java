package com.tequila.database.corrector;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.database.connection.Connect;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class CorrectTableFieldAtDB extends Connect{
	
	enum TableField{
		NEIGHBORHOOD,
		PHONE,
		FAX,
		URL,
		EMAIL,
		CONTACT_PERSON,
		TITLE;
	}
	
	public static void main(String[] args) {
		CorrectTableFieldAtDB correct = new CorrectTableFieldAtDB();
		correct.removeSpaceFromTableField(TableField.PHONE, false);
		correct.disconnect();
	}

	private void removeSpaceFromTableField(TableField type, boolean updateFlag){
		
		String query = "select id, "+ type +" from dataset where substr(" + type + ",length(" + type + "),length("+ type + ")-1) = \" \"";
		PreparedStatement pstmt = null;
		Map<Integer,String> updateDataset = new HashMap<>();
		
		try{
			pstmt = conn.prepareStatement(query);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				int id = rs.getInt(1);
				String val = rs.getString(2);
				U.log(id+"\t>> "+val+"<<\t > Len :"+val.length()+"\t > Correct Len :"+U.trim(val).length()+"\t>"+U.trim(val));
				updateDataset.put(id, U.trim(val));
			}
			rs.close();
			pstmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		/*
		 * flag is true then it will update at db
		 */
		if(updateFlag){
			update(updateDataset, type);
		}//eof if
		
		
	}//eof removeSpaceFromTableField()
	
	private void update(Map<Integer,String> updateDataset, TableField type){
		U.log("For Updated record count ::"+updateDataset.size());
		
		//update at database
		String query = "update dataset set " + type + "=?, UPDATED_DATE=? where ID=?";
		int x = 0;
		int[] updateCount;
		PreparedStatement pstmt = null;
		
		U.log("\nStart updating here ...");
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			
			for (Entry<Integer, String> entry : updateDataset.entrySet()) {
				if(type.equals(TableField.PHONE) || type.equals(TableField.FAX)){
					String val = U.formatNumbersAsCode(entry.getValue());
					pstmt.setString(1, val); //updated value
				}else{
					pstmt.setString(1, entry.getValue()); ///updated value
				}
				pstmt.setString(2, U.getTodayDate());  // Totay day for update
				pstmt.setInt(3, entry.getKey());  // ID
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
		
		/*
		 * Update report at updatedDataset
		 */
		if(updateDataset.size()> 0){
			List<Integer> idListForUpdate = new ArrayList<>(updateDataset.keySet());
			Collections.sort(idListForUpdate);
			U.log("Size of Unique List ::"+idListForUpdate.size());
			
			if(idListForUpdate.size() > 0){
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(idListForUpdate);
				report.disconnect();
			}
		}
	}//eof update()
}//eof CorrectTableFieldAtDB
