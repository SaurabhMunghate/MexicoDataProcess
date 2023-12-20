package com.shatam.scrapper;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class SplitNeighborhoodFromAdd {

	
	private String inputFile = "/home/shatam-100/Cache/All_Unique_Record.csv";

	List<String[]>  readLines = null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SplitNeighborhoodFromAdd correct = new SplitNeighborhoodFromAdd();
		correct.loadReadFile();
//		correct.readFile();
//		correct.writeFile();
		correct.extractNeighbourFromAddress(true);
	}
	
	void extractNeighbourFromAddress(boolean status){
		
		String[] lines = null;
		int i = 0;
		String neighbour = null;
		
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			U.log(lines[8]+"    "+lines[9]);
			
			if(i++ == 0)continue;
			
			if(lines[9].trim().isEmpty() || lines[9].trim().length() < 2){
				
				
				if(lines[8].endsWith("Colonia"))continue;
				if(lines[8].contains("Colonia") || lines[8].contains("Col.") || lines[8].contains("Col ")){
					neighbour = Util.match(lines[8].trim(), "\\b(Colonia|Col\\.?)\\s[\\w\\s\\.\\W]+$");
					U.log(lines[0]+"\t\t"+neighbour);
				}
				else if(lines[8].contains("Fracc")){
					neighbour = Util.match(lines[8].trim(), "\\b(Fraccionamiento|Fracc\\.)\\s[\\w\\s\\.\\W]+$");
					U.log(lines[0]+"\t\t"+neighbour);
				}
				else if(lines[8].contains("Barrio")){
					neighbour = Util.match(lines[8].trim(), "\\bBarrio\\b\\s[\\w\\s\\.\\W]+$");
					U.log(lines[0]+"\t\t"+neighbour);
				}else if (lines[8].trim().endsWith(", Centro")){
					neighbour = Util.match(lines[8].trim(), ",\\s?\\bCentro\\s?\\b+$");
					U.log(lines[0]+"\t\t"+neighbour);
				}else if (lines[8].trim().endsWith("Zona Centro")){
					neighbour = Util.match(lines[8].trim(), ",?\\s?\\bZona Centro\\s?,?\\b+$");
					U.log(lines[0]+"\t\t"+neighbour);
				}
				if(neighbour != null){
					lines[8] = lines[8].replace(neighbour, "");
					lines[9] = neighbour.replace(",", "").trim();
				}
				neighbour = null;
			}
		}//eof while
		if(status){
			U.writeCsvFile(readLines, inputFile.replace(".csv", "_Split_NB.csv"));
		}
	}
	
	public static String[] splitColonia(String address){
		String[] add = {"",""};
		String neighbour = null;
		if(address.endsWith("Colonia")){
			add[0] = address;
			add[1] = "";
			U.log("add inside: "+Arrays.toString(add));
		}
		if(address.contains("Colonia") || address.contains("Col.") || address.contains("Col ")){
			neighbour = Util.match(address.trim(), "\\b(Colonia|Col\\.?)\\s?[\\w\\s\\.\\W]+$");
			U.log("neighbour ==== "+neighbour);
		}
		else if(address.contains("Fracc")){
			neighbour = Util.match(address.trim(), "\\b(Fraccionamiento|Fracc\\.)\\s[\\w\\s\\.\\W]+$");
		}
		else if(address.contains("Barrio")){
			neighbour = Util.match(address.trim(), "\\bBarrio\\b\\s[\\w\\s\\.\\W]+$");
		}else if (address.trim().endsWith(", Centro")){
			neighbour = Util.match(address.trim(), ",\\s?\\bCentro\\s?\\b+$");
		}else if (address.trim().endsWith("Zona Centro")){
			neighbour = Util.match(address.trim(), ",?\\s?\\bZona Centro\\s?,?\\b+$");
		}
		if(neighbour != null){
			address = address.replace(neighbour, "");
			neighbour = neighbour.replace(",", "").trim();
		}
		address = address.trim().replaceAll(",$", "").trim();
		if(neighbour == null)neighbour = "";
		add[0] = address;
		add[1] = neighbour;
		return add;
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
	
	static int chk = 0;
	void readFile(){
		
		U.log("Size:"+readLines.size());
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {
			lines= it.next();

			if(x++ == 0)continue;

			if (x == 259)
			{
			
//			U.log(Arrays.toString(lines));
//			U.log(lines.length);
			String vals[] = findNeighborhoodFromAddress(lines[8],lines[10]);  //address
			lines[8] = vals[0]; //address
			lines[9] = vals[1];	//neighbourhood		
			U.log(Arrays.toString(vals));
			
			//further split,if neighbourhood is not extracted 
			if(lines[9].isEmpty()){
				vals = findNeighbourhood(lines[8]);
				lines[8] = vals[0]; //address
				lines[9] = vals[1];	//neighbourhood		
			}
			if(!lines[9].isEmpty()){
				lines[9] = lines[9].replace("Col. Colonia", "Colonia").replace("Col. Col.", "Col.");
			}
//			break;
			}
		}
		U.log("reading done..."+x+" rows");
		U.log("replace city from address "+chk);
	}
	
	public static String[] findNeighborhoodFromAddress (String address,String city) {

		if(address.trim().endsWith(city.trim())){
			address = address.trim().replaceAll(city.trim()+"$", "");
			chk++;
		}
		U.log(address);
		
		String neighborhhood = null;
		if(address.contains(","))
			neighborhhood = Util.match(address.trim(), "\\b(C|c)ol(\\.|onia)?\\s[\\w\\s\\.\\W]+$|,\\s?Centro$");  //|^Col(\\.)?\\s[\\w\\s[\\W+]?]+\\b,
		
		U.log("neighborhhood ==== "+neighborhhood);
		
		if(neighborhhood != null){
			U.log(">>"+neighborhhood);
			if(!neighborhhood.contains(","))
				address = address.replace(neighborhhood, "").trim().replaceAll(",$", "");
			
		}else if(neighborhhood == null){
			if(address.contains(",")){
				neighborhhood = Util.match(address, "^Col(\\.)?\\s[\\w\\s]+\\b\\s?,|^Col(\\.)?\\s[\\w\\s\\W]+\\b\\s?,");
				if(neighborhhood != null){
					address = address.replace(neighborhhood, "").trim().replaceAll(",$", "");
				}else{
					neighborhhood = Util.match(address, "\\b(C|c)ol(\\.|onia)?\\s([\\w\\s]+){1,2}?\\s?,(([\\w\\s]+){1,3}?)?");
					if(neighborhhood != null){
						//U.log(neighborhhood);
						address = address.replace(neighborhhood, "").trim().replaceAll(",$", "");
						neighborhhood = neighborhhood.replace(neighborhhood.substring(neighborhhood.indexOf(',')),"");
						
					}else{
						neighborhhood = "";
					}
				}
			}else{
				neighborhhood = Util.match(address, "\\b(C|c)ol(\\.|onia)?\\s[\\w\\s\\.\\W]+$");
				if(neighborhhood == null)
					neighborhhood = "";
				else{
					address = address.replace(neighborhhood, "").trim().replaceAll(",$", "");
				}
			}
		}
		if(neighborhhood != null && neighborhhood.contains(",")){
			//U.log(">>"+neighborhhood);
			neighborhhood = Util.match(neighborhhood.trim(), "^(C|c)ol(\\.)?\\s[\\w\\W]+\\b(\\s|\\.)?,");  //|^(C|c)ol(\\.)?\\s(.*?){1,}\\b\\s?,
//			U.log(">>"+neighborhhood);
			if(neighborhhood != null){
				//U.log(address);
				address = address.replace(neighborhhood, "").trim().replaceAll(",$", "");
			}else{
				neighborhhood = "";
			}
			if(address.contains("México Df."))
				address = address.replace("México Df.", "");
		}


		
		neighborhhood = neighborhhood.trim().replaceAll(",$|^,|^-", "")
				.replace("Col. Jardin Espaã±ol", "Col. Jardín Español");
		
		address = address.trim().replaceAll(",$|^,|-$", "");
		U.log("neighborhhood findNeighborhoodFromAddress: "+neighborhhood);
		
		
		return new String[]{address.trim(),neighborhhood.trim()};
	}
	
	
	private String[] findNeighbourhood(String address){
		address = address.trim().replaceAll(",$|^ |-$", "");
		String neighbour = null;
		if(address.contains(",")){
			String[] vals = address.trim().split(",");
			
			if(vals.length > 1){
				neighbour = vals[vals.length-1].trim();
			}
		}else if(address.contains("-")){
			String[] vals = address.trim().split("-");
	
			if(vals.length > 1){
				neighbour = vals[vals.length-1].trim();				
			}
		}
		if(neighbour == null) neighbour = "";
		
		if(!neighbour.isEmpty() || neighbour != null){
			
			if(neighbour.contains("#")) neighbour = "";

			if(U.matches(neighbour, "^(?!\\d+)[\\s\\w\\W]+") && (neighbour != null || !neighbour.isEmpty())){
				address = address.replace(neighbour, "");
			}
		}

		neighbour = neighbour.trim().replaceAll(",$|^,|^-|^ ", "");
		
		address = address.trim().replaceAll(",$|^,|-$|^ ", "");
		
		return new String[]{address,neighbour};
	}
	
	
	void writeFile(){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(inputFile.replace(".csv", "_Correct_1.csv")),',');
			writer.writeAll(readLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log("writing done...");
	}
	


}
