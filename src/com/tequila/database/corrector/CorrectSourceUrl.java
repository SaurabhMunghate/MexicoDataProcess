package com.tequila.database.corrector;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.database.connection.Connect;
import com.shatam.utils.U;

public class CorrectSourceUrl extends Connect{
	private static String fileName = 
			"/home/shatam-3/MexicoCache/UPDATED_RECORD/22_01_2019/Ofix_Source_Url_Correction_22_01_2019.csv";
	private static final String COMPOSITE_KEY_TEQUILA_SER_WITH_ID = "/home/shatam-3/MexicoCache/database/uniqueKeyTequilaWithId.ser";
	
	Map<String,Integer> uniqueKeyWithId = (Map<String, Integer>) U.deserialized(COMPOSITE_KEY_TEQUILA_SER_WITH_ID);
	public static void main(String[] args) {
		U.log("File Name ::"+fileName);
		CorrectSourceUrl  correct = new CorrectSourceUrl();
		correct.updatedSourceUrl(0, 4); //header ---> ID : COMPANY_NAME	 : URL : CREATED_DATE : _SOURCE_URL
	}
	
	/**
	 * This method is used to update Source_URL that are wrong in the database. 
	 * Use this code only in case of updation of inserted records incorrect src_url for the same day 
	 * It's not a part of updation records count for unique and non unique keys
	 * @param indexId
	 * @param indexUpdatedSourceUrl
	 */
	private void updatedSourceUrl(int indexId, int indexUpdatedSourceUrl) {
		Map<Integer, String> updateSrcUrlMap = new HashMap<>();
		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		for(String lines[] : readLines){
			if(lines[indexId].trim().isEmpty())continue;
			String srcUrl = lines[indexUpdatedSourceUrl].trim();			
			if(srcUrl.isEmpty())continue;
			//U.log(lines[indexId]+" :: "+srcUrl);
			int id = Integer.parseInt(lines[indexId].trim());
			updateSrcUrlMap.put(id, srcUrl.toLowerCase());
		}
		if(updateSrcUrlMap.size() > 0){
			updateSrcURLInDB(updateSrcUrlMap); // start updating url
		}
		
	}

	private void updateSrcURLInDB(Map<Integer, String> idSrcUrlMap) {
		String updateQuery = "update dataset set _SOURCE_URL=? where ID=?";
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			U.log("*** Start updating Source URL's here ....");
			for(Entry<Integer, String> entry : idSrcUrlMap.entrySet()){
				pstmt.setString(1, entry.getValue()); //Src_URL
				pstmt.setInt(2, entry.getKey()); //ID
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
		}
		catch(Exception e){
			e.printStackTrace();
		}
		U.log("Total count of updated SOURCE_URL at database ::"+x);
	}

}
