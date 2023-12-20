package com.chinmay.test;

import java.io.StringWriter;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class ValidateExtractedZip {
	public static void main(String[] args) {
		String fileName="/home/mypremserver/DatabasesTequila/sawansir/MexicoCity_Incorrect_zip_HERE_API_BOTH.csv";
		processdata(fileName);
	}
	private static void processdata(String fileName) {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			List<String[]> allRecord=U.readCsvFile(fileName);
			for (String[] newLine : allRecord) {
				if (newLine[0].contains("ID")) {
					writer.writeNext(newLine);
					continue;
				}
				U.log(newLine[2]+"|--------|"+ newLine[7]+"|--------|"+ newLine[14]);
				if (!newLine[10].toLowerCase().equals("mexico city")&&!newLine[17].toLowerCase().equals("mexico city")) {
					//writer.writeNext(newLine);
				}else if (newLine[11].equals(newLine[18])) {
					//U.log("EXACT MATCH");
					//writer.writeNext(newLine);
					//break;
				}else {
				int gooleAddress=FuzzySearch.tokenSortRatio(newLine[2], newLine[7]);
				int hereAddress=FuzzySearch.tokenSortRatio(newLine[2], newLine[14]);
				int gooleColonia=FuzzySearch.tokenSortRatio(newLine[3], newLine[8]);
				int hereColonia=FuzzySearch.tokenSortRatio(newLine[3], newLine[15]);
				
				if (gooleAddress>90&&gooleAddress<100&&gooleColonia==100) {
					String out[]= {newLine[0],newLine[1],newLine[2],newLine[3],newLine[4],newLine[5],newLine[6],newLine[7],newLine[8],newLine[9],newLine[10],newLine[11],newLine[12],newLine[13],"","","","","","",""};
					writer.writeNext(out);
				}else if(hereAddress>90&&hereAddress<100&&hereColonia==100){
					String out[]= {newLine[0],newLine[1],newLine[2],newLine[3],newLine[4],newLine[5],newLine[6],"","","","","","","",newLine[14],newLine[15],newLine[16],newLine[17],newLine[18],newLine[19],newLine[20]};
					writer.writeNext(out);
				}else {
					//writer.writeNext(newLine);
				}
//				U.log(gooleAddress+"|--------|"+hereAddress);
//				U.log(newLine[3]+"|--------|"+ newLine[8]+"|--------|"+ newLine[15]);
//				U.log(gooleColonia+"|--------|"+hereColonia);
				}
//				break;
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_FOR_TESTING_ADD_MATCH_90.csv"), sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
