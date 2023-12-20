package com.saurabh.demo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shatam.utils.U;

//import au.com.bytecode.opencsv.CSVWriter;

public class WebsiteDataChecker {
	private static final String String = null;

	public static void main(String[] args) throws IOException {
		// URL of the website to check
//		String websiteUrl = "";
		int i = 0;
		String csvFile = "/home/shatam-100/Desktop/WaterView_CII/AllMexico.csv";
		String filePath= "/home/shatam-100/Desktop/WaterView_CII/Untitled_1.csv";
		File fi = new File(filePath);
		FileWriter outputfile = new FileWriter(fi);
//		CSVWriter writer = new CSVWriter(outputfile);
		String line1 = "";
		String line2 = "";
		String csvSplitBy = ",";
		File file = new File("/home/shatam-100/Desktop/WaterView_CII/Restaurants.csv");
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);
		List<String[]> dataLines = new ArrayList<>();
		List<String[]> updatedData = new ArrayList<>();
		List<String[]> ResultantData = new ArrayList<>();
		String[] columnsRe = null;
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
			while ((line1 = br.readLine()) != null) {
				String[] columns = line1.split(csvSplitBy);
				if (columns[0].contains("ID"))
					continue;

				// URL of the website to check
				String websiteUrl = "https://www.google.com/search?q=Domino%27s+Pizza+Boulevard+Gran+Avenida+El+Dorado+1303+Las+Quintas+Fracc+Las+Quintas+Culiacan+Sinaloa+80060";

////				 Regular expressions to extract the data
//				String companyNameRegex = "Taqueria Guadalajara";
//				String addressRegex = "1033 South Park St";
//				String neighborhoodRegex = "Madison";
//				String cityRegex = "Madison";
//				String stateRegex = "WI";
//				String zipRegex = "53715";
//				String phoneRegex = "9am - 10pm";

//				if (columns.length >= 8) {
//				String websiteUrl = columns[15];
				String companyNameRegex = columns[7];
				String addressRegex = columns[8];
				String neighborhoodRegex = columns[9];
				String cityRegex = columns[10];
				String stateRegex = columns[11];
				String zipRegex = columns[12];
				String phoneRegex = columns[13];
				String ans = "";

				websiteUrl = "https://www.google.com/search?q="+companyNameRegex.replace("'", "%27") + addressRegex + neighborhoodRegex
						+ cityRegex + stateRegex +"+"+ zipRegex + "";
				websiteUrl = websiteUrl.replaceAll(" ", "+").replaceAll("\"", "");
				
				//ONE
//				if(!websiteUrl.contains(""))return;
				
				String shopHtml = U.getHTML(websiteUrl);

				line2 = websiteUrl + "," + companyNameRegex + "," + addressRegex + "," + neighborhoodRegex + ","
						+ cityRegex + "," + stateRegex + "," + zipRegex + "," + phoneRegex + "";
				String[] columnsResult = line2.split(csvSplitBy);
//				String[] columnsResult = { websiteUrl, companyNameRegex, addressRegex };
//				try {

				String li = checkAllData(shopHtml, websiteUrl, companyNameRegex, addressRegex, neighborhoodRegex,
						cityRegex, stateRegex, zipRegex, phoneRegex);
				columnsRe = li.split(csvSplitBy);
//				columnsResult[0] = websiteUrl; 

//				dataLines.addAll(websiteUrl);
//				bw.append(websiteUrl + "\n");
//						 dataLines.add(ans);
//				} catch (Exception e) {
//					System.out.println("-----------------  Exception  -------------------");
//					// TODO: handle exception
//				}

				dataLines.add(columns);
				updatedData.add(columnsRe);
//				System.out.println("Done  " + i + " " + websiteUrl + companyNameRegex + addressRegex);
				i++;
				if(i==50)break;
//				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

//		U.writeDataToCSV(dataLines, "/home/shatam-100/Desktop/WaterView_CII/dataLines.csv");
		writeDataToCSV(updatedData, "/home/shatam-100/Desktop/WaterView_CII/updatedData.csv");

	}
	public static void writeDataToCSV(List<String[]> data, String filePath) throws IOException {
		FileWriter fileWriter = new FileWriter(filePath);

		for (String[] row : data) {
			for (int i = 0; i < row.length; i++) {
				fileWriter.append(row[i]);
				if (i != row.length - 1) {
					fileWriter.append(",");
				}
			}
			fileWriter.append("\n");
		}

		fileWriter.flush();
		fileWriter.close();
	}

