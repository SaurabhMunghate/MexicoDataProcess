package com.tequila.extraction;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ExtractBancomerData {
	private static String sicCode="6021";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		String mainUrl="";
		String srcUrl="";
		try {
			String sicdetails[]=Sic.sicInfo(sicCode);
			HashSet<String> dupliData=new HashSet<>();
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int total=0;
			HashSet<String>data=new HashSet<>();
			String postURl="https://www.strategis.mx/Glocator/common/services/Buscador.ashx?";
			HashSet<String>intit=new HashSet<>();
			String postalCodeFileName="/home/chinmay/eclipse-workspace/DBReporting/Mexico_Address_Direcory_MiCodigo.csv";
			List<String[]> postalData=U.readCsvFileWithoutHeader(postalCodeFileName);
			for (String[] stateData : postalData) {
				if (stateData[0].equals("State")) continue;
				String inti=stateData[3].substring(0, 4);
				if (!intit.add(inti)) continue;
				U.log(Arrays.toString(stateData));
				String postData="metodo=getPuntos&latitud="+stateData[7]+"&longitud="+stateData[8]+"&idOpcionCatalogo=4&idOpcionAtributo=6&dia=1&hora=11%3A43&ubicacion=0&direccion=1&fecha=2019-04-29+11%3A43%3A152";
//				U.log(postData);
				String postHtml=U.sendPostRequest(postURl, postData);
				if (postHtml==null||postHtml.contains("\"Obj\":[]}"))continue;
//				U.log(postHtml);
				int i=0;
				String bankDatas[]=U.getValues(postHtml, "{\"IdPunto\"", "}");
				for (String bank : bankDatas) {
					String code=U.getSectionValue(bank, "\"Cr\":\"", "\"");
					if (data.add(code)) {
						String name="BBVA Bancomer "+U.getSectionValue(bank, "\"Nombre\":\"", "\"");
						String streetAdd=U.getSectionValue(bank, "\"Calle\":\"", "\"")+" "+U.getSectionValue(bank, "\"NumeroExterior\":\"", "\"");
						String neighb=U.getSectionValue(bank, "\"Colonia\":\"", "\"");
						String city=U.getSectionValue(bank, "\"Delegacion\":\"", "\"");
						String state=U.getSectionValue(bank, "\"Estado\":\"", "\"");
						String postalCode=U.getSectionValue(bank, "\"CodigoPostal\":\"", "\"");
						String lat=U.getSectionValue(bank, "\"Latitud\":\"", "\"");
						String lon=U.getSectionValue(bank, "\"Longitud\":\"", "\"");
						String phone=U.getSectionValue(bank, "\"Telefono\":\"", "\"");
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(streetAdd),U.toTitleCase(neighb),U.toTitleCase(city),U.toTitleCase(state),postalCode.trim(),U.formatNumbersAsCode(phone),null,"https://www.bancomer.com/",null,null,null,null,null,"1932",lat,lon,"https://portal.bancomer.com/aplicativos/buscador-sucursales/index.jsp",U.getTodayDate()};
						writer.writeNext(out);
						U.log(Arrays.toString(out));
						U.log(bank);
					}
//					U.log(bank);
//					break;
				}
//				U.log(bankDatas.length);
//				break;
			}
			FileUtil.writeAllText(U.getCachePath()+"BBVA_Bancomer_Branches.csv", sw.toString());
			sw.close();
			writer.close();
			U.log(data.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
