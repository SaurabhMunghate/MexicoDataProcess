package com.shatam.conversion;

import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;

import org.supercsv.io.CsvListReader;
import org.supercsv.prefs.CsvPreference;

import com.shatam.utils.U;

public class SicFileCreator {

	public static HashMap<String,String[]> sicMap = new HashMap();
	public static void main(String[] args) throws Exception {
		
		serializedSicData("/home/mypremserver/DatabasesTequila/sawansir/updateSiCCodeFile.csv");
	}
	public static void serializedSicData(String path) throws Exception
	{
		String newFile = path;
		CsvListReader newFileReader = new CsvListReader(new FileReader(newFile), CsvPreference.STANDARD_PREFERENCE);
		List<String> listRow = null;
		
		int count=0;
		while ((listRow = newFileReader.read()) != null) 
		{

			//if (count > 0) 
			{	
				U.log("kkkkk  "+listRow.get(0));
				//String allData=listRow.get(0);
				
				String[] aSicData=listRow.toArray(new String[0]);
				U.log(listRow.size());
				if(listRow.size() == 8){
					//log(aSicData[0]);
					String key = listRow.get(4);
					if(key.length() ==3){
						key = "0"+key;
					}
					if(aSicData[4].length() == 3){
						aSicData[4] = "0"+aSicData[4];
					}
					if(aSicData[3].length() == 1){
						aSicData[3] = "0"+aSicData[3];
					}
					sicMap.put(key,aSicData);
				}
			}
			count++;
		}
		
		try {
			//U.log("fffffffff  "+sicMap.get("01"));
	        FileOutputStream fileOut =new FileOutputStream("/home/mypremserver/DatabasesTequila/sawansir/updateSicCodeData.ser");
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(sicMap);
	        out.close();
	        fileOut.close();
	        //System.out.printf(System.getProperty("user.dir")+"/sicCodeData.ser");
	     } catch (IOException i) {
	        i.printStackTrace();
	     }
		//return sicObject;
		
	}
}
