package com.shatam.MexicoExtraction_1_15;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.bcel.generic.PUTFIELD;

import com.gargoylesoftware.htmlunit.javascript.host.Console;
import com.gargoylesoftware.htmlunit.javascript.host.Set;
import com.google.common.io.Files;
import com.opencsv.CSVWriter;
import com.shatam.utils.ApiKey;
import com.shatam.utils.DirectoryList;
import com.shatam.utils.FileUtil;
import com.shatam.utils.MXStates;
import com.shatam.utils.U;
import com.shatam.utils.Path;

public class FormatInegi extends DirectoryList {
//29may 2023 ; 10.00am
//	static String start_index="245000";	
	static String start_index = "0";
//	static String end_index="254999";
//	static String end_index="3216";
	static String end_index = "524718";// "123185";
//	static String end_index="26700";
	static String sicSub = "8221";
//	static String sicSub="5812";
//	static String fileName="InegiCSV/Restaurants/Inegi_Restaurant_"+start_index+"_"+end_index+".csv";
//	static String fileName="InegiCSV/Inegi_Information_"+start_index+"_"+end_index+".csv";
	static String fileName = "Inegi_Information_" + start_index + "_" + end_index + ".csv";
//	static String fileName="Inegi_Information_0_10000.csv";

//	static String fileName = "/home/shatam-10/MexicoCache/extracted_files/Inegi_Information_"+start_index+"_"+end_index+".csv";
//	static String fileName = "/home/shatam-10/MexicoCache/extracted_files/Inegi_Information.csv";

