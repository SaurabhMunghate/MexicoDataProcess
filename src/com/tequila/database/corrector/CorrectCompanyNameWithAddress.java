package com.tequila.database.corrector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.database.connection.DBConnection;
import com.shatam.scrapper.CSVCorrectorInValidFormat;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.DB;
import com.shatam.utils.Path;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;
import com.tequila.database.CreateSerializofCompositeKey;
import com.tequila.database.ScoreTequila;
import com.tequila.database.UpdateScore;

public class CorrectCompanyNameWithAddress {

	private static final String FILE_NAME = 
			"/home/glady/Downloads/MexicoProject/rakeshsir/19_July/StreetPosibleDuplicate_Final_1.csv";
	
	private static final String COMPOSITE_KEY_TEQUILA_SER = "/home/glady/MexicoCache/database/uniqueKeyTequila.ser";

	//write file for duplicate record while updating address.
	List<String[]> writeLines = new ArrayList<>();
	
	//for update at updateddataset
	List<Integer> idListForUpdate = new ArrayList<>();
	
	//for delete record at dataset		
	List<Integer> idListForDelete = new ArrayList<>();


	Connection conn = null;
	public CorrectCompanyNameWithAddress() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
	}
	
	/*
	 * load composite key from serialize file
	 */
	CreateSerializofCompositeKey chk = new CreateSerializofCompositeKey();
	HashSet<String> uniqueKeyHashSet = chk.deserializeUniqueKey(COMPOSITE_KEY_TEQUILA_SER);
	
	public static void main(String[] args) {
		CorrectCompanyNameWithAddress corrector = new CorrectCompanyNameWithAddress();
		corrector.startUpdateAddressAndRemoveDuplicate();
		corrector.disconnect();
	}

