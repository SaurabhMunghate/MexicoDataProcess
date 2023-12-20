package com.chinmay.test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.shatam.utils.U;

public class TestPost {
	public static String formatMillionPricesUpdated(String text) {
		Pattern pattern = Pattern.compile("\\$\\s*\\d\\.?[\\d+]*[Million|million|Mn|M|m]");
	    Matcher matcher = pattern.matcher(text);
	    boolean b=matcher.matches();
	     
	  //System.out.println(matcher.find());
	      
	     while(matcher.find())
	      {
	    	 	  String s1=matcher.group();  
	              Pattern patternnew = Pattern.compile("\\.(\\d*)\\w+");
	              Matcher  matcher1 = patternnew.matcher(s1);
	              String replaceStr = "";
	              int len =0;
	              while(matcher1.find()) {
	            	 U.log(matcher1.group(1)); 
	            	 String val = matcher1.group(1);
	            	 len = val.length();	            	
	              }
	              for(int i=0;i<(6-len);i++) {
//	            		 U.log("0");
	            		 replaceStr += "0";
	              }
	              //U.log(replaceStr);
	              s1 = s1.replaceAll("Million|million|M|m", replaceStr);
	              s1 = s1.replace(".", "");
	              text = text.replace(matcher.group(), s1);
	      }
	     return text;
	} 
	public static void main(String[] args) {
		String str = "House  is priced form  $1.2Million , high  price $1.53M, & low $1million";
		U.log(formatMillionPricesUpdated(str));
//		try {//'RECEIVER_NAME':'Chinmay Wankar',
//			String payload="{'SENDER':'Website_Monitor_Admin@myprem.com','SENDER_NAME':'Website_Monitor_Admin@myprem.com','RECEIVER_ADDRESS':['chinmay.shatam@gmail.com'],'SUBJECT':'Site is down Mail','BODY':'<div id=\":vw\" class=\"a3s aiL \">Site down: <a href=\"https://www.myprem.com\" target=\"_blank\" data-saferedirecturl=\"https://www.google.com/url?q=https://www.myprem.com&amp;source=gmail&amp;ust=1623908662511000&amp;usg=AFQjCNEHn3TRjb7RACvR2kg0Zp9-0wmYow\">https://www.myprem.com</a><img src=\"https://ci4.googleusercontent.com/proxy/lvvciWaDPJO06ckdF0fIZGEILijDy_GvKSIz-aLA0NJ1r88f79Ufzig8ITDwEzPud44Vv67MpujHtI1iCd9-uF35QZyAw_RiGfJc1zyVwUN2UVPjT8OWuxWzabEmeY5E_pnR8n7YqnCX1ZGwXGHCAgCHVVBwMA-eN4wjCDuFchaRaWNIFhq9Z1ikYMPhW2TZUnXcGAmXle8h2W5yZbMZ5q6KxrTmvk0c9hiRtGiy0iB6fLrsr5btXcYRybMoq6xOEKchdeZb-MaxDd78zN0wN1Jdpn4i0auAKyhSq6lufy7ZTEB4PmKjNm1o11bM2WABorMaa9cuXgmaaqSyjUFWsDme0dmeXmY9kOUvzZ06PqoQwpLRugFjqfkSZ0ggxOOYrECEUVvoo7AXSdU5osIQBjdS8OkQZq4WbXcdBGxLlg=s0-d-e1-ft#https://u7347416.ct.sendgrid.net/wf/open?upn=j6NVkGNI045QrKu8lahT-2FHAK1r41gd9hiCPp6Wzov9r-2BE-2B8zlfMLQk6ZTOxCzKxqipOxg9y-2BPyhjz1-2FT997RDjXLo500gX9YQBVqzBieF4FlV1WdyHPk7meEBt1I2xKd-2B4ofbQmTNCUQzkMl0Ah0nek1Y1YpnvORTkxqAG5jdO-2FVRMJhBkE9-2Bd5RsW9xWeGrsWX5GPNTWMloK0YdYnTNF2t8FLs2PZ2fbuBKvNNyT68-3D\" alt=\"\" width=\"1\" height=\"1\" border=\"0\" style=\"height:1px!important;width:1px!important;border-width:0!important;margin-top:0!important;margin-bottom:0!important;margin-right:0!important;margin-left:0!important;padding-top:0!important;padding-bottom:0!important;padding-right:0!important;padding-left:0!important\" class=\"CToWUd\"><div class=\"yj6qo\"></div><div class=\"adL\">','AUTHENTICATION':'8P1SQZeYro5DXpibFn597e6nr7Gz3HYHVhUZQ4RBojU'}";
//			U.log(U.sendPostRequestAcceptJson("https://mail.shatam.com/MailAPI",payload));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	}
}
