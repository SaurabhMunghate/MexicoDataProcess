/**
 * @author Sawan
 * @date 23 July 2019
 */
package com.tequila.extraction.restaurants;

import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;

public class ItaliannisCom extends DirectoryList{
	private static final String URL = "http://italiannis.com.mx";
	private static final String NAME = "Italianni's";

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		ItaliannisCom ic = new ItaliannisCom();
		ic.extractProcess();
		ic.printAll(NAME+".csv");
	}

	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub
		String sicSub = "5812";
		String url = "http://italiannis.com.mx/ubicaciones/";
		
		String  html = U.getHTML(url);
//		html = U.removeComments(html);
		String sections[] = U.getValues(html, "class=\"ubicacion-direccion\"", "<!-- /UBICACIÓN -->");
		U.log(sections.length);
		for(String sec : sections){
			sec = U.removeComments(sec);
			addDetails(sec, sicSub, url);
		}
	}

	int i = 0;
	private void addDetails(String section, String sicSub, String refUrl) {
		// TODO Auto-generated method stub
//	if(i ==22)
	{
		U.log("Count ="+i);
		
		if(!section.contains("<h2>") || !section.contains("<h1>")) return;
		U.log(section);
		String addSec = U.getSectionValue(section, "<p>", "<br>").trim();
		addSec = addSec.replace("Ylang Ylang Boca del Rio Veracruz", "Ylang Ylang, Boca del Rio, Veracruz");
		U.log("addSec =="+addSec);
		
		String[] add = U.getAddress(addSec);
		
		//Name
		String name = U.getSectionValue(section, "<h2>", "</h2>").trim();
		U.log(name);
		
		//Phone
		String phone = U.getSectionValue(section, "<strong>", "</strong>");
//		U.log(phone);
		if(phone == null) phone =  U.getSectionValue(section, "Teléfono:", "</");
		phone = phone.replaceAll("Teléfono:", "").trim();
		U.log(phone);
		
		//Add details
		addCompanyDetailsFromMexico(sicSub, NAME+" "+name, phone, ALLOW_BLANK, URL);
//		addAddress(street, colonia, city, state, zip);
		addAddress(add[0], add[1], add[2], add[3], add[4]);
		addBoundaries(ALLOW_BLANK, ALLOW_BLANK);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);

	}
	i++;
	}

}
