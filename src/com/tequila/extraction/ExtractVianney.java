package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractVianney {
	private static String sicCode="2399";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		String sicdetails[]=Sic.sicInfo(sicCode);
		String posetReqUrl="https://www.vianney.com.mx/Ubicanos/ObtenerTiendas";
		String baseURL="https://www.vianney.com.mx/Ubicanos";
		int i=0;
		try {
			String baseHtml=U.getHTML(baseURL);
			String statesSec=U.getSectionValue(baseHtml, "<option value=\"0\">SELECCIONA UN ESTADO</option>", "</select>");
			String statesVal[]=U.getValues(statesSec, "<option", "/option>");
			for (String stateVal : statesVal) {
				String state=U.getSectionValue(stateVal, "\">", "<");
//				if (!stateVal.contains("\"22\"")) continue;
				String formData="idEstado="+U.getSectionValue(stateVal, "value=\"", "\"");
				String postData=U.sendPostRequest(posetReqUrl, formData);
//				U.log(postData);
//				U.log(StringEscapeUtils.unescapeHtml3(state));
				state=U.matchState(StringEscapeUtils.unescapeHtml3(state));
				U.log(state);
				String listings[]=U.getValues(postData, "{\"id\":\"", "}");
//				U.log(listings.length);
				for (String listing : listings) {
					String teiendaID=U.getSectionValue(listing, "idTienda\":\"", "\"");
					if (teiendaID!=null&&teiendaID.length()>0) {
//						U.log(listing);
						listing=StringEscapeUtils.unescapeHtml3(listing);
						
						
						U.log(listing);
						String name="Vianney Textil Hogar, S.A. De C.V.";
						String streetAddress=U.getSectionValue(listing, "\"Domicilio\":\"", "\"");
						String city=U.getSectionValue(listing, "\"Delegacion\":\"", "\"");
						String codigoPostal=U.getSectionValue(listing, "\"CodigoPostal\":\"", "\"");
						String phone=U.getSectionValue(listing, "Telefono\":\"", "\"").replaceAll("Y|y", ";");
						String mobile=U.getSectionValue(listing, "\"Movil\":\"", "\"").replaceAll("Y|y", ";");
						if (phone.length()>1&&mobile.length()>1) {
							phone+=";"+mobile;
						}else if (phone.length()<1&&mobile.length()>1) {
							phone=mobile;
						}
//						U.log(phone);
						String email=U.getSectionValue(listing, "\"Email\":\"", "\"");
						String lat=U.getSectionValue(listing, "\"Latitud\":\"", "\"");
						String lon=U.getSectionValue(listing, "\"Longitud\":\"", "\"");
						String colonia=U.getSectionValue(listing, "\"Colonia\":\"", "\"");
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(streetAddress),U.toTitleCase(colonia),U.toTitleCase(city),U.toTitleCase(state),codigoPostal.trim(),U.formatNumbersAsCode(phone),null,"https://www.vianney.com.mx",email,null,null,null,null,null,lat,lon,baseURL,U.getTodayDate()};
						writer.writeNext(out);
						U.log(Arrays.toString(out));
					}
					
					 
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Vianney.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
