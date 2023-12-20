package com.shatam.lightbox.loqate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class CreateUpdateFile {
	static String loqateOutputFile="/home/chinmay/Mexico/LightBoxAddress/Jan-17/MexicoAddress_28_FEB_Output.csv";
	//static String tequilaFile="/home/chinmay/Mexico/LightBoxAddress/MexicoAddress_350001_40000.csv";
	static HashMap<String , Integer> GAC=new HashMap<>();
	static HashMap<String , Integer> AVC=new HashMap<>();
	static HashMap<String , Integer> QS=new HashMap<>();
	static String[] header= {"ID","SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","EMAIL","STATUS","CONTACT_PERSON","TITLE","LATITUDE","LONGITUDE",
			"YEARS_IN_BIZ","EMP_COUNT_MIN","EMP_COUNT_MAX","ANNUAL_SALES_VOL_MIN","ANNUAL_SALES_VOL_MAX","CREATED_DATE","SCORE","HOURS_OF_OPERATION","LOCATION_SOURCE","QUALITY_SCORE","GEO_DISTANCE"
			,"GEO_ACCURACY_CODE","ADDRESS_VERIFICATION_CODE"};
	public static void main(String[] args) {
		processRecords();
	}
	//Geo Accuracy Code	
//	P4 in 417 records 
//	P3 in 22 records
//	I4 in 163 records
	
//	A4 in 10 records
	
//	A3 in 151 records
//	A2 in 97 records
//	U0 in 133 records
	private static void processRecords() {
		List<String[]> loqateRecords=U.readCsvFile(loqateOutputFile);
		//List<String[]> tequilaRecords=U.readCsvFile(tequilaFile);
//		HashMap<String, String> recordsId=new HashMap<>();
//		for (String[] tequila : tequilaRecords) {
//			recordsId.put(tequila[1].toLowerCase(), tequila[0]);
//		}
		ArrayList<String[]> outList=new ArrayList<>();
		outList.add(header);
		for (int i = 1; i < loqateRecords.size(); i++) {
			//P4, P3 I4 in quality score 1 and A2,A3, U0 in quality score 3 others are in quality score 2
//			U.log(Arrays.toString(out));
			String[] loqateRecord=loqateRecords.get(i);
			
			U.log(Arrays.toString(loqateRecord));
			String id=loqateRecord[0];
			U.log(id);
			String GEO_ACCURACY_CODE=loqateRecord[12];
			String GEO_DISTANCE= loqateRecord[13];
			float distance=2000;
			if(!GEO_ACCURACY_CODE.equals("U0")) {
				distance=Float.parseFloat(GEO_DISTANCE);
			}
			String ADDRESS_VERIFICATION_CODE=loqateRecord[11];
			if(GAC.containsKey(GEO_ACCURACY_CODE)) {
				int temp=GAC.get(GEO_ACCURACY_CODE);
				GAC.put(GEO_ACCURACY_CODE, ++temp);
			}else {
				GAC.put(GEO_ACCURACY_CODE, 1);
			}
			String AVC2Char=ADDRESS_VERIFICATION_CODE.substring(0, 2);
			if(AVC.containsKey(AVC2Char)) {
				int temp=AVC.get(AVC2Char);
				AVC.put(AVC2Char, ++temp);
			}else {
				AVC.put(AVC2Char, 1);
			}
			if((!GEO_ACCURACY_CODE.equals("U0"))||distance<1000) {
				//U.log(Arrays.toString(loqateRecord));
				
				String streetAddress=loqateRecord[6];
				String neigh=loqateRecord[7];
				String city=loqateRecord[8];
				String state=loqateRecord[9];
				String zip=loqateRecord[10];
				String lat=loqateRecord[14];
				String lon=loqateRecord[15];
				String LOCATION_SOURCE="LOQATE";
				String QUALITY_SCORE="0";
//				if(GEO_ACCURACY_CODE.equals("A4"))
//					QUALITY_SCORE="2";, V5, P4, or P5
				if((ADDRESS_VERIFICATION_CODE.startsWith("V4")||ADDRESS_VERIFICATION_CODE.startsWith("V5")||ADDRESS_VERIFICATION_CODE.startsWith("P4")||ADDRESS_VERIFICATION_CODE.startsWith("P5"))&&(GEO_ACCURACY_CODE.equals("P4")||GEO_ACCURACY_CODE.equals("P5")||GEO_ACCURACY_CODE.equals("P3"))) {
					QUALITY_SCORE="1";
				}else if((ADDRESS_VERIFICATION_CODE.startsWith("V4")||ADDRESS_VERIFICATION_CODE.startsWith("V3"))&&(GEO_ACCURACY_CODE.equals("I4"))&&distance<50) {
					QUALITY_SCORE="1";
				}else if((ADDRESS_VERIFICATION_CODE.startsWith("V3")||ADDRESS_VERIFICATION_CODE.startsWith("P3"))&&(GEO_ACCURACY_CODE.equals("P4")||GEO_ACCURACY_CODE.equals("P5")||GEO_ACCURACY_CODE.equals("P3"))&&distance<50) {
					QUALITY_SCORE="1";
				}else if(GEO_ACCURACY_CODE.equals("U0")||distance>1000) {
					QUALITY_SCORE="3";
				}else {
					QUALITY_SCORE="2";
				}
				
				String out[]= {id,"","",streetAddress,neigh,city,state,zip,"","","","","","","",lat,lon,"","","","","","","","",LOCATION_SOURCE,QUALITY_SCORE,GEO_DISTANCE,GEO_ACCURACY_CODE,ADDRESS_VERIFICATION_CODE};
				//U.log(header.length);
			//	U.log(out.length);
				//U.log(Arrays.toString(out));
				outList.add(out);
				if(QS.containsKey(QUALITY_SCORE)) {
					int temp=QS.get(QUALITY_SCORE);
					QS.put(QUALITY_SCORE, ++temp);
				}else {
					QS.put(QUALITY_SCORE, 1);
				}
			}else {
				String out[]= {id,"","","","","","","","","","","","","","","","","","","","","","","","","","3",GEO_DISTANCE,GEO_ACCURACY_CODE,ADDRESS_VERIFICATION_CODE};
			//	U.log(header.length);
				//U.log(out.length);
				//U.log(Arrays.toString(out));
				outList.add(out);
			}
		}
		U.log(loqateRecords.size()+"  "+outList.size());
		U.writeCsvFile(outList, loqateOutputFile.replace("/home/chinmay/Mexico/LightBoxAddress/", "/home/chinmay/Mexico/Mexicoupdate/"));
		U.log("AVC "+AVC);
		U.log("-----");
		U.log("GAC "+GAC);
		U.log("-----");
		U.log("QS "+QS);
	}
}
