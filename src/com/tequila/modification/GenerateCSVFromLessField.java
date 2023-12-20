package com.tequila.modification;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.utils.U;

public class GenerateCSVFromLessField {
	
	String field = "13";
	String path="/home/glady/Tequila/wrong_record/csv_file/"+field+"_wrongList.csv";
	
	String textFile="/home/glady/Tequila/wrong_record/"+field+"_wrongList.txt";
	
	public void writeHead() throws IOException	{
		CSVWriter writer = new CSVWriter(new FileWriter(path), ',');
		String []columns={"ID",	"INDUSTRY_SECTOR","SPANISH_INDUSTRY_SECTOR","SIC_MAJOR","SIC_SUB",
				"PRODUCT_DESC",
				"SPANISH_PRODUCT_DESC",
				"COMPANY_NAME",
				"ADDRESS",
				"NEIGHBORHOOD",
				"CITY",
				"STATE",
				"ZIP",
				"PHONE",
				"FAX",
				"URL",
				"EMAIL",
				"CONTACT_PERSON",
				"TITLE",
				"ANNUAL_SALES_VOL",
				"EMP_COUNT",
				"YEARS_IN_BIZ",
				"LONGITUDE",
				"LATITUDE"};
		writer.writeNext(columns);
		readTextFile(writer);
		writer.close();
	}
	
	public void readTextFile(CSVWriter writer) throws IOException{
		//FileReader in = new FileReader(textFile);
		HashSet<Integer> set = new HashSet<Integer>();
	    BufferedReader br = new BufferedReader(new FileReader(textFile));
	    String line="";
	    while ((line = br.readLine()) != null) {
	        System.out.println(line);
	        
	        String line1[]=line.trim().split("\t");
	        U.log(line1.length);
	        set.add(line1.length);
	      writer.writeNext(line1);
	    }
	    
	    br.close();
	    U.log("=========== Length ==========");
	    for(int len : set)
	    	U.log(len);
	}
	
	public static void main(String[] args) throws IOException {
		GenerateCSVFromLessField cg=new GenerateCSVFromLessField();
		cg.writeHead();

	}
}
