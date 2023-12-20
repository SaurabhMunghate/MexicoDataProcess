package com.shatam.corrector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.database.connection.DBConnection;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class GenerateCSVFromTab {
	
	public void generateCSVFile(){

		BufferedReader  br = null;
		CSVWriter writer = null;
		try{
			br= new BufferedReader(new FileReader(Path.DB_CACHE+"octoberDataset.tab"));
			writer = new CSVWriter(new FileWriter(Path.DB_CACHE+"NEW_DATA_OCT.csv"));
			
//			
			String line = null;
			String[] fieldArray;
			int count = 0;
			int k = 0;
			int j = 0;
			while((line = br.readLine()) != null){
//				U.log(line);
				fieldArray = line.split("\t");
				if(fieldArray.length == 24){
					if(fieldArray[9].trim().length()> 100){
						k++;
					}else{
						fieldArray = StringUtils.stripAll(fieldArray); //trim the white spaces from elements
						if(fieldArray.length != 24){
							j++;
						}
						writer.writeNext(fieldArray);
						count++;
					}
				}
			}//eof while
//			writer.close();
			br.close();
			U.log("count ="+count);
			U.log("k ="+k);
			U.log("j ="+j);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GenerateCSVFromTab generate = new GenerateCSVFromTab();
		long startTime = System.currentTimeMillis();
		generate.generateCSVFile();
		long endTime   = System.currentTimeMillis();
		U.log((endTime - startTime)+" milliSecond");
	}

}
