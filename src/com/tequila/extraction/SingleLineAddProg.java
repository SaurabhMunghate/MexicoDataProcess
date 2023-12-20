package com.tequila.extraction;

import com.shatam.utils.U;

public class SingleLineAddProg {
	public static void main(String[] args) {
		System.out.println("SUM: "+(args[0]+args[1]));
		int a=10,b=20;
		a=a+b;
		b=a-b;
		a=a-b;
		U.log(a+"   "+b);
	}
}
