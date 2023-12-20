package com.shatam.conversion;

import java.util.ArrayList;
import java.util.Arrays;

import com.shatam.utils.U;
import com.shatam.utils.Util;

public class Employee {
/*	public static void main(String[] args) {
		String [] vals = getMinAndMaxEmp("500 To 1 000 Actual 780");
		U.log(Arrays.toString(vals));
	}*/

	public static String[] getMinAndMaxEmp(String empCount){
				
		empCount = empCount.replaceAll("\\+|,|\\.", "").replace(" ", "");
		
		ArrayList<String> vals = Util.matchAll(empCount, "\\d+", 0);
		
		if(vals.size() == 1){
			return new String[]{vals.get(0), ""};
		}
		if(vals.size() > 1){
			return new String[]{vals.get(0), vals.get(1)};
		}
		
		return new String[]{"",""};
	}
}
