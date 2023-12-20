package com.tequila.database.search;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import com.opencsv.CSVWriter;
import com.shatam.ruleParser.Rule;
import com.shatam.ruleParser.RuleParser;
import com.shatam.ruleParser.Rules;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

import me.xdrop.fuzzywuzzy.FuzzySearch;

public class DatabaseSearchClient {
	static String base = "http://127.0.0.1:8080/postRowsearch/";
	static String CSVPATH="";
	public static String ruleListFileName=System.getProperty("user.dir")+"/Record_Search_Rule.json";
	List<String[]> dupList=new ArrayList<String[]>();
	List<String[]> uniqList=new ArrayList<String[]>();
	List<String[]> exactList=new ArrayList<String[]>();
	public static void main(String args[]) throws IOException {
		DatabaseSearchClient search=new DatabaseSearchClient();
		try {
//			U.log(args.length);
//			if (args.length>0) {
//				CSVPATH=args[0];
				CSVPATH="/home/chinmay/MexicoCache/Cache/InegiCSV/Inegi_Information_0_20000_CORRECT_NW_REC.csv";
//			}else {				
//				throw new Exception("Plese Provide FilePath");
//			}
			
//			String DIR_PATH="/home/chinmay/MexicoCache/RestaurantGuruCSV/ExtractedFiles/temp/MERGEDFILE/SPLITTED";
//			File[] files = new File(DIR_PATH).listFiles();
//			
//			for(File file : files){
//				U.log("File Name ::"+file.getName());
//				if(file.isDirectory()){
//					U.log("Directory found so continue");
//					continue;
//				}
//				search.processCsvFile(file.getAbsolutePath());
//			}
			search.processCsvFile(CSVPATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public String processCsvFile(String csvFilePath) throws Exception{
		List<String[]> csvInputData=U.readCsvFile(csvFilePath);
		processHeader(csvInputData.get(0));
		RuleParser parser=new RuleParser();
		//ruleListFileName="/home/mypremserver/RYE DATA/RakeshSir/Record_Search_Rule.json";
		Rules ruleList = parser.getRulesObject(new File(ruleListFileName));
		Rule r = new Rule();
		Set<String>reqHeaderCol=reqHeaderCols.keySet();
		dupList.add(csvInputData.get(0));
		uniqList.add(csvInputData.get(0));
		exactList.add(csvInputData.get(0));
		int countr=0;
		for (int i = 1; i < csvInputData.size(); i++) {
			String[] csvInput=csvInputData.get(i);
			try {
			//
//			if (countr==0) {
//				break;
//			}
//			if (!csvInput[0].equals("425870")) {
//				continue;
//			}		
			U.log("Counter "+i);
			String queryString=getQueryString(reqHeaderCol,csvInput);
			String res = searchRecord(getSearchbleJSON(queryString.replaceAll("[\t\n\\.-]|!|\\*|\\?|\\[|\\]|no name|\\{|~", " ")));
			//U.log(res);
			ObjectMapper mapper = new ObjectMapper();
			if (res.contains("[]")) {
				continue;
			}
			boolean flag=true;
			ArrayList<Integer> matchedRecord=new ArrayList<>();
			List<List<String>> elements = mapper.readValue(res, List.class);
			for (int j = 0; j < elements.size(); j++) {
				//U.log(elements.get(j));
				//int sicSubRatio=reqHeaderCols.get("SIC_SUB").equals("")?0:FuzzySearch.ratio(csvInput[Integer.parseInt(reqHeaderCols.get("SIC_SUB"))], elements.get(j).get(Integer.parseInt(outputHeader.get("SIC_SUB"))));
				int companyNameRatio=reqHeaderCols.get("COMPANY_NAME").equals("")?0:FuzzySearch.tokenSortRatio(csvInput[Integer.parseInt(reqHeaderCols.get("COMPANY_NAME"))], elements.get(j).get(Integer.parseInt(outputHeader.get("COMPANY_NAME"))));
				int addressRatio=reqHeaderCols.get("ADDRESS").equals("")?0:FuzzySearch.tokenSortRatio(csvInput[Integer.parseInt(reqHeaderCols.get("ADDRESS"))], elements.get(j).get(Integer.parseInt(outputHeader.get("ADDRESS"))));
				int cityRatio=reqHeaderCols.get("CITY").equals("")?0:FuzzySearch.tokenSortRatio(csvInput[Integer.parseInt(reqHeaderCols.get("CITY"))], elements.get(j).get(Integer.parseInt(outputHeader.get("CITY"))));
				int stateRatio=reqHeaderCols.get("STATE").equals("")?0:FuzzySearch.tokenSortRatio(csvInput[Integer.parseInt(reqHeaderCols.get("STATE"))], elements.get(j).get(Integer.parseInt(outputHeader.get("STATE"))));
				int zipRatio=reqHeaderCols.get("ZIP").equals("")?0:FuzzySearch.tokenSortRatio(csvInput[Integer.parseInt(reqHeaderCols.get("ZIP"))], elements.get(j).get(Integer.parseInt(outputHeader.get("ZIP"))));
				String outputPhone=elements.get(j).get(Integer.parseInt(outputHeader.get("PHONE"))).replaceAll("\\s*Ext.\\s*\\d+|\\*Ext\\s*\\d+|\\s*ext\\s*\\d+|-|\\+", "");
				String inputPhone=csvInput[Integer.parseInt(reqHeaderCols.get("PHONE"))].replaceAll("\\s*Ext.\\s*\\d+|Ext\\s*\\d+|\\s*ext\\s*\\d+|-|\\+", "");
				int phoneRatio=0;
				if (outputPhone.contains(";")) {
					String tempout[]=outputPhone.split(";");
					int tempRatio=0;
					if (inputPhone.contains(";")) {
						String tempInput[]=inputPhone.split(";");
						for (String iTemp : tempInput) {
							for (String ptemp : tempout) {
								tempRatio=reqHeaderCols.get("PHONE").equals("")?0:FuzzySearch.tokenSortRatio(iTemp, ptemp);
								if (tempRatio>phoneRatio) {
									phoneRatio=tempRatio;
								}
							}
						}
					}else {
						for (String ptemp : tempout) {
							tempRatio=reqHeaderCols.get("PHONE").equals("")?0:FuzzySearch.tokenSortRatio(inputPhone, ptemp);
							if (tempRatio>phoneRatio) {
								phoneRatio=tempRatio;
							}
						}
					}
				}else {
					int tempRatio=0;
					if (inputPhone.contains(";")) {
						String tempInput[]=inputPhone.split(";");
						for (String iTemp : tempInput) {
							tempRatio=reqHeaderCols.get("PHONE").equals("")?0:FuzzySearch.tokenSortRatio(iTemp, outputPhone);
							if (tempRatio>phoneRatio) {
								phoneRatio=tempRatio;
							}
						}
					}else {
						phoneRatio=reqHeaderCols.get("PHONE").equals("")?0:FuzzySearch.tokenSortRatio(inputPhone, outputPhone);
					}
				}
				int urlRatio=reqHeaderCols.get("URL").equals("")?0:FuzzySearch.tokenSortRatio(csvInput[Integer.parseInt(reqHeaderCols.get("URL"))], elements.get(j).get(Integer.parseInt(outputHeader.get("URL"))));
				//int contactPersonRatio=reqHeaderCols.get("CONTACT_PERSON").equals("")?0:FuzzySearch.tokenSortRatio(csvInput[Integer.parseInt(reqHeaderCols.get("CONTACT_PERSON"))], elements.get(0).get(Integer.parseInt(outputHeader.get("CONTACT_PERSON"))));
				//ratio=companyNameRatio+addressRatio+cityRatio+stateRatio+zipRatio+phoneRatio+urlRatio;
				r.setCompany_Name(""+companyNameRatio);
				r.setAddress(""+addressRatio);
				r.setCity(""+cityRatio);
				r.setState(""+stateRatio);
				r.setZip(""+zipRatio);
				r.setPhone(""+phoneRatio);
				r.setUrl(""+urlRatio);
				if (RuleParser.executeAndGet(ruleList.getRules(), r)) {
					//U.log(RuleParser.getCaseNoValid());
//					if (RuleParser.getValidCaseNo()==0) {
//						String out[]=new String[csvInput.length+2];
//						System.arraycopy(csvInput, 0, out, 0, csvInput.length);
//						String updation=getUpdationColumnsName(csvInput,elements.get(j));
//						out[csvInput.length]=elements.get(j).get(Integer.parseInt(outputHeader.get("ID")));
//						out[csvInput.length+1]=updation;
//						exactList.add(out);
//						flag=false;
//						matchedRecord=new ArrayList<>();
//						break;
//					}else {
						matchedRecord.add(j);
//					}
				}
				//break;
			}
			if (matchedRecord.size()>0) {
				for (int k = 0; k < matchedRecord.size(); k++) {
					String out[]=new String[csvInput.length+1];
					System.arraycopy(csvInput, 0, out, 0, csvInput.length);
					out[csvInput.length]=elements.get(k).get(Integer.parseInt(outputHeader.get("ID")));
					dupList.add(out);
				}
			}else if (flag) {
					uniqList.add(csvInput);
			}
			//break;
			}catch(Exception e) {
				System.out.println(e);
			}
		}
		writeCsv(dupList,csvFilePath.replace(".csv", "_Maybe_Duplicate.csv"));
		writeCsv(uniqList,csvFilePath.replace(".csv", "_Unique.csv"));
		writeCsv(exactList,csvFilePath.replace(".csv", "_Exact_Match.csv"));
		return csvFilePath.replace(".csv", "_Unique.csv");
	}
	private int tokenSortRatio(String Header,String[] csvInput,List<List<String>> elements,int j) {
		return reqHeaderCols.get(Header).equals("")?0:FuzzySearch.tokenSortRatio(csvInput[Integer.parseInt(reqHeaderCols.get(Header))], elements.get(j).get(Integer.parseInt(outputHeader.get(Header))));
	}
	private String getUpdationColumnsName(String[] csvInput, List<String> outputList) {
		String update="";
		Set<String> headerkeySet=reqHeaderCols.keySet();
		for (String key : headerkeySet) {
			if (!U.isEmpty(reqHeaderCols.get(key))) {
				if (!U.isEmpty(csvInput[Integer.parseInt(reqHeaderCols.get(key))])&&U.isEmpty(outputList.get(Integer.parseInt(outputHeader.get(key))))) {
					update+=key+";";
				}
			}
		}
		if (!U.isEmpty(update)) {
			update=update.substring(0,update.length()-1);
		}
		return update;
	}

	private String getQueryString(Set<String> reqHeaderCol, String[] csvInput) {
		String queryString="";
		for (String headerCol : reqHeaderCol) {
			if (headerCol.contains("URL")||headerCol.contains("PHONE")||headerCol.contains("EMAIL")) continue;
			if(!U.isEmpty(reqHeaderCols.get(headerCol)))
				queryString+="\t"+csvInput[Integer.parseInt(reqHeaderCols.get(headerCol))].replaceAll(":|\\+|\"|/|\\(|\\)", "");
		}
		return queryString;
	}

	private void writeCsv(List<String[]> csvInputData,String fileName) {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeAll(csvInputData);
			FileUtil.writeAllText(fileName, sw.toString());
			writer.close();
			sw.close();
			U.log(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void processHeader(String[] headers) throws Exception {
		boolean flag=false;
		Set<String>reqHeaderCol=reqHeaderCols.keySet();
		for (int i = 0; i < headers.length; i++) {
			headers[i]=headers[i].toLowerCase().equals("SUB_SIC_Category=Categoria_Sub_SIC".toLowerCase())||headers[i].toLowerCase().equals("Sub_sic_category=categoria Del Sic".toLowerCase())?"SIC_SUB":headers[i];
			headers[i]=headers[i].toLowerCase().equals("Company_Name=Nombre_De_Empresa".toLowerCase())||headers[i].toLowerCase().equals("Company_name=nombre De La Empresa".toLowerCase())?"COMPANY_NAME":headers[i];
			headers[i]=headers[i].toLowerCase().equals("ADDRESS=DIRECCION".toLowerCase())||headers[i].toLowerCase().equals("Address=address_direccion".toLowerCase())?"ADDRESS":headers[i];
			headers[i]=headers[i].toLowerCase().equals("NEIGHBORHOOD=BARRIO".toLowerCase())||headers[i].toLowerCase().equals("Neighbourhood".toLowerCase())?"NEIGHBORHOOD":headers[i];
			headers[i]=headers[i].toLowerCase().equals("CITY=CIUDAD".toLowerCase())||headers[i].toLowerCase().equals("City=city_ciudad".toLowerCase())?"CITY":headers[i];
			headers[i]=headers[i].toLowerCase().equals("STATE=ESTADO".toLowerCase())||headers[i].toLowerCase().equals("State=state_estado".toLowerCase())?"STATE":headers[i];
			headers[i]=headers[i].toLowerCase().equals("ZIP_CODE=CODIGO_POSTAL".toLowerCase())||headers[i].toLowerCase().equals("Zipcode=codigopostal".toLowerCase())?"ZIP":headers[i];
			headers[i]=headers[i].toLowerCase().equals("PHONE=TELEFONO".toLowerCase())||headers[i].toLowerCase().equals("Phone=telephone_telefono".toLowerCase())?"PHONE":headers[i];
			headers[i]=headers[i].toLowerCase().equals("URL=URL".toLowerCase())||headers[i].toLowerCase().equals("WEBSITE_URL".toLowerCase())||headers[i].toLowerCase().equals("url=web address".toLowerCase())?"URL":headers[i];
			headers[i]=headers[i].toLowerCase().equals("CONTACT_PERSON=PERSONA_DE_CONTACTO".toLowerCase())||headers[i].toLowerCase().equals("Contact Person=contacto_ejecutivo".toLowerCase())?"CONTACT_PERSON":headers[i];
			headers[i]=headers[i].toLowerCase().equals("EMAIL=CORREO_ELECTRONICO".toLowerCase())||headers[i].toLowerCase().equals("EMAIL = Email_Correo Electrónico".toLowerCase())?"EMAIL":headers[i];
			
			if (reqHeaderCol.contains(headers[i])) {
				reqHeaderCols.put(headers[i], ""+i);
			}
			
		}
		for (String reheader : reqHeaderCols.keySet()) {
			//U.log(reheader+"  "+reqHeaderCols.get(reheader));
			if (U.isEmpty(reqHeaderCols.get(reheader))) { 
				U.log(reheader+"  "+reqHeaderCols.get(reheader));
				if (!reheader.equals("SIC_SUB")&&!reheader.equals("CONTACT_PERSON")&&!reheader.equals("URL")&&!reheader.equals("EMAIL"))
					flag=true;
			}
		}	
		if (flag) throw new Exception("Header missing");
	}
	
	@SuppressWarnings("unchecked")
	public static void sample() throws IOException {
		DatabaseSearchClient search = new DatabaseSearchClient();
		String data = "Gas Natural Mexico, S.A. de C.V.	Jaime Balmes #8, piso 7, Interior 704, Colonia Los Morales Polanco, Ciudad de Mexico, Distrito Federal, Mexico";
		data = data.replaceAll("[\t\n]", "");
		String res = search.searchRecord(search.getSearchbleJSON(data));
		ObjectMapper mapper = new ObjectMapper();
		List<List<String>> elements = mapper.readValue(res, List.class);
		System.out.println(elements.get(0).get(2));

	}

	public String getSearchbleJSON(String searchStr) {
		ObjectMapper mapper = new ObjectMapper();
		String arrNode = "[\"" + searchStr + "\"]";
		ObjectNode childNode1 = mapper.createObjectNode();
		childNode1.put("content", arrNode);
		System.out.println(childNode1);
		return childNode1.toString();

	}
	
	public String searchRecord(String data) throws IOException {

		URL url = new URL(base);
		URLConnection con = url.openConnection();
		con.setDoOutput(true);
		con.setRequestProperty("Content-Type",
				"application/json; charset=utf-8");
		con.setRequestProperty("content", data);
		con.setRequestProperty("Content-Length", String.valueOf(data.length()));
		con.getOutputStream().write(data.getBytes());
		String line = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		StringBuffer buff = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			buff.append(line);
		}
		return buff.toString();
	}
	private LinkedHashMap<String, String> reqHeaderCols=new LinkedHashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8869235573602820944L;	
		{
			put("SIC_SUB", "");
			//put("SUB_SIC_Category=Categoria_Sub_SIC","");
			put("COMPANY_NAME", "");
			//put("Company_Name=Nombre_De_Empresa", "");
			put("ADDRESS", "");
			//put("ADDRESS=DIRECCION", "");
			put("NEIGHBORHOOD", "");
			//put("NEIGHBORHOOD=BARRIO", "");
			put("CITY", "");
			//put("CITY=CIUDAD","");
			put("STATE", "");
			//put("STATE=ESTADO", "");
			put("ZIP", "");
			//put("ZIP_CODE=CODIGO_POSTAL", "");
			put("PHONE", "");
			//put("PHONE=TELEFONO","");
			put("URL", "");
			//put("URL=URL","");
			put("CONTACT_PERSON", "");
			//put("CONTACT_PERSON=PERSONA_DE_CONTACTO", "");
			put("EMAIL", "");
			
		}
	};
	private HashMap<String, String> outputHeader=new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8869235573602820944L;
		{
			put("ID", "0");
			put("SIC_SUB", "1");
			put("SUB_SIC_Category=Categoria_Sub_SIC", "1");
			put("COMPANY_NAME", "2");
			put("Company_Name=Nombre_De_Empresa", "2");
			put("ADDRESS", "3");
			put("ADDRESS=DIRECCION", "3");
			put("NEIGHBORHOOD", "4");
			put("NEIGHBORHOOD=BARRIO", "4");
			put("CITY", "5");
			put("CITY=CIUDAD", "5");
			put("STATE", "7");
			put("STATE=ESTADO", "7");
			put("ZIP", "6");
			put("ZIP_CODE=CODIGO_POSTAL", "6");
			put("PHONE", "8");
			put("PHONE=TELEFONO", "8");
			put("URL", "9");
			put("URL=URL", "9");
			put("EMAIL", "10");
			put("EMAIL=CORREO_ELECTRONICO", "10");
			put("CONTACT_PERSON", "11");
			put("CONTACT_PERSON=PERSONA_DE_CONTACTO", "11");
			put("SCORE", "12");
		}
	};
}
