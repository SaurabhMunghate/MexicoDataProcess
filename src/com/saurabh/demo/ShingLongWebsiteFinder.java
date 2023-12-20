package com.saurabh.demo;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ShingLongWebsiteFinder {
   public static void main(String[] args) {
      String query = "Restaurantes	Shing Long	Avenida Revolución 3592	Nuevo León	Monterrey	Nuevo Leon	64850";
      String charset = "UTF-8";
      String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36";

      try {
         URL url = new URL("https://www.google.com/search?q=" + query);
         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setRequestProperty("User-Agent", userAgent);
         connection.setRequestProperty("Accept-Charset", charset);
         connection.setRequestMethod("GET");
         connection.setConnectTimeout(5000);
         connection.setReadTimeout(5000);

         BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
         String line;
         while ((line = in.readLine()) != null) {
            if (line.contains("https://shinglong.com.mx/")) {
               System.out.println("The website of Shing Long restaurant is: https://shinglong.com.mx/");
               break;
            }
         }
         in.close();
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
