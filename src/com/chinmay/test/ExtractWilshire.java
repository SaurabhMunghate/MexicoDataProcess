package com.chinmay.test;

import java.io.IOException;

import org.apache.commons.text.StringEscapeUtils;

import com.shatam.utils.U;

public class ExtractWilshire {
	public static void main(String[] args) throws IOException {
		String regionUrl="https://www.wilshire-homes.com/austin";
		String regionHtml=U.getHTML(regionUrl);
		String jsonSec=U.getSectionValue(regionHtml, "<script id=\"app-root-state\" type=\"application/json\">", "</script>");
		jsonSec=StringEscapeUtils.unescapeJson(jsonSec.replace("&q;", "\"").replace("&g;", ">").replace("&l;", "<").replace("&s;", "'"));
//		U.log(jsonSec);
		String communities[]=U.getValues(jsonSec, "\"InfoWindow\":\"<div><div class='item'>", "\"Communities\":[");
		U.log(communities.length);
		for (String commSec : communities) {
//			U.log(commSec);
			String commUrl=U.getSectionValue(commSec, "href=\"", "\"");
			if (commUrl.contains("http://www.buildonyourlot")||commUrl.contains("/designcenter")) {
				continue;
			}
			U.log(commSec);
			U.log(commUrl);
//			break;
		}
	}
}
