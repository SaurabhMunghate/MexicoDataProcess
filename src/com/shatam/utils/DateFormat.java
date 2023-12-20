package com.shatam.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;

public final class DateFormat {
	
	private static final MultiMap month =  new MultiValueMap();
	static{
		month.put("01", "January");
		month.put("01", "Jan");
		month.put("02", "February");
		month.put("02", "Feb");
		month.put("03", "March");
		month.put("03", "Mar");
		month.put("04", "April");
		month.put("04", "Apr");
		month.put("05", "May");
		month.put("06", "June");
		month.put("06", "Jun");
		month.put("07", "July");
		month.put("07", "Jul");			
		month.put("08", "August");
		month.put("08", "Aug");
		month.put("09", "September");
		month.put("09", "Sep");
		month.put("09", "Sept");
		month.put("10", "October");
		month.put("10", "Oct");
		month.put("11", "November");
		month.put("11", "Nov");
		month.put("12", "December");
		month.put("12", "Dec");	
    }

    private static final String getMonth(String abbr){
		Set<String> keys = month.keySet();
		for(String key : keys){
			List<String> values = (List<String>) month.get(key);
			if(values.contains(abbr)){
				return key;
			}
		}
		return null;
	}
	
	public static final String correctDate(String inputDate){
		String correctFormat = "";
		inputDate = inputDate.replaceAll("\\s{1,}", " ").replace(" ", " ");
		if(inputDate.length() == 4){
			if(U.matches(inputDate, "\\d{4}")){
				correctFormat = inputDate+"-01-01";
			}
		}else{
			String yyyy = "", mm = "",dd ="";
			inputDate = inputDate.replaceAll("\\s{2,}", " ");
			
			if(U.matches(inputDate, "\\w{3,} \\d{1,2},\\s?\\d{4}")){
				
				String vals[] = inputDate.split(",");
				if(vals.length == 2){
					yyyy = vals[1].trim();
					String md [] = vals[0].split(" ");
					if(md.length == 2){
						mm = getMonth(md[0]);
						if(md[1].trim().length() == 1)
							dd = "0"+md[1].trim();
						else if(md[1].trim().length() == 2)
							dd = md[1].trim();
					}	
				}
			}else if(U.matches(inputDate, "\\w{3,} \\d{4}")){
				String my [] = inputDate.split(" ");
				if(my.length == 2){
					mm = getMonth(my[0]);
					yyyy = my[1].trim();
					dd = "01";
				}
			}else if(U.matches(inputDate, "\\d{1,2} \\w{3,} \\d{4}")){  //"\\d{1,2} \\w{3,} \\d{4}"
				
				String vals [] = inputDate.split(" ");
				if(vals.length == 3){
					
					if(vals[0].trim().length() == 1)
						dd = "0"+vals[0].trim();
					else if(vals[0].trim().length() == 2)
						dd = vals[0].trim();
					
					mm = getMonth(vals[1].trim());
					yyyy = vals[2].trim();
				}
			}else if(U.matches(inputDate, "\\d{1,2}/\\d{1,2}/\\d{4}")){
				String vals[] = inputDate.split("/");
				if(vals.length == 3){
					int day = Integer.parseInt(vals[0].trim());
					int month = Integer.parseInt(vals[1].trim());
					if(month <= 12){
						if(day > month && day <= 12){
							dd = vals[1].trim();
							mm = vals[0].trim();													
						}else{
							dd = vals[0].trim();
							mm = vals[1].trim();
						}
					}else if(day <= 12){
						dd = vals[1].trim();
						mm = vals[0].trim();						
					}
					if(dd.length() == 1){
						dd = "0"+dd;
					}
					if(mm.length() == 1){
						mm = "0"+mm;
					}
					yyyy = vals[2].trim();
				}
			}else if(U.matches(inputDate, "\\d{4}-\\d{1,2}-\\d{1,2}")){
				String vals[] = inputDate.split("-");
				if(vals.length == 3){
					int day = Integer.parseInt(vals[1].trim());
					int month = Integer.parseInt(vals[2].trim());
					if(month <= 12){
						if(day > month && day <= 12){
							dd = vals[2].trim();
							mm = vals[1].trim();													
						}else{
							dd = vals[1].trim();
							mm = vals[2].trim();
						}
					}else if(day <= 12){
						dd = vals[2].trim();
						mm = vals[1].trim();						
					}
					if(dd.length() == 1){
						dd = "0"+dd;
					}
					if(mm.length() == 1){
						mm = "0"+mm;
					}
					yyyy = vals[0].trim();
				}
			}
			correctFormat = yyyy.trim()+"-"+mm+"-"+dd;
		}
		return correctFormat;
	}
	
	private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static Date date = new Date();
	
	public static boolean validateDate(String input){
		try {
			date = df.parse(input+" 00:00:01");
			return true;
		} catch (ParseException e) {
			return false;				
		}
	}
	public static String getDate(String input){
		//return df.format(date);
		return input+" 00:00:01";
	}
	
}
