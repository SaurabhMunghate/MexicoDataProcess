package com.tequila.data;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.opencsv.CSVReader;
import com.shatam.conversion.Naisc;
import com.shatam.conversion.NaiscToSicConversion;
import com.shatam.conversion.Sic;
import com.shatam.scrapper.PreparedCSV;
import com.shatam.utils.U;

import me.xdrop.fuzzywuzzy.Applicable;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.ExtractedResult;

public class SiemData {
	
	private static final String FILE_NAME = "/home/glady/MexicoCache/Tequila_DATA/pek-mexico/Pek_SCIAN_Code_List.csv";

	
	private static final String TRANSLATE_SP_TO_EN_FILE = "/home/glady/MexicoCache/Tequila_DATA/translateData/spanishToEnglish_Translate.csv";
	
	public static void main(String[] args) {
		loadReadFile();
//		compare();
		compareManyMatchesNaisc();
		
//		compareNotMatchesNaisc();
		
//		createTranslateFile();
	}
	
	static void compareNotMatchesNaisc(){
//		List<String[]> writeLines = new ArrayList<>();
//		String[] header = {"NAISC","PRODUCT_DESC_ENGLISH","PRODUCT_DESC_SPANISH","SIC","SIC_DESC_ENGLISH"};
		
		//load unique naise
		HashSet<String> naiscSet = loadNaisc();
		U.log("size::"+naiscSet.size());
		
		//init NaiscToSicConversion data
		NaiscToSicConversion.init();
		//init Naisc data
		Naisc.init();
		
		//load naisc data
		HashMap<String,String> naiscMap = NaiscToSicConversion.getNaiscMap();

		
		HashMap<String, String[]> naiscSixMap = Naisc.getSixDigitMapAll();
		
		HashSet<String> spanishDescSet = loadNaiscSpanishDesc();

		int i = 0;
		for(String naisc : naiscSet){
/*			if(naiscMap.containsKey(naisc)){
				U.log(naisc);
			}
*/
			if(naiscSixMap.containsKey(naisc)){
//				writeLines.add(new String[]{naisc,naiscSixMap.get(naisc)[1],naiscSixMap.get(naisc)[0]});
				if(spanishDescSet.contains(naiscSixMap.get(naisc)[0])){
					U.log(naisc +"\t"+Arrays.toString(naiscSixMap.get(naisc)));					
				}else{
					U.errLog(naisc +"\t"+Arrays.toString(naiscSixMap.get(naisc)));
				}
				i++;
			}
		}
		
//		U.writeCsvFile(header, writeLines, "/home/glady/MexicoCache/Tequila_DATA/naisc_to_sic/NAISC_TO_SIC_CROSS_1.csv");
		U.log(spanishDescSet.size());
		
	}
	
	
	static void compareManyMatchesNaisc(){
		//init NaiscToSicConversion data
		NaiscToSicConversion.init();
		//init Naisc data
		Naisc.init();
				
		//load unique naise
		HashSet<String> naiscSet = loadNaisc();
		U.log("size::"+naiscSet.size());
		
		//load sic data
		HashMap<String,String> sicMap = NaiscToSicConversion.getSicMap();
		
		//load naisc data
		HashMap<String,String> naiscMap = NaiscToSicConversion.getNaiscMap();

		int i = 0;
		Collection<String> collectionSic = null;
		List<ExtractedResult> result_4D = null;
		List<ExtractedResult> result_6D = null;
		HashMap<String,String> sicDesc = new HashMap<>();
		
		HashMap<String,String> correctResultMap = correctResultMap();
		
		HashMap<String,String> naiscToSicMap = new HashMap<>();
		
		for(String naisc : naiscSet){
			
			//get sic list related to naisc
			collectionSic = NaiscToSicConversion.getSicForNaisc(naisc);
			
			if(collectionSic != null){ // && collectionSic.size() ==2
				U.log((++i)+"] ::");
				
//				if(i != 11)continue;
				
				//get naisc 4 digit description
				String [] data = Naisc.getNaiseFourDesc(naisc);
				if(data != null){
					U.log("naisc four digit desc ::"+data[1]); //Arrays.toString(data));
				}else{
					U.errLog("naise four digit desc not find");
				}
				
				if(naiscMap.containsKey(naisc)){
					U.log("naisc Six digit desc ::"+naiscMap.get(naisc));
				}
				U.log(naisc+"\t"+collectionSic);

				for(String sic : collectionSic){
					if(sicMap.containsKey(sic)){
						U.log("Sic ::"+sic+"\tsic desc ::"+sicMap.get(sic));
						sicDesc.put(sicMap.get(sic),sic);  //desc, sic
					}
				}
				
				//Fuzzy String matching
				result_4D = FuzzySearch.extractSorted(data[1],sicDesc.keySet(),80);
				result_6D = FuzzySearch.extractSorted(naiscMap.get(naisc),sicDesc.keySet(),80);
				
				U.log("4_digit_naise::"+result_4D);
				U.log("6_digit_naise::"+result_6D);
				String res = score(data[1], result_4D, naiscMap.get(naisc), result_6D, sicDesc);
				if(correctResultMap.containsKey(naisc)){
					res = correctResultMap.get(naisc);
				}
				U.log("final outout::"+res);
				if(res != null){
					naiscToSicMap.put(naisc, res);
				}
				sicDesc.clear();
			}else{
//				U.errLog(naisc);
			}
		}
		
		U.log("Naisc to sic store map size ::"+naiscToSicMap.size());
		
//		separateMoreMatchesSicCode(naiscToSicMap);
	}
	
