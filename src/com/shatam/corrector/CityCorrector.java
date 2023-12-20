package com.shatam.corrector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.U;
import com.tequila.address.StateReader;

public class CityCorrector {

	public static void main(String[] args){
		String fileName = null;
		try(BufferedReader br = new BufferedReader(new FileReader("input.txt"))){
			fileName = br.readLine();
		}catch(IOException ex){
			U.log("File Not Found or File path is not in correct format here.");
		}
		if(fileName != null){
			correctCityIntoStandardFormat(fileName);
		}
	}
	
	static void correctCityIntoStandardFormat(String fileName){
		File file = new File("CityError.log");
		if(file.exists())file.delete();
		
		try(FileWriter fw = new FileWriter("CityError.log",true);){

			boolean flag = false;
			List<String[]> readLines = U.readCsvFile(fileName);
			
			Map<String,Set<String>> citiesMap = new HashMap<>();
			
			String[] lines = null;
			int x = 0;
			Iterator<String[]> it = readLines.iterator();
			while (it.hasNext()) {
	
				lines= it.next();
				if (x++ == 0) continue;
					
				Set<String> cities = null;
				if(citiesMap.containsKey(lines[11].trim())){
					cities = citiesMap.get(lines[11].trim());
				}else{
					cities = StateReader.getAllCities(lines[11].trim());
					citiesMap.put(lines[11].trim(), cities);
				}
				
				if(cities.contains(U.toTitleCase(lines[10].trim()))){
					lines[10] = U.toTitleCase(lines[10].trim());
				}else if(cities.contains(U.toTitleCase(TranslateEnglish.convertToEnglish(lines[10].trim())).replace(" - ", "-"))){
					lines[10] = U.toTitleCase(TranslateEnglish.convertToEnglish(lines[10].trim())).replace(" - ", "-");
				}else{
					flag = true;
					try{
						throw new Exception();
					}catch(Exception e){
//						U.log("# Row :"+x+" # Index :"+lines[0]+" City is not correct for this state "+lines[11]+"\t\tCity :"+lines[10].trim());
						fw.write("# Row :"+x+" # Index :"+lines[0]+" City is not correct for this state "+lines[11]+"\t\tCity :"+lines[10].trim()+"\n");
					}
				}
			}//eof if
			if(!flag){
				U.writeCsvFile(readLines, fileName.replace(".csv", "_Correct_City.csv"));
				compareCityWithMultipleState(fileName.replace(".csv", "_Correct_City.csv"));
			}else U.log("Please, check 'CityError.log' file");
			fw.flush();
		}catch(IOException e){}
	}//eof correctCityIntoStandardFormat()
	
	static void compareCityWithMultipleState(String fileName){
		
		File file = new File("Correct_City_Found_At_State.log");
		if(file.exists())
			file.delete();
		
		try(FileWriter fw = new FileWriter("Correct_City_Found_At_State.log",true);){
			MultiMap<String, Record> cityMap = new MultiValueMap<>();
			
			List<String[]> readLines = U.readCsvFile(fileName);
			
			String[] lines = null;
			int i = 0;
			
			Iterator<String[]> it = readLines.iterator();
			
			while(it.hasNext()){
				lines = it.next();
				
				if(i++ == 0)continue;
				cityMap.put(lines[10].trim(), new Record(i,Integer.parseInt(lines[0].trim()), lines[11].trim()));			
			}//eof while
			
			Set<String> keys = cityMap.keySet();
			for(String key : keys){
	//			SawanUtil.toWrite("******* City :::"+key);
//				U.log("******* City :::"+key);
				fw.write("******* City :::"+key+"\n");
				List<Record> values = (List<Record>) cityMap.get(key);
				for(Record record : values){
//					U.log(record.toString());
					fw.write(record.toString()+"\n");
	//				SawanUtil.toWrite(record.toString());
				}
			}
			U.log("Please, check compare 'Correct_City_Found_At_State.log' file");
			fw.flush();
		}catch (IOException e){}
	}//eof compareCityWithMultipleState()

}
class Record{
	private int row = 0;
	private int index = 0;
	private String data = null;
	
	public Record(){}
	
	public Record(int row, int index, String data){
		this.row = row;
		this.index = index;
		this.data = data;
	}

	public void setRowNum(int row){
		this.row = row;
	}
	
	public void setIndexNum(int index){
		this.index = index;
	}
	
	public void setColumnValue(String data){
		this.data = data;
	}
	
	public int getRowNum(){return row;}
	public int getIndexNum(){ return index; }
	public String getColumnValues(){ return data; }
	
	@Override
	public String toString(){
		return "Row :"+row+"\tIndex :"+index+" ColumnVal :"+data;
	}
}