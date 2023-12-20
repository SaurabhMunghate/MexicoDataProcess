package com.tequila.extraction;


import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractCollegesList {
	private static String sicCode="8211";
	private String baseUrl="https://www.4icu.org/mx/";
	private static final String HEADER[] ={
			"Srnoid","Industry_sector=industry_sector","Spanish_industry_sector=sector De La Industria","Major_sic_category=principales_categoría_sic",
			"Sub_sic_category=categoría Del Sic","Product_desc=product_description","Sp_product_description=productos_y_descripción_de_los_servicios",
			"Company_Name=Nombre De La Empresa","Address=address_dirección","Neighbourhood","City=city_ciudad","State=state_estado","Zipcode=códigopostal","PHONE=Telephone_Teléfono","FAX=Fax",
			"URL=Web Address","EMAIL = Email_Correo Electrónico","Contact Person=contacto_Ejecutivo","Title=Title_título","Annual_sales_volume=volumen_anual_de_ventas",
			"Number_of_employees=número_de_empleados","Years_in_biz","LATUTUDE","LONGITUDE","UniqueUrl","Fetching Time"
	};
	public static void main(String[] args) {
		new ExtractCollegesList().extractData();
	}
	private void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			StringWriter sw1=new StringWriter();
			CSVWriter writer1=new CSVWriter(sw1);
			writer.writeNext(HEADER);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			String baseHtml=U.getHTML(baseUrl);
			String colListSec=U.getSectionValue(baseHtml, "<tbody>", "</tbody>");
			String collegeLists[]=U.getValues(colListSec, "<tr>", "</tr>");
			U.log(collegeLists.length);
			int i=0;
			for (String college : collegeLists) {
				U.log(college);
				String detailsUrl=U.getSectionValue(college, "href=\"", "\"");
				U.log("https://www.4icu.org"+detailsUrl);
				String detailsHtml=U.getHTML("https://www.4icu.org"+detailsUrl);
				String collegeName=StringUtils.stripAccents(StringEscapeUtils.unescapeHtml3(U.getSectionValue(college, "htm\">", "</a>")));
				U.log(collegeName);
				String url=U.getSectionValue(detailsHtml, "itemprop=\"url\" href=\"", "\"");
				String telephone=U.getSectionValue(detailsHtml, "itemprop=\"telephone\">", "</span>");
				String fax=U.getSectionValue(detailsHtml, "itemprop=\"faxNumber\">", "</span>");
				String addSec=StringUtils.stripAccents(StringEscapeUtils.unescapeHtml3(U.getSectionValue(detailsHtml, "Admission Office</th>", "<br> <span itemprop").replaceAll("<td>", "").replace("<br>", ",")));
				U.log(addSec);
				
				String add[]= {"","","","",""};
				add[0]=U.removeHtml(addSec);
				String openedIn=U.getSectionValue(detailsHtml, "\"foundingDate\">", "</span");
				String emp_Count=U.removeHtml(U.getSectionValue(detailsHtml, "Academic Staff</th>", "</tr>"));
				String sicdetails[]=Sic.sicInfo(sicCode);
				String out[]= {""+(i),sicdetails[0],sicdetails[1],sicdetails[3],sicdetails[4],sicdetails[5],sicdetails[6],collegeName,add[0],add[1],add[2],add[3],add[4],telephone,fax,url,"","","","",emp_Count,openedIn,"","","https://www.4icu.org"+detailsUrl,dateFormat.format(date).toString()};
				//U.log(emp_Count);
				String out1[]= {""+(i++),collegeName,url,openedIn};
				writer1.writeNext(out1);
				writer.writeNext(out);
				//break;
			}
			U.log(U.getCachePath()+"4icu.csv");
			FileUtil.writeAllText(U.getCachePath()+"4icu.csv", sw.toString());
			FileUtil.writeAllText(U.getCachePath()+"4icuList.csv", sw1.toString());
			writer1.close();
			sw1.close();
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
}
