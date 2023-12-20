package com.tequila.extraction;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractBanamex {
	private static String sicCode="6029";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	public static void main(String[] args) {
		HashSet<String>ServiceCode=new HashSet<>();
		String postUrl="https://kio.banamex.com/framework/geo";
		String postData[]= {"{\"site\":\"5\",\"entity\":\"bnmx_sucursales_summary\",\"options\":\"tipo.numero=100\",\"quadrant\":\"33.34354203011677 -115.14459371566772 15.138870032533317 -86.95367574691772\",\"logicalOperator\":\"OR\",\"single\":\"true\"}",
				"{\"site\":\"5\",\"entity\":\"bnmx_sucursales_summary\",\"options\":\"tipo.numero=100\",\"quadrant\":\"36.31882403712533 -123.40631246566772 18.60765054323039 -95.21539449691772\",\"logicalOperator\":\"OR\",\"single\":\"true\"}",
				"{\"site\":\"5\",\"entity\":\"bnmx_sucursales_summary\",\"options\":\"tipo.numero=100\",\"quadrant\":\"28.59248092337254 -111.25241097085234 9.727466416817453 -83.06149300210234\",\"logicalOperator\":\"OR\",\"single\":\"true\"}",
				"{\"site\":\"5\",\"entity\":\"bnmx_sucursales_summary\",\"options\":\"tipo.numero=100\",\"quadrant\":\"30.21884773098787 -109.64840706460234 21.183900141147582 -95.55294808022734\",\"logicalOperator\":\"OR\",\"single\":\"true\"}"};
		String services[]= {"100", "300"};//"950","500", "600", "800", "400" 
		String postHtml;
		int atmcount=0;
		HashSet<String> brancCodeHashSet=new HashSet<>();
		try {
			
			postHtml = U.sendPostRequest(postUrl, postData[0]);
			String postDataSecs[]=U.getValues(postHtml, "{\"servicios\"", "\"}");
			for (int i = 0; i < services.length; i++) {
				for (String post : postData) {
					post=post.replaceAll("tipo.numero=\\d{3}\"", "tipo.numero="+services[i]+"\"");
					postHtml += U.sendPostRequest(postUrl, post);
				}
			}
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			int i=0;
			
			postDataSecs=U.getValues(postHtml, "{\"servicios\"", "\"}");
			for (String poStr : postDataSecs) {
				poStr+="\"";
				String branchNo=U.getSectionValue(poStr, "\"numero\":\"", "\"");
//				if (!branchNo.contains("ATM"))continue;
				if (brancCodeHashSet.add(branchNo)) {
					if (branchNo.startsWith("ATM-")) {
						sicCode="6099";
						atmcount++;
					}else {
						sicCode="6029";
//						continue;
					}
					String sicdetails[]=Sic.sicInfo(sicCode);
					String dataUrl="https://kio.banamex.com/framework/query";
					String dataPostData="{\"site\": \"5\",\"entity\": \"portal_sucursales\",\"operation\":\"getEntitiesByOptions\",\"clave\":\"100\",\"sucursal\":\""+branchNo+"\"}";
					String dataPostData2="{\"site\":\"5\",\"entity\":\"bnmx_sucursales_summary\",\"operation\":\"getEntitiesByOptions\",\"options\":\"numero EQUALS "+branchNo+"\",\"page\":\"0\", \"page_size\":\"10\"}";
					U.log(dataPostData);
					String detailDataHtml=U.sendPostRequest(dataUrl, dataPostData);
					if (detailDataHtml.contains("\"result\":[],")) {
						detailDataHtml=U.sendPostRequest(dataUrl, dataPostData2);
						U.log("==> "+detailDataHtml);
						String add[]= {"","","","",""};
						String name="CitiBanamex "+U.getSectionValue(detailDataHtml, "],\"nombre\":\"", "\"");
						String addSec=U.getSectionValue(detailDataHtml, "\"direccion\":\"", "\"")+", "+U.getSectionValue(detailDataHtml, "\"dirComplemento\":\"", "\"");
//						add[0]=U.getSectionValue(detailDataHtml, "\"direccion\":\"", "\"");
//						add[1]=U.getSectionValue(detailDataHtml, "\"dirComplemento\":\"", "\"");
//						add[2]=U.getSectionValue(detailDataHtml, "\"ciudad\":\"", "\"");
//						add[3]=U.getSectionValue(detailDataHtml, "\"edo_nombre\":\"", "\"");
//						add[4]=U.getSectionValue(detailDataHtml, "\"cp\":\"", "\"");
						add=U.getAddress(addSec.replace(", C.P.", " "));
						U.log(Arrays.toString(add));
						String lat=U.getSectionValue(detailDataHtml, "\"latitud\":", ",");
						String lon=U.getSectionValue(detailDataHtml, "\"longitud\":", ",");
						String phone=U.getSectionValue(detailDataHtml, "\"telefono\":\"", "\"");
						ArrayList<String> ser=Util.matchAll(poStr, "\\{\"numero\":\\d{3}\\}", 0);
						ServiceCode.addAll(ser);
						U.log(poStr);
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,"https://www.banamex.com/",null,null,null,null,null,null,lat,lon,"https://www.banamex.com/es/localizador-sucursales.html",U.getTodayDate()};
						writer.writeNext(out);
						U.log(Arrays.toString(out));
					}else {
						U.log("==> "+detailDataHtml);
						String add[]= {"","","","",""};
						String name="CitiBanamex "+U.getSectionValue(detailDataHtml, "\"nombre\":\"", "\"");
						add[0]=U.getSectionValue(detailDataHtml, "\"sobrcall\":\"", "\"")+" "+U.getSectionValue(detailDataHtml, "\"calle_num\":\"", "\"");
						add[1]=U.getSectionValue(detailDataHtml, "\"colonia\":\"", "\"");
						add[2]=U.getSectionValue(detailDataHtml, "\"ciudad\":\"", "\"");
						add[3]=U.getSectionValue(detailDataHtml, "\"edo_nombre\":\"", "\"");
						add[4]=U.getSectionValue(detailDataHtml, "\"cp\":\"", "\"");
						U.log(Arrays.toString(add));
						String lat=U.getSectionValue(detailDataHtml, "\"latitud\":\"", "\"");
						String lon=U.getSectionValue(detailDataHtml, "\"longitud\":\"", "\"");
						String phone=U.getSectionValue(detailDataHtml, "\"telefono\":\"", "\"");
						ArrayList<String> ser=Util.matchAll(poStr, "\\{\"numero\":\\d{3}\\}", 0);
						ServiceCode.addAll(ser);
						U.log(poStr);
						String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase(sicdetails[6]),U.toTitleCase(name),U.toTitleCase(add[0]),U.toTitleCase(add[1]),U.toTitleCase(add[2]),U.toTitleCase(add[3]),add[4].trim(),U.formatNumbersAsCode(phone),null,"https://www.banamex.com/",null,null,null,null,null,null,lat,lon,"https://www.banamex.com/es/localizador-sucursales.html",U.getTodayDate()};
						writer.writeNext(out);
						U.log(Arrays.toString(out));
					}
//					break;
				}
			}
			FileUtil.writeAllText(U.getCachePath()+"CitiBanamex_Branches.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		U.log(ServiceCode);
		U.log(atmcount);
		U.log(brancCodeHashSet.size());
	}
}
//getKio('{"site":"5","entity":"bnmx_sucursales_summary","operation":"getEntitiesByOptions","options":"numero EQUALS '+param+'","page":"0", "page_size":"10"}'
//,"about","query",_result);
//{"site":"5","entity":"bnmx_sucursales_summary","operation":"getEntitiesByOptions","options":"numero EQUALS 333","page":"0", "page_size":"10"}
//{"site": "5","entity": "portal_sucursales","operation":"getEntitiesByOptions","clave":"100","sucursal":"333"}
//{"site":"5","entity":"bnmx_sucursales_summary","options":"tipo.numero=100","quadrant":"19.43717486043454 -99.15236234664917 19.40017902595196 -99.09730195999146","logicalOperator":"OR","single":"true"}