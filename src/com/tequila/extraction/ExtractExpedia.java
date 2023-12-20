package com.tequila.extraction;

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
import com.shatam.utils.U;

public class ExtractExpedia  extends DirectoryList{
	public String sicSub = "5812";
	public static void main(String[] args) throws Exception {
		ExtractExpedia abs=new ExtractExpedia();
		abs.extractProcess();
	}
	@Override
	protected void extractProcess() throws Exception {
		int startIndex=0;
		int count=0;
		String dataURl="https://www.expedia.com/hotel-shop/api/1/en_US/hotels?accessibility=&amenities=&deals=&destination=Mexico&endDate=&lodging=&paymentType=&price=&regionId=117&sort=RECOMMENDED&star=&startDate=&travelerType=&useRewards=true&bedroomFilter=&propertyStyle=&localDateFormat=M%2Fd%2Fyyyy&rfrrid=TG.LP.Hotels.Hotel&count="+count+"&startingIndex="+startIndex+"&adults=2";
		String dataHtml=getHTML(dataURl);
		U.log(dataHtml);
		String dataSecs[]=U.getValues(dataHtml, "{\"type\":\"HotelViewModel\",\"hotelId\":", "\"gallery\":{\"");
		U.log(dataSecs.length);
	}
	public static String getHTML(String path) throws IOException {

		path = path.replaceAll(" ", "%20");
		// U.log(" .............."+path);
		// Thread.sleep(4000);
		String fileName = U.getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);

		URL url = new URL(path);

		String html = null;

		// chk responce code

//		int respCode = CheckUrlForHTML(path);
		 //U.log("respCode=" + respCode);
//		 if (respCode == 200) {

