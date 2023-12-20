/**
 * @author Sawan
 */
package com.shatam.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public interface TextFormat {

	public static final String COUNTRY = "Mexico";
	public static final String  ALLOW_BLANK = "-";
	public static final String  BLANK = "";

	public static String getCompanyNameStandardised(String companyName){
		companyName = U.toTitleCase(companyName.trim());
		//U.log(companyName);
		companyName = companyName.replace(" .", ".")
				.replaceAll(" Cvv$", " Cv")
				.replaceAll(" J\\.R\\.S\\.A\\. De C\\s?\\.V\\.", " Jr., S.A. De C.V.")
				.replaceAll(" S\\s?\\.?\\s?(A|a)\\.? De C\\s?\\.?(v|V)?\\.?", ", S.A. De C.V.")
				.replaceAll(" S\\.?\\s?De R\\.L\\. De C\\.V\\.| S De Rl De Cv", ", S. De R.L. De C.V.")
				.replace(" S. De R.L.", ", S. De R.L.")
				.replace(" Snc De Cv", ", S.N.C. De C.V.")
				.replaceAll(" S\\.P\\.R\\.? De R\\.I\\.| Spr De Ri$", ", S.P.R. De R.I.")
				.replaceAll(" S\\.P\\.R\\.? De R\\.L\\.| Sp(r|\\sR) De Rl$", ", S.P.R. De R.L.")
				.replace(" S.P.R. De R.L. DE C.V.", ", S.P.R. De R.L. De C.V.")
				.replaceAll(" S\\.?(A|a)\\.?(P|p)\\.?(I|i)\\.? De C\\.?V\\.?", ", S.A.P.I. De C.V.")
				.replace(" S. En N.C.", ", S. En N.C.")
				.replace(" S. En C.S.", ", S. En C.S.")
				.replace(" S. En C. Por A.", ", S. En C. Por A.")
				.replaceAll(" S\\.?(C|c)\\.? De R\\.?(L|l)\\.?$", ", S.C. De R.L.")
				.replaceAll(" S\\.?(O|o)\\.?(F|f)\\.?(O|o)\\.?(M|m)\\.? E\\.N\\.R\\.", ", S.O.F.O.M. E.N.R.")
				.replaceAll(" S\\.?(O|o)\\.?(F|f)\\.?(O|o)\\.?(M|m)\\.?,? E\\.R\\.", ", S.O.F.O.M. E.R.")
				.replaceAll(" S\\.?(A|a)\\.?(B|b)\\.?\\s?\\s?De C\\.?\\s?(V|v)\\.?\\s?$", ", S.A.B. De C.V.")
				.replaceAll(" S De Rl Mi| S\\. De R\\.?(l|L)\\.?\\s?(m|M)\\.?(i|I)\\.", ", S. De R.L. M.I.")
				.replaceAll(" S\\.\\s?A\\.\\s?B\\.\\s?$", ", S.A.B.")
				.replaceAll(" S\\.\\s?A\\.$| Sa$", ", S.A.")
				.replaceAll(" S\\.?\\s?(C|c)\\.?$", ", S.C.")
				.replaceAll(" A\\.\\s?C\\.$", ", A.C.")
				.replaceAll(", Llc$", ", LLC")
				.replace(",,", ",").replace(" ,", ",").replace(",", ", ").replace(", ,", ",").replaceAll("\\s{2,}", " ")
				.replace("´", "'").replace(" - ", "-").trim();

		return companyName;
	}
	
	public static String getAddressStandardised(String address){
		
		address = U.toTitleCase(address.trim());
		address = address.trim()
				.replaceAll("\\s{2,}", " ").replaceAll(":|\\.$|,$|^,|^ |-$|^ |^ ", "").replace(", ,", ",").replaceAll(" – | - ", "-").replace(" / ", "/").replace("# ", "#")
				.replaceAll("Boulevard |Blvd\\.? |^Blv\\.? |^Bvld\\. |Boulevar |Bldv\\. |Blvrd\\.? |Boulebard |Boulevar(d)? |Blvr |Bolevard ", "Boulevard ")
				.replaceAll("Avenida |Ave(\\.|,)? |^Av |^Ave |^Avda | avenida |^Av, ", "Avenida ").replaceAll(",\\s?Av ", ", Avenida ")
				.replaceAll("Privada |Priv(\\.|,)? |^Priv | privada |^Priv, ", "Privada ")
				.replaceAll("Esquina |Esq |Esq\\.", "Esquina ").replace(" Esq", ", Esq" )
				//.replaceAll(" No | No\\.", " No. ")
				.replaceAll(" Sn | Sn$| Sin$| Sin N(ú|u)mero", " S/N")
				.replaceAll(" Carr |(c|C)arr(e)?\\. |Carret\\. |^Carr |^Carr(,)? | Ctra\\. ", " Carretera ").replace("Carr. ", "Carretera ").replace("Carretera.","Carretera")
				.replace(" Fed. ", " Federal ")
				.replaceAll("Clz\\. |Calz\\.? | Claz\\. |^Calz |^calz\\. |Clzd\\. ", "Calzada ")
				.replaceAll(" Esc\\.? ", " Escuela ")
				.replaceAll(" Pza\\.? ", ", Plaza ")
				.replace(" C/Le ", " Calle ").replace("Cll. ", "Calle ")
				.replaceAll("C/ | C/", "Calle") //|^C. | C. 
				.replaceAll(" Km | Km\\. | Km\\.|^Km(\\.)? | Kilometer | Kilómetro ", " Km. ")
				.replace("..", ".")
				.replaceAll("Cto\\. ", "Circuito ")
				.replaceAll("Prol\\.? |^Pro\\. |Prolong\\. |Prol ", "Prolongación ")
				.replace("Perif. ", "Periferico ")
				.replaceAll(" Lt(\\.)?| Lte\\.? | Lt\\.?| Lt-| Lot\\. ", " Lote ") //Lote
				.replace(" Lote ", ", Lote ")
				.replaceAll(" Mz\\.| Mz | Mza | Mzn\\.? | Maz | Mz-", " Manzana ") //Manzana
				.replace(" Manzana ", ", Manzana ")
//				.replaceAll(" Amp\\. |^Amp\\. ", " Ampliación ")
				.replace(" Int |Int\\.", " Int. ")
				.replaceAll("Loc\\.| Loc ", " Local ")
				.replace("Locales ", "Local ")
				.replaceAll(" Local |,Local|Loc\\.| Loc ", ", Local ")
				.replace("Av.", "Avenida ")
				.replaceAll(" Num | Num\\. | No\\.", " No. ")
				.replace(",,", ",")
				.replace(" Y, | y, ", " Y ")
				.replaceAll("^Ctro\\. ", "Centro ").replace(" ,", ",")
				.replaceAll(" Smza | Sm | S\\.M\\. | Supermza | sm | Súper Manzana | Smz ", " Supermanzana ")
				.replace(" Nal. ", " Nacional ")
//				.replaceAll("Ote(\\.)?", "Oriente").replaceAll("Nte\\.?", "Norte").replaceAll("Pte\\.?", "Poniente")
				.replaceAll("^C\\.(c|C)\\. ", "Centro Comercial ")
				.replaceAll("^Licenciado | Licenciado ", " Lic. ")
				.replaceAll(" (p|P)te\\.?$| (p|P)te\\.? ", " Poniente ").replaceAll(" (p|P)te\\.?, ", " Poniente, ")
				.replaceAll(" (o|O)te\\.?$| (o|O)te\\.? ", " Oriente ").replaceAll(" (o|O)te\\.?, ", " Oriente, ")
				.replaceAll(" (n|N)te\\.?$| (n|N)te\\.? ", " Norte ").replaceAll(" (n|N)te\\.?, ", " Norte, ")
				.replaceAll(" (s|S)ur\\.?$| (s|S)ur\\.? ", " Sur ").replaceAll(" (s|S)ur\\.?, ", " Sur, ")
				.replace(" Pdte. ", " Presidente ")
				.replace(" Aut. ", " Autopista ")
				.replace(" Entre ", ", Entre ")
//				.replace(" NoS/N", " No S/N")
				.replace("–", "-")
				.replaceAll("\\s{2,}", " ").replace(" ,", ",").replace(" # ", " #").replace("( ", "(").replace(" )", ")")
				.replace(", No. ", " No. ").replace(",,", ",").replace(" - ", "-").replace(" -", "-")
				.trim()
				.replaceAll("  *", "").replaceAll("\\.$| $", "");// address
		
		String str = Util.match(address, " No \\d+");
		if(str != null)
			address = address.replace(str, str.replace("No ", "No. "));
		
		return address;
	}
	
	/**
	 * This method is used to remove duplicate phone/fax numbers. 
	 * @param number is phone/fax number
	 * @return unique phone/fax number
	 */
	public static String getUniqueNumber(String number){
		number = U.formatNumbersAsCode(number);
		number = number.replace("-", "").replaceAll("\\s", "").replace("Ext.", " Ext. ").replaceAll("\\s{1,}", " ");
		String[] vals = null;
		if(number.contains(";"))vals = number.split(";");
		else if(number.contains(","))vals = number.split(",");
		
		if(vals != null){
			for(int i= 0; i < vals.length-1; i++){
				for(int j = i+1; j < vals.length; j++){
					if(vals[i].equals(vals[j])){
						vals[i] = "";
					}else if(vals[i].contains(vals[j])){
						if(vals[i].length() < vals[j].length()){
							vals[i] = "";
						}else{
							vals[j] = "";
						}			
					}else if(vals[j].contains(vals[i])){
						if(vals[j].length() < vals[i].length()){
							vals[j] = "";
						}else{
							vals[i] = "";
						}
					}//eof else if
				}//eof for j
			}//eof for i
			
			number = String.join(";", vals);
			number = number.trim().replaceAll("^;|;$", "").replace(";;", ";");
		}//eof if
		return U.formatNumbersAsCode(number);
	}
	
	public static String getUniqueEmail(String str){
		if(str == null)return str;
		if(str.trim().isEmpty()) return null;
		if(!str.contains(";"))return str;
		Set<String> uniqueEmailSet = new HashSet<>();
		
		String[] emails = str.split(";");
		if(emails.length > 1){
			for(String email : emails)
				uniqueEmailSet.add(email.toLowerCase());
		}
		if(uniqueEmailSet.size() > 0){
			str = "";
			for(String email : uniqueEmailSet){
				str = str + email+";";
			}
			str = str.replaceAll(";$", "");
		}			
		return str;
	}
	
    public static String correctPhoneNum(String str){

    	if(str.endsWith("0000")){	
//	        String phoneNum = null,ext = null;
	        str = str.replace("-", "");
/*	        phoneNum = str.substring(0, str.indexOf("000"));
	        ext = str.substring(str.indexOf("000"));
	        System.out.println("Correct Num ::: "+phoneNum+" Ext. "+ext.length());
*/	        
	        return str.substring(0, str.indexOf("000"))+ " Ext. "+ str.substring(str.indexOf("000")).length();	        
    	}
    	if(str.contains("Y") || str.contains("and")){
    		return str.replace("Y", ";");
    	}    	
    	return str;
    }
}
