package com.tequila.database.corrector;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.DB;
import com.shatam.utils.Path;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;
import com.tequila.database.CreateSerializofCompositeKey;
import com.tequila.database.validator.CorrectorAtValidateTable;

public class UpdateStandardCompanyName {
		
	private static final String COMPOSITE_KEY_TEQUILA_SER = "/home/glady/MexicoCache/database/uniqueKeyTequila.ser";

	private static final String COMPOSITE_KEY_TEQUILA_SER_WITH_ID = "/home/mypremserver/MexicoDataFiles/Db/uniqueKeyTequilaWithId.ser";
	
	private static final String MAIN_DB_NAME = "tequila.db";
	
	private static final String DELETED_RECORD_FILE_PATH = Path.DELETED_RECORD_PATH +"DELECTED_RECORD_"+U.getTodayDateWith()+"_3.csv";
	
	private static final String LOAD_COMPANY_WORNG_CORRECT_FILE = 
			"/home/mypremserver/MexicoDataFiles/Mar/9_MAr/Update/CompanyNamesMismatch_(20_withAdd_forUpdate.csv";
	
/*	private static final String ADDRESS_CORRECT_FILE_NAME_ = 
			"/home/glady/Downloads/MexicoProject/amit/InnerJoinCompany_NameAndAddress_unique_Add_Score (1).csv";
*/	
	
	Connection conn=null;
	int deleteCount=0;
	int indexOfInCorrectCompName=0;			//inCorrectIndex in file
	int indexOfCorrectCompName=1;			//CorrectIndex in file
	int noUpdate=0;
	int updateIncorrectCompanyNameCount = 0;
	

	HashSet<String[]> updateSetForUpdate = new HashSet<>();
	HashSet<String> deletecRecordSet = new HashSet<>();
	
	List<Integer> idList = new ArrayList<>();
	
	
	public static void main(String[] args) {
		long startTime=System.currentTimeMillis();
		UpdateStandardCompanyName update = new UpdateStandardCompanyName(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);	
//		UpdateStandardCompanyName update = new UpdateStandardCompanyName(DB_PATH, MAIN_DB_NAME);
		
//		update.updateCompanyNameIncorrectToCorrect(LOAD_COMPANY_WORNG_CORRECT_FILE);
//		update.correctCompanyNameWithIncorrectUsingID(indexId, indexCorrectCompany, indexIncorrectCompany);
		update.correctCompanyNameWithIncorrectUsingID(0, 2, 1);
		update.disconnect();
		U.log("Total Time Taken: "+(System.currentTimeMillis()-startTime));
	}
	
