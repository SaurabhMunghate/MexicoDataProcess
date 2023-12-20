package com.mexico.tiendeo;


import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.opencsv.CSVWriter;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.ApiKey;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.Path;
import com.shatam.utils.U;

public class ExtractTiendeoData extends DirectoryList{
	private static String sicCode=null;
	private static String cities[]=null;
	private static String navLink=null;
	static int err=0;
	private static String fileName="Tiendeo-ElectrónicaStores.csv";
	@SuppressWarnings("unchecked")
	HashMap<String,String> searchData=new HashMap() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		{
			put("Supermercados", "hiper supermercados");
			put("Moda", "ropa zapatos y accesorios");
			put("Tiendas Departamentales", "tiendas departamentales");
			put("Salud", "farmacias y salud");
			put("Electrónica", "electronica y tecnologia");
			put("Muebles", "hogar muebles");
			put("Ferreterías", "ferreterias construccion");
			put("Restaurantes", "restaurantes");
			put("Bancos", "bancos y servicios");
			put("Belleza", "perfumerias y belleza");
			put("Motor", "autos motos y repuestos");
			put("Deporte", "deporte");
			put("Ocio", "ocio");
			put("Librerías", "librerias y papelerias");
			put("Viajes", "viajes");
			put("Juguetes", "juguetes ninos y bebes");
			put("Ópticas", "opticas");
			put("Lujo", "marcas de lujo");
			put("Bodas", "bodas");
		}
	};
	public static void addTiendeoData(String[] cities, String sicCode, String navLink) throws Exception {
//		String fileName="Tiendeo-DepartmentStores.csv";
		
		fileName="Tiendeo-"+U.getSectionValue(navLink, "\">", "<").trim()+".csv";
		U.log(fileName);
		ExtractTiendeoData.cities=cities;
		ExtractTiendeoData.navLink=navLink;
		ExtractTiendeoData a=new ExtractTiendeoData();
		ExtractTiendeoData.sicCode=sicCode;
//		U.log("...");
		a.extractProcess();
		a.printAll(fileName);
		//a.addExtraData();
		U.log("Error Pages: "+err);
		U.log("dup: "+dup);
		U.log("Count: "+count);
	}
	static int count=0;
	HashSet<String>uniqueDataSet=new HashSet<>();
	@Override
	protected void extractProcess() throws Exception {
		String nav=U.getSectionValue(navLink, "href=\"", "\"");
		for (String city : cities) { 
			if (city.contains("blue-link bold"))continue;
//			U.log(city);
			String cityUrl="https://www.tiendeo.mx/Tiendas"+U.getSectionValue(city, "href=\"", "\"")+nav;
			String ci=U.getSectionValue(city, "class=\"blue-link\">", "</a>").trim();
//			U.log(city);
			getDetails(cityUrl,ci);
//			break;
		}
	}

	private void getDetails(String cityUrl,String city) throws Exception {
		String cityHtml=U.getHTML(cityUrl);
		U.log(cityUrl);
		String maxResults=U.getSectionValue(cityHtml, "\"NumResults\":", "}");
		U.log(maxResults);
		if (maxResults==null) return;
		String postDataUrl="https://www.tiendeo.mx/_ajax/stores/get";
		int index=0;
		int pageNo=1;
		String payLoad="{\"search\":\""+searchData.get(U.getSectionValue(navLink, "\">", "<").trim())+"\",\"city\":\""+city+"\",\"page\":"+pageNo+",\"index\":"+index+",\"view\":\"result\",\"filter\":\"*\",\"pageType\":\"STORES\",\"viewType\":\"list\"}";
		U.log(payLoad);
		String postCityData=U.sendPostRequest(postDataUrl, payLoad);
//		U.log(postCityData);
//		U.log("data-position=\""+(Integer.parseInt(maxResults)-1)+"\"");
//		while(!(postCityData.contains("data-position=\""+(Integer.parseInt(maxResults)-1)+"\"")||cityHtml.contains("data-position=\""+(Integer.parseInt(maxResults)-1)+"\""))) {
//			index=index+10;
//			pageNo=pageNo+1;
//			payLoad="{\"search\":\""+searchData.get(U.getSectionValue(navLink, "\">", "<").trim())+"\",\"city\":\""+city+"\",\"page\":"+pageNo+",\"index\":"+index+",\"view\":\"result\",\"filter\":\"*\",\"pageType\":\"STORES\",\"viewType\":\"list\"}";
//			U.log("|"+payLoad);
//			postCityData+=U.sendPostRequest(postDataUrl, payLoad);
//		}
//		U.log(postCityData);
		String listingsdata[]=U.getValues(cityHtml+postCityData, "<article class=\"", "</article>");
		U.log(listingsdata.length);
		for (String lisitng : listingsdata) {
//			U.log(lisitng);
			String storeUrl="https://www.tiendeo.mx"+U.getSectionValue(lisitng, "data-link=\"", "\"");
			addDetails(storeUrl,lisitng);
//			break;
		}
	}

	private void addDetails(String storeUrl, String lisitng) throws Exception {

		if (storeUrl.contains("https://www.tiendeo.mx/Tiendas/navojoa/honda-blvd-cuauhtemoc-y-av-josefa-o-de-domi%C2%8Dnguez-local/166531")) return;
		String companyName=U.getSectionValue(lisitng, "<h4 class=\"c·store__shop-name typo-subhead-14-med\">", "</h4>");
		String storeHtml=U.getHTML(storeUrl);
		
		if(storeHtml.contains("Top Ofertas en")) {err++;return;}
		storeHtml=TranslateEnglish.removeUnicode(storeHtml);
		lisitng=TranslateEnglish.removeUnicode(lisitng);
		String streetAdd=U.getSectionValue(storeHtml, "data-address=\"", "\"");
		String city=U.getSectionValue(storeHtml, "\"City\":\"", "\"");
		String zip=U.getSectionValue(storeHtml, "\"PostalCode\":\"", "\"");
		String state=ALLOW_BLANK;
		if (zip==null) {
			zip=ALLOW_BLANK;
		}
		if (zip.length()==5) {
			state=MXStates.findStateFromZip(zip);
		}else if (zip.length()==4) {
			state=MXStates.findStateFromZip("0"+zip);
		}
		String lat=U.getSectionValue(storeHtml, "\"storeLat\":", ",");
		String lon=U.getSectionValue(storeHtml, "\"storeLon\":", "}");
		String phone=U.getSectionValue(storeHtml, "data-phone=\"", "\"");
		String webSiteSec=U.getSectionValue(storeHtml, "<i class=\"icf-shopping-cart s·store-details__shopping-cart\"></i>", "</a>");
		String website=ALLOW_BLANK;
		if(webSiteSec!=null) {
			website=U.getSectionValue(webSiteSec, "href=\"", "\"");
		}
		U.log(streetAdd+", "+city+", "+state+", "+zip);
		U.log(lat+" "+lon);
//		if (lat==null) {
//			
//		}
//		U.log(Arrays.toString(U.getAddressFromLatlonHereApi(new String[]{lat,lon})));
		count++;
		if (!uniqueDataSet.add(companyName+streetAdd)) {
			dup++;
			return;
		}
		U.log(lat+" "+lon);
		U.log(phone);
		U.log(website);
		addCompanyDetailsFromMexico(sicCode, companyName, phone, ALLOW_BLANK, website);
//		addCompanyDetailsFromMexico(sicSub, companyName, phone, ALLOW_BLANK, website);
		addAddress(streetAdd, ALLOW_BLANK, city, state, zip);
		addBoundaries(lat, lon.replace(",\"NumResults\":0", ""));
		addReferenceUrl(storeUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
	}
	static int dup=0;
	private void addExtraData() {
		String filePath=Path.CACHE_PATH_FOR_EXTRACTION + fileName;
		List<String[]> data=U.readCsvFile(filePath);
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			for (String[] inputArr : data) {
				String outputArrp[]=new String[inputArr.length+6];
				if (inputArr[0].contains("ID")) {
					System.arraycopy(inputArr, 0, outputArrp, 0, inputArr.length);
					outputArrp[inputArr.length]="HERE_ADDRESS_FORMATED";
					outputArrp[inputArr.length+1]="HERE_ADDRESS";
					outputArrp[inputArr.length+2]="HERE_COLONIA";
					outputArrp[inputArr.length+3]="HERE_CITY";
					outputArrp[inputArr.length+4]="HERE_STATE";
					outputArrp[inputArr.length+5]="HERE_POSTAL";
					writer.writeNext(outputArrp);
					continue;
				}
				
				System.arraycopy(inputArr, 0, outputArrp, 0, inputArr.length);
				
				String add1[]=getAddressFromLatlonHereApi(new String[] {inputArr[inputArr.length-4],inputArr[inputArr.length-3]});
				
				String add=getAddressFromLAtlonHereApi(new String[] {inputArr[inputArr.length-4],inputArr[inputArr.length-3]});
				outputArrp[inputArr.length]=add;
				System.arraycopy(add1, 0, outputArrp, inputArr.length+1, add1.length);
				writer.writeNext(outputArrp);
				U.log(Arrays.toString(outputArrp));
			}
			FileUtil.writeAllText(filePath, sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getAddressFromLAtlonHereApi(String latlon[]) {
		try {
			String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox="+latlon[0]+"%2C"+latlon[1]+"%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
			String html=U.getHTML(url);
			if (html.contains("\"Address\":"))
				return U.getSectionValue(html, "\"Label\":\"", "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	public String[] getAddressFromLatlonHereApi(String latlon[]) {
		try {
			String url="https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox="+latlon[0]+"%2C"+latlon[1]+"%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1];
			String html=U.getHTML(url);
			
			if (html.contains("\"Address\":")) {
				String add=U.getSectionValue(html, "\"Street\":\"", "\"")+" "+U.getSectionValue(html, "\"HouseNumber\":\"", "\"");
				String colonia=U.getSectionValue(html, "\"District\":\"", "\"");
				String city=U.getSectionValue(html, "\"City\":\"", "\"");
				String state=MXStates.getFullNameFromAbbr(U.getSectionValue(html, "\"State\":\"", "\""));
				String postal=U.getSectionValue(html, "\"PostalCode\":\"", "\"");
				return new String[] {add,colonia,city,state,postal};
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String[] {"","","","",""};
	}
}