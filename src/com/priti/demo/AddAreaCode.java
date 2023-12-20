package com.priti.demo;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class AddAreaCode {
	static HashMap<String, String> areaCodeMap=new HashMap<>();
	public static void main(String[] args) {
		String areaPhoneNo="/home/chinmay/MexicoCache/Mexico_All_State_City_AreaCode_13_09_2018.csv";
		List<String[]> fileData=U.readCsvFile(areaPhoneNo);
		String areaPhoneNo1="/home/chinmay/MexicoCache/Codigo_Postal_Mexico_All_State_City_AreaCode_17_09_2018.csv";
		List<String[]> fileData1=U.readCsvFile(areaPhoneNo1);
		List<String[]> out=new ArrayList<String[]>();
		for (String[] fileDa : fileData) {
//			U.log(Arrays.toString(fileDa));
			areaCodeMap.put((fileDa[1]+fileDa[2]).toLowerCase(), fileDa[3]);
			
		}
		for (String[] fileDa : fileData1) {
//			U.log(Arrays.toString(fileDa));
			areaCodeMap.put((fileDa[1]+fileDa[2]).toLowerCase(), fileDa[3]);
			
		}
		String updateFile="/home/chinmay/MexicoCache/APR19-UPDATE.csv";
		List<String[]> updateFileData=U.readCsvFile(updateFile);
		List<String[]> out1=new ArrayList<String[]>();
		for (String[] fileDa : updateFileData) {
			String code=areaCodeMap.get((fileDa[10]+fileDa[11]).toLowerCase());
			if((U.formatNumbersAsCode(fileDa[13]).length()==8||U.formatNumbersAsCode(fileDa[13]).length()==9)&&code!=null) {
				U.log(U.formatNumbersAsCode(code+fileDa[13]));
				fileDa[13]=U.formatNumbersAsCode(code+fileDa[13]);
				out.add(fileDa);
			}else if(U.formatNumbersAsCode(fileDa[13]).length()==12){
				fileDa[13]=U.formatNumbersAsCode(fileDa[13]);
				out.add(fileDa);
			}else {
			fileDa[13]=U.formatNumbersAsCode(fileDa[13]);
			out1.add(fileDa);
			}
			//areaCodeMap.put((fileDa[1]+fileDa[2]).toLowerCase(), fileDa[3]);
			
		}
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(updateFile.replace(".csv", "_Correct_2.csv")),',');
			writer.writeAll(out);
			writer.flush();
			writer.close();
			
			writer = new CSVWriter(new FileWriter(updateFile.replace(".csv", "_Correct_1.csv")),',');
			writer.writeAll(out1);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
