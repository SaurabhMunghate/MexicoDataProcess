package com.shatam.scrapper;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.naming.NameNotFoundException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.TextUtils;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.geoboundary.Boundary;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.DateFormat;
import com.shatam.utils.MXStates;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;
import com.shatam.utils.Util;
import com.tequila.address.MiCodigoPostal;
import com.tequila.address.StateReader;
import com.tequila.address.TuCodigoPostal;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class CSVCorrectorInValidFormat implements TextFormat{
	
//	private String inputFile = "/home/shatam-10/MexicoCache/Cache/Inegi_Information_4000 (charumam).csv";
	private String inputFile = "/home/shatam-100/MexicoCache/Cache/Inegi_Information_0_224000_190000_200000_F1_From_0-2300.csv";

	List<String[]>  readLines = null;
	
	private CSVCorrectorInValidFormat(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(inputFile));  //,'\t'
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log("Load input file...... Done");
//		U.log("Count of record with header ::"+readLines.size());
		U.log("Count of record (without header)  ::"+(readLines.size()-1));
	}
	
	public static void main(String[] args) {
		CSVCorrectorInValidFormat correct = new CSVCorrectorInValidFormat();
//		correct.loadReadFile();
		//correct.readFile();
		
//		correct.validateRows();
		
		correct.correctNewlyRecords(true);
//		correct.correctionAtAddress(true);
//		correct.correctCityIntoStandardFormat(false);
	
//		correct.findCorrectCityStateZip(false);
//		correct.findMissingAddress();
//		correct.findMissingCity();
//		correct.findMunicipalitiesForMexicoCity(true);
		
//		correct.findUrls();
		
//		correct.extractAddressFromNeighbourhood();
		
//		correct.validateStateZipUsingBoundary();
		
//		correct.validateCity(false);
//		correct.validateCity(true);

//		correct.validateBoundary(true);
//		correct.validateBoundaryForDmpData(false);
//		correct.validateBoundary(false);
//		correct.findIncorrectIndustrySector();

//		correct.findCorrectStateFromAbbrAndZip(true);

//		correct.separateCompanyRecordForBBB(true);
//		correct.checkCompanyStandard();
//		U.log("Company Name : "+correct.getCompanyNameStandardised("Lacteos Y Recria San Angel Sc De Rl"));
		
//		correct.correctAddressFromatAtCsv(8, true);
//		U.log(getUniqueNumber("442-100-1062;442-100-6000 Ext. 79132;442 100-10-62;442 100-10 44"));
	}
	
	/**
	 * This method is used to changes the address into standardized format.
	 * @param index for particular address column number.
	 * @param status for write new modified csv file. If true, it will create new file. For false, nothing happen.
	 */
	void correctAddressFromatAtCsv(int index, boolean status){
		int x = 0;
		for(String[] lines : readLines){
			if(x++ == 0)continue;
			if(!lines[index].trim().isEmpty()){
				lines[index] = TextFormat.getAddressStandardised(lines[index]);
			}
		}
		if(status){
			U.writeCsvFile(readLines, inputFile.replace(".csv","_Correct_Add.csv"));
		}
	}
	

	void correctCityIntoStandardFormat(boolean status){
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
//				U.log(x+": Row   "+lines[10]);
				if(cities.contains(U.toTitleCase(lines[10].trim()))){
					lines[10] = U.toTitleCase(lines[10].trim());
				}else if(cities.contains(U.toTitleCase(TranslateEnglish.convertToEnglish(lines[10].trim())).replace(" - ", "-"))){
					lines[10] = U.toTitleCase(TranslateEnglish.convertToEnglish(lines[10].trim())).replace(" - ", "-");
				}else{
					throw new IllegalArgumentException("# Row :"+x+" # Index :"+lines[0]+" City is not correct for this state "+lines[11]+"\t\tCity :"+lines[10].trim());
				}

			}//eof if
		
