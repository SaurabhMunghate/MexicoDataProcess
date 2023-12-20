package com.chinmay.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class RestaurantGuru {
	public static void main(String[] args) {
		try {
			U.log(getHTML("https://restaurantguru.com/Manzanillo/6","Manzanillo"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
public static String getHTML(String path,String city) throws IOException {
		
		path = path.replaceAll(" ", "%20");
		String fileName = U.getCache(path);
		File cacheFile = new File(fileName);
		if (cacheFile.exists())
			return FileUtil.readAllText(fileName);
		URL url = new URL(path);
		String html = null;
		Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
				"68.251.250.193",	8080));
		final URLConnection urlConnection = url.openConnection();  //proxy
		try {
			urlConnection
					.addRequestProperty("User-Agent",
							"Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/75.0.3770.142 Chrome/75.0.3770.142 Safari/537.36");
			urlConnection.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
			urlConnection.addRequestProperty("Cache-Control", "max-age=0");
			urlConnection.addRequestProperty("Referer", "https://restaurantguru.com/"+city);
			urlConnection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
			urlConnection.setConnectTimeout(5000);
			// U.log("getlink");
			final InputStream inputStream = urlConnection.getInputStream();
			
			html = IOUtils.toString(inputStream,StandardCharsets.UTF_8);
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
