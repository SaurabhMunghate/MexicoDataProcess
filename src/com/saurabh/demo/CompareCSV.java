package com.saurabh.demo;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CompareCSV {
    public static void main(String[] args) {
        String csvFile1 = "/home/shatam-100/Desktop/WaterView_CII/MexicoData_31March2023_5April.csv";
        String csvFile2 = "/home/shatam-100/Desktop/WaterView_CII/MexicoData_31March2023.csv";
        String csvOutputFile = "/home/shatam-100/Desktop/WaterView_CII/MexicoData_31March2023_5April_UpdateCSV.csv";

        Set<String> set1 = new HashSet<String>();
        Set<String> set2 = new HashSet<String>();
        HashMap<String, String> hs = new HashMap<String,String>();
//        Map<String, Integer> hashMap = new HashMap<>();


        try {
            BufferedReader br1 = new BufferedReader(new FileReader(csvFile1));
            BufferedReader br2 = new BufferedReader(new FileReader(csvFile2));

            String line;
            while ((line = br1.readLine()) != null) {
            	System.out.println(line);
            	hs.put(line	,line );
                set1.add(line);
            }

            while ((line = br2.readLine()) != null) {
            	System.out.println(line);
                set2.add(line);
            }

            br1.close();
            br2.close();

            set1.removeAll(set2);

            FileWriter writer = new FileWriter(csvOutputFile);
            for (String s : set1) {
                writer.write(s + "\n");
            }
            writer.close();

            System.out.println("Comparison complete. Non-common rows written to " + csvOutputFile);
            
//            HashMapToCSV();
            

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
