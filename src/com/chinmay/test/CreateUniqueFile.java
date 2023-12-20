package com.chinmay.test;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class CreateUniqueFile {
	public static void main(String[] args) {
		String filePath="/home/chinmay/Mexico/MexicoDataFiles/Data/Jun/INSERTED/Jun_3/All_Record_BancoAzteca_CSV_30_04_2019_CORRECT_NW_REC_Unique.csv";
		try {
			HashSet<String>unique=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			List<String[]>inputFile=U.readCsvFile(filePath);
			for (String[] record : inputFile) {
				if (unique.add((TranslateEnglish.convertToEnglish(record[4]+record[7]+record[8]+record[10]).toLowerCase()))) {
					writer.writeNext(record);
				}
			}
			FileUtil.writeAllText(filePath.replace(".csv", "_unique.csv"), sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
