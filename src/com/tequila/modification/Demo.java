package com.tequila.modification;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
//import java.text.DateFormat;
import java.text.ParseException;
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
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Set;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.scrapper.FindStateFromZip;
import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;

import me.xdrop.fuzzywuzzy.Applicable;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

import com.shatam.utils.DateFormat;

public class Demo {				
	
		public static void main(String[] args) throws GeneralSecurityException, IOException, ParseException {
/*
			String str = "pramod";
			char [] ch = str.toCharArray();
			
			if((ch[0] >= 65 && ch[0] <=90) || (ch[0] >= 97 && ch[0] <=122)){
				U.log("Character");
			}else{
				U.log("Not");
			}
			String [] array = {" String", "Tom Selleck "," Fish "};
			for(String a : array)
				U.log(">"+a);
			array = StringUtils.stripAll(array);
			for(String a : array)
				U.log(">"+a);

			*/
//			U.log(U.formatNumbersAsCode("011 52 222 122 2300"));
			
//			U.log(FindStateFromZip.findStateFromZip("1900"));
			
			
		//	U.log(U.matches("130 el dalo", "^(?!\\d+)[\\s\\w\\W]+"));
/*			List<String[]> list = new ArrayList<>();
			list.add(new String[]{"1","Sawan","Nagpur"});
			list.add(new String[]{"2","Meshram","Nagpur"});
			String header [] = {"ID","Name","City","Age"};
			try(CSVWriter writer = new CSVWriter(new FileWriter("/home/glady/demoFile.csv"),',');){
				writer.writeNext(header);
				writer.writeAll(list);
			}catch(IOException e){
				e.printStackTrace();
			}
			U.log("Done");
*/
			
/*			HashMap<String, String[]> sicMap = U.deserializedSicData("/home/glady/MexicoCache/sicCodeCanadaData.ser");
			
			String[] data = sicMap.get("0112");
			List<String> list = new ArrayList<>(Arrays.asList(data));
			list.remove(2);
			list.remove(6);
			U.log("list::"+list);
			U.log(Arrays.toString(data));
			U.log("length:::"+data.length);
			U.log("English Ind desc ::"+ data[0]);
			U.log("Spanish Ind desc ::"+data[1]);
			U.log("Major Sic :"+data[3]);
			U.log("Sub Sic :"+data[4]);
			U.log("Prod Desc Eng ::"+data[5]);
			U.log("Prod Desc Spanish ::"+data[6]);
			
			
			U.log(U.matches("Humberto Lobo, 451, Local 1", "[\\w+]{2,}\\s?,\\s{1,2}?\\d+\\s?,"));
			
			U.log(U.matchState("Jalisco"));
			U.log(U.findStateFromZip("00000"));
	*/		
			
			//("2014-01-01");
			//U.log(date);
			//U.log("Util date:"+df.format(date));
			
//			loadFile();
//			printDateFromDB();
//			loadDateFile();
//			loadUniqueDateFile();
			
//			loadAnnualSales();
			//loadEmpCount();
/*			
			Date date = new Date();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			U.log(df.format(date));
*/
			String data[] = U.extractSICInfo("3211");  //4725 //8741 //5092
			U.log(Arrays.toString(data));
			

/*			U.log(U.formatNumbersAsCode("016677157950/016677157960".replace("/", ";")));
			U.log(U.formatNumbersAsCode("1-784-842-0636"));
*/
/*			String fileName = getWrongCompositeFile("/home/glady/MexicoCache/Tequila_DATA/files/Tested/Ofertia-Sports_1_Correct.csv");
			U.log(fileName);
*/			
//			compareSICMap();
			
			U.log(U.formatNumbersAsCode("+52 [55] 5550 7922"));
			boolean email = isValidEmailAddress("M.CEDE.@HOTMAIL.COM");
			U.log(email);
			
			email = isValidEmailAddress1("email@123.123.123.123");
			U.log(email);
			
			U.log(U.matchState("Jalisco"));
			
//			readPDF();
//			readNaiscToSicPdf();
//			readPDFFromPageFixDigit();
        	
			
			
			List<ExtractedResult> result = FuzzySearch.extractSorted("Metallic Ore Mining", Arrays.asList("Ferroalloy Ores, Except Vanadium","Metal Ores, Nec"));
			U.log(result);
/*			for(ExtractedResult res : result){
				U.log(res.getString()+"\t"+res.getScore()+"%");
			}
*/
			new Applicable(){
				@Override
				public int apply(String s1, String s2) {
					return FuzzySearch.ratio(s1, s2);
				}
			};
			List<ExtractedResult> result1 = FuzzySearch.extractTop("Metallic Ore Mining", Arrays.asList("Ferroalloy Ores, Except Vanadium","Metal Ores, Nec"),3);
			U.log(result1);
			
			String s1 = "Leon";
			String s2 = "León de los Aldama";

			U.log("Ratio ::"+FuzzySearch.ratio(s1, s2));
			U.log("Partial Ratio ::"+FuzzySearch.partialRatio(s1, s2));
			U.log("Token Set Ratio ::"+FuzzySearch.tokenSetRatio(s1, s2));
			U.log("Token Sort Ratio ::"+FuzzySearch.tokenSortRatio(s1, s2));
			U.log("Token Sort partial Ratio ::"+FuzzySearch.tokenSortPartialRatio(s1, s2));
			U.log("Weighted Ratio ::"+FuzzySearch.weightedRatio(s1, s2));
			
/*			U.log("Max::"+Collections.max(Arrays.asList(86,86,85)));
			Integer[] nums =  new Integer[] {1, 1, 2, 3, 3, 3};
			List<Integer> list = Arrays.asList(nums);
			List<Integer> dps = list.stream().distinct().filter(entry -> Collections.frequency(list, entry) > 1).collect(Collectors.toList());
			U.log(dps);
			
			if(!dps.contains(2)){
				U.log("unique");
			}
			
			List<Integer> intList = Arrays.asList(64,92,90,67,83,86);

		    int average = (int) intList.stream().mapToInt(val -> val).average().getAsDouble();
		    U.log(average);*/
			


		}
		

		
		static void readPDFFromPageFixDigit(){
			
			String fileName = "/home/glady/MexicoCache/Tequila_DATA/naisc_to_sic/estructura.pdf";
			
			try (PDDocument document = PDDocument.load(new File(fileName))) {

				PDFTextStripper tStripper = new PDFTextStripper();
				tStripper.setStartPage(101);
				tStripper.setEndPage(186);
				String result = tStripper.getText(document);
				result=result.replaceAll("\n"," ").replaceAll("\\s+", " ");

				result = result.replace("CÓDIGO ESTRUCTURA DEL SCIAN MÉXICO 2013 CÓDIGO ESTRUCTURA DEL SCIAN MÉXICO 2013 TRADUCCIÓN AL INGLÉS", "");
				
				ArrayList<String> allLine = Util.matchAll(result, " \\d{2,}", 0);
				for (String row : allLine) {
//					U.log(row);
					result=result.replace(" "+row+" "," @shatam"+row+" ");
					result=result.replace("@shatam@shatam", "@shatam");
				}
				//U.log(result);
				allLine.clear();
				
				String[] vals = result.split("@shatam");
				String str = null;
				for(String val : vals){
					if(val.contains("IN E G I. E st ru ct ur a de l")){
//	    				U.errLog(val);
	    				result = result.replace(val, "");
					}
					str = Util.match(val,"\\s\\w+\\d{6}");
					if(str != null){
						String naisc = Util.match(str, "\\d{6}");
						String str1 = str.replace(naisc, "@shatam"+naisc);
						String val1 = val.replace(str, str1);
						result = result.replace(val, val1);
					}
				}
				
				allLine.addAll(Arrays.asList(result.split("@shatam")));				
				
				// for digit specified here
				String row = null;				
	    		for(Iterator<String> it = allLine.iterator(); it.hasNext();){
	    			row = it.next();
	    			if(row.contains("IN E G I. E st ru ct ur a de l")){
	    				it.remove();
//						U.errLog(row);
					}else if(!row.matches("\\d{4}\\s[\\w,\\W\\s]+")){  //for 6 digit \\d{6}
	    				it.remove();
//		    			U.log(row);
	    			}
	    		}
	    		
	    		List<String[]> dataList = new ArrayList<>();
	    		String naisc = null;
	    		String spanish = null, english = null;
	    		for(int i = 0; i < allLine.size(); i = i+2){
	    			spanish = allLine.get(i);
	    			
	    			naisc = Util.match(spanish, "\\d{4}").trim();	//for 6 digit \\d{6}    			
	    			spanish = spanish.replace(naisc, "").trim();
	    			
	    			english = allLine.get(i+1);
	    			
	    			if(english.startsWith(naisc)){
	    				english = english.replace(naisc, "").trim();
	    			}else{
	    				U.errLog(naisc+"\t"+spanish+"\t"+english);
	    			}
	    			
	    			spanish = spanish.replaceAll(" T$", "");
	    			english = english.replaceAll(" T$", "");
	    			dataList.add(new String[]{naisc,U.toTitleCase(spanish),U.toTitleCase(english)});
	    			U.log(naisc+"\t"+spanish+"\t"+english);
	    		}
	    		
				CSVWriter writer = null;
				try{
					writer = new CSVWriter(new FileWriter(fileName.replace(".pdf", "_4_digit.csv")),',');
					writer.writeAll(dataList);
					writer.flush();
					writer.close();
				}catch(Exception e){
					e.printStackTrace();
				}
				U.log("write csv file of pdf file here...done");
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		static void readPDF(){
			List<String[]> listData = new ArrayList<>();

			String fileName = "/home/glady/Downloads/FINAL/SIC-to-NAICS-Crosswalk.pdf";
			
			try (PDDocument document = PDDocument.load(new File(fileName))) {

				document.getClass();

				if (!document.isEncrypted()) {

					PDFTextStripperByArea stripper = new PDFTextStripperByArea();
					stripper.setSortByPosition(true);
	
					PDFTextStripper tStripper = new PDFTextStripper();
	
					String pdfFileInText = tStripper.getText(document);
//					System.out.println("Text:" + pdfFileInText);
					String lines[] = pdfFileInText.split("\\r?\\n");
					int i = 0;
					int index = 0;
					String sic = null, sicDesc = null, naisc = null, naiscDesc = null;
				
	                for (String line : lines) {
	                    
	                	if(i++ == 0)continue;
	
	                    if(U.matches(line, "^\\d{4}\\s[\\w,\\W\\s]+\\d{6}\\s[\\w,\\W\\s]+")){
	                    	sic = Util.matchFromStart(line, "^(\\d{4})\\s([\\w,\\W\\s]+)(\\d{6})\\s([\\w,\\W\\s]+)",1);
	                    	sicDesc = Util.matchFromStart(line, "^(\\d{4})\\s([\\w,\\W\\s]+)(\\d{6})\\s([\\w,\\W\\s]+)", 2);
	                    	naisc = Util.matchFromStart(line, "^(\\d{4})\\s([\\w,\\W\\s]+)(\\d{6})\\s([\\w,\\W\\s]+)", 3);
	                    	naiscDesc = Util.matchFromStart(line, "^(\\d{4})\\s([\\w,\\W\\s]+)(\\d{6})\\s([\\w,\\W\\s]+)", 4);
	                    	
	//                    	System.out.println(line+"\n"+sic+"\t"+sicDesc+"\t"+naisc+"\t"+naiscDesc);
	                    	if(sic == null || sicDesc == null || naisc == null || naiscDesc == null){
	                    		U.log(line);
	                    	}else{
	                    		listData.add(new String[]{sic,sicDesc,naisc,naiscDesc});
	                    	}
	
	                    }else if(U.matches(line, "^\\d{4}\\s[\\w,\\W\\s]+")){
	                    	U.log("<><>found:::"+line);
	                    	
	                    	sic = Util.matchFromStart(line, "^(\\d{4})\\s([\\w,\\W\\s]+)",1);
	                    	sicDesc = Util.matchFromStart(line, "^(\\d{4})\\s([\\w,\\W\\s]+)", 2);
	                    	
	                    	if(sic == null || sicDesc == null){
	                    		U.log(line);
	                    	}else{
	                    		listData.add(new String[]{sic,sicDesc});
	                    	}
	                    	
	                    }else if(U.matches(line, "^\\d{6}\\s[\\w,\\W\\s]+")){
	                    	U.log(">>>found:::"+line);
	                   	
	                    	index = listData.size()-1;
	                    	naisc = Util.matchFromStart(line, "^(\\d{6})\\s([\\w,\\W\\s]+)", 1);
	                    	naiscDesc = Util.matchFromStart(line, "^(\\d{6})\\s([\\w,\\W\\s]+)", 2);
	                    	
	                    	if(naisc == null || naiscDesc == null){
	                     		U.log(line);
	                     	}else{
	                     		String data[] = listData.get(index);
	                        	listData.set(index, new String[]{data[0],data[1],naisc,naiscDesc});
	                     	}
	                    }else if(U.matches(line, "^Aux \\d{6}\\s[\\w,\\W\\s]+")){
	                    	U.log("<<<>>>found:::"+line);
	                    	naisc = Util.matchFromStart(line, "^Aux (\\d{6})\\s([\\w,\\W\\s]+)", 1);
	                    	naiscDesc = Util.matchFromStart(line, "^Aux (\\d{6})\\s([\\w,\\W\\s]+)", 2);
	                    	
	                    	if(naisc == null || naiscDesc == null){
	                     		U.log(line);
	                     	}else{
	                        	listData.add(index, new String[]{"Aux","",naisc,naiscDesc});
	                     	}
	                    }else if(U.matches(line, "^[\\w,\\W\\s]+")){
	                    	U.log("found:::"+line);
	                    	index = listData.size()-1;
	                    	String data[] = listData.get(index);
	                    	data[data.length-1] = data[data.length-1].trim()+" "+line.trim();
	                    	
	                    	U.errLog("index::"+index+"\t"+(data.length-1)+"\t"+Arrays.toString(data));
	                    	listData.set(index, data);
	                    	
	                    }else{
	                        U.errLog(line);
	                    }
	                    
	                   // if(i == 1440)break;
	                    
	                }//eof for
                
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String header[] = {"SIC", "SIC DESCRIPTION", "NAICS", "NAICS DESCRIPTION"};
			
			CSVWriter writer = null;
			try{
				writer = new CSVWriter(new FileWriter(fileName.replace(".pdf", ".csv")),',');
				writer.writeNext(header);
				writer.writeAll(listData);
				writer.flush();
				writer.close();
			}catch(Exception e){
				e.printStackTrace();
			}
			U.log("write csv file of pdf file here...done");
			
		}
		
		static void readNaiscToSicPdf() throws IOException{
			String fileName = "/home/glady/MexicoCache/Tequila_DATA/naisc_to_sic/SIC-to-NAICS-Crosswalk.pdf";
			CSVWriter writer=new CSVWriter(new FileWriter(fileName.replace(".pdf", ".csv")));
			PDDocument document = PDDocument.load(new File(fileName));
			if (!document.isEncrypted()) {
			    PDFTextStripper stripper = new PDFTextStripper();
			    String text = stripper.getText(document);
			    text=text.replaceAll("\n"," ").replaceAll("\\s+", " ");
			    		
			    		ArrayList<String> allLine=Util.matchAll(text, " \\d{4} ", 0);
			    		for (String string : allLine) {
			    			com.shatam.utils.U.log(string);
							text=text.replace(" "+string+" "," @shatam"+string+" ");
							text=text.replace("@shatam@shatam", "@shatam");
							
						}
			    		String[] AllLine=text.split("@shatam");
			    		for (String string : AllLine) {
							String [] lineData= {"","","",""};
							lineData[0]=Util.match(string, "\\d{4} ", 0);
							lineData[2]=Util.match(string, "\\d{6}", 0);
							if(lineData[2]==null)continue;
							U.log("hello  "+lineData[2]+"  ffff"+string);
							
							string=string.replace(lineData[2], "@@@@");
							string=string.replace(lineData[0], "");
							U.log(string);
							String[] decs=string.split("@@@@");
							lineData[1]=decs[0];
							lineData[3]=decs[1];
							
							writer.writeNext(lineData);
						}
			    		//text=text.replaceAll("\\s+", " ").replaceAll("\n", "");
			  System.out.println("Text:" + text);
			}
			writer.close();
			document.close();
		}
		
	    public static boolean isValidEmailAddress1(String email) {
//	    	"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	           String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
	           
//ePattern = "^(([^<>()\\[\\]\\\\\\.,;:\\s@\\\"]+(\\.[^<>()\\[\\]\\\\\\.,;:\\s@\\\"]+)*)|(\\\".+\\\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|((a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
	           java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
	           java.util.regex.Matcher m = p.matcher(email);
	           return m.matches();
	    }
	    
	    
		static InternetAddress emailAddr = new InternetAddress();
		public static boolean isValidEmailAddress(String email) {
			   boolean result = true;
			   if(email == null || email.trim().isEmpty()){
				   return false;
			   }
			   try {
				  emailAddr.setAddress(email);
			      emailAddr.validate();
			   } catch (AddressException ex) {
			      result = false;
			   }
			   return result;
		}
		
		
		static void loadFile(){
			String fileName = "/home/glady/MexicoCache/Tequila_DATA/files/Tested/final/Ofertia_Unique_Record.csv";
			
			HashSet<String> dateSet = new HashSet<>();
			try(CSVReader reader = new CSVReader(new FileReader(fileName),',');)  //',', '"', 1
			{
				List<String[]> dataList = new ArrayList<>(reader.readAll());
				dataList.remove(0);
				// List<String[]> allRows = reader.readAll();
			
				for(String[] data : dataList){
					if(!data[21].isEmpty()){
						dateSet.add(data[21].trim());
					}
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(String inputDate : dateSet){
				String newDate = com.shatam.utils.DateFormat.correctDate(inputDate);
				//U.log(inputDate +"\t\t"+com.shatam.utils.DateFormat.correctDate(inputDate));
				if(DateFormat.validateDate(newDate)){
					U.log(inputDate +"\t\t"+DateFormat.getDate(newDate));
				}else{
					U.errLog(inputDate+"\t\t"+newDate);
				}			
			}
			U.log("-----------------");
			for(String empCount : dateSet){
				U.log(empCount);
			}
			
		}

		static void printDateFromDB(){
			String query = "select years_in_biz from dataset where years_in_biz!='' group by years_in_biz";
			try(FileWriter writer =  new FileWriter(Path.TEQUILA_DB_PATH+"dateFile.txt");
				Connection conn = DBConnection.getConnection(Path.TEQUILA_DB_PATH, "tequilaFinal.db");
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query);)
			{
				while(rs.next()){
					writer.write(rs.getString("years_in_biz")+"\n");
				}
				writer.flush();
			} catch (SQLException | IOException e) {
				e.printStackTrace();
			}
		}
		static void loadDateFile(){
			List<String> dateList = new ArrayList<>();
			try(BufferedReader br = new BufferedReader(new FileReader(Path.TEQUILA_DB_PATH+"dateFile.txt"));){
				String line = null;
				while((line = br.readLine()) != null){
					dateList.add(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			U.log("Size of date list ::"+dateList.size());
			int i = 0;
			FileWriter writer1 = null, writer2 = null;
			try{
				writer1 = new FileWriter(Path.TEQUILA_DB_PATH+"correct_dateFile.txt");
				writer2 = new FileWriter(Path.TEQUILA_DB_PATH+"wrong_dateFile.txt");
				for(String inputDate : dateList){
					//U.log(inputDate);
//					if(i == 15600)break;
					i++;					
//					if(i <= 15180)continue;
					
					String newDate = DateFormat.correctDate(inputDate);
//					U.log(inputDate +"\t\t"+com.shatam.utils.DateFormat.correctDate(inputDate));
					if(DateFormat.validateDate(newDate)){
						U.log(inputDate +"\t\t"+DateFormat.getDate(newDate));
						writer1.write(inputDate +"\t\t"+DateFormat.getDate(newDate)+"\n");
					}else{
						U.errLog(inputDate+"\t\t"+newDate);
						writer2.write(inputDate +"\t\t"+DateFormat.getDate(newDate)+"\n");
					}
				}
				writer1.flush();
				writer2.flush();
				writer1.close();
				writer2.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		static void loadUniqueDateFile(){
			int dup = 0;
			HashSet<String> dateSet = new HashSet<>();
			try(BufferedReader br = new BufferedReader(new FileReader(Path.TEQUILA_DB_PATH+"dateFile.txt"));){
				String line = null;
				while((line = br.readLine()) != null){
					if(!dateSet.add(line)){
						dup++;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			U.log("Size of set::"+dateSet.size());
			U.log("Count of duplicate date :::"+dup);
		}
		
		
		static void loadAnnualSales(){
			HashMap<String,long[]> annualSalesMap = new HashMap<>();
			int i = 0;
			String filePath = "/home/glady/MexicoCache/database/AnnualSales_Formatted.csv";
			try(BufferedReader br =  new BufferedReader(new FileReader(filePath));)
			{
				String line = null;
				String [] nextLine = null;
				while((line = br.readLine()) != null){
					if(i++ == 0)continue;
					
					nextLine = line.split("\t");
//					U.log(Arrays.toString(nextLine));
					if(nextLine[2].equals("null")){
						nextLine[2] = "0";
					}
					if(nextLine[1].equals("null")){
						nextLine[1] = "0";
					}
					annualSalesMap.put(nextLine[0], new long[]{
						Long.parseLong(nextLine[1]),
						Long.parseLong(nextLine[2])
					});
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
/*			for(Entry<String, long[]> entry : annualSalesMap.entrySet()){
				U.log(entry.getKey()+"\t"+Arrays.toString(entry.getValue()));
			}*/
		}
		
		static void loadEmpCount(){
			HashMap<String,long[]> empCountMap = new HashMap<>();
			int i = 0;
			String filePath = "/home/glady/MexicoCache/database/EMP_Count_Formatted.csv";
			try(BufferedReader br =  new BufferedReader(new FileReader(filePath));)
			{
				String line = null;
				String [] nextLine = null;
				while((line = br.readLine()) != null){
					if(i++ == 0)continue;
					
					nextLine = line.replace("\"", "").split("\t");
//					U.log(Arrays.toString(nextLine));
					if(nextLine[2].equals("null")){
						nextLine[2] = "0";
					}
					if(nextLine[1].equals("null")){
						nextLine[1] = "0";
					}
					empCountMap.put(nextLine[0], new long[]{
						Long.parseLong(nextLine[1].trim()),
						Long.parseLong(nextLine[2].trim())
					});
				}				
			} catch (IOException e) {
				e.printStackTrace();
			}
			for(Entry<String, long[]> entry : empCountMap.entrySet()){
				U.log(entry.getKey()+"\t"+Arrays.toString(entry.getValue()));
			}
		}

		static void compareSICMap(){
			HashMap<String, String[]> sicMap1 = U.deserializedSicData("/home/glady/MexicoCache/sicCodeCanadaData.ser");
			HashMap<String, String[]> sicMap2 = U.deserializedSicData("/home/glady/MexicoCache/sic_source/updateSicCodeData.ser");
			U.log("sicMap1 ::"+sicMap1.size());
			U.log("sicMap2 ::"+sicMap2.size());
			
			for(Entry<String, String[]> map2 : sicMap2.entrySet()){
				if(!sicMap1.containsKey(map2.getKey())){
					U.log(Arrays.toString(map2.getValue()));					
				}

				if(sicMap1.containsKey(map2.getKey())){
					if(!Arrays.equals( sicMap1.get(map2.getKey()), map2.getValue() )){
						//U.errLog(Arrays.toString(map2.getValue()));
						U.log("Map1::"+Arrays.toString(sicMap1.get(map2.getKey())));
						U.log("Map2::"+Arrays.toString(map2.getValue()));
					}
				}
			}
			
			for(Entry<String, String[]> map1 : sicMap1.entrySet()){
				if(!sicMap2.containsKey(map1.getKey())){
					U.log(Arrays.toString(map1.getValue()));
				}
				if(sicMap2.containsKey(map1.getKey())){
					if(!Arrays.equals( sicMap2.get(map1.getKey()), map1.getValue() )){
						//U.errLog(Arrays.toString(map2.getValue()));
						U.errLog("Map1::"+Arrays.toString(map1.getValue()));
						U.errLog("Map2::"+Arrays.toString(sicMap2.get(map1.getKey())));
					}
				}
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
}
