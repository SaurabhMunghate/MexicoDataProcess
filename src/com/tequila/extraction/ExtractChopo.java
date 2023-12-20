package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractChopo {
	public static void main(String[] args) {
		extractData();
	}

	private static void extractData() {
		try {
			String dataUrl="https://www.chopo.com.mx/backend/index.php/api/post_sucursales";
			String allDataHtml=U.getHTML(dataUrl);
			U.log(allDataHtml);
			String listings[]=U.getValues(allDataHtml, "{\"id_sucursal", "}");
			String HEADER[]= {"SIC_SUB","COMPANY_NAME","ADDRESS","NEIGBOURHOOD","CITY","STATE","ZIP","PHONE","WEBSITE","LATITUDE","LONGITUDE","SOURCE_URL"};
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			for (String listing : listings) {
				listing=StringEscapeUtils.unescapeJava(listing);
				U.log(listing);
				
				String name=U.getSectionValue(listing, "\"nombre\":\"", "\"");
				String lat=U.getSectionValue(listing, "\"latitud\":\"", "\"");
				String lng=U.getSectionValue(listing, "\"longitud\":\"", "\"");
				String phone=U.getSectionValue(listing, "\"telefono\":\"", "\"");
				String addSec=U.getSectionValue(listing, "\"direccion\":\"", "\",").trim();
//				String add[]=U.getAddress(addSec);
				//U.log(Arrays.toString(add));
				String out[]= {"","Laboratorio Médico del Chopo- Sucursal "+name,addSec,"","","","",phone,"https://www.chopo.com.mx/",lat,lng,"https://www.chopo.com.mx/sucursales/"};
				writer.writeNext(out);
				//break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Chopo_Raw_file.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
