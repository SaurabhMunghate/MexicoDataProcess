package com.chinmay.test;

import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

import com.opencsv.CSVWriter;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

public class ActorData {
	
	static private String[] alpha= {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S"
			,"T","U","V","W","X","Y","Z"};
	public static void main(String[] args) throws Exception {
		ArrayList<String> celebUrls=new ArrayList<>();
		HashSet<String>moviesurl=new HashSet<>();
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		String[] HEADER= {"NAME","DATE_OF_BIRTH","AGE","HEIGHT","FILMS"};
		writer.writeNext(HEADER);
		for (int i = 0; i < alpha.length; i++) {
			try {
//				U.log("https://www.bollywoodlife.com/celeb/search/"+alpha[i]+"/");
				String celebListHtml= U.getHTML("https://www.bollywoodlife.com/celeb/search/"+alpha[i]+"/");
				String celebDataSec=U.getSectionValue(celebListHtml, "<div class=\"thumbnail_sec_title\"><span>Celebrities</span", "<div class=\"clear\"></div>");
				String celebData[]=U.getValues(celebDataSec, "<figure class=\"story_fig\">", "</figcaption>");
				celebUrls.addAll(Arrays.asList(celebData));
				String paginationSec=U.getSectionValue(celebListHtml, "<div class=\"pagination\">", "</ul>");
				if (paginationSec!=null) {
					String nextPage=U.getSectionValue(paginationSec, "</li><li>", "next<i class=\"fa fa-angle-right\">");
					while(nextPage!=null) {
						String nextHtml=U.getHTML(U.getSectionValue(nextPage, "href=\"", "\""));
						celebData=U.getValues(nextHtml, "<figure class=\"story_fig\">", "</figcaption>");
						celebUrls.addAll(Arrays.asList(celebData));
						paginationSec=U.getSectionValue(nextHtml, "<div class=\"pagination\">", "</ul>");
						nextPage=U.getSectionValue(paginationSec, "</li><li>", "next<i class=\"fa fa-angle-right\">");
					}
				}
//				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		U.log(celebUrls.size());
		for (String celebData : celebUrls) {
			try {
//				if (!celebData.contains("Aamir Khan")) continue;
				if (celebData.contains("Meiyang Chаng"))continue;
				String name=U.getSectionValue(celebData, "title=\"", "\"");
				String dataUrl=U.getSectionValue(celebData, "href=\"", "\"");
				
				String dataHtml=U.getHTML(dataUrl);
				String flimsHtml=U.getHTML(dataUrl+"filmography/");
				String flimmsSection=U.getSectionValue(flimsHtml, "<div class=\"celeb_filmography_tab\">", "<div class=\"clear\"></div></div></div></div><div class=\"clear\">");
//				U.log(flimmsSection);
				String films="";
				if (flimmsSection!=null) {
					String filmsList[]=U.getValues(flimmsSection, "<figure class=\"film_fig\">", "</figure>");
					for (String film : filmsList) {
						U.log(film);
						moviesurl.add(U.getSectionValue(film, "href=\"", "\""));
						films+=U.getSectionValue(film, "title=\"", "\"")+";";
					}
				}
				if (films.length()>0) {
					films=films.substring(0, films.length()-1);
				}
				String dob=U.getSectionValue(dataHtml, "<span class=\"ques\">born</span><span class=\"anws\">", "</span>");
				String height=U.getSectionValue(dataHtml, "<span class=\"ques\">Height</span><span class=\"anws\">", "</span>");
				String zodiacSign=U.getSectionValue(dataHtml, "<span class=\"ques\">Zodiac</span><span class=\"anws\">", "</span>");
				if (dob!=null) {
					if (height!=null&&!height.equals("0 feet 0 inches")) {}else {
						height="";
					}
					dob=dob.replaceAll("&nbsp;.*", "");
					U.log(name);
					U.log(dob);
					U.log(zodiacSign);
					DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
					Date date = format.parse(dob);
					LocalDate today = LocalDate.now();
					LocalDate birthday = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					Period p = Period.between(birthday, today);
					String age=""+p.getYears();
//					System.out.println("You are " + p.getYears() + " years, " + p.getMonths() + " months, and " + p.getDays() + " days old");
					U.log(height);
					U.log(age);
					String out[]= {name,dob,age,height,films};
					writer.writeNext(out);
					U.log("+++++++++++++++++++++++++++++++++");
//					break;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception();
			}
			
		}
		U.log(moviesurl.size());
		FileUtil.writeAllText(U.getCachePath()+"ActorCSV.csv", sw.toString());
		writer.close();
		sw.close();
	}
}