//			if(status)U.writeCsvFile(readLines, inputFile.replace(".csv", "_STD_CITY.csv"));
	}
	
	/**
	 * This method is used to find city is within a given state.
	 * @param inputCity
	 * @param state
	 * @return :- return true if given city is exist within state
	 */
	public static boolean isExistCityWithinState(String inputCity, String state){
		if(state.equals("Baja California")&&inputCity.equals("San Quintin")) {
			return true;
		}//Gómez Farí­as	Jalisco
		if(state.equals("Jalisco")&&inputCity.equals("Gomez Farias")) {
			return true;
		}
		if(state.equals("Mexico State")&&inputCity.equals("Ciudad Lopez Mateos")) {
			return true;
		}
		Set<String> cities = StateReader.getAllCities(state.trim());
		if(cities == null){
			throw new IllegalArgumentException("Given state is not exist, input state is "+state);
		}
		if(cities.contains(U.toTitleCase(inputCity.trim()))){
			return true;
		}else if(cities.contains(U.toTitleCase(TranslateEnglish.convertToEnglish(inputCity.trim())).replace(" - ", "-"))){
			return true;
		}
		return false;
	}
	
/*	void readFile(){
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			U.log(Arrays.toString(lines));
			U.log(lines.length);
			if (x==10)break;

			lines[8] = lines[8].trim().replaceAll(",$|\\.$|^,|^ |-$|^ |^ ", "").replace(", ,", ",").replace(" – ", "-").replaceAll("\\s{2,}", " "); // address
			lines[9] = lines[9].trim().replaceAll("\\.$|,$|^,|^ |^-|^ ", "").replace(" – ", "-").replaceAll("\\s{2,}", " ")
					.replace("Col:", "Col.");  //neighbourhood
		
		}
	}*/
	


