package com.tequila.database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.email.validations.MailHostsLookup;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.Corrector;
import com.shatam.utils.Path;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;

public class InsertNewRecordAtDB {
	
	private enum InsertType{
		DMP_NEWLY_RECORD,
		EXTRACTED_NEWLY_RECORD,
		NONE
	}
	
/*	private static final String insertQuery = "INSERT INTO dataset(INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"
			+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,LONGITUDE,LATITUDE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_LAST_UPDATED) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
*/
	private static final String insertQuery = "INSERT INTO dataset(INDUSTRY_SECTOR,SPANISH_INDUSTRY_SECTOR,SIC_MAJOR,SIC_SUB,PRODUCT_DESC,SPANISH_PRODUCT_DESC,COMPANY_NAME,ADDRESS,NEIGHBORHOOD,CITY,STATE,ZIP,PHONE,"
			+ "FAX,URL,EMAIL,CONTACT_PERSON,TITLE,ANNUAL_SALES_VOL_MIN,ANNUAL_SALES_VOL_MAX,EMP_COUNT_MIN,EMP_COUNT_MAX,YEARS_IN_BIZ,HOURS_OF_OPERATION,LONGITUDE,LATITUDE,CREATED_DATE,UPDATED_DATE,DELETED_DATE,_SCORE,_SOURCE_URL,_STATUS,_LAST_UPDATED_BY,_DELETED) "
			+ "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static String[] shortNames = { "ID","INDUSTRY_SECTOR", "SPANISH_INDUSTRY_SECTOR", "SIC_MAJOR", "SIC_SUB", "PRODUCT_DESC", "SPANISH_PRODUCT_DESC", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD","CITY", "STATE", "ZIP",
			"PHONE", "FAX", "URL", "EMAIL", "CONTACT_PERSON", "TITLE", "ANNUAL_SALES_VOL", "EMP_COUNT", "YEARS_IN_BIZ", "LONGITUDE", "LATITUDE" };

	private static final String COMPOSITE_KEY_TEQUILA_SER = "/home/shatam-100/CODE_Repository/Maxico/tequila.db_latest/uniqueKeyTequila.ser";
	
	private static final String MAIN_DB_NAME = "tequila.db";
	
//	private static final String INPUT_FILE_PATH = "/home/shatam-10/MexicoCache/Cache/All_Unique_Record_16_01_2023_Part_1.csv";
	private static final String INPUT_FILE_PATH = "/home/shatam-100/Desktop/MexicoCacheUniqueRecord/All_Unique_Record_0_224000_190000_200000_F1_From_0-2300.csv";
