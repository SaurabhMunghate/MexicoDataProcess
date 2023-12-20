package com.shatam.verifyphone.extractareacode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.xalan.xsltc.compiler.sym;

import com.opencsv.CSVWriter;
import com.shatam.translate.TranslateEnglish;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class ExtractBnamericas {
	static int j=0;
	static ArrayList< String [] > mydata = new ArrayList<>();
	static CSVWriter writer = null;
	
	
	public static void main(String[] args) throws IOException {
		writer = new CSVWriter(new FileWriter("/home/shatam-3/MexicoCache/26_09_2018_bnamericas.csv"));
		//writer.writeNext(new String []{"SrNo","LISTING_URL","LISTING_NAME","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","EMAIL","WEBSITE_URL"});
		writer.writeNext(new String []{"SrNo","LISTING_URL","COMPANY_NAME","COMPANY_TYPE","ADDRESS_SECTIOn","ADDRESS","NEIGHBORHOOD","CITY","STATE","ZIP","PHONE","FAX","EMAIL","WEBSITE_URL"});
		int mexicoFoundCount = 0;
		String mainhtml = "";
		//mainhtml = U.getHTML("https://www.bnamericas.com/en/company-profile/subscription?page="+0);
		for(int i =0;i<510;i++){
		mainhtml = U.getHTML("https://www.bnamericas.com/en/company-profile/subscription?page="+i);
		
		//if(i==5)break;
		
		String[] section = U.getValues(mainhtml, "<td class=\"col_lista_titulo\"> <a ", "</span> </td> </tr>");
		//U.log(section.length);
		for(String sec : section){
			if(sec.contains("hidden-xs hidden-sm col_lista_nombre_paises\"> Mexico </td>")){
				//System.out.println("Main Url  : "+"https://www.bnamericas.com/en/company-profile/subscription?page="+i);
				if(sec.contains("electronic-trafic-de-mexico-sa-de-cv-etrectronic-trafic-mexico"))continue;
				getDetails(sec);
				mexicoFoundCount++;
				
			}
		}
		//break;
		}
		System.out.println("No of Mexico Company FOund : "+mexicoFoundCount);
		writer.writeAll(mydata);
		writer.close();
	}

	
	
	private static void getDetails(String sec) throws IOException {
		
		//if(j<=10)
		{
		//U.log(":::::::::::::::"+j+"::::::::::::::::::::::");
		String listingUrl ="https://www.bnamericas.com"+ U.getSectionValue(sec, "href=\"", "\"");
		//U.log("listingUrl : "+listingUrl);
		//if(listingUrl.contains(""))return;
		String listingHtml = U.getHTML(listingUrl);
		listingHtml = TranslateEnglish.convertToEnglish(listingHtml);
		String name = U.getSectionValue(listingHtml, "<h1 itemprop=\"headline\">", "<");
		//U.log("Name : "+ name);
		
		String address[] = {"","","","","",""};
		String state ="";
		String tempAddSec = "";
		String addSec =tempAddSec= U.getSectionValue(listingHtml, "<dt>Address</dt> <dd>", "</dd>");
		//U.log("addSec : "+ addSec);
		
		//----Format----
		if(addSec!=null){
			
			
			
			if(!addSec.contains(", Col"))addSec = addSec.replace("Col", ",Col").trim();
			if(addSec.trim().endsWith("Mexico"))addSec = addSec.replace(Util.match(addSec, ",\\s*Mexico$"),"");
			if(addSec.trim().endsWith(" Estado de Mexico"))addSec = addSec.replace(Util.match(addSec, " Estado de Mexico$"),"Mexico State");
			if(addSec.trim().endsWith(" D.F."))addSec = addSec.replace(Util.match(addSec, "(,)*\\s* D\\.F\\.$"),"Mexico City");
			if(addSec.trim().endsWith("Disitrito Federal"))addSec = addSec.replace(Util.match(addSec, "Disitrito Federal$"),"Mexico City");
			if(addSec.trim().endsWith("Distrito Federal"))addSec = addSec.replace(Util.match(addSec, "Distrito Federal$"),"Mexico City");
			if(addSec.trim().contains("Ciudad de Mexico"))addSec = addSec.replace(Util.match(addSec, "Ciudad de Mexico"),"Mexico City");
			addSec = addSec.replace("Nueva Leon", "Nuevo Leon").replace(" Quintana Roo.", " Quintana Roo")
					.replace("Mexico City, DF", "Mexico City, Mexico City").replace(" Ciudad De Mexico", "Mexico City")
					.replace("D.F., DF",  "Mexico City, Mexico City").replace("Cuajimalpa DF", "Cuajimalpa, Mexico City")
					.replace("Cuidad de Mexico", "Mexico City").replace(" Jalisco, Guadalajara", "Guadalajara, Jalisco")
					.replace("Miguel Hidalgo, Mexico City, Mexico City", "Miguel Hidalgo, Mexico City")
					;
			//U.log("addSec : "+ addSec);
			addSec = addSec.toLowerCase();
			
			//----------Fetch State---------
			state = getState(addSec); 
			
			//---------------Format addsection----
			addSec = addSec.replace("col., ", "col.");
			addSec = addSec.replace("cuauhtemoc,, mexico city", "cuauhtemoc, mexico city")
					.replace("queretaro, queretaro", "queretaro")
					.replace("mexico city, distrito federal, tlalpan", "mexico city")
					.replace("mexico city., mexico city, mexico city", "mexico city")
					.replace("tlacopac, san angel", "tlacopac san angel")
					.replace(" fuente,, ", " fuente,")
					.replace(", piso", " piso").replace(", oficina", " oficina")
					.replace("mexico city, mexico city mexico city", "mexico city, mexico city");
			
			
			if(addSec.contains("col.")||addSec.contains("colonia")){
			
			if(addSec.contains("col.")){
				String [] aa = addSec.split("col.");
				address[0] = aa[0];
				//U.log("addSec : "+ addSec);	
			}
			if(addSec.contains("colonia")){
				String [] aa = addSec.split("colonia");
				address[0] = aa[0];
			}
			if(address[0].length()!=0){
				
				
				String[] aa = addSec.replace(address[0], "").split(",");
				if(aa.length==2){
					U.log(addSec);
					U.log(aa.length+"\t"+Arrays.toString(aa));
					address[1]=aa[0].trim();//neighborhood
					if(aa[1].trim().equals(state)){
						address[3]=aa[1].trim();//state
					}
					else{
						address[2]=aa[1].trim();//city
					}
				}
				if(aa.length==3){
						address[1] = aa[0];
						address[2] = aa[1];
						address[3] = aa[2];
					
				}
				if(aa.length==4){
					address[1] = aa[0].trim();
					if(aa[3].trim().equals(state))
					{
						if(!aa[2].contains(state)){
							address[2] = aa[1]+","+aa[2];
						}
						else{
							address[2] = aa[1];
						}
						address[3] = aa[3].trim();
					}
					else{
						U.log("No valid Format : "+addSec);
						U.log(aa.length+"\t"+Arrays.toString(aa));
					}
				}
				//no records greater then 5 length found
			}	
			}	//address col end
			else{
				String aa[] = addSec.split(",");
				address[0] = aa[0]; //address
				if(aa.length==2){
					U.log(aa.length+"\t"+Arrays.toString(aa));					
					address[3] = aa[1];
				}
				if(aa.length==3){
					U.log(aa.length+"\t"+Arrays.toString(aa));
					address[1]=aa[1];
					if(aa[2].trim().equals(state)){
						address[3]=aa[2];
					}
					else{
						address[2]=aa[2];
					}
				}
				if(aa.length==4){
					U.log(aa.length+"\t"+Arrays.toString(aa));
					address[1] = aa[1];
					address[2] = aa[2];
					address[3] = aa[3];
					
				}
				if(aa.length==5){
					U.log(addSec);
					if(aa[4].equals(aa[3])){
						address[3] = aa[3];
						address[2] = aa[2];
						address[1] = aa[1];
					}
					else{
						address[3] = aa[4];
						address[2] = aa[3];
						address[1] = aa[1]+", "+aa[2];
					}
								
				}
				
				
			}
			
			if(address[0].length()>0){
				address[0] = address[0].trim();
				if(address[0].endsWith(",")){address[0] = address[0].replaceAll(",$", "");}
			}
		
		
		
		String phone = U.getSectionValue(listingHtml, "Phone</dt> <dd>", "</dd>");
		//U.log("phone : "+ phone);
		if(phone!=null){
			phone =U.formatNumbersAsCode(phone).replaceAll("<br->|<b-r>|<-br>|<br>", "");
			//U.log(phone);
		}
		
		String fax = U.getSectionValue(listingHtml, "<dt>Fax</dt> <dd>", "</dd>");
		//U.log("fax : "+ fax);
		if(fax!=null){
			fax =U.formatNumbersAsCode(fax);
			//U.log(fax);
		}
		
		String email = U.getSectionValue(listingHtml, "col-xs-9 col-sm-7\" href=\"mailto:", "\"");
		//U.log("email : "+ email);
		
		String webSiteUrl = U.getSectionValue(listingHtml, "-web-i -right-gradient col-xs-9 col-sm-7\" href=\"", "\"");
		//U.log("webSiteUrl : "+ webSiteUrl);
		String compType = U.getSectionValue(listingHtml, "<dt>Company Type</dt> <dd> ", "<br> </dd>");
		if(compType!=null)compType = compType.replace("<br>", "-");
		//U.log("compType : "+compType);
		j++;
		String[] output = {j+"",listingUrl,name,compType,tempAddSec,address[0],address[1].trim(),address[2].trim(),address[3].trim()," ",phone,fax,email,webSiteUrl};
		mydata.add(output);
		
		}
		}
		//System.out.println(mydata.size());		
	}
	
	private static String getState(String addSec){
		String val = "";
		for(String st : myStateList){
			if(addSec.toLowerCase().trim().endsWith(st.toLowerCase())){
				//U.log(":::::::Match::::::"+st);
				return val = st.toLowerCase();
			}
		}
		return val;
	}
	
	private static ArrayList<String> myStateList = new ArrayList<String>(){
		{
			//add("Ciudad de Mexico");
			//add("Distrito Federal");
			add("Mexico City");
			add("Jalisco");
			add("Mexico State");
			add("Nuevo Leon");
			add("Aguascalientes");
			add("Baja California");
			add("Baja California Sur");
			add("Campeche");
			add("Coahuila");
			add("Colima");
			add("Chiapas");
			add("Chihuahua");
			add("Durango");
			add("Guanajuato");
			add("Guerrero");
			add("Hidalgo");
			add("Michoacan");
			add("Morelos");
			add("Nayarit");
			add("Oaxaca");
			add("Puebla");
			add("Queretaro");
			add("Quintana Roo");
			add("San Luis Potosi");
			add("Sinaloa");
			add("Sonora");
			add("Tabasco");
			add("Tamaulipas");
			add("Tlaxcala");
			add("Veracruz");
			add("Yucatan");
			add("Zacatecas");
			
		}
	};
}
