package lab3;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Scanner;

/**
 * Skeleton for an evaluator of the type extraction
 */
public class Evaluator {

	/**
	 * Takes as arguments (1) the gold standard and (2) the output of the type
	 * extraction as a file. Prints to the screen one line with the precision
	 * and one line with the recall.
	 * When computing recall, the denominator should not take into account the
	 * gold standard items with "none".
	 */
	public static void main(String[] args) throws Exception {
		 args = new String[] {
		 "src/lab3/goldstandard-sample.tsv",
		 "src/lab3/result.tsv" };

//						args = new String[] {
//								"goldstandard-sample.tsv", "result.tsv"
//						};

		String line = "";
		BufferedReader br;
		br = new BufferedReader(new FileReader(args[1]));
		int lines = 0;
		int nones = 0;
		int correct = 0;

		Hashtable<String, String> goldStandard = searchInGold(args[0]);
		int goldStandard_lines = goldStandard.size();
		for(String s : goldStandard.keySet()){
			String type = goldStandard.get(s);
			if(type == "none") nones++;
		}

		while((line = br.readLine()) != null) {
			//System.out.println(line);
			lines++;
			String[] words = line.split("\t");
			String title = words[0];
			String foundType; 
			if (words.length==1){
				foundType = "";	 
			}else {
				foundType = words[1];
			}

			String correctType = goldStandard.get(title);

			if(correctType != null && correctType.contains(foundType)) 
				correct++;
		}	

		float precision, recall;
		precision = (float) correct / lines * 100; 
		recall = (float) correct/(goldStandard_lines-nones) * 100;

		System.out.println("Precision: " + precision);		
		System.out.println("Recall: " + recall);

	}

	private static Hashtable<String,String> searchInGold(String path) throws IOException {
		Hashtable ht = new Hashtable();

		String line = "";
		BufferedReader br;
		br = new BufferedReader(new FileReader(path));

		while((line = br.readLine()) != null) {
			String[] words = line.split("\t");
			String title = words[0];
			String correctType = words[1];
			ht.put(title, correctType);	
		}
		return ht;
	}
}
