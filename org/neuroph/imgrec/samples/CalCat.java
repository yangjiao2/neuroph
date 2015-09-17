package org.neuroph.imgrec.samples;
import org.apache.commons.io.FilenameUtils;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.imgrec.ColorMode;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.ImageRecognitionHelper;
import org.neuroph.imgrec.ImageRecognitionPlugin;
import org.neuroph.imgrec.ImageSampler;
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
import java.util.TreeMap;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.io.FilenameFilter;
import java.util.StringTokenizer;
import java.util.Collections;

import org.neuroph.imgrec.image.Image;


import org.neuroph.imgrec.image.ImageFactory;


public class CalCat {
	//	private static Integer numlayers = 2;
	//	private static ArrayList<Integer> layers = new ArrayList<Integer> ();
	//	private static List<String> imageLabels = new ArrayList<String> ();

	private static HashMap<String, BufferedImage> imagesMap = new HashMap<String, BufferedImage> ();
	private static NeuralNetwork nn;
	private static ImageRecognitionPlugin imageRecognition;
	public static ImageRecognitionPlugin createlearningdataset (File picfolder, File labeledImagesDir, String trainfolderpath, String[] directories , Integer width, Integer height, Integer numlayers, ArrayList<Integer> layers){

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
		List<String> imageLabels = new ArrayList<String>();
		String imageDir = labeledImagesDir.toString();

		// load color infor for images to recognize
		try {
			// get labels for all images
			rgbDataMap.putAll(ImageRecognitionHelper.getFractionRgbDataForDirectory(labeledImagesDir, new Dimension (width, height))); 
			System.out.println("Load images from labeled images dir: '" + imageDir + "'");
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
		for (String labeledjunkDir: directories){
			File junkDir = new File(picfolder, labeledjunkDir);
			if (!junkDir.toString().equals(imageDir.toString())){
				
				try {
					rgbDataMap.putAll(ImageRecognitionHelper.getFractionRgbDataForDirectory(junkDir, new Dimension (width, height))); // pre je koristio ImageLoader
					System.out.println("Load images from junk images dir: '" + junkDir.toString() + "'");
				} catch (IOException ioe) {
					System.err.println("Unable to load images from junk images dir: '" + junkDir + "'");
					System.err.println(ioe.toString());
				}
			}
		}

//		System.out.println(imageLabels.size());
//		System.out.println(rgbDataMap.size());
//		System.out.println(imageLabels);
//		System.out.println(rgbDataMap.keySet());
//		System.out.println(rgbDataMap.values());
		dataSet = ImageRecognitionHelper.createBlackAndWhiteTrainingSet(imageLabels, rgbDataMap);

		// create neural networks
		//		ArrayList<Integer> layers = new ArrayList<Integer> ();

		nn = ImageRecognitionHelper.createNewNeuralNetwork (trainfolderpath, new Dimension (width, height), ColorMode.BLACK_AND_WHITE, imageLabels, layers, TransferFunctionType.SIGMOID);

		// learn data
		MomentumBackpropagation mb1 = (MomentumBackpropagation)nn.getLearningRule();
		mb1.setLearningRate(0.2);
		mb1.setMaxError(0.1);
		mb1.setMomentum(0.7);
		nn.learn(dataSet);

		imageRecognition = (ImageRecognitionPlugin)nn.getPlugin(ImageRecognitionPlugin.class); 
		return imageRecognition;
	}

	// Test images
	public static TreeMap<String, ArrayList<Double>> createtestingdataset (TreeMap<String, ArrayList<Double>> imageRecMap, ImageRecognitionPlugin imageRecognition, File testfolderpath, Integer width, Integer height){

		String result = "";
//		System.out.println("-----------------------------------------------------------------------------------------------------");
		File[] testfiles = testfolderpath.listFiles();
		System.out.println(Arrays.deepToString(testfiles));
		Arrays.sort(testfiles);
		System.out.println(Arrays.deepToString(testfiles));
		for (File file : testfiles)
		{
			String testfile = FilenameUtils.removeExtension(file.getName());
			

			Image img = ImageFactory.getImage(file);
			img = ImageSampler.downSampleImage(new Dimension(width, height), img);
			HashMap<String, Double> output = imageRecognition.recognizeImage(img);

			NumberFormat formatter = new DecimalFormat("#0.0"); 
			double maxPercent = Double.MIN_VALUE;
			for (Map.Entry<String, Double> entry : output.entrySet()) {
				result = entry.getKey();
				if ( entry.getValue() > maxPercent){
					maxPercent = entry.getValue(); 
					result = entry.getKey() + " (" + formatter.format(maxPercent * 100) + " %)";
					if (imageRecMap.containsKey(testfile)){
						ArrayList<Double> previousList = imageRecMap.get(testfile);
						previousList.add(entry.getValue());
						imageRecMap.put(testfile, previousList);
					}else{
						ArrayList<Double> newList = new ArrayList<Double> ();
						newList.add(entry.getValue());
						imageRecMap.put(testfile, newList);
					}
				}
			}
		}
//		System.out.print(result);
//		System.out.println("-----------------------------------------------------------------------------------------------------");
		return imageRecMap;
	}
	
}
