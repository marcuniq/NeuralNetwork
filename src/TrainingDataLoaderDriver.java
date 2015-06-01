import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import Jama.Matrix;


public class TrainingDataLoaderDriver {
	
	private static String pathToTrainCSV = "C:\\NeuralNetwork\\train.csv";

	public static void main(String[] args) {

		List<Integer> unitsPerLayer = Arrays.asList(TrainingDataLoader.NUMBER_OF_PIXELS_KAGGLE, 30, 30, 10);
		ICostFunction costFunction = new CrossEntropyCost();
		NeuralNetwork net = new NeuralNetwork(unitsPerLayer, costFunction);
		
		net.toFile("test.json");
		
		
        /* 
         * some test cases to show how to use the loader utility
         */
		
		// read only a single line (= image) from a file
        int[] kaggleImageData1;
        kaggleImageData1 = TrainingDataLoader.readSingleKaggleImage(pathToTrainCSV, 20);
        
        // write content of chosen line to sysout
//        for (int i = 0; i < kaggleImageData1.length; i++) {
//            System.out.println(kaggleImageData1[i] + ",");
//        }
        
        // generate BufferedImage show image in a frame
        BufferedImage myImage = TrainingDataLoader.getSingleKaggleImage(kaggleImageData1);
        int scale = 20;
        BufferedImage after = new BufferedImage(scale*myImage.getWidth(), scale*myImage.getHeight(), myImage.getType());
        AffineTransform at = new AffineTransform();
        at.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BICUBIC);
        after = scaleOp.filter(myImage, after);
        
        JFrame frame = new JFrame();
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().add(new JLabel(new ImageIcon(after)));
        frame.pack();
        frame.setVisible(true);
        
        // reading complete file
        Matrix kaggleImageData2 = new Matrix(0, 0);
        kaggleImageData2 = TrainingDataLoader.readKaggleData(pathToTrainCSV);
        //kaggleImageData2.print(3, 0);
		
        Matrix pics = kaggleImageData2.getMatrix(0, kaggleImageData2.getRowDimension()-1, 1, kaggleImageData2.getColumnDimension()-1);
        pics.timesEquals(1.0/256.0);
        kaggleImageData2.setMatrix(0, kaggleImageData2.getRowDimension()-1, 1, kaggleImageData2.getColumnDimension()-1, pics);
		
		net.train(kaggleImageData2, 30, 10, 0.25, 5.0);
		
		net.toFile("C:\\NeuralNetwork\\test.json");
		
		Matrix test = kaggleImageData2.getMatrix(0, 0, 1, 784);
		
		Matrix classification = net.classify(test);
	}
}
