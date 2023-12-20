package com.chinmay.test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class FormatHerePlaceApiFile {
	static final String NOT_FOUND="NOT_FOUND";
	static final String VERIFIED="VERIFIED";
	static final String CLOSED="CLOSED";
//	static HashMap<String, Integer> COLUMNO=new HashMap<String,Integer>(){{ //For GOOGLE
//		put("COMPANY_NAME",10);
//		put("SIC_SUB",2);
//		put("ADDRESS",11);
//		put("NEIGHBORHOOD",12);
//		put("CITY",13);
//		put("STATE",14);
//		put("ZIP",15);
//		put("PHONE",16);
//		put("URL",17);
//		put("UPDATED_URL",17);
//	}
//	};
	static HashMap<String, Integer> COLUMNO=new HashMap<String,Integer>(){{     //For here
		put("COMPANY_NAME",9);
		put("SIC_SUB",10);
		put("ADDRESS",11);
		put("NEIGHBORHOOD",12);
		put("CITY",13);
		put("STATE",14);
		put("ZIP",15);
		put("PHONE",17);
		put("URL",18);
		put("UPDATED_URL",18);
	}
	};
//	static HashMap<String, Integer> COLUMNO=new HashMap<String,Integer>(){{     //For here
//		put("COMPANY_NAME",10);
//		put("SIC_SUB",18);
//		put("ADDRESS",11);
//		put("NEIGHBORHOOD",12);
//		put("CITY",13);
//		put("STATE",14);
//		put("ZIP",15);
//		put("PHONE",16);
//		put("URL",17);
//		put("UPDATED_URL",17);
//	}
//	};
	static HashMap<String, String> pStatus=new HashMap<String,String>(){{
		put("street chnge&wrong sic ph no change  update url","ADDRESS;;SIC_SUB;;PHONE;;UPDATED_URL");
		put("city change  update url","CITY;;UPDATED_URL");
		put("city shld be &wrong sic ph no change","CITY;;SIC_SUB;;PHONE");
		put("city should be  update url","CITY;;UPDATED_URL");
		put("street,city shld be","ADDRESS;;CITY");
		put("correct ph no change  url added","PHONE;;UPDATED_URL");
		put("city change","CITY");
		put("correct","_STATUS");
		put("street change&wrong sic ph no change","ADDRESS;;SIC_SUB;;PHONE");
		put("wrong record","_STATUS");
		put("website missing ph no change  update url","PHONE;;UPDATED_URL");
		put("street,zip,city change ph no change  update url","ADDRESS;;ZIP;;CITY;;PHONE;;UPDATED_URL");
		put("street chnge  update url","ADDRESS;;UPDATED_URL");
		put("company name change&wrong sic  update url","COMPANY_NAME;;SIC_SUB;;UPDATED_URL");
		put("street change&wrong sic ph no change  update url","ADDRESS;;SIC_SUB;;PHONE;;UPDATED_URL");
		put("change street ph no change","ADDRESS;;PHONE");
		put("change city format& wrong sic  update url","CITY;;SIC_SUB;;UPDATED_URL");
		put("city shld be ph no change","CITY;;PHONE");
		put("street,ph no change","ADDRESS;;PHONE");
		put("zip change to ph no change","ZIP;;PHONE");
		put("change zip,city ph no change","ZIP;;CITY;;PHONE");
		put("street change ph no change","ADDRESS;;PHONE");
		put("street,zip change","ADDRESS;;ZIP");
		put("street,company name chnge  update url","ADDRESS;;COMPANY_NAME;;UPDATED_URL");
		put("duplicate_1166091  update url","");
		put("street,city chnge ph no change  update url","ADDRESS;;CITY;;PHONE;;UPDATED_URL");
		put("zip change  update url","ZIP;;UPDATED_URL");
		put("duplicate_915339","");
		put("street,city,state,zip & update ph no. ph no change  update url","ADDRESS;;CITY;;STATE;;ZIP;PHONE;;UPDATED_URL");
		put("city change ph no change","CITY;;PHONE");
		put("street,zip change  update url","ADDRESS;;ZIP;;UPDATED_URL");
		put("city should be ph no change  update url","CITY;;PHONE;;UPDATED_URL");
		put("zip,street change ph no change","ZIP;;ADDRESS;;PHONE");
		put("change city","CITY");
		put("correct ph no change","PHONE");
		put("street,city,zip,state&update ph no. ph no change","ADDRESS;;CITY;;ZIP;;STATE;;PHONE");
		put("ph no change  update url","PHONE;;UPDATED_URL");
		put("city shld be & wrong sic ph no change","CITY;;SIC_SUB;;PHONE");
		put("street,zip chnge  ph no change  update url","ADDRESS;;ZIP;;PHONE;;UPDATED_URL");
		put("zip change ph no change  update url","ZIP;;PHONE;;UPDATED_URL");
		put("zip change","ZIP");
		put("wrong sic ph no change  update url","SIC_SUB;;UPDATED_URL");
		put("company name change&wrong sic ph no change","COMPANY_NAME;;SIC_SUB;;PHONE");
		put("update website  update url","UPDATED_URL");
		put("city shld be &wrong sic ph no change  update url","CITY;;SIC_SUB;;PHONE;;UPDATED_URL");
		put("closed","_STATUS");
		put("change street.city,zip ph no change","ADDRESS;;CITY;;ZIP;;PHONE");
		put("buisness name,ph no change  update url","COMPANY_NAME;;PHONE;;UPDATED_URL");
		put("change street,city,zip,state,phn no. ph no change  update url","ADDRESS;;CITY;;STATE;;PHONE;;UPDATED_URL");
		put("street,zip chnge   update url","ADDRESS;;ZIP;;UPDATED_URL");
		put("street chnge","ADDRESS");
		put("street shld be ph no change","ADDRESS;;PHONE");
		put("street chnge ph no change","ADDRESS;;PHONE");
		put("street,zip change ph no change","ADDRESS;;ZIP;;PHONE");
		put("company name,zip,street change. ph no change  update url","COMPANY_NAME;;ADDRESS;PHONE;;UPDATED_URL");
		put("wrong sic","SIC_SUB");
		put("sic wrong  update url","SIC_SUB;;UPDATED_URL");
		put("company name change ph no change  update url","COMPANY_NAME;;PHONE;;UPDATED_URL");
		put("buisness name change  update url","COMPANY_NAME;;UPDATED_URL");
		put("company name,street chnge","COMPANY_NAME;;ADDRESS");
		put("zip change&sic wrong","ZIP;;SIC_SUB");
		put("in codigo site city is&wrong sic","CITY;;SIC_SUB");
		put("city shld be ,street change, update ph no ph no change  update url","CITY;;ADDRESS;;PHONE;;UPDATED_URL");
		put("change city&wrong sic","CITY;;SIC_SUB");
		put("wrong sic ph no change","SIC_SUB;;PHONE");
		put("street,city,state,zip & update ph no. ph no change","ADDRESS;;CITY;;STATE;;ZIP;;PHONE");
		put("in codigo site city is ph no change","CITY;;PHONE");
		put("street,zip change ph no change  update url","ADDRESS;;ZIP;;UPDATED_URL");
		put("city should be","CITY");
		put("city shld be  update url","CITY;;UPDATED_URL");
		put("street,zip,city change ph no change","ADDRESS;;ZIP;;CITY;;PHONE");
		put("correct  update url","UPDATED_URL");
		put("street,zip,phn no ph no change  update url","ADDRESS;;ZIP;;PHONE;;UPDATED_URL");
		put("change street","ADDRESS");
		//put("duplicate","");
		put("zip,city,street change ph no change","ZIP;;CITY;;ADDRESS;;PHONE");
		put("city shld be  and street change & wrong sic ph no change  update url","CITY;;ADDRESS;;SIC_SUB;;PHONE;;UPDATED_URL");
		put("company name change ph no change","COMPANY_NAME;;PHONE");
		put("zip change&sic wrong ph no change","ZIP;;SIC_SUB;;PHONE");
		put("sic wrong ph no change  update url","SIC_SUB;;PHONE;;UPDATED_URL");
		put("change city to &wrong sic","CITY;;SIC_SUB");
		put("street,city chnge","ADDRESS;;CITY");
		put("zip,street change ph no change  update url","ZIP;;ADDRESS;;PHONE;;UPDATED_URL");
		put("city shld be &wrong sic","CITY;;SIC_SUB");
		put("street change ph no change  update url","ADDRESS;;PHONE;;UPDATED_URL");
		put("zip change ph no change","ZIP;;PHONE");
		put("street change  update url","ADDRESS;;UPDATED_URL");
		put("city shld be and street change, wrong sic ph no change","CITY;;ADDRESS;;SIC_SUB;;PHONE");
		put("duplicate_1357297 ph no change  update url","");
		put("change address to ph no change","ADDRESS;;PHONE");
		put("change city,zip,street ph no change  update url","CITY;;ZIP;;ADDRESS;;UPDATED_URL");
		put("company name,street change  update url","COMPANY_URL;;ADDRESS;;UPDATED_URL");
		put("wrong sic,ph no change","SIC_SUB;;PHONE");
		put("zip,street,city change ph no change  update url","ZIP;;CITY;;ADDRESS;;PHONE;;UPDATED_URL");
		put("zip,street change&wrong sic ph no change","ZIP;;ADDRESS;;SIC_SUB;;PHONE");
		put("correct ph no change  update url","PHONE;;UPDATED_URL");
		put("city shld be  and street change ph no change","CITY;;ADDRESS;;PHONE");
		put("city,ph no. change","CITY;;PHONE");
		put("change address to","ADDRESS");
		put("ph no change ph no change  update url","PHONE;;UPDATED_URL");
		put("wrong sic,ph no change   update url","SIC_SUB;;PHONE;;UPDATED_URL");
		put("change street&wrong sic","ADDRESS;;SIC_SUB");
		put("change street ph no change  update url","ADDRESS;;PHONE;;UPDATED_URL");
		put("wrong sic  update url","SIC_SUB;;UPDATED_URL");
		put("city,ph no. change  update url","CITY;;PHONE;;UPDATED_URL");
		put("city shld be ph no change  update url","CITY;;PHONE;;UPDATED_URL");
		put("change compny name, ph no change  update url","COMPANY_NAME;;PHONE;;UPDATED_URL");
		put("company name change  update url","COMPANY_NAME;;UPDATED_URL");
		put("change address to ph no change  update url","ADDRESS;;PHONE;;UPDATED_URL");
		put("wrong record ph no change  update url","_STATUS");
		put("city shld be","CITY");
		put("sic wrong correct ph no change  update url","SIC_SUB;;PHONE;;UPDATED_URL");
		put("zip,street change","ZIP;;ADDRESS");
		put("change street,city,state,zip,ph no.&wrong sic ph no change  update url","ADDRESS;;CITY;;STATE;;ZIP;;PHONE;;SIC_SUB;;UPDATED_URL");
		put("zip,phn no change","ZIP;;PHONE");
		put("zip,ph no change update url","ZIP;;PHONE;;UPDATED_URL");
		put("zip change and phn no update url","ZIP;;PHONE;;UPDATED_URL");
		put("zip change , ph no update url","ZIP;;PHONE;;UPDATED_URL");
		put("city change update url","CITY;;UPDATED_URL");
		put("street,city,zip&update ph no. update url","ADDRESS;;CITY;;ZIP;;PHONE;;UPDATED_URL");
		put("street change&update no","ADDRESS;;PHONE");
		put("ph no change&wrong sic","PHONE;;SIC_SUB");
		put("change street&wrong sic update url","ADDRESS;;SIC_SUB;;UPDATED_URL");
		put("street ,ph no,zip change&wrong sic","ADDRESS;;PHONE;;ZIP;;SIC_SUB");
		put("duplicate_658467","");
		put("street ,ph no,url change update url","ADDRESS;;PHONE;;UPDATED_URL");
		put("street,zip,city change","ADDRESS;;ZIP;;CITY");
		put("zip change &change ph&wrong sic update url","ZIP;;PHONE;;SIC_SUB;;UPDATED_URL");
		put("street,zip,city,ph no change","ADDRESS;;ZIP;;CITY;;PHONE");
		put("street change&wrong sic","ADDRESS;;SIC_SUB");
		put("zip  &city change&wrong sic","ZIP;;CITY;;SIC_SUB");
		put("change zip update url","ZIP;;UPDATED_URL");
		put("street,city,zip,ph no. change","ADDRESS;;CITY;;ZIP;;PHONE");
		put("street,zip,phn no change","ADDRESS;;ZIP;;PHONE");
		put("ph no,city,zip change","PHONE;;CITY;;ZIP");
		put("compny,street,zip,city,ph no change update url","COMPANY_NAME;;ADDRESS;;ZIP;;CITY;;PHONE;;UPDATED_URL");
		put("street,zip,company mane change&wrong sic update url","ADDRESS;;ZIP;;COMPANY_NAME;;SIC_SUB;;UPDATED_URL");
		put("street,city,zip change","ADDRESS;;CITY;;ZIP");
		put("ph no change&wrong sic update url","PHONE;;SIC_SUB;;UPDATED_URL");
		put("change street,city,zip update url fax 52 55 5089 9311","ADDRESS;;CITY;;ZIP;;UPDATED_URL");
		put("street,ph no change&sic wrong update url","ADDRESS;;PHONE;;SIC_SUB;;UPDATED_URL");
		put("street,city,ph no,zip change update url","ADDRESS;;PHONE;;ZIP;;UPDATED_URL");
		put("zip change &wrong sic update url","ZIP;;SIC_SUB;;UPDATED_URL");
		put("street,zip,city change update url","ADDRESS;;ZIP;;CITY;;UPDATED_URL");
		put("street,zip,city change&wrong sic update url","ADDRESS;;ZIP;;CITY;;SIC_SUB;;UPDATED_URL");
		put("ph no change","PHONE");
		put("ph no,zip change","PHONE;;ZIP");
		put("zip change &wrong sic","ZIP;;SIC_SUB");
		put("ph no change and city change update url","PHONE;;CITY;;UPDATED_URL");
		put("street change","ADDRESS");
		put("street,ph no. change","ADDRESS;;PHONE");
		put("street,zip change update url","ADDRESS;;ZIP;;UPDATED_URL");
		put("street,zip,city,ph no change update url","ADDRESS;;ZIP;;CITY;;PHONE;;UPDATED_URL");
		put("street change update url","ADDRESS;;UPDATED_URL");
		put("zip change and update ph","ZIP;;PHONE");
		put("zip,city,ph no change update url","ZIP;;PHONE;;CITY;;UPDATED_URL");
		put("ph no,zip change&wrong sic","PHONE;;ZIP;;SIC_SUB");
		put("zip,ph no. change","ZIP;;PHONE");
		put("ph no change update url","PHONE;;UPDATED_URL");
		put("ph no,zip change update url","PHONE;;ZIP;;UPDATED_URL");
		put("correct update url","UPDATED_URL");
		put("zip change update url","ZIP;;UPDATED_URL");
		put("street,city,state,zip","ADDRESS;;CITY;;STATE;;ZIP");
		put("zip change &city","ZIP;;CITY");
		put("change zip,ph no&wrong sic update url","ZIP;;PHONE;;SIC_SUB;;UPDATED_URL");
		put("ph no,zip,city change","PHONE;;ZIP;;CITY");
		put("sic wrong update url","SIC_SUB;;UPDATED_URL");
		put("street,zip,city,cmp nm change update url","ADDRESS;;ZIP;;CITY;;COMPANY_NAME;;UPDATED_URL");
		put("change ph no. update url","PHONE;;UPDATED_URL");
		put("street,zip,city,state&ph no change update url","ADDRESS;;CITY;;ZIP;;STATE;;PHONE;;UPDATED_URL");
		put("city,zip change","CITY;;ZIP");
		put("change zip&ph no","ZIP;;PHONE");
		put("city,zip change update url","CITY;;ZIP;;UPDATED_URL");
		put("sic wrong","SIC_SUB");
		put("change company name update url","COMPANY_NAME;;UPDATED_URL");
		put("change zip","ZIP");
		put("city change,update ph no.","CITY;;PHONE");
		put("change zip &wrong sic update url","ZIP;;SIC_SUB;;UPDATED_URL");
		put("zip &ph no change&wrong sic update url","ZIP;;PHONE;;SIC_SUB;;UPDATED_URL");
		put("zip change&update no&wrong sic update url","ZIP;;PHONE;;SIC_SUB;;UPDATED_URL");
		put("city,zip,ph no change","CITY;;ZIP;;PHONE");
		put("street,ph no change update url","ADDRESS;;PHONE;;UPDATED_URL");
		put("duplicate_508647","");
		put("wrong sic update url","SIC_SUB;;UPDATED_URL");
	}};
	static HashMap<String, String> status=new HashMap<String,String>(){{
		put("address, zip, phone","ADDRESS;;ZIP;;PHONE");
		put("data","");
		put("company name, address, url","COMPANY_NAME;;ADDRESS;;UPDATED_URL");
		put("company name, address","COMPANY_NAME;;ADDRESS");
		put("address,city zip","ADDRESS;;CITY;;ZIP");
		put("change address, city, zip, phone","ADDRESS;;CITY;;ZIP;;PHONE");
		put("street address","ADDRESS");
		put("address, city, phone no","ADDRESS;;CITY,PHONE");
		put("city, phone no","CITY,PHONE");
		put("wrong city","");
		put("company name, phone no, address, zip","COMPANY_NAME;;ADDRESS;PHONE;;ZIP");
		put("duplicate data","DUPLICATE");
		put("change address, phone","ADDRESS;;PHONE");
		put("change address, zip, phone","ADDRESS;;ZIP;;PHONE");
		put("zip, company name","ZIP;;COMPANY_NAME");
		put("address,city, zip","ADDRESS;;CITY;;ZIP");
		put("address,city, zip, url","ADDRESS;;CITY;;ZIP;;UPDATED_URL");
		put("change address, zip, phone,url","ADDRESS;;PHONE;;ZIP;;UPDATED_URL");
		put("url","URL");
		put("address, phone no, url","ADDRESS;;PHONE;;UPDATED_URL");
		put("address, phone no","ADDRESS;;PHONE");
		put("address, zip","ADDRESS;;ZIP");
		put("address","ADDRESS");
		put("address,url","ADDRESS;;UPDATED_URL");
		put("company name, address, zip","COMPANY_NAME;;ADDRESS;;ZIP");
		put("company name, address, zip, url","COMPANY_NAME;;ADDRESS;;ZIP;;UPDATED_URL");
		put("company name, address, phone no, url","COMPANY_NAME;;ADDRESS;;PHONE;;UPDATED_URL");
		put("city, zip","CITY;;ZIP");
		put("phone no , address","PHONE;;ADDRESS");
		put("company name, phone no","COMPANY_NAME;;PHONE");
		put("company name, phone no, url","COMPANY_NAME;;PHONE;;UPDATED_URL");
		put("address,neighbourhood,city,state,zip,phone","ADDRESS;;CITY;;STATE;;ZIP;;PHONE;;NEIGHBORHOOD");
		////
		put("phone no, url","PHONE;;UPDATED_URL");
		put("phone,url","PHONE;;UPDATED_URL");
		put("phone no","PHONE");
		put("zip,phone","ZIP;;PHONE");
		put("address and latlon","ADDRESS");
		put("address and zip","ADDRESS;;ZIP");
		put("correct","_STATUS");
		put("company name,zip,address","COMPANY_NAME;;ZIP;;ADDRESS");
		put("address latlon","ADDRESS");
		put("company name,zip","COMPANY_NAME;;ZIP");
		put("company name, url","COMPANY_NAME;;UPDATED_URL");
		put("company name","COMPANY_NAME");
		put("company name, zip,phone","COMPANY_NAME;;ZIP;;PHONE");
		put("company name, zip","COMPANY_NAME;;ZIP");
		put("latlon and phone","PHONE");
		//put("latlon","");
		put("wrong sic_sub","SIC_SUB");
		put("comapny name address","COMPANY_NAME;;ADDRESS");
		put("zip and latlon","ZIP");
		put("company name, phone","COMPANY_NAME,PHONE");
		put("zip and latlon and change phone no.","ZIP;;PHONE");
		put("zip","ZIP");
		put("company name,phone","COMPANY_NAME;;PHONE");
		put("company name,wrong sic 6511","COMPANY_NAME;;SIC_SUB");
		put("company name,address","COMPANY_NAME;;ADDRESS");
		put("address,city,zip","ADDRESS;;CITY;;ZIP");
		put("udpate company name,phone,city,zip","COMPANY_NAME;;PHONE;;ZIP;;CITY");
		put("latlon and change phone no.","PHONE");
		put("address and phone no","ADDRESS;;PHONE");
		put("address,phone","ADDRESS;;PHONE");
		put("company name,zip, phone","COMPANY_NAME;;ZIP;;PHONE");
		put("udpate zip","ZIP");
		put("phone no, zip, address,company name","COMPANY_NAME;;ZIP;;ADDRESS;;PHONE");
		put("phone","PHONE");
		put("udpate address,phone","ADDRESS;;PHONE");
		put("zip and phone","ZIP;;PHONE");
		put("address,latlon","ADDRESS");
		put("company name,phone,address","COMPANY_NAME;;PHONE;;ADDRESS");
		put("address,zip","ADDRESS;;ZIP");
		put("company name,phone no","COMPANY_NAME;;PHONE");
		put("city","CITY");
		put("phone no.","PHONE");
		put("udpate phone","PHONE");
		put("wrong data","_STATUS");
		put("Wrong data","_STATUS");
		put("udpate company name","COMPANY_NAME");
		put("address zip phone","ADDRESS;;ZIP;;PHONE");
		put("address, url","ADDRESS;;UPDATED_URL");
		put("zip,phoneno","ZIP,PHONE");
		put("companyname,phone no","COMPANY_NAME;;PHONE");
		put("address,zip& phone","ADDRESS;;ZIP;;PHONE");
		put("company name,address,zip","COMPANY_NAME;;ADDRESS;;ZIP");
		put("udpate address","ADDRESS");
		put("phone no, zip","PHONE;;ZIP");
		put("company name,zip,phone","COMPANY_NAME;;ZIP;;PHONE");
		put("company name, sic_sub","COMPANY_NAME;;SIC_SUB");
		put("closed","_STATUS");
		put("wrong db url","UPDATED_URL");
		put("sic_sub,address,neighboorhood,city,state,zip,phone","SIC_SUB;;ADDRESS;;NEIGHBORHOOD;;CITY;;STATE;;ZIP;;PHONE");//
		put("address,neighboorhood,city,state,zip,phone,url","ADDRESS;;NEIGHBORHOOD;;CITY;;STATE;;ZIP;;PHONE;;URL");
		put("sic_sub,company name,zip,address,url,phone","SIC_SUB;;COMPANY_NAME;;ZIP;;ADDRESS;;PHONE;;URL");
		//sic_sub,company name,zip,address,url,phone
		put("sic_sub,address,zip, phone","ADDRESS;;SIC_SUB;;ZIP;;PHONE");
		put("Closed","_STATUS");
		put("sic_sub,company name,zip,address,url","SIC_SUB;;ADDRESS;;ZIP;;COMPANY_NAME;;URL");//
		//put("","_STATUS");
		
		put("company namephone","COMPANY_NAME;;PHONE");
		put("phone,address,neighboorhood","PHONE;;ADDRESS;;NEIGHBORHOOD");
		put("address.neighborhood,zip,phone","ADDRESS;;NEIGHBORHOOD;;ZIP;;PHONE");
		put("phone,neighborhood,zip","PHONE;;NEIGHBORHOOD;;ZIP");
		//put("found on tripadvisor","_STATUS");
		put("address,neighborhood,zip","ADDRESS;;NEIGHBORHOOD;ZIP");
		put("address,neighborhood,phone","ADDRESS;;NEIGHBORHOOD;;PHONE");
		put("address,neighborhood,city,zip","ADDRESS;;NEIGHBORHOOD;;CITY;;ZIP");
		put("address, phone","ADDRESS;;PHONE");
		put("company name,address,neighborhood,phone","COMPANY_NAME;;ADDRESS;;NEIGHBORHOOD;;PHONE");
		put("company name,address,phone","COMPANY_NAME;;ADDRESS;;PHONE");
		put("address, zip,phone","ADDRESS;;ZIP;;PHONE");
		put("address,city,zip,phone","ADDRESS;;CITY;;ZIP;;PHONE");
		put("address,zip,phone","ADDRESS;;ZIP;;PHONE");
		put("address,neighborhood,city,zip,phone","ADDRESS;;NEIGHBORHOOD;;CITY;;ZIP;;PHONE");
		put("address,city,phone","ADDRESS;;CITY;;PHONE");
		put("company name,neighborhood,address,zip,phone","COMPANY_NAME;;NEIGHBORHOOD;;ADDRESS;;ZIP;;PHONE");
		put("address,neighborhood,zip,phone","ADDRESS;;NEIGHBORHOOD;;ZIP;;PHONE");
		put("phone,zip,neighboorhood","PHONE;;ZIP;;NEIGHBORHOOD");
		put("not found","_STATUS");
		put("address,neighborhood","ADDRESS;;NEIGHBORHOOD");
		put("address,city","ADDRESS;;CITY");
		put("city,phone","CITY;;PHONE");
	}
	};
	
	static HashSet<String> comment=new HashSet<String>();
	static String header[]= {"ID","SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY"
			,"STATE","ZIP","PHONE","FAX","URL","UPDATED_URL","_STATUS"};
	public static void main(String[] args) {
		startProcessForHereFile();
		//startProcessForGoogleFile();
		//startProcessForGoogleFileP();
	}
	private static void startProcessForGoogleFile() {
		try {
			String fileName="/home/mypremserver/DatabasesTequila/CompletedDatabaseFile/Company_2_googleMatched_URL.csv";
//			List<String[]> inputfile=U.readCsvFileWithoutHeader(fileName);
//			for (String[] inputRow : inputfile) {	
//				//U.log(inputRow[inputRow.length-1]);
//				comment.add(inputRow[inputRow.length-1].replace("Update ", "").toLowerCase());
//				//break;
//			}
//			for (String s : comment) {
//				if (status.get(s)==null) {
//					U.log(s);
//				}
//				//U.log("------"+s);
//			}
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(header);
			List<String[]> inputfile=U.readCsvFileWithoutHeader(fileName);
			for (String[] inputRow : inputfile) {
				String out[]=new String[header.length];
				out[0]=inputRow[0];
				//if (!out[0].equals("104447"))continue;
				//U.log(inputRow[inputRow.length-2]);
				String status=inputRow[inputRow.length-1].toLowerCase();
				if (!(Util.match(status, "\\d{5,}")!=null||status.contains("found"))) {
					for (String key : FormatHerePlaceApiFile.status.keySet()) {
						if (status.contains(key)) {
							U.log(key);
							if (FormatHerePlaceApiFile.status.get(key).contains(";;")) {
								String updateCols[]=FormatHerePlaceApiFile.status.get(key).split(";;");
								
								for (String updateCol : updateCols) {
									if (updateCol.equals("_STATUS")) {
										U.log(updateCol);
										if (inputRow[inputRow.length-1].toLowerCase().contains("wrong data")) {
											out[header.length-1]=NOT_FOUND;
										}
									}else {
										for (int i = 0; i < header.length; i++) {
											if (header[i].equals(updateCol)) {
												//U.log(updateCol);
												//U.log(COLUMNO.get(updateCol));
//												if (updateCol.equals("SIC_SUB")) {
//													String sic=Util.match(inputRow[1], "\\d{4}");
//													out[i]=sic;
//												}else
												{
													U.log(updateCol);
													out[i]=inputRow[COLUMNO.get(updateCol)];
												}
											}
										}
									}
									//U.log(updateCol);
								}
								if (FormatHerePlaceApiFile.status.get(key).equals("_STATUS")) {
									if (inputRow[21].toLowerCase().contains("wrong data")) {
										out[header.length-1]=NOT_FOUND;
									}else if (inputRow[21].toLowerCase().contains("correct")) {
										out[header.length-1]=VERIFIED;
									}else if (inputRow[21].trim().length()==0) {
										out[header.length-1]=VERIFIED;
									}else {
										out[header.length-1]=inputRow[21].trim();
									}
									
								}
							} else {
								for (int i = 0; i < header.length; i++) {
									if (header[i].equals(FormatHerePlaceApiFile.status.get(key))) {
									//	U.log(FormatHerePlaceApiFile.status.get(key));
										//U.log(COLUMNO.get(FormatHerePlaceApiFile.status.get(key)));
										if (FormatHerePlaceApiFile.status.get(key).equals("SIC_SUB")) {
											String sic=Util.match(inputRow[21], "\\d{4}");
											out[i]=sic;
										}else if (FormatHerePlaceApiFile.status.get(key).equals("_STATUS")) {
											U.log(inputRow[21]);
												if (inputRow[inputRow.length-1].toLowerCase().contains("wrong data")) {
													out[header.length-1]=NOT_FOUND;
												}
										}else {
											//U.log(key);
											//U.log(FormatHerePlaceApiFile.status.get(key));
											out[i]=inputRow[COLUMNO.get(FormatHerePlaceApiFile.status.get(key))];
										}
									}
								}
							}
							//break;
						}
					}
					
				}else if (Util.match(inputRow[21], "\\d{5,}")!=null) {
					//U.log(inputRow[21]);
					//Thread.sleep(50000);
					out[header.length-1]="Possible Duplicate of "+inputRow[21];
					out[header.length-1]=out[header.length-1].replace("Possible Duplicate of Possible Duplicate of ", "Possible Duplicate of ");
				}
				if (inputRow[21].trim().length()==0) {
					out[header.length-1]=VERIFIED;
				}
				if (inputRow[21].toLowerCase().contains("wrong db url")) {
					out[11]="DELETE_URL";
				}
				if (inputRow[21].toLowerCase().contains("closed")) {
					out[header.length-1]=CLOSED;
				}
				writer.writeNext(out);
				//U.log(Arrays.toString(out));
				//break;
			}
			FileUtil.writeAllText(fileName.replace("/CompletedDatabaseFile", "").replace(".csv", "_FOR_UPDATE.csv"), sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void startProcessForGoogleFileP() {
		try {
			String fileName="/home/mypremserver/DatabasesTequila/CompletedDatabaseFile/PramodSir/353_onwards_.csv";
//			List<String[]> inputfile=U.readCsvFileWithoutHeader(fileName);
//			for (String[] inputRow : inputfile) {	
//				//U.log(inputRow[inputRow.length-1]);
//				comment.add((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).replace("Update ", "").trim().toLowerCase());
//				//break;
//			}
//			for (String s : comment) {
//				if (pStatus.get(s)==null) {
//					U.log(s);
//				}
//				//U.log("------"+s);
//			}
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(header);
			List<String[]> inputfile=U.readCsvFileWithoutHeader(fileName);
			for (String[] inputRow : inputfile) {
				String out[]=new String[header.length];
				out[0]=inputRow[0];
				//if (!out[0].equals("1267062"))continue;
				//U.log(inputRow[inputRow.length-2]);
				String status=(inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).replace("Update ", "").trim().toLowerCase();
				U.log(status);
				if (!(Util.match(status, "\\d{5,}")!=null||status.contains("found"))) {
					for (String key : FormatHerePlaceApiFile.pStatus.keySet()) {
						//U.log(key);
						if (status.contains(key)) {
							//U.log(key);
							if (FormatHerePlaceApiFile.pStatus.get(key).contains(";;")) {
								String updateCols[]=FormatHerePlaceApiFile.pStatus.get(key).split(";;");
								
								for (String updateCol : updateCols) {
									if (updateCol.equals("_STATUS")) {
										//U.log(updateCol);
										if ((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).replace("Update ", "").trim().toLowerCase().contains("wrong data")) {
											out[header.length-1]=NOT_FOUND;
										}
									}else {
										for (int i = 0; i < header.length; i++) {
											if (header[i].equals(updateCol)) {
												//U.log(updateCol);
												//U.log(COLUMNO.get(updateCol));
//												if (updateCol.equals("SIC_SUB")) {
//													String sic=Util.match(inputRow[1], "\\d{4}");
//													out[i]=sic;
//												}else
												{
													U.log(updateCol);
													out[i]=inputRow[COLUMNO.get(updateCol)];
												}
											}
										}
									}
									//U.log(updateCol);
								}
								if (FormatHerePlaceApiFile.pStatus.get(key).equals("_STATUS")) {
									if ((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).replace("Update ", "").trim().toLowerCase().contains("wrong data")) {
										out[header.length-1]=NOT_FOUND;
									}else if ((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).replace("Update ", "").trim().toLowerCase().contains("correct")) {
										out[header.length-1]=VERIFIED;
									}else if ((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).trim().length()==0||(inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).trim().equals("correct")) {
										U.log(inputRow[21]);
										out[header.length-1]=VERIFIED;
									}else {
										U.log(inputRow[21]);
										out[header.length-1]=inputRow[21].trim();
									}
									
								}
							} else {
								for (int i = 0; i < header.length; i++) {
									if (header[i].equals(FormatHerePlaceApiFile.pStatus.get(key))) {
									U.log(FormatHerePlaceApiFile.pStatus.get(key));
										//U.log(COLUMNO.get(FormatHerePlaceApiFile.status.get(key)));
										if (FormatHerePlaceApiFile.pStatus.get(key).equals("SIC_SUB")) {
											String sic=Util.match(inputRow[18], "\\d{4}");
											U.log(inputRow[18]);
											out[i]=sic;
										}else if (FormatHerePlaceApiFile.pStatus.get(key).equals("_STATUS")) {
											U.log(inputRow[21]);
												if ((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).toLowerCase().contains("wrong data")||(inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).toLowerCase().equals("wrong record")) {
													out[header.length-1]=NOT_FOUND;
												}
										}else {
											//U.log(key);
											//U.log(FormatHerePlaceApiFile.status.get(key));
											out[i]=inputRow[COLUMNO.get(FormatHerePlaceApiFile.pStatus.get(key))];
										}
									}
								}
							}
							//break;
						}
					}
					
				}else if (Util.match((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]), "\\d{5,}")!=null) {
					//U.log(inputRow[21]);
					//Thread.sleep(50000);
					out[header.length-1]="Possible_Duplicate_of "+(inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).toLowerCase().replace("duplicate_", "");
					out[header.length-1]=out[header.length-1].replace("Possible_Duplicate_of Possible Duplicate of ", "Possible_Duplicate_of");
				}
				if ((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).trim().length()==0) {
					out[header.length-1]=VERIFIED;
				}
				if ((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).toLowerCase().contains("wrong db url")) {
					out[11]="DELETE_URL";
				}
				if ((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).toLowerCase().contains("closed")) {
					out[header.length-1]=CLOSED;
				}
				if ((inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).trim().length()==0||(inputRow[inputRow.length-3]+" "+inputRow[inputRow.length-2]+" "+inputRow[inputRow.length-1]).trim().toLowerCase().equals("correct")) {
					U.log(inputRow[21]);
					out[header.length-1]=VERIFIED;
				}
				//U.log(status);
				if (status.toLowerCase().contains("wrong data")||status.toLowerCase().equals("wrong record")) {
					out[header.length-1]=NOT_FOUND;
				}
				writer.writeNext(out);
				U.log(Arrays.toString(out));
				//break;
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_FOR_UPDATE.csv"), sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static void startProcessForHereFile() {
		try {
			String fileName="/home/mypremserver/DatabasesTequila/CompletedDatabaseFile/HEREPlaceApi_CSV5812_7011_300-400.csv";
//			List<String[]> inputfile=U.readCsvFileWithoutHeader(fileName);
//			for (String[] inputRow : inputfile) {
////				/U.log(inputRow[inputRow.length-2]);
//				comment.add(inputRow[inputRow.length-1].replaceAll("update |Update ", "").toLowerCase());
//				//break;
//			}
//			for (String s : comment) {
//				if (status.get(s)==null) {
//					U.log(s);
//				}
//				//U.log("------"+s);
//			}
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(header);
			List<String[]> inputfile=U.readCsvFileWithoutHeader(fileName);
			for (String[] inputRow : inputfile) {
				String out[]=new String[header.length];
				out[0]=inputRow[0];
				//if (!out[0].equals("1495604"))continue;
				//U.log(inputRow[inputRow.length-1]);
				String status=inputRow[inputRow.length-1].toLowerCase();
				U.log(status);
				if (!(Util.match(status, "\\d{5,}")!=null||status.contains("found"))||status.contains("not found")) {
					for (String key : FormatHerePlaceApiFile.status.keySet()) {
					//	U.log(key);
						if (status.contains(key)) {
							String updateCols[]=FormatHerePlaceApiFile.status.get(key).split(";;");
							for (String updateCol : updateCols) {
								if (updateCol.equals("_STATUS")) {
									U.log(inputRow[23]);
									if (inputRow[23].toLowerCase().contains("wrong data")||inputRow[23].toLowerCase().contains("not found")) {
										out[header.length-1]=NOT_FOUND;
									}else if (inputRow[23].toLowerCase().contains("correct")) {
										out[header.length-1]=VERIFIED;
									}else if (inputRow[23].trim().length()==0) {
										out[header.length-1]=VERIFIED;
									}else {
										out[header.length-1]=inputRow[23].trim();
									}
									
								}else {
									for (int i = 0; i < header.length; i++) {
										if (header[i].equals(updateCol)) {
											U.log(updateCol);
											//U.log(COLUMNO.get(updateCol));
//											if (updateCol.equals("SIC_SUB")) {
//												String sic=Util.match(inputRow[22], "\\d{4}");
//												out[i]=sic;
//											}else
											{
												out[i]=inputRow[COLUMNO.get(updateCol)];
											}
										}
									}
								}
								U.log(updateCol);
							}
							//break;
						}
					}
					
				}else if (Util.match(inputRow[23], "\\d{5,}")!=null) {
					//U.log(inputRow[23]);
					//Thread.sleep(50000);
					out[header.length-1]="Possible_Duplicate_of "+inputRow[23];
					out[header.length-1]=out[header.length-1].replace("Possible_Duplicate_of Possible Duplicate of ", "Possible_Duplicate_of ");
				}
				if (inputRow[23].trim().length()==0) {
					out[header.length-1]=VERIFIED;
				}
				if (inputRow[23].toLowerCase().contains("closed")) {
					out[header.length-1]=CLOSED;
				}
				if (inputRow[18]!=null&&inputRow[18].trim().length()!=0&&out[header.length-1]!=NOT_FOUND&&out[header.length-1]!=CLOSED) {
					out[10]=inputRow[18];
					out[11]=inputRow[18];
				}
				writer.writeNext(out);
				U.log(Arrays.toString(out));
				//break;
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_FOR_UPDATE.csv"), sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
