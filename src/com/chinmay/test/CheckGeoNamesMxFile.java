package com.chinmay.test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import com.shatam.utils.U;

public class CheckGeoNamesMxFile {
	private static String fileName="/home/mypremserver/DatabasesTequila/MX_Postal_Codes/MX.txt";
	public static HashMap<String, String[]> MX_ZIP_MAP=new HashMap<>();
	public static void main(String[] args) {
		getMX_ZIP_MAP();
	}
	public static HashMap<String, String[]> getMX_ZIP_MAP() {
		try {
			List<String> inputLine=Files.readAllLines(Paths.get(fileName));
			for (String nextLine : inputLine) {
				U.log(nextLine);
				String[] record=nextLine.split("\t");
				MX_ZIP_MAP.put(record[1], record);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return MX_ZIP_MAP;
	}
}
