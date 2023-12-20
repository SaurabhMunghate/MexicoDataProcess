package com.tequila.extraction;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractGenerix {
	private static String sicCode="5912";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int i=0;
			HashSet<String> uniqueSet=new HashSet<>();
			String sicdetails[]=Sic.sicInfo(sicCode);
			String companyName="GENERIX";
			String baseHtml=U.getHTML("http://generix.com.mx/");
			String estateSecs=U.getSectionValue(baseHtml, "<option value=\"\">Estado...</option>", "</select>");
			String[] states=U.getValues(estateSecs, "<option value=\"", "\"");
			String cityurl="http://generix.com.mx/ciudades.php";
			String lisitngsUrl="http://generix.com.mx/valida_mapita.php";
			String sucursalesData=U.getHTML("http://generix.com.mx/js/funciones.js");
			for (String state : states) {
				String cityPayLoad="idEstado="+state;
				U.log(cityPayLoad);
				String cityData=U.sendPostRequest(cityurl, cityPayLoad).replaceAll("<option value=\"\" id=\"\">Seleccione...</option>", "");
				String cityDatas[]=U.getValues(cityData, "<option value=\"", "\"");
				
				for (String city : cityDatas) {
					String listingData=U.sendPostRequest(lisitngsUrl, "estado="+state+"&ciudad="+city);
//					U.log(listingData);
					String lisitngs[]= U.getValues(listingData, "{\"titulo\"", "}");
					for (String lisitng : lisitngs) {
						U.log(lisitng);
						String sucData=U.getSectionValue(lisitng, "\"idSucursal\":\"", "\"");
						String extraDataSec=U.getSectionValue(sucursalesData, "\""+sucData+"\"==", ".html(\"Horario:");
						U.log(extraDataSec);
						String telephone=U.getSectionValue(extraDataSec, ".html(\"Tel. ", "\"");
						U.log(telephone);
						String lat=U.getSectionValue(lisitng, "\"lat\":\"", "\"");
						String lon=U.getSectionValue(lisitng, "\"lng\":\"", "\"");
						String addressSec=U.getSectionValue(extraDataSec, "oDireccion\").html(\"", "\")").replaceAll(", <br />|<br />", ",");
						String add[]=U.getAddress(addressSec);
						U.log(Arrays.toString(add));
//						U.log(addressSec);
						if (uniqueSet.add((companyName+add[0]+add[3]).toLowerCase())) {
							String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),telephone,null,"http://generix.com.mx/",null,null,null,null,null,null,lat,lon,"http://generix.com.mx/",U.getTodayDate()};
							writer.writeNext(out);
							U.log(Arrays.toString(out));
						}
//						break;
					}
//					break;
				}
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Generix_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
}
