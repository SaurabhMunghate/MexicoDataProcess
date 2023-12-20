package com.chinmay.test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractLatLon {
	static String fileName="/home/chinmay/Mexico/MexicoDataFiles/Data/Jun/INSERTED/Jun_3/All_Record_BancoAzteca_CSV_30_04_2019_CORRECT_NW_REC_Unique_unique.csv";
	public static void main(String[] args) {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			List<String[]> fileData=U.readCsvFile(fileName);
			for (String[] records : fileData) {
				U.log(records[22].length());
				if (records[22]!=null&&records[22].length()>1) {
					writer.writeNext(records);
					continue;
				}
				U.log(records[0]);
				String add[]= {records[8],records[10],records[11],records[12]};
				String laton[]=U.getlatlongGoogleApi(add);
				records[22]=laton[0];
				records[23]=laton[1];
				U.log(Arrays.toString(laton));
				writer.writeNext(records);
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_latlon.csv"), sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
