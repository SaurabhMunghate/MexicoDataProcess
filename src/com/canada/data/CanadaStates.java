package com.canada.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CanadaStates {

    @SuppressWarnings("unchecked")
	private static final Map<String, String> stateAbbrMap = new HashMap(){
		{
    		put("Alberta","AB");
	    	put("British Columbia","BC");
	    	put("Manitoba","MB");
	    	put("New Brunswick","NB");
	    	put("Newfoundland and Labrador","NL");
//	    	put("Newfoundland","NL");
//	    	put("Labrador","NL");
	    	put("Nova Scotia","NS");
	    	put("Northwest Territories","NT");
	    	put("Nunavut","NU");
	    	put("Ontario","ON");
	    	put("Prince Edward Island","PE");
	    	put("Quebec","QC");
	    	put("Saskatchewan","SK");
	    	put("Yukon","YT");
    	}
    };

    public static String getStateFromAbbr(String inputState){
        for (String state: stateAbbrMap.keySet()){
            if (state.trim().toLowerCase().equalsIgnoreCase(inputState.trim().toLowerCase())) return stateAbbrMap.get(state);
        }
    	return null;
    }
}
