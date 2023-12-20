package com.shatam.lightbox.loqate;

public class LoqateHelper {
	String Key;
	boolean Geocode;
	Addresses[] Addresses;
	public LoqateHelper(String key, boolean Geocode, Addresses[] Addresses) {
		this.Addresses=Addresses;
		this.Key=key;
		this.Geocode=Geocode;
	}
	public String getKey() {
		return Key;
	}
	public void setKey(String key) {
		this.Key = key;
	}
	public boolean isGeocode() {
		return Geocode;
	}
	public void setGeocode(boolean Geocode) {
		this.Geocode = Geocode;
	}
	public Addresses[] getAddress() {
		return Addresses;
	}
	public void setAddress(Addresses[] Addresses) {
		this.Addresses = Addresses;
	}
}
class Addresses {
	String Address,Address1,Address2,Address3,Address4,Address5,Address6,Address7,Address8,SuperAdministrativeArea,AdministrativeArea,SubAdministrativeArea,Locality;
	String DependentLocality,DoubleDependentLocality,Thoroughfare,DependentThoroughfare,Building,Premise,SubBuilding,PostalCode,Organization,PostBox;
	final String Country="MEX";
	public String getAddress1() {
		return Address1;
	}
	public void setAddress1(String address1) {
		this.Address1 = address1;
	}
	public String getAddress() {
		return Address;
	}
	public void setAddress(String address) {
		Address = address;
	}
	public String getAddress2() {
		return Address2;
	}
	public void setAddress2(String address2) {
		Address2 = address2;
	}
	public String getAddress3() {
		return Address3;
	}
	public void setAddress3(String address3) {
		Address3 = address3;
	}
	public String getAddress4() {
		return Address4;
	}
	public void setAddress4(String address4) {
		Address4 = address4;
	}
	public String getAddress5() {
		return Address5;
	}
	public void setAddress5(String address5) {
		Address5 = address5;
	}
	public String getAddress6() {
		return Address6;
	}
	public void setAddress6(String address6) {
		Address6 = address6;
	}
	public String getAddress7() {
		return Address7;
	}
	public void setAddress7(String address7) {
		Address7 = address7;
	}
	public String getAddress8() {
		return Address8;
	}
	public void setAddress8(String address8) {
		Address8 = address8;
	}
	public String getSuperAdministrativeArea() {
		return SuperAdministrativeArea;
	}
	public void setSuperAdministrativeArea(String superAdministrativeArea) {
		SuperAdministrativeArea = superAdministrativeArea;
	}
	public String getAdministrativeArea() {
		return AdministrativeArea;
	}
	public void setAdministrativeArea(String administrativeArea) {
		AdministrativeArea = administrativeArea;
	}
	public String getSubAdministrativeArea() {
		return SubAdministrativeArea;
	}
	public void setSubAdministrativeArea(String subAdministrativeArea) {
		SubAdministrativeArea = subAdministrativeArea;
	}
	public String getLocality() {
		return Locality;
	}
	public void setLocality(String locality) {
		Locality = locality;
	}
	public String getDependentLocality() {
		return DependentLocality;
	}
	public void setDependentLocality(String dependentLocality) {
		DependentLocality = dependentLocality;
	}
	public String getDoubleDependentLocality() {
		return DoubleDependentLocality;
	}
	public void setDoubleDependentLocality(String doubleDependentLocality) {
		DoubleDependentLocality = doubleDependentLocality;
	}
	public String getThoroughfare() {
		return Thoroughfare;
	}
	public void setThoroughfare(String thoroughfare) {
		Thoroughfare = thoroughfare;
	}
	public String getDependentThoroughfare() {
		return DependentThoroughfare;
	}
	public void setDependentThoroughfare(String dependentThoroughfare) {
		DependentThoroughfare = dependentThoroughfare;
	}
	public String getBuilding() {
		return Building;
	}
	public void setBuilding(String building) {
		Building = building;
	}
	public String getPremise() {
		return Premise;
	}
	public void setPremise(String premise) {
		Premise = premise;
	}
	public String getSubBuilding() {
		return SubBuilding;
	}
	public void setSubBuilding(String subBuilding) {
		SubBuilding = subBuilding;
	}
	public String getPostalCode() {
		return PostalCode;
	}
	public void setPostalCode(String postalCode) {
		PostalCode = postalCode;
	}
	public String getOrganization() {
		return Organization;
	}
	public void setOrganization(String organization) {
		Organization = organization;
	}
	public String getPostBox() {
		return PostBox;
	}
	public void setPostBox(String postBox) {
		PostBox = postBox;
	}
	public String getCountry() {
		return Country;
	}
	//6000
	//4500
	//600
	public Addresses(String street,String neigh,String city,String state,String zip) {
		this.Address=street +", "+neigh+", "+city+", "+zip;
//		this.Address=street +", "+neigh+", "+city+", "+state;
//		this.Address=street +", "+neigh+", "+zip +" "+state;
//		this.Address=street +", "+neigh+", "+city+", "+zip;
	//	this.Address=street +", "+neigh+", "+zip;
	//	this.Address=street +", "+neigh+", "+zip;
	//	this.Address=street +", "+city+", "+zip+" "+state;
		this.Address1=street;
		this.Address2="";
		this.Address3="";
		this.Address4="";
		this.Address5="";
		this.Address6="";
		this.Address7="";
		this.Address8="";
		this.SuperAdministrativeArea="";
//		this.AdministrativeArea=state;
		this.AdministrativeArea="";
		this.SubAdministrativeArea="";
		this.Locality=city;
//		this.Locality="";
		this.DependentLocality=neigh;
	//	this.DependentLocality="";
		this.DoubleDependentLocality="";
		this.Thoroughfare="";
		this.DependentThoroughfare="";
		this.Building="";
		this.Premise="";
		this.SubBuilding="";
		this.PostalCode=zip;
//		this.PostalCode="";
		this.Organization="";
		this.PostBox="";
	}
}