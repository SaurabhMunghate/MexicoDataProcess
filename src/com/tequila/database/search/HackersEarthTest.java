package com.tequila.database.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class HackersEarthTest {
	public static void main(String[] args) throws NumberFormatException, IOException {
	    int T;
	    try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    T = Integer.parseInt(br.readLine());
	    String input="";
	    if(T>=1 && T<=5) {
	    	for (int i = 0; i < T; i++) {
				int	N = Integer.parseInt(br.readLine());
				int bobArmy[]=new int[N];
				int sBobArmy=0;
				input=br.readLine();
				String arr[]=input.split(" ");
				for (int j = 0; j < arr.length; j++) {
					bobArmy[j]=Integer.parseInt(arr[j]);
					sBobArmy+=Integer.parseInt(arr[j]);
				}
				input=br.readLine();
				int aliceArmy[]=new int[N];
				arr=input.split(" ");
				int saliceArmy=0;
				for (int j = 0; j < arr.length; j++) {
					aliceArmy[j]=Integer.parseInt(arr[j]);
					saliceArmy+=Integer.parseInt(arr[j]);
				}
				if (sBobArmy>saliceArmy) {
					System.out.println("Bob");
				} else if (sBobArmy<saliceArmy) {
					System.out.println("Alice");
				} else {
					System.out.println("Tie");
				}
			}
	    	
	    }
	    
	}
}
