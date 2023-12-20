package com.tequila.extraction;

import com.shatam.utils.U;

public class ExtractDataUBS {
	public static void main(String[] args) {
		String mainUrl="https://www.ubs.com/";
		String srcUrl="https://www.ubs.com/locations.html";
		try {
			String dataJson=U.getHTML("https://www.ubs.com/locations/_jcr_content.lofisearch.all.en.data");
//			U.log(dataJson);
			String datajsonSecs[]=U.getValues(dataJson, "{\"number\":", "\"type\":\"");
			U.log(datajsonSecs.length);
			int i=0;
			for (String dataJsonSec : datajsonSecs) {
				if (dataJsonSec.contains("(MX)")) {
					U.log(dataJsonSec);
					String branchUrl=U.getSectionValue(dataJsonSec, "\"id\":[\"", "\"]").replace("/", "_");
					U.log(branchUrl);
					String dataBranchData=U.getHTML("https://www.ubs.com/locations/_jcr_content.location."+branchUrl+".en.data");
					U.log(dataBranchData);
					//https://www.ubs.com/locations/_jcr_content.location._en_mx_ciudad-de-mexico_pedregal-24-piso-11.en.data
				}
			}
			U.log(i);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
