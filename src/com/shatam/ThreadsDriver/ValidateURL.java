package com.shatam.ThreadsDriver;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class ValidateURL {
	public static WebDriver driver=null;
	private static List<String[]> NameFound = new ArrayList<String[]>();
	private static List<String[]> NotOpening = new ArrayList<String[]>();
	private static List<String[]> InValid = new ArrayList<String[]>();

	public static void main(String args[]) throws Exception {
		UtilURL.setUpChromePath();
		driver = new ChromeDriver();
		ValidateURL obj = new ValidateURL(
				"/home/shatam-22/PallaviWorkspace/MexicoUrlCorrection/validAndInvalidUrls/"+"Dt:24sept2018_SIR_above9500.csv");
		obj.process().printStatatics()
				.collectInvalid(
						"/home/shatam-22/PallaviWorkspace/MexicoUrlCorrection/validAndInvalidUrls/"+"Dt:24Sep2018_SIR_Invalid_9500above.csv")
				.collectNameFound(
						"/home/shatam-22/PallaviWorkspace/MexicoUrlCorrection/validAndInvalidUrls/"+"Dt:24Sep2018_SIR_Valid_7500above.csv")
				.collectNotOpening(
						"/home/shatam-22/PallaviWorkspace/MexicoUrlCorrection/validAndInvalidUrls/"+"Dt:24Sep2018_SIR_NotOpening_7500above.csv");
	}

	String path = "";

	public ValidateURL(String path) {
		this.path = path;
	}

	public ValidateURL process() throws Exception {
		List<String> list = new ArrayList<String>();
		list = Files.readAllLines(Paths.get(path));
		for (String line : list) {
			String lineData[] = line.split(",");
//			if(lineData[1].contains("www.centrotecnologias.mx"))
			validate(lineData[0], lineData[2], lineData[1], UtilURL.getHtml(lineData[1],driver));
		}
		return this;
	}

	public ValidateURL printStatatics() {

		System.out.println("VALID:\t" + NameFound.size());
		System.out.println("NOT-OPENING:\t" + NotOpening.size());
		System.out.println("INVALID:\t" + InValid.size());
		return this;
	}

	public ValidateURL validate(String id, String company_Name, String url, String html) {

		if (isblank(html)) {
			System.out.println("adding invalid");
			InValid.add(new String[] { id, company_Name, url });
		} else if (isNotFound(html)) {
			System.out.println("adding Notopening");

			NotOpening.add(new String[] { id, company_Name, url });
		} else if (isCompany_NameFound(html, company_Name)) {
			System.out.println("adding valid comname");
			NameFound.add(new String[] { id, company_Name, url });
		}
		else if (isLinkFound(url, company_Name)) {
			System.out.println("adding valid url");
			NameFound.add(new String[] { id, company_Name, url });
		} else {
			System.out.println("adding invalid");
			InValid.add(new String[] { id, company_Name, url });
		}
		return this;
	}

	public boolean isLinkFound(String url, String name) {
		String presentWord = "";
		int presentCount = 0;
		System.out.println(name);
		String[] str = name.replaceAll("\\.|\\-|,", " ").split(" ");
		for (String s : str) {
		System.out.println(s+" "+url.contains(s.toLowerCase()));
			if (url.contains(s.toLowerCase()) && s.length()>2) {
				presentCount++;
				presentWord = s;
			}
			if (presentCount > 0) {
				return true;
			}
		}
		
		return false;

	}

	public boolean isCompany_NameFound(String html, String company_name) {
		String[] str = company_name.replaceAll("\\.|\\-|,", " ").split(" ");
		int length1 = 0, p = 0, j = 0;
		for (int k = 0; k < str.length; k++) {
			str[k] = str[k].toLowerCase().trim();
			if (str[k].length() <= 2) {
				p++;
				continue;
			}
			if (html.toLowerCase().contains(str[k])) {
				j++;
				System.out.println("matches:"+str[k]+" "+j);
			}
		}
		length1 = str.length - p;
		System.out.println(length1+":::::::::::::"+j);
		if (length1 == j) {
			return true;
		}
		return false;
	}

	public String[] getComName(String url) throws IOException {

		String[] arr = { "ABCD", "2" };
		return arr;
	}

	public boolean isblank(String html) {

		if (html == null) {
			return true;
		}

		if (html.length() < 20) {
			return true;
		}

		return false;
	}

	private static ArrayList<String> NOTFOUNDPAGES = new ArrayList<String>() {
		{
			add("the domain name does not exist");
			add("your new domain");
			add("server error");
			add("this domain isn't connected to a website yet");
			add("account suspended");
			add("page not found");
			add("504 gateway time-out");
			add("sitio suspendido");
			add("your domain registrar");
			add("no route to host");
			add("<h2>not found</h2>");
			add("page cannot be displayed");
			add("this site can’t be reached");
			add("err_connection_timed_out");
			add(" forbidden ");
			add("own this domain today");
			add("was not found on");
			add("the requested url could not be retrieved");
			add("the requested resource is not found");
			add("error 404");
			add("404 not found");
			add("this page is under construction");
			add("buy this domain");
			add("it's a great label for your website");
			add("is for sale");
			add("<h1>index of /</h1>");
			add("<h1>forbidden</h1>");
			add("click+here+to+renew+it");
			add("connect your domain to your website");
			add("site not found.");
			add("access denied");
			add("this account has been suspended");
			add("pagina suspendida");
			add("<h3>web site not found</h3>");
			add("webpage not available");
			add("microsoft internet information services");
			add("página en construcción");//PáGINA EN CONSTRUCCIóN//PAGINA SUSPENDIDA
			add("SITIO EN CONSTRUCCIÓN".toLowerCase());
			add("may be for sale");
			add("cuenta restringida");
			add("free web hosting");
			add("domain for sale");
			add("acceso denegado");
			add("domain name expired");
			add("504 error");
			add("404 error");
			add("web page is parked for free");
			add("register or transfer your domain name");
			add("create a logo for");
			add("<h1>en mantenimiento</h1>");
			add("servicio de web hosting ha sido activado");
			add("could not connect to mysql.database");
			add("this domain has recently been listed");
			add("the page you have entered does not exist");
			add("the ip address has changed.");
			add("upload your website to get started.");
			add("nuestra página está bajo construcción.");
			add("index of /pagina");
			add("NO DISPONIBLE TEMPORALMENTE".toLowerCase());
			add("create your own stunning website");
			add("this domain name expired");
			add("account disabled by server administrator");
			add("is currently unable to handle this request");
			add("this account is currently unavailable");
			add("bad gateway");
			add("<body>not found ");
			add("website is not available");
			add("este sitio web ya no está disponible");
			add("Pageserver 404");
			add("create domains and set up web hosting");
			add("algo no está funcionando con tu página");
			add("The website encountered an unexpected error");
			add("<h1>403 forbidden</h1>");
			add("jimdo - pages to the people");
			add("web hosting - courtesy of www.bluehost.com");
			add("bandwidth limit exceeded");
			add(">forbidden<");
			add("database error");
			add("blog not found");
		}
	};

	public boolean isNotFound(String html) {
		for (String a : NOTFOUNDPAGES) {
			if (html.toLowerCase().contains(a)) {
				return true;
			}
		}
		return false;
	}

	private void writeData(String path, List<String[]> list) throws IOException {

		OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8);

		for (String data[] : list) {
			System.out.println(data[1]);
			writer.write("\"" + data[0] + "\",\"" + data[1] + "\",\"" + data[2] + "\"" + System.lineSeparator());
		}
		writer.close();
	}

	public ValidateURL collectNameFound(String path) throws IOException {
		writeData(path, NameFound);
		return this;
	}

	public ValidateURL collectNotOpening(String path) throws IOException {
		writeData(path, NotOpening);
		return this;
	}

	public ValidateURL collectInvalid(String path) throws IOException {
		writeData(path, InValid);
		return this;
	}

}
