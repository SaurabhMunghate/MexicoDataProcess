package com.shatam.geocode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import com.opencsv.CSVWriter;
//import com.opencsv.CSVReader;
import com.shatam.maps.BingMap;
import com.shatam.maps.GoogleMap;
import com.shatam.maps.Map;
import com.shatam.utils.U;

public class FindLatLong {

	
	
	
	
	
	public static void main(String[] arg) throws IOException {
		//writeInCsvCommonData();
		
		
	}


	
	
	/**
	 * @author Upendra
	 * @throws IOException
	 */
	private static void writeInCsvCommonData() throws IOException {
		
		String outputFile = "/home/shatam-20/Upendra_Data/ryeDatabaseJan11_7PM/users7.csv";
		
		int count=0;
		
		boolean alreadyExists = new File(outputFile).exists();
		
		Map goole = new GoogleMap();
		Map bing =new BingMap();
		
		FileInputStream inputStream = null;
		String[] nextLine = null;
		Scanner sc = null;
		try {
			inputStream = new FileInputStream("/home/shatam-20/Upendra_Data/ryeDatabaseJan11_7PM/dataset.csv");
			sc = new Scanner(inputStream, "UTF-8");
			CSVWriter csvOutput = new CSVWriter(new FileWriter(outputFile, true), ',');
			//if (!alreadyExists) {
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					count++;
					//U.log(line);
					if(count<=15000)continue;
					if(count>20000)
					{
						break;	
					}
					U.log(line);
					
					nextLine = line.split("\t");
					String[] aa= {nextLine[8],nextLine[10],nextLine[11],nextLine[12]};
					String[] ll = goole.getLatLong(aa);
					if(ll[0].length()<3)
					{
						ll = bing.getLatLong(aa);
					}
					U.log(ll[0]);
					//U.log(nextLine.length+"   "+nextLine[12]);
					csvOutput.writeNext(new String[]{nextLine[0],nextLine[8],nextLine[10],nextLine[11],nextLine[12],ll[0],ll[1]});
					//csvOutput.writeNext("hello");
									
				}
			//}
			csvOutput.close();
			if (sc.ioException() != null) {
				throw sc.ioException();
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
		}
	}

}