/*	void validateRows(){
		
//		HashMap<String,String> emailMap = getUrlEmails();
		
		int index = 0;
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;

			lines[0] = String.valueOf(index++);
//			U.log(lines[4].trim());
			String [] sicData = U.extractSICInfo(lines[4].trim());
			lines[1] = sicData[0]; //industry sector
			lines[2] = sicData[1]; //spanish industry sector
			lines[3] = sicData[3]; //major sic code
			lines[5] = sicData[5]; //product description
			lines[6] = sicData[6]; //spanish product description
						
			lines[8] = lines[8].trim().replaceAll("\\s{2,}", " ").replaceAll(":|\\.$|,$|^,|^ |-$|^ |^ ", "").replace(", ,", ",").replaceAll(" – | - ", "-")
					.replaceAll("\\.$", "").replace(" ,", ","); // address
			lines[9] = lines[9].trim().replaceAll("\\s{2,}", " ").replaceAll(":|\\.$|,$|^,|^ |^-|^ ", "").replaceAll(" – | - ", "-")
					.replace("Col:", "Col.");  //neighbourhood
			
			if(!lines[13].isEmpty())lines[13] = correctPhoneNum(lines[13]);

//			if(!lines[15].isEmpty()){ //url
//				if(emailMap.containsKey(lines[15].trim())){ 
//					lines[16] = emailMap.get(lines[15].trim()); //email
//				}
//			}
			
			U.stripAll(lines);

		}
		U.log("Validate rows..... Done");
	}
*/	
	void correctNewlyRecords(boolean status){
		Map<String, String> correctStateMap = loadStateMap();
		
		int index = 0;
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if(Arrays.toString(lines).contains("�"))continue;
			if (x++ == 0) continue;

//			lines[0] = String.valueOf(index++);
//			U.log(lines[4].trim());
			
			if(lines[4].trim().length() == 3){
				lines[4] = "0"+lines[4].trim();
			}
//			U.log(lines[4]);
			String [] sicData = U.extractSICInfo(lines[4].trim());
			lines[1] = sicData[0]; //industry sector
			lines[2] = sicData[1]; //spanish industry sector
			lines[3] = sicData[3]; //major sic code
			lines[5] = sicData[5]; //product description
			lines[6] = sicData[6].replace("Nuevos Y Usados ​​Concesionarios De Coches Y Camiones", "Nuevos Y Usados Concesionarios De Coches Y Camiones"); //spanish product description
//			U.log(lines[1]);U.log(lines[2]);U.log(lines[3]);U.log(lines[4]);U.log(lines[5]);U.log(lines[6]);
			lines[7] = TextFormat.getCompanyNameStandardised(TranslateEnglish.convertToEnglish(lines[7]).trim().replaceAll("\\s{2,}", " ").replaceAll(" – | - ", "-")
					.replace("&amp;", "&")); // company name
//			if(!lines[7].toLowerCase().contains("constructora inmobiliaria regina"))continue;
/*			lines[8] = lines[8].trim().replaceAll("\\s{2,}", " ").replaceAll(":|;|,$|^,|^ |-$|^ |^ ", "").replace(", ,", ",").replaceAll(" – | - ", "-").replace(" / ", "/")
					.replace("# ", "#").trim().replaceAll("\\.$", ""); // address
*/			
			lines[8] = TextFormat.getAddressStandardised(TranslateEnglish.convertToEnglish(lines[8]));
			lines[9] = TranslateEnglish.convertToEnglish(lines[9]).trim().replaceAll("\\s{2,}", " ").replaceAll(":|;|\\.$|,$|^,|^ |^-|^ ", "").replaceAll(" – | - ", "-")
					.replace("Col:", "Col.").trim().replaceAll("  *", "").replaceAll("\\.$| $", "");//neighbourhood
			lines[10]=TranslateEnglish.convertToEnglish(lines[10]);
			
			lines[11] = correctStateMap.get(lines[11]); // state
			
			if(!lines[13].isEmpty()){
				lines[13] = TextFormat.getUniqueNumber(lines[13]);
			}
			if(!lines[14].isEmpty()){
				lines[14] = TextFormat.getUniqueNumber(lines[14]);
			}
			
			lines[15] = lines[15].trim().replaceAll("//$", "/");  //url
			
			lines[16] = uniqueValuesFromString(lines[16].trim().toLowerCase());
			if(!lines[17].isEmpty()){
				lines[17] = U.toTitleCase(lines[17]);
			}
			if(!lines[18].isEmpty()){
				lines[18] = U.toTitleCase(lines[18]);
			}
			U.log(Arrays.toString(lines));
			U.stripAll(lines);
		}
		
		U.log("Validate rows..... Done");
		if(status){
			U.writeCsvFile(readLines, inputFile.replace(".csv", "_CORRECT_NW_REC.csv"));
		}
	}
	
	
	void extractAddressFromNeighbourhood(boolean status){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		
		String match = null;
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
//			U.log(Arrays.toString(lines));
//			U.log(lines.length);
//			if (x==250)break;

			lines[8] = lines[8].trim().replaceAll(",$|\\.$|^,|^ |-$|^ |^ ", "").replace(", ,", ",").replace(" – ", "-").replaceAll("\\s{2,}", " "); // address
			lines[9] = lines[9].trim().replaceAll("\\.$|,$|^,|^ |^-|^ ", "").replace(" – ", "-").replaceAll("\\s{2,}", " ")
					.replace("Col:", "Col.");  //neighbourhood
			
			if(lines[9].contains("Entre")){
				match = Util.match(lines[9], "Entre .*");
			}
			if(lines[9].contains("Esq.")){
				match = Util.match(lines[9], "Esq\\. .*");
			}
			if(lines[9].trim().contains("Zona Hotelera")){
				match = Util.match(lines[9], "Zona Hotelera( Sur)?");				
			}
			if(match != null){
				lines[8] = lines[8]+", "+match.trim();
				lines[9] = lines[9].replace(match, "").trim();
				U.log(match);
//				U.log(Arrays.toString(lines));
			}
			match = null;
	//		U.log(Arrays.toString(lines));
		}
		if(status)U.writeCsvFile(readLines, inputFile.replace(".csv", "_EXT_ADD_COL.csv"));
	}
	
	
	/**
	 * This method is used to format adddress at Address Field
	 */
