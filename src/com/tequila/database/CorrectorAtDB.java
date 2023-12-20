/**
 * @author Sawan
 */
package com.tequila.database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.WrongMethodTypeException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.database.connection.Connect;
import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.utils.DB;
import com.shatam.utils.DateFormat;
import com.shatam.utils.Field;
import com.shatam.utils.Path;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;
import com.tequila.database.corrector.UpdateReportAtTable;
import com.tequila.database.validator.UpdateValidateTable;

public class CorrectorAtDB extends Connect{

/*	Connection conn = null;
	public CorrectorAtDB() {
//		conn = DBConnection.getConnection(TestPath.DB_PATH, TestPath.DB_NAME);
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);			
	}
*/	
	String fileName = "/home/glady/MexicoCache/database/Correctors/26_Nov_18/Corrector_Company_Name_26_Nov_18_FailedID.csv";


	public static void main(String[] args) {
		CorrectorAtDB corrector = new CorrectorAtDB();
		//corrector.validateStateFromZip();
		//corrector.findDifferenceAtNewAndOriginalDB();
		//corrector.findStateCorrectFormat(TestPath.DB_PATH, TestPath.DUP_DB_NAME,"datasetdup");
		//corrector.findStateCorrectFormat(TestPath.DB_PATH, TestPath.DB_NAME,"dataset");
//		corrector.findSubSICInfo();
		//corrector.checkYearInBizAtDB();
		//corrector.findSICWrongRecord();
		
		
//		corrector.findScoreForID(fileName);
		
//		corrector.updatePhonesAtDB(0,9);
//		corrector.findIncorrectFormatForCompanyName();
//		corrector.findUniqueRecordIdForDupRecordID(2);
		corrector.findUniqueRecordIdForDupRecordID(2,1);
//		corrector.findEmailInCorrect();

		
		corrector.disconnect();
	}
	