	public static ArrayList<String> matchAll(String html, String expression, int groupNum) {

		Matcher m = Pattern.compile(expression, Pattern.CASE_INSENSITIVE).matcher(html);

		ArrayList<String> list = new ArrayList<String>();

		while (m.find()) {
			// Util.log(m.group(groupNum));
			list.add(m.group(groupNum).trim());
		}
		return list;

	}
	public static String checkAllData(String shopHtml, String websiteUrl, String companyNameRegex, String addressRegex,
			String neighborhoodRegex, String cityRegex, String stateRegex, String zipRegex, String phoneRegex)
			throws IOException {
		String old = websiteUrl + "," + companyNameRegex + "," + addressRegex + "," + neighborhoodRegex + "," + cityRegex
		+ "," + stateRegex + "," + zipRegex + "," + phoneRegex + ",";
		System.out.println(websiteUrl);
//		String websiteContent = "";
//		try {
//			// Retrieve the website content
//			URL url = new URL(websiteUrl);
//			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
//			StringBuilder contentBuilder = new StringBuilder();
//			String line;
//			while ((line = reader.readLine()) != null) {
//				contentBuilder.append(line);
//			}
//			reader.close();
//			websiteContent = contentBuilder.toString();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//
//		String websiteContentt = "";
//		try {
////			 Retrieve the Contact website content
//			websiteUrl = websiteUrl + "/contacto";
//			URL urll = new URL(websiteUrl);
//			BufferedReader readerr = new BufferedReader(new InputStreamReader(urll.openStream()));
//			StringBuilder contentBuilderr = new StringBuilder();
//			String linee;
//			while ((linee = readerr.readLine()) != null) {
//				contentBuilderr.append(linee);
//			}
//			readerr.close();
//			websiteContentt = contentBuilderr.toString();
//		} catch (Exception e) {
//			// TODO: handle exception
//		}

//		String websiteContentt ="";
//		String websiteContent ="";
		System.out.println(" length:::" + shopHtml.length());

//		boolean isDataPresent = false;
		// Check if all data is present
//		String isDataPresent1 = null, isDataPresent2 = null, isDataPresent3 = null, isDataPresent4 = null,
//				isDataPresent5 = null, isDataPresent6 = null, isDataPresent7 = null;

//		System.out.println("matchAll:::"+U.matchAll(websiteContent, "([A-Z]{2})\\s+(\\d{4,5})", 0));
//		System.out.println("matchAll:::"+U.matchAll(websiteContent, "^(?:\\+1)?\\s*(?:\\d{3}|\\(\\d{3}\\))[- ]*\\d{3}[- ]*\\d{4}$", 0));
//		System.out.println("matchAll:::"+U.matchAll(websiteContent, "Service options", 0));
//		System.out.println("matchAll:::"+U.matchAll(websiteContent, "Address:", 0));

//		checkdataP(websiteContent, companyNameRegex);
//		checkdataP(websiteContent, addressRegex);
//		checkdataP(websiteContent, neighborhoodRegex);
//		checkdataP(websiteContent, cityRegex);
//		checkdataP(websiteContent, stateRegex);
//		checkdataP(websiteContent, zipRegex);
//		checkdataP(websiteContent, phoneRegex);

		companyNameRegex = FindDataP(shopHtml, companyNameRegex);
		addressRegex = FindDataP(shopHtml, addressRegex);
		neighborhoodRegex= FindDataP(shopHtml, neighborhoodRegex);
		cityRegex = FindDataP(shopHtml, cityRegex);
		stateRegex = FindDataP(shopHtml, stateRegex);
		zipRegex = FindDataP(shopHtml, zipRegex);
		phoneRegex = FindDataP(shopHtml, phoneRegex.replace("+", ""));

//		if (!checkDataPresence(websiteContent, companyNameRegex)) {
////			System.out.println("COMPANY_NAME is not present.");
//			isDataPresent1 = "COMPANY_NAME is not present.";
//		}
//		if (!checkDataPresence(websiteContent, addressRegex)) {
////			System.out.println("ADDRESS is not present.");
//			isDataPresent2 = "ADDRESS is not present.";
//		}
//		if (!checkDataPresence(websiteContent, neighborhoodRegex)) {
////			System.out.println("NEIGHBORHOOD is not present.");
//			isDataPresent3 = "NEIGHBORHOOD is not present.";
//		}
//		if (!checkDataPresence(websiteContent, cityRegex)) {
////			System.out.println("CITY is not present.");
//			isDataPresent4 = "CITY is not present.";
//		}
//		if (!checkDataPresence(websiteContent, stateRegex)) {
////			System.out.println("STATE is not present.");
//			isDataPresent5 = "STATE is not present.";
//		}
//		if (!checkDataPresence(websiteContent, zipRegex)) {
////			System.out.println("ZIP is not present.");
//			isDataPresent6 = "ZIP is not present.";
//		}
//		if (!checkDataPresence(websiteContent, phoneRegex)) {
////			System.out.println("PHONE is not present.");
//			isDataPresent7 = "PHONE is not present.";
//		}

//		if (isDataPresent) {
////			System.out.println("All data is present.");
//		}
		return old+companyNameRegex + "," + addressRegex + "," + neighborhoodRegex + "," + cityRegex
				+ "," + stateRegex + "," + zipRegex + "," + phoneRegex + "";
//		return ""+isDataPresent+isDataPresent1+isDataPresent2+isDataPresent3+isDataPresent4+isDataPresent5+isDataPresent6+isDataPresent7;
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		return "Exception";
	}

