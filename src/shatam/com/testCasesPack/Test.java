package shatam.com.testCasesPack;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.iterators.EntrySetMapIterator;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.shatam.utils.U;

public class Test {

	static HashMap<String, String[]> map1 = new HashMap<>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		U.log("hello");
		/*CSVReader read1 = new CSVReader(
				new FileReader("/home/shatam-20/Downloads/CanadaCitiesData_OnlyCity.csv"));*/
		CSVReader read2 = new CSVReader(new FileReader("/home/shatam-20/Downloads/StateLatLon2.csv"));
		CSVWriter write = new CSVWriter(new FileWriter("/home/shatam-20/Downloads/StateLatLon.csv"));
		U.log("hell1");
		int count = 0;
		/*for (String[] data : read1) {
			String key = data[1] + data[2];
			U.log(key);
			count++;
			map1.put(key, data);
		}*/

		U.log("First File Done....");
		for (String[] data : read2) {
			String key = data[1] + data[2];
			map1.put(key, data);
			count++;
		}

		Set st = (Set) map1.entrySet();
		Iterator it = st.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Entry) it.next();
			// System.out.println(entry.getKey() + " : " + entry.getValue());
			String[] data = (String[]) entry.getValue();
			write.writeNext(data);
			/*if (data.length < 5) {
				String[] data1= {"",data[1],data[2],"","","","","",""};
				write.writeNext(data1);
			} else {
				write.writeNext(data);
			}*/
		}
		write.close();
		U.log(map1.size() + "\t" + count);
	}

}
