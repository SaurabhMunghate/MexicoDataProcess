package com.shatam.corrector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashSet;

import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class WriteWrongDataInField {
	private static final String CACHE_PATH = "/home/glady/testdemo/Tequila/data/";
	private static final String[] header = { "ID","INDUSTRY_SECTOR", "SPANISH_INDUSTRY_SECTOR", "SIC_MAJOR", "SIC_SUB", "PRODUCT_DESC", "SPANISH_PRODUCT_DESC", "COMPANY_NAME", "ADDRESS", "NEIGHBORHOOD","CITY", "STATE", "ZIP",
			"PHONE", "FAX", "URL", "EMAIL", "CONTACT_PERSON", "TITLE", "ANNUAL_SALES_VOL", "EMP_COUNT", "YEARS_IN_BIZ", "LONGITUDE", "LATITUDE" };

	public static void main(String[] args) {
		WriteWrongDataInField write = new WriteWrongDataInField();
//		write.generateTextFile(CACHE_PATH+"text/");
		write.generateCSVFile(CACHE_PATH+"csv/",true);
	}
	
	/**
	 * This method is used to create text file
	 * @param path
	 */
	public void generateTextFile(String path){
		FileWriter writerList14 = null;
		FileWriter writerList15 = null;
		FileWriter writerList16 = null;
		FileWriter writerList17 = null;
		FileWriter writerList18 = null;
		FileWriter writerList19 = null;
		FileWriter writerList20 = null;
		FileWriter writerList21 = null;
		FileWriter writerList22 = null;
		FileWriter writerList23 = null;
		
		HashSet<Integer> dataSize = new HashSet<Integer>();
		
		BufferedReader  br = null;
		try{
			/*
			 * Read source file
			 */
			br= new BufferedReader(new FileReader(Path.DB_CACHE+"octoberDataset.tsv"));

			writerList14 = new FileWriter(path+"14_wrongList.txt");
			writerList15 = new FileWriter(path+"15_wrongList.txt");
			writerList16 = new FileWriter(path+"16_wrongList.txt");
			writerList17 = new FileWriter(path+"17_wrongList.txt");
			writerList18 = new FileWriter(path+"18_wrongList.txt");
			writerList19 = new FileWriter(path+"19_wrongList.txt");
			writerList20 = new FileWriter(path+"20_wrongList.txt");
			writerList21 = new FileWriter(path+"21_wrongList.txt");
			writerList22 = new FileWriter(path+"22_wrongList.txt");
			writerList23 = new FileWriter(path+"23_wrongList.txt");
			
			
			String line = null;
			String[] fieldArray;
			int count = 0;
			int i = 0;
			int k = 0;
			while((line = br.readLine()) != null){
//				U.log(line);
				fieldArray = line.split("\t");
				if(fieldArray.length == 24){
					count++;
					if(fieldArray[9].trim().length()> 100){
//						writer1.write(line+"\n");
						k++;
					}
				}
				else{
					i++;
					if(fieldArray.length == 14){
						writerList14.write(line+"\n");
					}else if(fieldArray.length == 15){
						writerList15.write(line+"\n");
					}else if(fieldArray.length == 16){
						writerList16.write(line+"\n");
					}else if(fieldArray.length == 17){
						writerList17.write(line+"\n");
					}else if(fieldArray.length == 18){
						writerList18.write(line+"\n");
					}else if(fieldArray.length == 19){
						writerList19.write(line+"\n");
					}else if(fieldArray.length == 20){
						writerList20.write(line+"\n");
					}else if(fieldArray.length == 21){
						writerList21.write(line+"\n");
					}else if(fieldArray.length == 22){
						writerList22.write(line+"\n");
					}else if(fieldArray.length == 23){
						writerList23.write(line+"\n");
					}
					U.log(">>"+fieldArray.length);
					dataSize.add(fieldArray.length);
				}
			}
			
			writerList14.flush(); writerList14.close();
			writerList15.flush(); writerList15.close();
			writerList16.flush(); writerList16.close();
			writerList17.flush(); writerList17.close();
			writerList18.flush(); writerList18.close();
			writerList19.flush(); writerList19.close();
			writerList20.flush(); writerList20.close();
			writerList21.flush(); writerList21.close();
			writerList22.flush(); writerList22.close();
			writerList23.flush(); writerList23.close();
			br.close();
			U.log("count 24="+count);
			U.log("count wrong list="+i);
			U.log("Count 24="+k);
			
			for(int len : dataSize){
				U.log(len);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	/**
	 * This method is used to create csv file.
	 * @param path
	 * This variable is contain the path for storing the file
	 * @param headerStatus
	 * If this true then it will write header at csv file else it will not.
	 */
	public void generateCSVFile(String path, boolean headerStatus){

		BufferedReader  br = null;
		CSVWriter writerList14 = null;
		CSVWriter writerList15 = null;
		CSVWriter writerList16 = null;
		CSVWriter writerList17 = null;
		CSVWriter writerList18 = null;
		CSVWriter writerList19 = null;
		CSVWriter writerList20 = null;
		CSVWriter writerList21 = null;
		CSVWriter writerList22 = null;
		CSVWriter writerList23 = null;
		
		try{
			/*
			 * Read source file
			 */
			br= new BufferedReader(new FileReader(Path.DB_CACHE+"octoberDataset.tsv"));
			U.log("Loading input file here....");
			
			writerList14 = new CSVWriter(new FileWriter(path+"14_wrongList.csv"));
			writerList15 = new CSVWriter(new FileWriter(path+"15_wrongList.csv"));
			writerList16 = new CSVWriter(new FileWriter(path+"16_wrongList.csv"));
			writerList17 = new CSVWriter(new FileWriter(path+"17_wrongList.csv"));
			writerList18 = new CSVWriter(new FileWriter(path+"18_wrongList.csv"));
			writerList19 = new CSVWriter(new FileWriter(path+"19_wrongList.csv"));
			writerList20 = new CSVWriter(new FileWriter(path+"20_wrongList.csv"));
			writerList21 = new CSVWriter(new FileWriter(path+"21_wrongList.csv"));
			writerList22 = new CSVWriter(new FileWriter(path+"22_wrongList.csv"));
			writerList23 =new CSVWriter( new FileWriter(path+"23_wrongList.csv"));
//			
			U.log("Start writing csv here...");
			if(headerStatus){
				writerList14.writeNext(header);
				writerList15.writeNext(header);
				writerList16.writeNext(header);
				writerList17.writeNext(header);
				writerList18.writeNext(header);
				writerList19.writeNext(header);
				writerList20.writeNext(header);
				writerList21.writeNext(header);
				writerList22.writeNext(header);
				writerList23.writeNext(header);
			}
			
			String line = null;
			String[] fieldArray;
			while((line = br.readLine()) != null){
//				U.log(line);
				fieldArray = line.split("\t");

				if(fieldArray.length == 14){
					writerList14.writeNext(fieldArray);
				}else if(fieldArray.length == 15){
					writerList15.writeNext(fieldArray);
				}else if(fieldArray.length == 16){
					writerList16.writeNext(fieldArray);
				}else if(fieldArray.length == 17){
					writerList17.writeNext(fieldArray);
				}else if(fieldArray.length == 18){
					writerList18.writeNext(fieldArray);
				}else if(fieldArray.length == 19){
					writerList19.writeNext(fieldArray);
				}else if(fieldArray.length == 20){
					writerList20.writeNext(fieldArray);
				}else if(fieldArray.length == 21){
					writerList21.writeNext(fieldArray);
				}else if(fieldArray.length == 22){
					writerList22.writeNext(fieldArray);
				}else if(fieldArray.length == 23){
					writerList23.writeNext(fieldArray);
				}
			}//eof while

			writerList14.flush(); writerList14.close();
			writerList15.flush(); writerList15.close();
			writerList16.flush(); writerList16.close();
			writerList17.flush(); writerList17.close();
			writerList18.flush(); writerList18.close();
			writerList19.flush(); writerList19.close();
			writerList20.flush(); writerList20.close();
			writerList21.flush(); writerList21.close();
			writerList22.flush(); writerList22.close();
			writerList23.flush(); writerList23.close();
			br.close();
			U.log("Done all csv writing.....");
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

}
