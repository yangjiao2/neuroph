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
	private File folder;
	private File file;
	Scanner in = new Scanner(System.in);


	public void main(String[] args) {

		//	Steps:
		//	1. pass in picture folder path (not case sentative)
		//	2. adding training image (image folder) in train folder
		//	3. make neural network 
		//	4. test image (image folder) and generate readable output

		System.out.println("Enter picture folder path");
		String picfolder = in.nextLine();
		System.out.println("You entered picture folder path (string): "+ picfolder);
		//
		picfolder = "C:\\Users\\Yang\\MDP\\Neuroph\\test1\\morethan20";
		//
		folder = new File (picfolder);
		
		String[] directories = folder.list(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
			});
			System.out.println(Arrays.toString(directories));
	}

	public void createlearningdataset (File folder, String term){

		// Create learning data
		List<String> imageLabels = new ArrayList<String> ();

		HashMap<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage> ();
		for (File file : folder.listFiles ())
		{
			imageLabels.add(FilenameUtils.removeExtension(file.getName()));
			imagesMap.put(file.getName(), ImageUtilities.resizeImage (ImageUtilities.loadImage(file), 20, 20));           
		}
		Map<String, FractionRgbData> imageRgbData = ImageUtilities.getFractionRgbDataForImages (imagesMap);
		DataSet learningData = ImageRecognitionHelper.createRGBTrainingSet(imageLabels, imageRgbData);

		// 
		// create neural networks
		//  
		ArrayList<Integer> layers = new ArrayList<Integer> ();layers.add (12);
		NeuralNetwork nn1 = ImageRecognitionHelper.createNewNeuralNetwork ("recognition", new Dimension (20, 20), ColorMode.BLACK_AND_WHITE, imageLabels, layers, TransferFunctionType.SIGMOID); // create my own network
		NeuralNetwork nn2 = NeuralNetwork.createFromFile("NeuralNetworks\\test_notTrained.nnet");   // load network created in NeurophStudio following tutorial at http://neuroph.sourceforge.net/image_recognition.html
		NeuralNetwork nn3 = NeuralNetwork.createFromFile("NeuralNetworks\\test_trained.nnet");      // load network created in NeurophStudio following tutorial at http://neuroph.sourceforge.net/image_recognition.html

		//
		// learn data
		//
		MomentumBackpropagation mb1 = (MomentumBackpropagation)nn1.getLearningRule();
		mb1.setLearningRate(0.2);
		mb1.setMaxError(0.1);
		mb1.setMomentum(0.7);

		MomentumBackpropagation mb2 = (MomentumBackpropagation)nn2.getLearningRule();
		mb2.setLearningRate(0.2);
		mb2.setMaxError(0.1);
		mb2.setMomentum(0.7);

		nn1.learn(learningData);
		nn2.learn(learningData);

		// get the image recognition plugin from neural network    
		ImageRecognitionPlugin ir1 = (ImageRecognitionPlugin)nn1.getPlugin(ImageRecognitionPlugin.class); 
		ImageRecognitionPlugin ir2 = (ImageRecognitionPlugin)nn2.getPlugin(ImageRecognitionPlugin.class);
		ImageRecognitionPlugin ir3 = (ImageRecognitionPlugin)nn3.getPlugin(ImageRecognitionPlugin.class);

		//
		// Try to check all learning images
		//
		System.out.println("-----------------------------------------------------------------------------------------------------");
		for (File file : folder.listFiles ())
		{
			System.out.println("Checking '" + FilenameUtils.removeExtension(file.getName()) + "'");
			System.out.println("Java Runtime created network     : " + RecognizeImage(ir1, file.getPath()));
			System.out.println("NS created network (not trained) : " + RecognizeImage(ir2, file.getPath()));
			System.out.println("NS created network (trained)     : " + RecognizeImage(ir3, file.getPath()));
			System.out.println("-----------------------------------------------------------------------------------------------------");
		}

		private static String RecognizeImage(ImageRecognitionPlugin imageRecognition, String imagePath) {
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