/*	void correctionAtAddress(boolean status){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		
		while (it.hasNext()) {
			lines= it.next();
			if (x++ == 0) continue;
			//Carretera 
			lines[8] = lines[8].trim()
					.replaceAll("Boulevard\\.? |Blvd |^Blv\\.? |^Bvld\\. |Boulevar |Bldv\\. |Blvrd\\.? |Boulebard |Boulevar(d)? |Blvr\\.? |B(o|u)levard ", "Blvd. ")
					.replaceAll("Avenida |Ave(\\.|,)? |^Av |^Ave |^Avda | avenida |^Av, ", "Av. ").replaceAll(",\\s?Av ", ", Av. ")
					.replaceAll("Esquina |Esq |Esq\\.", "Esq. ").replace(" Esq", ", Esq" )
					//.replaceAll(" No | No\\.", " No. ")
					.replaceAll(" Sn | Sn$| Sin$| Sin N(ú|u)mero", " S/N")
					.replaceAll(" Carr |(c|C)arr(e)?\\. |Carret\\. |^Carr |^Carr(,)? | Ctra\\. ", " Carretera ").replace("Carr. ", "Carretera ")
					.replaceAll("Clz\\. |Calz\\.? | Claz\\. |^Calz |^calz\\. |Clzd\\. ", "Calzada ")
					.replace(" C/Le ", " Calle ").replace("Cll. ", "Calle ")
					.replaceAll("C/ | C/", "Calle") //|^C. | C. 
					.replaceAll(" Km | Km\\. | Km\\.|^Km(\\.)? | Kilometer | Kil(ó|o)metro ", " K.M. ")
					.replace("..", ".")
					.replaceAll("Cto\\. ", "Circuito ")
					.replace("Prol. ", "Prolongacion ")
					.replaceAll(" Lt(\\.)?| Lte\\.? ", " Lt. ") //Lote
					.replaceAll(" Mz\\.| Mz | Mza | Mzn\\.? | Maz ", " Mz. ") //Manzana
					.replace(" Int |Int\\.", " Int. ")
					.replaceAll("Loc\\.| Loc ", " Loc. ")
					.replaceAll(" Local |,Local", ", Local ")
					.replace("Av.", "Av. ")
					.replaceAll(" Num | Num\\. | No\\.", " No. ")
					.replace(",,", ",")
					.replace(" Y, | y, ", " Y ")
					.replaceAll("^Ctro\\. ", "Centro ").replace(" ,", ",")
					.replaceAll(" Smza | Sm | S\\.M\\. | Supermza | sm | Súper Manzana | Smz ", " Supermanzana ")
	//				.replaceAll("Ote(\\.)?", "Oriente").replaceAll("Nte\\.?", "Norte").replaceAll("Pte\\.?", "Poniente")
					.replaceAll("^C\\.(c|C)\\. |^Cc ", "Centro Comercial ")
					.replace(" Entre ", ", Entre ")
//					.replace(" NoS/N", " No S/N")
					.replaceAll("\\s{2,}", " ").replace(" ,", ",").replace(" # ", " #").replace("( ", "(").replace(" )", ")")
					.replace(", No. ", " No. ").replace(",,", ",")
					.trim();
			
			String str = Util.match(lines[8], " No \\d+");
			if(str != null)
				lines[8] = lines[8].replace(str, str.replace("No ", "No. "));
			
			//Prolongación
			
//			lines[9] = lines[9].trim().replaceAll("^Col ", "Col. ");
//			if(!lines[9].trim().contains("Col") || !lines[9].trim().contains("Fracc") || !lines[9].trim().contains("Barrio")){
//				if(!lines[9].trim().isEmpty() || lines[9].trim().length() > 4)
//					lines[9] = "Col. "+lines[9].trim();
//			}
		}
		
		if(status){
			U.writeCsvFile(readLines, inputFile.replace(".csv", "_CORRECT_ADD.csv"));
		}
	}*/
	
	void findMunicipalitiesForMexicoCity(boolean status){
		Map<String, String> correctStateMap = loadStateMap();
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			lines[11] = correctStateMap.get(lines[11]); // state
			
			if(lines[12].trim().isEmpty())continue; //zip
			

			if(lines[11].trim().equals("Mexico City")){
				String city = MXStates.getMexicoCityMunicipalites(lines[12]);				
				if(city == null){
					throw new IllegalArgumentException("Zip is wrong at Row :"+x+"\tIndex :"+lines[0]+"\t>> Found zip is : "+lines[12]+"\t>> Found city is : "+lines[10]);
				}

				if(!TranslateEnglish.convertToEnglish(U.toTitleCase(lines[10])).equals(TranslateEnglish.convertToEnglish(U.toTitleCase(city)))){
					U.log("Found City :"+lines[10]+"\tMunicipality :"+city+"\tZip :"+lines[12]+"\t\tRow :"+x);
					if(status){
						lines[10] = U.toTitleCase(city);
//						lines[10] = TranslateEnglish.convertToEnglish(U.toTitleCase(city));
					}
				}
			}
		}
		
		if(status){
			U.writeCsvFile(readLines, inputFile.replace(".csv", "_ADD_MNC_MEX_CITY.csv"));
		}
	}
	
	
	void validateCity(boolean modifiedAtFile){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		int match = 0;
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			
			String city = MiCodigoPostal.getCity(lines[12]);
			if(city == null)
				city = TuCodigoPostal.getCity(lines[12]);
			
			if(city != null && city.contains("Centro"))
				throw new IllegalArgumentException("This City is incorrect for zip "+lines[12]);
			
			if(city == null){				
				U.errLog(lines[10]+"::\t\t"+lines[12]);
			}else{
				match = FuzzySearch.partialRatio(lines[10], city);
				if(match < 70){
					U.log("Original city name ::"+lines[10]+"\tNew city name ::"+city+"\t\tMatch ::"+match+"\t\tIndex ::"+lines[0]+"\t\tRow::"+x);
				}
				if(modifiedAtFile){
					lines[10] = city.replaceAll("\\(.*?\\)", "");
				}
//				U.log(lines[10]+"::\t\t"+city);
			}
			
		}
		
		if(modifiedAtFile){
			U.writeCsvFile(readLines, inputFile.replace(".csv", "_VALIDATE_CITY.csv"));
		}
	}
	
	void validateStateZipUsingBoundary(){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();

		U.log("Invalid Boundaries");
		while (it.hasNext()) {
			lines= it.next();
			if (x++ == 0) continue;
			
			if(!lines[22].trim().isEmpty() && !lines[23].trim().isEmpty()){
//				U.log(lines[11].trim());
				try{
					if(!Boundary.boundaryCheckForState(lines[11].trim(), lines[22].trim(), lines[23].trim(), COUNTRY)){
						U.log("Index ::"+lines[0]+"\t\tRow::"+x+"\t\tWrong State : "+lines[11]+"\t\tWrong zip : "+lines[12]);	
					}
				}catch(Exception e){
					throw new IllegalArgumentException("Current row is having problem. Row is "+x);
				}
			}
		}//eof while
		U.log("Done");
	}//eof validateBoundary()
	
	
	void validateBoundary(boolean status){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();

		U.log("Invalid Boundaries");
		while (it.hasNext()) {
			lines= it.next();
			if (x++ == 0) continue;
			
			if(!lines[22].isEmpty() && !lines[23].isEmpty()){
//				U.log(lines[11].trim());
				try{
				if(!Boundary.boundaryCheckForState(lines[11].trim(), lines[22], lines[23], COUNTRY)){
					U.log("Row::"+x+"\t\tIndex ::"+lines[0]);
					if(status){
						lines[22] = lines[23] = "";
					}
				}
				}catch(Exception e){
					throw new IllegalArgumentException("Current row is having problem. Row is "+x);
				}
			}
		}//eof while
		U.log("Done");
		if(status){
			U.writeCsvFile(readLines, inputFile.replace(".csv", "_VAL_BOUNDARY.csv"));
		}
	}//eof validateBoundary()
	
	void validateBoundaryForDmpData(boolean status){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();

		U.log("Invalid Boundaries");
		while (it.hasNext()) {
			lines= it.next();
			if (x++ == 0) continue;
			
			if(!lines[24].isEmpty() && !lines[25].isEmpty()){
				U.log(lines[11].trim());
				if(!Boundary.boundaryCheckForState(lines[11].trim(), lines[25].trim(), lines[24].trim(), COUNTRY)){
					U.log("Index ::"+lines[0]+"\t\tRow::"+x);
					if(status){
						lines[24] = lines[25] = "";
					}
				}
			}
		}//eof while
		if(status){
			U.writeCsvFile(readLines, inputFile.replace(".csv", "_VAL_BOUNDARY.csv"));
		}
	}//eof validateBoundary()
	
	
	public static String uniqueValuesFromString(String val){
		String [] vals = null;
		if(val.contains(";")){
			vals = val.split(";");
			if(vals != null && vals.length > 1){
				vals = uniqueValues(vals);
				val = String.join(";", vals);
			}
		}
		if(val.contains(",")){
			vals = val.split(",");
			if(vals != null && vals.length > 1){
				vals = uniqueValues(vals);
				val = String.join(";", vals);
			}
		}
		return val;
	}
	
	private static String[] uniqueValues(String[] vals){
		U.stripAll(vals);
		Set<String> set = new HashSet<>(Arrays.asList(vals));
		return (String[]) set.toArray(new String[set.size()]);
	}
	
