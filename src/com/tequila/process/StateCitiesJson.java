package com.tequila.process;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.simple.parser.ParseException;

import com.shatam.utils.U;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContentHandler;
import org.json.simple.parser.JSONParser;

public class StateCitiesJson {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StateCitiesJson process = new StateCitiesJson();
		process.readJsonFile();
	}
	
	Map<String, List<String>> data =  new HashMap<String, List<String>>();
	
	public void readJsonFile() {
		JSONParser parser = new JSONParser();
		InputStream res = RowDataProcess.class.getResourceAsStream("/state_cities.json");
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(res));
			String line = null;
			int i = 0;
			while((line = reader.readLine()) != null){
				U.log(line);
				i++;
			}
//			String json = JSONObject.escape((String) parser.parse(reader));
			Object obj = parser.parse(reader);
			JSONObject jsonObject = (JSONObject)obj;
		
			List<JSONObject> list = (List<JSONObject>) jsonObject.get("result");
			for(JSONObject jsonobj : list){
				String state = (String) jsonobj.get("STATE");
				List<String> cities = (List<String>) jsonobj.get("CITIES");
				data.put(state, cities);
			}
            
		}catch(IOException | ParseException e){
			e.printStackTrace();
		}finally {
			try{
				reader.close();
				res.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}		
		U.log("Size of map:::"+data.size());
	}
	
/*	public void writeJson(FileWriter writer) throws IOException
	{
		JSONObject outer=new JSONObject();
		List<JSONObject> list  = new ArrayList<JSONObject>();
		for ( Entry<String,List<String>> entry : StateCitiesMap.entrySet()) {
			JSONObject jsonobj=new JSONObject();
		    
			String key = (String) entry.getKey();
		    List<String> values = (List<String>) entry.getValue();

		    jsonobj.put("STATE",key);
		    jsonobj.put("CITIES",values);
		    
		    list.add(jsonobj);
		    
		    U.log(jsonobj.toString());
		}
		outer.put("result", list);
		writer.write(outer.toJSONString());
    }*/
}
