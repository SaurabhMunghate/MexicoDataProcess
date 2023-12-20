package com.shatam.translate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.text.*;

import com.shatam.utils.Path;
import com.shatam.utils.U;
import com.shatam.utils.Util;

public class TranslateEnglish {
	
	public static void main(String[] args){
		U.log(Path.RESOURCES);
		U.log(U.toTitleCase(convertToEnglish("Tlajomulco de Zúñiga")));
		U.log(convertUnicodeToSpanish("M\u00e9xico"));
//		U.log(org.apache.commons.text.StringEscapeUtils.unescapeHtml4("M\u00e9xico"));
	}
	
	private static Map<String,String> unicodeMap = loadunicodeToChar();
	
	private static Map<String,String> javaCodeMap = loadJavaForSpanishChar();
	
	public final static String convertToEnglish(String str){
		
		if (unicodeMap == null) {
			unicodeMap = loadunicodeToChar();
		}
		// System.out.println(str.toUpperCase());
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			String codePoint = str.codePointAt(i) + "";
			if (codePoint.length() == 3) {
				codePoint = "0" + codePoint;
			}
			String char1 = unicodeMap.get(codePoint);
			if (char1 != null) {
				buff.append(char1);
			} else {
				buff.append(str.charAt(i));
			}
		}
		return buff.toString(); 
	}
	
	private static final Map<String, String> loadunicodeToChar()  {
		List<String> list = null;
		try {
			list = Files.readAllLines(Paths.get(Path.RESOURCES + "Unicodecodepoint.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		list.remove(0);
		Map<String, String> map = new HashMap<>();
		for (String s : list) {
			String unicode = s.split("\\s")[1];
			String char1 = Util.match(s, "letter ([a-zA-Z]{1,2}) with", 1);
			if (char1 != null) {
				map.put(unicode, char1);
			}
			// System.out.println(unicode + "\t" + char1);
		}
		return map; 
	}
	
	
	public final static String convertUnicodeToSpanish(String str){
		for(String javaCode : javaCodeMap.keySet()){
			if(str.toLowerCase().contains(javaCode.toLowerCase())){
				str = str.replace(javaCode.toLowerCase(), javaCodeMap.get(javaCode));
			}
		}
		return str;
	}
	
	private static final Map<String,String> loadJavaForSpanishChar(){
		List<String> list = null;
		try {
			list = Files.readAllLines(Paths.get(Path.RESOURCES + "SpanishAndUnicode.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> map = new HashMap<>();
		for(String line : list){
			String[] vals = line.split("\t");
			map.put(vals[vals.length-1], vals[0]);
		}
		return map;
	}
	
	public static String removeUnicode(String str) {
		// TODO Auto-generated method stub
		str = StringEscapeUtils.unescapeHtml3(str);
		str = StringEscapeUtils.unescapeHtml4(str);
		str = StringEscapeUtils.unescapeJava(str);
		str = StringEscapeUtils.unescapeJson(str);
		str = StringEscapeUtils.unescapeXml(str);
		return str;
	}
}
