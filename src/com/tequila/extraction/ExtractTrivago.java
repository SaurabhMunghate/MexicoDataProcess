package com.tequila.extraction;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;

import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;

public class ExtractTrivago extends DirectoryList{
	public static void main(String[] args) throws Exception {
		ExtractTrivago ext=new ExtractTrivago();
		ext.extractProcess();
		ext.printAll("TrivagoData.csv");
	}
	@Override
	protected void extractProcess() throws Exception {
		HashSet<String>dataSet=new HashSet<>();
		String url="https://cdn-hs-graphql-dca.trivago.com/graphql";
		String para="{\"operationName\":\"regionSearchKotlin\",\"variables\":{\"searchType\":\"cep.json\",\"queryParams\":\"{\\\"tid\\\":\\\"9z1ApKuN9D7PeuTTXdBGWobbV_\\\"}\",\"pollData\":\"SoNPlhIIcB9mAtWIDj7ZzLmPhLBmv0r83BRR43YnCyll/bYfCSi/LSajbmMzLJU3f+zmFRgsRuw2i0k8tRdb5+OsEdsCNFPf7YWTbCaW+/TWMkvLN9v569aBFWqAtvpVpYQDpfRAzp+R2V/Yc7BAZKqGBZ7wSYp1I383HxWxZMiXKumzYwCt4rBBTB/qxIDMdW+5GDu+xZ3CElsnFuTp/16oBJr708Dl3+hrZgywzpZQRqh2/O6QExd6CLWRpmQxu+2IuVvxOhnAHn4DRGM2fbatH0KgmMwj/JFCR5OmWs60LhAdDWnq4QmotezvEzUxiHzbmOrNHlJIvYsC61cQJGxJXANl5UhbiAaX0K0NREEnYDQrJbPFCpKSaVmu54sL3lFIslTtK8iKzQF8TPiMBeeUjiD0Wa0ZfxvfLs3XWmULwPgmN/Q4rtdhXVF7xdG/xKV/qv2+AIlNfOgWs65HXWHIdOAZ+gLzi/jdIwEBDeHYLnrCLBWJBLgf5gt7IfIooF1uFKzTC2130LR2gIFTz9bUKsIqpm/4mY3aU7LZiC88JV40nVUHnh9yet9iSjYZR4J2dpStliEk9jDWkSTMmwOrqdi1qAlhJHE0VjJ12DSXuXLd2/RB24mP4f/6bJWGnn+e1UMRYJhCuMrVKHLnhKg5VuF7jJ0Q+hmO7CzEr0q3SYgMdOPGBBw3xvLSz0uhGY747fKeeRTkDmKja6yZdVWwYqOL8xBsNzqJnEw/Fd16giD0nCm+cPz0TSjHsq0UWIgNzJgi+Xh1/TabR68jTQJXuzMBgRAAOoP6dX9ZDWIqNQ8/FY2LSDlTHJPGpDztU5LCNFsi4ujbQEdlSHVRS/k3Tp02Gnk4RzMk2pMi906myccPxq23pNgIaGPTzp1pA64hpyRUrXbCq5flDqWbvBw8DLLRt1x7L0zYmA5b1x9w/3u8O2QShpEuUbdRpUkXuLq0weRT32B1rBhxN64KFg==\",\"isAirBnbSupported\":true,\"openItemsInNewTab\":false,\"showHomeAwayPremierProperties\":false,\"isMobileList\":false,\"skipAlternativeDeals\":false,\"skipMinPriceExtraInfo\":true,\"shouldSkipRedirect\":true},\"extensions\":{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"0a514c3befbd26ce610d76bd656dfb8223d9fe992c8efdf53dbcf6291b2f4421\"}}}";
		String postHtml=U.sendPostRequest(url, para);
//		U.log("--"+postHtml);
		String totalCount=U.getSectionValue(postHtml, "\"totalCount\":", ",");
		int statrtIndex=25;
		int page=1;
		String paramters="{\"operationName\":\"regionSearchKotlin\",\"variables\":{\"searchType\":\"cep.json\",\"queryParams\":\"{\\\"tz\\\":-330,\\\"pra\\\":\\\"\\\",\\\"channel\\\":\\\"b,isd:0\\\",\\\"csid\\\":41,\\\"ccid\\\":\\\"XaVLhac1dDDbXOP-qE7fVwAAABA\\\",\\\"adl\\\":3,\\\"crcl\\\":\\\"-102.552788/23.634501,20000\\\",\\\"s\\\":\\\"0\\\",\\\"uiv\\\":\\\"128/200:1\\\",\\\"tid\\\":\\\"9z1ApKuN9D7PeuTTXdBGWobbV_\\\",\\\"sp\\\":\\\"20191029/20191030\\\",\\\"rms\\\":\\\"2\\\",\\\"p\\\":\\\"ca\\\",\\\"l\\\":\\\"en-CA\\\",\\\"ccy\\\":\\\"CAD\\\",\\\"accoff\\\":"+statrtIndex+",\\\"acclim\\\":25}\",\"pollData\":null,\"isAirBnbSupported\":true,\"openItemsInNewTab\":false,\"showHomeAwayPremierProperties\":false,\"isMobileList\":false,\"skipAlternativeDeals\":false,\"skipMinPriceExtraInfo\":true,\"shouldSkipRedirect\":true},\"extensions\":{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"0a514c3befbd26ce610d76bd656dfb8223d9fe992c8efdf53dbcf6291b2f4421\"}}}";
		while(statrtIndex<Integer.parseInt(totalCount)) {
			postHtml+=U.sendPostRequest(url, paramters);
			page++;
			statrtIndex=statrtIndex+25;
			paramters="{\"operationName\":\"regionSearchKotlin\",\"variables\":{\"searchType\":\"cep.json\",\"queryParams\":\"{\\\"tz\\\":-330,\\\"pra\\\":\\\"\\\",\\\"channel\\\":\\\"b,isd:0\\\",\\\"csid\\\":41,\\\"ccid\\\":\\\"XaVLhac1dDDbXOP-qE7fVwAAABA\\\",\\\"adl\\\":3,\\\"crcl\\\":\\\"-102.552788/23.634501,20000\\\",\\\"s\\\":\\\"0\\\",\\\"uiv\\\":\\\"128/200:1\\\",\\\"tid\\\":\\\"9z1ApKuN9D7PeuTTXdBGWobbV_\\\",\\\"sp\\\":\\\"20191029/20191030\\\",\\\"rms\\\":\\\"2\\\",\\\"p\\\":\\\"ca\\\",\\\"l\\\":\\\"en-CA\\\",\\\"ccy\\\":\\\"CAD\\\",\\\"accoff\\\":"+statrtIndex+",\\\"acclim\\\":25}\",\"pollData\":null,\"isAirBnbSupported\":true,\"openItemsInNewTab\":false,\"showHomeAwayPremierProperties\":false,\"isMobileList\":false,\"skipAlternativeDeals\":false,\"skipMinPriceExtraInfo\":true,\"shouldSkipRedirect\":true},\"extensions\":{\"persistedQuery\":{\"version\":1,\"sha256Hash\":\"0a514c3befbd26ce610d76bd656dfb8223d9fe992c8efdf53dbcf6291b2f4421\"}}}";
		}
		U.log(page);
		String secs[]=U.getValues(postHtml, "{\"id\":{\"id\"", "\"packageDealOTAId\"");
		dataSet.addAll(Arrays.asList(secs));
		U.log(dataSet.size());
		
		for (String dataSec : dataSet) {
			String hotelId=U.getSectionValue(dataSec, ":", ",");
			String hotelName=U.getSectionValue(dataSec, "\"name\":{\"value\":\"", "\"");
			extractData(hotelId,hotelName);
		}
	}
	String sicSub="7011";
	int count=0;
	private void extractData(String hotelId, String hotelName) throws IOException {
		count++;
//		if (count>10) {
//			return;
//		}
		String detailUrl="https://www.trivago.ca/api/v1/accommodation/"+hotelId+"/overview.json?requestId=v91_10_3_aj_ca_CA_CA";
		String detailJson=U.getHTML(detailUrl);
		detailJson=TranslateEnglish.removeUnicode(detailJson);
		U.log(detailJson);
		String lat=U.getSectionValue(detailJson, "\"lat\":", ",");
		String lon=U.getSectionValue(detailJson, "\"lng\":", "}");
		String email=U.getSectionValue(detailJson, "\"email\":\"", "\"");
		String fax=U.getSectionValue(detailJson, "\"fax\":\"", "\"");
		String phone=U.getSectionValue(detailJson, "\"phone\":\"", "\"");
		String streetAddress=U.getSectionValue(detailJson, "\"street\":\"", "\"");
		String locality=U.getSectionValue(detailJson, "\"locality\":\"", "\"");
		String postalCode=U.getSectionValue(detailJson, "\"postalCode\":\"", "\"");
		
		U.log(streetAddress+" | "+locality+" | "+postalCode);
		if (streetAddress==null) {
			streetAddress=ALLOW_BLANK;
		}
		if (locality==null) {
			locality=ALLOW_BLANK;
		}
		if (postalCode==null) {
			postalCode=ALLOW_BLANK;
		}
		String web=U.getSectionValue(detailJson, "\"web\":\"", "\"");
		String website=ALLOW_BLANK;
		if (web!=null) {
			website="https://www.trivago.ca/forward.php?enc="+web;
			website=getRedirectedURL(website);
			if (website.contains(".trivago.c")||website.contains("ww.expedia.ca")) {
				website=ALLOW_BLANK;
			}
		}
		
		addCompanyDetailsFromMexico(sicSub, hotelName, phone, fax, website);
		addAddress(streetAddress, ALLOW_BLANK, locality, U.findStateFromZip(postalCode), postalCode);
//		addAddress(add[0], add[1], add[2], add[3], add[4]);
		addBoundaries(lat, lon);
		addReferenceUrl(detailUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, email);

	}
	public static String getRedirectedURL(String url) throws IOException {
		U.log(url);
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("157.245.192.250", 8080));
	    HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection(proxy);
	    con.setInstanceFollowRedirects(false);
	    con.connect();
	    url=con.getHeaderField("Location");
	    U.log("response code : "+con.getResponseCode());
	    
	    return url;
	}
}
