package com.chinmay.test;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.shatam.utils.ApiKey;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * @author Chinmay
 */
public class PlacesService {
//	private static final String GOOGLE_API_KEY  = "***";

    private final HttpClient client = new DefaultHttpClient();

    public static void main(final String[] args) throws ParseException, IOException, URISyntaxException
    {
        new PlacesService().performSearch("banks", -102.2906, 21.87879);
    }

    public void performSearch(final String types, final double lon, final double lat) throws ParseException, IOException, URISyntaxException
    {
        final URIBuilder builder = new URIBuilder().setScheme("https").setHost("maps.googleapis.com").setPath("/maps/api/place/nearbysearch/json");

        builder.addParameter("location", lat + "," + lon);
        builder.addParameter("radius", "5");
        builder.addParameter("types", types);
        builder.addParameter("&rankby", "distance");
        builder.addParameter("sensor", "true");
        builder.addParameter("key", ApiKey.GOOGLEAPIKEYS[0]);

        final HttpUriRequest request = new HttpGet(builder.build());

        final HttpResponse execute = this.client.execute(request);

        final String response = EntityUtils.toString(execute.getEntity());

        System.out.println(response);
    }
}
