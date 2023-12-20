package com.chinmay.test;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.scrapper.CSVCorrectorInValidFormat;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ProcessTestedGoogleSearchFile {
	public static void main(String[] args) {
		String fileName="/home/chinmay/Mexico/MexicoDataFiles/Data/Jul/INSERTED/Jul_31/JULY_31_4File_CORRECT_NW_REC.csv";
		startProcess(fileName);
	}
	//+A-ddp-hon-enu-mber

	private static void startProcess(String fileName) {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			int phone=0,herePhone=0,googlePhone=0,zip=0,neigh=0,city=0,state=0;
			int website=0,herewebsite=0,googlewebsite=0;
			List<String[]> inputRecords=U.readCsvFile(fileName);
			for (String[] inputData : inputRecords) {
				if (inputData[0].contains("ID")) {
					for (int i = 0; i < inputData.length; i++) {
						if (inputData[i].equalsIgnoreCase("PHONE")) {
							phone=i;
						}else if (inputData[i].equalsIgnoreCase("HERE_PHONE")) {
							herePhone=i;
						}else if (inputData[i].equalsIgnoreCase("GOOGLE_PHONE")) {
							googlePhone=i;
						}else if (inputData[i].equalsIgnoreCase("URL")) {
							website=i;
						}else if (inputData[i].equalsIgnoreCase("HERE_WEBSITE")) {
							herewebsite=i;
						}else if (inputData[i].equalsIgnoreCase("GOOGLE_WEBSITE")) {
							googlewebsite=i;
						}else if (inputData[i].equalsIgnoreCase("HERE_ZIP")) {
							zip=i;
						}else if (inputData[i].equalsIgnoreCase("HERE_STATE")) {
							state=i;
						}else if (inputData[i].equalsIgnoreCase("HERE_COL")) {
							neigh=i;
						}else if (inputData[i].equalsIgnoreCase("HERE_CITY")) {
							city=i;
						}
					}
					writer.writeNext(inputData);
					continue;
				}
				inputData[phone]=inputData[phone].replaceAll("\\+|\\(|\\)|A-ddp-hon-enu-mber|-", "");
				if (!inputData[phone].contains(inputData[googlePhone])) {
					if (inputData[phone].length()==0) {
						inputData[phone]=inputData[googlePhone].replaceAll("\\+", "");
					}else {
						inputData[phone]+=";"+inputData[googlePhone].replaceAll("\\+", "");
					}
					
				}
				if (!inputData[phone].contains(inputData[herePhone])) {
					if (inputData[phone].length()==0) 
					inputData[phone]=inputData[herePhone].replaceAll("\\+", "");
					else
						inputData[phone]=";"+inputData[herePhone].replaceAll("\\+", "");
				}
				if (inputData[website].trim().length()==0)
					inputData[website]=inputData[googlewebsite].trim().length()<3?inputData[herewebsite]:inputData[googlewebsite];
				inputData[phone]=U.formatNumbersAsCode(inputData[phone]);
//				inputData[9]=inputData[neigh].trim();
//				inputData[10]=inputData[city].trim();
//				inputData[11]=inputData[state].trim();
//				inputData[12]=inputData[zip].trim();
				U.log(Arrays.toString(inputData));
				if (inputData[11].contains("Mexico City")) {
					if (inputData[12].length()==4) {
						inputData[12]="0"+inputData[12];
					}
					String tempCity=MXStates.getMexicoCityMunicipalites(inputData[12]);
					if(inputData[10].trim().length()!=0)
					if (!tempCity.equalsIgnoreCase(inputData[10])) {
//						System.err.println("City is Not Matching "+inputData[0]);
						throw new IllegalArgumentException("Wrong city found, city is "+inputData[10].trim()+" for id :"+inputData[0]+" "+inputData[11]);
//						break;
					}
					
//					U.log("hete");
				}
				if(inputData[10].trim().length()!=0)
				if(!CSVCorrectorInValidFormat.isExistCityWithinState(inputData[10], inputData[11])){
					throw new IllegalArgumentException("Wrong city found, city is "+inputData[10].trim()+" for id :"+inputData[0]+" "+inputData[11]);
					
				}
//				inputData[12]=inputData[zip];
				U.log(Arrays.toString(inputData));
				writer.writeNext(inputData);
//				U.log(inputData[website]);
//				break;
			}
			FileUtil.writeAllText(fileName.replace(".csv", "_formatted.csv"), sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		
	}
	
}
