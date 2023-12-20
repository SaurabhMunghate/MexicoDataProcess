package com.canada.data;

import java.util.ArrayList;
import java.util.List;

import com.shatam.utils.U;

public class VerifyCanadaEmailIds {
	public static void main(String[] args) {
		String fileName="/home/chinmay/Canada/technomantra/OntarioEmail-2000.csv";
		processEmailFile(fileName);
	}

	private static void processEmailFile(String fileName) {
		List<String[]> csvData=U.readCsvFile(fileName);
		List<String[]> outData=new ArrayList<String[]>();
		int x = 0;
		for (String[] data : csvData) {
			if(x++ == 0)continue;
			List<String> invalidEmail = U.invalidEmailList(data[16]);
			if(invalidEmail != null){
				if(!invalidEmail.isEmpty()){
					System.err.println("Invalid email at row :"+x+"\tInvalid emails are "+invalidEmail);
					
				}else {
					outData.add(data);
				}
			}else {
				outData.add(data);
			}
			
		}
		U.writeCsvFile(outData, fileName.replace(".csv", "_CORRECT_NW_REC.csv"));
	}
}