	public UpdateStandardCompanyName(String path,String dbName) {
		conn = DBConnection.getConnection(path, dbName);		
		try {
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
/*	public static final getDeletedFileName(String fileName){
//		String fileName = Path.TEQUILA_DB_PATH +"DELECTED_RECORD_"+U.getTodayDateWith()+"_1"+".csv";
		File file = new File(fileName);
		if(file.createNewFile()){
			return fileName;
		}else{
			String fileNumber = fileName.lastIndexOf("_");
		}
	}*/
	private void updateCompanyNameIncorrectToCorrect(String fileName) {
		HashSet<String[]> outputFile = new HashSet<>();

		List<String[]> inputFile = loadFile(fileName);
		try {
			for (String[] input : inputFile) {
				if (input[0].contains(";")) {
					String splitArr[]=input[indexOfInCorrectCompName].split(";");
					for (String split : splitArr) {
						String[]output=new String[2];
						output[0]=split;
						output[1]=input[indexOfCorrectCompName];
						if (!output[0].equals(output[1]))
							outputFile.add(output);
					}
				}else {
					String[]output=new String[2];
					output[0]=input[indexOfInCorrectCompName];
					output[1]=input[indexOfCorrectCompName];
					if (!output[0].equals(output[1]))
						outputFile.add(output);
				}
			}
			inputFile.clear();
			inputFile.addAll(outputFile);
			U.log("Start correcting company name at database here...");
			updateCorrectedCompName(inputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void updateCorrectedCompName(List<String[]> inputFile) {
/*		String sql="select ID,SIC_SUB,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,FAX,URL,EMAIL,CONTACT_PERSON,TITLE,"+
				"ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,LONGITUDE,LATITUDE from dataset where COMPANY_NAME=?";
*/
		String sql="select * from dataset where COMPANY_NAME=?";
		try {
			
			for (String[] inputData : inputFile) {
				
				U.log(Arrays.toString(inputData));
				
				HashSet<String[]> correctCompData = new HashSet<>();
				HashSet<String[]> incorrectCompData = new HashSet<>();
				
				/**
				 * For Incorrect Company Name
				 */
				PreparedStatement psmt = conn.prepareStatement(sql);
				psmt.setString(1, inputData[0]);//incorrectData
				ResultSet rs = psmt.executeQuery();
								
				incorrectCompData.addAll(resultSetToSet(rs));
				rs.close();
				
				/**
				 * For Correct Company Name
				 */
				inputData[1] = TextFormat.getCompanyNameStandardised(inputData[1]); //Standardized company name 
				psmt.setString(1, inputData[1]);//correctData
				rs=psmt.executeQuery();

				correctCompData.addAll(resultSetToSet(rs));
				rs.close();
				psmt.close();
				
//				U.log("Correct Company ::"+correctCompData.size()+"\tIncorrect Company ::"+incorrectCompData.size()+"\tName ::"+inputData[1]);

				/**
				 * Compare records here
				 */
				compareRecordsForCompany(correctCompData, incorrectCompData, inputData[1]);
				
			}//endof for InputFile
			/**
			 * Delete incorrect company records here
			 */
			deleteRecords(deletecRecordSet);
			/**
			 * Update corrected company records here
			 */
			updateCorrectCompanyRecord(updateSetForUpdate);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		U.log("Updated count of records for correct company name in dataset and modifed its other records : "+updateSetForUpdate.size());
		U.log("Update count while only updating incorrect company name is ::"+updateIncorrectCompanyNameCount);
		U.log("Deleted records from dataset :: "+deleteCount);

		U.log("Size of modified records at database ::"+idList.size());
		
		if(idList.size() > 0){
			Set<Integer> idSet = new HashSet<>(idList);
			idList.clear();
			idList.addAll(idSet);
			Collections.sort(idList);
			idSet.clear();
			
			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
			report.startExtractingFromDB(idList);
		}
		
		
	}//eof updateCorrectedCompName()
	
	private HashSet<String[]> resultSetToSet(ResultSet rs) throws SQLException {
	    ResultSetMetaData md = rs.getMetaData();
	    int columns = md.getColumnCount();
	    HashSet<String[]> rows = new HashSet<>();
	    while (rs.next()){
	        String[]  row = new String[columns];
	        for(int i = 1; i <= columns; ++i){
	        	row[i-1] = rs.getString(i);
	        }
	        rows.add(row);
	    }
	    return rows;
	}
	
	
	private synchronized void compareRecordsForCompany(HashSet<String[]> correctCompData, HashSet<String[]> incorrectCompData, String correctCompanyName){
		
		boolean updateFlag = true;
		for (String[] correctData : correctCompData) {
			
			Iterator<String[]> itr = incorrectCompData.iterator();
			
			while (itr.hasNext()) {
				String[]incorrectData = itr.next();
				
				if(correctData[17] == null) correctData[17] = "";
				if(incorrectData[17] == null) incorrectData[17] = "";
				
				/**
				 * SIC_SUB && ADDERSS && CITY && STATE && CONTACT_PERSON
				 */
				if (correctData[4].trim().equals(incorrectData[4].trim()) &&  //SIC_SUB
						correctData[8].trim().equals(incorrectData[8].trim()) && //ADDERSS
 							correctData[10].trim().equals(incorrectData[10].trim()) && //CITY
								correctData[11].trim().equals(incorrectData[11].trim()) && //STATE
									correctData[17].trim().equals(incorrectData[17].trim())){ //CONTACT_PERSON
					
					correctData = validateDataAndPerformOperation(correctData,incorrectData);
					itr.remove();
					updateFlag=false;
				}//eof if
				
			}//eof itr while
			
			if (!updateFlag) {
				updateSetForUpdate.add(correctData);
			}//endofif
			
		}//eof for correctCompData
		
		if (incorrectCompData.size()>0) {
			updateIncorrectRecord(incorrectCompData,correctCompanyName);
		}//eof if
	}
	
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
		
		deletecRecordSet.add(incorrectData[0]);
//		deleteRecord(incorrectData[0]);
		return correctData;
	}
	
	private synchronized void updateCorrectCompanyRecord(HashSet<String[]> updateSet) {
		U.log("Start updating company name and its other records here...");

		String update="update dataset set NEIGHBORHOOD=?,PHONE=?,FAX=?,URL=?,EMAIL=?,TITLE=?,ANNUAL_SALES_VOL_MIN=?,ANNUAL_SALES_VOL_MAX=?,EMP_COUNT_MIN=?,EMP_COUNT_MAX=?,YEARS_IN_BIZ=?,LONGITUDE=?,LATITUDE=?,UPDATED_DATE=? "
				+ "where ID=?";
		
		int updateCount[];
		PreparedStatement pstmt= null;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(update);
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
				pstmt.setString(14, U.getTodayDate()); //Today Date
				pstmt.setString(15, correctData[0]); //ID
				
				pstmt.addBatch();
				if ((++i % 100) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows Updated: "+ updateCount.length + "\t" + i);
					conn.commit();
					U.log("Batch Committed");
				}
				
				idList.add(Integer.parseInt(correctData[0])); //add Id at list here..				
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows Updated: "+ updateCount.length + "\t" + i);
			conn.commit();
			U.log("Batch Committed");
			
			pstmt.close();
			conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	void updateIncorrectRecord(HashSet<String[]> incorrectCompData,String correctName) {
		U.log("Start updating company name here...");
		String updateQuery="update dataset set COMPANY_NAME=?, UPDATED_DATE=? where COMPANY_NAME=? and ID=?";
		PreparedStatement pstmt = null;
		int i = 0;
		int[] updateCount;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			for (String[] data : incorrectCompData) {
				
				pstmt.setString(1, correctName);
				pstmt.setString(2, U.getTodayDate()); //Today date
				pstmt.setString(3, data[7]);  // COMPANY_NAME
				pstmt.setString(4, data[0]);  //ID
				pstmt.addBatch();
				
				if((i++ % 50) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");
				}
				
				idList.add(Integer.parseInt(data[0])); //add Id at list here..
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + i);
			conn.commit();
			System.out.println("Commit the batch");
			
			updateIncorrectCompanyNameCount += i;
			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	void deleteRecords(HashSet<String> deleteRecordSet) {
		U.log("Start deleting the record here....");

/*		String[] header = {	"ID","INDUSTRY_SECTOR","SPANISH_INDUSTRY_SECTOR","SIC_MAJOR","SIC_SUB","PRODUCT_DESC","SPANISH_PRODUCT_DESC","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY",
				"STATE","ZIP","PHONE","FAX","URL","EMAIL","CONTACT_PERSON","TITLE","ANNUAL_SALES_VOL_MIN","ANNUAL_SALES_VOL_MAX", "EMP_COUNT_MIN", 
				"EMP_COUNT_MAX","YEARS_IN_BIZ","LONGITUDE","LATITUDE","_SCORE","_SOURCE_URL","_LAST_UPDATED"
		};
		
		List<String[]> deletedRecord = new ArrayList<>();

		int countRecord = 0;
		try {
			
			for(String id : deleteRecordSet){
				stmt = conn.prepareStatement("select * from dataset where ID="+id);
				ResultSet rs = stmt.executeQuery();
				
				while (rs.next()) {
					deletedRecord.add(new String[]{
							rs.getString("ID"), rs.getString("INDUSTRY_SECTOR"), rs.getString("SPANISH_INDUSTRY_SECTOR"), rs.getString("SIC_MAJOR"),rs.getString("SIC_SUB"),
							rs.getString("PRODUCT_DESC"),rs.getString("SPANISH_PRODUCT_DESC"),rs.getString("COMPANY_NAME"),rs.getString("ADDRESS"),rs.getString("NEIGHBORHOOD"),rs.getString("CITY"),rs.getString("STATE"),
							rs.getString("ZIP"),rs.getString("PHONE"),rs.getString("FAX"),rs.getString("URL"),rs.getString("EMAIL"),rs.getString("CONTACT_PERSON"),rs.getString("TITLE"),rs.getString("ANNUAL_SALES_VOL_MIN"),
							rs.getString("ANNUAL_SALES_VOL_MAX"), rs.getString("EMP_COUNT_MIN"), rs.getString("EMP_COUNT_MAX"),rs.getString("YEARS_IN_BIZ"),rs.getString("LONGITUDE"),rs.getString("LATITUDE"),rs.getString("_SCORE"),
							rs.getString("_SOURCE_URL"),rs.getString("_LAST_UPDATED")	
					});
					countRecord++;
				}
				rs.close();
			}//eof for
		} catch (SQLException e) {
			e.printStackTrace();
		}
*/		
		//load list that contain id 
		List<Integer> idList = new ArrayList<>();
		for(String id : deleteRecordSet){
			idList.add(Integer.parseInt(id));
		}
		
		//load header of the table
		String header[] = null;
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from dataset");){
			header = DB.getTableHeader(rs);
		}catch(SQLException e){
			e.printStackTrace();
		}
		//load duplicate records here
		Map<Integer,String[]> deletedRecordMap = DB.getIdRecordDetails(idList, conn);
		
		U.log("Loading records from database ...");
		
		List<String[]> allRows = new ArrayList<>(deletedRecordMap.values());

		U.log("All Records Size ::"+allRows.size());
		
		U.log("Start inserting at database here .......");
		PreparedStatement pstmt = null;
		int x = 0;
		int updateCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(UpdateReportAtTable.insertQueryAtDeletedTable);
			
			for(String[] rows : allRows){
			
				for(int i = 0; i < rows.length; i++){
					if(i == 28){
						pstmt.setString(i+1, U.getTodayDate());
					}else{
						pstmt.setString(i+1, rows[i]);
					}
				}
				pstmt.addBatch();
				
				if((++x % 1000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total inserted record at deletedDataset table ::"+x);
		
		U.log("Start deleting duplicate composite key record at database here ....");
		String updateQuery = "delete from dataset where id=?";
		pstmt = null;
		x = 0;
		int deleteCount[];
		try{
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			
			for(int id : idList){
				pstmt.setInt(1, id);
				pstmt.addBatch();
				
				if((++x % 1000) == 0){
					deleteCount = pstmt.executeBatch();
					System.out.println("Number of rows deleted: "+ deleteCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}			
			}//eof for
			deleteCount = pstmt.executeBatch();
			System.out.println("Number of rows deleted: "+ deleteCount.length + "\t" + x);
			conn.commit();
			System.out.println("Commit the batch");

			pstmt.close();
			conn.setAutoCommit(true);
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("Total count of deleted duplicated composite key record at database ::"+x);
		
		if(header != null){
			U.writeCsvFile(header, deletedRecordMap.values(), DELETED_RECORD_FILE_PATH);
		}
		
/*		PreparedStatement stmt = null;	
		
		U.log("Deleted record has been write at file, the count is ::"+countRecord);
		deleteCount = countRecord;
		
		int i = 0;
		int[] updateCount;
		
		String sql="delete from dataset where ID=?";
		try{
			stmt=conn.prepareStatement(sql);
			for(String id : deleteRecordSet){
				
				stmt.setString(1, id);
				stmt.addBatch();
				if((i++ % 50) == 0){
					updateCount = stmt.executeBatch();
					System.out.println("Number of rows deleted: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");
				}
			}
			updateCount = stmt.executeBatch();
			System.out.println("Number of rows deleted: "+ updateCount.length + "\t" + i);
			conn.commit();
			System.out.println("Commit the batch");
			stmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		writeFile(header, deletedRecord);
*/	}
	
	/**
	 * This method is used to update company Name in standardized format.<br>
	 * Also, find duplicate record id for updated company name.<br><br>
	 * 
	 * File attribute are, <p>zero index is contain company name record id,<br>
	 * first index is contain incorrect company name format,<br>
	 * second index is contain correct company name format.</p>
	 */
	private void correctCompanyNameWithIncorrectUsingID(int indexId, int indexCorrectCompany, int indexIncorrectCompany){
		/*
		 * load composite key from serialize file
		 */
/*		CreateSerializofCompositeKey chk = new CreateSerializofCompositeKey();
		HashSet<String> uniqueKeyHashSet = chk.deserializeUniqueKey(COMPOSITE_KEY_TEQUILA_SER);
*/		U.log("Load composite key from serialize file here ...");
		Map<String,Integer> uniqueKeyWithId = (Map<String, Integer>) U.deserialized(COMPOSITE_KEY_TEQUILA_SER_WITH_ID);
		U.log("Total Record ::"+uniqueKeyWithId.size());
		/*
		 * Load company correct & incorrect file here..
		 */
		List<String[]> readLines = U.readCsvFileWithoutHeader(LOAD_COMPANY_WORNG_CORRECT_FILE);
		HashSet<String> uniqueIds=new HashSet<>();
		List<String[]> writeLines = new ArrayList<>();
		
		U.log("Total company records ::"+readLines.size());
		List<Integer> idList = loadAllID(readLines, indexId);
		U.log("Total ID's record ::"+idList.size());
		
		List<String[]> datasetForUpdate = new ArrayList<>();
		Map<Integer,String[]> idUniqueRecordMap = DB.getIdRecordCompositeDetails(idList, conn);
		
		String [] lines = null;
		Iterator<String[]> it = readLines.iterator();
		
		while(it.hasNext()){
			lines = it.next();
			if (!uniqueIds.add(lines[indexId])) continue;
			int tempId = Integer.parseInt(lines[indexId]);
			String uniqueKeys[] = idUniqueRecordMap.get(tempId);
			
			if(uniqueKeys == null){
				U.log("Not found ID :"+lines[indexId]);
				writeLines.add(lines);
				continue;
			}
			
			if(uniqueKeys[0].length() == 3)
				uniqueKeys[0] = "0"+uniqueKeys[0];
			
			if(uniqueKeys[5] == null) uniqueKeys[5] = "";
			
			String exsitingUniqueKey=uniqueKeys[0].trim() 
					+ uniqueKeys[1].trim()
					+ uniqueKeys[2].trim()
					+ uniqueKeys[3].trim()
					+ uniqueKeys[4].trim()
					+ uniqueKeys[5].trim();
			String uniqueKey = U.toTitleCase(uniqueKeys[0].trim()) 
					+ U.toTitleCase(lines[indexCorrectCompany].trim()) 
					+ U.toTitleCase(uniqueKeys[2].trim())
					+ U.toTitleCase(uniqueKeys[3].trim())
					+ U.toTitleCase(uniqueKeys[4].trim())
					+ U.toTitleCase(uniqueKeys[5].trim());

/*			if(!uniqueKeyHashSet.add(uniqueKey.toLowerCase())){
				writeLines.add(lines);
			}else{
				datasetForUpdate.add(lines);
			}
*/			
			if (exsitingUniqueKey.equals(uniqueKey)) {
				noUpdate++;
				U.log(tempId+"\t Updation Not Required");
			}else if(uniqueKeyWithId.containsKey(uniqueKey.toLowerCase())){
//				int tempId = uniqueKeyWithId.get(uniqueKey);
				if(tempId == uniqueKeyWithId.get(uniqueKey.toLowerCase())){
					datasetForUpdate.add(lines);
				}else{
					writeLines.add(lines);
				}
			}else{
				uniqueKeyWithId.put(uniqueKey.toLowerCase(), tempId);
				datasetForUpdate.add(lines);
			}
		
		}
		U.log("Total count of records not updated ::"+noUpdate);
		U.log("Total count of records for update ::"+datasetForUpdate.size());
		U.log("Total count of records for getting duplicate ::"+writeLines.size());
		updateIncorrectCompanyNameRecord(datasetForUpdate, indexId, indexCorrectCompany, indexIncorrectCompany);
		List<Integer> updateIdList = loadAllID(datasetForUpdate, indexId);
		
		if(updateIdList.size() > 0){
			Set<Integer> idSet = new HashSet<>(updateIdList);
			updateIdList.clear();
			updateIdList.addAll(idSet);
			Collections.sort(updateIdList);
			idSet.clear();
			
//			UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
			UpdateReportAtTable report = new UpdateReportAtTable(conn);
			report.startExtractingFromDB(updateIdList);
			/*
			 * Validate the company name here
			 */
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			CorrectorAtValidateTable.validateCompanyNameAtValidationTable(updateIdList);
//			report.disconnect();
		}
		writeLines.add(0,new String[]{"ID","INCORRECT_COMPANY_NAME","CORRECT_COMPANY_NAME"});
		if(writeLines.size() > 1)
			U.writeCsvFile(writeLines, LOAD_COMPANY_WORNG_CORRECT_FILE.replace(".csv", "_FailedID.csv"));
	}
	
	void updateIncorrectCompanyNameRecord(List<String[]> readLines, int indexId, int indexCorrectCompany, int indexIncorrectCompany) {
		U.log("Start updating company name here...");
		String updateQuery="update dataset set COMPANY_NAME=?, UPDATED_DATE=? where COMPANY_NAME=? and ID=?";
//		String updateQuery="update dataset set COMPANY_NAME=? where COMPANY_NAME=? and ID=?";
		PreparedStatement pstmt = null;
		int i = 0;
		int[] updateCount;
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(updateQuery);
			for (String[] data : readLines) {
				
				pstmt.setString(1,  TextFormat.getCompanyNameStandardised(data[indexCorrectCompany]));
				pstmt.setString(2, U.getTodayDate()); //Today date
				pstmt.setString(3, data[indexIncorrectCompany]);  // COMPANY_NAME
				pstmt.setString(4, data[indexId]);  //ID
//				pstmt.setString(2, data[indexIncorrectCompany]);  // COMPANY_NAME
//				pstmt.setString(3, data[indexId]);  //ID
				pstmt.addBatch();
				
				if((i++ % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");
				}
				
				idList.add(Integer.parseInt(data[indexId])); //add Id at list here..
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + i);
			conn.commit();
			System.out.println("Commit the batch");
//			U.log("Update");
			updateIncorrectCompanyNameCount += i;
			pstmt.close();
			conn.setAutoCommit(true);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private List<Integer> loadAllID(List<String[]> readLines, int indexId){
		List<Integer> idList = new ArrayList<>();
		for(String[] lines : readLines){
			if(!lines[indexId].trim().isEmpty())
				idList.add(Integer.parseInt(lines[indexId].trim()));
		}
		return idList;
	}
	
	private List<String[]>  loadFile(String fileName){
		CSVReader reader = null;
		List<String[]>  readLines = null;
		try{
			reader = new CSVReader(new FileReader(fileName));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		readLines.remove(0);
		U.log("Load input file...... Done");
		U.log("Size of record is ::"+readLines.size());
		return readLines;
	}
	
	private void writeFile(String[] header, List<String[]> records){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(DELETED_RECORD_FILE_PATH),',');
			writer.writeNext(header);
			writer.writeAll(records);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log("Writing deleted record file.....Done");
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
	
	//Date date = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final String getTodayDate(){
		return dateFormat.format(new Date());
	}
	

}
