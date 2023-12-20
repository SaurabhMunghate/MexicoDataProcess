package com.shatam.corrector;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.shatam.utils.U;

public class SimilarityTool {


	/*
	 * String Comparison Algorithm based on Character Pair Similarity
	 */

		private SimilarityTool() {

		}

		public static void main(String []  args){
			
			String  add =  "Grain And Seed Milling, And Fats And Oils Manufacturing"; //"38.9613361"; //"1016 MAKEWAY STREET";
	//2170 W Elm Terrace
			String add1 = "Flour and Other Grain Mill Products"; // "38.9613120136";// "1016 MAKEWAY ST";
	//2605 W Loula Street		
			
			String percent = compareStrings(add, add1);


			//U.isState("");
			U.log("Match in Percent % ::"+percent);
		}
		
		/*
		 * Compares the two strings based on letter pair matches
		 * Return the percentage match from 0.0 to 1.0 where 1.0 is 100%
		 */
	    public static String compareStrings(String str1, String str2){
	        List<String> pairs1 = wordLetterPairs(str1.toUpperCase());
	        List<String> pairs2 = wordLetterPairs(str2.toUpperCase());

	        int interSection = 0;
	        int union = pairs1.size() + pairs2.size();

	        for (int i = 0; i < pairs1.size(); i++){
	        	Object pair1=pairs1.get(i);
	            for (int j = 0; j < pairs2.size(); j++){
	            	Object pair2=pairs2.get(j);
	               	if (pair1.equals(pair2)) {
	                    interSection++;
	                    pairs2.remove(j);//Must remove the match to prevent "GGGG" from appearing to match "GG" with 100% success
	                    break;
	                }
	            }
	        }
	        double percent = ((2.0 * interSection) / union)*100;

			DecimalFormat df = new DecimalFormat(".##");

			if(df.format(percent).equals(".0")){
				return "0";
			}
	        return df.format(percent);
	    }

	    /*
	     * Gets all letter pairs for each individual word in the string
	     */
	    private static List<String> wordLetterPairs(String str){
	        List<String> allPairs = new ArrayList<String>();

	        // Tokenize the string and put the tokens/words into an array
	        String[] words = str.split("\\s");

	        // For each word
	        for (int w = 0; w < words.length; w++){
	            if (words[w] != null || !words[w].isEmpty()){
	                // Find the pairs of characters
	                String[] pairsInWord = letterPairs(words[w]);

	                for (int p = 0; p < pairsInWord.length; p++){
	                    allPairs.add(pairsInWord[p]);
	                }
	            }
	        }
	        return allPairs;
	    }

	    /*
	     * Generates an array containing every two consecutive letters in the input string
	     */
	    private static String[] letterPairs(String str){
	        int numPairs = str.length()-1;
	        String[] pairs = new String[numPairs];
	        for (int i = 0; i < numPairs; i++){
	  			pairs[i] = str.substring(i, i+2);
//	          U.log(pairs[i]);
	        }
	        return pairs;
	    }

}
