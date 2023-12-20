package com.tequila.address;

import java.util.Arrays;
import java.util.List;

import com.shatam.utils.U;

public class MexicoCorrector {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		start("/home/glady/MexicoCache/Tequila_DATA/files/Tested/Tiendo/tiendo-DepartmentStores/Tiendeo-DepartmentStores1_ADDRESS_CORRECTION.csv");
	}

	static void start(String fileName){
		List<String[]> readLines = U.readCsvFile(fileName);
		U.log(Arrays.toString(readLines.get(0)));
	}
}
