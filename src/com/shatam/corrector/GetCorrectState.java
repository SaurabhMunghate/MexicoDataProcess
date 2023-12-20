package com.shatam.corrector;

import java.util.ArrayList;
import java.util.List;

import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class GetCorrectState {
	static String header[]= {"ID","SIC_SUB","COMPANY_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","URL","EMAIL","STATUS","CONTACT_PERSON","TITLE","LATITUDE","LONGITUDE","YEARS_IN_BIZ","EMP_COUNT_MIN","EMP_COUNT_MAX","ANNUAL_SALES_VOL_MIN","ANNUAL_SALES_VOL_MAX","CREATED_DATE","SCORE","HOURS_OF_OPERATION","LOCATION_SOURCE","QUALITY_SCORE","GEO_DISTANCE","GEO_ACCURACY_CODE","ADDRESS_VERIFICATION_CODE"};
	public static void main(String[] args) {
		String inputfile="/home/chinmay/Mexico/Mexicoupdate/AddressCorrection_OCT_25/MexicoAddress_400001_450000.csv";
		String outputfile=inputfile.replace(".csv", "_correctState.csv");
		List<String[]> inputdata=U.readCsvFile(inputfile);
		List<String[]> outdata=new ArrayList<String[]>();
		outdata.add(header);
		for (String[] nextLine : inputdata) {
			String[] out=new String[header.length];
			out[0]=nextLine[0];
			out[6]=U.findStateFromZip(nextLine[5]);
			outdata.add(out);
		}
		U.writeCsvFile(outdata, outputfile);
	}
}	
