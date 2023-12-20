package com.shatam.loqate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class GatherAddress {
	static String folderPath = "/home/chinmay/MexicoCache/Cache/api.addressy.com/";

	public static void main(String[] args) {
		
		GatherAddress ga = new GatherAddress();
		
		ga.getFilesList();
		ga.writeCSV();
	}

	private void writeCSV() {
		U.writeCsvFile(outData, "/home/chinmay/MexicoCache/Cache/LoqateCsv.csv");
	}

	String[] header = { "AQI", "AVC", "Address", "Address1", "Address2", "Address3", "Address4", "AdministrativeArea",
			"Building", "CountryName", "DeliveryAddress", "DeliveryAddress1", "DeliveryAddress2", "DeliveryAddress3",
			"DependentLocality", "DependentLocalityLeadingType", "DependentLocalityName", "DuplicateInfo",
			"GeoAccuracy", "GeoDistance", "HyphenClass", "Country", "Latitude", "Locality", "Longitude",
			"MatchRuleLabel", "PostalCode", "PostalCodePrimary", "Premise", "PremiseNumber", "Thoroughfare", "SubAdministrativeArea"};
	// {"AQI":"E","AVC":"A11-I24-P2-100","Address":"Rio Mante 2458 No. S/N,
	// Longoria,Prol Reynosa,88699 Tamaulipas TAMPS",
	// "Address1":"Rio Mante 2458 No. S/N","Address2":" Longoria","Address3":"Prol
	// Reynosa","Address4":"88699 Tamaulipas TAMPS",
	// "AdministrativeArea":"TAMPS","Building":"No. S/N,
	// Longoria","CountryName":"Mexico","DeliveryAddress":"Rio Mante 2458 No. S/N,
	// Longoria,Prol Reynosa",
	// "DeliveryAddress1":"Rio Mante 2458 No. S/N","DeliveryAddress2":"
	// Longoria","DeliveryAddress3":"Prol Reynosa","DependentLocality":"Prol
	// Reynosa",
	// "DependentLocalityLeadingType":"Prol","DependentLocalityName":"Reynosa",
	// "DuplicateInfo":"16:[12,10]Rio Mante No. 2458 No. S/N Longoria Prolongacion
	// Reynosa 16:[23,1]88699",
	// "GeoAccuracy":"A1","GeoDistance":"620195.0","HyphenClass":"A","ISO3166-2":"MX",
	// "Country":"MX","ISO3166-3":"MEX","ISO3166-N":"484","Latitude":"25.042793","Locality":"Tamaulipas","Longitude":"-98.393820",
	// "MatchRuleLabel":"C8","PostalCode":"88699","PostalCodePrimary":"88699","Premise":"2458","PremiseNumber":"2458","Thoroughfare":"Rio
	// Mante"
	static List<String[]> outData = new ArrayList<>();

	private void processFile(String filePath) {
		try {

			String fileData = FileUtil.readAllText(filePath);
			String ouputData[] = U.getValues(fileData, "\"Input\":", "]}");
			for (String nextLine : ouputData) {
				//U.log(nextLine);
				String matcheString = U.getSectionValue(nextLine, "\"Matches\":[", "}");
				LoqateMatches loqateMatches = new Gson().fromJson(matcheString + "}", LoqateMatches.class);
				if(loqateMatches.GeoAccuracy.equals("U0")) continue;
				String out[] = { loqateMatches.AQI, loqateMatches.AVC, loqateMatches.Address, loqateMatches.Address1,
						loqateMatches.Address2, loqateMatches.Address3, loqateMatches.Address4,
						loqateMatches.AdministrativeArea, loqateMatches.Building, loqateMatches.CountryName,
						loqateMatches.DeliveryAddress, loqateMatches.DeliveryAddress1, loqateMatches.DeliveryAddress2,
						loqateMatches.DeliveryAddress3, loqateMatches.DependentLocality,
						loqateMatches.DependentLocalityLeadingType, loqateMatches.DependentLocalityName,
						loqateMatches.DuplicateInfo, loqateMatches.GeoAccuracy, loqateMatches.GeoDistance,
						loqateMatches.HyphenClass, loqateMatches.Country, loqateMatches.Latitude,loqateMatches.Locality,
						loqateMatches.Longitude, loqateMatches.MatchRuleLabel, loqateMatches.PostalCode,
						loqateMatches.PostalCodePrimary, loqateMatches.Premise, loqateMatches.PremiseNumber,
						loqateMatches.Thoroughfare,loqateMatches.SubAdministrativeArea };
				outData.add(out);
				// outData.add(e)
				// U.log(matcheString);
				// LoqateMatches
			}
			U.log(ouputData.length);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void getFilesList() {
		File folder = new File(folderPath);
		outData.add(header);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				processFile(listOfFiles[i].getAbsolutePath());
			} else if (listOfFiles[i].isDirectory()) {
				System.out.println("Directory " + listOfFiles[i].getName());

			}
			//break;
		}

	}
}