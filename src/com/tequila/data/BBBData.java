package com.tequila.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.shatam.collection.MultiMap;
import com.shatam.collection.MultiValueMap;
import com.shatam.utils.U;

public class BBBData {

	public static void main(String[] args) {
//		categoriesCount();
//		sectorAndCategory();
		addSicToBBBFile();
	}

	private static final String SOURCE_DIR = "/home/glady/MexicoCache/Tequila_DATA/BBB/AllFiles/"; 
	
	private static final String BBB_SIC_FILE = "/home/glady/Downloads/MexicoProject/Priyanka/BBB_SIC.csv";
	
	private static final String[] HEADER = {"INDEX","CATEGORY","COUNT_OF_RECORDS","COUNT_OF_URLS","COUNT_OF_EMAILS","COUNT_OF_CONTACT_PERSONS","COUNT_OF_TITLES","COUNT_OF_OPENED_YEARS"};
	
	static TreeSet<String> categoryTree =  new TreeSet<>();
	static TreeSet<String> industrySectorTree =  new TreeSet<>();
	
	static MultiMap<String, String> categoryAndSectorMultiMap = new MultiValueMap<>();
	
	static MultiMap<String, String> urlMultiMap = new MultiValueMap<>();
	static MultiMap<String, String> emailMultiMap = new MultiValueMap<>();

	static MultiMap<String, String> contactPersonMultiMap = new MultiValueMap<>();
	static MultiMap<String, String> titleMultiMap = new MultiValueMap<>();
	static MultiMap<String, String> yearsInBizMultiMap = new MultiValueMap<>();

	
	
	static void addSicToBBBFile(){
		Map<String,String[]> sicMap = new HashMap<>();
		List<String[]> readLines = U.readCsvFileWithoutHeader(BBB_SIC_FILE);
		for(String[] lines : readLines){
			sicMap.put((lines[1].trim()+lines[2].trim()).toLowerCase(), lines);
		}
		
		File[] files = new File(SOURCE_DIR).listFiles();
		for(File file : files){
			if(file.getName().endsWith(".csv")){
				List<String[]> writeLines = U.readCsvFile(file.getAbsolutePath());
				String [] lines = null;
				int x = 0;
				int index = 0;
				
				Iterator<String[]> it = writeLines.iterator();
				while(it.hasNext()){
					lines = it.next();
					if(x++ == 0)continue;
					
					String[] data = sicMap.get((lines[1].trim()+lines[5].trim()).toLowerCase());
					if(data != null){
						lines[4] = data[4].trim();
						if(lines[4].length() == 3)lines[4] = "0"+lines[4];
//						U.log("Sic :"+lines[4].trim());
						String [] sicData = U.extractSICInfo(lines[4].trim());
						if(sicData != null){
							lines[1] = sicData[0]; //industry sector
							lines[2] = sicData[1]; //spanish industry sector
							lines[3] = sicData[3]; //major sic code
							lines[5] = sicData[5]; //product description
							lines[6] = sicData[6]
									.replace("Nuevos Y Usados ​​Concesionarios De Coches Y Camiones", "Nuevos Y Usados Concesionarios De Coches Y Camiones"); //spanish product description
						}
					}
					lines[0] = ""+index++;
				}
				U.writeCsvFile(writeLines, file.getAbsolutePath().replace(file.getName(), "Added/"+file.getName()));
			}//eof file
		}
	}
	
/*	class BBBSicCategories{
		private String industrySector = null;
		private String category = null;
		private String sicSub = null;
		
		void setIndustrySector(String industrySector){
			this.industrySector = industrySector;
		}
		void setCategory(String category){
			this.category = category;
		}
		void setSicSub(String sicSub){
			this.sicSub = sicSub;
		}
		
		String getIndustrySector(){
			return industrySector;
		}
		String getCategory(){
			return category;
		}
		String getSicSub(){
			return sicSub;
		}
	}*/
	
