package org.neuroph.imgrec.samples;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Collections;

import com.csvreader.CsvWriter;



public class outputcsv {
	public static void writecsv( TreeMap<String, ArrayList<Double>> imageRecMap, String[] directories , Integer width, Integer height, Integer numlayers, ArrayList<Integer> layers) {

		String outputFile = "C:\\Users\\Yang\\Dropbox\\MDP\\neuroph test result\\neuroph2settest.csv";
		// before we open the file check to see if it already exists
		boolean alreadyExists = new File(outputFile).exists();

		try {
			// use FileWriter constructor that specifies open for appending
			CsvWriter csvOutput = new CsvWriter(new FileWriter(outputFile, true), ',');

			// if the file didn't already exist then we need to write out the header line
			//			if (!alreadyExists){

			csvOutput.write("width");
			csvOutput.write("height");
			csvOutput.write("numlayers");
			csvOutput.write("neurals");
			csvOutput.endRecord();

			csvOutput.write(Integer.toString(width));
			csvOutput.write(Integer.toString(height));
			csvOutput.write(Integer.toString(numlayers));
			for (int i2 = 0; i2 < layers.size(); i2++){
				csvOutput.write(Integer.toString(layers.get(i2)));
			}
			csvOutput.endRecord();


			csvOutput.write("pic_id");
			for (int i = 0; i < directories.length; i++){
				csvOutput.write(directories[i]);
			}
			csvOutput.endRecord();
			//			}
			// else assume that the file already has the correct header line

			// write out a few records

			for (Map.Entry<String, ArrayList<Double>> entry: imageRecMap.entrySet()){
				csvOutput.write(entry.getKey());
				for (int m = 0; m < entry.getValue().size(); m++){
					csvOutput.write(Double.toString((entry.getValue()).get(m)));
				}
				csvOutput.endRecord();
			}
			
			List<Integer> evaList = evaluate(imageRecMap, directories);
			csvOutput.write("Result");
			Integer total = 0;
			for (Integer i3: evaList){
				csvOutput.write(Integer.toString(i3));
				total += i3;
			}
			csvOutput.endRecord();
			
			csvOutput.write("Total");
			csvOutput.write(Integer.toString(total));
			csvOutput.endRecord();
			
			csvOutput.endRecord();
			csvOutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static List<Integer> evaluate(TreeMap<String, ArrayList<Double>> imageRecMap, String[] directories){
		Double percent = 0.5;
		List<Integer> evaList = new ArrayList<Integer>(Collections.nCopies(directories.length, 0));
		for (Map.Entry<String, ArrayList<Double>> entry : imageRecMap.entrySet()) {
			StringTokenizer st = new StringTokenizer(entry.getKey(), "._");
			String imageLabel = st.nextToken();
			for (int i = 0; i < directories.length; i++){
				if (entry.getValue().get(i) > percent && imageLabel.equals(directories[i])) {
					evaList.set(i, evaList.get(i) + 1);
				}else if (entry.getValue().get(i) <= percent && !imageLabel.equals(directories[i])){
					evaList.set(i, evaList.get(i) + 1);
				}
			}
		}
		return evaList;
	}
}
