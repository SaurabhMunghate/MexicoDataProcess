package com.chinmay.test;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class FormatFile {
	public static void main(String[] args) {
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		String fileNmae="/home/chinmay/Mexico/Tested/RakeshSir/emailResult_Formatted_2.csv";
		List<String[]> data=U.readCsvFile(fileNmae);
		for (String[] datas : data) {
			if (count(datas[2], ';')<5) {
//				datas[2]=datas[2];
				writer.writeNext(datas);
			}
		}
		try {
			FileUtil.writeAllText("/home/chinmay/Mexico/Tested/RakeshSir/emailResult_Formatted_2_1.csv", sw.toString());
			writer.close();
			sw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static int count(String s, char c) 
    { 
        int res = 0; 
        for (int i=0; i<s.length(); i++) { 
            if (s.charAt(i) == c) 
            res++; 
        }  
        return res; 
    } 
}
