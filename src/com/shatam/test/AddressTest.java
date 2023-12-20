package com.shatam.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.shatam.scrapper.SplitNeighborhoodFromAdd;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class AddressTest {

	public static void main(String[] args) {
		// TODO : To test address file
		addressTest();

		//TODO : To test single address
		test();
	}
	
	public static void test(){
/*		getAddress("3a Calle Sur Poniente S/N, esquina Avenida Central ,Col. Centro, Frontera Comalapa, Chiapas.");
		getAddress("Andador Teopisca No. 101, esquina con Cordoba, Fraccionamiento Fovissste, Tapachula, Chiapas,30779");
		getAddress("1a. Poniente entre 1a. Norte y Avenida Central No. 29 planta alta,Col. Centro, Cintalapa de Figueroa, Chiapas.30400");
		getAddress("Calle 28, numero 113 entre 29A y 29 ,Colonia centro, Ciudad del Carmen, Campeche ,24100");
		getAddress("Avenida 1, número 418 entre calle 4 y 6,Col. Centro,Córdoba, Veracruz,94500.");
		getAddress("Calle 28, numero 113 entre 29A y 29 ,Colonia centro, Ciudad del Carmen, Campeche ,24100");
		

		getAddress("Calle 40 N° 193-b x 37 y 35 ,Col. Centro, sector 6,97780, Valladolid, Yucatán.");
		getAddress("Calle 55 N° 395 x 48 y 50 ,Col. Centro,97700, Tizimin, Yucatan.");
		getAddress("Calle 28 local 194 x 21 y 23 ,Col. Centro,97860, Ticul, Yucatan.");
		
		getAddress("Avenida Chiapas N° 5 ,Col. Ganadera, Emiliano Zapata, Tabasco.");
		getAddress("Av. Gregorio Mendez, esquina con la calle Eduardo R. Bastar, Teapa, Tabasco.");
		getAddress("Avenida Hidalgo N° 128, entre calle 5 de Mayo, Tonalá, Chiapas.");
		getAddress("1a Avenida Poniente Sur N° 9,Col. Centro, Comitán,Chiapas.");
	
		getAddress("Avenida 1a Sur Oriente N° 3,Col. Centro,29939, Yajalón,Chiapas.");
		getAddress("Calle Nicolas Bravo entre Guadalupe Victoria y Benito Barriovero N° 203,Col. Centro,96000, Acayucan Veracruz.");
		getAddress("Av. Central N° 14 Barrio Centro, Venustiano Carranza, Chiapas.");
		
*/
//		getAddress("Avenida López Portillo Num. 868 Entre calles Xuencal y calle Comalcalco,(Super manzana 59, manzana 37, lote 5)77515");
		
/*
		getAddress("Calle Jardin del Centenario 17, Coyoacán, Del Carmen, 04000 Ciudad de México, D.F., Mexico");
		getAddress("Julio Verne 93, Miguel Hidalgo, Polanco Reforma, 11560 Ciudad de México, D.F., Mexico");
		getAddress("Av. Nuevo León 92, Cuauhtémoc, Condesa, 06600 Ciudad de México, D.F., Mexico");
		getAddress("Milan 18, Cuauhtemoc, Juárez, 06600 Ciudad de México, D.F., Mexico");
		getAddress("Av. de los Insurgentes Sur 226, Cuauhtémoc, Roma Nte., 06700 Ciudad de México, D.F., Mexico");
		getAddress("Álv. Obregón 153-A, Col. Roma, 06700, Roma Nte., 06700 Ciudad de México, D.f., Mexico");
		getAddress("Avenida de Las Fuentes 556 Local 8 Planta Baja, Jardines del Pedregal, 01900 Ciudad de México, Mexico");
		getAddress("Emilio Castelar 229 Colonia Chapultepec, Polanco 11560 México D.F., Mexico");

		getAddress("Av. Cuitláhuac 3102, Clavería, Azcapotzalco, Ciudad de México, 02080, Mexico City, Mexico");
		getAddress("Avenida Ejército Nacional 843, Miguel Hidalgo, Antara Fashion Hall, Granada, 11520 Ciudad de México, D.F., Mexico");
		getAddress("Centro Comercial Arcos Av. de los Tamarindos, 90, Priv. 01, Bosques de las Lomas, Bosque de las Lomas, 05120 Ciudad de México, D.F., Mexico");

		getAddress("Petrarca 254, Polanco V Secc, 1156, Ciudad de México, D.F; México");
		getAddress("Colima 166, Colonia Roma, Mexico City, Mexico");
		getAddress("Newton 55, Polanco DF, Mexico City, Mexico");
		getAddress("Ometusco #56, Colonia Condesa, México D.F., Mexico");
		getAddress("Nuevo León 68, Condesa Df, Mexico City, Mexico");

		getAddress("Antwerp 1 corner Paseo de la Reforma., Colonia Juarez, Mexico City, Mexico");
		getAddress("777 Boulevard de la Luz, between Santa Teresa Road and Paseo del Pedregal. Col. Jardines del Pedregal, Mexico City, Mexico");
		getAddress("06140, Av Michoacán 133, Colonia Condesa, Ciudad de México, CDMX, Mexico");
		getAddress("Av. Juarez No. 70, Hilton Mexico, City Reforma, Mexico City, Mexico");
		getAddress("Durango 181 Colonia Roma, 06700 Mexico City, Mexico");
		getAddress("Mariano Escobedo 700 Col. Anzures, 11590 Mexico City, Mexico");
		getAddress("Calle 5 de Mayo #10 Historic Center, Mexico City, Mexico");
*/
		getAddress("Presidente Masaryk 407 Polanco, Miguel Hidalgo, 11550 Mexico City, FD, Mexico City, Mexico");

		

		

	}
	
	public static String[] getAddress(String addSec){
		final String ALLOW_BLANK = "-";
		String[] add = {"","","","",""};
		
		addSec = addSec.trim().replaceAll("\\.$", "").replace(";", ",");
		U.log("Input Test ::"+addSec);
		String extraInfo = Util.match(addSec, "\\(.*?\\)");
		if(extraInfo != null) addSec = addSec.replace(extraInfo, "");
//		U.log(extraInfo);
		//Zip		 
		ArrayList<String> zipList = Util.matchAll(addSec, "\\d{5}",0); //exp. to find postal code in address section
//		U.log(zipList.size());
		if(zipList.size() > 0)
			add[4] = zipList.get(zipList.size()-1); //found zip
		U.log("Found zip ::"+add[4]);
		
		
		//State 
		if(!add[4].isEmpty()){ //if postal code is found
			
			if(addSec.endsWith(add[4])){
				int index = addSec.lastIndexOf(",", addSec.indexOf(add[4])-4);
				if(index == -1)	index = addSec.lastIndexOf(",", addSec.indexOf(add[4]));
//				U.log(index);
				String val = addSec.substring(index);
				U.log("state :"+val);
				if(val != null)val = val.replaceAll(add[4]+"|,", "").trim();
				if(val.length()>4){
//					add[3] = U.matchState(val);
					if(!U.matchState(val).equals(ALLOW_BLANK)) add[3] = val; //found state
				}
				if(add[3].equals("-")) add[3] = "";
			}else{
	//			U.log(addSec);
				String val = addSec.substring(addSec.indexOf(add[4])+add[4].length()); //remaining section onward postal code
				val = val.trim().replaceAll("^,|,$", "");
				String v[] = val.split(",");
				
				if(v.length >1){
					if(v[v.length-1].trim().equalsIgnoreCase("Mexico") || v[v.length-1].trim().equalsIgnoreCase("México")){ //country
//						U.log("0000::"+U.matchState(v[v.length-2]));
						if(!U.matchState(v[v.length-2]).equals(ALLOW_BLANK)){
							add[3] = v[v.length-2]; //found state
						}
					}
					if(v.length > 2 && add[3].length() > 1)
						add[2] = v[v.length-3]; //found city
				}	
				
				
/*				if(v.length == 3){ //if val size is 3 means, section have both city and state and country
					if(v[v.length-1].trim().equalsIgnoreCase("Mexico") || v[v.length-1].trim().equalsIgnoreCase("México")){ //country
//						U.log("0000::"+U.matchState(v[1]));
						if(!U.matchState(v[1]).equals(ALLOW_BLANK)){
							add[3] = v[1]; //found state
							add[2] = v[0]; //found city
						}
					}
				}
				if(v.length == 4){
					if(v[v.length-1].trim().equalsIgnoreCase("Mexico") || v[v.length-1].trim().equalsIgnoreCase("México")){ //country
//						U.log("0000::"+U.matchState(v[1]));
						if(!U.matchState(v[2]).equals(ALLOW_BLANK)){
							add[3] = v[2]; //found state
							add[2] = v[1]; //found city
						}
					}
				}
				if(v.length == 2){
					if(v[v.length-1].trim().equalsIgnoreCase("Mexico") || v[v.length-1].trim().equalsIgnoreCase("México")){ //country
//						U.log(v[0]+"::0000::"+U.matchState(v[0]));
						if(!U.matchState(v[0]).equals(ALLOW_BLANK)){
							add[3] = v[0]; //found state
						}
					}
				}
*/				
				U.log(addSec);
				U.log(addSec.indexOf(add[4]));
				if(add[3].length() > 1){ // && add[2].length() > 1){
					if(addSec.indexOf(add[4]) > 1)
						addSec = addSec.replace(addSec.substring(addSec.indexOf(add[4])), "").trim().replaceAll(",$", "");  //replace zip, if it is present in between address
					
					if(addSec.indexOf(add[4]) == 0){
						addSec = addSec.trim().replaceAll("^"+add[4], "");  //replace zip, if it is present at beginning of address
						addSec = addSec.trim().replaceAll("^,", "").trim();
						
						if(v[v.length-1].trim().equalsIgnoreCase("Mexico") || v[v.length-1].trim().equalsIgnoreCase("México")){
							addSec = addSec.replaceAll(v[v.length-1]+"$", "").trim();
							for(int i = v.length-1; i >= 0 && i >= v.length-3; i--){
								addSec = addSec.replaceAll(v[i]+"$", "").trim();
								addSec = addSec.replaceAll(",$", "").trim();
							}
						}
					}
					if(v.length == 4) addSec = addSec.trim()+", "+v[0].trim();
				}
				U.log("........"+val);
				U.log("... addSec ..."+addSec);
			}
		}
/*		else{
			//If zip is not found
			U.log("*** Zip is not found");
		}
*/		
		//Address, Neighborhood, city
		String[] vals = SplitNeighborhoodFromAdd.splitColonia(addSec);
		for(String val : vals)U.log("** "+val);
		
		if(vals.length == 2 && add[2].isEmpty()){
			if(!add[3].isEmpty()) vals[1] = vals[1].replaceAll(add[3], "");
			if(!add[4].isEmpty()) vals[1] = vals[1].replaceAll(add[4], "");
			add[0] = vals[0];
			String val = addSec;
			U.log(addSec);
			
			U.log(Arrays.toString(add));
			if(!add[0].isEmpty())val = val.replace(add[0], "");
			if(!add[4].isEmpty())val = val.replace(add[4], "");
			if(!add[3].isEmpty())val = val.replace(add[3], "");
			U.log("val ::"+val);
			if(!val.isEmpty()){
				val = val.trim().replaceAll("^,|\\.$|, ,|,$", "").trim().replaceAll(",$", "").replace(",,", ",");
				U.log(">> val ::"+val);
				if(val.contains(",")){
					String v[] = val.split(",");
					//
					if(v.length == 2){
						add[1] = v[0].trim(); //found neighborhood
						add[2] = v[1].trim(); //Found city
					}
					if(v.length == 3){
						if(v[2].length()>4){
//							add[3] = U.matchState(v[2].trim());
							if(!U.matchState(v[2].trim()).equals(ALLOW_BLANK)) add[3] = v[2].trim();  //found state
						}
						add[1] = v[0].trim(); //found neighborhood
						add[2] = v[1].trim(); //Found city
					}
					if(v.length == 4){
						if(v[3].length()>4){
//							add[3] = U.matchState(v[3].trim());
							if(!U.matchState(v[3].trim()).equals(ALLOW_BLANK)) add[3] = v[3].trim();  //found state
						}
						add[1] = v[0].trim()+ ", " + v[1].trim(); //found neighborhood
						add[2] = v[2].trim(); //Found city
					}
				}

			}else{
				//when address section and street both are same
				if(addSec.trim().equals(add[0].trim())){
					//TODO :
					U.log("Found");
					String v[] = addSec.split(",");
					
					if(add[3].length() < 2){
						if(v[v.length-1].trim().equalsIgnoreCase("Mexico") || v[v.length-1].trim().equalsIgnoreCase("México")){ //country
//							U.log(v[v.length-2]+"::0000::"+U.matchState(v[v.length-2]));
							if(!U.matchState(v[v.length-2]).equals(ALLOW_BLANK)){
								add[3] = v[v.length-2]; //found state
							}
						}
						if(add[3].length() > 1){
							add[0] = add[0].trim().replaceAll(v[v.length-1]+"$", "");
							add[0] = add[0].trim().replaceAll(",$", "").replaceAll(v[v.length-2]+"$", "");
							add[0] = add[0].trim().replaceAll(",$", "");
						}
						U.log("****"+add[0]);
						v = add[0].split(",");
//						addSec = add[0];
					}
					
					if(add[2].length() < 2 && add[3].length() > 1){
						
						if(v.length > 1){
							add[2] = v[v.length-1].trim(); //Found city
						}
						if(v.length > 2){
							add[1] = v[v.length-2].trim(); //found neighborhood
						}
					}

					U.log("City-------"+add[2]);
					U.log("State------"+add[3]);
					add[0] = add[0].trim().replaceAll(add[2]+"$", "");
					add[0] = add[0].trim().replaceAll(",$", "").replaceAll(add[1]+"$", "");
				}
			}
			
			if(add[1].length() < 2 && !vals[1].trim().isEmpty()) add[1] = vals[1]; //found neighborhood
		}//eof if
		else if(vals.length == 2 && add[2].length() > 1){
			if(add[3].length() > 1 && add[4].length() > 1){
				if(vals[1].isEmpty()){ //Check if neighborhood is empty
					String[] v = addSec.split(",");
					U.log(v.length);
					if(v.length >1){
						add[1] = v[v.length-1].trim(); //found neighborhood
						String street = "";
						for(int i = 0; i < v.length-1; i++){
							street = street+ v[i].trim()+ ", ";
						}
						add[0] = street;
					}
					/*if(v.length == 3){
						add[1] = v[v.length-1].trim(); //found neighborhood
						add[0] = v[0].trim()+ ", " + v[1].trim(); //found street
					}
					if(v.length == 4){
						add[1] = v[v.length-1].trim(); //found neighborhood
						add[0] = v[0].trim()+ ", " + v[1].trim()+", " + v[2].trim(); //found street
					}*/
				}else{
					add[1] = vals[1]; //found neighborhood
					add[0] = vals[0]; //found street
				}
			}
		}
		
		//if zip & colonia are not find at above algo.
		if(add[2].isEmpty() && add[3].isEmpty()){
			String v[] = null;
			if(!add[0].isEmpty()){
				v = add[0].split(",");
				if(v.length == 3){
					if(v[v.length-1].length() > 4){
//						add[3] = U.matchState(v[v.length-1].trim());
						if(!U.matchState(v[v.length-1].trim()).equals(ALLOW_BLANK)) add[3] = v[v.length-1].trim();  //found state
					}
					add[2] = v[1]; //found city
					add[0] = v[0]; //found street
				}

				if(v.length > 3){
					if(v[v.length-1].length() > 4){
//						add[3] = U.matchState(v[v.length-1].trim());
						if(!U.matchState(v[v.length-1].trim()).equals(ALLOW_BLANK)) add[3] = v[v.length-1].trim();  //found state
					}

					if(add[3].equals("-")){
						if(v[v.length-2].length() > 4){
//							add[3] = U.matchState(v[v.length-2].trim());
							if(!U.matchState(v[v.length-2].trim()).equals(ALLOW_BLANK)) add[3] = v[v.length-2].trim();  //found state
						}
						add[2] = v[v.length-1].trim(); //found city
					}else{
						add[2] = v[v.length-2].trim(); //found city
					}
				}
				if(!add[2].isEmpty())add[0] = add[0].replace(add[2], "");
				if(!add[3].isEmpty())add[0] = add[0].replace(add[3], "");
			}
		}
		if(add[3].equals("-")) add[3] = "";
		add[0] = add[0].replace(" ,", ",").replace(",", ", ").replaceAll("\\s{2,}", " ").trim().replace(", ,", ",").replaceAll(",$", "").trim();
		if(extraInfo != null) add[0] += " "+extraInfo; 
		
		add[1] = add[1].trim();
		add[2] = add[2].trim();

		if(add[1].matches("\\d+")){
			add[0] += ", "+add[1];
			add[1] = "";
		}
		if(add[2].matches("\\d+")) add[2] = "";

		add[3] = add[3].trim();
		add[4] = add[4].trim();

		U.log("**************** Result ******************");
		U.log("Street : "+add[0]);
		U.log("Colonia : "+add[1]);
		U.log("City : "+add[2]);
		U.log("State : "+add[3]);
		U.log("Zip : "+add[4]);
		U.log("**********************************");
//		U.log(Arrays.toString(U.getAddressGoogle(addSec)));
		return add;
	}
	/**
	 * This method is used to test the address files for checking behavior of {@code getAddress()}.
	 */
	public static void addressTest(){
		StringBuffer sb = new StringBuffer();
		List<String[]> readLines = U.readCsvFile("/home/glady/MexicoCache/test/AddressTest.csv");
		for(String[] line : readLines){
			sb.append(assertAddressTest(line[0], line[1], line[2], line[3], line[4], line[5]));
		}
		System.err.println(sb.toString());
	}
	/**
	 * This method is used to test if {@code getAddress()} have its behavior working properly or not.
	 * @param input
	 * @param expectedStreet
	 * @param expectedNeighborhood
	 * @param expectedCity
	 * @param expectedState
	 * @param expectedZip
	 * @return Message for behavior of this method. If the message is empty, means test case has done successfully.
	 */
	public static String assertAddressTest(String input, String expectedStreet, String expectedNeighborhood, 
			String expectedCity, String expectedState, String expectedZip){
		
		String msg = "";
		String add[] = getAddress(input);
		if(expectedStreet.equalsIgnoreCase(add[0]) && expectedNeighborhood.equalsIgnoreCase(add[1]) &&
				expectedCity.equalsIgnoreCase(add[2]) && expectedState.equalsIgnoreCase(add[3]) &&
				(expectedZip.equalsIgnoreCase(add[4]) || add[4].contains(expectedZip)) ){
//			msg = "";
		}else{
			msg ="****** Test Failed ******:[Input] :"+input+"\n";
//			System.err.print("Test Failed :");
			if(!expectedStreet.equalsIgnoreCase(add[0])){
//				System.err.print("[Street(E :"+expectedStreet+",R :"+add[0]+")] ");
				msg += "[Street(E :"+expectedStreet+",R :"+add[0]+")]\n";
			}
			if(!expectedNeighborhood.equalsIgnoreCase(add[1])){
//				System.err.print("[Neighborhood(E :"+expectedNeighborhood+",R :"+add[1]+")] ");	
				msg += "[Neighborhood(E :"+expectedNeighborhood+",R :"+add[1]+")]\n";
			}
			if(!expectedCity.equalsIgnoreCase(add[2])){
//				System.err.print("[City(E :"+expectedCity+",R :"+add[2]+")] ");
				msg += "[City(E :"+expectedCity+",R :"+add[2]+")]\n";
			}
			if(!expectedState.equalsIgnoreCase(add[3])){
//				System.err.print("[State(E :"+expectedState+",R :"+add[3]+")] ");
				msg += "[State(E :"+expectedState+",R :"+add[3]+")]\n";
			}
			if(!expectedZip.equalsIgnoreCase(add[4])){
//				System.err.println("[Zip(E :"+expectedZip+",R :"+add[4]+")] ");
				if(!add[4].contains(expectedZip))
					msg += "[Zip(E :"+expectedZip+",R :"+add[4]+")]\n";
			}
			msg +="\n";
		}
		return msg;
	}
}
