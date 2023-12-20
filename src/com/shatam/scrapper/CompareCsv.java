package com.shatam.scrapper;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.SawanUtil;
import com.shatam.utils.U;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class CompareCsv {

	public static void main(String[] args) {
//		compareOldCityWithNewCity();
		compareCityWithMultipleState();
	}

	private static final String NEW_FILE = "/home/glady/Downloads/MexicoProject/pramod/Untitled Folder/Ofertia-Supermarket_Unique_Record.csv";
	private static final String OLD_FILE = "/home/glady/MexicoCache/Tequila_DATA/files/Tested/25-apr/Ofertia-CarsAndMotorCycles.csv";
	
	static void compareOldCityWithNewCity(){
		U.log("Start comparing old city with newly formatted city here..");
		List<String[]> oldReadLines = U.readCsvFileWithoutHeader(OLD_FILE);
		List<String[]> newReadLines = U.readCsvFileWithoutHeader(NEW_FILE);

		String[] lines = null;
		String[] oldLines = null;
		int x = 0;

		Iterator<String[]> it = newReadLines.iterator();
		
		if(oldReadLines.size() == newReadLines.size()){
			
			while (it.hasNext()) {
				lines= it.next();
				oldLines = oldReadLines.get(x++);
				if(FuzzySearch.partialRatio(lines[10].trim(), oldLines[10].trim()) < 60){
					if(!lines[0].trim().equals(oldLines[0])){
						U.errLog("Index not match. New [index] ::"+lines[0]+"\t\tOld [Index] ::"+oldLines[0]);
					}else{
						U.log("(Row) :"+(x+1)+" [Index] :"+lines[0]+"==> {New City} ::"+lines[10]+" {Old City} ::"+oldLines[10]);	
					}
				}
				
			}//eof while
		}//eof if
	}
	
	
	static void compareCityWithMultipleState(){
		MultiMap<String, Record> cityMap = new MultiValueMap<>();
		
		List<String[]> readLines = U.readCsvFile(NEW_FILE);
		
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
			U.log("******* City :::"+key);
			List<Record> values = (List<Record>) cityMap.get(key);
			for(Record record : values){
				U.log(record.toString());
//				SawanUtil.toWrite(record.toString());
			}
		}
	}
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