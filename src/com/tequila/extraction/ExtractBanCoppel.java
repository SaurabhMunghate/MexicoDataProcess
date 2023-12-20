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
import com.shatam.utils.Util;

public class ExtractBanCoppel {
	public static void main(String[] args) {
		extractData();
//		testCode();
	}
	private static void testCode() {
		try {
			HashSet<String>intit=new HashSet<>();
			String postalCodeFileName="/home/chinmay/eclipse-workspace/DBReporting/Mexico_Address_Direcory_MiCodigo.csv";
			List<String[]> postalData=U.readCsvFileWithoutHeader(postalCodeFileName);
			for (String[] postal : postalData) {
				U.log(Arrays.toString(postal));
				String inti=postal[3].substring(0, 2);
				if (intit.add(inti)) {
					
				}
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static String sicCode="6021";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int i=0;
			String srcUrl="https://www.bancoppel.com/geolocalizacion/";
			String mainUrl="https://www.bancoppel.com";
//			List<String[]> stateLatLonList=U.readCsvFile("/home/chinmay/eclipse-workspace/MexicoDataProcess/resources/stateLatLon.csv");
			HashSet<String>intit=new HashSet<>();
			String postalCodeFileName="/home/chinmay/eclipse-workspace/DBReporting/Mexico_Address_Direcory_MiCodigo.csv";
			List<String[]> postalData=U.readCsvFileWithoutHeader(postalCodeFileName);
			for (String[] stateData : postalData) {
				if (stateData[0].equals("State")) continue;
				String inti=stateData[3].substring(0, 3);
				if (!intit.add(inti)) continue;
				U.log(Arrays.toString(stateData));
				String jsonHtml=U.getHTML("https://www.bancoppel.com/CoppelSucursales/ServletConsultaSucursales?sucursalesCoordenadas={'longitud':"+stateData[8]+",'latitud':"+stateData[7]+"}&opcion=1&jsoncallback=jQuery320002454767578995609_1556162416303&_=1556162416305");
//				U.log(jsonHtml);
				String datas[]=U.getValues(jsonHtml, "{\"tipo\"", "}");
				U.log(datas.length);
				for (String data : datas) {
					U.log(data);
					String type=U.getSectionValue(data, ":\"", "\"");
					if (type.contains("SUC")) {
						sicCode="6021";
						
					}else {
						sicCode="6099";
					}
					String sicdetails[]=Sic.sicInfo(sicCode);
					String name="BanCoppel Mexico, "+U.getSectionValue(data, "\"nombre\":\"", "\"");
					String streetadd=U.getSectionValue(data, "\"direccion1\":\"", "\"").replaceAll(" N/A", "");
					String neigh=U.getSectionValue(data, "\"direccion2\":\"", "\"");
					String postalCode=U.getSectionValue(data, "\"dCodigo\":\"", "\"");
					if (neigh!=null&&postalCode==null) {
						postalCode=Util.match(neigh, "\\d{4,5}");
					}
					if (neigh!=null) {
						neigh=neigh.replaceAll("C\\.?P\\.? \\d{4,5}|Cp \\d{4,5}", "");
					}
					String phone=U.getSectionValue(data, "\"telefono\":\"", "\"");
					String lat=U.getSectionValue(data, "\"latitud\":", ",");
					String lon=U.getSectionValue(data, "\"longitud\":", ",");
//					String city=u.gets
					if(dupliData.add((streetadd+type+neigh).toLowerCase())){
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(streetadd),U.toTitleCase(neigh),"","",postalCode.trim(),U.formatNumbersAsCode(phone),null,mainUrl,null,null,null,null,null,null,lat,lon,srcUrl,U.getTodayDate()};
						writer.writeNext(out);
						U.log(Arrays.toString(out));
					}
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"BanCoppel_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
