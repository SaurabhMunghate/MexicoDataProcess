package com.shatam.utils;

public class GoogleGeoCodeResponse {
	public String status;
	public results[] results;
	public plus_code plus_code;
	
	public GoogleGeoCodeResponse() {
	}
}

class results {
	public String formatted_address;
	public String place_id;
	public geometry geometry;
	public String[] types;
	public String[] postcode_localities;
	public plus_code plus_code;
	public address_component[] address_components;
}
class plus_code {
	public String compound_code;
	public String global_code;
}

class geometry {
	public bounds bounds;
	public String location_type;
	public location location;
	public bounds viewport;
}

class bounds {
	public location northeast;
	public location southwest;
}

class location {
	public String lat;
	public String lng;
}

class address_component {
	public String long_name;
	public String short_name;
	public String[] types;
	
}