	public static void main(String[] args) {
		FormatInegi ext = new FormatInegi();

		try {
			String filePath = Path.CACHE_PATH_FOR_EXTRACTION + fileName;
//			List<String[]> data=U.readCsvFile(filePath);

			ext.extractProcess();
			ext.printAll(fileName);

//			ext.addExtraData();
//			ext.checkeSicCodes();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void checkeSicCodes() {
		try {/// home/chinmay/MexicoCache/denue_00_51_0419_csv/conjunto_de_datos/denue_00_51_1118_csv/conjunto_de_datos/denue_inegi_51_.csv
			HashMap<String, String> sicCodeSet = new HashMap<String, String>();

//			String filePath = "/home/shatam-100/Desktop/MexicoInegiData/denue_00_31-33_0522_csv/conjunto_de_datos/denue_inegi_31-33_.csv";
			String filePath = "/home/shatam-100/Desktop/MexicoInegiData/denue_62_25022015_csv_denue_inegi_02_From_0_to_10000.csv";
			String filename = "/home/shatam-100/Cache/textFile.txt";
			List<String> array = new ArrayList<>();

			List<String[]> data = U.readCsvFile(filePath);
			for (String[] da : data) {
//				Thread.sleep(100);
//				U.log(Arrays.toString(da));
				if (!sicMap.containsKey(da[4].trim())) {
//					sicCodeSet.put("Key:" + da[4],"   Val:" + da[3]);
//					U.log("Val:"+da[3]+"Key:"+da[4]);
					U.log("da[4] - " + da[4]);
//					String value = "Key:"+da[4]+"    Val:"+da[3];
//					array.add(value+"");
				} else {
//					U.log("da[4] - "+da[4]);
				}

//					sicCodeSet.put(da[4],da[5]);

//				sicCodeSet.put("Val:"+da[3],"Key:"+da[4]);
			}
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
//	            for (int i = 0; i < array.size(); i++) {
//	                writer.write(array.get(i).toString());
//	                writer.newLine();
				writer.write(sicCodeSet.toString());
//	            }
			} catch (IOException e) {
				System.err.format("IOException: %s%n", e);
			}
			U.log(sicCodeSet.size());
			U.log(sicCodeSet.toString());
//			U.log(sicCodeSet);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	private void addExtraData() {
		String filePath = Path.CACHE_PATH_FOR_EXTRACTION + fileName;
		List<String[]> data = U.readCsvFile(filePath);
		try {
			StringWriter sw = new StringWriter();
			CSVWriter writer = new CSVWriter(sw);
			for (String[] inputArr : data) {
				String outputArrp[] = new String[inputArr.length + 6];
				if (inputArr[0].contains("ID")) {
					System.arraycopy(inputArr, 0, outputArrp, 0, inputArr.length);
					outputArrp[inputArr.length] = "HERE_ADDRESS_FORMATED";
					outputArrp[inputArr.length + 1] = "HERE_ADDRESS";
					outputArrp[inputArr.length + 2] = "HERE_COLONIA";
					outputArrp[inputArr.length + 3] = "HERE_CITY";
					outputArrp[inputArr.length + 4] = "HERE_STATE";
					outputArrp[inputArr.length + 5] = "HERE_POSTAL";
					writer.writeNext(outputArrp);
					continue;
				}

				System.arraycopy(inputArr, 0, outputArrp, 0, inputArr.length);
				String add1[] = getAddressFromLatlonHereApi(
						new String[] { inputArr[inputArr.length - 4], inputArr[inputArr.length - 3] });

				String add = getAddressFromLAtlonHereApi(
						new String[] { inputArr[inputArr.length - 4], inputArr[inputArr.length - 3] });
				outputArrp[inputArr.length] = add;
				System.arraycopy(add1, 0, outputArrp, inputArr.length + 1, add1.length);
				writer.writeNext(outputArrp);
				U.log(Arrays.toString(outputArrp));
			}
			FileUtil.writeAllText(filePath, sw.toString());
			writer.close();
			sw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void extractProcess() throws Exception {

//		String filePath="/home/shatam-100/Desktop/MaxicoInegiData/denue_00_21_csv/conjunto_de_datos/denue_inegi_21_.csv";
//		String filePath = "/home/shatam-100/Desktop/MexicoInegiData/denue_62_25022015_csv (1)/denue_02_csv/conjunto_de_datos/denue_inegi_02_.csv";
		String filePath = "/home/shatam-100/Desktop/MexicoCacheUniqueRecord/denue_inegi_31-33_Manufacture_524718.csv";

//		String filePath="/home/shatam-10/MexicoCache/Cache/denue_inegi_61_10_january.csv";
//		String filePath="/home/chinmay/MexicoCache/InegiCache/HotelsAndRest.csv";
		String companyDataUrl = "https://www.inegi.org.mx/app/descarga/?ti=6";

		int count = 0;
//		U.log("count :::::::::: "+count);
		List<String[]> data = U.readCsvFile(filePath);

		for (String[] nextLine : data) {

//			U.log("nextLine:::" + Arrays.toString(nextLine));
			if (nextLine[0].contains("id"))
				continue;
			count++;

			if (count < Integer.parseInt(start_index) || count > Integer.parseInt(end_index))
				continue;
//			String companyName=nextLine[1];

			String companyName = nextLine[2].replace("M�XICO", "MEXICO");
//			U.log(companyName);

//			String originalString = "Captación, tratamiento y suministro de agua realizados por el sector público";
//			companyName = new String(companyName.getBytes("ISO-8859-1"), "UTF-8");
//			System.out.println(companyName);
//companyName == OFICINA DE ADMINISTRACI�N DEL SIDEAPAS ANTONIO AMARO
// Did you mean: OFICINA DE ADMINISTRACIÓN DEL SIDEAPAS ANTONIO AMARO
//companyName == OFICINA DE ADMINISTRACI?N DEL SIDEAPAS ANTONIO AMARO

//			String text = "Captación, tratamiento y suministro de agua realizados por el sector público";
//			byte[] utf8Bytes = companyName.getBytes("Windows-1252");
//			 companyName = new String(utf8Bytes, "Windows-1252");
//			System.out.println(companyName);

//			String text = "OFICINA DE ADMINISTRACIÓN DEL SIDEAPAS ANTONIO AMARO";
//			byte[] utf8Bytes = companyName.getBytes("UTF-8");
//			String correctValue = new String(utf8Bytes, "UTF-8");
//			System.out.println("correctValue:::"+correctValue);
			// ================
//			HashSet<Character> validChars = new HashSet<>();
//	        for (char c = 'a'; c <= 'z'; c++) {
//	            validChars.add(c);
//	        }
//	        for (char c = 'A'; c <= 'Z'; c++) {
//	            validChars.add(c);
//	        }
//	        String word = "H3llo W0rld!";
//	        System.out.println("Original word: " + word);
//	        String str = "H?llo?W?rld?";
//	        word = word.replaceAll("\\?+", "");
//	        System.out.println(word);
//	        System.out.println("Corrected word: " + correctWord(word, validChars));
			// =============
//			Copy code
//			str = "LICENCIADO BENITO JU�REZ"
//			companyName = companyName.replace("�", "\u00e9");
//			print(str)
//			U.log("companyName == " + companyName);

//			String companyName=nextLine[1];
//			if(!companyName.toLowerCase().contains("constructora inmobiliaria regina"))continue;
			String empCount = U.isEmpty(nextLine[6]) ? "" : nextLine[6].replace("personas", "").replace(" a ", "-");
//			String empCount=U.isEmpty(nextLine[5])?"":nextLine[5].replace("personas", "").replace(" a ", "-");
//			U.log("empCount == " + empCount);

			if (nextLine[15].equals("0")) {
				nextLine[15] = nextLine[16];
			} else if (nextLine[15].trim().length() != 0 && nextLine[16].trim().length() != 0) {
				nextLine[15] = nextLine[15] + " " + nextLine[16];
			}
			String streetAdd = nextLine[7] + " " + nextLine[8] + " " + nextLine[15] + " " + nextLine[16];
//			U.log(nextLine[8]);
//			String streetAdd=nextLine[6]+" "+nextLine[7]+" "+nextLine[14]+" "+nextLine[15];

//			String text = "OFICINA DE ADMINISTRACIÓN DEL SIDEAPAS ANTONIO AMARO";
//			byte[] utf8Bytes1 = companyName.getBytes("UTF-8");
//			String correctValue = new String(utf8Bytes1, "UTF-8");
//			System.out.println("correctValue:::"+correctValue);
//			U.log("streetAdd == "+streetAdd);

			String colonia = nextLine[22] + " " + nextLine[23];
//			String colonia=nextLine[20]+" "+nextLine[21];
//			U.log("colonia == " + colonia);
			String postalCode = nextLine[26];
//			String postalCode=nextLine[25];
//			U.log("postalCode == " + postalCode);

			String city = nextLine[30];
//			String city=nextLine[29];
//			U.log("city == " + city);

//			String state=nextLine[27];
			String state = nextLine[28];
//			U.log("state == " + state);
			String phone = nextLine[35];
//			String phone=nextLine[34];
			if (phone.length() == 0)
				phone = ALLOW_BLANK;
//			U.log("phone == " + phone.length());
			String email = nextLine[36];
//			String email=nextLine[35];
			String website = nextLine[37];
//			String website=nextLine[36];
			if (website.isEmpty())
				website = ALLOW_BLANK;
//			U.log("website: " + website);
			String lat = nextLine[39];
//			String lat=nextLine[38];
			U.log("lat: " + lat);

			String lon = nextLine[40];
//			String lon=nextLine[39];
			U.log("lon: " + lon);
//			U.log(add);
//			U.log(Arrays.toString(add1));
			String siccode = "";
//			siccode=sicMap.get(nextLine[4]);
//			U.log("nextLine[3]: " + nextLine[3]);
//			U.log("nextLine[4]: " + nextLine[4]);

//			if(sicMap.get(nextLine[4])==null) {
//				System.out.println(nextLine[4]);
//				continue;
//			}

			siccode = sicMap.get(nextLine[4]).trim();
			U.log("nextLine[4] " + nextLine[4]);
//			U.log("siccode: " + siccode);

			// U.log("nextLine[3]: "+nextLine[3]);

//			if (siccode != null) {
			U.log("count :::::::::: " + count);
			U.log(siccode + " == " + companyName + " == " + phone + " == " + ALLOW_BLANK + " " + website.toLowerCase());

			addCompanyDetailsFromMexico(siccode, companyName, phone, ALLOW_BLANK, website.toLowerCase());
			// addCompanyDetailsFromMexico(sicSub, companyName, phone, ALLOW_BLANK,
			// website);
			addCompanyHoursOfOperation("");
			addAddress(streetAdd, colonia, city, state, postalCode);
			addBoundaries(lat, lon);
			addReferenceUrl(companyDataUrl);
			addCompanyOtherDetails(ALLOW_BLANK, empCount, ALLOW_BLANK);
			addContactPersonDetails(ALLOW_BLANK, ALLOW_BLANK, email.toLowerCase());
//			}

//			if(count == 10) break;
//			break;
		}
	}

	public static String correctWord(String word, HashSet<Character> validChars) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);
			if (validChars.contains(c)) {
				sb.append(c);
			} else {
				sb.append("?");
			}
		}
		return sb.toString();
	}

