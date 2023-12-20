package com.shatam.test;

import java.io.FileReader;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Scanner;

import org.apache.commons.io.input.NullInputStream;
import org.supercsv.exception.NullInputException;

import com.opencsv.CSVReader;
import com.shatam.conversion.Sic;
import com.shatam.email.validations.MailHostsLookup;
import com.shatam.geoboundary.Boundary;
import com.shatam.url.validations.ServerLookUp;
import com.shatam.utils.Corrector;
import com.shatam.utils.SawanUtil;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;
import com.shatam.utils.Util;
import com.tequila.database.ScoreTequila;
import com.tequila.database.UpdateScore;


public class Test {

//	private static final String FILE_PATH = "/home/shatam-10/MexicoCache/Cache/Inegi_Information_4000 (charumam)_CORRECT_NW_REC.csv";
//	private static final String FILE_PATH = "/home/shatam-100/Desktop/MexicoCacheUniqueRecord/Inegi_Information_0_34781_15May_1_00000_10000_F_CORRECT_NW_REC.csv";
	private static final String FILE_PATH = "/home/shatam-100/MexicoCache/Cache/Inegi_Information_0_224000_190000_200000_F1_From_0-2300_CORRECT_NW_REC.csv";

	public static void main(String[] args) {
		
		//validURLs(10);//for update
		//validEmails(11);//for update
			
		//validSIC(1);//for updates
		
//		testCompanyNameStandardised();

//		U.log(TextFormat.getCompanyNameStandardised("GRUPO CUPRUM S.A.P.I. DE C.V"));
//		U.log(U.formatNumbersAsCode("818-851-0600;8-188-510-635-"));
		//testCSV();
		
		new Test().testCSV();//for insertion
//		test();
/*		String str = "Blvd. General Marcelino García Barragán No 2077";
		str = str.replace(Util.match(str, " No \\d+"), Util.match(str, " No \\d+").replace("No ", "No. "));
		U.log(str);*/
//		U.log(U.getTodayDate());
	
//		U.log(U.toTitleCase("Tanks - Metal, Fiber, Glass, Plastic, Etc (Whol)"));

//		U.log(Arrays.toString(U.extractSICInfo("5813")));
//		U.log(U.toTitleCase("J.o. Domínguez No. 140"));
//		U.log(U.toTitleCase("Estacion 420 Pizza&Drinks"));
//		U.log(U.isValidUrl("http://www.engramexcom.mx/"));
//		U.log(U.isValidUrl("www.azimatronics.com"));
/*		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		U.log(dateFormat.format(new Date()));
		U.log(Boundary.boundaryCheckForCityZipState(-99.16279,19.35077,19.3512653,-99.1616686,19.3444058,-99.16515749999999));
		
		U.log(U.formatNumbersAsCode("555700046"));
*/
/*		U.log(Boundary.boundaryCheckForCityZipState(22.893305,-109.906853,28.0000017,-109.413173,22.8719539,-115.2237643));
		
		
		U.log(Boundary.boundaryCheckForState("Baja California Sur", 22.893305,-109.906853,"Mexico"));
	*/	
		}

