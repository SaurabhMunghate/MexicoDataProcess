package com.chinmay.test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;

import com.shatam.utils.U;

public class FormatPhoneTabFile {
	static String inputFile="/home/chinmay/Mexico/urlsResult.tab";
	static String refFile="/home/chinmay/Mexico/urlList.csv";
	public void recordsWithoutPhoneNo(String inputFile,String refFile) {
		try {
			List<String[]> outData=new ArrayList<>();
			List<String> urlResultList = Files.readAllLines(Paths.get(inputFile));
//			List<String[]> refRecords=U.readCsvFile(refFile);
//			MultiValueMap refMap=new MultiValueMap();
//			for (String[] refRecord : refRecords)
//				refMap.put(refRecord[1], refRecord[0]);
			for (String urlresult : urlResultList) {
				String urlArr[]=urlresult.split("\t");
				if (urlArr[1].contains("[]")) {
						String out[]= {urlArr[0],urlArr[1]};
						outData.add(out);
				}
			}
			U.writeCsvFile(outData, "/home/chinmay/Mexico/OutputFileWithoutPhone.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void recordsWithPhoneNo(String inputFile,String refFile) {
		try {
			List<String[]> outData=new ArrayList<>();
			List<String> urlResultList = Files.readAllLines(Paths.get(inputFile));
			List<String[]> refRecords=U.readCsvFile(refFile);
			MultiValueMap refMap=new MultiValueMap();
			for (String[] refRecord : refRecords)
				refMap.put(refRecord[1], refRecord[0]);
			for (String urlresult : urlResultList) {
				String urlArr[]=urlresult.split("\t");
				if (!(urlArr[1].contains("Email Not Found o")||urlArr[1].contains("[]"))) {
					ArrayList<String> ids=new ArrayList<>();
					if (refMap.containsKey(urlArr[0])) {
						ids=(ArrayList<String>) refMap.get(urlArr[0]);
					}else if (refMap.containsKey(urlArr[0].replaceAll("http://|https://", ""))) {
						ids=(ArrayList<String>) refMap.get(urlArr[0].replaceAll("http://|https://", ""));
					}
					for (String id : ids) {
						String out[]= {id,urlArr[0],urlArr[1].replaceAll("\\[|\\]", "")};
						outData.add(out);
					}
				}
			}
			U.writeCsvFile(outData, "/home/chinmay/Mexico/OutputFilePhone.csv");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		new FormatPhoneTabFile().recordsWithoutPhoneNo(inputFile,refFile);
	}
}
