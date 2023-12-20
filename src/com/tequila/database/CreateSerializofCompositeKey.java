package com.tequila.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.shatam.utils.Path;
import com.shatam.utils.U;

public class CreateSerializofCompositeKey implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8065637774441935549L;

/*	public static void main(String[] args) throws Exception {
		String filePath="/home/mypremserver/RyeProject/Database Csv Files/database1.csv";
		String deserialzefile=Path.CACHE_PATH+"/uniquekeyRYE.ser";
		CreateSerializofCompositeKey ck=new CreateSerializofCompositeKey();
		ck.serializeUniqueKey(filePath);
		
		HashSet<String> hs=ck.deserializeUniqueKey(deserialzefile);
		for (String h : hs) {
			U.log(h);
			break;
		}
		U.log(hs.size());
	}*/
	private void serializeUniqueKey(String filePath) throws IOException{
		List<String> list =Files.readAllLines(java.nio.file.Paths.get(filePath));
		Iterator<String> itr = list.iterator();
		String[] nextLine = null;
		HashSet<String> hashset = deserializeUniqueKey("/home/mypremserver/RyeProject/uniquekeyRYE1.ser");
		while (itr.hasNext()) {
			String line = itr.next();
			nextLine = line.split("\",\"");
			if (nextLine.length != 29)
				continue;
			//String uniqueKey = nextLine[7].trim() + nextLine[8].trim() + nextLine[9].trim() + nextLine[10].trim();
			String uniqueKey = U.toTitleCase(nextLine[7].trim()) + U.toTitleCase(nextLine[8].trim()) + U.toTitleCase(nextLine[10].trim()) + U.toTitleCase(nextLine[11].trim());

			if (!hashset.add(uniqueKey)){
				continue;
			}
		}
		try {
	        FileOutputStream fileOut =new FileOutputStream("/home/mypremserver/RyeProject/uniquekeyRYE1.ser");
	        ObjectOutputStream out = new ObjectOutputStream(fileOut);
	        out.writeObject(hashset);
	        out.close();
	        fileOut.close();
	     } catch (IOException i) {
	        i.printStackTrace();
	     }
	}
	public HashSet<String> deserializeUniqueKey(String fileName)
	{
		HashSet<String> hashset = null;
		try {
	        FileInputStream fileIn = new FileInputStream(fileName);
	        ObjectInputStream in = new ObjectInputStream(fileIn);
	        hashset = (HashSet<String>) in.readObject();
	        in.close();
	        fileIn.close();
	     }  catch (FileNotFoundException c) {
	        System.out.println("no data found");
	        return new HashSet<String>();
	        //c.printStackTrace();
	     }catch (IOException i) {
		        i.printStackTrace();
	     } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (hashset==null) {
			return new HashSet<String>();
		}
		return hashset;
	}
}
