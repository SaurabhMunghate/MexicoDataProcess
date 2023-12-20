package com.saurabh.demo;
import java.io.*;

public class FileProcessor {
    public static void main(String[] args) {
        String inputFile = "/home/shatam-100/Cache/temp.csv";
        String outputFile = "/home/shatam-100/Cache/Untitled 1.csv";

        try {
            File input = new File(inputFile);
            File output = new File(outputFile);

            BufferedReader reader = new BufferedReader(new FileReader(input));
            BufferedWriter writer = new BufferedWriter(new FileWriter(output));

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.length() >= 12) {
                    writer.write(line);
                    writer.newLine();
                }else {
                	System.out.println("remove"+ line);
                	writer.write("");
                    writer.newLine();
				}
            }

            reader.close();
            writer.close();

            System.out.println("File processing complete. Output file: " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
