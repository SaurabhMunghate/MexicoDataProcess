/**
 * 
 */
package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import org.apache.commons.text.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

/**
 * @author Chinmay
 * 29-Apr-2019
 */
public class ExtractActinver {
	private static String sicCode="6029";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		try {
			String sicdetails[]=Sic.sicInfo(sicCode);
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int i=0;
			String dataURl="https://www.actinver.com/webcenter/portal/Actinver/Centros%20Financieros?_afrLoop=25676407893219037#!%40%40%3F_afrLoop%3D25676407893219037%26pageTemplate%3D%252Foracle%252Fwebcenter%252Fsiteresources%252FscopedMD%252Fs8bba98ff_4cbb_40b8_beee_296c916a23ed%252FsiteTemplate%252Fgsrbc443488_2e2f_47a6_bf97_1c5e11b73b59%252FTemplate.jspx%26_adf.ctrl-state%3Ddtf4nxiw9_17";
			String dataHtml=U.getPageSource(dataURl);
			String banksLists[]=U.getValues(dataHtml, "console.debug(\"iniciando\");", "console.debug(Estado);");
			U.log(banksLists.length);
			for (String banksData : banksLists) {
				banksData=StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml4(banksData));
				U.log(banksData);
				String name="Actinver "+U.getSectionValue(banksData, "Nombre_Google = '", "'");
				String state=U.findState(U.getSectionValue(banksData, "Estado = '", "'"));
				String streetadd=U.getSectionValue(banksData, "Direccion = '", "'");
				String neighb=U.getSectionValue(banksData, "Colonia = '", "'");
				String city=U.getSectionValue(banksData, "Delegacion = '", "'");
				String postalCode=U.getSectionValue(banksData, "CP = '", "'");
				String telePhone=U.getSectionValue(banksData, "Telefono = '", "'");
				String lat=U.getSectionValue(banksData, "Latitud= '", "'");
				String lon=U.getSectionValue(banksData, "Longitud = '", "'");
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(streetadd),U.toTitleCase(neighb),U.toTitleCase(city),U.toTitleCase(state),postalCode.trim(),U.formatNumbersAsCode(telePhone),null,"https://www.actinver.com",null,null,null,null,null,null,lat,lon,"https://www.actinver.com/webcenter/portal/Actinver/Centros%20Financieros",U.getTodayDate()};
				writer.writeNext(out);
				U.log(Arrays.toString(out));
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Actinver_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
