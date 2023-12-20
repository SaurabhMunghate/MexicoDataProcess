package com.shatam.conversion;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class PriceConverter {

	private PriceConverter(){}
	
	private enum Type{
		THOUSAND("000"),
		MILLION("000000"),
		BILLION("000000000"),
		TRILLION("000000000000");
		
		private String digit = null;
		Type(String digit){
			this.digit = digit;
		}
		
		public String getDigit(){
			return digit;
		}
		public int getDigitLength(){
			return digit.length();
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
/*
		convertToDigit("$20m to $50m");
		convertToDigit("250m To 500m");
		convertToDigit("500m To 1 Billion Actual 538,600,000");
		convertToDigit("500m To 1b");
		convertToDigit("Over 1 Billion Actual 1,129,000,000");
		convertToDigit("100 M To 500 M Actual 483,000,000");
*/
//		convertToDigit("5m To 10");
//		convertToDigit("20 To 50m");
		
		String vals[] = null;
/*		
		convertToDigit("1 To 3");
		convertToDigit("50 To 100 Million");

		convertToDigit("1 To 2.5 Million");
		String vals[] =  convertToDigit("2.5 To 5 Million");
		U.log(">>"+Arrays.toString(vals));

		vals = convertToDigit("15.45m To 10.1 B");
		U.log(">>"+Arrays.toString(vals));
//		convertToDigit("25 To 50 Million");
		
		vals =  convertToDigit("100 M To 500 M Actual 483,000,000");
		U.log(">>"+Arrays.toString(vals));

		vals =  convertToDigit("More than 100 Billion");
		U.log(">>"+Arrays.toString(vals));
		
		vals =  convertToDigit("$2.775 billion");
		U.log(">>"+Arrays.toString(vals));
		
		vals =  convertToDigit("$1m to $500,000");
		U.log(">>"+Arrays.toString(vals));
*/		
		
		vals =  convertToDigit("3.56 billion");
		U.log(">>"+Arrays.toString(vals));

		
/*		String fileName = "/home/glady/Downloads/FINAL/UniqueAnnual_Sales_VOL.csv";
		String[] header = {"ANNUAL_SALES_VOL","ANNUAL_SALES_VOL_MIN","ANNUAL_SALES_VOL_MAX"};
		
		List<String[]> readLine = null;
		try(CSVReader reader = new CSVReader(new FileReader(fileName));){
			readLine = reader.readAll();
		}catch (Exception e) {}
		
		readLine.remove(0);
		
		List<String[]> writeLine = new ArrayList<>();
		String vals[] = null;
		
		for(String[] lines : readLine){
			vals = convertToDigit(lines[0]);
			writeLine.add(new String[]{lines[0], vals[0], vals[1]});
		}
		try(CSVWriter writer = new CSVWriter(new FileWriter(fileName.replace(".csv", "_correct.csv")));){
			writer.writeNext(header);
			writer.writeAll(writeLine);
			writer.flush();
		}catch (Exception e) {}
		U.log("done");
*/		
	}
	
	
	public static String[] convertToDigit(String price){
	
		price = price.replace(" to ", " To ").replace("+", "")
				.replaceAll("\\(?USD\\)?|per|year", "").trim();
//		U.log(price);
		
//		if(U.matches(price, "\\d{1,3}\\s?(Million|million|M|m) To \\d{1,3}") ||
		if(U.matches(price, "\\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})\\s?(Million|million|M|m) To \\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})")){ 
			price = price+"Million";
			if(price.contains("."))	price = changesTodigit(price,Type.MILLION);
		}

//		if(U.matches(price, "\\d{1,3} To \\d{1,3}\\s?(Million|million|M|m)") || 
		if(U.matches(price, "\\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2}) To \\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})\\s?(Million|million|M|m)")){ 
			price = price.replace(price.substring(0, price.indexOf(" To")), price.substring(0, price.indexOf(" To"))+"Million");
			if(price.contains("."))	price = changesTodigit(price,Type.MILLION);
		}
		
		if(U.matches(price, "\\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})\\s?(Billion|billion|b|B|bn) To \\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})")){ 
			price = price+"Billion";
			if(price.contains("."))	price = changesTodigit(price,Type.BILLION);
		}
		
		if(U.matches(price, "\\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2}) To \\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})\\s?(Billion|billion|b|B|bn)")){ 
			price = price.replace(price.substring(0, price.indexOf(" To")), price.substring(0, price.indexOf(" To"))+"Billion");
			if(price.contains("."))	price = changesTodigit(price,Type.BILLION);
		}
		if(U.matches(price, "\\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})\\s?(Million|million|M|m) To \\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})\\s?(Billion|billion|b|B|bn)")){
			if(price.contains("."))	price = changesTodigit(price,Type.MILLION);
			if(price.contains("."))	price = changesTodigit(price,Type.BILLION);
		}
		
		if(U.matches(price, "\\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})\\s?(Billion|billion|b|B|bn) To \\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,2})\\s?(Million|million|M|m)")){
			if(price.contains("."))	price = changesTodigit(price,Type.MILLION);
			if(price.contains("."))	price = changesTodigit(price,Type.BILLION);
		}
		
		//\\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,3})\\s?(Million|million|M|m)
		if(U.matches(price, "\\$?(\\d{1,3}|\\d{1,2})\\.\\d{1,3}\\s?(Millions|Million|million|M|m)")){
			if(price.contains("."))	price = changesTodigit(price,Type.MILLION);
		}
		
		if(U.matches(price, "\\$?(\\d{1,3}|\\d{1,2}\\.\\d{1,3})\\s?(Billion|billion|b|B|bn)")){
			if(price.contains("."))	price = changesTodigit(price,Type.BILLION);
		}
		
		price = matchAndReplace(price, "\\$?\\d{1,3}\\s?(Million|million|m|M)", "Million|million|m|M", Type.MILLION.getDigit()); //"000000");
		price = matchAndReplace(price, "\\$?\\d{1,3}\\s?(Billion|billion|b|B|bn)", "Billion|billion|b|B|bn", Type.BILLION.getDigit()); //"000000000");
		
		price = price.replaceAll("\\$|\\.|,", "");
//		U.log(">>"+price);
		
		ArrayList<String> vals = Util.matchAll(price, "\\d+", 0);
		
		if(vals.size() == 1){
			return new String[]{vals.get(0), ""};
		}
		if(vals.size() > 1){
			return correctMinMax(vals.get(0), vals.get(1));
		}
		return new String[]{"",""};
	}
	
	private static String[] correctMinMax(String val1, String val2){
		long num1 = Long.parseLong(val1);
		long num2 = Long.parseLong(val2);
		if(num1 < num2){
			return new String[]{val1, val2};
		}
		if(num1 > num2){
			return new String[]{val2, val1};
		}
		if(num1 == num2){
			return new String[]{val1, ""};
		}
		return new String[]{"",""};
	}
	
	private static String changesTodigit(String str, Type type){
		
		ArrayList<String> vals = new ArrayList<>();
		switch (type) {
			case MILLION:
				ArrayList<String> vals1 = Util.matchAll(str, "\\d{1,2}.\\d{1,2}\\s?(Million|million|m|M)",0);
				if(vals1.size() > 0) vals.addAll(vals1);
				break;
	
			case BILLION:
				ArrayList<String> vals2 = Util.matchAll(str, "\\d{1,2}.\\d{1,2}\\s?(Billion|billion|b|B|bn)",0);
				if(vals2.size() > 0) vals.addAll(vals2);
				break;
				
			default:
				break;
		}
/*		if(Type.MILLION == type.MILLION){
			ArrayList<String> vals1 = Util.matchAll(str, "\\d{1,2}.\\d{1,2}\\s?(Million|million|m|M)",0);
			if(vals1.size() > 0) vals.addAll(vals1);
		}
		
		if(Type.BILLION == type.BILLION){
			ArrayList<String> vals2 = Util.matchAll(str, "\\d{1,2}.\\d{1,2}\\s?(Billion|billion|b|B)",0);
			if(vals2.size() > 0) vals.addAll(vals2);
		}*/
//		U.log(vals);
		String newVal = null;
		for(String val : vals){
			
			if(val != null){
				String suffix = Util.match(val, "\\d{1,2}.\\d{1,2}");
//				U.log(suffix);
				if(suffix != null){
					int len = suffix.substring(suffix.indexOf(".")+1).length();
//					U.log(suffix.substring(suffix.indexOf(".")+1));
//					U.log(type.name()+"\t"+type.getDigit());
//					U.log(type.getDigit().substring(0, type.getDigitLength()-len));
					String digit = type.getDigit().substring(0, type.getDigitLength()-len);
					
					switch (type) {
						case MILLION:
							newVal = matchAndReplace(val, "\\$?\\d{1,3}\\s?(Million|million|m|M)", "Million|million|m|M", digit);
							break;
						case BILLION:
							newVal = matchAndReplace(val, "\\$?\\d{1,3}\\s?(Billion|billion|b|B|bn)", "Billion|billion|b|B|bn", digit);
							break;
						default:
							break;
					}
/*					if(type.name() == "MILLION"){
						newVal = matchAndReplace(val, "\\$?\\d{1,3}\\s?(Million|million|m|M)", "Million|million|m|M", digit);
//						U.log("found"+newVal);
					}
					if(type.name() == "BILLION"){
						newVal = matchAndReplace(val, "\\$?\\d{1,3}\\s?(Billion|billion|b|B)", "Billion|billion|b|B", digit);
//						U.log("found"+newVal);
					}
*/						
				}//eof if
				str = str.replace(val, newVal);
			}//eof if
		}//eof for
		return str;
	}
		
	private static String matchAndReplace(String str, String expression, String replaceTo, String replacement){
		Matcher mat = Pattern.compile(expression, Pattern.CASE_INSENSITIVE).matcher(str);
		String val = null;
		while(mat.find()){
//		    U.log(mat.group());		    
		    val = mat.group().replaceAll(replaceTo, replacement).replace(" ", "");
		    str = str.replace(mat.group(), val);
//		    U.log(str);
		}
		return str;
	}

}