	private static boolean checkDataPresence(String websiteContent, String regex) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(websiteContent);
		return matcher.find();
	}

	private static void checkdataP(String websiteContent, String regexd) {
		// TODO Auto-generated method stub
		String input = websiteContent;
//        String regex = "([a-zA-Z]+\\s*[a-zA-Z]*),\\s*([A-Z]{2})\\s+(\\d{5})";
		String regex = regexd;
		Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("Match found!");
			System.out.println(matcher.group());
		} else {
			System.out.println("Match not found!");
		}

	}

	private static String FindDataP(String websiteContent, String regexd) {
		// TODO Auto-generated method stub
		String input = websiteContent;
//		websiteContent = websiteContent.replaceAll("<br>", "");
//		        String regex = "(\\d{4,5})\\s*([a-zA-Z]+\\s*[a-zA-Z]+\\s*[a-zA-Z]*)<br>\\n\\s*\\n*([a-zA-Z]+\\s*[a-zA-Z]*),\\s*([A-Z]{2})\\s+(\\d{4,5})";
//		String regex = "\\s*\\n*([a-zA-Z]+\\s*[a-zA-Z]*),\\s*([A-Z]{2})\\s+(\\d{4,5})|(\\d{4,5})\\s*([a-zA-Z]+\\s*[a-zA-Z]+\\s*[a-zA-Z]*)";
//		String regex1 = "([A-Z]{2})\\s+(\\d{4,5})";

		String ans= "";
//				String regex = regexd;
		Pattern pattern = Pattern.compile(regexd, Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("Match found!");
			ans = matcher.group();
			System.out.println(matcher.group());
		} else {
//			System.out.println("Match not found!$");
		}
		return ans;
	}
}
