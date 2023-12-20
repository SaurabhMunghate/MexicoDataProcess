package com.shatam.url.validations;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import com.opencsv.CSVReader;

public class ServerLookUp {
	public static void main(String args[]) throws IOException {

		if (args.length > 0) {

			if (args[0].equalsIgnoreCase("-b")) {
				CSVReader reader = new CSVReader(new FileReader(args[1]));
				String line[] = null;
				while ((line = reader.readNext()) != null) {
					System.out.println(ServerLookUp.isReachable(line[0]) + "\t"
							+ line[0]);
				}
				reader.close();
			} else {
				System.out.println(ServerLookUp.isReachable(args[0]));
			}		
		}
		
		// System.out
		// .println(ServerLookUp.isReachable("www.vcrdrafting.ca"));

	}

	/**
	 * 
	 * @param url
	 * @return Return true, if URL is reachable otherwise false.
	 * @throws MalformedURLException
	 */
	public static boolean isReachable(String url) throws MalformedURLException {
		if (!url.contains("http") && !url.contains("https")) {
			url = "http://" + url;
		}
		URL urlObj = new URL(url);
		url = urlObj.getHost();
		if (!isWindows()) {
			url = "ping -c 2 -W 1 " + url;
		} else {
			url = "ping -w 1 " + url;
		}
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(url);
			BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(p.getInputStream()));
			String s = "";
			while ((s = inputStream.readLine()) != null) {
				if (s.contains("Packets: Sent") || s.contains("bytes of data")) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (p != null) {
				p.destroy();
			}
		}
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").startsWith("Windows");
	}
}
