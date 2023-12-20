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

public class ExtractBanBajio {
	private static String sicCode="6029";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		try {
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			
			String mainHtml=U.getHTML("https://www.bb.com.mx/webcenter/portal/BanBajio/ubicanos/sucursales1?_afrLoop=35039751683999973&_afrWindowMode=0&Adf-Window-Id=btsykx0lu&_afrPage=3&_afrFS=16&_afrMT=screen&_afrMFW=1285&_afrMFH=953&_afrMFDW=1920&_afrMFDH=1080&_afrMFC=8&_afrMFCI=0&_afrMFM=0&_afrMFR=96&_afrMFG=0&_afrMFS=0&_afrMFO=0");
			String[] commVals=U.getValues(mainHtml, "{\"POIS_ID\":\"", "}");
			int i=0;
//			U.log(commVals.length);
			for (String records : commVals) {
				if (!(records.contains("\"POIS_NAME\":\"Sucursal")||(records.contains("\"POIS_NAME\":\"Cajero\"")&&records.contains("\"POIS_SHORTNAME\":\"E")))) continue;
				boolean flag=true;
				records=StringEscapeUtils.unescapeHtml3(records);
				if (records.contains("POIS_NAME\":\"Cajero")) {
					sicCode="6099";
				}else if (records.contains("\"POIS_NAME\":\"Sucursal")&&records.contains("POIS_NAME\":\"Cajero")) {
					sicCode="6029";
					flag=false;
				}else {
					sicCode="6029";
				}
				String sicdetails[]=Sic.sicInfo(sicCode);
				U.log(records);
				String add=U.getSectionValue(records, "POIS_ADDRESS\":\"", "\"");
				String neigh=U.getSectionValue(records, "\"POIS_COUNTRY_SUBDIVISION_1\":\"", "\"");
				String city=U.getSectionValue(records, "\"POIS_COUNTRY_SUBDIVISION_2\":\"", "\"");
				String state=U.getSectionValue(records, "\"POIS_COUNTRY_SUBDIVISION_3\":\"", "\"");
				String zip=U.getSectionValue(records, "\"POIS_POSTAL_CODE\":\"", "\"");
				String lat=U.getSectionValue(records, "\"POIS_LATITUDE\":\"", "\"");
				String lng=U.getSectionValue(records, "\"POIS_LONGITUDE\":\"", "\"");
				String phone=U.getSectionValue(records, "\"POIS_PHONE\": \"", "\"");
				String name=U.getSectionValue(records, "\"POIS_NAMES\":\"", "\"");
				U.log(zip);
				
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(add),U.toTitleCase(neigh),U.toTitleCase(city),U.toTitleCase(state),zip.trim(),U.formatNumbersAsCode(phone),null,"https://www.bb.com.mx/webcenter/portal/BanBajio/home",null,null,null,null,null,null,lat,lng,"https://www.bb.com.mx/webcenter/portal/BanBajio/ubicanos/sucursales1?_afrLoop=35041845607680187&_afrWindowMode=0&Adf-Window-Id=eghbp4sa5&_afrPage=3&_afrFS=16&_afrMT=screen&_afrMFW=1853&_afrMFH=953&_afrMFDW=1920&_afrMFDH=1080&_afrMFC=8&_afrMFCI=0&_afrMFM=0&_afrMFR=96&_afrMFG=0&_afrMFS=0&_afrMFO=0",U.getTodayDate()};
				writer.writeNext(out);
				if (flag==false) {
					sicdetails=Sic.sicInfo("6099");
					out= new String[]{""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(add),U.toTitleCase(neigh),U.toTitleCase(city),U.toTitleCase(state),zip.trim(),U.formatNumbersAsCode(phone),null,"https://www.bb.com.mx/webcenter/portal/BanBajio/home",null,null,null,null,null,null,lat,lng,"https://www.bb.com.mx/webcenter/portal/BanBajio/ubicanos/sucursales1?_afrLoop=35041845607680187&_afrWindowMode=0&Adf-Window-Id=eghbp4sa5&_afrPage=3&_afrFS=16&_afrMT=screen&_afrMFW=1853&_afrMFH=953&_afrMFDW=1920&_afrMFDH=1080&_afrMFC=8&_afrMFCI=0&_afrMFM=0&_afrMFR=96&_afrMFG=0&_afrMFS=0&_afrMFO=0",U.getTodayDate()};
					writer.writeNext(out);
				}
				U.log(Arrays.toString(out));
//				i++;
//				break;
			}
			U.log(i);
			FileUtil.writeAllText(U.getCachePath()+"BanBajio_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
