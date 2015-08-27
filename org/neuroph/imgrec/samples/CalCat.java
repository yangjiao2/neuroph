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

public class CalCat {
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
		picfolder = new File (picfolderpath);

		String[] directories = picfolder.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		System.out.println(Arrays.toString(directories));

		System.out.println("Enter picture folder ");
		String trainfolderpath = in.nextLine();
		System.out.println("You entered picture folder path (string): "+ picfolderpath + "\\" + trainfolderpath);
		trainfolder = new File (picfolderpath, trainfolderpath);
		createlearningdataset(trainfolder);

		System.out.println("Enter picture folder ");
		String testfolderpath = in.nextLine();
		System.out.println("You entered picture folder path (string): " + testfolderpath);
		testfolder = new File (testfolderpath);
		createtestingdataset (imageRecognition, testfolder);
	}




	public static ImageRecognitionPlugin createlearningdataset (File trainfolder){

		System.out.println("Enter picture width ");
		width = in.nextInt();
		System.out.println("You entered picture width: "+ width);

		System.out.println("Enter picture height ");
		height = in.nextInt();
		System.out.println("You entered picture height: "+ height);


		// Create learning data
		//		List<String> imageLabels = new ArrayList<String> ();
		//		HashMap<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage> ();

		for (File file : trainfolder.listFiles ())
		{
			imageLabels.add(FilenameUtils.removeExtension(file.getName()));
			imagesMap.put(file.getName(), ImageUtilities.resizeImage (ImageUtilities.loadImage(file), width, height));           
		}
		Map<String, FractionRgbData> imageRgbData = ImageUtilities.getFractionRgbDataForImages (imagesMap);
		DataSet learningData = ImageRecognitionHelper.createRGBTrainingSet(imageLabels, imageRgbData);

		// create neural networks
		//		ArrayList<Integer> layers = new ArrayList<Integer> ();
		System.out.println("Enter number of layers ");
		numlayers = in.nextInt();
		System.out.println("You entered number of layers: "+ numlayers);

		for (Integer i = 1; i <= numlayers; i++){
			System.out.println("Enter number of neurons for layer " + i);
			Integer neuron = in.nextInt();
			System.out.println("You entered number of neurons for layer " + i + " :"+ neuron);
			layers.add (neuron);
		}
		nn = ImageRecognitionHelper.createNewNeuralNetwork ("recognition", new Dimension (width, height), ColorMode.BLACK_AND_WHITE, imageLabels, layers, TransferFunctionType.SIGMOID); // create my own network

		// learn data
		MomentumBackpropagation mb1 = (MomentumBackpropagation)nn.getLearningRule();
		mb1.setLearningRate(0.2);
		mb1.setMaxError(0.1);
		mb1.setMomentum(0.7);

		nn.learn(learningData);

		return imageRecognition = (ImageRecognitionPlugin)nn.getPlugin(ImageRecognitionPlugin.class); 
	}

	// Test images
	public static HashMap<String, Double> createtestingdataset (ImageRecognitionPlugin ir, File testfolderpath){
		System.out.println("-----------------------------------------------------------------------------------------------------");
		for (File file : testfolderpath.listFiles ())
		{
			String testfile = FilenameUtils.removeExtension(file.getName());
			System.out.println("Checking '" + testfile + "'");
			
			try {
				HashMap<String, Double> output = imageRecognition.recognizeImage(file);

				NumberFormat formatter = new DecimalFormat("#0.0"); 
				double maxPercent = Double.MIN_VALUE;
				for (Map.Entry<String, Double> entry : output.entrySet()) {
					String result = entry.getKey() ;
					if ( entry.getValue() > maxPercent){
						maxPercent = entry.getValue(); 
						result = entry.getKey() + " (" + formatter.format(maxPercent * 100) + " %)";
						imageRecMap.put(entry.getKey(), entry.getValue());
					}
					System.out.println(result);
				}

			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			
		}
		System.out.println("-----------------------------------------------------------------------------------------------------");
		return imageRecMap;
	}

}
