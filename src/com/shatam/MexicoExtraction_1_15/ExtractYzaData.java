package com.shatam.MexicoExtraction_1_15;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;

import com.chinmay.test.GoogleAddressJson;
import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractYzaData {
	private static String sicCode="5912";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) throws Exception {
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		writer.writeNext(HEADER);
		int i=0;
		String sicdetails[]=Sic.sicInfo(sicCode);
		String rawFilePath="/home/chinmay/MexicoCache/Cache/yzaDataData.txt";
		String fileData=FileUtil.readAllText(rawFilePath);
		HashSet<String> uniqueSet=new HashSet<>();
		String lisintgs[]=U.getValues(fileData, "{\\\"IdSucursal\\\"", "}");
		for (String listing : lisintgs) {
			U.log(listing);
			String streetAddress=(U.getSectionValue(listing, "\\\"DirNombre\\\":\\\"", "\\\"")+U.getSectionValue(listing, "\"DirNoExterior\\\":\\\"", "\\\"")+", "+U.getSectionValue(listing, "\\\"DirEntreCalles\\\":\\\"", "\\\"")).trim().replaceAll("\\s{2,}", " ");
			if (streetAddress.endsWith(",")) 
				streetAddress=streetAddress.substring(0, streetAddress.length()-1);
			U.log(streetAddress);
			String companyName=U.getSectionValue(listing, "\\\"NombreCadena\\\":\\\"", "\\\"")+" "+U.getSectionValue(listing, "\"Nombre\\\":\\\"", "\\\"");
			String neighb=U.getSectionValue(listing, "\\\"DirColonia\\\":\\\"", "\\\"");
			String lat=U.getSectionValue(listing, "\"Latitud\\\":", ",");
			String lon=U.getSectionValue(listing, "\"Longitud\\\":", ",");
			String state=U.matchState(U.getSectionValue(listing, "\\\"NombreEstado\\\":\\\"", "\\\""));
			String city=U.getSectionValue(listing, "\"CiudadStr\\\":\\\"", "\\\"");
//			String zip=new GoogleAddressJson().getGoogleAddressFromLatLon(new String[] {lat,lon})[4];
//			if (state.equals("Mexico City")&&zip.length()>4) {
//				city=MXStates.getMexicoCityMunicipalites(zip);
//			}
			if (uniqueSet.add((companyName+streetAddress+state).toLowerCase())) {
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(companyName),U.toTitleCase(streetAddress),U.toTitleCase(neighb),U.toTitleCase(city),U.toTitleCase(state),null,null,null,"https://www.yza.mx/",null,null,null,null,null,null,lat,lon,"https://www.yza.mx/Sucursales/Busqueda",U.getTodayDate()};
				writer.writeNext(out);
				U.log(Arrays.toString(out));
			}
//			break;
		}
		FileUtil.writeAllText(U.getCachePath()+"YZA_Branches_MexicoCity.csv", sw.toString());
		sw.close();
		writer.close();
	}
	
}
