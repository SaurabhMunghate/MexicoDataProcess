package com.shatam.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

public class CAStates {
	public static String abbr(String name){
        name = name.trim().toLowerCase();
        for (String objname: map.keySet()){
            String sname =objname.trim().toLowerCase();
            if (sname.equalsIgnoreCase(name)) return map.get(objname);
        }
        return null;
    }

    public static Set<String> getAllFull(){
        return map.keySet();
    }
    public static Collection<String> getAllAbbr(){
        return map.values();
    }

    private static HashMap<String, String> map = new HashMap<String, String>();
    static {
    	map.put("Alberta","AB");
    	map.put("British Columbia","BC");
    	map.put("Manitoba","MB");
    	map.put("New Brunswick","NB");
    	map.put("Newfoundland and Labrador","NL");
    	map.put("Newfoundland","NL");
    	map.put("Labrador","NL");
    	map.put("Nova Scotia","NS");
    	map.put("Northwest Territories","NT");
    	map.put("Nunavut","NU");
    	map.put("Ontario","ON");
    	map.put("Prince Edward Island","PE");
    	map.put("Quebec","QC");
    	map.put("Saskatchewan","SK");
    	map.put("Yukon","YT");
        
    }
}
