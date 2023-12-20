package com.saurabh.demo;
import java.util.regex.*;

public class AddressValidator {
    public static boolean validateAddress(String address) {
        String regex = "^([A-Za-z0-9-]+\\s?)+,\\s*\\d+\\s+[A-Za-z]+\\s+[A-Za-z]{2},\\s*\\d{5}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(address);
        return matcher.matches();
    }

    public static void main(String[] args) {
        String address = "Taqueria Guadalajara Mexican Restaurant, 1033 South Park St, Madison, WI, 53715  ";
        if (validateAddress(address)) {
            System.out.println("The address is valid.");
        } else {
            System.out.println("The address is not valid.");
        }
    }
}
