package com.priti.demo;


import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class VerifyPhoneFromDBUsingTheListOFAreaCode {
	static MultiMap<String, String[]> multiMap = new MultiValueMap<String, String[]>();
	static ArrayList<String[] > dbPhoneList = new ArrayList<>();
	Connection conn = null;
	static String myState = "Veracruz";
	public VerifyPhoneFromDBUsingTheListOFAreaCode() {
		conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, Path.TEQUILA_MAIN_DB_NAME);	
	}
	
	
	public static void main(String[] args) throws IOException, SQLException {
		VerifyPhoneFromDBUsingTheListOFAreaCode verify = new VerifyPhoneFromDBUsingTheListOFAreaCode();
		verify.getCityStateAreaCodeList(myState);
		U.log("Total Area Code Count "+myState+" : "+multiMap.size());
		verify.getRecordsFromDB();
		U.log("Total Area Code Count "+myState+" : "+dbPhoneList.size());
		verify.disconnect();
		
		
		//Verifying the phone fetch from db using the are code list of specific city and state
		if( dbPhoneList.size()>0){
			verify.verifyingPhone();
		}
		
	}//end main
	
	
	private void verifyingPhone() throws IOException {
		String [] header = {"ID","COMPANY_NAME","URL","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE"};
		CSVWriter writerValid= new CSVWriter(new FileWriter("/home/shatam-3/MexicoCache/DB_Valid_"+myState+"_PhoneList_22_09_2018.csv"));
		writerValid.writeNext(header);	
		
		CSVWriter writerInValid= new CSVWriter(new FileWriter("/home/shatam-3/MexicoCache/DB_InValid_"+myState+"_PhoneList_22_09_2018.csv"));
		writerInValid.writeNext(header);
		
		CSVWriter writerNotMatchRecord= new CSVWriter(new FileWriter("/home/shatam-3/MexicoCache/DB_NotMatchRecord_"+myState+"_PhoneList_22_09_2018.csv"));
		writerNotMatchRecord.writeNext(header);
		
		HashSet<String [] > validSet  = new HashSet<>();
		HashSet<String [] > invalidSet  = new HashSet<>();
		int validCount=0;
		int invalidCount=0;
		int matchCount = 0 ;
		int noMatchCount = 0 ;
		int count=0;
		 
		HashSet<String [] > cityNoMatchWithAreaCodeList = new HashSet<>();
		  for(String [] dbRow : dbPhoneList){
			 // if(!dbRow[0].contains("1464831"))continue;
			  String finalPhone = ""; 
			  dbRow[5] = dbRow[5].replace("é", "e").replace("á", "a").replace("ó", "o").replace("ú", "u").replace("ñ", "n").replace("í", "i").replace("Á", "A")
					  	.replaceAll(" Ext. \\d", "");
			  String dbRowCity = dbRow[5].trim().toLowerCase();//area code city
			  String dbRowPhone = dbRow[8].trim().toLowerCase();//db city
			  //System.out.println("dbRowCity : "+dbRowCity);		
			  if(!dbRowPhone.contains("-")){
				  dbRow[8] = dbRowPhone= U.formatNumbersAsCode(dbRowPhone);
			  }
			
			  //--Verify PostalCode---------
			  if(dbRow[7]==null)dbRow[7]="";
			  /*if(dbRow[7].length()!=0){
				  if((Integer.parseInt(dbRow[7]))>=MXStates.mexicoPostalCodeRange.get(myState)[1]  || (Integer.parseInt(dbRow[7]))<MXStates.mexicoPostalCodeRange.get(myState)[0] ){
						  System.out.println("Wrong Zip : "+dbRowCity+"\t"+dbRow[7]);
						  cityNoMatchWithAreaCodeList.add(dbRow);
						  noMatchCount++;
				  }
			  }*/
			  //----Match DB City-State with Wikipedia AreaCode city-state List------------
			  Collection<String[]> cityAreaCodeList = multiMap.get(dbRowCity);
			  if(cityAreaCodeList != null && !cityNoMatchWithAreaCodeList.contains(dbRow)){
				 for(String[] cac :cityAreaCodeList){
					 System.out.println(dbRow[0]+"\t,dbRowCity:"+dbRowCity+"\t,DBPhone:"+dbRowPhone+"\t,"+Arrays.toString(cac));		
					 String myAreaCode = cac[3].trim();
					 String checkAreaCode = "Uncheck";
					 if(dbRowPhone.contains(";")){
						 String[] eachDBPhone = dbRowPhone.split(";");
							for(String eachdbPh : eachDBPhone){
								eachdbPh = eachdbPh.replace("+", "");
								System.out.println("eachdbPh :"+eachdbPh+":");
								if((eachdbPh.matches("^\\d{4}-\\d{4}$") || eachdbPh.matches("^\\d{3}-\\d{4}$"))){ //for pattern "xxx-xxxx" and "xxxx-xxxx"
									if(finalPhone.length()==0)finalPhone = "Valid";
									else finalPhone += ";Valid";
								}
								else if((eachdbPh.matches("(\\d)*(-)*(01|1|52|052)*(800)-\\d{3}-\\d{4}"))){ //toll free no 
									if(finalPhone.length()==0)finalPhone = eachdbPh;
									else finalPhone += ";Valid"; //800-710-8888 [or] 01-800-710-6352
								}
								/*else if(myAreaCode.length()==2){
									
									if(eachdbPh.matches("^"+myAreaCode+"(\\d)*-\\d{3}-\\d{4}$")){ // 33x-xxx-xxxx
										if(finalPhone.length()==0)finalPhone = "Valid";
										else finalPhone += ";Valid";
									}
									else if(eachdbPh.matches("^\\d+-"+myAreaCode+"\\d-\\d{3}-\\d{4}$")){//01-areacode(2 digit)3-848-2003 [or] 1-areacode(2 digit)5-614-6478
										if(finalPhone.length()==0)finalPhone = "Valid";
										else finalPhone += ";Valid";
									}
									else if(eachdbPh.matches("^\\d+-\\d{3}-"+myAreaCode+"\\d-\\d{3}-\\d{4}$")){//0-052-areacode(2 digit)3-953-2026
										if(finalPhone.length()==0)finalPhone = "Valid";
										else finalPhone += ";= Valid";
									}
									else{
										if(finalPhone.length()==0)finalPhone = "Invalid";
										else finalPhone += ";Invalid";
									}
									
									
								}*/
								else if(myAreaCode.length()==3){
									
									if(eachdbPh.matches(myAreaCode+"-\\d{3}-\\d{4}$")){
										if(finalPhone.length()==0)finalPhone = "Valid";
										else finalPhone += ";Valid";
									}
									else if(eachdbPh.matches("^\\d+-"+myAreaCode+"-\\d{3}-\\d{4}$")){//01-areacode(3 digit)-848-2003 [or] 1-areacode(3 digit)-614-6478
										if(finalPhone.length()==0)finalPhone = "Valid";
										else finalPhone += ";Valid";
									}
									else if(eachdbPh.matches("^\\d+-\\d{3}-"+myAreaCode+"-\\d{3}-\\d{4}$")){
										if(finalPhone.length()==0)finalPhone = "Valid";
										else finalPhone += ";Valid";//0-052-areacode-953-2026
									}
									else{
										if(finalPhone.length()==0)finalPhone = "Invalid";
										else finalPhone += ";Invalid";
									}
								}
								else{
									if(finalPhone.length()==0)finalPhone = "Not Match";
									else finalPhone += ";Not Match";
								}
						}
							System.out.println("FinalPhone : "+finalPhone);
					 }
					 else{
						 dbRowPhone = dbRowPhone.replace("+", "");
						 if(dbRowPhone.matches("^\\d{4}-\\d{4}$")||dbRowPhone.matches("^\\d{3}-\\d{4}$"))
							 finalPhone = "Valid"; //without prefix and area code
						 else if((dbRowPhone.matches("(\\d)*(-)*(01|1|52|052)*(800)-\\d{3}-\\d{4}")))
							finalPhone  = "Valid";  //toll free no  //800-710-8888 [or] 01-800-710-6352 
						 /*else if(myAreaCode.length()==2){
							 	//areaCodeCheck="check";
							 	if(dbRowPhone.matches("^"+myAreaCode+"(\\d)*-\\d{3}-\\d{4}$"))finalPhone = "Valid";	 // 33x-xxx-xxxx	
							 	else if(dbRowPhone.matches("^\\d+-"+myAreaCode+"\\d-\\d{3}-\\d{4}$"))finalPhone = "Valid"; //01-areacode(2 digit)3-848-2003 [or] 1-areacode(2 digit)5-614-6478
								else if(dbRowPhone.matches("^\\d+-\\d{3}-"+myAreaCode+"\\d-\\d{3}-\\d{4}$"))finalPhone = "Valid";	//0-052-areacode(2 digit)3-953-2026	
								else  finalPhone = "Invalid";
							 	
							 	
							 	System.out.println(":::::::::::::"+finalPhone);
						 }*/
						 else if(myAreaCode.length()==3){
							// areaCodeCheck="check";
							 if(dbRowPhone.matches("^"+myAreaCode+"-\\d{3}-\\d{4}$"))finalPhone = dbRowPhone; // 333-xxx-xxxx
							 else if(dbRowPhone.matches("^\\d+-"+myAreaCode+"-\\d{3}-\\d{4}$"))finalPhone = dbRowPhone; //01-areacode(3 digit)-848-2003 [or] 1-areacode(3 digit)-614-6478
							 else if(dbRowPhone.matches("^\\d+-\\d{3}-"+myAreaCode+"-\\d{3}-\\d{4}$"))finalPhone = dbRowPhone;	//0-052-areacode-953-2026
							 else finalPhone = "Invalid";
							
							 
							 System.out.println("$$$$$$$$$"+finalPhone);
						 }
						 else{
							 finalPhone = "Not Match";
						 }
						// System.out.println("SingleFinalPhone : "+finalPhone);
					 }//end else
				 }//end of cityAreaCodeList
				 matchCount++;
				 //--------Adding values in list---------
				  if(!finalPhone.contains("Invalid") && !finalPhone.contains("Not Match") && finalPhone.length()!=0){
					  validSet.add(dbRow);
					  validCount++;
				  }
				  else{
					  invalidSet.add(dbRow);
					  invalidCount++;
				  }
			  }
			  else{
				  if(!cityNoMatchWithAreaCodeList.contains(dbRow)){
					  System.out.println("Not Match dbRowCity : "+dbRowCity);
					  cityNoMatchWithAreaCodeList.add(dbRow);
					  noMatchCount++;
				 }
			  }
			  count++; 
			 
		  }//end of db row loop
		  System.out.println("-----------Shatam_Records---------------------");
		  System.out.println(":::::::::::::::"+myState+":::::::::::::::::");
		  System.out.println("Total : "+count);
		  System.out.println("noMatchCount : "+noMatchCount);
		  System.out.println("matchCount : "+matchCount);
		  System.out.println("validCount : "+validCount);
		  System.out.println("invalidCount : "+invalidCount);
		  
		  writerValid.writeAll(validSet);
		  writerValid.close();
		  
		  writerInValid.writeAll(invalidSet); 
		  writerInValid.close();
		  
		  writerNotMatchRecord.writeAll(cityNoMatchWithAreaCodeList);
		  writerNotMatchRecord.close();
		
	}

	//-----------------
	private void getCityStateAreaCodeList(String state) throws IOException {
		if(state=="Mexico City")state="Federal District";
		
		List<String[]> data = U.readCsvFile("/home/shatam-3/MexicoCache/Merge_Wikipedia_&_Codigo_Postal_Mexico_All_State_City_AreaCode_18_09_2018.csv");
		for(String[] d : data){
			//System.out.println(Arrays.toString(d));
			if(d[2].contains(state))
			{
				d[1] = d[1].toLowerCase().trim();
				System.out.println(Arrays.toString(d));
				multiMap.put(d[1], d);
				//cityStateAreaCodeList.add(d);//Added item in list for a specific state
			}
		}
	}
	void disconnect(){
		try{
			if(!conn.isClosed()){
				conn.close();
				conn = null;
				System.out.println("Disconnect Successfully");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}	
	private void getRecordsFromDB() throws SQLException, IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("/home/shatam-3/MexicoCache/DB_"+myState+"_PhoneList_22_09_2018.csv"));
		String [] header = {"ID","COMPANY_NAME","URL","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE"};
		writer.writeNext(header);
		int count = 0 ;
		String query = "select id,company_name,url,address,neighborhood,city,state,zip,phone from dataset where state = \""+myState+"\" and phone is not null and city is not null";
		//String query = "select id,company_name,url,address,neighborhood,city,state,zip,phone from dataset where state = \""+myState+"\" and phone is not null and city is not null and _source_url is not null";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		System.out.println(rs);
		try{
			 while(rs.next()){
				 
				 String [] result = {rs.getString(1).trim(),rs.getString(2),rs.getString(3),rs.getString(4),rs.getString(5),rs.getString(6),rs.getString(7),rs.getString(8),rs.getString(9)};
				 dbPhoneList.add(result);
				 writer.writeNext(result);
				 count++;
			 }
			 System.out.println("Count of records of state "+myState+ " : " + count);
			 writer.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

}
