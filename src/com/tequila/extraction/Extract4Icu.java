package com.tequila.extraction;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;

import org.eclipse.jetty.util.UrlEncoded;

import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.ApiKey;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;

public class Extract4Icu extends DirectoryList{
	public static void main(String[] args) {
		//
		try {
			Extract4Icu ext=new Extract4Icu();
			ext.extractProcess();
			ext.printAll("College_ICU.csv");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	static String sicSub="8221";
	@Override
	protected void extractProcess() throws Exception {
		String regionUrl="https://www.4icu.org/mx/";
		String regHtml=U.getHTML(regionUrl);
		String collegeMainSec=U.getSectionValue(regHtml, "<tbody>", "<tr class=\"small\">");
		String collegeListings[]=U.getValues(collegeMainSec, "<td><a ", "</td>");
		for (String collegeSec : collegeListings) {
			U.log(collegeSec);
			String collegePageUrl="https://www.4icu.org"+U.getSectionValue(collegeSec, "href=\"", "\"");
			U.log(collegePageUrl);
			String detailHtml=U.getHTML(collegePageUrl);
			detailHtml=TranslateEnglish.removeUnicode(detailHtml);
			String name=U.getSectionValue(detailHtml, "<h1 itemprop=\"name\">", "</h1>");
			U.log(name);
			String foundDate=U.getSectionValue(detailHtml, "span itemprop=\"foundingDate\">", "</span>");
			String url=U.getSectionValue(detailHtml, "<a itemprop=\"url\" href=\"", "\"");
			String streetAdd=U.getSectionValue(detailHtml, "<span itemprop=\"streetAddress\">", "</span>");
			String city=U.getSectionValue(detailHtml, "<span itemprop=\"addressLocality\">", "</span>").replaceAll("\\(population range: .*\\)", "");
			String state=U.getSectionValue(detailHtml, "<span itemprop=\"addressRegion\">", "</span>");
			String postCode=U.getSectionValue(detailHtml, "<span itemprop=\"postalCode\">", "</span>");
			String phone=U.getSectionValue(detailHtml, "<span itemprop=\"telephone\">", "</span>");
			String fax=U.getSectionValue(detailHtml, "<span itemprop=\"faxNumber\">", "</span>");
			String empCount=U.getSectionValue(detailHtml, "<i class=\"sp sp-academic-staff\"></i>", "</td>");
			U.log(city);
			String latlng[]=getLatLonFromAddressHereApi(new String[] {streetAdd,"",city,state,postCode});
			if (latlng==null||U.isEmpty(latlng[0])) {
				latlng=new String[2];
				latlng[0]=ALLOW_BLANK;
				latlng[1]=ALLOW_BLANK;
			}
			addCompanyDetailsFromMexico(sicSub, name, phone, fax, url);
			addAddress(streetAdd, ALLOW_BLANK, city, state, postCode);
//			addAddress(add[0], add[1], add[2], add[3], add[4]);
			addBoundaries(latlng[0], latlng[1]);
			addReferenceUrl(collegePageUrl);
			addCompanyOtherDetails(ALLOW_BLANK, empCount, foundDate);
			addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
//			break;
		}
	}
	public String[] getLatLonFromAddressHereApi(String add[]) {
		try {
			String url="https://geocoder.api.here.com/6.2/geocode.json?app_id="+ApiKey.HEREAPIKEYS[0][0]+"&app_code="+ApiKey.HEREAPIKEYS[0][1]+"&searchtext="+URLEncoder.encode(add[0]+" "+add[2]+" "+add[3],"UTF-8");
			String html=U.getHTML(url);
			U.log(html);
			if (html.contains("NavigationPosition")) {
				String latLonSec=U.getSectionValue(html, "\"NavigationPosition\"", "]");
				String lat=U.getSectionValue(latLonSec, "\"Latitude\":", ",");
				String lng=U.getSectionValue(latLonSec, "\"Longitude\":", "}");
				return new String[] {lat.trim(),lng.trim()};
			}else if (html.contains("DisplayPosition")) {
				String latLonSec=U.getSectionValue(html, "\"DisplayPosition\"", ",\"Nav");
				String lat=U.getSectionValue(latLonSec, "\"Latitude\":", ",");
				String lng=U.getSectionValue(latLonSec, "\"Longitude\":", "}");
				return new String[] {lat.trim(),lng.trim()};
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
