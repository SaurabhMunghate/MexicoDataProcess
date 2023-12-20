package com.tequila.process;

import java.util.List;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.opencsv.CSVReader;
import com.shatam.utils.U;

public class PreparedRawData {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		PreparedRawData rawData = new PreparedRawData();
		rawData.loadResources();
	}
	
	Map<String,List<String>> uniqueData = new HashMap<String,List<String>>();
	public void loadResources(){
		FileReader fr = null;
		CSVReader reader = null;
		try {
			fr = new FileReader(new File(this.getClass().getResource("/City_state.csv").getPath()));
			reader = new CSVReader(fr);
			String [] nextLine = null;
			int i= 0;
			while((nextLine = reader.readNext()) != null){
				if(i ==0 )continue;
//				U.log(Arrays.toString(nextLine));
				
				i++;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try{
				if(reader != null){
					reader.close();
				}
				if(fr != null)
					fr.close();
			}catch(IOException e){
				e.printStackTrace();
			}				
		}
	
	}

}
