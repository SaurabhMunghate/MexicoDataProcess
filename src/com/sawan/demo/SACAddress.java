package com.sawan.demo;

import java.util.Iterator;
import java.util.List;


import com.shatam.utils.U;
import com.shatam.utils.Util;
public class SACAddress {

	public static void main(String[] args) {
		addressSaparate();
	}
	
	private static final String filePath = "/home/glady/sac/4-July-2018/30kaddresses.csv";
	
	static void addressSaparate(){
		List<String[]> readLines = U.readCsvFile(filePath);
		String[] lines = null;
		int x = 0;
		
		Iterator<String[]> it = readLines.iterator();
		while(it.hasNext()){
			lines = it.next();
			
			if(x++ == 0)continue;
			
			String cityState = Util.match(lines[0].trim(), "[A-Z]{2}\\s\\d{5}$");
			if(cityState == null)
				U.log(lines[0]);
		}
	}
}
