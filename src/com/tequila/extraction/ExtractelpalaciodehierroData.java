package com.tequila.extraction;

import java.io.IOException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import com.shatam.utils.U;

public class ExtractelpalaciodehierroData {
	public static void main(String[] args) {
		try {
			extractData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void extractData() throws Exception {
		 System.setProperty("webdriver.chrome.driver", "/home/mypremserver/chromedriver");
		WebDriver driver=new ChromeDriver();
		
		String baseUrl="https://www.elpalaciodehierro.com/mapa.html";
		String baseHtml=U.getHTML(baseUrl);
		String googleSec=U.getSectionValue(baseHtml, "function abreGoogleMaps() {", "}");
//		U.log(googleSec);
		String googleUrls[]=U.getValues(googleSec.replaceAll("var url = \"\";", ""), "url = \"", "\"; break");
		for (String googleUrl : googleUrls) {
			U.log(googleUrl.trim());
			String html=U.getHtml(googleUrl.trim(), driver);
			String name=U.getSectionValue(html, "<div class=\"section-hero-header-description\">", "</div>");
			String addressSec=U.getSectionValue(html, "aria-label=\"Dirección\"", "section-info-action-icon-area");
			if (addressSec!=null) {
				addressSec=U.getSectionValue(addressSec, "class=\"widget-pane-link\" jsan=\"7.widget-pane-link\">", "</span>");
			}
			U.log(addressSec);
			U.log(U.removeHtml(name));
//			U.log(html);
//			break;
		}
		driver.close();
	}
}
