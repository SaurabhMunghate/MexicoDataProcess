package com.shatam.scrapper;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.U;

public class CorrectorAndUniqueCSV {

//	private static final String DIR_PATH = "/home/shatam-10/MexicoCache/Cache/";
	private static final String DIR_PATH = "/home/shatam-100/MexicoCache/Cache/";
	
//	private static final String UNIQUE_FILE_PATH = DIR_PATH + "/All_Unique_Record_16_01_2023.csv";
	private static final String UNIQUE_FILE_PATH = DIR_PATH + "/All_Unique_Record_0_224000_190000_200000_F1_From_0-2300.csv";
	
	/*
	 * For Single File
	 */
	private static final String ALL_RECORD_FILE_PATH = DIR_PATH + "All_Record.csv";
	
//	private static final String SINGLE_FILE = "/home/shatam-10/MexicoCache/Cache/Inegi_Information_4000 (charumam)_CORRECT_NW_REC.csv";
//	private static final String SINGLE_FILE = "/home/shatam-100/Desktop/MexicoCacheUniqueRecord/Inegi_Information_0_224000_60001_70000_S_F_7500_to_10000_.csv";
	private static final String SINGLE_FILE = "/home/shatam-100/MexicoCache/Cache/Inegi_Information_0_224000_190000_200000_F1_From_0-2300_CORRECT_NW_REC.csv";
	
