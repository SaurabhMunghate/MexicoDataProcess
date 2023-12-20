package com.chinmay.DbCheck;

import java.util.Arrays;
import java.util.List;

import com.shatam.utils.U;

public class CorrectAddressFromFile {
	
	public static void main(String[] args) {
		try {
			String fileName="/home/chinmay/MexicoCache/Cache/Banorte_Branches_test.csv";
			List<String[]> fileDatas=U.readCsvFile(fileName);
			for (String[] fileData : fileDatas) {
				if (fileData[0].contains("ID"))continue;
				U.log(fileData[22]);
				U.log(fileData[23]);
				String add[]=U.getAddressGoogleApi(new String[] {fileData[22],fileData[23]});
				U.log(fileData[8]+", "+fileData[9]+", "+fileData[10]+", "+fileData[11]+", "+fileData[12]);
				U.log(Arrays.toString(add));
				String outData[]= {};
				
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
