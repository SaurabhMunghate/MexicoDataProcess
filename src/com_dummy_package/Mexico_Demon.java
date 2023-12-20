package com_dummy_package;

import java.util.Arrays;

import com.shatam.scrapper.SplitNeighborhoodFromAdd;
import com.shatam.utils.U;

public class Mexico_Demon {

	public static void main(String[] args) {
		
		Mexico_Demon demon = new Mexico_Demon();
		
		String address = "Blvd. Agua Caliente No. 10307";
		String[] add = SplitNeighborhoodFromAdd.splitColonia(address);
		U.log("add: "+Arrays.toString(add));
		
		String[] addhere = SplitNeighborhoodFromAdd.findNeighborhoodFromAddress(address, "TIJUANA");
		U.log("addhere: "+Arrays.toString(addhere));
	}

}
