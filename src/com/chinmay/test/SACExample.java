package com.chinmay.test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.json.simple.JSONObject;

import oauth.signpost.http.HttpResponse;

public class SACExample {
	public static void main(String[] args) {
		String input="[[\"1\",\"1333 Antietam Dr\",\"\",\"Columbus\",\"GA\",\"31907\"]]";
		SACExample sac=new SACExample();
		String out=sac.httprequestToSac(input);
		System.out.println(out);
	}
	public String httprequestToSac(String addresses) {
		int outPutCount = 1; // No of output expected min 1 max 3
		String data = addresses;
		HttpClient client = new DefaultHttpClient();
		HttpConnectionParams.setConnectionTimeout(client.getParams(), 1000);
		HttpResponse response;
		JSONObject json = new JSONObject();

		try {
			HttpPost post = new HttpPost("http://64.227.39.117:3309/postData/");//Url with default port 3309
			json.put("address", data.toString());
			json.put("count", outPutCount);
			json.put("jobs", "23");
			json.put("data", "USPS");//USPS or TIGER or USPS and TIGER or TIGER and USPS
			json.put("log", "Disable");
			json.put("city_weight", ""); //default are set to 4
			json.put("zip_weight", ""); //default are set to 4

			StringEntity se = new StringEntity(json.toString());
			se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
					"application/json"));
			post.setEntity(se);
			response = (HttpResponse) client.execute(post);

			if (response != null) {
				InputStream in = ((HttpEntityEnclosingRequestBase) response).getEntity().getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				StringBuilder out = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					out.append(line);
				}
				// System.out.println("OUTPUT=" + out.toString());
				reader.close();

				return out.toString();
			}

		} catch (Exception e) {
			e.printStackTrace();

		}
		return null;
	}
}