	private void findIncorrectFormatForCompanyName(){
		List<String[]> writeLines = new ArrayList<>();
		writeLines.add(new String[]{"INCORRECT_COMPANY_NAME","CORRECT_COMPANY_NAME","ID"});
		//Select distinct company_name from dataset where company_name like '% Sa De Cv' order by company_name limit 50000
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("Select * from dataset");){
			
			while(rs.next()){
				String companyName = rs.getString(Field.COMPANY_NAME.toString());
				String temp = TextFormat.getCompanyNameStandardised(companyName);
				if(!temp.equals(companyName)){
					writeLines.add(new String[]{companyName, temp,rs.getString(Field.ID.toString())});
				}
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		U.log("File size ::"+writeLines.size());
		U.writeCsvFile(writeLines, Path.CORRECTORS_DIR+"COMPANY_NAME_CORRECTION_"+U.getTodayDateWith()+".csv");

/*		int count = 0;
		try(Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("Select * from dataset");){
				
				while(rs.next()){
					String companyName = rs.getString(Field.COMPANY_NAME.toString());
					String temp = TextFormat.getCompanyNameStandardised(companyName);
					if(!temp.equals(companyName)){
						count++;
					}
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
		U.log("Count ::"+count);
*/	
		}
	
	private void findUniqueRecordIdForDupRecordID(int indexId){

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		List<Integer> idList = new ArrayList<>();
		
		for(String[] lines : readLines){
			if(lines[indexId].isEmpty())continue;
			idList.add(Integer.parseInt(lines[indexId].trim()));
		}
		
		Map<Integer, String[]> idRecordMap = DB.getIdRecordCompositeDetails(idList, conn);
	
		PreparedStatement pstmt = null;
		try{
			
			for(Map.Entry<Integer, String[]> entry : idRecordMap.entrySet()){
				List<String> duplicateIdList = new ArrayList<>();
				String[] data = entry.getValue();
				
				String query = "select * from dataset where sic_sub=? and lower(company_name)=lower(?) and address=? and city=? and state=? and contact_person=?";
				if(data[5] == null)
					query = "select * from dataset where sic_sub=? and lower(company_name)=lower(?) and address=? and city=? and state=? and contact_person is null";
				
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, data[0]);
				pstmt.setString(2, data[1]);
				pstmt.setString(3, data[2]);
				pstmt.setString(4, data[3]);
				pstmt.setString(5, data[4]);
				if(data[5] != null)
					pstmt.setString(6, data[5]);
				ResultSet rs = pstmt.executeQuery();
				
				while(rs.next()){
					if(entry.getKey() != rs.getInt(Field.ID.toString())){
						duplicateIdList.add(rs.getString(Field.ID.toString()));
					}
				}
				U.log("Id :"+entry.getKey()+"\tDupID :"+duplicateIdList);
				rs.close();
			}
			pstmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	private void findUniqueRecordIdForDupRecordID(int indexId, int indexCorrectCompanyName){

		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		List<String[]> writeLines = new ArrayList<>();
		
		List<Integer> idList = new ArrayList<>();
		
		for(String[] lines : readLines){
			if(lines[indexId].isEmpty())continue;
			idList.add(Integer.parseInt(lines[indexId].trim()));
		}
		
		Map<Integer, String[]> idRecordMap = DB.getIdRecordCompositeDetails(idList, conn);
	
		PreparedStatement pstmt = null;
		try{
			for(String[] lines : readLines){
				if(lines[indexId].isEmpty())continue;
				
				int id = Integer.parseInt(lines[indexId].trim());
				
//			for(Map.Entry<Integer, String[]> entry : idRecordMap.entrySet()){
				List<String> duplicateIdList = new ArrayList<>();
//				String[] data = entry.getValue();
				String[] data = idRecordMap.get(id);
				
				if(data == null){
					U.log("Not found id ::"+id);
					continue;
				}
				String query = "select * from dataset where sic_sub=? and lower(company_name)=lower(?) and address=? and city=? and state=? and contact_person=?";
				if(data[5] == null)
					query = "select * from dataset where sic_sub=? and lower(company_name)=lower(?) and address=? and city=? and state=? and contact_person is null";
				
				pstmt = conn.prepareStatement(query);
				pstmt.setString(1, data[0]);
				pstmt.setString(2, lines[indexCorrectCompanyName]);
				pstmt.setString(3, data[2]);
				pstmt.setString(4, data[3]);
				pstmt.setString(5, data[4]);
				if(data[5] != null)
					pstmt.setString(6, data[5]);
				ResultSet rs = pstmt.executeQuery();
				
				while(rs.next()){
/*					if(entry.getKey() != rs.getInt(Field.ID.toString())){
						duplicateIdList.add(rs.getString(Field.ID.toString()));
					}
*/					if(id != rs.getInt(Field.ID.toString())){
						duplicateIdList.add(rs.getString(Field.ID.toString()));
					}
				}
//				U.log("Id :"+entry.getKey()+"\tDupID :"+duplicateIdList);
				U.log("Id :"+id+"\tDupID :"+duplicateIdList);
				if(duplicateIdList.size() == 1){
					writeLines.add(new String[]{
						""+id,"","","","","","","","","","","", "Possible Duplicate of "+duplicateIdList.get(0),"",""
					});
				}else{
					if(duplicateIdList.size() > 1){
						String ids = "";
						for(String dupId : duplicateIdList){
							ids = ids+dupId+";";
						}
						ids = ids.replaceAll(";$", "");
						writeLines.add(new String[]{
								""+id,"","","","","","","","","","","", "Possible Duplicate of "+ids,"",""
							});
					}//EOF if
				}
				rs.close();
			}
			if(pstmt != null)
				pstmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		writeLines.add(0, new String[]{
			"ID","SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","UPDATED_URL","_STATUS",
			"CONTACT_PERSON","TITLE"
		});
		
		if(writeLines.size() > 1){
			U.writeCsvFile(writeLines, fileName.replace(".csv", "_Duplicate_Rec_"+U.getTodayDateWith()+".csv"));
		}
	}
	
	private void findEmailInCorrect(){
		String[] wrongEmailExp = {".mx.","com.mx.","com.m","hotmailcom","hotmail",".commx","netmx","net-mx",
				"prodigynetmx","prodigy","avantelnet","mailinternetcommx","yahoocommx","attnetmx","megarednetmx",
				"gmailcom","yahoocom","yahoo","gmail","infoselnetmx","df1telmexnetmx","mxe","hotmail:com","com'",".ne"};
		
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("select * from dataset where email is not null");
			){
			U.log("Start ...");
				while(rs.next()){
					String email = rs.getString("email");

					if(!email.contains(";"))continue;
					// || !email.contains(",")
					if(email.contains(";")){
						U.log(rs.getInt("id")+"\t"+email);
						String vals[] = email.split(";");
						for(String val : vals){
							val = val.trim();
							for(String exp : wrongEmailExp){
								if(val.endsWith(exp)){
									U.log(rs.getInt("id")+"\t"+email+"\tMatch :"+exp);
								}
							}
						}
					}
					if(email.contains(",")){
						U.log("Found comm===>"+rs.getInt("id")+"\t"+email);
					}
				}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	private void updatePhonesAtDB(int indexID, int indexUpdatedPhone){
		
		List<Integer> updatedIdList = new ArrayList<>();
		
		List<String[]> readLines = U.readCsvFileWithoutHeader(fileName);
		U.log("Total File Size ::"+readLines.size());

		String query = "update dataset set phone=?, updated_date=? where id=?";
		PreparedStatement pstmt = null;

		int x = 0;
		int updateCount[];
		
		try {
			conn.setAutoCommit(false);
			pstmt = conn.prepareStatement(query);
			for(String lines[] : readLines){

				if(lines[indexID].trim().isEmpty())continue;
				if(lines[indexUpdatedPhone].trim().isEmpty())continue;

				pstmt.setString(1, lines[indexUpdatedPhone].trim());
				pstmt.setString(2, U.getTodayDate());
				pstmt.setString(3, lines[indexID].trim());
				pstmt.addBatch();
				
				if((++x % 5000) == 0){
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows deleted: "+ updateCount.length + "\t" + x);
					conn.commit();
					System.out.println("Commit the batch");
				}
				
				updatedIdList.add(Integer.parseInt(lines[indexID].trim())); //add Id at list here.
				
			}//eof for
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows deleted: "+ updateCount.length + "\t" + x);
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
		if(updatedIdList.size()> 0){
			Set<Integer> set = new HashSet<>(updatedIdList);
			updatedIdList.clear();
			updatedIdList.addAll(set);
			Collections.sort(updatedIdList);
			set.clear();
			U.log("Size of Unique List ::"+updatedIdList.size());
			
			if(updatedIdList.size() > 0){
				UpdateReportAtTable report = new UpdateReportAtTable(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);
				report.startExtractingFromDB(updatedIdList);
				report.disconnect();
			}
		}
	}
	
	void findScoreForID(String fileName){
		List<String[]> readLines = U.readCsvFile(fileName);
		
		Map<String, String> uniqueScoreIdMap = new HashMap<>();
		Set<String> uniqueId = new HashSet<>();
		int i = 0;
		for(String [] lines : readLines){
			if(i++ == 0)continue;
			uniqueId.add(lines[4].trim());
			uniqueId.add(lines[5].trim());
		}
		U.log("Count of ID's :: "+uniqueId.size());
		
		String query  = "select _score from dataset where id=";
		PreparedStatement stmt = null;
		try {
			for(String id : uniqueId){
				stmt = conn.prepareStatement(query+id);
				ResultSet rs = stmt.executeQuery();
				while (rs.next()){
					String score = rs.getString(1);
					U.log("ID ::"+id+"\t\tScore :"+score);
					uniqueScoreIdMap.put(id, score);
				}
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
			disconnect();
			System.exit(0);
		}
		
		String [] header = {
			"Company_Name_1","Company_Name_2","Address_1","Address_2","ID1","ID2","Score_ID1","Score_ID2","Correct_Company_Name","Correct_Address"
		};
		List<String[]> writeLines = new ArrayList<>();
		writeLines.add(header);
		i = 0;
		for(String [] lines : readLines){
			if(i++ == 0)continue;
			
			String score1 = uniqueScoreIdMap.get(lines[4].trim());
			String score2 = uniqueScoreIdMap.get(lines[5].trim());
			if(score1 == null) score1 = "";
			if(score2 == null) score2 = "";
			writeLines.add(new String[]{
				lines[0],lines[1],lines[2],lines[3],lines[4],lines[5], score1,score2,lines[6],lines[7]
			});
		}
		if(writeLines.size() > 1)
			U.writeCsvFile(writeLines, fileName.replace(".csv", "_Add_Score.csv"));
	}
	
	void findDifferenceAtNewAndOriginalDB(){
		HashSet<String> uniqueKeySet = new HashSet<>();
		
		int count = 0;
		String query = "SELECT COMPANY_NAME,ADDRESS,CITY,STATE,CONTACT_PERSON FROM dataset";
		try(Connection conn1 = DBConnection.getConnection(Path.TEQUILA_DB_PATH, "tequilaFinal.db");
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query);)
		{
			while(rs.next()){
				String uniqueKey = rs.getString(1).trim()+
						rs.getString(2).trim()+
						rs.getString(3).trim()+
						rs.getString(4).trim()+
						rs.getString(5).trim();
				
				if(!uniqueKeySet.add(uniqueKey.toLowerCase())){
					count++;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Total unique key ::"+uniqueKeySet.size());
		U.log("Total duplicate unique key ::"+count);
		
		int differCount = 0;
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);)
		{
			while(rs.next()){
				String uniqueKey = rs.getString(1).trim()+
						rs.getString(2).trim()+
						rs.getString(3).trim()+
						rs.getString(4).trim()+
						rs.getString(5).trim();
				
				if(uniqueKeySet.add(uniqueKey.toLowerCase())){
					differCount++;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Total difference records with original and new db ::"+differCount);
	}
	
	private void findSICWrongRecord(){
		List<String[]> dataset = new ArrayList<>();
		
		String query = "select id,sic_major,sic_sub from dataset";
		try(Connection conn1 = DBConnection.getConnection(Path.TEQUILA_DB_PATH,"tequila.db");
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query);)
		{
			while(rs.next()){				
				if(!rs.getString("sic_sub").trim().startsWith(rs.getString("sic_major"))){
					U.log(rs.getString("sic_sub")+"\t"+rs.getString("sic_major"));
					dataset.add(new String[]{
						rs.getString("id"),
						rs.getString("sic_major"),
						rs.getString("sic_sub")	
					});
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Size of wrong sic & sic_sub at dataset ::"+dataset.size());
		for(String[] vals : dataset){
			U.log(Arrays.toString(vals));
		}
	}
	
	/**
	 * This method is used to check if value of years_in_biz is in correct format or not at database.<br>
	 * If it is not in correct format then find its correct format. 
	 */
	void checkYearInBizAtDB(){
		int dup = 0;
		HashSet<String> dateSet = new HashSet<>();
		
		String query1 = "select years_in_biz from dataset where years_in_biz!='' group by years_in_biz";
		Connection conn1 = DBConnection.getConnection(Path.TEQUILA_DB_PATH, "tequilaFinal.db");

		try(Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query1);)
		{
			while(rs.next()){
				dateSet.add(rs.getString("years_in_biz"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
/*		try(BufferedReader br = new BufferedReader(new FileReader(Path.TEQUILA_DB_PATH+"dateFile.txt"));){
			String line = null;
			while((line = br.readLine()) != null){
				if(!dateSet.add(line)){
					dup++;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
*/		U.log("Size of set::"+dateSet.size());
		U.log("Count of duplicate date :::"+dup);
		
/*		int count=0;
		HashMap<String,String> correctDateMap = new HashMap<>();
		for(String inputDate : dateSet){
			if(!DateFormat.validateDate(inputDate)){
				count++;
				String newDate = DateFormat.correctDate(inputDate);				
				if(DateFormat.validateDate(newDate)){
					U.log(inputDate +"\t\t"+DateFormat.getDate(newDate));
					correctDateMap.put(inputDate,DateFormat.getDate(newDate));
				}else{
					U.errLog(inputDate+"\t\t"+newDate);
				}
			}			
		}
		U.log("Count of date is not in proper format ::"+count);
		U.log("Size of corrected date map ::"+correctDateMap.size());
	*/	
		HashSet<String> wrongDateSet = new HashSet<>();
		for(String inputDate : dateSet){
			if(U.matches(inputDate, "\\d{1,2}/\\d{1,2}/\\d{4}")){
				String vals[] = inputDate.split("/");
				if(vals.length == 3){
					int yyyy = Integer.parseInt(vals[2].trim());
					if(yyyy >= 2018){
						U.errLog(inputDate);
						wrongDateSet.add(inputDate.trim());
					}
					
				}
			}
		}
		U.log("Size of wrong date format ::"+wrongDateSet.size());
		String query = "select id from dataset where years_in_biz=?";

		
		HashMap<String,String> correctDateMapWithID = new HashMap<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			pstmt = conn1.prepareStatement(query);
			U.log("Start searching from database here....");
			for(String wrongDate : wrongDateSet){
				
				pstmt.setString(1, wrongDate);				
				rs = pstmt.executeQuery();
				while(rs.next()){
					correctDateMapWithID.put(rs.getString("ID"), wrongDate);
				}
				rs.close(); 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(!pstmt.isClosed() && pstmt != null){
					pstmt.close(); pstmt = null;
				}
				if(!conn1.isClosed() && conn1 != null){
					conn1.close(); conn1 = null;
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		
		U.log("Size of correct date with its ID :::"+correctDateMapWithID.size());

		for(Entry<String, String> entry : correctDateMapWithID.entrySet()){
			U.log(entry.getKey()+"\t\t"+entry.getValue());
		}
	}//eof checkYearInBizAtDB();
	
	/**
	 * This method is used to validate state from zip.
	 */
	void validateStateFromZip(){
		List<String[]> dataset = new ArrayList<>();
		String query = "select state, zip from dataset where zip !=\"\" group by zip,state";
		try(Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(query);){

			while(rs.next()){
				dataset.add(new String[]{
					rs.getString("state").trim(),
					rs.getString("zip").trim()
				});
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Size of dataset of state & zip ::"+dataset.size());
		
		for(String data[] : dataset){
			data[1] = data[1].replace(" ", "");
			String state = U.findStateFromZip(data[1]);
			if(state == null){
				System.err.println(data[0]+"\t"+data[1]);
			}else{
				if(!state.equals(data[1])){
					U.log(data[0]+"\t"+data[1]+"\t"+state);
				}
			}
		}
	}
	/**
	 * This method is used to find sub-sic information from SIC Map. 
	 */
	private final void findSubSICInfo(){
		HashMap<String, String[]> sicInfoMap = new HashMap<>();
		
		List<String> subSICList = new ArrayList<>();
		String query ="select distinct sic_sub from dataset";
		try(Connection conn1 = DBConnection.getConnection(Path.TEQUILA_DB_PATH,"tequila.db");
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query);)
		{
			while(rs.next()){
				subSICList.add(rs.getString("sic_sub"));
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Size of sub-sic ::"+subSICList.size());
		
		List<String> unknownSubSIC = new ArrayList<>();
		for(String subSIC : subSICList){
			//U.errLog(subSIC);
			if(subSIC.length() == 3)
				subSIC = "0"+subSIC;
			
			
			String [] data = U.extractSICInfo(subSIC);
			if(data == null){
				unknownSubSIC.add(subSIC);
				U.errLog(subSIC);
			}else{
				//U.log(subSIC+"\t"+Arrays.toString(data));
				sicInfoMap.put(subSIC, data);
			}
		}
		U.log("Size of subSIC Info is ::"+sicInfoMap.size());
		U.log("Size of unknown sub-SIC ::"+unknownSubSIC.size());
		
		generateUnknownSubSICSV(unknownSubSIC);
		
		//update records at database using sic-sub info.
		//updateSICInfoAtDB(sicInfoMap);
	}
	
	private final void generateUnknownSubSICSV(List<String> unknownSubSIC){
		//27 columns
		String[] header ={"ID","INDUSTRY_SECTOR","SPANISH_INDUSTRY_SECTOR","SIC_MAJOR","SIC_SUB","PRODUCT_DESC","SPANISH_PRODUCT_DESC","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE",
				"FAX","URL","EMAIL","CONTACT_PERSON","TITLE","ANNUAL_SALES_VOL_MIN","ANNUAL_SALES_VOL_MAX","EMP_COUNT_MIN","EMP_COUNT_MAX","YEARS_IN_BIZ","LONGITUDE","LATITUDE","_SCORE"};
		
		String filePath = Path.TEQUILA_DB_PATH + "Unknown_sub_sic_records.csv";
		
		List<String[]> dataset = new ArrayList<>();

		
		String query = "select * from dataset where sic_sub=?";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn1 = DBConnection.getConnection(Path.TEQUILA_DB_PATH,"tequila.db");
		
		try{
			pstmt = conn1.prepareStatement(query);
			
			for(String subSIC : unknownSubSIC){
				pstmt.setString(1, subSIC);
				rs = pstmt.executeQuery();
				U.log(subSIC);
				while(rs.next()){
					dataset.add(new String[]{
						rs.getString("ID"),rs.getString("INDUSTRY_SECTOR"),rs.getString("SPANISH_INDUSTRY_SECTOR"),rs.getString("SIC_MAJOR"),rs.getString("SIC_SUB"),rs.getString("PRODUCT_DESC"),
						rs.getString("SPANISH_PRODUCT_DESC"),rs.getString("COMPANY_NAME"),rs.getString("ADDRESS"),rs.getString("NEIGHBORHOOD"),rs.getString("CITY"),rs.getString("STATE"),
						rs.getString("ZIP"),rs.getString("PHONE"),rs.getString("FAX"),rs.getString("URL"),rs.getString("EMAIL"),rs.getString("CONTACT_PERSON"),
						rs.getString("TITLE"),rs.getString("ANNUAL_SALES_VOL_MIN"),rs.getString("ANNUAL_SALES_VOL_MAX"),rs.getString("EMP_COUNT_MIN"),rs.getString("EMP_COUNT_MAX"),rs.getString("YEARS_IN_BIZ"),
						rs.getString("LONGITUDE"),rs.getString("LATITUDE"),rs.getString("_SCORE")
					});
				}
			}
			rs.close();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally{
			try{
				if(!pstmt.isClosed() && pstmt != null){
					pstmt.close(); pstmt = null;
				}
				if(!conn1.isClosed() && conn1 != null){
					conn1.close(); conn1 = null;
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		U.log("Size of dataset ::"+dataset.size());
		
		if(dataset.size() > 1){
			try(CSVWriter writer = new CSVWriter(new FileWriter(filePath),',');){
				writer.writeNext(header);
				writer.writeAll(dataset);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			U.log("File is create at path "+filePath);
		}
	}
	/**
	 * This method is used to update correct format of sic & sub sic information along with its industry sector and product description.
	 * @param sicInfoMap
	 */
	private final void updateSICInfoAtDB(HashMap<String, String[]> sicInfoMap){
		List<String[]> dataset = new ArrayList<>();
		
		String findQuery = "select id,sic_sub from dataset where sic_sub=?";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Connection conn1 = DBConnection.getConnection(Path.TEQUILA_DB_PATH,"tequila.db");
		try{
			pstmt = conn1.prepareStatement(findQuery);
			
			for(Entry<String, String[]> entry : sicInfoMap.entrySet()){
				pstmt.setString(1, entry.getKey());
				rs = pstmt.executeQuery();
				
				while(rs.next()){
					dataset.add(new String[]{
						rs.getString("id").trim(),
						entry.getKey()
					});
				}
			}
			rs.close();
		}catch(SQLException e){
			e.printStackTrace();			
		}finally{
			try{
				if(!pstmt.isClosed() && pstmt != null){
					pstmt.close(); pstmt = null;
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		U.log("Size of dataset is ::"+dataset.size());
		int updateCount[];
		int i = 0;

		String updateQuery = "update dataset set INDUSTRY_SECTOR=?, SPANISH_INDUSTRY_SECTOR=?, PRODUCT_DESC=?, SPANISH_PRODUCT_DESC=? where ID=? and SIC_MAJOR=? and SIC_SUB=?";

		try{
			
			pstmt = conn1.prepareStatement(updateQuery);
			conn1.setAutoCommit(false);
			
			for(String[] idVals : dataset){
				String id = idVals[0];
				String subSIC = idVals[1];

				String[] data = sicInfoMap.get(subSIC);
				
				pstmt.setString(1, data[0].trim()); //INDUSTRY_SECTOR
				pstmt.setString(2, data[1].trim()); //SPANISH_INDUSTRY_SECTOR
				pstmt.setString(3, data[5].trim()); //PRODUCT_DESC
				pstmt.setString(4, data[6].trim()); //SPANISH_PRODUCT_DESC
				pstmt.setString(5, id);	//ID
				pstmt.setString(6, data[3]); //SIC_MAJOR
				pstmt.setString(7, subSIC); //SIC_SUB
				pstmt.addBatch();

				if ((++i % 10000) == 0) {
					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows updated: "+ updateCount.length + "\t" + i);
					conn1.commit();
					System.out.println("Commit the batch");
				}
			}
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows updated: "+ updateCount.length + "\t" + i);
			conn1.commit();
			System.out.println("Commit the batch");
			System.out.println("exit");
			System.out.println("Total updated records :" + i);
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			try{
				if(!pstmt.isClosed() && pstmt != null){
					pstmt.close(); pstmt = null;
				}
				if(!conn1.isClosed() && conn1 != null){
					conn1.setAutoCommit(true);
					conn1.close(); conn1 = null;
				}
			}catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}//eof updateSICInfoAtDB()
	
	/**
	 * This method is used to find wrong state value and correct into its proper English format at database.
	 */
	private final void findStateCorrectFormat(String dbPath, String dbName, String tableName){
		List<String> stateList = new ArrayList<>();
		String query =  "Select distinct state from "+tableName;
		try(Connection conn1 = DBConnection.getConnection(dbPath, dbName);
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery(query);)
		{
			while(rs.next()){
				stateList.add(rs.getString("state"));
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		U.log("Size of State list : "+stateList.size());
		List<String> wrongStateList = new ArrayList<>();
		HashMap<String,String> correctStateMap = new HashMap<>();
		for(String state : stateList){
			boolean isState = U.isState(state);
			if(isState){
				String correctState = U.matchState(state);
				if(!state.equals(correctState)){
					U.log(state+"\t::Correct format : "+correctState);
					correctStateMap.put(state, correctState);
				}
			}else{
				wrongStateList.add(state.trim());
				U.errLog(state);
			}
		}
		U.log("Size of wrong state list ::"+wrongStateList.size());
		U.log("Size of correct state map ::"+correctStateMap.size());
		if(wrongStateList.size() > 0){
			checkWrongStateWithCity(wrongStateList,dbPath,dbName,tableName);
		}
		if(correctStateMap.size() > 0){
			updateCorrectStateAtDB(correctStateMap,dbPath,dbName,tableName);
		}
	}
	
	/**
	 * This method is used correct state field with correct format of state value at database.
	 * @param correctStateMap
	 * @param dbPath
	 * @param dbName
	 * @param tableName
	 */
	private final void updateCorrectStateAtDB(HashMap<String,String> correctStateMap, String dbPath, String dbName, String tableName){
		U.log("start updating state with correct format of state here.....");
		String query = "update "+tableName+" set state=? where state=?";
		Connection conn1 = DBConnection.getConnection(dbPath,dbName);
		PreparedStatement stmt = null;
		try{
			stmt = conn1.prepareStatement(query);
			conn1.setAutoCommit(false);
			for(Map.Entry<String, String> map : correctStateMap.entrySet()){
				
				stmt.setString(1, map.getValue()); //state values
				stmt.setString(2, map.getKey()); //city values
				stmt.addBatch();
			}
			int[] updateCount = stmt.executeBatch();
			U.log("Count of update record :"+updateCount.length);
			conn1.commit();
			U.log("Commit batch");
			conn1.setAutoCommit(true);
			stmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Update corrected state at table '"+tableName+"' is done....");
		try{
			if(!conn1.isClosed()){
				conn1.close();
				conn1 = null;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	/**
	 * This method is used to find if city contain state value.
	 * @param wrongStateList
	 * @param dbPath
	 * @param dbName
	 * @param tableName
	 */
	private final void checkWrongStateWithCity(List<String> wrongStateList, String dbPath, String dbName, String tableName){
		U.log("start checking wrong state at database here......");
		HashMap<Integer,String[]> dataset = new HashMap<>();
		
		Connection conn1 = DBConnection.getConnection(dbPath,dbName);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try{
			pstmt = conn1.prepareStatement("select id,city,state from "+tableName+" where state=?");
			
			for(String state : wrongStateList){
				pstmt.setString(1, state);
				rs = pstmt.executeQuery();
				while(rs.next()){
					dataset.put(rs.getInt("id"), new String[]{
							rs.getString("city"),
							rs.getString("state")
					});
				}
			}
			rs.close();
			pstmt.close();
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		U.log("Size of corrector dataset : "+dataset.size());
		int matchCount = 0;
		for(Map.Entry<Integer, String[]> map : dataset.entrySet()){
			int id = map.getKey();
			String[] vals = map.getValue();
			
			boolean isState = U.isState(vals[0]);
			if(isState){
				matchCount++;
				U.log(vals[0]+"\t::Correct format : "+U.matchState(vals[0])+"\t\t"+id);				
			}
		}
		try {
			if(!conn1.isClosed()){
				conn1.close();
				conn1 = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	
		/*
		 * Swap city with state, since city field content state value.
		 */
		if(dataset.size() == matchCount){
			U.log("Match all data....");
			swapCityWitStateAtDB(dataset,TestPath.DB_PATH, TestPath.DUP_DB_NAME,tableName);
		}

	}
	/**
	 * This method is used to swap data of city with state.
	 * @param dataset
	 * @param dbPath
	 * @param dbName
	 * @param tableName
	 */
	private final void swapCityWitStateAtDB(HashMap<Integer,String[]> dataset, String dbPath, String dbName, String tableName){
		U.log("start swapping city with state here...........");
		String query = "update "+tableName+" set city=?,state=? where id=?";
		Connection conn1 = DBConnection.getConnection(dbPath,dbName);
		PreparedStatement stmt = null;
		try{
			stmt = conn1.prepareStatement(query);
			conn1.setAutoCommit(false);
			for(Map.Entry<Integer, String[]> map : dataset.entrySet()){
				String[] vals = map.getValue();
				
				stmt.setString(1, vals[1].trim().toString()); //state values
				stmt.setString(2, vals[0].trim().toString()); //city values
				stmt.setString(3, String.valueOf(map.getKey())); //id
				
				stmt.addBatch();
			}
			int[] updateCount = stmt.executeBatch();
			U.log("Count of update record :"+updateCount.length);
			conn1.commit();
			U.log("Commit batch");
			conn1.setAutoCommit(true);
			stmt.close();			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		U.log("Swapping of city & state is done at table '"+tableName+"'....");
		try{
			if(!conn1.isClosed()){
				conn1.close();
				conn1 = null;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	
/*	void disconnect(){
		try{
			if(!conn.isClosed()){
				conn.close();
				conn = null;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}*/
}
