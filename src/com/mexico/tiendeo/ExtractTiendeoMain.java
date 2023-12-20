package com.mexico.tiendeo;

import com.shatam.utils.U;

public class ExtractTiendeoMain {
	static int cou=0;
	//https://www.tiendeo.mx/Ciudades		//All Cities List URL	
	private static String[] getNavLinks(String cityHtml) {
		String navSection=U.getSectionValue(cityHtml, "<ul class=\"s·header__container-links\">", "</ul>");
		navSection=navSection.replaceAll("/Folletos-Catalogos|/ofertas-catalogos|/ofertas-promociones", "");
//		U.log(cityHtml);
		return U.getValues(navSection, "<li>", "</li>");
	}
	public static void main(String[] args) throws Exception {
		String allCititesHtml=U.getPageSource("https://www.tiendeo.mx/Ciudades");
//		String allCitySec=U.getSectionValue(allCititesHtml, "<ul class=\"citiesindex-list list-inline\">", "</ul>");
		String cities[]=U.getValues(allCititesHtml, "<li class=\"index__element typo-btn-13-reg\">", "</li>");
		//String[] cities={"<a href=\"/buenavista\" class=\"blue-link\">Buenavista (Cuauht&#233;moc)</a> "};
		String navlinks[]=getNavLinks(allCititesHtml);
		//int count=0;
		for (String navLink : navlinks) {
//			U.log("put(\""+U.getSectionValue(navLink, "\">", "<").trim()+"\", \""+U.getSectionValue(navLink, "href=\"/", "\"").trim().replace("-", " ")+"\");");
//			if (navLink.contains("hiper-supermercados")) {//Super Market
//				ExtractTiendeoData.addTiendeoData(cities, "5411", navLink);//COMPLETED
//			}
//			if (navLink.contains("tiendas-departamentales")) {//DepartmentalSotres
//				ExtractTiendeoData.addTiendeoData(cities,"5311",navLink);//COMPLETED
//			}
//			else 
//			if (navLink.contains("ropa-zapatos-y-accesorios")) {//Fashion Shops
//				ExtractTiendeoData.addTiendeoData(cities, "5651", navLink);//COMPLETED
//			}
//			else 
//				if (navLink.contains("electronica-y-tecnologia")) //Electronics stores
//					ExtractTiendeoData.addTiendeoData(cities, "4813", navLink);//COMPLETED
//			}
//			else 
//			if (navLink.contains("hogar-muebles")) {//Home Furniture Stores
//				ExtractTiendeoData.addTiendeoData(cities, "5712", navLink);
//			}
//				else 
//			if (navLink.contains("ferreterias-construccion")) {//Hardware Stores
//				ExtractTiendeoData.addTiendeoData(cities, "5251", navLink);
//			}
//				else 
//			if (navLink.contains("juguetes-ninos-y-bebes")) {//Toys&babies Shop     (Can Change depending on Store type)
//				ExtractTiendeoData.addTiendeoData(cities, "5699", navLink);
//			}
//				else 
//				if (navLink.contains("farmacias")) {//Pharmacy
//				ExtractTiendeoData.addTiendeoData(cities, "2834", navLink);
//			}
//			else 
//			if (navLink.contains("perfumerias-y-belleza")) {//Beauty and perfume shops
//				ExtractTiendeoData.addTiendeoData(cities, "2844", navLink);
//			}
//				else 
//			if (navLink.contains("viajes")) {//Travel Agency
//				ExtractTiendeoData.addTiendeoData(cities, "4724", navLink);
//			}
//				else 
//				if (navLink.contains("autos-motos-y-repuestos")) {//Cars, motorcycle and spare parts
//					ExtractTiendeoData.addTiendeoData(cities, "5571", navLink);
//			}
//				else 
//			U.log(navLink);
//			if (navLink.contains("restaurantes")) {//Restaurants
//				ExtractTiendeoData.addTiendeoData(cities, "5812", navLink);
//			}
//				else 
//			if (navLink.contains("librerias-y-papelerias")) {//Book Stores
//				ExtractTiendeoData.addTiendeoData(cities, "5942", navLink);
//			}
//				else 
//			if (navLink.contains("Bancos")) {//Banks							(Can Change depending on bank type)
//				ExtractTiendeoData.addTiendeoData(cities, "6011", navLink);
//			}
//				else 
//			if (navLink.contains("grandes-marcas")) {//Big Brand Stores		(Can Change depending on Store type)
//				ExtractTiendeoData.addTiendeoData(cities, "3229", navLink);
//			}
//				else 
//			if (navLink.contains("deporte")) //Sports 
//				ExtractTiendeoData.addTiendeoData(cities, "5942", navLink);
//			}else if (navLink.contains("bodas")) {//Wedding 							(Can Change depending on bank type)
//				ExtractWeddingShops.addWeddingShops(cities, "6011", navLink);
//			}else 
//				if (navLink.contains("opticas")) {//Big Brand Stores		(Can Change depending on Store type)
//					ExtractTiendeoData.addTiendeoData(cities, "3229", navLink);
//			}
			//break;
		}
	}	
}