//	private static final String INPUT_FILE_PATH = "/home/shatam-100/Desktop/MexicoCacheUniqueRecord/All_Unique_Record_15_05_2023_N_0_22010_1_2_3_Final_00001-15000_Part_1_DDDD.csv";
	
	Connection conn = null;
	
	private InsertNewRecordAtDB(){																																	
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, MAIN_DB_NAME);
	}
	
	public static void main(String[] args) {
		InsertNewRecordAtDB process = new InsertNewRecordAtDB();
		
		
//		process.loadData(INPUT_FILE_PATH, InsertType.DMP_NEWLY_RECORD);
		process.loadData(INPUT_FILE_PATH, InsertType.EXTRACTED_NEWLY_RECORD);
//		process.loadData(INPUT_FILE_PATH, InsertType.NONE);
		process.updateIdAtValidateTable();
		process.disconnect();
	}
	
	@SuppressWarnings("deprecation")
	private final void loadData(String inputFilePath, InsertType type){
		List<String[]> dataset = null;
		try(CSVReader reader = new CSVReader(new FileReader(INPUT_FILE_PATH),',','"',1);){
			dataset = reader.readAll();						
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		U.log("Loading..... composite key here...");
		CreateSerializofCompositeKey chk = new CreateSerializofCompositeKey();

		/*
		 * load composite key from serialize file
		 */
		HashSet<String> uniqueKeyHashSet =chk.deserializeUniqueKey(COMPOSITE_KEY_TEQUILA_SER);
		U.log("Composite key set size:::"+uniqueKeyHashSet.size());
		
/*		for(String key : uniqueKeyHashSet){
			U.log(">>"+key);
		}*/
		
		if(dataset.size() > 0){
			switch (type) {
				case DMP_NEWLY_RECORD:
					U.log("Inserting newly records provided by DMP");
					insertNewRecordAtDbByDMP(dataset, uniqueKeyHashSet);
					break;

				case EXTRACTED_NEWLY_RECORD:
					U.log("Inserting newly extracted records from csv");
					insertNewRecordAtDB(dataset, uniqueKeyHashSet);
					break;

				default:
					U.errLog("Nothing happened here....");
					break;
			}			
		}
	}
	
	private final void insertNewRecordAtDB(List<String[]> dataset, HashSet<String> uniqueKeyHashSet){
		
		HashMap<String,long[]> annualSalesMap = Corrector.loadAnnualSales(INPUT_FILE_PATH,19);
		HashMap<String,long[]> empCountMap = Corrector.loadEmpCount(INPUT_FILE_PATH,20);
		HashMap<String,String> correctDateMap = Corrector.loadCorrectYearsInBizOnlyYear(INPUT_FILE_PATH,21);
		HashMap<String,String> companyNameMap = Corrector.loadCompanyNameStandardised(INPUT_FILE_PATH, 7);
		
		CSVWriter writer = null;
		
		int catchCount = 0;
		int dupCount = 0;
		U.log("Start inserting record here.........");
		PreparedStatement pstmt = null;		
		String[] nextLine = null;
		int updateCount[];
		int i = 0;
		
		try {
			
			writer = new CSVWriter(new FileWriter(getWrongCompositeFile(INPUT_FILE_PATH)));
			
			pstmt = conn.prepareStatement(insertQuery);
			
			conn.setAutoCommit(false);
			Iterator<String[]> itr = dataset.iterator();
			while (itr.hasNext()) {
				nextLine = itr.next();
				
				/*
				 * uniqueKey = SIC_SUB + COMPANY_NAME + ADDRESS + CITY + STATE + CONTACT_PERSON
				 */
/*				if (nextLine[17].trim().length()!=0 && nextLine[17].trim().endsWith(";")) {
					nextLine[17]=nextLine[17].toLowerCase().substring(0, nextLine[17].trim().length()-1); //CONTACT_PERSON
				}*/
				
				if(nextLine[4].trim().length() == 3){
					nextLine[4] = "0"+nextLine[4].trim();
				}
//				U.log(nextLine[7]);
				String companyName = companyNameMap.get(nextLine[7]); 

				if(companyName == null)throw new IllegalArgumentException("Company Name is never be null. Input company name is "+nextLine[7]);

//				companyName = TranslateEnglish.convertToEnglish(companyName);
				
//				String address = TranslateEnglish.convertToEnglish(nextLine[8]);
				String address = nextLine[8].trim();
				
				String uniqueKey = U.toTitleCase(nextLine[4].trim())+ U.toTitleCase(companyName.trim()) + U.toTitleCase(address) + U.toTitleCase(nextLine[10].trim()) + U.toTitleCase(nextLine[11].trim())
						+ U.toTitleCase(nextLine[17].trim());
				
				if (!uniqueKeyHashSet.add(uniqueKey.toLowerCase())) {
//					U.log(uniqueKey.toLowerCase());
					dupCount++;
					writer.writeNext(nextLine);
					continue;
				}
//				U.log(uniqueKey.toLowerCase());

				
				pstmt.setString(1, U.toTitleCase(nextLine[1])); //INDUSTRY_SECTOR
				pstmt.setString(2, U.toTitleCase(nextLine[2])); //SPANISH_INDUSTRY_SECTOR
				pstmt.setString(3, U.toTitleCase(nextLine[3])); //SIC_MAJOR
				pstmt.setString(4, U.toTitleCase(nextLine[4])); //SIC_SUB
				pstmt.setString(5, U.toTitleCase(nextLine[5])); //PRODUCT_DESC
				pstmt.setString(6, U.toTitleCase(nextLine[6])); //SPANISH_PRODUCT_DESC
//				pstmt.setString(7, U.toTitleCase(nextLine[7])); //COMPANY_NAME
				pstmt.setString(7, companyName); //COMPANY_NAME
				
//				pstmt.setString(8, U.toTitleCase(TranslateEnglish.convertToEnglish(nextLine[8]))); //ADDRESS
				pstmt.setString(8, U.toTitleCase(address)); //ADDRESS
				
				String neighbour = U.toTitleCase(nextLine[9]);
				if(neighbour.trim().isEmpty())neighbour = null;
				pstmt.setString(9, neighbour); //NEIGHBORHOOD
				
				pstmt.setString(10, U.toTitleCase(nextLine[10]).replace(" - ", "-")); //CITY
				
				pstmt.setString(11, U.toTitleCase(nextLine[11])); //STATE
				
				String zip = nextLine[12].trim();
				if(zip.length() == 4){
					zip = "0"+zip;
				}
				if(zip.trim().isEmpty()) zip = null;
				pstmt.setString(12, zip); //ZIP
				
				String phoneNum = U.formatNumbersAsCode(nextLine[13].trim().replace("/", ";"));
				if(phoneNum.isEmpty())phoneNum = null;
				pstmt.setString(13, phoneNum); //PHONE
				
				String fax = U.formatNumbersAsCode(nextLine[14].trim().replace("/", ";"));
				if(fax.isEmpty())fax = null;
				pstmt.setString(14, fax); //FAX
				
				String url = nextLine[15].toLowerCase().trim();
				if(url.isEmpty())url = null;
				pstmt.setString(15, url); //URL //web url no need to format
				
/*				if (nextLine[16].trim().length()!=0 && nextLine[16].endsWith(";")) {
					nextLine[16]=nextLine[16].toLowerCase().substring(0, nextLine[16].length()-1);  //EMAIL
				}
*/				
				String email = U.formatEmail(nextLine[16].toLowerCase());
				if(email.trim().isEmpty())email = null;	
				pstmt.setString(16, email); //EMAIL //Email no need to format
				
/*				if (nextLine[17].trim().length()!=0 && nextLine[17].endsWith(";")) {
					nextLine[17]=nextLine[17].toLowerCase().substring(0, nextLine[17].length()-1); //CONTACT_PERSON
				}
*/				
				String contactPerson = U.toTitleCase(nextLine[17]);
				if(contactPerson.trim().isEmpty())contactPerson = null;
				pstmt.setString(17, contactPerson); //CONTACT_PERSON
				
/*				if (nextLine[18].trim().length()!=0 && nextLine[18].endsWith(";")) {
					nextLine[18]=nextLine[18].toLowerCase().substring(0, nextLine[18].length()-1); //TITLE
				}
*/				String title = U.toTitleCase(nextLine[18]);
				if(title.trim().isEmpty())title = null;
				pstmt.setString(18, title); //TITLE
				
				//ANNUAL_SALES_VOL
				String annualSalesVol = nextLine[19].trim(); //ANNUAL_SALES_VOL
				
				long[] salesVol= annualSalesMap.get(annualSalesVol);
				String minAnnualSales = null;
				String maxAnnualSales = null;

				if(salesVol != null){
					if(salesVol[0] != 0){
						minAnnualSales = String.valueOf(salesVol[0]);
					}else{
						minAnnualSales = null;
					}
					
					if(salesVol[1] != 0){
						maxAnnualSales = String.valueOf(salesVol[1]);
					}else{
						maxAnnualSales = null;
					}
					if(minAnnualSales == null && maxAnnualSales != null){
						minAnnualSales = maxAnnualSales;
						maxAnnualSales = null;
					}
				}else{
					minAnnualSales = null;
					maxAnnualSales = null;
				}
				
				pstmt.setString(19, minAnnualSales); //ANNUAL_SALES_VOL_MIN
				pstmt.setString(20, maxAnnualSales); ////ANNUAL_SALES_VOL_MAX
								
				//EMP_COUNT
				String empCount = nextLine[20].trim();//EMP_COUNT
				long[] empCountVal = empCountMap.get(empCount);
				String minEmpCount = null;
				String maxEmpCount = null;

				if(empCountVal != null){
					if(empCountVal[0] != 0){
						minEmpCount = String.valueOf(empCountVal[0]);
					}else{
						minEmpCount = null;
					}
					
					if(empCountVal[1] != 0){
						maxEmpCount = String.valueOf(empCountVal[1]);
					}else{
						maxEmpCount = null;
					}
					if(minEmpCount == null && maxEmpCount != null){
						minEmpCount = maxEmpCount;
						maxEmpCount = null;
					}
				}else{
					minEmpCount = null;
					maxEmpCount = null;
				}
				pstmt.setString(21, minEmpCount); //EMP_COUNT_MIN
				pstmt.setString(22, maxEmpCount); ////EMP_COUNT_MAX
				
				
				//YEARS_IN_BIZ
				String yearsInBiz = nextLine[21].trim(); //YEARS_IN_BIZ
				String correctYearsInBiz = null;
				if(!yearsInBiz.isEmpty()){
					correctYearsInBiz = correctDateMap.get(yearsInBiz);					
				}else{
					correctYearsInBiz = null;
				}
				pstmt.setString(23, correctYearsInBiz); //YEARS_IN_BIZ
//				String longitude = (); //LONGITUDE
				pstmt.setString(24, nextLine[22]);
				/**
				 * note ::
				 * nextLine[22] = lat at csv 
				 * nextLine[23] = long at csv 
				 * 
				 */
				
				String longitude = U.formatLongitude(nextLine[24]); //LONGITUDE
				if(longitude.isEmpty()) longitude = null;
				pstmt.setString(25, longitude); //LONGITUDE
				
				String latitude = nextLine[23].trim(); //LATITUDE
				if(latitude.isEmpty()) latitude = null;
				pstmt.setString(26, latitude); //LATITUDE
				
				pstmt.setString(27, getTodayDate());   //CREATED_DATE
				
				pstmt.setString(28, null);   //UPDATED_DATE
				pstmt.setString(29, null);   //DELETED_DATE
				
				pstmt.setInt(30, calculateScore(shortNames,nextLine));  //SCORE
//				U.log(nextLine[0]);
				String sourceUrl = nextLine[25].toLowerCase().trim();
				if(sourceUrl.isEmpty())sourceUrl = null;
				pstmt.setString(31, sourceUrl);   //_SOURCE_URL
			
				pstmt.setString(32, null);   //_STATUS
				pstmt.setString(33, null);   //_LAST_UPDATED_BY
				pstmt.setString(34, null);   //_DELETED
				
				pstmt.addBatch();

				if ((++i % 10000) == 0) {

					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");
					break;
				}

			}// end while

			
			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
			conn.commit();
			System.out.println("Commit the batch");
			conn.setAutoCommit(true);
			System.out.println("Insertion done....");

			U.log("Send to write composite key here......"+uniqueKeyHashSet.size());
			updateCompositeSer(uniqueKeyHashSet);
			
			U.log("Catch Count:::"+catchCount);
			U.log("Duplicate ::"+dupCount);
			
		}catch(SQLException | IOException e){
			e.printStackTrace();
		}
		try {
			writer.flush();
			writer.close();
			pstmt.close();
		}catch(SQLException | IOException e){
			e.printStackTrace();
		}
	}
	;
	private final void insertNewRecordAtDbByDMP(List<String[]> dataset, HashSet<String> uniqueKeyHashSet){
	
		
		CSVWriter writer = null;
		
		U.log("Start inserting record here.........");
		PreparedStatement pstmt = null;		
		String[] nextLine = null;
		int updateCount[];
		int i = 0;
		try {
			
			writer = new CSVWriter(new FileWriter(getWrongCompositeFile(INPUT_FILE_PATH)));
			
			pstmt = conn.prepareStatement(insertQuery);
		
			conn.setAutoCommit(false);
			Iterator<String[]> itr = dataset.iterator();
			
			while (itr.hasNext()) {
				nextLine = itr.next();
				
				/*
				 * uniqueKey = SIC_SUB + COMPANY_NAME + ADDRESS + CITY + STATE + CONTACT_PERSON
				 */
				
				if (nextLine[17].trim().length()!=0 && nextLine[17].endsWith(";")) {
					nextLine[17]=nextLine[17].toLowerCase().substring(0, nextLine[17].length()-1); //CONTACT_PERSON
				}
				
				if(nextLine[4].trim().length() == 3){
					nextLine[4] = "0"+nextLine[4].trim();
				}
				
				String companyName = TextFormat.getCompanyNameStandardised(nextLine[7]);
				
//				companyName = TranslateEnglish.convertToEnglish(companyName);
				
//				String address = TranslateEnglish.convertToEnglish(nextLine[8]);
				
				String address = nextLine[8].trim();
				
				String uniqueKey = U.toTitleCase(nextLine[4].trim())+ U.toTitleCase(companyName.trim()) + U.toTitleCase(address) + U.toTitleCase(nextLine[10].trim()) + U.toTitleCase(nextLine[11].trim())
						+ U.toTitleCase(nextLine[17].trim());
				
				
				if (!uniqueKeyHashSet.add(uniqueKey.toLowerCase())) {
					writer.writeNext(nextLine);
					continue;
				}
				
				pstmt.setString(1, U.toTitleCase(nextLine[1])); //INDUSTRY_SECTOR
				pstmt.setString(2, U.toTitleCase(nextLine[2])); //SPANISH_INDUSTRY_SECTOR
				pstmt.setString(3, U.toTitleCase(nextLine[3])); //SIC_MAJOR
				pstmt.setString(4, U.toTitleCase(nextLine[4])); //SIC_SUB
				pstmt.setString(5, U.toTitleCase(nextLine[5])); //PRODUCT_DESC
				pstmt.setString(6, U.toTitleCase(nextLine[6])); //SPANISH_PRODUCT_DESC
//				pstmt.setString(7, U.toTitleCase(nextLine[7])); //COMPANY_NAME
				pstmt.setString(7, U.toTitleCase(companyName)); //COMPANY_NAME
				
//				pstmt.setString(8, U.toTitleCase(nextLine[8])); //ADDRESS
				pstmt.setString(8, U.toTitleCase(address)); //ADDRESS
				
				String neighbour = U.toTitleCase(nextLine[9]);
				if(neighbour.trim().isEmpty())neighbour = null;
				pstmt.setString(9, neighbour); //NEIGHBORHOOD
				
				pstmt.setString(10, U.toTitleCase(nextLine[10]).replace(" - ", "-")); //CITY
				
				pstmt.setString(11, U.toTitleCase(nextLine[11])); //STATE
				
				String zip = nextLine[12].trim();
				if(zip.length() == 4){
					zip = "0"+zip;
				}
				if(zip.trim().isEmpty()) zip = null;
				pstmt.setString(12, zip); //ZIP
				
				String phoneNum = U.formatNumbersAsCode(nextLine[13].trim().replace("/", ";"));
				if(phoneNum.isEmpty())phoneNum = null;
				pstmt.setString(13, phoneNum); //PHONE
				
				String fax = U.formatNumbersAsCode(nextLine[14].trim().replace("/", ";"));
				if(fax.isEmpty())fax = null;
				pstmt.setString(14, fax); //FAX
				
				String url = nextLine[15].toLowerCase().trim();
				if(url.isEmpty())url = null;
				pstmt.setString(15, url); //URL //web url no need to format

				String email = U.formatEmail(nextLine[16].toLowerCase());
				if(email.trim().isEmpty())email = null;				
				pstmt.setString(16, email); //EMAIL //Email no need to format

				String contactPerson = U.toTitleCase(nextLine[17]);
				if(contactPerson.trim().isEmpty())contactPerson = null;
				pstmt.setString(17, contactPerson); //CONTACT_PERSON
				
				String title = U.toTitleCase(nextLine[18]);
				if(title.trim().isEmpty())title = null;
				pstmt.setString(18, title); //TITLE
											
				String minAnnualSales = nextLine[19].trim(); //ANNUAL_SALES_VOL_MIN
				String maxAnnualSales = nextLine[20].trim(); //ANNUAL_SALES_VOL_MAX

				if(minAnnualSales.isEmpty()){
					minAnnualSales = null;
				}else{
					minAnnualSales = minAnnualSales.trim();
				}
				if(maxAnnualSales.isEmpty()){
					maxAnnualSales = null;
				}else{
					maxAnnualSales = maxAnnualSales.trim();
				}
				if(minAnnualSales == null && maxAnnualSales != null){
					minAnnualSales = maxAnnualSales;
					maxAnnualSales = null;
				}
				
				pstmt.setString(19, minAnnualSales); //ANNUAL_SALES_VOL_MIN
				pstmt.setString(20, maxAnnualSales); ////ANNUAL_SALES_VOL_MAX
								

				String minEmpCount = nextLine[21].trim();//EMP_COUNT_MIN
				String maxEmpCount = nextLine[22].trim();//EMP_COUNT_MAX

				if(minEmpCount.isEmpty()){
					minEmpCount = null;
				}else{
					minEmpCount = minEmpCount.trim();
				}
				if(maxEmpCount.isEmpty()){
					maxEmpCount = null;
				}else{
					maxEmpCount = maxEmpCount.trim();
				}
				if(minEmpCount == null && maxEmpCount != null){
					minEmpCount = maxEmpCount;
					maxEmpCount = null;
				}
				pstmt.setString(21, minEmpCount); //EMP_COUNT_MIN
				pstmt.setString(22, maxEmpCount); ////EMP_COUNT_MAX
				
				/**
				 * Note ::
				 * Before inserting years_in_big, correct it into proper format.
				 */
				//YEARS_IN_BIZ
				String correctYearsInBiz = nextLine[23].trim(); //YEARS_IN_BIZ
				if(!correctYearsInBiz.isEmpty()){
					correctYearsInBiz = correctYearsInBiz.trim();					
				}else{
					correctYearsInBiz = null;
				}
				pstmt.setString(23, correctYearsInBiz); //YEARS_IN_BIZ
				

				String longitude = U.formatLongitude(nextLine[24]); //LONGITUDE
				if(longitude.isEmpty()) longitude = null;
				pstmt.setString(24, longitude); //LONGITUDE
				
				String latitude = nextLine[25].trim(); //LATITUDE
				if(latitude.isEmpty()) latitude = null;
				pstmt.setString(25, latitude); //LATITUDE
				
				pstmt.setString(26, getTodayDate());   //CREATED_DATE
				
				pstmt.setString(27, null);   //UPDATED_DATE
				pstmt.setString(28, null);   //DELETED_DATE
				
				pstmt.setInt(29, calculateScoreForMinMax(shortNames,nextLine));  //SCORE
				
				pstmt.setString(30, null);   //_SOURCE_URL
			
				pstmt.setString(31, "DMP");   //_STATUS
				pstmt.setString(32, null);   //_LAST_UPDATED_BY
				pstmt.setString(33, null);   //_DELETED
								
				pstmt.addBatch();

				if ((++i % 10000) == 0) {

					updateCount = pstmt.executeBatch();
					System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
					conn.commit();
					System.out.println("Commit the batch");

				}
			}// end while

			updateCount = pstmt.executeBatch();
			System.out.println("Number of rows inserted: "+ updateCount.length + "\t" + i);
			conn.commit();
			System.out.println("Commit the batch");
			conn.setAutoCommit(true);
			System.out.println("Insertion done....");

			U.log("Send to write composite key here......");
			updateCompositeSer(uniqueKeyHashSet);
			
		}catch(SQLException | IOException e){
			e.printStackTrace();
		}
		try {
			writer.flush();
			writer.close();
			pstmt.close();
		}catch(SQLException | IOException e){
			e.printStackTrace();
		}/*finally {
			if (conn != null) {
				try {
					conn.close(); // <-- This is important
				} catch (SQLException e) {
				 handle exception 
				}
			}
		}*/
	}
	
	
	private void updateIdAtValidateTable(){
		try{
			Statement stmt = conn.createStatement();
			int count = stmt.executeUpdate("INSERT INTO dataV (ID) SELECT ID FROM dataset WHERE NOT EXISTS(SELECT ID FROM dataV WHERE dataV.ID = dataset.ID) AND dataset.CREATED_DATE is NOT null");
			U.log("Total updated count :"+count);
			stmt.close();
			U.log("New ID is added to dataV table.");
		}catch(SQLException e){
			U.log("New ID is not added to dataV table.");
			e.printStackTrace();
		}
	}
	
	private static String getWrongCompositeFile(String inputFilePath) {
		File file = new File(inputFilePath);
		if(file.isFile()){
			String ext = file.getName().substring(file.getName().lastIndexOf("."));
			return inputFilePath.replace(file.getName(), file.getName().replace(ext, "_WRONG_COMPOSITE_DATA"+ext));
		}
		return null;
	}

	public static void updateCompositeSer(HashSet<String> uniqueKeyHashSet){
		try {
			U.log("Start creating new composite key ser file here...");
	        FileOutputStream fileOut = new FileOutputStream(COMPOSITE_KEY_TEQUILA_SER);
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(uniqueKeyHashSet);
	        out.close();
	        fileOut.close();
	     } catch (IOException ex) {
	        ex.printStackTrace();
	     }
		U.log("Done composite key ser file.");
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

	private HashMap<String,Integer> scoreMap = new HashMap<String,Integer>() {
		{
			put("ID", 100);
			put("OLD_ID", 0);
			put("INDUSTRY_SECTOR", 0);
			put("SPANISH_INDUSTRY_SECTOR", 0);
			put("SIC_MAJOR", 0);
			put("SIC_SUB", 100);
			put("PRODUCT_DESC", 0);
			put("SPANISH_PRODUCT_DESC", 0);
			put("COMPANY_NAME", 100);
			put("ADDRESS", 100);
			put("NEIGHBORHOOD", 50);
			put("CITY", 100);
			put("STATE", 100);
			put("ZIP", 100);
			put("PHONE", 50);
			put("FAX", 50);
			put("URL", 25);
			put("EMAIL", 50);
			put("CONTACT_PERSON", 10);
			put("TITLE", 10);
			put("ANNUAL_SALES_VOL", 5);
			put("EMP_COUNT", 5);
			put("YEARS_IN_BIZ", 5);
			put("LONGITUDE", 50);
			put("LATITUDE", 50);
		}
	};
	
	DecimalFormat df = new DecimalFormat("#.0000");
	public final int calculateScore(final String[] shortNames, final String[] vals) {  
		//int score = 0;
		int score = 100;  //Score of generated ID is always 100, In vals array we don't have ID
		
		for (int i = 1; i < shortNames.length; i++){
			if (U.isEmpty(vals[i]))    
				continue;  
			
			String shortName = shortNames[i];
			
			if (U.isEmpty(shortName))
				continue;
			
			score += scoreMap.get(shortName);  
		}//for i
		
	 	int avgScore = (100 * score) / 1060;
	 	
		return avgScore;
	}
	
	public final int calculateScoreForMinMax(final String[] shortNames, final String[] vals) {  
		//int score = 0;
		int score = 100;  //Score of generated ID is always 100, In vals array we don't have ID
		String val = null;
		for (int i = 1; i < shortNames.length; i++){
			if(i < 20){
				val = vals[i];
			}			
			if(i == 20){
				val = vals[i+1]; //
			}
			if(i >= 21){
				val = vals[i+2];
			}
			
			if (U.isEmpty(val))    
				continue;  
			
			String shortName = shortNames[i];
			
			if (U.isEmpty(shortName))
				continue;
			
			score += scoreMap.get(shortName);  
		}//for i
		
	 	int avgScore = (100 * score) / 1060;
	 	
		return avgScore;
	}
	
	//Date date = new Date();
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final String getTodayDate(){
		return dateFormat.format(new Date());
	}
	
}
