package com.tequila.extraction;

import java.io.StringWriter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

@SuppressWarnings("deprecation")
public class ExtractEpsServicioAutomotriz {
	private static String baseUrl="http://www.epsservicioautomotriz.com/sucursales";
	public static void main(String[] args) {
		extractData();
	}
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			System.setProperty("webdriver.chrome.driver", "/home/mypremserver/chromedriver");
			WebDriver driver = new ChromeDriver();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			String baseHtml=U.getHTML(baseUrl);
			writer.writeNext(HEADER);
			String dataVal[]=U.getValues(baseHtml, "data-src=\"", "\"");
			int count=0;
			String openedIn="1995";
			String sicData[]=Sic.sicInfo("7538");
			for (String datas : dataVal) {
				if (!datas.contains("https://obc.app"))continue;
				count++;
				U.log(StringEscapeUtils.unescapeHtml3(datas));
//				if (count<20) {
//					continue;
//				}
				
				String dataHtml=U.getHtml(StringEscapeUtils.unescapeHtml3(datas),driver);
				//U.log(dataHtml);
				
				String phone=U.getSectionValue(dataHtml, "itemprop=\"telephone\">", "</div>").trim();
				String name=U.getSectionValue(dataHtml, "\"companyName\": \"", "\"").trim();
//				String addSec=U.getSectionValue(dataHtml, "addressStr: \"", "\",");
//				U.log(addSec);
				String address=U.getSectionValue(dataHtml, "\"addressLine1\": \"", "\"");
				String zip=U.getSectionValue(dataHtml, "\"zipCode\": \"", "\"");
				String city=StringEscapeUtils.unescapeJava(U.getSectionValue(dataHtml, "\"city\": \"", "\""));
				String state=U.getSectionValue(dataHtml, "\"state\": \"", "\"");
				String lat=U.getSectionValue(dataHtml, "\"lat\": \"", "\"");
				String lon=U.getSectionValue(dataHtml, "\"lon\": \"", "\"");
				String website=U.getSectionValue(dataHtml, "\"website\": \"", "\"");
				String email=U.getSectionValue(dataHtml, "\"submissionEmail\": \"", "\"");
//				U.log(name);
//				U.log(address);
//				U.log(city);
//				U.log(MXStates.getFullNameFromAbbr(state));
//				U.log(zip);
//				U.log(lat);
//				U.log(lon);
//				U.log(phone);
//				U.log(website);
//				U.log(email);
				String out[]= 
					{
						""+count,//id
						U.toCapitalizeCase(sicData[0]),
						U.toCapitalizeCase(sicData[1]),
						U.toCapitalizeCase(sicData[3]),
						U.toCapitalizeCase(sicData[4]),
						U.toCapitalizeCase(sicData[5]),
						U.toCapitalizeCase(sicData[6]),
						U.toCapitalizeCase(StringEscapeUtils.unescapeJava(name)),
						U.toCapitalizeCase(StringEscapeUtils.unescapeJava(address)),
						U.toCapitalizeCase(name.replace("EPS", "").trim().contains("Sucursal")||name.replace("EPS", "").trim().contains("Ciudad")?"":StringEscapeUtils.unescapeJava(name.replace("EPS", "").trim())),
						U.toCapitalizeCase(city),
						U.toCapitalizeCase(MXStates.getFullNameFromAbbr(state)),
						U.toCapitalizeCase(zip),
						U.formatNumbersAsCode(phone),
						U.formatNumbersAsCode(""),
						website.toLowerCase(),
						email.toLowerCase(),
						null,
						null,
						null,
						null,
						openedIn,
						lat,
						lon,
						baseUrl,
						null
					};
				writer.writeNext(out);
			//	break;
			}
			U.log(count);
			FileUtil.writeAllText(U.getCachePath()+"EPS_Servicio_Automotriz.csv", sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
