package com.tequila.process;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;  
import com.opencsv.CSVReader;
import com.shatam.utils.U;
public class RowDataProcess {
	
	public static void main(String[] args) throws IOException
    {
		RowDataProcess process = new RowDataProcess();
//		FileWriter writer=new FileWriter("/home/shatam12/Downloads/write/state.json");
		FileWriter writer =  new FileWriter(new File(RowDataProcess.class.getResource("state_cities.json").getPath()));
		
/*		InputStream res = RowDataProcess.class.getResourceAsStream("/state_cities.json");
		
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new InputStreamReader(res));
		    String line = null;
		    while ((line = reader.readLine()) != null) {
//		    	U.log(line);
		    	line = line.replaceAll("\"", "");
		        for(String index : indexBuilder){
		        	if(index.equals(line.split(",")[0])){
		        		builderListFromFile.add(line);
		        	}
		        }
		    }
		}catch(IOException ioe){
			ioe.printStackTrace();
		}finally{
			try{
				reader.close();
			}catch(IOException ioe){}
		}*/
		process.csvReader();
		process.writeJson(writer);
		writer.flush();
		writer.close();
	}

	String path="/home/shatam12/Downloads/unique/Munipalities List.csv";	
	ConcurrentHashMap<String, List<String>> StateCitiesMap = new ConcurrentHashMap<String,List<String>>();
	List<String[]> data=new ArrayList<String[]>();
	
	HashMap obj=new HashMap();
	public static int k=0;	 
	//String STATES,CITIES="";
    
	
	public void csvReader() throws IOException
	{
		//jsonobj.put("STATE", "CITIES");
		
		
		//writer.write("STATE,"+"CITY");
		CSVReader reader = new CSVReader(new FileReader(path));
		String [] nextLine;
		int count=0;
		int z=0;
		int x=0;
		
		Set<String> states = new HashSet<String>();
		while ((nextLine = reader.readNext()) != null) {
			                                             if(z==0){
				                                                  z++;
				                                                  continue;
			                                                     }
		       data.add(new String[]{nextLine[1],nextLine[2],nextLine[3]});
		       states.add(nextLine[3]);
		       count++;
		}//your while loop close here.
		U.log(count);
				
		for (String state:states)
		{
			getNewData(state,data);
			k++;			
		}
		U.log("StateCount=="+k);
		
			
		for (Map.Entry<String,List<String>> entry : StateCitiesMap.entrySet())
		 {
			
			if(U.isState(entry.getKey()))
			{
				String newState=U.matchState(entry.getKey());
				if(newState.equals(entry.getKey()))
				{
	//				U.log("my keys=="+entry.getKey());
			//		U.log("my keys=="+entry.getKey()+"\tcities=="+entry.getValue());
				}
				else 
				{
									
					//U.log("key="+entry.getKey()+"\t\tmatching=="+newState);
					getUpdatedKeys(entry.getKey(),newState);
					//writer.write(entry.getKey()+""+newState);	
				  
				    //U.log("after remove key="+entry.getKey()+"\t\tmatching=="+newState);
				}
			}
			else
			{
				//System.err.println("hiiiiiiii===="+entry.getKey());
			}
			
		 }//Your for loop ends here.
		//writer.write(jsonobj.toJSONString());
		/*String jsonV=JSONValue.toJSONString(obj);
		writer.write(jsonV);
		writer.flush();
		writer.close();///filewriter object close here.
*/		
		
	}
	
	public void getNewData(String State,List<String[]> data)
	{
		int c=0;
		String State1="";
		
		List<String> data1=new ArrayList<String>();		
		for(String []city:data)
		{
			if(State.equals(city[2]))
			{				
				data1.add(city[1]);
			}
			
		}
		
		StateCitiesMap.put(State,data1);
	}//Your method ends here.
	
	public void getUpdatedKeys(String oldKey,String state) throws IOException
	{
		
		Iterator<String> it = StateCitiesMap.keySet().iterator();
		   while (it.hasNext())
		   {
		      String key = it.next();
		      //U.log(key);
		      if(oldKey.equals(key)){
		    	                     //String getS=StateCitiesMap.get(oldKey);
		    	                     StateCitiesMap.put(state, StateCitiesMap.get(oldKey));
		    	                     it.remove();
		    	                     //U.log("Remove key=="+key);		    	                     
		                            }
		      
           }//while loop ends here.
	
		
    }//your method ends here.
	
	@SuppressWarnings("unchecked")
	public void writeJson(FileWriter writer) throws IOException
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
	}

}
