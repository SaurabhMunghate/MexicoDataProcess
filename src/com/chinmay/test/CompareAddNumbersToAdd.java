package com.chinmay.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.FileUtil;
import com.shatam.utils.TextFormat;
import com.shatam.utils.U;

public class CompareAddNumbersToAdd {
	String dbFile="/home/chinmay/Inegi_58_CSV.csv";
	String inegiFile="/home/chinmay/MexicoCache/InegiCache/HotelsAndRest.csv";
	public static void main(String[] args) {
		new CompareAddNumbersToAdd().processRecords();
		//new CompareAddNumbersToAdd().processOnlyUnique();
	}
	private void processOnlyUnique() {
		HashSet<String> uniqueList=new HashSet<>();
		String fileName="/home/chinmay/Mexico/MexicoDataFiles/Tested/Inegi_58_CSV_ForUpdate.csv";
		List<String[]> rowData=U.readCsvFile(fileName);
		List<String[]> outData=new ArrayList<>();
		for (String[] nextLine : rowData) {
			if(nextLine[1].matches(".* \\d+ .*|.* \\d+")) {
				if(uniqueList.add(nextLine[0])) {
					outData.add(nextLine);
				}
			}
		}
		U.log(outData.size());
		U.writeCsvFile(outData, fileName.replace(".csv","_ForUpdate.csv"));
	}
	private void processRecords() {
		List<String[]> dbFileRecords=U.readCsvFile(dbFile);
		int count=0;
		HashMap<String, String> recordsMap=new HashMap<>();
		for (String[] dbFileRecord : dbFileRecords) {
			if(dbFileRecord[0].contains("ID")) continue;
			if(!dbFileRecord[8].matches(".* \\d+ .*|.* \\d+")) {
				//U.log(dbFileRecord[8]);
				recordsMap.put((dbFileRecord[7]+dbFileRecord[8]).toLowerCase(), dbFileRecord[0]);
//				count++;
			}
		}
		List<String[]> inegiFileRecords=U.readCsvFile(inegiFile);
		ArrayList<String[]> outList=new ArrayList<>();
		for (String[] inegiFileDataRow : inegiFileRecords) {
			String streetAdd=inegiFileDataRow[6]+" "+inegiFileDataRow[7]+" "+inegiFileDataRow[15]+" "+inegiFileDataRow[16];
			if(recordsMap.containsKey((inegiFileDataRow[1]+streetAdd).toLowerCase())) {
				String out[]=new String[2];
				if(inegiFileDataRow[14].equals("0")) {
					inegiFileDataRow[14]=inegiFileDataRow[15];
				}else if(inegiFileDataRow[14].trim().length()!=0&&inegiFileDataRow[15].trim().length()!=0){
					inegiFileDataRow[14]=inegiFileDataRow[14]+" "+inegiFileDataRow[15];
				}
				out[0]=recordsMap.get((inegiFileDataRow[1]+streetAdd).toLowerCase());
				streetAdd=inegiFileDataRow[6]+" "+inegiFileDataRow[7]+" "+inegiFileDataRow[14]+" "+inegiFileDataRow[16];
				out[1]=streetAdd;
				outList.add(out);
			}else if(recordsMap.containsKey((TextFormat.getCompanyNameStandardised(inegiFileDataRow[1])+TextFormat.getAddressStandardised(TranslateEnglish.convertToEnglish(streetAdd))).toLowerCase())){
				String out[]=new String[2];
				if(inegiFileDataRow[14].equals("0")||(inegiFileDataRow[14].trim().length()==0&&inegiFileDataRow[15].trim().length()!=0)) {
					inegiFileDataRow[14]=inegiFileDataRow[15];
				}else if(inegiFileDataRow[14].trim().length()!=0&&inegiFileDataRow[15].trim().length()!=0){
					inegiFileDataRow[14]=inegiFileDataRow[14]+" "+inegiFileDataRow[15];
				}
				out[0]=recordsMap.get((TextFormat.getCompanyNameStandardised(inegiFileDataRow[1])+TextFormat.getAddressStandardised(TranslateEnglish.convertToEnglish(streetAdd))).toLowerCase());
				streetAdd=inegiFileDataRow[6]+" "+inegiFileDataRow[7]+" "+inegiFileDataRow[14]+" "+inegiFileDataRow[16];
				out[1]=streetAdd;
				outList.add(out);
			}else {
				count++;
			}
		}
		U.writeCsvFile(outList, dbFile.replace(".csv","_ForUpdate.csv"));
		U.log("Not Matched "+ count);
	}
}