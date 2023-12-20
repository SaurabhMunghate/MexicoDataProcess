package com.shatam.scrapper;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NameNotFoundException;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.U;

public class CorrectStateFromCSV {

	private String inputFile = "/home/glady/MexicoCache/Tequila_DATA/siem/Not_Empty_company/Siem_Gob_Mex_NonEmptyCompany_Correct.csv";

	List<String[]>  readLines = null;
	
	public static void main(String[] args) throws NameNotFoundException {
		CorrectStateFromCSV correct = new CorrectStateFromCSV();
		U.log("Start...");
		correct.loadReadFile();
		correct.readFile();
		correct.writeFile();
		U.log("Done....");
	}
	
	void loadReadFile(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(inputFile));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	Map<String, String> correctStateMap = new HashMap<>();
	void loadStateSet() throws NameNotFoundException{
		Set<String> stateSet = new HashSet<String>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			stateSet.add(lines[11].trim());
		}
		
		for(String state : stateSet){
			String correctFormat = U.matchState(state);
			if(correctFormat.equals("-")){
				throw new NameNotFoundException("State name is not found for "+state);
			}
			correctStateMap.put(state, correctFormat);
		}
	}
	
	void readFile() throws NameNotFoundException{
		
		loadStateSet(); //-----1
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
//			if (x++ != 3305) continue;
			
//			U.log(lines[11]);
			
/*			lines[11] = U.matchState(lines[11]);  //State
			if(lines[11].equals("-")){
				throw new NameNotFoundException("State name is not found at line index "+lines[0]);
			}
*/
			lines[11] = correctStateMap.get(lines[11]);   //----1
//			U.log(lines[11]);
//			if(x == 3305) break;
		}
	}
	
	void writeFile(){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(inputFile.replace(".csv", "_Correct_State.csv")),',');
			writer.writeAll(readLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
