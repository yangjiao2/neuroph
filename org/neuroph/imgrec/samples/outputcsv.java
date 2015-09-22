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
	public static Integer writecsv( TreeMap<String, ArrayList<Double>> imageRecResultMap, String[] directories , String outputFile, Integer width, Integer height, Integer numlayers, ArrayList<Integer> layers, double learningrate, double maxerror, double momentum) {
		Integer cornum = 0;

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
			csvOutput.write("learningrate");
			csvOutput.write("maxerror");
			csvOutput.write("momentum");
			csvOutput.endRecord();

			csvOutput.write(Integer.toString(width));
			csvOutput.write(Integer.toString(height));
			csvOutput.write(Integer.toString(numlayers));
			csvOutput.write(layers.toString());
			csvOutput.write(Double.toString(learningrate));
			csvOutput.write(Double.toString(maxerror));
			csvOutput.write(Double.toString(momentum));
			csvOutput.endRecord();


			csvOutput.write("pic_id");
			csvOutput.write("result");
			csvOutput.write("confidence");
			for (int i = 0; i < directories.length; i++){
				csvOutput.write(directories[i]);
			}
			csvOutput.endRecord();
			//			}
			// else assume that the file already has the correct header line

			// write out a few records
			HashMap<String, List<String>> cat = new HashMap<String, List<String>> ();
			for (Map.Entry<String, ArrayList<Double>> entry: imageRecResultMap.entrySet()){
				csvOutput.write(entry.getKey());
				//				for (int n = 0; n < entry.getKey().size(); n++){
				//				csvOutput.write(entry.getKey().get(n));
				//				}

				List<Double> list = entry.getValue();
				List<String> indices = new ArrayList<String>();
				Double max =  entry.getValue().get(0);
				for (int i = 1; i < list.size(); i++) {
					System.out.println(max);
					if(list.get(i) >= max) {
						indices.add(directories[i - 1]);
					}
				}
				
				cat.put(entry.getKey(), indices);
				csvOutput.write(indices.toString());
				
//				csvOutput.write(directories[entry.getValue().indexOf(entry.getValue().get(0)]);
				for (int m = 0; m < entry.getValue().size(); m++){
					csvOutput.write(Double.toString((entry.getValue()).get(m)));
				}
				csvOutput.endRecord();
			}

			// optional
			List<Integer> evaList = evaluate(imageRecResultMap, directories, cat);
			csvOutput.write("Result");
			csvOutput.write("");
			csvOutput.write("");
			for (Integer i3: evaList){
				csvOutput.write(Integer.toString(i3));
				cornum += i3;
			}
			csvOutput.endRecord();

			csvOutput.write("Total");
			csvOutput.write(Integer.toString(cornum));
			csvOutput.endRecord();

			csvOutput.endRecord();
			csvOutput.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
		return cornum;
	}

	public static List<Integer> evaluate(TreeMap<String, ArrayList<Double>> imageRecResultMap, String[] directories, HashMap<String, List<String>> cat){
		//		Double percent = 0.5;
		List<Integer> evaList = new ArrayList<Integer>(Collections.nCopies(directories.length, 0));
		for (Map.Entry<String, ArrayList<Double>> entry : imageRecResultMap.entrySet()) {
			StringTokenizer st = new StringTokenizer(entry.getKey(), "._");
			String imageLabel = st.nextToken();
			for (int i = 0; i < directories.length; i++){
				if (cat.get(entry.getKey()).contains(imageLabel)){
					evaList.set(i, evaList.get(i) + 1);
				}
				//				if (entry.getValue().get(i) > percent && imageLabel.equals(directories[i])) {
				//					evaList.set(i, evaList.get(i) + 1);
				//				}else if (entry.getValue().get(i) <= percent && !imageLabel.equals(directories[i])){
				//					evaList.set(i, evaList.get(i) + 1);
				//				}
			}
		}
		return evaList;
	}
}
