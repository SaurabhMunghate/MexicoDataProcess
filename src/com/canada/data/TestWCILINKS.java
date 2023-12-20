package com.canada.data;

import java.io.IOException;

import com.shatam.utils.U;

public class TestWCILINKS {
	public static void main(String[] args) throws IOException {
		String mainurl="https://www.wcicommunities.com/";
		String mainHtml=U.getHTML(mainurl);
		String regionSecs=U.getSectionValue(mainHtml, "aria-current=\"page\">Find a Home</a>", "</ul>");
		String regions[]=U.getValues(regionSecs, "href=\"", "\"");
		for (String reg : regions) {
			String regHtmk=U.getHTML(reg);
			String commSecs[]=U.getValues(regHtmk, "<div class=\"wcicard wcicard--city\">", "</a>");
			U.log(commSecs.length);
			for (String com : commSecs) {
				U.log(U.getSectionValue(com, "href=\"", "\""));
			}
		}
	}
}
