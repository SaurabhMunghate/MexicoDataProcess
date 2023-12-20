package com.tequila.extraction.education;

import java.io.IOException;
import java.io.StringWriter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.utils.ApiKey;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;
import com.shatam.utils.Path;

public class FormatInegi extends DirectoryList{
	 
//	static String start_index="30000";
	static String start_index="50000";
//	static String end_index="34999";
	static String end_index="59999";
	static String sicSub="8221";
//	static String sicSub="5812";
//	static String fileName="InegiCSV/Restaurants/Inegi_Restaurant_"+start_index+"_"+end_index+".csv";
	static String fileName="InegiCSV/Inegi_Education_"+start_index+"_"+end_index+".csv";
	public static void main(String[] args) {
		FormatInegi ext=new FormatInegi();
		try {
			String filePath=Path.CACHE_PATH_FOR_EXTRACTION + fileName;
//			List<String[]> data=U.readCsvFile(filePath);
			ext.extractProcess();
			ext.printAll(fileName);
			ext.addExtraData();
//			ext.checkeSicCodes();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void checkeSicCodes() {
		try {
			HashMap<String,String>sicCodeSet=new HashMap<String,String>();
			
			String filePath="/home/chinmay/MexicoCache/InegiCache/EducationServicses.csv";
			List<String[]> data=U.readCsvFile(filePath);
			for (String[] da : data) {
				sicCodeSet.put(da[3],da[4]);
			}
//			for (String sicCode : sicCodeSet) {
//				U.log(NaiscToSicConversion.getSicForNaisc(sicCode));
//			}
			U.log(sicCodeSet);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	private void addExtraData() {
		String filePath=Path.CACHE_PATH_FOR_EXTRACTION + fileName;
		List<String[]> data=U.readCsvFile(filePath);
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			for (String[] inputArr : data) {
				String outputArrp[]=new String[inputArr.length+6];
				if (inputArr[0].contains("ID")) {
					System.arraycopy(inputArr, 0, outputArrp, 0, inputArr.length);
					outputArrp[inputArr.length]="HERE_ADDRESS_FORMATED";
					outputArrp[inputArr.length+1]="HERE_ADDRESS";
					outputArrp[inputArr.length+2]="HERE_COLONIA";
					outputArrp[inputArr.length+3]="HERE_CITY";
					outputArrp[inputArr.length+4]="HERE_STATE";
					outputArrp[inputArr.length+5]="HERE_POSTAL";
					writer.writeNext(outputArrp);
					continue;
				}
				
				System.arraycopy(inputArr, 0, outputArrp, 0, inputArr.length);
				String add1[]=getAddressFromLatlonHereApi(new String[] {inputArr[inputArr.length-4],inputArr[inputArr.length-3]});
				
				String add=getAddressFromLAtlonHereApi(new String[] {inputArr[inputArr.length-4],inputArr[inputArr.length-3]});
				outputArrp[inputArr.length]=add;
				System.arraycopy(add1, 0, outputArrp, inputArr.length+1, add1.length);
				writer.writeNext(outputArrp);
				U.log(Arrays.toString(outputArrp));
			}
			FileUtil.writeAllText(filePath, sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Override
	protected void extractProcess() throws Exception {
		String filePath="/home/chinmay/MexicoCache/InegiCache/EducationServicses.csv";
//		String filePath="/home/chinmay/MexicoCache/InegiCache/HotelsAndRest.csv";
		String companyDataUrl="https://www.inegi.org.mx/app/descarga/?ti=6";
		
		int count=0;
		
		List<String[]> data=U.readCsvFile(filePath);
		
		for (String[] nextLine : data) {
			if (nextLine[0].contains("id"))continue;
			count++;
			if (count<Integer.parseInt(start_index)||count>Integer.parseInt(end_index)) continue;
			String companyName=nextLine[1];
//			U.log(nextLine[5]);
			String empCount=!U.isEmpty(nextLine[5])?nextLine[5].replace("personas", "").replace(" a ", "-"):"";
//			U.log(nextLine[0]);
			String streetAdd=nextLine[6]+" "+nextLine[7]+" "+nextLine[14]+" "+nextLine[15]+" "+nextLine[16];
//			U.log(streetAdd);
			String colonia=nextLine[21]+" "+nextLine[22];
			String postalCode=nextLine[25];
			String city=nextLine[29];
			String state=nextLine[27];
			String phone=nextLine[34];
			String email=nextLine[35];
			String website=nextLine[36];
			String lat=nextLine[38];
			String lon=nextLine[39];
			
//			U.log(add);
//			U.log(Arrays.toString(add1));
			addCompanyDetailsFromMexico(sicMap.get(nextLine[3]), companyName, phone, ALLOW_BLANK, website);
//			addCompanyDetailsFromMexico(sicSub, companyName, phone, ALLOW_BLANK, website);
			addAddress(streetAdd, colonia, city, state, postalCode);
			addBoundaries(lat, lon);
			addReferenceUrl(companyDataUrl);
			addCompanyOtherDetails(ALLOW_BLANK, empCount, ALLOW_BLANK);
			addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, email);
//			add
//			break;
		}
	}
	public String getAddressFromLAtlonHereApi(String latlon[]) {
		try {
			String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox="+latlon[0]+"%2C"+latlon[1]+"%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
			String html=U.getHTML(url);
			if (html.contains("\"Address\":"))
				return U.getSectionValue(html, "\"Label\":\"", "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String[] getAddressFromLatlonHereApi(String latlon[]) {
		try {
			String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox="+latlon[0]+"%2C"+latlon[1]+"%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
			String html=U.getHTML(url);
			if (html.contains("\"Address\":")) {
				String add=U.getSectionValue(html, "\"Street\":\"", "\"")+" "+U.getSectionValue(html, "\"HouseNumber\":\"", "\"");
				String colonia=U.getSectionValue(html, "\"District\":\"", "\"");
				String city=U.getSectionValue(html, "\"City\":\"", "\"");
				String state=MXStates.getFullNameFromAbbr(U.getSectionValue(html, "\"State\":\"", "\""));
				String postal=U.getSectionValue(html, "\"PostalCode\":\"", "\"");
				return new String[] {add,colonia,city,state,postal};
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	static HashMap<String, String> sicMap=new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("721190", "7011");
			put("722320", "5812");
			put("721111", "7011");
			put("721210", "7033");
			put("722310", "5812");
			put("722330", "5963");
			put("721312", "7041");
			put("722511", "5812");
			put("722412", "5813");
			put("722512", "5812");
			put("721112", "7011");
			put("722411", "5813");
			
			put("721311", "7041");
			put("721113", "5812");
			put("722515", "5812");
			put("722516", "5812");
			put("722513", "5812");
			put("722514", "5812");
			put("722519", "5812");
			put("722517", "5812");
			put("722518", "5812");
			
			put("611622", "8299");
			put("611511", "8299");
			put("611698", "8299");
			put("611412", "8244");
			put("611632", "8299");
			put("611631", "8299");
			put("611312", "8221");
			put("611411", "8244");
			put("611311", "8211");
			put("611212", "8222");
			put("611432", "8299");
			put("611112", "8211");
			
			put("611211", "8222");
			put("611431", "8299");
			put("611111", "8211");
			put("611132", "8211");
			put("611131", "8211");
			put("611691", "8299");
			put("611152", "8211");
			put("611151", "8211");
			put("611172", "8299");
			
			put("611171", "8299");
			put("611612", "8299");
			put("611611", "8299");
			put("611512", "8299");
			put("611699", "8299");
			put("611710", "8299");
			put("611621", "8299");
			put("611422", "8243");
			put("611421", "8243");
			
			put("611122", "8211");
			put("611121", "8211");
			put("611142", "8211");
			put("611141", "8211");
			put("611162", "8211");
			put("611161", "8211");
			put("611182", "8211");
			put("611181", "8211");
			
		}	
	};
}
