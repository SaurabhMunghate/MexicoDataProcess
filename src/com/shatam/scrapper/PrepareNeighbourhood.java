package com.shatam.scrapper;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.opencsv.CSVReader;

public class PrepareNeighbourhood {

	private List<String> inputFilesList = new ArrayList<String>(){
		{
			add("/home/glady/MexicoCache/Tequila_DATA/MEXICO CSV/Formatted_CSV/Trip Advisor-HotelsALL_Correct_Correct_State.csv");
			add("/home/glady/MexicoCache/Tequila_DATA/first-csv/files_25_jan/Ofertia-BooksStores_Correct_1_Correct_Find_State_Correct.csv");
		}
	};
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PrepareNeighbourhood prepare = new PrepareNeighbourhood();
		prepare.prepareNeighbourhood();
	}

	Set<String> neighbour = new HashSet<String>();
	
	void prepareNeighbourhood(){
		
		for(String filePath : inputFilesList){
			
			Set<String> set = loadNeighbourhoodMap(filePath);
			if(set != null)
				neighbour.addAll(set);
		}
	}
	
	private Set<String> loadNeighbourhoodMap(String filePath){
		
		Set<String> neighbourMap = new HashSet<String>();
		
		CSVReader reader = null;
		List<String[]> neighbourList = null;
		try{
			reader = new CSVReader(new FileReader(filePath),',');
			neighbourList = reader.readAll();
			reader.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		String[] lines = null;
		int x = 0;
		
		Iterator<String[]> it = neighbourList.iterator();
		
		while (it.hasNext()) {
			lines= it.next();

			if(x++ == 0)continue;
			
			neighbourMap.add(lines[9].trim().replaceAll(",$|\\.$|^ |^,", "").replaceAll("\\s{1,}", " ").trim());			
		}
		return neighbourMap;	
	}
	
	private void generateSerialiseFile(){
		
	}
}
