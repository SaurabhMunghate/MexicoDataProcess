package com.tequila.extraction.education;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.IOUtils;

import com.shatam.utils.DirectoryList;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;

public class ExtractYelpEducation extends DirectoryList{
	static String startIndex="0";
	static String endIndex="50";
	static boolean executionFlag=false;//u.gethml
//	static boolean executionFlag=true;//gethtml
	public static void main(String[] args) {
		ExtractYelpEducation educ=new ExtractYelpEducation();        
		try {
			educ.extractProcess();
			educ.printAll("YelpEducation/YelpEducation_college_Cancun_"+startIndex+"_"+endIndex+".csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	int counter=0;
	@Override
	protected void extractProcess() throws Exception {
		String regionUrl="https://www.yelp.com/search?cflt=education&find_loc=Merida&start=";
		String regionHtml1=U.getHTML(regionUrl+"0");
		String totalPage=U.getSectionValue(regionHtml1, "Page 1 of ", "</span>");
		U.log(totalPage);
		String regionHtml="";
		for (int i = 0; i < Integer.parseInt(totalPage); i++) {
			if (i<Integer.parseInt(startIndex)||i>Integer.parseInt(endIndex))
				continue;
			if (i==0) {
				regionHtml=U.getHTML(regionUrl+i);
			}else {
				U.log(regionUrl+i+"0");
				regionHtml+=U.getHTML(regionUrl+i+"0");
			}
//			if (i==20)
//				break;
//			break;
		}
//		String listingsVals[]=U.getValues(regionHtml, "lemon--div__373c0__1mboc searchResult__373c0__1yggB border-color--default__373c0__2oFDT", "</a>");
		String listingsVals[]=U.getValues(regionHtml, "{\"searchActions\"", "\"childrenBusinessInfo\":");
		U.log(listingsVals.length);
		for (String listingSec : listingsVals) {
			String companyDataUrl="https://www.yelp.com"+U.getSectionValue(listingSec, "\"businessUrl\":\"", "\"");
			addDetails(listingSec,companyDataUrl);
			//break;
		}
	}
	String sicSub="8221";
	private void addDetails(String listingSec, String companyDataUrl) {
		try {
			U.log((counter++)+") ----------------------------------------------------------");
			U.log(listingSec+"\n"+companyDataUrl);
			String dataHtml="";
			if (executionFlag) {
				getHTML(companyDataUrl);
			}else {
				dataHtml=U.getHTML(companyDataUrl);
			}
			if (dataHtml==null) {
				return;
			}
			
			String companyName=U.getSectionValue(listingSec, "\"name\":\"", "\"");
			String phone=U.getSectionValue(listingSec, "\"phone\":\"", "\"");
			String streetAddress=U.getSectionValue(listingSec, "\"formattedAddress\":\"", "\"");
			String colonia=U.getSectionValue(listingSec, "\"neighborhoods\":[\"", "\"")==null?ALLOW_BLANK:U.getSectionValue(listingSec, "\"neighborhoods\":[\"", "\"");
			String postalCode=U.getSectionValue(dataHtml, "\"postalCode\": \"", "\"")==null?ALLOW_BLANK:U.getSectionValue(dataHtml, "\"postalCode\": \"", "\"");
			String cityState=U.getSectionValue(dataHtml, "<span itemprop=\"addressLocality\">", "</span>");
			String streetCol=U.getSectionValue(dataHtml, "<span itemprop=\"streetAddress\">", "</span>");
			String state=MXStates.findStateFromZip(postalCode)!=null?MXStates.findStateFromZip(postalCode):U.findState(cityState);
			
			String city=state.equals("Mexico City")?MXStates.getMexicoCityMunicipalites(postalCode):U.findCityFromZip(postalCode);
			if (streetCol!=null&&streetAddress!=null&&colonia==ALLOW_BLANK)
				colonia=U.removeHtml(streetCol.replace(streetAddress, ""));
			String website=U.getSectionValue(dataHtml, "<span class=\"offscreen\">Business website</span>", "</a>");
			if (website!=null)
				website=U.removeHtml(website);
			else
				website=ALLOW_BLANK;
			String lat=U.getSectionValue(dataHtml, "{&#34;latitude&#34;: ", ",");
			String lng=U.getSectionValue(dataHtml, "&#34;longitude&#34;: ", "}");
			U.log(streetAddress+" --> "+colonia+" --> "+city+" --> "+state+" --> "+companyName+" --> "+phone+" --> "+postalCode+" --> "+lat+", "+lng);
			addCompanyDetailsFromMexico(sicSub, companyName, phone, ALLOW_BLANK, website);
			addAddress(streetAddress, colonia, city, state, postalCode);
//			addAddress(add[0], add[1], add[2], add[3], add[4]);
			addBoundaries(lat, lng);
			addReferenceUrl(companyDataUrl);
			addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
			addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	public static String getHTML(String path) throws IOException {

		path = path.replaceAll(" ", "%20");
		// U.log(" .............."+path);
		// Thread.sleep(4000);
		String fileName = U.getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists())
			return null;
			//return null;

		URL url = new URL(path);

		String html = null;

		// chk responce code

		 //U.log("respCode=" + respCode);
//		 if (respCode == 200) {

		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress("107.151.136.218",80 ));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"182.74.243.39",3128));
		final URLConnection urlConnection = url.openConnection();  //proxy

		// Mimic browser
		try {
			urlConnection
					.addRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:10.0.2) Gecko/20100101 Firefox/10.0.2");
			urlConnection.addRequestProperty("Accept", "text/css,*/*;q=0.1");
			urlConnection.addRequestProperty("Accept-Language",	"en-us,en;q=0.5");
			urlConnection.addRequestProperty("Cache-Control", "max-age=0");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			urlConnection.setConnectTimeout(5000);
			// U.log("getlink");
			final InputStream inputStream = urlConnection.getInputStream();

			html = IOUtils.toString(inputStream);
			// final String html = toString(inputStream);
			inputStream.close();

			if (!cacheFile.exists())
				FileUtil.writeAllText(fileName, html);

			return html;
		} catch (Exception e) {
			U.log(e);
		}
		return html;
	}
}
