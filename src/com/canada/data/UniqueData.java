package com.canada.data;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class UniqueData {
	public static void main(String[] args) {
		StringWriter sw= new  StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		HashSet<String>emailSet=new HashSet<>();
		List<String[]>inData=U.readCsvFile("/home/chinmay/Downloads/Sample_Database/SampleMailReocrds.csv");
		for (String[] data : inData) {
			if(emailSet.add(data[2]))
				writer.writeNext(data);
		}
		
		try {
			FileUtil.writeAllText("/home/chinmay/Downloads/Sample_Database/SampleMailReocrds_Unique.csv", sw.toString());
			writer.close();
			sw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
