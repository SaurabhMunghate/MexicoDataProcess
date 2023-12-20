/**@author Sawan
 * @date 20 July 2019
 */
package com.tequila.extraction.restaurants;

import java.io.IOException;
import java.util.Arrays;

import com.shatam.scrapper.SplitNeighborhoodFromAdd;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class WingStopCom extends DirectoryList{
	private static final String URL = "https://wingstop.com.mx";
	private static final String NAME = "Wing Stop";
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		WingStopCom wsc = new WingStopCom();
		wsc.extractProcess();
		wsc.printAll(NAME+".csv");
	}

	@Override
	protected void extractProcess() throws Exception {
		// TODO Auto-generated method stub
		String sicSub = "5812";
		String html = U.getHTML("http://wingstop.com.mx/sucursales/");
		String urlSection = U.getSectionValue(html, "$('#mapadropdown').change(function(){" ,"</script>");
		String[] regUrls = U.getValues(urlSection, "location.replace(\"", "\");");
		U.log(regUrls.length);
		for(String regUrl : regUrls){
//			U.log(regUrl);
			
			String regHtml = U.getHTML(regUrl);
			String sections[] = U.getValues(regHtml, "<h3 style=", "</iframe>");
			
			String state = regUrl.substring(regUrl.lastIndexOf("com.mx/")+7 ).replace("/", "");
//			U.log(state);
//			U.log(sections.length);
			for(String section : sections){
				addDetails(section, state, sicSub, regUrl);
			}
		}
	}
	
	int i = 0;
	private void addDetails(String section, String _state, String sicSub, String refUrl) throws IOException{
//	if(i ==1)
	{
		U.log("Count =="+i);
		U.log(refUrl);
		U.log(section);
		//Name
		String name = U.getSectionValue(section, "\">", "</h3>");
		U.log("Name ::"+name);
		
		//Phone
		String phone = U.getSectionValue(section, "<br>", "</p>");
		if(phone == null) phone = U.getSectionValue(section, "<br>", "</div>");
		phone = phone.replaceAll("Tels?: ", "").trim().replace(" | ", ";").replace(" / ", ";");
		U.log("Phone :"+phone);
		
		//Address
		String addSec = U.getSectionValue(section, "<p style=", "<br>");
		addSec = addSec.replaceAll("\"font-size: 14px; color: #666\">", "").trim().replaceAll(" +", " ");
		addSec = addSec.trim().replaceAll("\\.$", "");
		U.log("AddSec :"+addSec);
		
		
		String [] add =U.getAddress(addSec);
		
		String street = ALLOW_BLANK, colonia = ALLOW_BLANK, zip = ALLOW_BLANK;
		String city = ALLOW_BLANK, state = ALLOW_BLANK;
		

		zip = Util.match(addSec, "\\d{5}");
		if(zip == null) zip = ALLOW_BLANK;
		if(zip.length() == 5)
			addSec = addSec.replaceAll(zip, "").replaceAll("C\\.?P\\.?", "").trim().replaceAll("^,|,$", "");
		U.log(addSec);
		
		
		String[] v = addSec.split(",");
		U.log("v.len ::"+v.length);
		
		U.log("state :"+_state);
		
/*		if(v.length == 4){
			String x[] = SplitNeighborhoodFromAdd.splitColonia(addSec);

			if(!x[1].isEmpty() && !x[0].isEmpty()){
				street = x[0];
				if(x[1].contains(",")){
					colonia = x[1].split(",")[0];
					if(city == ALLOW_BLANK){
						city = x[1].replace(colonia, "").trim().replaceAll("^,|,$", "");
					}
					if(zip.length() == 5)
						city = city.replaceAll(zip, "").replaceAll("C\\.?P\\.?", "").trim();
				}else
					colonia = x[1];
			}else if(x[1].isEmpty() && !x[0].isEmpty()){
				if(zip.length() == 5)
					x[0] = x[0].replaceAll(zip, "").replaceAll("C\\.?P\\.?", "").trim().replaceAll("^,|,$", "");
				street = x[0];
				U.log(x[0]);
			}
		}*/

		String x[] = SplitNeighborhoodFromAdd.splitColonia(addSec);
		U.log(Arrays.toString(x)+" L :"+x.length);
		if(!x[1].isEmpty() && !x[0].isEmpty()){
			street = x[0];
			if(x[1].contains(",")){
				String val[] = x[1].split(",");
				if(val.length ==3){
					colonia = val[0];
					city = val[1];
					state = val[2];
				}
			}else
				colonia = x[1];
		}else if(x[1].isEmpty() && !x[0].isEmpty()){
			
		}
		
/*		if(v.length == 5){
			
			String x[] = SplitNeighborhoodFromAdd.splitColonia(addSec);
			U.log(Arrays.toString(x)+" L :"+x.length);
			if(!x[1].isEmpty() && !x[0].isEmpty()){
				street = x[0];

				if(x[1].contains(",")){
					String val[] = x[1].split(",");
					if(val.length ==3){
						colonia = val[0];
						city = val[1];
						state = val[2];
					}
//					if(city == ALLOW_BLANK){
//						city = x[1].replace(colonia, "").trim().replaceAll("^,|,$", "");
//					}
				}else
					colonia = x[1];
			}
		}
*/		U.log("*** Street :"+street);
		U.log("*** Col :"+colonia);
		U.log("*** City :"+city+"\n*** State :"+state);
		U.log("*** Zip :"+zip);
		
		
		//Lat-Lng
		String mapUrl = U.getSectionValue(section, "<iframe src=\"", "\"");
		String mapHtml = U.getHTML(mapUrl);
//		U.log(mapHtml);
		String latLngSection = Util.match(mapHtml, "null,null,null,\\[\\[\\[\\d{2,}\\.\\d{3,},\\s?(-\\d+\\.\\d+,\\s?\\d+\\.\\d+)\\]", 1);
		String latLng[] = {ALLOW_BLANK, ALLOW_BLANK};
		U.log(latLngSection);

		if(latLngSection != null){
			latLng[0] = latLngSection.split(",")[1];
			latLng[1] = latLngSection.split(",")[0];
		}
		U.log(Arrays.toString(latLng));
		U.log(Arrays.toString(add));
		if(add[3].isEmpty() || add[3].length() <2) add[3] = _state;
		//add details
		addCompanyDetailsFromMexico(sicSub, name, phone, ALLOW_BLANK, URL);
//		addAddress(street, colonia, city, state, zip);
		addAddress(add[0], add[1], add[2], add[3], add[4]);
		addBoundaries(latLng[0], latLng[1]);
		addReferenceUrl(refUrl);
		addCompanyOtherDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, ALLOW_BLANK);
		
	}
	i++;
	}

}
