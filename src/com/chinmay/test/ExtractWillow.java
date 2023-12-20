package com.chinmay.test;

import java.io.IOException;

import com.shatam.utils.U;

public class ExtractWillow {
	public static void main(String[] args) {
		String data="https://www.zillow.com/miami-fl/";
		try {
			String html=U.getHTML(data);
			String totalRecords=U.getSectionValue(html, "<span class=\"result-count\">", "</span>");
			U.log("--"+totalRecords);
			for (int i = 2; i < Integer.parseInt(totalRecords.replaceAll(",| results", ""))/40; i++) {
				html=U.getHTML("https://www.zillow.com/miami-fl/"+i+"_p/");
				U.log(i+" "+U.getSectionValue(html, "<span class=\"result-count\">", "</span>"));
			}
			U.log(U.getSectionValue(html, "<span class=\"result-count\">", "</span>"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

