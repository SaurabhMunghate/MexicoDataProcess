package com.shatam.corrector;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.shatam.utils.Path;
import com.shatam.utils.U;

public class GenerateCSVToCSV {

	public void generateCSVToCSV(String path){
		List<String> listEntries = null;
		String[] nextLine = null;
		// read csv file
		try {
			listEntries = Files.readAllLines(java.nio.file.Paths.get("/home/glady/mexicoProject/octoberDataset.tsv"));
			System.out.println(listEntries.size());

		} catch (IOException e1) {
			e1.printStackTrace();
		}
		Iterator<String> itr = listEntries.iterator();
		int i=0;
		while (itr.hasNext()) {

			if(i < 1000000)continue;
			String line = itr.next();
			nextLine = line.split("\t");
			if (nextLine.length != 24)
				continue;
			
			if(i == 1000020)break;
			i++;
			
			U.log(Arrays.toString(nextLine));
			U.log(">>"+scoring(nextLine));
			
		}
	}
	
	DecimalFormat df = new DecimalFormat("#.0000");
	
	public float scoring(String[] data){
		int total = 0;
		int id = Integer.parseInt(data[0]);
		if(id > 0)	total += 100;
		if(data[3].trim().length() > 0 && !data[3].isEmpty()) total += 100;
		if(data[7].trim().length() > 1 && !data[7].isEmpty()) total += 100;
		if(data[8].trim().length() > 5 && !data[8].isEmpty()) total += 100;
		if(data[9].trim().length() > 2 && !data[9].isEmpty()) total += 50;
		if(data[10].trim().length() > 2 && !data[10].isEmpty()) total += 100;
		if(data[11].trim().length() > 2 && !data[11].isEmpty()) total += 100;
		if(data[12].trim().length() >= 4 && !data[12].isEmpty()) total += 100;
		if(data[13].trim().length() > 6 && !data[13].isEmpty()) total += 50;
		if(data[14].trim().length() > 5 && !data[14].isEmpty()) total += 50;
		if(data[15].trim().length() > 5 && !data[15].isEmpty()) total += 25;
		if(data[16].trim().length() > 5 && data[16].trim().contains("@") && !data[16].isEmpty()) total += 50;
		if(data[17].trim().length() > 2 && !data[17].isEmpty()) total += 10;
		if(data[18].trim().length() > 2 && !data[18].isEmpty()) total += 10;
		if(data[19].trim().length() > 2 && !data[19].isEmpty())  total += 5;
		if(data[20].trim().length() > 0 && !data[20].isEmpty()) total += 5;
		if(data[21].trim().length() > 6 && !data[21].isEmpty()) total += 5;
		if(data[22].trim().length() > 3  && !data[22].isEmpty()) total += 50;
		if(data[23].trim().length() > 3  && !data[23].isEmpty()) total += 50;
		
		float score = (100 * total)/1060;
//		df.format(score);
		return Float.valueOf(df.format(score));
	}
	public static void main(String[] args) {
		GenerateCSVToCSV gcc = new GenerateCSVToCSV();
		gcc.generateCSVToCSV(Path.WRONG_RECORD_CACHE);		
	}

}
