package com.shatam.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.opencsv.CSVReader;
import com.shatam.conversion.Employee;
import com.shatam.conversion.PriceConverter;
import com.shatam.scrapper.CSVCorrectorInValidFormat;

public class Corrector{
	
	public static HashMap<String,long[]> loadAnnualSales(){
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
		return annualSalesMap;
	}
	
	public static HashMap<String,long[]> loadEmpCount(){
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
		return empCountMap;
	}
	
	@SuppressWarnings("deprecation")
	public static HashMap<String,String> loadCorrectYearsInBiz(String inputFilePath){
		
		HashSet<String> dateSet = new HashSet<>();
		try(CSVReader reader = new CSVReader(new FileReader(inputFilePath),',','"',1);)
		{
			List<String[]> dataList = reader.readAll();
		
			for(String[] data : dataList){
				if(!data[21].isEmpty()){
					dateSet.add(data[21].trim());
				}
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		U.log("Size of set::"+dateSet.size());
		
		int count=0;
		HashMap<String,String> correctDateMap = new HashMap<>();
		for(String inputDate : dateSet){
			if(!DateFormat.validateDate(inputDate)){
				count++;
				String newDate = DateFormat.correctDate(inputDate);				
				if(DateFormat.validateDate(newDate)){
					//U.log(inputDate +"\t\t"+DateFormat.getDate(newDate));
					correctDateMap.put(inputDate,DateFormat.getDate(newDate));
				}else{
					U.errLog(inputDate+"\t\t"+newDate);
				}
			}			
		}
		U.log("Count of date is not in proper format ::"+count);
		U.log("Size of corrected date map ::"+correctDateMap.size());
		
		return correctDateMap;
	}//eof checkYearInBizAtDB();
	
	
	public static HashMap<String,String> loadCorrectYearsInBiz(String inputFilePath, int index){
		
		HashMap<String,String> correctDateMap = new HashMap<>();
		HashSet<String> dateSet = new HashSet<>();
/*		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {
			lines= it.next();
			
			if (x++ == 0) continue;
			if(!lines[23].isEmpty()){
				dateSet.add(lines[23].trim());
			}
		}*/
		
		try(CSVReader reader = new CSVReader(new FileReader(inputFilePath),',','"',1);)
		{
			List<String[]> dataList = reader.readAll();
		
			for(String[] data : dataList){
				if(!data[index].trim().isEmpty()){
					dateSet.add(data[index].trim());
				}
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String newDate = null;
		for(String inputDate : dateSet){
			inputDate = inputDate.replace("'", "");
			if(!DateFormat.validateDate(inputDate)){
				newDate = DateFormat.correctDate(inputDate);	
				if(DateFormat.validateDate(newDate)){
					correctDateMap.put(inputDate,DateFormat.getDate(newDate));
				}else{
					throw new IllegalArgumentException("Input date is :"+inputDate+" and its illegal date format is ::"+newDate);
				}
			}else{
				correctDateMap.put(inputDate,inputDate);
			}
		}
		return correctDateMap;
	}
	
	public static HashMap<String,String> loadCorrectYearsInBizOnlyYear(String inputFilePath, int index){
		
		HashMap<String,String> correctDateMap = new HashMap<>();
		try(CSVReader reader = new CSVReader(new FileReader(inputFilePath),',','"',1);)
		{
			List<String[]> dataList = reader.readAll();
			 
			for(String[] data : dataList){
				if(!data[index].trim().isEmpty() || data[index].trim().length()>3){
					String dateVal = data[index];
					if(data[index].contains("/")){
						dateVal = dateVal.replace("/", "-");
//						throw new IllegalArgumentException("YEAR IN BIZ does not contain '/' char.");
					}
					correctDateMap.put(data[index], Util.match(dateVal, "\\d{4}"));
				}
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return correctDateMap;
	}
	
	public static HashMap<String,long[]> loadAnnualSales(String inputFilePath, int index){
		HashMap<String,long[]> annualSalesMap = new HashMap<>();

		HashSet<String> dateSet = new HashSet<>();		
		try(CSVReader reader = new CSVReader(new FileReader(inputFilePath),',','"',1);)
		{
			List<String[]> dataList = reader.readAll();
		
			for(String[] data : dataList){
				if(!data[index].isEmpty()){
					dateSet.add(data[index].trim().replace(" ", ""));
				}
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String vals[] = null;
		for(String price : dateSet){
			vals = PriceConverter.convertToDigit(price);
			
			if(vals[0].isEmpty()){
				vals[0] = "0";
			}
			if(vals[1].isEmpty()){
				vals[1] = "0";
			}
			annualSalesMap.put(price, new long[]{
					Long.parseLong(vals[0]),
					Long.parseLong(vals[1])
			});
		}
		return annualSalesMap;
	}
	
	public static HashMap<String,long[]> loadEmpCount(String inputFilePath, int index){
		HashMap<String,long[]> empCountMap = new HashMap<>();
		
		HashSet<String> dateSet = new HashSet<>();		
		try(CSVReader reader = new CSVReader(new FileReader(inputFilePath),',','"',1);)
		{
			List<String[]> dataList = reader.readAll();
		
			for(String[] data : dataList){
				if(!data[index].isEmpty()){
					dateSet.add(data[index].trim());
				}
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String vals[] = null;
		for(String empCount : dateSet){
			vals = Employee.getMinAndMaxEmp(empCount);
			
			if(vals[0].isEmpty()){
				vals[0] = "0";
			}
			if(vals[1].isEmpty()){
				vals[1] = "0";
			}
			long[] empCounts = new long[]{Long.parseLong(vals[0]),Long.parseLong(vals[1])};
			
			if(empCounts[0] == 0 && empCounts[1] > 1){
				empCounts[0] = 1;
			}
			empCountMap.put(empCount, empCounts);
		}
		return empCountMap;
	}
	
	public static HashMap<String,String> loadCompanyNameStandardised(String inputFilePath, int index){
		HashMap<String,String> companyNameStandardisedMap = new HashMap<>();
		
		HashSet<String> dateSet = new HashSet<>();		
		try(CSVReader reader = new CSVReader(new FileReader(inputFilePath),',','"',1);)
		{
			List<String[]> dataList = reader.readAll();
		
			for(String[] data : dataList){ 
				if(!data[index].isEmpty()){
					dateSet.add(data[index].trim());
				}
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
		for(String companyName : dateSet){
			companyNameStandardisedMap.put(companyName, TextFormat.getCompanyNameStandardised(companyName));
		}
		return companyNameStandardisedMap;
	}
	
}