	static HashMap<String,String> correctResultMap(){
		HashMap<String,String> map = new HashMap<>();
		map.put("313210", null);//empty
		map.put("311423", "2099");
		map.put("322299","2679");
		map.put("333249","3559");
		map.put("238290","1799");
		map.put("339113","3842");
		map.put("322122","2621");
		map.put("315210","2389");
		map.put("325180","2819");
		map.put("238910","1799");
		map.put("311812","2051");
		map.put("335999","3699");
		map.put("339930","3944");
		map.put("333243","3553");
		map.put("321920","2449");
		map.put("115112","0711");
		map.put("325220","2823");
		map.put("337110","2434");
		map.put("333999","3569");
		map.put("332999","3499");
		
		map.put("322220","2672");
		map.put("313310","2299");
		
		
		return map;
	}
	
	static String score(String naiscFourDesc, List<ExtractedResult> result_4D, String naiscSixDesc,List<ExtractedResult> result_6D, HashMap<String,String> sicDesc){
		
//		U.errLog("Reach here..."+result_4D.size()+"\t"+result_6D.size());

		if(result_4D.size()==1 && result_6D.size() ==0){
//			U.errLog("Reach here...1");

			ExtractedResult result = result_4D.get(0);
			return sicDesc.get(result.getString());
			
		}else if(result_4D.size()==0 && result_6D.size() ==1){
//			U.errLog("Reach here...2");
			ExtractedResult result = result_6D.get(0);
			return sicDesc.get(result.getString());
		}
		
		if(result_4D.size() > 1 && result_6D.size() == 0){
//			U.errLog("Reach here...3");
			String sic = getHighestScoreSic(result_4D, sicDesc);
			
			if(sic == null){
				sic = calculateByScoring(naiscFourDesc, result_4D, sicDesc);
				if(sic != null) return sic;
			}else return sic; 
			
		}else if(result_4D.size() == 0 && result_6D.size() > 1){
//			U.errLog("Reach here...4");
			String sic = getHighestScoreSic(result_6D, sicDesc);
			
			if(sic == null){
				sic = calculateByScoring(naiscSixDesc, result_6D, sicDesc);
				if(sic != null) return sic;
			}else return sic; 
			
		}
		if(result_4D.size() >= 1 && result_6D.size() >= 1){
//			U.errLog("Reach here...5");
			String sicForFour = getHighestScoreSic(result_4D, sicDesc);
			String sicForSix = getHighestScoreSic(result_6D, sicDesc);
			
//			U.log(sicForFour+"\t"+sicForSix);
			if(sicForFour != null && sicForSix != null){
				
				if(sicForFour.equals(sicForSix)){
					return sicForFour; //or sicForSix;
				}else{
					sicForFour = calculateByScoring(naiscFourDesc, result_4D, sicDesc);
					if(sicForFour != null) return sicForFour;
				}					
			}else if(sicForFour != null && sicForSix == null){
				return sicForFour;
			}
			else if(sicForFour == null && sicForSix != null){
				return sicForSix;
			}
		}
		if(result_4D.size() == 0 && result_6D.size() == 0){
			String sicForSix = getHighestScoreSic(naiscSixDesc, sicDesc);
			if(sicForSix != null) return sicForSix;
		}
		
		
		return null;
	}
	
