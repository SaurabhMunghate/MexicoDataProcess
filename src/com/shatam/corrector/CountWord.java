package com.shatam.corrector;



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

import com.shatam.utils.Path;
import com.shatam.utils.U;

public class CountWord {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub

		int count=0;
		HashSet<Integer> hs=new HashSet();
        // This will reference one line at a time
        String line = null;

        try {
            // FileReader reads text files in the default encoding.
            FileReader fileReader = new FileReader(Path.DB_CACHE+"octoberDataset.tsv");
            
 //           FileReader fileReader = new FileReader("/home/glady/testdemo/Tequila/database/duplicate_composite_data.txt");
            // Always wrap FileReader in BufferedReader.

            int count_24=0,count_23=0,count_22=0,count_21=0,count_20=0,count_19=0,count_18=0,count_17=0,count_16=0,count_15=0,count_14=0;

            int latLngCount=0;
            BufferedReader bufferedReader =  new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
    
            	count++;
               line=line.replaceAll("\t","shatam@");
               String[] s=line.split("shatam@");
             //   U.log(" Count :-- "+count);
                hs.add(s.length);
                
               int leg=s.length;
                if(leg==24){
                	count_24++;
                	if(!s[22].trim().isEmpty()){
                		latLngCount++;
                	}
                }
                if(leg==23){
                	count_23++;                	
                }
                if(leg==22){
                	count_22++;                	
                }
                if(leg==21){
                	count_21++;                	
                }
                if(leg==20){
                	count_20++;                	
                }
                if(leg==19){
                	count_19++;                	
                }
                if(leg==18){
                	count_18++;                	
                }
                if(leg==17){
                	count_17++;                	
                }
                if(leg==16){
                	count_16++;                	
                }
                if(leg==15){
                	count_15++;                	
                } 
                if(leg==14){
                	count_14++;                	
                }
            }   

            // Always close files.
            bufferedReader.close();         
            U.log("Count 24 of === "+count_24);
            U.log("Count 23 of === "+count_23);
            U.log("Count 22 of === "+count_22);
            U.log("Count 21 of === "+count_21);
            U.log("Count 20 of === "+count_20);
            U.log("Count 19 of === "+count_19);
            U.log("Count 18 of === "+count_18);
            U.log("Count 17 of === "+count_17);
            U.log("Count 16 of === "+count_16);
            U.log("Count 15 of === "+count_15);
            U.log("Count 14 of === "+count_14);
            U.log("LatLong found at 24 field ::"+latLngCount);

        }
        catch(IOException ex) {
        	ex.printStackTrace();
        }
        U.log("total count::"+count);
		
        for(int num : hs)
        	U.log(num);

	}
	
	

}
