package com.priti.demo.extractareacode;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.lang3.StringEscapeUtils;

import com.opencsv.CSVWriter;
import com.shatam.utils.U;

public class ExtractingAreaCodeOfMexico {

	public static void main(String[] args) throws IOException {
		CSVWriter writer = new CSVWriter(new FileWriter("/home/chinmay/MexicoCache/Mexico_All_State_City_AreaCode_13_09_2018.csv"));
		String [] header = {"Sr.No.","City","State","Area_Code"};
		writer.writeNext(header);
		
		String html = U.getHTML("https://en.wikipedia.org/wiki/Area_codes_in_Mexico_by_code");
		String subHtml = "";
		int count = 0;
		try{
		for(String url : U.getValues(html, "<dl><dd>See <a href=\"", "\"")){
			U.log("Area_codes Limit: https://en.wikipedia.org/"+url);
			subHtml = U.getHTML("https://en.wikipedia.org/"+url);
			
			subHtml = subHtml.replace("é", "e").replace("á", "a").replace("ó", "o").replace("ú", "u").replace("ñ", "n").replace("í", "i").replace("Á", "A");
			//------------List----------
			String sec = U.getSectionValue(subHtml, "<td><b>Code</b>", "</tbody></table>");
			if(sec == null)sec = U.getSectionValue(subHtml, "<th>Code", "</tbody></table>");
			if(sec == null)sec = U.getSectionValue(subHtml, "<th scope=\"col\">Code", "</tbody></table>");
//			U.log(sec);
			//System.out.println("url : "+url +"\t"+U.getValues(sec, "<tr>", "</tr>").length);
			for(String citySec : U.getValues(sec, "<tr>", "</tr>")){
				//U.log(citySec);
				String[] mySec = U.getValues(citySec, "<td>", "</td>");
				for(int i =0 ; i<mySec.length;i++){
					mySec[i]  = mySec[i].replaceAll("<a (.*?)>|</a>", "").trim();
					
					//U.log(mySec[i]);
				}
				count++;
				writer.writeNext(new String[]{count+"",mySec[0],mySec[1],mySec[2]});
				//System.out.println();
			}
			//break;
		}
		writer.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	U.log("Total : "+count);
	}
	
	
}
