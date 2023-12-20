package com.shatam.ruleParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.fasterxml.jackson.core.JsonParseException;
import com.shatam.utils.U;

public class RuleParser {
	public static String ruleListFileName = System.getProperty("user.dir") + "/Record_Search_Rule.json";

	public static void main(String args[]) throws JsonParseException, JsonMappingException, IOException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ruleListFileName = "/home/mypremserver/RYE DATA/RakeshSir/Record_Search_Rule.json";
		new RuleParser().test();

	}

	public void test() throws JsonParseException, JsonMappingException, IOException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {

		Rules ruleList = getRulesObject(new File(ruleListFileName));
		//
		Rule r = new Rule();
		r.setAddress("60");
		r.setCity("70");

		System.out.println(executeAndGet(ruleList.getRules(), r));
	}

	public Rules getRulesObject(File f) throws IOException {

		BufferedReader buffReader = null;
		try {
			StringBuilder builder = new StringBuilder();
			FileReader reader = new FileReader(f);
			buffReader = new BufferedReader(reader);
			String line = null;
			while ((line = buffReader.readLine()) != null) {
				builder.append(line);
			}
			buffReader.close();
			ObjectMapper mapper = new ObjectMapper();
			// U.log(builder);
			Rules ruleList = mapper.readValue(builder.toString(), Rules.class);
			// U.log(ruleList.);
			return ruleList;
		} finally {
			if (buffReader != null) {
				buffReader.close();
			}
		}
	}

	public static List<Integer> getList(Rule r)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method[] = r.getClass().getDeclaredMethods();
		List<Integer> list1 = new ArrayList<Integer>();
		for (int i = 0; i < method.length; i++) {
			String name = method[i].getName();
			if (name.startsWith("get")) {
				String value = (String) method[i].invoke(r, null);
				list1.add(Integer.parseInt((value == null) ? "0" : value));
				// System.out.println(method[i]);
			}
		}
		return list1;
	}
	static int caseNoValid;
	public static int getValidCaseNo() {
		return caseNoValid;
	}
	public static boolean executeAndGet(List<Rule> rulesList, Rule rule1)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<Integer> list1 = getList(rule1);
		caseNoValid=0;
		for (Rule rule2 : rulesList) {
			List<Integer> list2 = getList(rule2);
			boolean isvalid = true;
			for (int i = 0; i < list2.size(); i++) {
				if (!(list2.get(i) <= list1.get(i))) {
					isvalid = false;
					caseNoValid++;
				}
			}
			if (isvalid) {
				return true;
			}
		}
		return false;
	}

}
