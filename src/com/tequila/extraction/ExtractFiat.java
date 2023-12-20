package com.tequila.extraction;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.jetty.util.ArrayUtil;

import com.database.connection.DBConnection;
import com.opencsv.CSVWriter;
import com.shatam.conversion.Sic;
import com.shatam.utils.Field;
import com.shatam.utils.FileUtil;
import com.shatam.utils.U;

import me.xdrop.fuzzywuzzy.FuzzySearch;

@SuppressWarnings("deprecation")
public class ExtractFiat {
	static final int CONTACT_PERSON=17;
	static final int COMPANY_NAME=7;
	static final int ADDRESS=8;
	static final int CITY=10;
	static final int STATE=11;
	static final int SIC_SUB=4;
	
	public static void main(String[] args) {
		//extractData();
		processDuplicateFile();
	}
	private static void processDuplicateFile() {
		String fileName="/home/mypremserver/DatabasesTequila/Ishan/Fiat_Distributor_Maybe_Duplicate.csv";
		String dbPath="/home/mypremserver/DatabasesTequila/sawansir/";
		String dbName="tequila.db";
		StringWriter sw=new StringWriter();
		CSVWriter writer=new CSVWriter(sw);
		StringWriter dupsw=new StringWriter();
		CSVWriter dupwriter=new CSVWriter(dupsw);
		writer.writeNext(HEADER);
		Connection conn=DBConnection.getConnection(dbPath, dbName);
		int i=0;
		try {
			MultiValueMap recordMap=new MultiValueMap();
			HashSet<String> uniqueKey=new HashSet<>();
			Statement stmt=conn.createStatement();
			List<String[]> data=U.readCsvFile(fileName);
			for (String[] record : data) {
				String dbID=record[record.length-1];
				if (dbID.contains("Fetching Time")||U.isEmpty(dbID)) continue;
				String key="";
				for (int j = 0; j < record.length-1; j++) {
					key+=record[j]+";;";
				}
				key=key.substring(0,key.length()-2);
				recordMap.put(key, record[record.length-1]);
//				if (i++==10) {
//					break;
//				}
			}
			for (Object key : recordMap.keySet()) {
				String record[]=key.toString().split(";;");
				Collection<String> dbIDs=recordMap.getCollection(key);
				boolean writeFlag=false;
				for (String dbID : dbIDs) {
	//				break;
					U.log(dbID);
					String sql="SELECT SIC_SUB,COMPANY_NAME,ADDRESS,CITY,STATE,CONTACT_PERSON from dataset where ID="+dbID;
					U.log(sql);
					ResultSet rs=stmt.executeQuery(sql);
					if (rs.next()) {
						String dbSic=rs.getString("SIC_SUB");
						String dbCmpName=rs.getString("COMPANY_NAME");
						String dbAdd=rs.getString("ADDRESS");
						String dbCity=rs.getString("CITY");
						String dbState=rs.getString("STATE");
						String dbContPer=rs.getString("CONTACT_PERSON");
	//					U.log(record[17]);
						if (dbContPer==null&&!U.isEmpty(record[CONTACT_PERSON])) {
							writeFlag=false;
						}else if (!U.isEmpty(dbContPer)&&!U.isEmpty(record[CONTACT_PERSON])) {
							int score=FuzzySearch.tokenSortRatio(record[CONTACT_PERSON], dbContPer);
							if (score>70) {
								writeFlag=true;
								break;
							}
						}else {
							writeFlag=true;
						}
	//					U.log(dbContPer);
						//FuzzySearch.tokenSortRatio(record[0], sicCode);
					}
					rs.close();
				}
				if(writeFlag==false&&uniqueKey.add((record[SIC_SUB]+record[COMPANY_NAME]+record[ADDRESS]+record[CITY]+record[STATE]+record[CONTACT_PERSON]).toLowerCase())) {
					String out[]=new String[record.length+1];
					System.arraycopy(record, 0, out, 0, record.length);
					out[record.length]=dbIDs.toString();
					U.log(dbIDs.toString());
					writer.writeNext(out);
//					break;
				}else if (writeFlag) {
					String out[]=new String[record.length+1];
					System.arraycopy(record, 0, out, 0, record.length);
					out[record.length]=dbIDs.toString();
					U.log(dbIDs.toString());
					dupwriter.writeNext(out);
				}
			}
			U.log(recordMap.size());
			FileUtil.writeAllText(fileName.replace("_Maybe_Duplicate.csv", "_CONTACT_PERSON_UNIQUE.csv"), sw.toString());
			FileUtil.writeAllText(fileName.replace("_Maybe_Duplicate.csv", "_CONTACT_PERSON_NOT_UNIQUE.csv"), dupsw.toString());
			stmt.close();
			writer.close();
			sw.close();
			dupwriter.close();
			dupsw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	private static String sicCode="5511";
	private static final String HEADER[] ={
			Field.ID.toString(),Field.INDUSTRY_SECTOR.toString(),Field.SPANISH_INDUSTRY_SECTOR.toString(),Field.SIC_MAJOR.toString(),Field.SIC_SUB.toString(),
			Field.PRODUCT_DESC.toString(),Field.SPANISH_PRODUCT_DESC.toString(),Field.COMPANY_NAME.toString(),Field.ADDRESS.toString(),Field.NEIGHBORHOOD.toString(),Field.CITY.toString(),Field.STATE.toString(),Field.ZIP.toString(),Field.PHONE.toString(),
			Field.FAX.toString(),Field.URL.toString(),Field.EMAIL.toString(),Field.CONTACT_PERSON.toString(),Field.TITLE.toString(),Field.ANNUAL_SALES.toString(),Field.EMP_COUNT.toString(),
			Field.YEARS_IN_BIZ.toString(),Field.LATITUDE.toString(),Field.LONGITUDE.toString(),Field.SOURCE_URL.toString(),"Fetching Time"
	};
	private static void extractData() {
		try {
			StringWriter sw=new StringWriter();
			CSVWriter writer=new CSVWriter(sw);
			writer.writeNext(HEADER);
			String sicdetails[]=Sic.sicInfo(sicCode);
			String brancehsHtml=U.getHTML("https://www.fiat.com.mx/distribuidores");
			String branches[]=U.getValues(brancehsHtml, "<a class=\"dealers__b dealers__b--offset-both table dealers__hover js-dealers-item", "</a>");
			U.log(branches.length);
			int i=0;
			for (String branch : branches) {
				U.log(branch);
				branch=StringEscapeUtils.unescapeJava(StringEscapeUtils.unescapeHtml3(branch));
				String name=U.getSectionValue(branch, "title=\"", "\"");
				String state=U.getSectionValue(branch, "\"state\":\"", "\"");
				String phone=U.getSectionValue(branch, "\"tel\":\"", "\"");
				String website=U.getSectionValue(branch, "\"url\":\"", "\"");
				String address=U.getSectionValue(branch, "address\":\"", "\"");
				String lat=U.getSectionValue(branch, "\"lat\":", ",");
				String lng=U.getSectionValue(branch, "\"lng\":", "}");
				U.log(name);
				U.log(address);
				U.log(state);
				U.log(phone);
				U.log(StringEscapeUtils.unescapeJson(website));
				U.log(lat);
				U.log(lng);
				String out[]= {""+(i++),U.toTitleCase(sicdetails[0]),U.toTitleCase(sicdetails[1]),U.toTitleCase(sicdetails[3]),U.toTitleCase(sicdetails[4]),U.toTitleCase(sicdetails[5]),U.toTitleCase("Nuevos Y Usados Concesionarios De Coches Y Camiones"),U.toTitleCase(name),U.toTitleCase(address),"","",U.toTitleCase(state),"",U.formatNumbersAsCode(phone),null,website,"",null,null,null,null,null,lat,lng,"https://www.fiat.com.mx/distribuidores",U.getTodayDate()};
				writer.writeNext(out);
				//break;
			}
			FileUtil.writeAllText(U.getCachePath()+"Fiat_Distributor2.csv", sw.toString());
			sw.close();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
