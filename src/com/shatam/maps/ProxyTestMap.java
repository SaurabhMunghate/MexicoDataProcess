package com.shatam.maps;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.shatam.utils.FileUtil;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ProxyTestMap {

	public final static String COUNTRY_US = "United States";
	public final static String COUNTRY_MX = "Mexico";
	public final static String COUNTRY_CA = "Canada";
	
	private final static String baseUrl = "https://free-proxy-list.net/";

	public static void main(String[] args) throws IOException, InterruptedException {
		findProxyWithHttps(ProxyTestMap.COUNTRY_US);
//		loadProxyWithDuration();
	}
	
	public static void delete(){
		try {
			U.deleteFile(U.getCache(baseUrl));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	public static List<String[]> findProxy(String country) throws IOException, InterruptedException{
		List<String[]> proxyList = new ArrayList<String[]>();
		String proxyHtml = U.getPageSource(baseUrl);		
		String section = U.getSectionValue(proxyHtml, "<thead>", "</tfoot>");
		String [] rowSection = U.getValues(section, "<tr>", "</tr>");
		int count = 0;
		for(String rowSec : rowSection){
			if(rowSec.contains(country)){
				String ip = Util.match(rowSec, "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
				String port = Util.match(rowSec, "<td>\\s+?(\\d{2,5})\\s+?</td>", 1);
				String https = U.getSectionValue(rowSec, "<td class=\"hx\">", "</td>");
				if(https != null){
					proxyList.add(new String[]{ip.trim(),port.trim()});
					count++;
				}
			}
		}
		U.log("Found Ip ::"+count);
/*		for(String[] proxy : proxyList){
			U.log(proxy[0]+"\t"+proxy[1]);
		}
*/		return proxyList;
	}
	
	public static List<String[]> findProxyWithHttps(String country) throws IOException, InterruptedException{
		List<String[]> proxyList = new ArrayList<String[]>();
		String proxyHtml = U.getPageSource(baseUrl);		
		String section = U.getSectionValue(proxyHtml, "<thead>", "</tfoot>");
		String [] rowSection = U.getValues(section, "<tr>", "</tr>");
		int count = 0;
		for(String rowSec : rowSection){
			if(rowSec.contains(country)){
				String ip = Util.match(rowSec, "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
				String port = Util.match(rowSec, "<td>\\s+?(\\d{2,5})\\s+?</td>", 1);
				String https = U.getSectionValue(rowSec, "<td class=\"hx\">", "</td>");
				if(https != null){
					if(https.contains("yes")){
						proxyList.add(new String[]{ip.trim(),port.trim()});
						count++;
					}
				}
			}
		}
		U.log("Found Ip ::"+count);
/*		for(String[] proxy : proxyList){
			U.log(proxy[0]+"\t"+proxy[1]);
		}
*/		return proxyList;
	}

	public static List<String[]> findProxyWithHttp(String country) throws IOException, InterruptedException{
		List<String[]> proxyList = new ArrayList<String[]>();
		String proxyHtml = U.getPageSource(baseUrl);		
		String section = U.getSectionValue(proxyHtml, "<thead>", "</tfoot>");
		String [] rowSection = U.getValues(section, "<tr>", "</tr>");
		int count = 0;
		for(String rowSec : rowSection){
			if(rowSec.contains(country)){
				String ip = Util.match(rowSec, "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}");
				String port = Util.match(rowSec, "<td>\\s+?(\\d{2,5})\\s+?</td>", 1);
				String https = U.getSectionValue(rowSec, "<td class=\"hx\">", "</td>");
				if(https != null){
					if(https.contains("no")){
						proxyList.add(new String[]{ip.trim(),port.trim()});
						count++;
					}
				}
			}
		}
		U.log("Found Ip ::"+count);
/*		for(String[] proxy : proxyList){
			U.log(proxy[0]+"\t"+proxy[1]);
		}
*/		return proxyList;
	}
	
/*	public static void loadProxyWithDuration() throws IOException{
		long currentTime = System.currentTimeMillis();
		
		File file = new File(System.getProperty("user.home")+File.separator+"previousTime.log");
		
		if(file.exists()){
			FileReader reader = new FileReader(file);
			int i = 0;
			String num = "";
			while((i = reader.read())!= -1){
//				U.log((char)i);
				num +=(char)i;
			}
			reader.close();
			long previousTime = Long.parseLong(num.trim());
//			U.log(previousTime);
			
			long min = (currentTime - previousTime)/60000;
			if(min > 10){
				U.log(min);
				String proxyFilePath = U.getCache(baseUrl);
				File proxyfile = new File(proxyFilePath);
				if(proxyfile.exists()){
					proxyfile.delete();
				}
				findProxyWithHttps();
				writeFile(file, currentTime);
			}else{
				findProxyWithHttps();
			}			
		}else{
			writeFile(file,currentTime);
		}
	}
	
	private static void writeFile(File file, long time) throws IOException{
		FileWriter writer = new FileWriter(file);
		writer.write(String.valueOf(time));
		writer.flush();
		writer.close();
	}*/

}
