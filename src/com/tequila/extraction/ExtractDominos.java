package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractDominos {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="7231";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int i=0;
			String sicdetails[]=Sic.sicInfo(sicCode);
			HashSet<String> uniqueSet=new HashSet<>();
			String website="http://dominos.com.mx";
			String baseUrl="http://dominos.com.mx/wp-admin/admin-ajax.php";
			String baseData="action=getSucursalesbyEstadoAJAX&estado=none";
			String data=U.sendPostRequest(baseUrl, baseData);
//			U.log(data);
			
			String listings[]=U.getValues(data, "col-6 col-sm-4 px-0", "MAPA");
			U.log(listings.length);
			for (String listing : listings) {
				U.log(listing);
				String latLonSec=U.getSectionValue(listing, "data-cordenadas=\\\"", "\\\"");
				U.log(latLonSec);
				String latLon[]=latLonSec.split(",");
				if (latLon.length!=2) {
					latLon=new String[] {"",""};
				}
				String telePhone=U.getSectionValue(listing, "informacion-sucursal\\\"><div>", "<\\/div>").replaceAll(" Tel. ", "");
				U.log(telePhone);
				String companyName="Dominos "+U.getSectionValue(listing, "data-sucursal=\\\"", "\\\"");;
				String addressSec=U.getSectionValue(listing, "data-direccion=\\\"", "\\\"");
				String add[]=U.getAddress(StringEscapeUtils.unescapeJson(addressSec).replace("Col. Rancho Guadalupe. ", "Col. Rancho Guadalupe, "));
				U.log(Arrays.toString(add));
				if (uniqueSet.add((companyName+add[0]+add[2]).toLowerCase())) {
					String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(telePhone),null,website,null,null,null,null,null,null,latLon[0],latLon[1],baseUrl,U.getTodayDate()};
					writer.writeNext(out);
					U.log(Arrays.toString(out));
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Dominos_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
