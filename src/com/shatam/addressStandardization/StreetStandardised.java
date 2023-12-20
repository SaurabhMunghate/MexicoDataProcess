package com.shatam.addressStandardization;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StreetStandardised {

	private static boolean isDebug = false;

	static Map<String, String[]> rgxMap = new HashMap<String, String[]>();

	static Map<String, String[]> streetAbr = new HashMap<String, String[]>();

	// Cam. Temixco A Emiliano Zapata Km.3
	// Eje 114 No. 330esq. Avenida Control

	static {

		rgxMap.put("^[a-z\\.\\s\\d]+ [No\\.]*[\\s\\d+]+", new String[] {
				" No ", " No. " });

		rgxMap.put("^[Ave\\.?\\s]*[Av\\.\\s]* [a-z\\.\\s]+ [No\\.]*[\\s\\d+]+",
				new String[] { "Av. ", " Avenida ", "Ave\\.? ", " Avenida ",
						" No ", " No. " });

		rgxMap.put("(^Av\\.?).+?", new String[] { "Av\\.? ", " Avenida " });

		rgxMap.put("(^[Avda.\\.]+).+?", new String[] { "Avda. ", " Avenida " });

		rgxMap.put("(^[Blvd\\.]+).+?|^Blv .+?", new String[] { " Blvd ",
				" Boulevard ", "Blv ", " Boulevard " });

		// rgxMap.put("^[C] [a-z\\.\\s]+ [No\\.]*[\\s\\d+]+", new String[] {
		// "C ",
		// " Calle ", " No ", " No. " });

		rgxMap.put("^Div .+?", new String[] { "Div\\.? ",
				" Division "});
		
		rgxMap.put("^Prol\\.+?", new String[] { "Prol\\.? ",
		" Prolongacion "});
		
		rgxMap.put("^Carr\\?.+? |^Carretera\\..+?", new String[] { "Carr\\.? ",
				" Carretera ", "Carretera\\.? ", " Carretera " });

		rgxMap.put("^Calz\\.?.+? |^Clz\\.?.+? ", new String[] { "Calz\\.? ",
				" Calzada ", "Clz\\.? ", " Calzada " });

		rgxMap.put("^Eje\\.? .+?", new String[] { "Eje\\.? ", " Eje " });

		//rgxMap.put("^Fco .+?", new String[] { "Fco ", " Fco\\. " });

		rgxMap.put("Fco[\\.]?", new String[] { "Fco[\\.]? ", " Francisco " });
		
		rgxMap.put("^1o\\. .+?", new String[] { "1o. ", " 1o " });
		rgxMap.put("^Diag(\\.)?.+?",
				new String[] { "Diag\\.? ", " Diagonal ", });
		rgxMap.put("^Ps.+?", new String[] { "Ps\\.? ", " Paseo ", });
		rgxMap.put("^Plza(\\.)? .+?|^Pza(\\.)? .+?", new String[] {
				"Plza\\.? ", " Plaza ", });
		rgxMap.put("^Via(\\.)? .+?", new String[] { "Via\\.? ", " Via ", });

		rgxMap.put("^[a-z]+ [S/N]+", new String[] { " Sn ", " S/N " });

		rgxMap.put(" Int ", new String[] { " Int ", " Int. " });
		
		rgxMap.put(" Cruiz ", new String[] { " Cruiz ", " Cruz " });
		
		rgxMap.put(" Hgo ", new String[] { " Hgo ", " Hidalgo " });
		
		rgxMap.put(" Fdo ", new String[] { " Fernando ", " Hidalgo " });
		
		rgxMap.put(".+?Norte\\. |.+?Nte\\.? ", new String[] { " Norte. ",
				" Norte ", " Nte\\.? ", " Norte " });
		rgxMap.put(".+? Sur. .+?$", new String[] { " Sur. ", " Sur " });
		rgxMap.put(".+? Km .+?$|.+? K\\.M\\. .+?", new String[] { " Km ",
				" Km. ", " K\\.M\\. ", " Km\\. " });
		rgxMap.put(".+? Del. .+?$", new String[] { " Del. ", " Del " });

		rgxMap.put(".+? S/N[,\\.] .+?$|", new String[] {
				" S/N[,\\.] ", " S/N " });

		rgxMap.put(".+? Saint .+?$|.+? San. .+?$", new String[] { " Saint ",
				" San ", " San.", " San " });

		// Prol. 1 Mayo No. 38
		rgxMap.put("\\s?([0-9]{1,2})[\\w]? Mayo,? ", new String[] {
				"\\s?([0-9]{1,2})([\\w])? Mayo,? ", " $1$2 De Mayo ",
				" 1 Mayo, ", " 1 De Mayo " });

		// Cam. Temixco A Emiliano Zapata Km.3
		rgxMap.put(" Km.([0-9]{1,3})", new String[] { " Km.([0-9]{1,3})",
				" Km. $1", });
		
		rgxMap.put(" Kilometro ([0-9]{1,3})", new String[] { " Kilometro ([0-9]{1,3})",
				" Km. $1", });
		
		//Emiliano Zapata No. Kilometro 2 99
	}

	private static Set<String> rgxSet = null;

	public static void main(String args[]) {

		// rgxSet = rgxMap.keySet();

		testAll();
		System.out
				.println(streetStandardised("Calle Miguel Hgo Y Costilla No. 102"));

	}

	private static void testAll() {

		assertTest(streetStandardised("AVe. Morelos No 96"),
				"Avenida Morelos NO. 96");
		assertTest(streetStandardised("Avenida Morelos No 96"),
				"Avenida Morelos No. 96");

		// assertTest(streetStandardised("C Jose Marti 3125"),
		// "Calle Jose Marti 3125");

		assertTest(streetStandardised("Republica De Cuba 301 Int 7-A"),
				"Republica De Cuba 301 Int. 7-A");
		assertTest(streetStandardised("Escobedo Norte. 2635 No 12"),
				"Escobedo Norte 2635 No. 12");

		assertTest(streetStandardised("Av. 1 Mayo, 178 Naucalpan De Juarez"),
				"Avenida 1 De Mayo 178 Naucalpan De Juarez");

		assertTest(streetStandardised("Avda. Adolfo Ruiz Cortinez No. 2027"),
				"Avenida Adolfo Ruiz Cortinez No. 2027");

		assertTest(streetStandardised("Carretera Monterrey Monclova Km 30"),
				"Carretera Monterrey Monclova Km. 30");

		assertTest(
				streetStandardised("Calz. Nacional Km 270 bodega 2 Y 3 No 1"),
				"Calzada Nacional Km. 270 bodega 2 Y 3 No. 1");

		assertTest(streetStandardised("Diag. De Saint Antonio No. 1002"),
				"Diagonal De San Antonio No. 1002");

		assertTest(streetStandardised("Diag De Saint Antonio No. 1002"),
				"Diagonal De San Antonio No. 1002");

		assertTest(streetStandardised("Plza. Principal 4"), "Plaza Principal 4");

		assertTest(streetStandardised("Via. Morelos 382"), "Via Morelos 382");

		assertTest(streetStandardised("Fco. Rojas González K.M. 9 3opiso"),
				"Francisco Rojas González Km. 9 3opiso");

		assertTest(streetStandardised("Carretera. San Luis Km. 19"),
				"Carretera San Luis Km. 19");

		assertTest(streetStandardised("Calz. Cerro Hueco S/N. ,Col."),
				"Calzada Cerro Hueco S/N ,Col.");

		assertTest(streetStandardised("Calz. Cerro Hueco S/N. ,Col."),
				"Calzada Cerro Hueco S/N ,Col.");
		
		// Fco Rojas González K.M. 9 3opiso
	}

	public static void assertTest(String actual, String exptected) {

		if (!actual.equalsIgnoreCase(exptected)) {
			System.out.println(actual + "\t" + exptected + "\tFAILED");
		}

	}

	public static String streetStandardised(String street) {

		if (rgxSet == null) {
			rgxSet = rgxMap.keySet();
		}

		street = street + " ";
		for (String rgx : rgxSet) {
			boolean res = match(rgx, street);
			if (res) {
				res = match(rgx, street);
				street = correctAddreess(rgx, street).trim();
				debug(street + "\t" + rgx);
			}
		}
		return street.trim();
	}

	private static String correctAddreess(String rgx, String str) {
		String rplace[] = rgxMap.get(rgx);

		for (int i = 0; i < rplace.length; i = i + 2) {
			str = Pattern.compile(rplace[i], Pattern.CASE_INSENSITIVE)
					.matcher(str).replaceFirst(rplace[i + 1]);
		}
		return str.trim();
	}

	private static boolean isExist(Set<String> dataset) {
		return true;
	}

	private static boolean match(String rgx, String str) {
		Pattern pat = Pattern.compile(rgx, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pat.matcher(str);
		if (matcher.find()) {
			return true;
		}
		return false;
	}

	private static void debug(Object o) {
		if (isDebug)
			System.out.println(o);
	}

}
