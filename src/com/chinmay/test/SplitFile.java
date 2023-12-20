package com.chinmay.test;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class SplitFile {
	static String fileName="/home/chinmay/MexicoCache/Cache/Tripadvisor_Hotel_GOOGLESearch_Success_Match.csv";
	static String folderPath="/home/chinmay/MexicoCache/Cache/SplitetripAdvisor/";
	public static void main(String[] args) {
		
		List<String[]> data=U.readCsvFile(fileName);
		try {
			int i=0;
			StringWriter sw=null;
			CSVWriter writer=null;
			U.log(Arrays.toString(data.get(0)));
			String header[] = data.get(0);
			ArrayList<String[]> writeData=new ArrayList<>();
			writeData.add(header);
			for (String[] rowData : data) {
				i++;
//				if (writeData.size()==1) continue;
				writeData.add(rowData);
				if (writeData.size()==201) {
					sw=new StringWriter();
					writer=new CSVWriter(sw);
					writer.writeAll(writeData);
					FileUtil.writeAllText(folderPath+"Tripadvisor_Hotel_GOOGLESearch_Success_Match_"+i+".csv", sw.toString());
					U.log(i);
					writeData=new ArrayList<>();
					writeData.add(header);
					writer.close();
					sw.close();
				}
			}
			sw=new StringWriter();
			writer=new CSVWriter(sw);
			writer.writeAll(writeData);
			FileUtil.writeAllText(folderPath+"Splitted_190000_GOOGLESearch_Not_Found_At_Here_GOOGLESearch_Success_Match_Matched_"+i+".csv", sw.toString());
//			writeData=new ArrayList<>();
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
