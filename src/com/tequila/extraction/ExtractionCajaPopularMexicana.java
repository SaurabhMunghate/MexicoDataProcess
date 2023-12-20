package com.tequila.extraction;

import java.io.StringWriter;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractionCajaPopularMexicana {
	public static void main(String[] args) {
		extractData();
	}
	private static String sicCode="6061";
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
			String sicdetails[]=Sic.sicInfo(sicCode);
			String baseUrl="https://www.cpm.coop/buscador_sucursales/sucursales.php";
			String website="https://www.cpm.coop";
			String baseHtml=U.getHTML(baseUrl);
			int i=0;
			String sections=U.getSectionValue(baseHtml, "var features =", "features.forEach");
			String listingSecs[]=U.getValues(sections, "position", "},");
			for (String listing : listingSecs) {
				U.log(listing);
				String name="Caja Popular Mexicana "+U.getSectionValue(listing, "sucursal: \"", "\"");
				String streetAdd=U.getSectionValue(listing, "direccion: \"", "\"");
				String neigh=U.getSectionValue(listing, "colonia: \"", "\"");
				String city=U.getSectionValue(listing, "ciudad: \"", "\"");
				String state=U.getSectionValue(listing, "estado: \"", "\"");
				String postalCode=U.getSectionValue(listing, "cp: \"", "\"");
				String phone=U.getSectionValue(listing, "tel: \"", "\"");
				String latLon[]=U.getSectionValue(listing, "LatLng(", ")").split(",");
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(streetAdd),U.toTitleCase(neigh),U.toTitleCase(city),U.toTitleCase(state),postalCode,U.formatNumbersAsCode(phone),null,website,"",null,null,null,null,null,latLon[0],latLon[1],baseUrl,U.getTodayDate()};
				writer.writeNext(out);
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Caja_Popular_Mexicana_Branches.csv", sw.toString());
			
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