/*	void findMissingAddress(){
		int index = 0;
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			
			if(lines[8].isEmpty()){
				U.errLog("Index ::"+(x-2));
			}
		}
		U.log("Validate missing address at rows..... Done");
	}
	
	void findMissingCity(){
		int index = 0;
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			
			if(lines[10].isEmpty()){
				U.errLog("Index ::"+(x));
			}
		}
		U.log("Validate city address at rows..... Done");
	}
*/	
	Map<String, String> loadStateMap(){
		Map<String, String> correctStateMap = new HashMap<>();
		Set<String> stateSet = new HashSet<String>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			if (lines[11].trim().length()<3) {
				U.log(Arrays.toString(lines));
				U.log(lines[0]+" ->"+lines[11]);
				
			}
			stateSet.add(lines[11].trim());  //state
		}
		
		for(String state : stateSet){
			
			U.log(state);
			
			String correctFormat = U.matchState(state);
			if(correctFormat.equals("-")){
				throw new IllegalArgumentException("State name is not found for "+state);
			}
			U.log(state+"  "+correctFormat);
			correctStateMap.put(state, correctFormat);
		}
		return correctStateMap;
	}
	

	HashMap<String,String> loadCorrectYearsInBiz(){
		
		HashMap<String,String> correctDateMap = new HashMap<>(); 
		HashSet<String> dateSet = new HashSet<>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {
			lines= it.next();
			
			if (x++ == 0) continue;
			if(!lines[23].isEmpty()){
				dateSet.add(lines[23].trim());
			}
		}
		
		String newDate = null;
		for(String inputDate : dateSet){
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
	/**
	 *	This method is used to find if records has correct city, state and zip.<br>
	 * 	Create separated wrong & correct records csv file. 
	 * @param status
	 * <br>: status is true then Create separated wrong & correct records csv file<br> 
	 * if false, then only print counts.
	 */
	public void findCorrectCityStateZip(boolean status){
		List<String[]> wrongDataset = new ArrayList<>();
		int index = 0, count=0;
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0){
				wrongDataset.add(lines);
				continue;
			}
			if(!U.isValidCityStateZip(lines[12], lines[10], lines[11])){
				wrongDataset.add(lines);
				it.remove();
				index++;
			}else{
				count++;
			}
		}
		if(status){
			U.writeCsvFile(readLines, inputFile.replace(".csv", "_Separated_Correct_Data.csv"));
			U.writeCsvFile(wrongDataset, inputFile.replace(".csv", "_Separated_Wrong_Data.csv"));
		}
		U.log("Count of record that has wrong either city, state or zip  ::"+index);
		U.log("Count of record that has correct city, state and zip  ::"+count);
	}
	
	void findIncorrectIndustrySector(){
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			
			if(!lines[1].isEmpty()){
				if(lines[1].trim().contains("Wholesale Trade-Nondurable Goods")){
					U.log(Arrays.toString(lines));
				}
				if(lines[1].trim().contains("Wholesale Trade-Durable Goods")){
					U.log(Arrays.toString(lines));
				}
			}	
		}
	}
	
	void separateCompanyRecordForBBB(boolean status){

		U.log("Total ::"+readLines.size());
		List<String[]> dataset1 = new ArrayList<>();
		List<String[]> dataset2 = new ArrayList<>();
		List<String[]> writeLines = new ArrayList<>();
		int i = 0;
		for(String lines[] : readLines){
//			if(i++ == 10)break;
			if(lines[7].contains("####")){
				String[] companyNames = lines[7].trim().split("####");
//				U.log(companyNames.length+"\t"+Arrays.toString(companyNames));
				
				for(String name : companyNames){
					List<String> dataset = Arrays.asList(lines);
					dataset.set(7, name);
					dataset1.add(dataset.toArray(new String[dataset.size()]));
				}
				
//				dataset1.addAll(separateCompanyRecord(companyNames, lines));
				/*
				for(int j = 0; j< companyNames.length; j++){
					String[] vals = lines;
					vals[7] = companyNames[j];
					if(!dataset1.contains(vals))
						dataset1.add(vals);
					U.log(Arrays.toString(vals));

				}*/
/*				for(String name : companyNames){
//					U.log(companyName);
					;
					vals[7] = name;
//					U.log(Arrays.toString(vals));
//					i++;
				}
*/				
			}else if(!lines[7].contains("####")){
//				U.errLog(Arrays.toString(lines));			
				dataset2.add(lines);
//				i++;
			}
		}
		writeLines.addAll(dataset1);
		writeLines.addAll(dataset2);
		
		U.log("Total new record ::"+writeLines.size());
		
/*		int x = 0;
		for(String lines[] : writeLines){
			if(x++ == 5)break;
			U.log(Arrays.toString(lines));
		}*/
		if(status){
			U.writeCsvFile(readLines.get(0), writeLines, inputFile.replace(".csv", "_Separate_CMP_NM.csv"));
		}
		
	}
	
	Collection<String[]> separateCompanyRecord(String[] companyName, String[] lines){
		Map<String,String[]> map = new HashMap<>();
		for(String name : companyName){
			List<String> dataset = Arrays.asList(lines);
			dataset.set(7, name);
			map.put(name, dataset.toArray(new String[dataset.size()]));
		}
		return map.values();
	}
	
	void findCorrectStateFromAbbrAndZip(boolean status){
		Set<String> stateAbbrSet = new HashSet<>();
		Map<String, String> stateAbrrMap = new HashMap<>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			if(lines.length> 26){
				U.log(Arrays.toString(lines));
			}
			
			if(!lines[11].isEmpty()){
				if(lines[11].trim().length() > 3){
					U.log(lines[0]+"\t\tRow :"+x);
				}
				stateAbbrSet.add(lines[11].trim());
			}	
		}//eof while
		for(String stateAbbr : stateAbbrSet){
			String state = MXStates.getFullNameFromAbbr(stateAbbr);
			U.log(stateAbbr+"\t\t\t"+state);
			stateAbrrMap.put(stateAbbr, state);
		}
		x = 0;
		
		it = readLines.iterator();
		String[] data;
		while (it.hasNext()) {			
			data = it.next();
			if (x++ == 0) continue;
			if(data[12].trim().length() ==4){
				data[12] = "0"+data[12].trim();
			}
			String state  = FindStateFromZip.findStateFromZip(data[12].trim());
			if(state == null)U.log("state not found :"+data[12]);
			if(!stateAbrrMap.get(data[11].trim()).equals(state)){
				//U.log(Arrays.toString(data));
				U.log("Zip ::"+data[12]+"\t\tFrom Zip, State is :"+state+"\t\tFrom Abbr, State is ::"+stateAbrrMap.get(data[11].trim()));
			}
			if(status){
				data[11] = stateAbrrMap.get(data[11].trim());
			}
		}//eof while
		
		if(status){
			U.writeCsvFile(readLines, inputFile.replace(".csv", "_CORRECT_STATE_ABBR_ZIP.csv"));
		}
	}
	
	
