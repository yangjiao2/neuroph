package org.neuroph.imgrec.samples;

import org.apache.commons.io.FilenameUtils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.imgrec.ColorMode;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.ImageRecognitionHelper;
import org.neuroph.imgrec.ImageRecognitionPlugin;
import org.neuroph.imgrec.ImageUtilities;
import org.neuroph.imgrec.image.Dimension;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FilenameFilter;
import java.util.StringTokenizer;
import java.util.Collections;

import org.neuroph.imgrec.samples.CalCat;
import org.neuroph.imgrec.samples.outputcsv;
public class main {
	private static File picfolder;
	private static File trainfolder;
	private static Scanner in = new Scanner(System.in);
	private static Integer width = 30;
	private static Integer height = 33;
	private static Integer numlayers = 2;
	private static ArrayList<Integer> layers = new ArrayList<Integer> ();
	private static List<String> imageLabels = new ArrayList<String> ();
	private static Map<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage> ();
	//	private static ImageRecognitionPlugin imageRecognition;
	private static File testfolder;
	private static TreeMap<String, ArrayList<Double>> imageRecMap;
	private static NeuralNetwork nn;
	private static String[] directories;

	public static void main(String[] args) {

		//	Steps:
		//	1. pass in picture folder path (not case sentative)
		//	2. adding training image (image folder) in train folder
		//	3. make neural network 
		//	4. test image (image folder) and generate readable output


		//		String picfolderpath = "C:\\Users\\Yang\\MDP\\Tesseract\\manually_dataset_7\\train";
		String picfolderpath =  "C:\\Users\\Yang\\MDP\\Tesseract\\manually_dataset_2\\morethan20\\train";
		//
		//		System.out.print("Enter picture folder among");
		picfolder = new File (picfolderpath);

		directories = picfolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});



		//		System.out.println("Enter picture width ");
		//		width = in.nextInt();
		//		System.out.println("You entered picture width: "+ width);
		//
		//		System.out.println("Enter picture height ");
		//		height = in.nextInt();
		//		System.out.println("You entered picture height: "+ height);
		//		
		//		System.out.println("Enter number of layers ");
		//		numlayers = in.nextInt();
		//		System.out.println("You entered number of layers: "+ numlayers);
		//		
		//		for (Integer i = 1; i <= numlayers; i++){
		//			System.out.println("Enter number of neurons for layer " + i);
		//			Integer neuron = in.nextInt();
		//			System.out.println("You entered number of neurons for layer " + i + " :"+ neuron);
		//			layers.add (neuron);
		//		}

		width = 30;
		height = 35;
		numlayers = 2;
		layers.add(70);
		layers.add(50);


		imageRecMap = new TreeMap<String, ArrayList<Double>> ();
		for (String trainfolderpath : directories){
			//			System.out.println("Checking '" + trainfolderpath + "'");

			trainfolder = new File (picfolderpath, trainfolderpath);
			System.out.println(Arrays.toString(directories));
			//		String trainfolderpath = in.nextLine();	

			System.out.println("You entered picture folder path (string): "+ trainfolder.toString());

			ArrayList<Integer> copylayers = new ArrayList<Integer> ();

			for (Integer int1: layers){
				copylayers.add(int1);
			};
			ImageRecognitionPlugin imageRecognition = CalCat.createlearningdataset(picfolder, trainfolder, trainfolderpath, directories, width, height, numlayers, copylayers);

			//			System.out.println("Enter test picture folder ");
			//			String testfolderpath = in.nextLine();
			//			System.out.println("You entered picture folder path (string): " + testfolderpath);
//			String testfolderpath = "C:\\Users\\Yang\\MDP\\Tesseract\\manually_dataset_7\\test";
			String testfolderpath = "C:\\Users\\Yang\\MDP\\Tesseract\\manually_dataset_2\\morethan20\\test";
			testfolder = new File (testfolderpath);
			//			for (String trainfolder : directories){
			//				imageRecMap.put(trainfolder, new ArrayList<Double> ());
			//			}

			imageRecMap = CalCat.createtestingdataset (imageRecMap, imageRecognition, testfolder, width, height);
			// assume test folder contains information such as language, info#, age, etc.


		}
		//				System.out.println(imageRecMap.toString());
		outputcsv.writecsv(imageRecMap, directories, width, height, numlayers, layers);
		System.out.println("");

		System.out.println("========================================================");
		System.out.println("Test done!");
	}


}