package com.tequila.extraction;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.shatam.utils.U;
import com.shatam.utils.Util;

public class OfertiaExtraction {

	public static void main(String[] args){
		// TODO Auto-generated method stub
		extractPhoneNum();
	}
	
	private static final String FILE_NAME = 
			"/home/glady/Downloads/MexicoProject/pramod/Untitled Folder/Ofertia-Supermarket_Unique_Record_Remain_12Phone_Ad.csv";

	static void extractPhoneNum(){
		List<String[]> readLines = U.readCsvFile(FILE_NAME);
		
		String[] lines = null;
		int i = 0;
		
		Iterator<String[]> it = readLines.iterator();
		
		while(it.hasNext()){
			lines = it.next();
			
			if(i++ == 0)continue;
			
//			if(i< 2999)continue;
//			if(i< 1210)continue;
//			if(i< 2280)continue;
//			if(i< 2372)continue;
//			if(i< 2670)continue;
			if(i< 2837)continue;
			
			if(lines[13].trim().isEmpty()){
				U.log(">>"+i);
				lines[13] = phoneNumber(lines[24]);
				if(!lines[13].isEmpty()) lines[13] = U.formatNumbersAsCode(lines[13]);
			}
//			if(i == 3500)break;			
//			if(i == 1400)break;
//			if(i == 2369)break;
//			if(i == 2400)break;
			if(i == 2701)break;
		}
		
		U.writeCsvFile(readLines, FILE_NAME.replace(".csv", "Phone_Ad.csv"));
	}
	
	static String phoneNumber(String url){
		U.log(url);
/*		if(url.contains("https://www.ofertia.com.mx/tiendas/San-Nicolas-de-los-Garza/bodega-aurrera-av-santo-domingo-sn-esq-diego-rivera-col-san-isidro-san-nicols-de-los-garza-nl-cp-66430/filial-496221657"))
			return "";
*/		String phoneNum = null;
		try{
			String html = U.getHTML(url);
			phoneNum = Util.match(html, "<span class=\"icon-info-phone\"></span>\\s+<p>(.*?)</p>", 1);
		}catch(Exception e){
			e.printStackTrace();
		}
		if(phoneNum == null) return "";
		if(phoneNum.length() < 6) return "";
		U.log(phoneNum);
		return phoneNum;
	}
}
