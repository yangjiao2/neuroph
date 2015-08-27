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
	private File picfolder;
	private File trainfolder;
	Scanner in = new Scanner(System.in);
	Integer width = 30;
	Integer height = 33;
	Integer numlayers = 2;
	ArrayList<Integer> layers = new ArrayList<Integer> ();
	List<String> imageLabels = new ArrayList<String> ();
	HashMap<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage> ();
	ImageRecognitionPlugin imageRecognition;
	private File testfolder;
	HashMap<String, Double> imageRecMap = new HashMap<String, Double> ();


	public void main(String[] args) {

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
		System.out.println("You entered picture folder path (string): "+ picfolderpath + trainfolderpath);
		trainfolder = new File (picfolderpath, trainfolderpath);
		this.createlearningdataset(trainfolder);

		System.out.println("Enter picture folder ");
		String testfolderpath = in.nextLine();
		System.out.println("You entered picture folder path (string): " + testfolderpath);
		testfolder = new File (testfolderpath);
		this.createtestingdataset (imageRecognition, testfolder);
	}




	public ImageRecognitionPlugin createlearningdataset (File trainfolder){

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

		for (Integer i = 0; i < numlayers; i++){
			System.out.println("Enter number of neurons for layer " + i);
			Integer neuron = in.nextInt();
			System.out.println("You entered number of neurons for layer " + i + " :"+ neuron);
			layers.add (neuron);
		}
		NeuralNetwork nn = ImageRecognitionHelper.createNewNeuralNetwork ("recognition", new Dimension (width, height), ColorMode.BLACK_AND_WHITE, imageLabels, layers, TransferFunctionType.SIGMOID); // create my own network

		// learn data
		MomentumBackpropagation mb1 = (MomentumBackpropagation)nn.getLearningRule();
		mb1.setLearningRate(0.2);
		mb1.setMaxError(0.1);
		mb1.setMomentum(0.7);

		nn.learn(learningData);

		return imageRecognition = (ImageRecognitionPlugin)nn.getPlugin(ImageRecognitionPlugin.class); 
	}

	// Test images
	public String createtestingdataset (ImageRecognitionPlugin ir, File testfolderpath){
		System.out.println("-----------------------------------------------------------------------------------------------------");
		for (File file : testfolderpath.listFiles ())
		{
			String testfile = FilenameUtils.removeExtension(file.getName());
			System.out.println("Checking '" + testfile + "'");
			String result = "";
			try {
				HashMap<String, Double> output = imageRecognition.recognizeImage(file);

				NumberFormat formatter = new DecimalFormat("#0.0"); 
				double maxPercent = Double.MIN_VALUE;
				for (Map.Entry<String, Double> entry : output.entrySet()) {

					if ( entry.getValue() > maxPercent){
						maxPercent = entry.getValue(); 
						result = entry.getKey() + " (" + formatter.format(maxPercent * 100) + " %)";
					}
				}

			} catch(IOException ioe) {
				ioe.printStackTrace();
			}
			System.out.println(testfile + "  " + testfilerec);
			imageRecMap.put(testfile, testfilerec);
		}
		System.out.println("-----------------------------------------------------------------------------------------------------");
		return null;
	}
	private static Double RecognizeImage(ImageRecognitionPlugin imageRecognition, String imagePath) {
		String result = "";
		try {
			HashMap<String, Double> output = imageRecognition.recognizeImage(new File(imagePath));

			NumberFormat formatter = new DecimalFormat("#0.0"); 
			double maxPercent = Double.MIN_VALUE;
			for (Map.Entry<String, Double> entry : output.entrySet()) {

				if ( entry.getValue() > maxPercent){
					maxPercent = entry.getValue(); 
					result = entry.getKey() + " (" + formatter.format(maxPercent * 100) + " %)";
				}
			}

		} catch(IOException ioe) {
			ioe.printStackTrace();
		}

		return result;
	}
}
