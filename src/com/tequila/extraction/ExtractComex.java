package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractComex {
	private static String sicCode="5231";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		HashSet<String> dupliData=new HashSet<>();
		HashSet<String> postalCode=new HashSet<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		String postalCodeFileName="/home/chinmay/eclipse-workspace/DBReporting/Mexico_Address_Direcory_MiCodigo.csv";
		List<String[]> postalData=U.readCsvFileWithoutHeader(postalCodeFileName);
		try {
			int i=0;
			for (String[] postalDa : postalData) {
				if (!postalCode.add(postalDa[3])) {
					continue;
				}
				U.log(postalDa[3]);
				
				String srcUrl="https://tienda.comex.com.mx/selecciona-tu-tienda";
				String mainUrl="https://www.comex.com.mx/";
				String latLonComexData = U.getHTML("https://tienda.comex.com.mx/public/v1/locations/postalCodes/"+postalDa[3]);
//				U.log(latLonComexData);
				if (latLonComexData==null) {
					continue;
				}
				String lat=U.getSectionValue(latLonComexData, "\"latitude\":", ",");
				String lon=U.getSectionValue(latLonComexData, "\"longitude\":", ",");
				String listingsPage=U.getHTML("https://tienda.comex.com.mx/public/v1/stores/location/"+lat+","+lon+"?radius=200");	
//				U.log(listingsPage);
				String listingsData[]=U.getValues(listingsPage, "{\"country\"", "}");
//				U.log(listingsData.length);
				
				for (String listingData : listingsData) {
					U.log(listingData);
					String add[]= {"","","","",""};
					add[0]=U.getSectionValue(listingData, "\"address1\":\"", "\"");
					add[1]=U.getSectionValue(listingData, "\"address2\":\"", "\"");
					add[2]=U.getSectionValue(listingData, "\"city\":\"", "\"");
					add[4]=U.getSectionValue(listingData, "\"postalCode\":\"", "\"");
					add[3]=U.findStateFromZip(add[4]);
					String phone=U.getSectionValue(listingData, "\"phoneNumber\":\"", "\"");
					String latitude=U.getSectionValue(listingData, "\"latitude\":", ",");
					String longitude=U.getSectionValue(listingData+",", "\"longitude\":", ",");
					String email=U.getSectionValue(listingData, "\"email\":\"", "\"");
					if(dupliData.add((add[0]+add[1]+add[2]+add[3]+phone).toLowerCase())){
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase("Comex"),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,mainUrl,email,null,null,null,null,null,latitude,longitude,srcUrl,U.getTodayDate()};
						writer.writeNext(out);
						U.log(Arrays.toString(out));
					}
				}
			}
			FileUtil.writeAllText(U.getCachePath()+"Comex_Tienda.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
