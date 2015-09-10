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

	





	public static ImageRecognitionPlugin createlearningdataset (File picfolder, File labeledImagesDir, String trainfolderpath, String[] directories){

		System.out.println("Enter picture width ");
		width = in.nextInt();
		System.out.println("You entered picture width: "+ width);

		System.out.println("Enter picture height ");
		height = in.nextInt();
		System.out.println("You entered picture height: "+ height);

		// Create learning data
		//		List<String> imageLabels = new ArrayList<String> ();
		//		HashMap<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage> ();

		//		for (File file : trainfolder.listFiles ())
		//		{
		//			imageLabels.add(FilenameUtils.removeExtension(file.getName()));
		//			imagesMap.put(file.getName(), ImageUtilities.resizeImage (ImageUtilities.loadImage(file), width, height));           
		//		}
		//		Map<String, FractionRgbData> imageRgbData = ImageUtilities.getFractionRgbDataForImages (imagesMap);
		//		
		//		for (String imgName : imageRgbData.keySet()) {
		//            StringTokenizer st = new StringTokenizer(imgName, "._");
		//            String imageLabel = st.nextToken();
		//            if (!imageLabels.contains(imageLabel)) {
		//                imageLabels.add(imageLabel);
		//            }
		//        }
		//        Collections.sort(imageLabels);
		//        


		DataSet dataSet = null;
		HashMap<String, FractionRgbData> rgbDataMap = new HashMap<String, FractionRgbData>();
		List imageLabels = new ArrayList<String>();
		String imageDir = labeledImagesDir.toString();

		// load color infor for images to recognize
		try {
			// get labels for all images
			rgbDataMap.putAll(ImageRecognitionHelper.getFractionRgbDataForDirectory(labeledImagesDir, new Dimension (width, height))); // pre je koristio ImageLoader

			for (String imgName : rgbDataMap.keySet()) {
				StringTokenizer st = new StringTokenizer(imgName, "._");
				String imageLabel = st.nextToken();
				if (!imageLabels.contains(imageLabel)) {
					imageLabels.add(imageLabel);
				}
			}
			Collections.sort(imageLabels);                        
		} catch (IOException ioe) {
			System.err.println("Unable to load images from labeled images dir: '" + imageDir + "'");
			System.err.println(ioe.toString());
		}

		// load junk images
//		for (String junkDir: directories){
//			if (!junkDir.equals(picfolder)){
//				try {
//					File junkImagesDir = new File(picfolder, junkDir);
//					rgbDataMap.putAll(ImageRecognitionHelper.getFractionRgbDataForDirectory(junkImagesDir, new Dimension (width, height))); // pre je koristio ImageLoader
//				} catch (IOException ioe) {
//					System.err.println("Unable to load images from junk images dir: '" + junkDir + "'");
//					System.err.println(ioe.toString());
//				}
//			}
//		}
		
		System.out.println(imageLabels.size());
		System.out.println(rgbDataMap.size());
		System.out.println(imageLabels);
		System.out.println(rgbDataMap.keySet());
		System.out.println(rgbDataMap.values());
		dataSet = ImageRecognitionHelper.createBlackAndWhiteTrainingSet(imageLabels, rgbDataMap);

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
		nn = ImageRecognitionHelper.createNewNeuralNetwork (trainfolderpath, new Dimension (width, height), ColorMode.BLACK_AND_WHITE, imageLabels, layers, TransferFunctionType.SIGMOID);

		// learn data
		MomentumBackpropagation mb1 = (MomentumBackpropagation)nn.getLearningRule();
		mb1.setLearningRate(0.2);
		mb1.setMaxError(0.1);
		mb1.setMomentum(0.7);
		nn.learn(dataSet);

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
