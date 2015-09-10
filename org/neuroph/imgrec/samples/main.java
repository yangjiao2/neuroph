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

public class main {
	private static File picfolder;
	private static File trainfolder;
	private static Scanner in = new Scanner(System.in);
	private static Integer width = 30;
	private static Integer height = 33;
	private static Integer numlayers = 2;
	private static ArrayList<Integer> layers = new ArrayList<Integer> ();
	private static List<String> imageLabels = new ArrayList<String> ();
	private static HashMap<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage> ();
	private static ImageRecognitionPlugin imageRecognition;
	private static File testfolder;
	private static HashMap<String, Double> imageRecMap = new HashMap<String, Double> ();
	private static NeuralNetwork nn;

	public static void main(String[] args) {
	


		//	Steps:
		//	1. pass in picture folder path (not case sentative)
		//	2. adding training image (image folder) in train folder
		//	3. make neural network 
		//	4. test image (image folder) and generate readable output


		//
		String picfolderpath = "C:\\Users\\Yang\\MDP\\Neuroph\\test1\\morethan20";
		//
		//		System.out.print("Enter picture folder among");
		picfolder = new File (picfolderpath);

		String[] directories = picfolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		System.out.println(Arrays.toString(directories));
		//		String trainfolderpath = in.nextLine();	

		for (String trainfolderpath : directories ){
			trainfolder = new File (picfolderpath, trainfolderpath);
			System.out.println("You entered picture folder path (string): "+ trainfolder.toString());

			imageRecognition = CalCat.createlearningdataset(picfolder, trainfolder, trainfolderpath, directories);

			System.out.println("Enter test picture folder ");
			String testfolderpath = in.nextLine();
			System.out.println("You entered picture folder path (string): " + testfolderpath);
			testfolder = new File (testfolderpath);
			imageRecMap = CalCat.createtestingdataset (imageRecognition, testfolder);
			// assume test folder contains information such as language, info#, age, etc.


		}
	}
}