	public static void main(String[] args) {
		CorrectorAndUniqueCSV corrector = new CorrectorAndUniqueCSV();
		corrector.loadAllFiles();
//		corrector.loadReadFile(false);
	}
	//https://ppmls.mlsmatrix.com/Matrix/Public/IDXSearch.aspx?c=AAEAAAD*****AQAAAAAAAAARAQAAAEMAAAAGAgAAAAEwDT8GAwAAAAfClMOCVsOYDQIL&idx=2cfc1ee
	//__EVENTTARGET=m_DisplayCore&__EVENTARGUMENT=Redisplay%7C%2C%2C25&__VIEWSTATE=%2FwEPDwUKMTEwMjAyMTg1NQ8WCB4JSW1wVXNlcklEBQEyHgxFbmNvZGVkUXVlcnky2hAAAQAAAP%2F%2F%2F%2F8BAAAAAAAAAAwCAAAASEVuY29kZWRRdWVyeVRyZWUsIFZlcnNpb249MTAuNy4xLjEsIEN1bHR1cmU9bmV1dHJhbCwgUHVibGljS2V5VG9rZW49bnVsbAUBAAAAH0VuY29kZWRRdWVyeVRyZWUuQmFzZS5FbmNvZGVkUVQHAAAAGEluY2x1ZGVJbmNvbXBsZXRlUmVjb3JkcwptX1NvcnRMYW5nDk1hbnVhbFNvcnRLZXlzDElzTWFudWFsU29ydAdtX1RvcFFGClNvcnRGaWVsZHMKbV9uVGFibGVJRAAAAgAEAwABCAEfRW5jb2RlZFF1ZXJ5VHJlZS5CYXNlLlFGQW5kTGlzdAIAAAAcU3lzdGVtLkNvbGxlY3Rpb25zLkFycmF5TGlzdAgCAAAAAAEAAAAKAAkDAAAACQQAAAAJAAAADAUAAABBTWF0cml4TUFMLCBWZXJzaW9uPTEwLjcuMS4xLCBDdWx0dXJlPW5ldXRyYWwsIFB1YmxpY0tleVRva2VuPW51bGwFAwAAAB9FbmNvZGVkUXVlcnlUcmVlLkJhc2UuUUZBbmRMaXN0BgAAAAZRRkxpc3QIbkZpZWxkSUQHQ3JlYXRvcgRKb2luBlVTRkNJRAhVU0ZDVHlwZQMAAAQAABxTeXN0ZW0uQ29sbGVjdGlvbnMuQXJyYXlMaXN0CAgWTWF0cml4LkNvbXBvbmVudHMuSm9pbgUAAAAICAIAAAAJBgAAAP%2F%2F%2F%2F8IAAAACv%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FBAQAAAAcU3lzdGVtLkNvbGxlY3Rpb25zLkFycmF5TGlzdAMAAAAGX2l0ZW1zBV9zaXplCF92ZXJzaW9uBQAACAgJBwAAAAEAAAABAAAAAQYAAAAEAAAACQgAAAACAAAAAgAAABAHAAAABAAAAAkJAAAADQMQCAAAAAQAAAAJCgAAAAkLAAAADQIMDAAAAERNYXRyaXhDb21tb24sIFZlcnNpb249MTAuNy4xLjEsIEN1bHR1cmU9bmV1dHJhbCwgUHVibGljS2V5VG9rZW49bnVsbAUJAAAAH0VuY29kZWRRdWVyeVRyZWUuQmFzZS5Tb3J0RmllbGQDAAAACURpcmVjdGlvbgdGaWVsZElEBEpvaW4EAAQQTWF0cml4LkRpcmVjdGlvbgwAAAAIFk1hdHJpeC5Db21wb25lbnRzLkpvaW4FAAAAAgAAAAXz%2F%2F%2F%2FEE1hdHJpeC5EaXJlY3Rpb24BAAAAB3ZhbHVlX18ACAwAAAABAAAAcwAAAAkOAAAABQoAAAAeRW5jb2RlZFF1ZXJ5VHJlZS5CYXNlLlFGTG9va3VwBwAAAA9zdHJTZWxlY3RWYWx1ZXMLIG1fT3BlcmF0b3IIbkZpZWxkSUQHQ3JlYXRvcgRKb2luBlVTRkNJRAhVU0ZDVHlwZQYEAAAEAAAMTWF0cml4LkxvZ2ljDAAAAAgIFk1hdHJpeC5Db21wb25lbnRzLkpvaW4FAAAACAgCAAAACQ8AAAAF8P%2F%2F%2FwxNYXRyaXguTG9naWMBAAAAB3ZhbHVlX18ACAwAAAABAAAAfgAAAAgAAAAK%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8FCwAAAB5FbmNvZGVkUXVlcnlUcmVlLkJhc2UuUUZPckxpc3QGAAAABlFGTGlzdAhuRmllbGRJRAdDcmVhdG9yBEpvaW4GVVNGQ0lECFVTRkNUeXBlAwAABAAAHFN5c3RlbS5Db2xsZWN0aW9ucy5BcnJheUxpc3QICBZNYXRyaXguQ29tcG9uZW50cy5Kb2luBQAAAAgIAgAAAAkRAAAA%2F%2F%2F%2F%2FwUAAAAK%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8FDgAAABZNYXRyaXguQ29tcG9uZW50cy5Kb2luAQAAAAhuSm9pbklEcwMcU3lzdGVtLkNvbGxlY3Rpb25zLkFycmF5TGlzdAUAAAAKEQ8AAAAEAAAABhIAAAADMTAxBhMAAAAEOTAyMgYUAAAABDkwMjMGFQAAAAQ5MDI0AREAAAAEAAAACRYAAAACAAAAAgAAABAWAAAABAAAAAkXAAAACRgAAAANAgUXAAAAH0VuY29kZWRRdWVyeVRyZWUuQmFzZS5RRk51bWVyaWMGAAAADU51bWVyaWNWYWx1ZXMIbkZpZWxkSUQHQ3JlYXRvcgRKb2luBlVTRkNJRAhVU0ZDVHlwZQQAAAQAAC5FbmNvZGVkUXVlcnlUcmVlLkJhc2UuUUZOdW1lcmljK051bWVyaWNWYWx1ZVtdAgAAAAgIFk1hdHJpeC5Db21wb25lbnRzLkpvaW4FAAAACAgCAAAACRkAAABLAAAABQAAAAr%2F%2F%2F%2F%2F%2F%2F%2F%2F%2FwEYAAAAFwAAAAkaAAAAOQAAAAUAAAAK%2F%2F%2F%2F%2F%2F%2F%2F%2F%2F8HGQAAAAABAAAAAQAAAAQsRW5jb2RlZFF1ZXJ5VHJlZS5CYXNlLlFGTnVtZXJpYytOdW1lcmljVmFsdWUCAAAACRsAAAAHGgAAAAABAAAAAQAAAAQsRW5jb2RlZFF1ZXJ5VHJlZS5CYXNlLlFGTnVtZXJpYytOdW1lcmljVmFsdWUCAAAACRwAAAAFGwAAAC9FbmNvZGVkUXVlcnlUcmVlLkJhc2UuUUZOdW1lcmljK051bWVyaWNJbnRWYWx1ZQQAAAAJaW50VmFsdWUxCWludFZhbHVlMgRiTm90DUZpZWxkT3BlcmF0b3IAAAAECQkBD01hdHJpeC5PcGVyYXRvcgwAAAACAAAAbweSAQAAAABvB5IBAAAAAAAF4%2F%2F%2F%2Fw9NYXRyaXguT3BlcmF0b3IBAAAAB3ZhbHVlX18ACAwAAAACAAAAARwAAAAbAAAAbweSAQAAAABvB5IBAAAAAAAB4v%2F%2F%2F%2BP%2F%2F%2F8CAAAACx4HU2V0U2l6ZQIrHhNWYWxpZGF0ZVJlcXVlc3RNb2RlAgEWBAIBD2QWAgICDxYCHgRUZXh0BWU8bGluayByZWw9InN0eWxlc2hlZXQiIHR5cGU9InRleHQvY3NzIiBocmVmPSIvTWF0cml4L1B1YmxpYy9UZW1wbGF0ZS9Dc3MuYXNoeD90PTIxMCZwYnM9dHJ1ZSZ2PTI5Ij4NCmQCAw8WAh4GYWN0aW9uBXAvTWF0cml4L1B1YmxpYy9JRFhTZWFyY2guYXNweD9jPUFBRUFBQUQqKioqKkFRQUFBQUFBQUFBUkFRQUFBRU1BQUFBR0FnQUFBQUV3RFQ4R0F3QUFBQWZDbE1PQ1ZzT1lEUUlMJmlkeD0yY2ZjMWVlFgoCEQ8WAh4HVmlzaWJsZWcWAgIDDw8WAh8GaGRkAhMPZBYCAgEPFgIfBGVkAhUPDxYCHwZoZGQCFw9kFgICCQ9kFgICAQ8WAh4FY2xhc3MFKGNvbC14cy0xMiBpcy1ub25yZXNwb25zaXZlIG5vbnJlc3BvbnNpdmVkAhkPFgIfBmcWAgIBDxYCHwQF9ANUaGUgY29udGVudCByZWxhdGluZyB0byByZWFsIGVzdGF0ZSBmb3Igc2FsZSBpbiB0aGlzIFdlYiBzaXRlIGNvbWVzIGluIHBhcnQgZnJvbSB0aGUgSW50ZXJuZXQgRGF0YSBlWGNoYW5nZSAo4oCcSURY4oCdKSBwcm9ncmFtIG9mIFBpa2VzIFBlYWssIE11bHRpcGxlIExpc3RpbmcgU2VydmljZS4gUmVhbCBlc3RhdGUgbGlzdGluZ3MgaGVsZCBieSBicm9rZXJzIG90aGVyIHRoYW4gTWVnYW4gRmFycmVsbCBNUlAgb2YgUHJvcGVydHkgU29sdXRpb25zIFRlYW0gTExDIGFyZSBtYXJrZWQgd2l0aCB0aGUgSURYIExvZ28uIFRoaXMgaW5mb3JtYXRpb24gaXMgYmVpbmcgcHJvdmlkZWQgZm9yIHRoZSBjb25zdW1lcnPigJkgcGVyc29uYWwsIG5vbi1jb21tZXJjaWFsIHVzZSBhbmQgbWF5IG5vdCBiZSB1c2VkIGZvciBhbnkgb3RoZXIgcHVycG9zZS4gQWxsIGluZm9ybWF0aW9uIHN1YmplY3QgdG8gY2hhbmdlIGFuZCBzaG91bGQgYmUgaW5kZXBlbmRlbnRseSB2ZXJpZmllZC7igJ0NCmRkoOh3b6grXQWqTY7TVDH3dwuedms%3D&__VIEWSTATEGENERATOR=AC30CA1B&_ctl0Contact%24m_hfLeadSignUpErrors=&_ctl0Contact%24m_hfLeadListAgentID=&_ctl0Contact%24m_hfLeadListAgentName=&_ctl0Contact%24m_hfLeadMessage=&_ctl0Contact%24m_hfLeadHeaderMessage=
	void loadAllFiles(){	
		
		List<String[]> readAllLines = new ArrayList<>();
		
		Set<String[]> header = new HashSet<>();
		
		File[] files = new File(DIR_PATH).listFiles();
		
		for(File file : files){
			U.log("File Name ::"+file.getName());
			if(file.isDirectory()){
				U.log("Directory found so continue");
				continue;
			}
			List<String[]> readLines = loadFiles(file);
			U.log("Record Size ::"+readLines.size());
			header.add(readLines.get(0));
			readLines.remove(0);
			readAllLines.addAll(readLines);
		}
		
		U.log("Size of header ::"+header.size());
		U.log("Read All Records ::"+readAllLines.size());
		
		for(String[] head : header){
//			U.log(Arrays.toString(head));
		}
		
		loadUniqueRecords(readAllLines, new ArrayList<>(header).get(0));
	}
	
	
	void loadReadFile(boolean headerStatus){
		List<String[]>  readLines = null;

		CSVReader reader = null;
		try{
			reader = new CSVReader(new FileReader(DIR_PATH+SINGLE_FILE));
			readLines = reader.readAll();
			reader.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		U.log("Load input file...... Done");
		
		if(headerStatus){
			writeFile(readLines);			
		}else{
			readLines.remove(0);
			writeFile(readLines);
		}
	}
	void writeFile(List<String[]>  readLines){
		CSVWriter writer = null;
		try{
			writer = new CSVWriter(new FileWriter(ALL_RECORD_FILE_PATH,true),',');
			writer.writeAll(readLines);
			writer.flush();
			writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		U.log("Writing validate file.....Done");
	}
	
	void loadUniqueRecords(List<String[]> readAllLines,String[] header){
		TreeMap<String, String[]> uniqueLines = new TreeMap<>();
		
		int exactMatchDup = 0;
		int notExactMatchDup = 0;
		int notMatchFieldCount = 0;
		int updateRowCount=0;
		
		
		String[] lines = null;
		int x = 0;
		Iterator<String[]> it = readAllLines.iterator();
		while (it.hasNext()) {
//		for(String[] lines : readAllLines){
			
			lines= it.next();
			/*
			 * uniqueKey = SIC_SUB + COMPANY_NAME + ADDRESS +  CITY + STATE +  CONTACT_PERSON;
			 */
			
			U.log(Arrays.toString(lines));
		
			
			String uniqueKey = TranslateEnglish.convertToEnglish((lines[4].trim()+lines[7].trim()+lines[8].trim()+lines[10].trim()+lines[11].trim()+lines[17].trim()).toLowerCase());
			U.log("uniqueKey: "+uniqueKey);
			
			U.log("uniqueLines size: "+uniqueLines.size());
			
			if(uniqueLines.containsKey(uniqueKey)){
				if(!compares(uniqueLines.get(uniqueKey), lines)){
					
					String[] oldData = uniqueLines.get(uniqueKey);
					if(oldData[13].trim().isEmpty()){
						if(!lines[13].trim().isEmpty()){
							oldData[13] = lines[13].trim(); //phones							
						}
					}else if(!oldData[13].trim().isEmpty()){
						if(!lines[13].trim().isEmpty()){
							if(!oldData[13].contains(lines[13].trim())){
								oldData[13] = oldData[13].trim()+";"+lines[13].trim(); 
							}
						}
					}
					if(oldData[14].trim().isEmpty()){
						if(!lines[14].trim().isEmpty()){
							oldData[14] = lines[14].trim(); //fax							
						}
					}else if(!oldData[14].trim().isEmpty()){
						if(!lines[14].trim().isEmpty()){
							if(!oldData[14].equalsIgnoreCase(lines[14].trim())){
								oldData[14] = oldData[14].trim()+";"+lines[14].trim(); 
							}
						}
					}
					if(oldData[15].trim().isEmpty()){
						if(!lines[15].trim().isEmpty()){
							oldData[15] = lines[15].trim(); //url							
						}
					}
					
					if(oldData[16].trim().isEmpty()){
						if(!lines[16].trim().isEmpty()){
							oldData[16] = lines[16].trim(); //EMAIL							
						}
					}else if(!oldData[16].trim().isEmpty()){
						if(!lines[16].trim().isEmpty()){
							if(!oldData[16].contains(lines[16].trim())){
								oldData[16] = oldData[16].trim()+";"+lines[16].trim(); 
							}
						}
					}
					if(oldData[17].trim().isEmpty()){
						if(!lines[17].trim().isEmpty()){
							oldData[17] = lines[17].trim(); //contact person						
						}
					}
					if(oldData[18].trim().isEmpty()){
						if(!lines[18].trim().isEmpty()){
							oldData[18] = lines[18].trim(); //title							
						}
					}
					if(oldData[19].trim().isEmpty()){
						if(!lines[19].trim().isEmpty()){
							oldData[19] = lines[19].trim(); //annual_sales							
						}
					}
					if(oldData[20].trim().isEmpty()){
						if(!lines[20].trim().isEmpty()){
							oldData[20] = lines[20].trim(); //emp_count							
						}
					}
					if(oldData[21].trim().isEmpty()){
						if(!lines[21].trim().isEmpty()){
							oldData[21] = lines[21].trim(); //year_in_biz							
						}
					}
					if(oldData[22].trim().isEmpty()){
						if(!lines[22].trim().isEmpty()){
							oldData[22] = lines[22].trim(); //latitude							
						}
					}
					if(oldData[23].trim().isEmpty()){
						if(!lines[23].trim().isEmpty()){
							oldData[23] = lines[23].trim(); //longitude							
						}
					}
					
					notMatchFieldCount = comparesNotMatchFieldCount(uniqueLines.get(uniqueKey), lines);
//					String newData[] = comparesNotMatchFieldCount(uniqueLines.get(uniqueKey), lines,notMatchFieldCount);
					
					for(Entry<String, String[]> entry :uniqueLines.entrySet()){
						if(entry.getKey().equals(uniqueKey)){
							entry.setValue(oldData);
							updateRowCount++;
						}
					}
					if(notMatchFieldCount > 1)
					{
/*					U.log("Not Match Field Count ::"+notMatchFieldCount);
					U.errLog(Arrays.toString(uniqueLines.get(uniqueKey)));
					U.log(Arrays.toString(lines));
*/					}
					
					notExactMatchDup++;
				}else{
					exactMatchDup++;
				}
				
			}else{
				U.log("CAME HERE ----------- ");
				uniqueLines.put(uniqueKey, lines);
			}
//			break;
		}
		
		
		U.log("Total Unique Records ::"+uniqueLines.size());
		U.log("Exact Match Duplicate Unique Record ::"+exactMatchDup);
		U.log("Not Exact Match Duplicate Unique Record ::"+notExactMatchDup);
		U.log("Total remove records ::"+(readAllLines.size()-uniqueLines.size()));
		U.log("Update row records count ::"+updateRowCount);
		
		
		writeUniqueRecords(header, uniqueLines);
	}
	
	List<String[]> loadFiles(File file){
		List<String[]> readLines = null;
		try(CSVReader reader = new CSVReader(new FileReader(file));){
			readLines = reader.readAll();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return readLines;
	}
	
	boolean compares(String[] oldData, String[] newData){
		
		if(oldData.length == newData.length){
			
			for(int i = 0; i < oldData.length; i++){
				if(!oldData[i].trim().equalsIgnoreCase(newData[i].trim())){
					return false;
				}
			}
			return true;
		}		
		return false;
	}
	
	int comparesNotMatchFieldCount(String[] oldData, String[] newData){
		int notMatch = 0;
		if(oldData.length == newData.length){
			
			for(int i = 1; i < oldData.length; i++){
				if(!oldData[i].trim().equalsIgnoreCase(newData[i].trim())){
					U.log(oldData[i]+"\t"+newData[i]);
					notMatch++;
				}
			}
		}		
		return notMatch;
	}
	String[] comparesNotMatchFieldCount(String[] oldData, String[] newData, int notMatchCount){
		
		if((oldData.length == newData.length) && notMatchCount == 1){
			
			for(int i = 1; i < oldData.length; i++){
				if(!oldData[i].trim().equalsIgnoreCase(newData[i].trim())){
					if(U.matches(oldData[i],"\\d+-\\d+-\\d+((;\\d+-\\d+-\\d+)?(;\\d+-\\d+-\\d+)?(;\\d+-\\d+-\\d+)?)?")){
						oldData[i] +=";"+newData[i];
					}
					U.log(oldData[i]+"\t"+newData[i]);
				}
			}
		}		
		return oldData;
	}
	
	void writeUniqueRecords(String[] header, TreeMap<String, String[]> uniqueLines){
		try(CSVWriter writer = new CSVWriter(new FileWriter(UNIQUE_FILE_PATH),',');){
			writer.writeNext(header);
			writer.writeAll(uniqueLines.values());
			writer.flush();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		U.log("Writing unique record is done..");
		U.log(UNIQUE_FILE_PATH);
	}
}
