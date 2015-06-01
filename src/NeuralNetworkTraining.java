import java.util.Arrays;
import java.util.List;

import Jama.Matrix;


public class NeuralNetworkTraining {
	
	private static String pathToInputFolder = "C:\\NeuralNetwork\\";
	private static String pathToOutputFolder = "C:\\NeuralNetwork\\";

	public static void main(String[] args) {

		// create neural network with the following architecture
		List<Integer> sizes = Arrays.asList(784, 30, 10);
		ICostFunction costFunction = new CrossEntropyCost();
		NeuralNetwork net = new NeuralNetwork(sizes, costFunction);

        // get training data
        Matrix trainingData = TrainingDataLoader.readKaggleData(pathToInputFolder + "train.csv");

        // rescale training data from 0-255 to 0-1
        Matrix pics = trainingData.getMatrix(0, trainingData.getRowDimension()-1, 1, trainingData.getColumnDimension()-1);
        pics.timesEquals(1.0/256.0);
        trainingData.setMatrix(0, trainingData.getRowDimension()-1, 1, trainingData.getColumnDimension()-1, pics);
		
        // train and save
		net.train(trainingData, 30, 32, 0.25, 5.0);
		
		net.toFile(pathToOutputFolder + "nn-784-30-10-30e.json");
	}
}