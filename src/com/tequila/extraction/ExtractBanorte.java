package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractBanorte {
	private static String sicCode="6029";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		String postrurl="https://www.banorte.com/wps/portal/banorte/Home/inicio/!ut/p/a1/hc7LDoIwEAXQb2HBth15667BBEQiJEqEbgwYLBhsCVT4fZG4Mj5mdyfnZgZTnGLK86FmuawFz5tnptbJ0xaOr7uwjeJEB-JGiRuba-1g2RPIJgBfhsC__hHTmbge8Q07BIgMTwPi7AJjby412Fgv8ONEgClrRDG_mxFe6A7DtCsvZVd26N5N60rKtl-poMI4jogJwZoSnQWquQqfSpXoJU7fLW5vKVzNZgiJojwADVrE9w!!/dl5/d5/L2dBISEvZ0FBIS9nQSEh/pw/Z7_G218H3C0K8GB50AI44FVQI10K1/res/id=getCentros/c=cacheLevelPage/=/";
		HashSet<String> dupliData=new HashSet<>();
		HashSet<String> intit=new HashSet<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		int i=0;
		String postalCodeFileName="/home/chinmay/eclipse-workspace/DBReporting/Mexico_Address_Direcory_MiCodigo.csv";
		List<String[]> postalData=U.readCsvFileWithoutHeader(postalCodeFileName);
		try {
			for (String[] stateData : postalData) {
				String inti=stateData[3].substring(0, 3);
				if (!intit.add(inti)) continue;
				
//				if (!stateData[3].equals("3326")) continue;
				U.log(stateData[3]);
				
				String postData="flag=65798&lat="+stateData[7]+"&lng="+stateData[8]+"&brand=GF";
				try {
					String output=U.sendPostRequest(postrurl, postData);
					U.log(output);
					String data[]=U.getValues(output, "|", "false==");
					for (String processData : data) {
						U.log(processData);
						String[] listData=processData.split("\\|");
						String name="Banorte "+listData[0];
						String lat=listData[15];
						String lon=listData[16];
						String phone=listData[10];
						String addSec=listData[9];
						String add[]=U.getAddress(addSec);
						if (dupliData.add(addSec)) {
							U.log(Arrays.toString(add));
//							U.log(listData.length);
							String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,"https://www.banorte.com/",null,null,null,null,null,null,lat,lon,"https://www.banorte.com/",U.getTodayDate(),addSec};
							writer.writeNext(out);
							U.log(Arrays.toString(out));
						}
//						break;
					}
//					U.log(data.length);
//					i+=data.length;	 
				} catch (Exception e) {
					e.printStackTrace();
				}
//				break;
			}
			U.log("sda:= "+i);
			FileUtil.writeAllText(U.getCachePath()+"Banorte_Branches_test.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
