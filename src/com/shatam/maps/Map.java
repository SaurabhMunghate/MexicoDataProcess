package com.shatam.maps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public interface Map {
	
	String[] getLatLong(String[] address);
	String[] getAddress(String[] latLong);
	
	@SuppressWarnings("unchecked")
	public static final List<String> statusCodeList = new ArrayList(){
		{
			add("INVALID_REQUEST"); //indicates that the provided request was invalid.
			add("MAX_ELEMENTS_EXCEEDED"); //indicates that the product of origins and destinations exceeds the per-query limit
			add("OVER_DAILY_LIMIT"); //The API key is missing or invalid/A self-imposed usage cap has been exceeded
			add("OVER_QUERY_LIMIT"); //indicates the service has received too many requests from your application within the allowed time period.
			add("REQUEST_DENIED"); //indicates that the service denied use of the Distance Matrix service by your application.
			add("UNKNOWN_ERROR"); //indicates a Distance Matrix request could not be processed due to a server error. The request may succeed if you try again.
		}
	};
	
	@SuppressWarnings("unchecked")
	public static final java.util.Map<String, String> statusCodeMap = new HashMap(){
		{
			put("OK",				"Valid Result");  //indicates the response contains a valid result.
			put("NOT_FOUND",		"Pairing Could Not Be Geocoded"); // indicates that the origin and/or destination of this pairing could not be geocoded.
			put("ZERO_RESULTS",		"No Route Could Be Found"); //indicates no route could be found between the origin and destination.
			put("MAX_ROUTE_LENGTH_EXCEEDED",	"Route Is Too Long"); //indicates the requested route is too long and cannot be processed.
		}
	};
	
	@SuppressWarnings("unchecked")
	public static final List<String> keyList = new ArrayList(){
		{
			add("AIzaSyB47TwCX7ymRK5_gyWcNXX_f7qA7GIVwnk");
			add("AIzaSyAFbOdK6IQuxWRKj7qTr8pWYxVtzokZC-s");
			add("AIzaSyBmsZ3YkZy-4-4lF__4qTX8UXVIitJM8uQ"); //sawan
//			add("AIzaSyCd3RyrftCYt6qyDfxT9-Zv2UEuDB3zIsE"); //upendra
		}
	};
}