	static String calculateByScoring(String naiscDesc, List<ExtractedResult> result, HashMap<String,String> sicDesc){
		
		List<Integer> resultAvgScore = new ArrayList<>();
		for(ExtractedResult res : result){
			resultAvgScore.add(getAvgScores(naiscDesc,res.getString()));
		}
		
		int maxAvg = Collections.max(resultAvgScore);
		List<Integer> dupAvgVal = resultAvgScore.stream().distinct().filter(entry -> Collections.frequency(resultAvgScore, entry) > 1).collect(Collectors.toList());
		
		if(!dupAvgVal.contains(maxAvg)){
			int index = resultAvgScore.indexOf(maxAvg);
			ExtractedResult res = result.get(index);
			return sicDesc.get(res.getString());
		}
		return null;
	}
	
	static String getHighestScoreSic(List<ExtractedResult> result, HashMap<String,String> sicDesc){
		
		List<Integer> score = new ArrayList<>();
		for(ExtractedResult res : result){
			score.add(res.getScore());
		}
		
		int max = Collections.max(score);
		List<Integer> dupVal = score.stream().distinct().filter(entry -> Collections.frequency(score, entry) > 1).collect(Collectors.toList());
		
		if(!dupVal.contains(max)){
			for(ExtractedResult res : result){
				if(max == res.getScore()){
					return sicDesc.get(res.getString()); 
				}
			}
		}		
		return null;
	}
	
	static String getHighestScoreSic(String naiscDesc, HashMap<String,String> sicDesc){
		
		HashMap<String,Integer> score = new HashMap<>();
		for(Entry<String, String> entry : sicDesc.entrySet()){
			score.put(entry.getKey(), getAvgScores(naiscDesc,entry.getKey()));
		}
		int max = Collections.max(score.values());
		List<Integer> dupVal = score.values().stream().distinct().filter(entry -> Collections.frequency(score.values(), entry) > 1).collect(Collectors.toList());
		
		if(!dupVal.contains(max)){
			for(Entry<String, Integer> entry : score.entrySet()){
				if(max == entry.getValue()){
					return sicDesc.get(entry.getKey()); 
				}
			}
		}		
		return null;
	}
	