	private static void validSIC(int indexUrl) {
		
		validSICs(indexUrl, FILE_PATH);
	}
	/**
	 * This method is used to validate SIC_CODE.
	 * @param indexSic
	 * @param filePath
	 */
	private static void validSICs(int indexSic, String filePath) {
		List<String[]> readLines = U.readCsvFileWithoutHeader(filePath);
		Set<String> sicCodes = new HashSet<>();
		U.log("Total records ::"+readLines.size());
		U.log("*** Start validating SIC_CODE is here .......");
		int x = 1;
		int count = 0;
		for(String [] lines : readLines){
			x++;
			if(lines[indexSic].trim().isEmpty())continue;
			try {
				U.log("row : "+x+" ,SIC_CODE : "+lines[indexSic]);
				if(sicCodes.size() > 0){
					if(sicCodes.contains(lines[indexSic])){
						count++;
						continue;
					}
				}
				if(Sic.sicInfo(lines[indexSic])==null){
					System.err.println("InValid, SIC_CODE : "+lines[indexSic].trim().toLowerCase()+" at row :"+x);
					continue;
				}
				count++;
				sicCodes.add(lines[indexSic]);

			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		U.log("Total Valid SIC_CODE is "+count);
		U.log("Validate SIC_CODE is done.");
	}

	private static void validURLs(int indexUrl){
		validURLs(indexUrl, FILE_PATH);
	}
	/**
	 * This method is used to validate URL that are active or not.
	 * @param indexUrl
	 * @param filePath
	 */
	public static void validURLs(int indexUrl, String filePath){
		List<String[]> readLines = U.readCsvFileWithoutHeader(filePath);
		Set<String> searchableURLs = new HashSet<>();
		
		U.log("Total records ::"+readLines.size());
		U.log("*** Start validating URL is here .......");
		int x = 1;
		int count = 0;
		for(String [] lines : readLines){
			x++;
			if(lines[indexUrl].trim().isEmpty())continue;
			lines[indexUrl] = lines[indexUrl].trim().toLowerCase();
			try {
//				U.log("row : "+x+" ,url : "+lines[indexUrl]);
				if(searchableURLs.size() > 0){
					if(searchableURLs.contains(lines[indexUrl])){
						count++;
						continue;
					}
				}
				if(!ServerLookUp.isReachable(lines[indexUrl])){
					System.err.println("Url is not reachable, Url is "+lines[indexUrl].trim().toLowerCase()+" at row :"+x);
//					System.out.println("P"+x);
					continue;
				}else {
//					System.out.println(lines[indexUrl]);
				}
				count++;
				searchableURLs.add(lines[indexUrl]);

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		U.log("Total Valid URL is "+count);
		U.log("Validate URL's is done.");
	}
	
	private static void validEmails(int indexEmail){
		validEmails(indexEmail, FILE_PATH);
	}
	/**
	 * This method is used to validate email's by checking its domain is active or not.
	 * @param indexEmail
	 * @param filePath
	 */
	public static void validEmails(int indexEmail, String filePath){
		List<String[]> readLines = U.readCsvFileWithoutHeader(filePath);
		
		Set<String> searchableEmails = new HashSet<>();
		U.log("Total records ::"+readLines.size());
		U.log("*** Start validating Emails is here .......");
		int x = 1;
		for(String [] lines : readLines) {
			x++;
			if(lines[indexEmail] == null) throw new IllegalArgumentException("Email is null at row :"+x);
			
			if(lines[indexEmail].trim().isEmpty()) continue;
			
			lines[indexEmail] = lines[indexEmail].trim().toLowerCase();
			
			List<String> invalidEmail = new ArrayList<>();
			if(lines[indexEmail].contains(";")){
				System.out.println(lines[indexEmail]);
				
				String emails[] = lines[indexEmail].split(";");
				boolean validEmail [] = new boolean[emails.length];
				for(int i = 0; i < emails.length; i++){
					if(!emails[i].contains("@"))
						validEmail[i] = false;
					else{
						if(searchableEmails.size() > 0){
							if(searchableEmails.contains(emails[i]))continue;
						}
						String[] vals = emails[i].split("@");
						if(vals.length != 2)
							validEmail[i] = false;
						else if (vals.length == 2){
							validEmail[i] = MailHostsLookup.isEmailExist(vals[1].trim());
						}
						
						if(validEmail[i]) searchableEmails.add(emails[i]);
					}
				}//eof for
				for(int i = 0; i < validEmail.length; i++){
					if(validEmail[i] == false)
						invalidEmail.add(emails[i]);
				}
			}else{
				if(!lines[indexEmail].contains("@")){
					invalidEmail.add(lines[indexEmail]);
					continue;
				}
				if(searchableEmails.size() > 0){
					if(searchableEmails.contains(lines[indexEmail]))continue;
				}
				
				String[] vals = lines[indexEmail].split("@");
				if(vals.length != 2)
					invalidEmail.add(lines[indexEmail]);
				else if (vals.length == 2){
					if(!MailHostsLookup.isEmailExist(vals[1].trim())){
						invalidEmail.add(lines[indexEmail]);
					}else 
						searchableEmails.add(lines[indexEmail]);
				}
			}
			if(invalidEmail != null){
				if(!invalidEmail.isEmpty()){
					System.err.println("Invalid email at row :"+x+"\tInvalid emails are "+invalidEmail);
				}else {
//					System.out.println(0);
				}
			}else {
//				System.out.println(0);
			}
		}//eof for
		
		U.log("Validate Emails is done.");
	}
	
	public static void testCSV(){
		loadReadFile();

		U.log("*** To chech whether Sic sub field is empty");
		findMissingField(4, "SIC");
		
		U.log("*** To chech whether Company name field is empty");
		findMissingField(7,"Company name");
		
		U.log("*** To chech whether address field is empty");
		findMissingField(8,"Address");
		
		U.log("*** To chech whether City field is empty");
		findMissingField(10,"City");
		
		U.log("*** To chech whether State field is empty");
		findMissingField(11,"State");
		
		U.log("*** To find whether State field is in correct format");
		findStateInCorrectFormat(11);
		
		test();
		testCompanyNameStandardised();
/*		
		U.errLog("Find Unique Urls");
		findUniqueUrls(15);
		
		U.errLog("Find Unique Emails");
		findUniqueUrls(16);
*/	
		validURLs(15);
		validEmails(16);
		
		}
	
	private static void test(){
		HashMap<String,String> yearsInBizMap = Corrector.loadCorrectYearsInBizOnlyYear(FILE_PATH, 21);
		
//		HashMap<String,String> yearsInBizMap = loadCorrectYearsInBiz(21);
		
		HashMap<String,long[]> annualSalesMap = Corrector.loadAnnualSales(FILE_PATH, 19);
		
		HashMap<String,long[]> empCountMap = Corrector.loadEmpCount(FILE_PATH, 20);
		
		U.log("**** Year_In_Biz test here...");
		
		for(Entry<String, String> entry : yearsInBizMap.entrySet()){
			U.log(entry.getKey()+"\t"+entry.getValue());
		}
		
		U.log("*** Annual Sales test here...");
		for(Entry<String, long[]> entry : annualSalesMap.entrySet()){
			U.log(entry.getKey()+"\t"+Arrays.toString(entry.getValue()));
		}
		
		U.log("**** Emp Count test here...");
		for(Entry<String, long[]> entry : empCountMap.entrySet()){
			U.log(entry.getKey()+"\t"+Arrays.toString(entry.getValue()));
		}
	}
	
	private static void testCompanyNameStandardised(){
		HashMap<String, String> companyNameMap = Corrector.loadCompanyNameStandardised(FILE_PATH, 7); //for insertion
//		HashMap<String, String> companyNameMap = Corrector.loadCompanyNameStandardised(FILE_PATH, 2); //for Updation
		U.log("**** Company Name in Standardised Form here...");
		
		for(Entry<String, String> entry : companyNameMap.entrySet()){
			U.log(entry.getKey()+"\t\t=> "+entry.getValue());
		}
	}
	
/*	HashMap<String,String> loadCorrectYearsInBiz(int index){
		HashMap<String,String> map = new HashMap<>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
//			U.log(Arrays.toString(lines));
			if (x++ == 0) continue;
			if(!lines[index].trim().isEmpty() || lines[index].trim().length()>3)
				map.put(lines[index], Util.match(lines[index], "\\d{4}"));
			
		}
		U.log("Validate YearsInBiz..... Done");
		return map;
	}*/
	
	private static List<String[]>  readLines = null;

	private static void loadReadFile(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(FILE_PATH));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log("Load input file...... Done");
	}
	
	
	private static void findMissingField(int index, String txt){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
//			U.log(Arrays.toString(lines));
			if (x++ == 0) continue;		
			if(lines[index].isEmpty()){
				throw new NullInputException(txt+" Field is empty at row :"+x);
			}
		}
		U.log("===> Validate missing field at rows..... Done");
	}
	
	private static void findStateInCorrectFormat(int index){
		U.log("*** Start validating STATE is here .......");
		Set<String> stateSet = new HashSet<String>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {
			lines= it.next();
			if (x++ == 0) continue;
			stateSet.add(lines[index].trim());  //state
		}
		
		for(String state : stateSet){
			String correctFormat = U.matchState(state);
			if(correctFormat.equals("-")){
				throw new IllegalArgumentException("State name is not found for "+state);
			}
		}
		U.log("Done");
	}
	
	private static void findUniqueUrls(int index){
//		StringBuffer str = new StringBuffer();
		
		HashSet<String> urlSet = new HashSet<>();
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			
			if(!lines[index].isEmpty()){
				
				if(urlSet.add(lines[index])){
					U.log(lines[index] +"\t\t"+U.isValidUrl(lines[index]));
//					str.append(lines[index]).append("\t\t").append(U.isValidUrl(lines[index])).append("\n");
				}
			}	
		}
		U.log("Unique url's..... Done");
//		SawanUtil.toWrite(str.toString());
	}
	
	private static void findUniqueEmails(int index){
		HashSet<String> urlSet = new HashSet<>();
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			
			if(!lines[index].isEmpty()){
				
				if(urlSet.add(lines[index])){
					U.log(lines[index] +"\t\t"+U.isValidEmailAddress(lines[index]));
				}
			}	
		}
		U.log("Unique email's..... Done");
	}
	
}