		// Proxy proxy = new Proxy(Proxy.Type.HTTP, new
		// InetSocketAddress("107.151.136.218",80 ));
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"198.211.96.170",	3128));
		final URLConnection urlConnection = url.openConnection();  //proxy

		// Mimic browser
		try {
			urlConnection
					.addRequestProperty("User-Agent",
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/77.0.3865.90 Chrome/77.0.3865.90 Safari/537.36");
			urlConnection.addRequestProperty("Accept", "application/json");
			urlConnection.addRequestProperty("Accept-Language",	"en-GB,en-US;q=0.9,en;q=0.8");
			urlConnection.addRequestProperty("Cache-Control", "no-cache");
//			urlConnection.addRequestProperty("cookie", "s_ecid=MCMID%7C40001247662256811604244867120855440728; tpid=v.1,1; iEAPID=0; currency=USD; linfo=v.4,|0|0|255|1|0||||||||1033|0|0||0|0|0|-1|-1; HMS=1aa9fe42-0e14-4f72-891c-af13bded0412; MC1=GUID=b4e94ae40f0149bcbcb20e06df5bada3; DUAID=b4e94ae4-0f01-49bc-bcb2-0e06df5bada3; ak_bmsc=1FE266C1F80482DA9C70C92589DAC2B7687C363C0D38000071FEA35DDD62F738~pl8brviBPKsJtooJAanwC7izNzvTnT3m0v7g0J2zi2beMZlMoZWwhQRAeYGxLGqFBlFCWf7ZY/bJexDhfMi4CHVz6XlBkl12ZQxFXNCNd0CBcI7S3PNevxT0Gtw9vFfxkbXPuHeAr5uT4bQx/tSeGUJBVVreWVz8QLcHfmcNLVcLccdS/EL2C8Dyule/fIeuCXhTcb+QSuAe+MTzBYEhUMh5RkEAJgqsTgLmHJPzg9Nnc=; AMCVS_C00802BE5330A8350A490D4C%40AdobeOrg=1; aspp=v.1,0|||||||||||||; AMCV_C00802BE5330A8350A490D4C%40AdobeOrg=1406116232%7CMCIDTS%7C18184%7CMCMID%7C40001247662256811604244867120855440728%7CMCAAMLH-1571633394%7C12%7CMCAAMB-1571633394%7CRKhpRz8krg2tLO6pguXWp5olkAcUniQYPHaMWWgdJ3xzPWQmdj0y%7CMCOPTOUT-1571035794s%7CNONE%7CMCAID%7CNONE%7CvVersion%7C2.5.0; s_cc=true; qualtrics_sample=true; rlt_marketing_code_cookie=; qualtrics_SI_sample=false; ipsnf3=v.3%7Cus%7C1%7C753%7Cchandler; _gcl_au=1.1.2125334563.1571028596; _fbp=fb.1.1571028596078.1297430665; _ga=GA1.2.672414378.1571028596; _gid=GA1.2.2005260505.1571028596; xdid=355a648a-5799-406f-9a79-48ab0d68ba88; _ctpuid=d64f606c-d293-40cf-8dd4-4891abd12972; kppid_managed=M8swYvs6; __gads=ID=6408149b5c7ae36a:T=1571028612:S=ALNI_Mb0UtputmUUBXF92bM8zBEAqt12sg; CONSENTMGR=ts:1571028630735%7Cconsent:true; pwa_csrf=04e8154d-ee37-4e24-843a-ae7b21c1e30d|3cwiGMDz9F4E_MtoMS3mCe_lJzRdyGeoZTXy_cUYEIY8VuNWXEEgMPj6Nzr_BK7zUMpIzLsN7glD6UqTJR2PaQ; JSESSIONID=CE749F403A6C639A6C81837408D3A30C; cesc=%7B%22marketingClick%22%3A%5B%22false%22%2C1571028788061%5D%2C%22hitNumber%22%3A%5B%228%22%2C1571028788061%5D%2C%22visitNumber%22%3A%5B%221%22%2C1571028592803%5D%2C%22cidVisit%22%3A%5B%22SEO.U.google.com%22%2C1571028788061%5D%2C%22entryPage%22%3A%5B%22page.TravelGuides.Hotels.CitySitemap%22%2C1571028788061%5D%2C%22seo%22%3A%5B%22SEO.U.google.com%22%2C1571028622666%5D%2C%22cid%22%3A%5B%22SEO.U.google.com%22%2C1571028622666%5D%7D; x-CGP-exp-15795=0; s_ppn=page.Hotel-Search; utag_main=v_id:016dc899f0a3001dd74190dcdd0002083004e07b00bd0$_sn:1$_ss:0$_pn:7%3Bexp-session$_st:1571030589465$ses_id:1571028594851%3Bexp-session; _gat_gtag_UA_35711341_2=1; s_ppvl=page.TravelGuides.Hotels.GeoDest.Country%2C25%2C22%2C953%2C1293%2C953%2C1920%2C1080%2C1%2CP; QSI_HistorySession=https%3A%2F%2Fwww.expedia.com%2FAll-Cancun-Hotels.d179995.Travel-Guide-City-All-Hotels~1571028598596%7Chttps%3A%2F%2Fwww.expedia.com%2FDestinations-In-Mexico.d117.Hotel-Destinations~1571028614436%7Chttps%3A%2F%2Fwww.expedia.com%2FHotel-Search%3Fdestination%3DMexico%26endDate%3D10%252F16%252F2019%26regionId%3D117%26rfrrid%3DTG.LP.Hotels.Hotel%26sort%3DRECOMMENDED%26startDate%3D10%252F15%252F2019~1571028792325; intent_media_prefs=; im_snid=979fa440-05ee-44a9-b00e-16ab4c5cfd54; s_ppv=page.TravelGuides.Hotels.GeoDest.Country%2C100%2C25%2C9069%2C1293%2C953%2C1920%2C1080%2C1%2CP; QSI_SI_3skx70ORyLVuGih_intercept=true; SawSIalready=Yes");
			urlConnection.addRequestProperty("referer", "https://www.expedia.com/Hotel-Search?destination=Mexico&endDate=10%2F16%2F2019&regionId=117&rfrrid=TG.LP.Hotels.Hotel&sort=RECOMMENDED&startDate=10%2F15%2F2019");	
			urlConnection.addRequestProperty("x-api-token", "fade1d16cc417f8d99ca5c7702a879291a61749f");
			urlConnection.addRequestProperty("x-mc1-guid", "b4e94ae4-0f01-49bc-bcb2-0e06df5bada3");	
			urlConnection.addRequestProperty("sec-fetch-mode", "cors");
			urlConnection.addRequestProperty("sec-fetch-site", "same-origin");
			urlConnection.addRequestProperty("x-remote-addr", "117.199.43.101, 104.124.54.60, 96.17.180.164, 10.7.22.126, 52.220.191.233");
			urlConnection.addRequestProperty("x-user-state", "anonymous");
			urlConnection.addRequestProperty("x-user-agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/77.0.3865.90 Chrome/77.0.3865.90 Safari/537.36");
			urlConnection.addRequestProperty("Connection", "keep-alive");
			urlConnection.addRequestProperty("authority", "www.expedia.com");
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