	public String getAddressFromLAtlonHereApi(String latlon[]) {
		try {
			String url = "https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox=" + latlon[0] + "%2C"
					+ latlon[1] + "%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id=" + ApiKey.HEREAPIKEYS[0][0]
					+ "&app_code=" + ApiKey.HEREAPIKEYS[0][1];
			String html = U.getHTML(url);
			if (html.contains("\"Address\":"))
				return U.getSectionValue(html, "\"Label\":\"", "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getAddressFromLatlonHereApi(String latlon[]) {
		try {
			String url = "https://reverse.geocoder.api.here.com/6.2/reversegeocode.json?prox=" + latlon[0] + "%2C"
					+ latlon[1] + "%2C50&mode=retrieveAddresses&maxresults=1&gen=9&app_id=" + ApiKey.HEREAPIKEYS[0][0]
					+ "&app_code=" + ApiKey.HEREAPIKEYS[0][1];
			String html = U.getHTML(url);
			if (html.contains("\"Address\":")) {
				String add = U.getSectionValue(html, "\"Street\":\"", "\"") + " "
						+ U.getSectionValue(html, "\"HouseNumber\":\"", "\"");
				String colonia = U.getSectionValue(html, "\"District\":\"", "\"");
				String city = U.getSectionValue(html, "\"City\":\"", "\"");
				String state = MXStates.getFullNameFromAbbr(U.getSectionValue(html, "\"State\":\"", "\""));
				String postal = U.getSectionValue(html, "\"PostalCode\":\"", "\"");
				return new String[] { add, colonia, city, state, postal };
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	static HashMap<String, String> sicMap = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("112511", "0273");
			put("112512", "0273");
			put("114111", "0921");
			put("114111", "0921");
			put("114112", "0273");
			put("114113", "0921");
			put("114119", "0273");
//			
			put("115111", "0279");
			put("115112", "0273");
			put("115113", "0273");
			put("115119", "0273");
//			
			put("115119", "8299");
			put("115210", "0219");
			put("115310", "0181");

			put("222111", "4941");
			put("222210", "4619");
			put("221110", "4612");
			put("221120", "4911");
			put("222112", "4619");

			put("212395", "1479");
			put("212231", "1021");
			put("212110", "1241");
			put("212396", "1499");
			put("212210", "1011");
			put("212232", "1031");
			put("212299", "1459");
			put("212398", "3299");
			put("212391", "1479");
			put("212292", "1099");
			put("212392", "1499");
			put("211110", "1389");

			put("212311", "7389");
			put("212395", "1479");
			put("212110", "1241");
			put("212231", "1021");
			put("212396", "1499");
			put("212210", "1011");
			put("212232", "1031");
			put("212397", "1479");
			put("212398", "1499");
			put("212299", "1099");
			put("212391", "1479");
			put("212292", "1099");
			put("212392", "1499");
			put("212393", "1479");
			put("212394", "1475");
			put("212291", "1061");
			put("213111", "1381");
			put("212221", "1041");

			put("212321", "1442");
			put("212222", "1044");
			put("211111", "1311");
			put("212319", "1411");
			put("213119", "1081");
			put("211112", "1311");
			put("212329", "1459");

			put("221111", "4931");

			put("212399", "1499");
			put("212322", "3241");
			put("212312", "1411");
			put("212323", "1459");
			put("212324", "1446");
			put("212325", "1455");

			put("721190", "7011");
			put("722320", "5812");
			put("721111", "7011");
			put("721210", "7033");
			put("722310", "5812");
			put("722330", "5963");
			put("721312", "7041");
			put("722511", "5812");
			put("722412", "5813");
			put("722512", "5812");
			put("721112", "7011");
			put("722411", "5813");

			put("721311", "7041");
			put("721113", "5812");
			put("722515", "5812");
			put("722516", "5812");
			put("722513", "5812");
			put("722514", "5812");
			put("722519", "5812");
			put("722517", "5812");
			put("722518", "5812");

			put("551112", "6719");
			put("551111", "6712");
			put("551112", "6719");
			put("551112", "6719");
			put("551112", "6719");
			put("551112", "6719");
			put("551112", "6719");
			put("551112", "6719");
			put("551112", "6719");

			put("611622", "8299");
			put("611511", "8299");
			put("611698", "8299");
			put("611412", "8244");
			put("611632", "8299");
			put("611631", "8299");
			put("611312", "8221");
			put("611411", "8244");
			put("611311", "8211");
			put("611212", "8222");
			put("611432", "8299");
			put("611112", "8211");

			put("611211", "8222");
			put("611431", "8299");
			put("611111", "8211");
			put("611132", "8211");
			put("611131", "8211");
			put("611691", "8299");
			put("611152", "8211");
			put("611151", "8211");
			put("611172", "8299");

			put("611171", "8299");
			put("611612", "8299");
			put("611611", "8299");
			put("611512", "8299");
			put("611699", "8299");
			put("611710", "8299");
			put("611621", "8299");
			put("611422", "8243");
			put("611421", "8243");

			put("611122", "8211");
			put("611121", "8211");
			put("611142", "8211");
			put("611141", "8211");
			put("611162", "8211");
			put("611161", "8211");
			put("611182", "8211");
			put("611181", "8211");

			put("221119", "4911");
			put("221114", "4911");
			put("221112", "4931");
			put("221113", "4911");
			put("221121", "4911");
			put("221122", "4911");
			put("221123", "4911");
			put("221210", "4922");
			put("221311", "4941");
			put("221111", "4931");
			put("221312", "4941");
			put("221311", "4941");

			put("236221", "8712");
			put("236111", "1522");
			put("236112", "1522");
			put("236113", "1522");
			put("236211", "1521");
			put("236212", "8712");

			put("238210", "1629");
			put("238222", "1711");
			put("238221", "1711");
			put("237132", "1623");
			put("237111", "1629");
			put("237112", "1629");
			put("238122", "3448");
			put("237131", "8711");
			put("238320", "1799");

			put("323111", "2759");
			put("331419", "3339");
			put("331412", "3331");
			put("313310", "7389");
			put("339940", "3951");
			put("312221", "7389");
			put("312222", "7389");
			put("331411", "3339");
			put("314999", "3569");
			put("326192", "3088");
			put("332991", "3562");
			put("326191", "3088");
			put("336310", "3592");
			put("326194", "3088");
			put("326193", "3088");
			put("335220", "3633");
			put("311921", "2043");
			put("311922", "2043");
			put("311923", "2043");
			put("326198", "3069");
			put("311924", "2099");
			put("326199", "3999");
			put("323120", "2796");
			put("312111", "2086");
			put("339930", "3951");// 332610
			put("313320", "3069");
			put("312113", "2097");
			put("332610", "3496");
			put("331520", "3365");
			put("312112", "2086");
			put("327391", "3272");

			put("335210", "3999");
			put("311930", "2087");
			put("311811", "5461");
			put("311812", "2051");
			put("311813", "2053");
			put("323119", "2732");

			put("327399", "3272");
			put("325411", "5122");
			put("325412", "2834");
			put("322122", "2611");
			put("322121", "2621");
			put("325999", "2899");
			put("311350", "2066");
			put("311230", "2043");
			put("311110", "2048");
			put("339920", "3231");
			put("332720", "3452");
			put("331510", "3325");
			put("339911", "5999");
			put("339912", "5944");
			put("339913", "5094");
			put("334110", "3577");
			put("336410", "3728");
			put("326290", "3069");
			put("339914", "5051");
			put("325520", "2891");
			put("322132", "2611");
			put("322131", "2653");
			put("339111", "5047");
			put("314991", "2395");
			put("332710", "3569");
			put("339112", "3841");
			put("333249", "3569");
			put("333920", "3531");
			put("339113", "3851");
			put("336999", "3799");
			put("333246", "3999");
			put("312210", "5194");
			put("333245", "3555");
			put("314992", "2298");
			put("314993", "2299");
			put("335311", "3621");
			put("335312", "5063");
			put("334220", "3663");
			put("325190", "2869");
			put("311910", "2099");
			put("326160", "3085");
			put("333130", "3532");
			// put("325991", "5063");
			put("325992", "3861");
			put("325993", "2221");
			put("325510", "2851");
			put("327910", "3291");
			put("322220", "2679");
			put("333910", "3561");
			put("311211", "2044");
			put("316219", "5139");
			put("333242", "3559");
			put("333120", "3531");
			put("336510", "3743");
			put("333241", "3553");
			put("333244", "3552");
			put("336992", "3751");
			put("333243", "3556");
			put("325180", "2819");
			put("334210", "3661");
			put("326150", "3086");
			// put("325991", "3086");
			put("336991", "3751");
			put("327122", "3255");
			put("327121", "3297");
			put("321920", "2449");
			put("327123", "3255");
			put("325620", "2844");
			put("322110", "2611");
			put("322230", "2678");
			put("332810", "3479");
			put("316110", "3111");
			put("311340", "2064");
			put("311221", "2079");
			put("311222", "2079");
			put("311212", "2041");
			put("311213", "2041");
			put("311214", "2041");
			put("333112", "3523");
			put("333111", "3523");
			put("311215", "2083");
			put("326140", "3086");
			put("327111", "3269");
			put("333319", "3599");
			put("315210", "3172");
			put("314120", "2391");
			put("333312", "3555");
			put("337910", "2515");
			put("311520", "2024");
			put("334410", "3679");
			put("314911", "2674");
			put("332110", "2674");
			put("314912", "2295");
			put("337120", "2519");
			put("327320", "3272");
			put("326110", "2671");
			put("321210", "3083");

			put("511142", "7331");
			put("512111", "7812");
			put("511141", "7331");
			put("512112", "7812");
			put("512113", "7812");
			put("511121", "2721");
			put("511122", "2721");
			put("512130", "7833");
			put("512230", "8999");
			put("519190", "7389");
			put("511192", "2741");
			put("511191", "2771");
			put("519110", "7383");
			put("511131", "2731");
			put("511132", "2731");
			put("517910", "4899");
			put("512240", "7389");
			put("512120", "7822");
			put("517311", "4813");
			put("517312", "4812");
			put("515210", "4841");
			put("517410", "4899");
			put("511210", "7372");
			put("511111", "2711");
			put("511112", "3555");
			put("518210", "7374");
			put("512190", "7819");
			put("519122", "8231");
			put("512290", "7922");
			put("515120", "4833");
			put("515110", "4832");
			put("512250", "3652");
			put("519121", "8231");
			put("519130", "8999");
			put("488492", "9131");
			put("482110", "4011");

			put("562111", "4953");

//			put("621398", "6213");

//			put("481111","4511");
			put("481112", "4512");
			put("481210", "5812");
			put("483111", "4731");
			put("483210", "4789");
			put("484111", "4213");
			put("484119", "4789");
			put("484121", "4213");
			put("484129", "4789");
			put("484210", "4789");
			put("484221", "4213");
			put("484222", "4789");
			put("484223", "4222");
			put("485210", "4131");
			put("485510", "4213");
			put("485990", "4789");
			put("487110", "4789");
			put("487210", "4725");
			put("488111", "3812");
			put("488112", "4581");
			put("488190", "4512");
			put("488210", "3743");
			put("488310", "4173");
			put("481111", "4512");
			put("485111", "4789");
			put("488410", "5599");
			put("488493", "7521");
			put("488519", "4731");
			put("488990", "4731");
			put("488491", "4131");
			put("485410", "7363");
			put("484229", "4789");
			put("491110", "4311");
			put("492110", "4731");
			put("493111", "5063");
			put("493119", "3764");
			put("493120", "3632");
			put("488390", "4491");
			put("483112", "4491");
			put("483113", "4922");
			put("484224", "4212");
			put("485113", "4111");
			put("485114", "4111");
			put("488330", "4499");
			put("486910", "4613");
			put("493130", "4221");
			put("485115", "4111");
			put("486210", "4922");
			put("488320", "4491");
			put("487990", "4724");
			put("486990", "4619");
			put("486110", "4612");

			put("484119", "4731");
			put("484239", "3555");
			put("488519", "4731");
			put("484111", "4214");
			put("484232", "4213");
			put("484231", "4213");
			put("488511", "4833");
			put("485320", "4832");
			put("484234", "4213");
			put("484233", "4213");
			put("493190", "4226");
			put("492210", "4215");
//			put("321210", "3083");

			put("931610", "7991");
			put("931510", "4941");
			put("931710", "8421");
			put("931410", "0851");
			put("931810", "9711");
			put("931710", "9199");
			put("931510", "9431");
			put("931410", "9211");
			put("931310", "9631");
//			put("931210", "8411");
//			put("931110", "9121");
			put("813230", "8641");
			put("813210", "8661");
			put("813130", "8621");
			put("813120", "8631");
			put("813110", "8611");
			put("812990", "7299");
			put("812910", "7384");
			put("812410", "7521");
			put("812321", "7261");
			put("812310", "7261");
			put("812210", "7212");
			put("812130", "7933");
			put("812110", "7231");
			put("811499", "7692");
			put("811492", "7539");
			put("811491", "7215");
			put("811430", "7251");
			put("811420", "7641");
			put("811410", "7629");
			put("811313", "7623");
			put("811312", "7623");
			put("811219", "7629");
			put("811211", "7629");
			put("811199", "7539");
			put("811192", "7542");
			put("811191", "7534");
			put("811129", "7538");
			put("811122", "7532");
			put("811121", "7532");
			put("811119", "7539");
			put("811116", "7539");
			put("811115", "7539");
			put("811113", "3714");
			put("811111", "7538");
			put("811113", "7539");
			put("722519", "2099");
			put("722518", "5812");
//			put("931110", "9121");
			put("931210", "8741");
			put("931110", "9121");
			put("812322", "6553");
			put("811493", "7699");
			put("621115", "8011");
			put("621116", "8011");
			put("624191", "5813");
			put("623991", "8361");
			put("811493", "7999");
			put("811114", "7537");
			put("621211", "8021");
			put("621511", "8071");
			put("622112", "8069");
			put("622111", "8069");
			put("621111", "8011");
			put("561110", "8741");
			put("561330", "7363");
			put("621113", "8011");
			put("621311", "8041");
			put("624411", "5261");
			put("621320", "8042");
			put("621331", "8049");
			put("621391", "8322");
			put("621398", "8361");
			put("713943", "7991");
			put("811112", "7539");
			put("561432", "7376");
			put("621341", "8049");
			put("624112", "8322");
			put("624199", "8322");
			put("624212", "5812");
			put("624412", "5261");
			put("713120", "7372");
			put("713291", "7941");
			put("713299", "7372");
			put("713944", "7991");
			put("713991", "3949");

			put("339999", "3999");
			put("339995", "3995");
			put("339994", "3994");
			put("339993", "3991");
			put("339992", "3965");
			put("339991", "3931");
			put("339950", "3993");
			put("339940", "3952");
			put("339930", "3942");
			put("339920", "3949");
			put("339914", "3911");

			put("339913", "3915");
			put("339912", "3911");
			put("339914", "3911");
			put("461110", "5499");

		}

	};
}