/*	void findUrls(){
		HashSet<String> urlSet = new HashSet<>();
		
		int index = 0;
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0) continue;
			
			if(!lines[15].isEmpty()){
				
				if(urlSet.add(lines[15])){
					U.log(lines[15]);
				}
			}	
		}
		
		U.log("Validate missing address at rows..... Done");
	}*/
	
/*	void writeFile(){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(inputFile.replace(".csv", "_Correct.csv")),',');
			writer.writeAll(readLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log("Writing validate file.....Done");
	}
	
	void writeFile(String fileName){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(fileName),',');
			writer.writeAll(readLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log("File is created on location ::"+fileName);
	}*/
	
    
/*    HashMap<String,String> getUrlEmails(){
    	HashMap<String,String> emailMap =  new HashMap<>();
    	
    	emailMap.put("https://www.viajesvivatours.com/","agencias@viajesvivatours.com");
    	emailMap.put("https://www.europamundo.com","redes@europamundo.com");
    	emailMap.put("https://www.bestday.com.mx/","redessociales@bestday.com");
    	emailMap.put("https://www.pricetravel.com.mx","contact@pricetravel.com");
    	emailMap.put("https://aeromexico.com","customercare@aeromexicovacations.com");
    	emailMap.put("https://www.megatravel.com.mx/","info@megatravel.com.mx");
    	emailMap.put("http://mundojoven.com/","admin@mundojoven.com");
    	emailMap.put("https://www.viajespalacio.com.mx/","vpjefeinsurgentes@ph.com.mx");
    	emailMap.put("https://www.viajeselcorteingles.com.mx/","ventasadistancia@viajeseci.com.mx");
    	emailMap.put("http://www.exceltours.com.mx/","contacto@exceltours.com.mx,social.media@exceltours.com.mx");
    	emailMap.put("https://www.interjet.com","customer.service@interjet.com.mx");
    	emailMap.put("http://www.euromundoenlineamx.com.mx","soporteycalidadagencies@euromundo.com.mx");
    	emailMap.put("http://www.rsviajes.com/","ventas@rsviajes.com");
    	emailMap.put("http://www.viajesalto.com/","info@viajesalto.com");
    	
    	return emailMap;
    }*/

}