	static int getAvgScores(String s1,String s2){
		List<Integer> scoreList = new ArrayList<>();
		
		scoreList.add(FuzzySearch.ratio(s1, s2));
		scoreList.add(FuzzySearch.partialRatio(s1, s2));
		scoreList.add(FuzzySearch.tokenSetRatio(s1, s2));
		scoreList.add(FuzzySearch.tokenSortRatio(s1, s2));
		scoreList.add(FuzzySearch.tokenSortPartialRatio(s1, s2));
		scoreList.add(FuzzySearch.weightedRatio(s1, s2));
		return (int) scoreList.stream().mapToInt(val -> val).average().getAsDouble();
		
	}

	
	static void compare(){
		int extactMatch = 0;
		int moreMatch = 0;
		int notFound = 0;
		
		HashMap<String,String> extactMatchMap =  new HashMap<>();
		HashSet<String> moreMatchSet =  new HashSet<>();
		HashSet<String> noMatchFoundSet = new HashSet<>();
		
		HashSet<String> naiscSet = loadNaisc();
		U.log("size::"+naiscSet.size());
		
		NaiscToSicConversion.init();
		
		HashMap<String,String> naiscMap = NaiscToSicConversion.getNaiscMap();
		HashMap<String,String> sicMap = NaiscToSicConversion.getSicMap();
		
		Collection<String> collectionSic = null;
		for(String naisc : naiscSet){
			
/*			if(naiscMap.containsKey(naisc)){
				U.log("naisc Six digit desc ::"+naiscMap.get(naisc));
			}
*/			
			collectionSic = NaiscToSicConversion.getSicForNaisc(naisc);
			if(collectionSic != null){
				if(collectionSic.size() == 1){
					extactMatch++;
					for(String sic : collectionSic){
						extactMatchMap.put(naisc, sic);
						
/*						if(sicMap.containsKey(sic)){
							U.log("naisc::"+naisc+"\tDesc::"+naiscMap.get(naisc)+"\tsic::"+sic+"\t\tsic desc ::"+sicMap.get(sic));							
						}
*/	
					}
				}else{
					moreMatch++;
					moreMatchSet.add(naisc);
				}
			}else{
				notFound++;
				noMatchFoundSet.add(naisc);
//				U.log(naisc);
			}
		}
		U.log("Extact Match Naise to Sic :::"+extactMatch);
		U.log("More Matches Naisc to Sic ::"+moreMatch);
		U.log("Not found Naisc at NaiscToSicMap ::"+notFound);
		U.log("extactMatchMap ::"+extactMatchMap.size());
		U.log("moreMatchSet ::"+moreMatchSet.size());
		U.log("noMatchFoundSet ::"+noMatchFoundSet.size());
		
/*		for(Entry<String, String> entry : extactMatchMap.entrySet()){
//			U.log(entry.getKey()+"\t"+entry.getValue());
			String[] data = Sic.sicInfo(entry.getValue());
			if(data == null){
				U.log("Not found sic ::"+entry.getValue());
			}
		}
*/		
/*		try {
			separatedNonEmptyCompanyName(extactMatchMap, moreMatchSet, noMatchFoundSet);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
	}
	
	static List<String[]>  readLines = null;
	static void loadReadFile(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(FILE_NAME));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log("Load input file...... Done");
	}
	
	
	static HashSet<String> loadNaisc(){
		HashSet<String> naiscSet = new HashSet<>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {
			lines= it.next();
			if (x++ == 0) continue;
			if(lines[4].trim().isEmpty()){
				U.log(Arrays.toString(lines));
			}
			naiscSet.add(lines[4].trim());
		}
		return naiscSet;
	}
	
	static HashSet<String> loadNaiscSpanishDesc(){
		HashSet<String> naiscSet = new HashSet<>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {
			lines= it.next();
			if (x++ == 0) continue;
			if(lines[6].trim().isEmpty()){
				U.log(Arrays.toString(lines));
			}
			naiscSet.add(lines[6].trim());
		}
		return naiscSet;
	}
	
	static HashMap<String,String> loadNaiscAndSpanishDesc(){
		HashMap<String,String> naiscSet = new HashMap<>();
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {
			lines= it.next();
			if (x++ == 0) continue;
			if(lines[4].trim().isEmpty() || lines[6].trim().isEmpty()){
				U.log(Arrays.toString(lines));
			}
			naiscSet.put(lines[4].trim(), lines[6].trim());
		}
		return naiscSet;
	}
	
	
	static void separateMoreMatchesSicCode(HashMap<String, String> naiscToSicMap){
		
		List<String[]>  readSicFoundLines = new ArrayList<>();
		List<String[]>  readSicNotFoundLines = new ArrayList<>();
		
		int x = 0;
		String[] data = null;
		String[] lines = null;
		
		Iterator<String[]> it = readLines.iterator();
		
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0){
//				U.log(Arrays.toString(lines));
				readSicFoundLines.add(lines);
				readSicNotFoundLines.add(lines);
				continue;
			}
			if(naiscToSicMap.containsKey(lines[4].trim())){
//				U.log(x);
				
				data = Sic.sicInfo(naiscToSicMap.get(lines[4].trim()));
					if(data != null){
						lines[1] = data[0]; //industry sector
						lines[2] = data[1]; //spanish industry sector
						lines[3] = data[3]; // major sic
						lines[4] = data[4]; // sic sub
						lines[5] = data[5]; //product desc
						lines[6] = data[6]; //spanish product desc
					}else{
						try {
							throw new Exception("Sic code not match");
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					readSicFoundLines.add(lines);
					it.remove();
			}else if(!naiscToSicMap.containsKey(lines[4].trim())){
				readSicNotFoundLines.add(lines);
			}
			
		}
		
//		PreparedCSV.writeFile(readLines, FILE_NAME.replace(".csv", "_SIC_Found_Naisc.csv"));
		PreparedCSV.writeFile(readSicFoundLines, FILE_NAME.replace(".csv", "_SIC_Found_with_desc.csv"));
		
		PreparedCSV.writeFile(readSicNotFoundLines, FILE_NAME.replace(".csv", "_SIC_Not_Found.csv"));
		
		U.log("File created here..");
	}
	
	static void separatedNonEmptyCompanyName(HashMap<String,String> extactMatchMap, HashSet<String> moreMatchSet, HashSet<String> noMatchFoundSet) throws Exception{
		List<String[]>  readMoreMatchesLines = new ArrayList<>();
		List<String[]>  readExactMatchLines = new ArrayList<>();
		List<String[]>  readExactMatchLinesWithSic = new ArrayList<>();
		
		String[] data = null;
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readLines.iterator();
		while (it.hasNext()) {

			lines= it.next();
			if (x++ == 0){
				readMoreMatchesLines.add(lines);
				readExactMatchLines.add(lines);
				readExactMatchLinesWithSic.add(lines);
				continue;
			}
			
			if(!lines[4].trim().isEmpty()){
				
				if(extactMatchMap.containsKey(lines[4].trim())){
					readExactMatchLines.add(lines);
					
					data = Sic.sicInfo(extactMatchMap.get(lines[4].trim()));
/*					if(data != null){
						lines[1] = data[0]; //industry sector
						lines[2] = data[1]; //spanish industry sector
						lines[3] = data[3]; // major sic
						lines[4] = data[4]; // sic sub
						lines[5] = data[5]; //product desc
						lines[6] = data[6]; //spanish product desc
					}else{
						throw new Exception("Sic code not match");
					}
					readExactMatchLinesWithSic.add(lines);
*/					it.remove();
				}else if(moreMatchSet.contains(lines[4].trim())){
					readMoreMatchesLines.add(lines);
					it.remove();
				}else if(!noMatchFoundSet.contains(lines[4].trim())){
					throw new Exception("Naisc code not match with NoMatchFoundSet");
				}
				
			}
		}
		PreparedCSV.writeFile(readLines, FILE_NAME.replace(".csv", "_Not_Found_Naisc.csv"));
		
		PreparedCSV.writeFile(readMoreMatchesLines, FILE_NAME.replace(".csv", "_More_Matches_Naisc.csv"));
		
		PreparedCSV.writeFile(readExactMatchLines, FILE_NAME.replace(".csv", "_Extact_Match_Naise.csv"));
		
//		PreparedCSV.writeFile(readExactMatchLinesWithSic, FILE_NAME.replace(".csv", "_Extact_Match_NaiseToSic.csv"));
		
		
		U.log("File created here..");
	}
	
	static List<String[]>  readTranslateLines = null;
	static void loadTranslateFile(){
		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(TRANSLATE_SP_TO_EN_FILE));
			readTranslateLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log("Load input file...... Done");
	}
	
	static void createTranslateFile(){
		
		loadTranslateFile();
		
		HashMap<String,String> spanishToEnglishMap = new HashMap<>();
		for(String lines[] : readTranslateLines){
			spanishToEnglishMap.put(lines[0].trim(), lines[1].trim());
		}
		
		List<String[]> outputLines = new ArrayList<>();
		
		HashMap<String,String> naiscDescMap = loadNaiscAndSpanishDesc();
		
		for(Entry<String, String> entry : naiscDescMap.entrySet()){
			outputLines.add(new String[]{entry.getKey(), entry.getValue(), spanishToEnglishMap.get(entry.getValue())});
		}
		
		PreparedCSV.writeFile(outputLines, TRANSLATE_SP_TO_EN_FILE.replace(".csv", "_Naisc.csv"));
	}
}