/*	void checkCompanyStandard(){
		List<String[]> readLines = U.readCsvFile(FILE_NAME);
		
		int x = 0;
		for(String[] lines : readLines){
			if(x++ == 0)continue;
			
			if(!lines[8].trim().isEmpty()){
				String correctId = lines[10].trim();
				String wrongId = lines[11].trim();
				U.log(getAddressStandardised(lines[9]));
//				U.log(lines[8]+"\t\t==> "+CSVCorrectorInValidFormat.getCompanyNameStandardised(lines[8].trim()));
			}
		}
	}*/
	
	private void startUpdateAddressAndRemoveDuplicate(){
		List<String[]> readLines = U.readCsvFileWithoutHeader(FILE_NAME);

		//key is contain correct id, value with incorrect id
		Map<Integer,Integer> idCorrectIncorrectMap =  new HashMap<>();
		Map<Integer,String> idCorrectAddressForUpdate = new HashMap<>();
		
		String[] header = {"Correct_Id","Incorrect_Id","Correct_Address"};
		writeLines.add(header);
		
		for(String[] lines : readLines){

			if(lines[6].trim().isEmpty()){
				writeLines.add(new String[]{lines[2],lines[3],""});
				continue;
			}
			int correctId = Integer.parseInt(lines[6].trim());
			int id1 = Integer.parseInt(lines[2].trim());
			int id2 = Integer.parseInt(lines[3].trim());
			if(correctId == id1){
				idCorrectIncorrectMap.put(correctId, id2); //id2 is incorrect id
			}else if(correctId == id2){
				idCorrectIncorrectMap.put(correctId, id1); //id1 is incorrect id
			}
				
			//Corrected Address is not empty
			if(!lines[7].trim().isEmpty()){
				idCorrectAddressForUpdate.put(correctId, TranslateEnglish.convertToEnglish(lines[7].trim()));  //correct Address
			}else{
				idListForDelete.add(idCorrectIncorrectMap.get(correctId));
			}

		}//eof for
		
		//send for update address & other field
		if(idCorrectAddressForUpdate.size() > 0){
			updateAddress(idCorrectAddressForUpdate, idCorrectIncorrectMap);
		}
		/*
		 * Update report at updatedDataset
		 */
		if(idListForUpdate.size()> 0){
			U.log("Size Of Id List ::"+idListForUpdate.size());
			Set<Integer> idSet = new HashSet<>(idListForUpdate);
			idListForUpdate.clear();
			idListForUpdate.addAll(idSet);
			Collections.sort(idListForUpdate);
			idSet.clear();		
			U.log("Size of Unique List ::"+idListForUpdate.size());
			
			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			report.startExtractingFromDB(idListForUpdate);
			report.disconnect();
		}
		/*
		 * Delete duplicate record
		 */
		if(idListForDelete.size() > 0){
			U.log("Size Of Id List ::"+idListForDelete.size());
			Set<Integer> idSet = new HashSet<>(idListForDelete);
			idListForDelete.clear();
			idListForDelete.addAll(idSet);
			Collections.sort(idListForDelete);
			idSet.clear();		
			U.log("Size of Unique List ::"+idListForDelete.size());
			
			DeleteReportAtTable deleteReportAtTable = new DeleteReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			deleteReportAtTable.deleteRecordAtDB(idListForDelete);
			deleteReportAtTable.disconnect();
		}
		
		if(writeLines.size() > 1){
			U.writeCsvFile(writeLines, FILE_NAME.replace(".csv", "_Not_Updated.csv"));
		}
		
		//Update score
		updateScore(ScoreTequila.MAIN_TABLE, idListForUpdate);
		updateScore(ScoreTequila.UPDATED_TABLE, idListForUpdate);

	}
	
	private void updateAddress(Map<Integer,String> idCorrectAddressForUpdate, Map<Integer,Integer> idCorrectIncorrectMap){
		List<Integer> correctIdList = new ArrayList<>();
		List<Integer> incorrectIdList = new ArrayList<>();
		
		
		for(Entry<Integer, String> entry : idCorrectAddressForUpdate.entrySet()){
			int correctId = entry.getKey();
			int incorrectId = idCorrectIncorrectMap.get(correctId);
			correctIdList.add(correctId);
			incorrectIdList.add(incorrectId);
		}
		Map<Integer,String[]> correctIdRecords = DB.getIdRecordDetails(correctIdList, conn);
		Map<Integer,String[]> incorrectIdRecords = DB.getIdRecordDetails(incorrectIdList, conn);
		//compare records
		compareRecordsForAddress(correctIdRecords, incorrectIdRecords, idCorrectAddressForUpdate, idCorrectIncorrectMap);
		
	}
	/**
	 * This method is used to compare the records for correct_id and incorrect_id.
	 * @param correctIdRecords : Key is contain correct_id and value is contain records for particular id.
	 * @param incorrectIdRecords : Key is contain incorrect_id and value is contain records for particular id.
	 * @param idCorrectAddressForUpdate : Key is contain correct_id and value is contain corrected address.
	 * @param idCorrectIncorrectMap : Key is contain correct_id and value is contain the incorrect_id.
	 * 
	 */
	private void compareRecordsForAddress(Map<Integer,String[]> correctIdRecords, 
			Map<Integer,String[]> incorrectIdRecords, 
			Map<Integer,String> idCorrectAddressForUpdate,
			Map<Integer,Integer> idCorrectIncorrectMap){
		
		Map<Integer,String[]> updateAddressMap = new HashMap<>();
		List<Integer> immediateRemoveId = new ArrayList<>();
		
		for(Entry<Integer,String[]> entry : correctIdRecords.entrySet()){
			
			String [] data = validateDataAndPerformOperation(entry.getValue(), incorrectIdRecords.get(idCorrectIncorrectMap.get(entry.getKey())));
			/*
			 * to check the correct id's address is match with corrected address value
			 */
			if(data[8].toLowerCase().trim().equals(idCorrectAddressForUpdate.get(entry.getKey()).toLowerCase())){
				updateAddressMap.put(entry.getKey(), data);
				idListForDelete.add(idCorrectIncorrectMap.get(entry.getKey()));
			}else{
				/* To check the correct id's address is not match with corrected address value
				 * then to check whether while updating address in record may create duplicate record or not. 
				 */
				if(!isDuplicateCompositeKey(data, idCorrectAddressForUpdate.get(entry.getKey()))){ //not duplicated
					data[8] = idCorrectAddressForUpdate.get(entry.getKey());//update correct address
					updateAddressMap.put(entry.getKey(), data);
					idListForDelete.add(idCorrectIncorrectMap.get(entry.getKey()));
				}else{
					List<String[]> duplicateData = getDataFromId(data, idCorrectAddressForUpdate.get(entry.getKey()));
					
					if(duplicateData.size() == 1){
						
						int tempId = Integer.parseInt(duplicateData.get(0)[0]); //duplicateData id

						if(tempId != idCorrectIncorrectMap.get(entry.getKey())){
							data = validateDataAndPerformOperation(entry.getValue(), duplicateData.get(0));
						}
						U.log("Correct id :"+entry.getKey()+"\t\tDeleted Id :"+tempId);
						data[8] = idCorrectAddressForUpdate.get(entry.getKey());//update correct address
						updateAddressMap.put(entry.getKey(), data);
						immediateRemoveId.add(tempId);
						
					}else if(duplicateData.size()== 0 || duplicateData.size() > 1){
						//duplicated unique record
						writeLines.add(new String[]{
							""+entry.getKey(),""+idCorrectIncorrectMap.get(entry.getKey()),idCorrectAddressForUpdate.get(entry.getKey())
						});
						idListForDelete.remove(idCorrectIncorrectMap.get(entry.getKey()));
					}//eof else if
					
				}//eof else inner

			}//eof else outer
		}//eof for
		
		/*
		 *Immediate Duplicate ID record for not getting UNIQUE constraint failed exception.
		 */
		
		if(immediateRemoveId.size() > 0){
			U.log("Start removing immediate Duplicate ID record for not getting UNIQUE constraint failed exception");
			U.log("Size Of Id List ::"+immediateRemoveId.size());
			Set<Integer> idSet = new HashSet<>(immediateRemoveId);
			immediateRemoveId.clear();
			immediateRemoveId.addAll(idSet);
			Collections.sort(immediateRemoveId);
			idSet.clear();		
			U.log("Size of Unique List ::"+immediateRemoveId.size());
			
			DeleteReportAtTable deleteReportAtTable = new DeleteReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
			deleteReportAtTable.deleteRecordAtDB(immediateRemoveId);
			deleteReportAtTable.disconnect();
		}
		
		/*
		 * Update Address here
		 */
		updateCorrectIdAddressRecord(new HashSet<>(updateAddressMap.values()));
		
	}//eof compareRecordsForAddress()
	
	public List<String[]> getDataFromId(String[] data, String correctAddress){
		List<String[]> records = new ArrayList<>();
		try(PreparedStatement pstmt = conn.prepareStatement("select * from dataset where sic_sub=? and company_name=? and lower(address)=lower(?) and city=? and state = ? and contact_person=?");){
			pstmt.setString(1, data[4]);
			pstmt.setString(2, data[7]);
			pstmt.setString(3, correctAddress);
			pstmt.setString(4, data[10]);
			pstmt.setString(5, data[11]);
			pstmt.setString(6, data[17]);
			ResultSet rs = pstmt.executeQuery();
			records.addAll(DB.resultSetToList(rs));
			rs.close();
			pstmt.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return records;
	}
	private boolean isDuplicateCompositeKey(String[] data, String correctAddress){

		String sicSub = data[4].trim();
		if(sicSub.length() == 3)sicSub="0"+sicSub;
		
		String contactPerson = data[17];
		if(contactPerson == null) contactPerson = "";
		
		String uniqueKey =	U.toTitleCase(sicSub)+ U.toTitleCase(data[7].trim())+ U.toTitleCase(correctAddress.trim()) + U.toTitleCase(data[10].trim()) 
		+ U.toTitleCase(data[11].trim())+ U.toTitleCase(contactPerson.trim());
		
		if(uniqueKeyHashSet.add(uniqueKey.toLowerCase()))return false;
		else return true;
	}
	
	/**
	 * This method is used to compare between correct data record with incorrect data record.
	 * @param correctData
	 * @param incorrectData
	 * @return
	 */
	private String[] validateDataAndPerformOperation(String[] correctData, String[] incorrectData) {

		correctData[9] = TequilaCorrector.compareNeighbourhood(correctData[9], incorrectData[9]); 
		
		correctData[13] = TequilaCorrector.phoneAndFaxCompare(correctData[13], incorrectData[13]);
		correctData[14] = TequilaCorrector.phoneAndFaxCompare(correctData[14], incorrectData[14]);
		correctData[15] = TequilaCorrector.urlCompare(correctData[15], incorrectData[15]);		
		correctData[16] = TequilaCorrector.emailCompare(correctData[16], incorrectData[16]);
		
		correctData[18] = TequilaCorrector.compareDesignation(correctData[18], incorrectData[18]);
		
		correctData[19] = TequilaCorrector.compareAnnualSales(correctData[19], incorrectData[19]);
		correctData[20] = TequilaCorrector.compareAnnualSales(correctData[20], incorrectData[20]);
		
		correctData[21] = TequilaCorrector.compareEmployeeCount(correctData[21], incorrectData[21]);
		correctData[22] = TequilaCorrector.compareEmployeeCount(correctData[22], incorrectData[22]);
		
		correctData[23] = TequilaCorrector.compareYearInBiz(correctData[23], incorrectData[23]);
		
		correctData[24] = TequilaCorrector.compareLatLong(correctData[24], incorrectData[24]);
		correctData[25] = TequilaCorrector.compareLatLong(correctData[25], incorrectData[25]);

		return correctData;
	}
	
	private void updateCorrectIdAddressRecord(HashSet<String[]> updateSet) {
		U.log("Start updating address and its other records here...");

		String update="update dataset set NEIGHBORHOOD=?,PHONE=?,FAX=?,URL=?,EMAIL=?,TITLE=?,ANNUAL_SALES_VOL_MIN=?,ANNUAL_SALES_VOL_MAX=?,EMP_COUNT_MIN=?,EMP_COUNT_MAX=?,YEARS_IN_BIZ=?,LONGITUDE=?,LATITUDE=?,UPDATED_DATE=?,ADDRESS=? "
				+ "where ID=?";
		
		int updateCount[];
		PreparedStatement pstmt= null;
		try {
			pstmt = conn.prepareStatement(update);
			conn.setAutoCommit(false);
			int i=0;
			for (String[] correctData : updateSet) {
				
				pstmt.setString(1, correctData[9]); //NEIGHBORHOOD
				pstmt.setString(2, correctData[13]); //PHONE
				pstmt.setString(3, correctData[14]); //FAX
				pstmt.setString(4, correctData[15]); //URL
				pstmt.setString(5, correctData[16]); //EMAIL
				pstmt.setString(6, correctData[18]); //TITLE
				pstmt.setString(7, correctData[19]); //ANNUAL_SALES_VOL_MIN
				pstmt.setString(8, correctData[20]); //ANNUAL_SALES_VOL_MAX
				pstmt.setString(9, correctData[21]); //EMP_COUNT_MIN
				pstmt.setString(10, correctData[22]); //EMP_COUNT_MAX
				pstmt.setString(11, correctData[23]); //YEARS_IN_BIZ
				pstmt.setString(12, correctData[24]); //LONGITUDE
				pstmt.setString(13, correctData[25]); //LATITUDE
				pstmt.setString(14, U.getTodayDate()); //today date
				pstmt.setString(15, U.toTitleCase(correctData[8]));
				pstmt.setString(16, correctData[0]); //ID
				
				pstmt.addBatch();
				if ((++i % 100) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows Updated: "+ updateCount.length + "\t" + i);
					conn.commit();
					U.log("Batch Committed");
				}
				
				idListForUpdate.add(Integer.parseInt(correctData[0])); //add Id at list here..				
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows Updated: "+ updateCount.length + "\t" + i);
			conn.commit();
			U.log("Batch Committed");
			conn.setAutoCommit(true);

			pstmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}//eof updateCorrectIdAddressRecord()
	
	
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
	
	private void updateScore(ScoreTequila scoreTequila, List<Integer> idList){
		UpdateScore update = new UpdateScore();
		update.updateScore(scoreTequila, idList);
		update.disconnect();
	}
	
}