	static void categoriesCount(){
		int index = 1;
		List<String[]> writeLines = new ArrayList<>();
		List<String> categoryDataset = new ArrayList<>();
	
		File[] files = new File(SOURCE_DIR).listFiles();
		for(File file : files){		
			U.log(file.getName());
			categoryDataset.addAll(loadCategory(file.getPath()));
		}
		U.log("Size of category dataset :"+categoryDataset.size());
		U.log("Total category found :"+categoryTree.size());
		
		int count = 0;
		int urlCount = 0, emailCount = 0, personCount = 0, titleCount = 0, bizCount = 0;
		for(String category : categoryTree){
			count = Collections.frequency(categoryDataset, category);
			U.log(category+"\t\tCount ::"+count);
			
			Collection url = urlMultiMap.get(category);
			if(url != null) urlCount = url.size();

			Collection email = emailMultiMap.get(category);
			if(email != null) emailCount = email.size();

			Collection person = contactPersonMultiMap.get(category);
			if(person != null) personCount = person.size();

			Collection title = titleMultiMap.get(category);
			if(title != null) titleCount = title.size();

			Collection biz = yearsInBizMultiMap.get(category);
			if(biz != null) bizCount = biz.size();

			
			writeLines.add(new String[]{
				""+index++, category, ""+count, ""+urlCount, ""+emailCount, ""+personCount, ""+titleCount, ""+bizCount });
			count = 0;
			urlCount = 0;
			emailCount = 0;
			personCount = 0;
			titleCount = 0;
			bizCount = 0;
		}
		U.writeCsvFile(HEADER, writeLines, SOURCE_DIR+"Category_Report_All.csv");
	}
	
	static void sectorAndCategory(){
		String titles [] = {"INDEX","INDUSTRY_SECTOR","CATEGORY","COUNT_OF_RECORDS","SIC_SUB"};
		int index = 1;
		List<String[]> writeLines = new ArrayList<>();
		List<String> categoryDataset = new ArrayList<>();
		
		File[] files = new File(SOURCE_DIR).listFiles();
		for(File file : files){		
			U.log(file.getName());
			if(file.isFile())
				categoryDataset.addAll(loadCategory(file.getPath()));
		}
		U.log("Size of category dataset :"+categoryDataset.size());
		U.log("Total category found :"+categoryTree.size());
		U.log("Total sector found :"+industrySectorTree.size());
		
		Map<String, Integer> categoryCountMap = new HashMap<>();

		for(String category : categoryTree){
			categoryCountMap.put(category, Collections.frequency(categoryDataset, category));
		}
		
		for(String industrySector : industrySectorTree){
			List<String> categortList = (List<String>) categoryAndSectorMultiMap.get(industrySector);
			
			Set<String> categorySet = new HashSet<>(categortList);
			
			for(String category : categorySet){
				writeLines.add(new String[]{
					""+index++, industrySector, category, ""+categoryCountMap.get(category),""	
				});
			}
		}
		U.writeCsvFile(titles, writeLines, SOURCE_DIR+"Report/Industry_Category_Sic.csv");
	}
			
	
	static List<String> loadCategory(String filePath){
		List<String> categoryDataset = new ArrayList<>();
		List<String[]> readLines = U.readCsvFileWithoutHeader(filePath);
		
		for(String[] lines : readLines){
			if(lines[5].trim().isEmpty()){
				U.log(filePath +"\t\tIndex :"+lines[0]);
			}
			
			categoryDataset.add(lines[5].trim());
			categoryTree.add(lines[5].trim());
			industrySectorTree.add(lines[1].trim());
			categoryAndSectorMultiMap.put(lines[1].trim(), lines[5].trim());
			
			if(!lines[15].trim().isEmpty()) urlMultiMap.put(lines[5].trim(), lines[15].trim());
			if(!lines[16].trim().isEmpty()) emailMultiMap.put(lines[5].trim(), lines[16].trim());
			if(!lines[17].trim().isEmpty()) contactPersonMultiMap.put(lines[5].trim(), lines[17].trim());
			if(!lines[18].trim().isEmpty()) titleMultiMap.put(lines[5].trim(), lines[18].trim());
			if(!lines[21].trim().isEmpty()) yearsInBizMultiMap.put(lines[5].trim(), lines[21].trim());

		}
		return categoryDataset;
	}
}
