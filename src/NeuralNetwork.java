import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.io.File;
import java.io.IOException;

import Jama.*;

import org.apache.commons.math3.distribution.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NeuralNetwork {

	private List<Integer> sizes; // e.g. [784 30 30 10]
	private Vector<Matrix> weights;
	private Vector<Matrix> biases;

	private ICostFunction costFunctionObj;


	public NeuralNetwork(List<Integer> sizes, ICostFunction costFunctionObj){
		this.sizes = sizes;

		this.costFunctionObj = costFunctionObj;

		weights = new Vector<Matrix>();
		biases = new Vector<Matrix>();

		initializeBiasesAndWeights();
	}
	
	public NeuralNetwork(){
		this.costFunctionObj = new CrossEntropyCost();
	}

	/*
	 * Initialize with random weights 
	 */
	private void initializeBiasesAndWeights(){
		shapeVectorMatrices(biases, weights, true);
	}

	private void shapeVectorMatrices(Vector<Matrix> biases, Vector<Matrix> weights, boolean normalDist){
		// start with first hidden layer
		for(int i = 1; i < sizes.size(); ++i){
			int in = sizes.get(i - 1);
			int out = sizes.get(i);

			// initialize biases
			biases.addElement(randInitializeBiases(out, normalDist));

			// initialize weights
			weights.addElement(randInitializeWeights(in, out, normalDist));
		}
	}


	/*
	 * Randomly initialize the weights of a layer with "in"
	 * incoming connections and "out" outgoing connections
	 */
	private Matrix randInitializeWeights(int in, int out, boolean normalDist){

		double[][] array = new double[out][in];

		// normal "gaussian" distribution with mean 0 and variance 1
		NormalDistribution nd = new NormalDistribution();

		for(int row = 0; row < out; ++row){
			for(int column = 0; column < in; ++ column)			
				array[row][column] = normalDist ? nd.sample() : 0.0;
		}

		return new Matrix(array);
	}

	/*
	 * Randomly initialize the biases 
	 */
	private Matrix randInitializeBiases(int out, boolean normalDist){

		double[][] array = new double[out][1];

		// normal "gaussian" distribution with mean 0 and variance 1
		NormalDistribution nd = new NormalDistribution();

		for(int row = 0; row < out; ++row)
			array[row][0] = normalDist ? nd.sample() : 0.0;

		return new Matrix(array);
	}

	/*
	 * feed forward / classify input
	 */
	public Matrix classify(Matrix input){
		// feedforward
		Matrix activation = input.copy().transpose();

		for(int i = 0; i < biases.size(); ++i){
			Matrix layerBiases = biases.get(i);
			Matrix layerWeights = weights.get(i);

			Matrix z = layerWeights.times(activation).plus(layerBiases);

			activation = sigmoid(z);

		}
		return activation;
	}

	/*
	 * y Matrix is desired output
	 */
	private DeltaNabla backprop(Matrix x, Matrix y){

		// same dimensions like weights, initialize with zeros
		Vector<Matrix> nablaB = new Vector<Matrix>();
		Vector<Matrix> nablaW = new Vector<Matrix>();
		shapeVectorMatrices(nablaB, nablaW, false);

		// feedforward
		Matrix activation = x.copy().transpose();
		Vector<Matrix> activations = new Vector<Matrix>(); // vector to store all the activations, layer by layer
		activations.add(activation);

		Vector<Matrix> zVectors = new Vector<Matrix>(); // vector to store all the z vectors, layer by layer

		for(int i = 0; i < biases.size(); ++i){
			Matrix layerBiases = biases.get(i);
			Matrix layerWeights = weights.get(i);

			Matrix z = layerWeights.times(activation).plus(layerBiases);
			zVectors.add(z);

			activation = sigmoid(z);
			activations.add(activation);
		}

		// backward pass
		Matrix delta = costFunctionObj.delta(zVectors.lastElement(), activations.lastElement(), y.transpose());
		nablaB.setElementAt(delta, nablaB.size()-1);

		Matrix secondLastActivation = activations.get(activations.size()-2);
		Matrix backwardError = delta.times(secondLastActivation.transpose());
		nablaW.setElementAt(backwardError, nablaW.size()-1);


		// for each layer, starting from second last until second layer
		for(int layer = nablaW.size() - 2; layer >= 0; --layer){

			// compute delta
			Matrix z = zVectors.get(layer);
			Matrix sigmoidPrimeVec = sigmoidPrime(z);

			Matrix w = weights.get(layer + 1).transpose();
			delta = w.times(delta).arrayTimes(sigmoidPrimeVec);

			// gradient of cost function = activation(layer-1) * delta(layer)
			Matrix a = activations.get(layer).transpose();
			nablaW.setElementAt(delta.times(a), layer);

			// gradient bias = delta(layer)
			nablaB.setElementAt(delta, layer);

		}
		return new DeltaNabla(nablaB, nablaW);
	}

	/*
	 * write neural network to json file
	 */
	public void toFile(String filename){
		NeuralNetworkDataObject data = NeuralNetworkDataObjectMapper.create(sizes, weights, biases);
		
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			mapper.writeValue(new File(filename), data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 *  Train the neural network using mini-batch stochastic gradient descent.
	 */
	public void train(Matrix trainingData, int epochs, int miniBatchSize, double eta, double lambda){
		
		int n = trainingData.getRowDimension();

		for(int i = 0; i < epochs; ++i){

			// shuffle trainingData
			shuffleTrainingData(trainingData);

			// group training data examples into mini batches
			Vector<Matrix> miniBatches = new Vector<Matrix>();
			for(int k = 0; k < n; k += miniBatchSize){
				int endRow = k + miniBatchSize < n ? k + miniBatchSize - 1 : n-1;
				Matrix miniBatch = trainingData.getMatrix(k, endRow, 0, trainingData.getColumnDimension()-1);
				miniBatches.add(miniBatch);
			}

			// update mini batch
			for(Matrix miniBatch : miniBatches){
				updateMiniBatch(miniBatch, eta, lambda, n);
			}
			
			System.out.println("Epoch " + i + " completed.");

		}
	}

	private void shuffleTrainingData(Matrix trainingData){

		Random rnd = new Random();
		for(int row = trainingData.getRowDimension() - 1; row > 0 ; --row){
			int index = rnd.nextInt(row + 1);
			
			// Simple swap
			Matrix indexMatrix = trainingData.getMatrix(index, index, 0, trainingData.getColumnDimension()-1);
			Matrix rowMatrix = trainingData.getMatrix(row, row, 0, trainingData.getColumnDimension()-1);
			trainingData.setMatrix(index, index, 0, trainingData.getColumnDimension()-1, rowMatrix);			
			trainingData.setMatrix(row, row, 0, trainingData.getColumnDimension()-1, indexMatrix);
		}
	}

	/*
	 * Update the network's weights and biases by applying gradient descent
	 * using backpropagation to a single mini batch
	 */
	private void updateMiniBatch(Matrix miniBatch, double eta, double lambda, int n){

		// initialize with zeros
		Vector<Matrix> nablaB = new Vector<Matrix>();
		Vector<Matrix> nablaW = new Vector<Matrix>();
		shapeVectorMatrices(nablaB, nablaW, false);


		for(int i = 0; i < miniBatch.getRowDimension(); ++i){
			// split into inputActivation, desiredOutput
			Matrix inputActivation = miniBatch.getMatrix(i, i, 1, miniBatch.getColumnDimension()-1);


			Matrix desiredOutputTemp = miniBatch.getMatrix(i, i, 0, 0); // [3]
			Matrix desiredOutput = new Matrix(1, 10, 0.0); // [0 0 0 0 0 0 0 0 0 0]

			// vectorize y: 3 -> 0 0 0 1 0 0 0 0 0 0
			for(int j = 0; j< desiredOutputTemp.getRowDimension(); ++j){
				desiredOutput.set(j, (int) desiredOutputTemp.get(j, 0), 1); // [0 0 0 1 0 0 0 0 0 0]
			}

			// backprop
			DeltaNabla dn = backprop(inputActivation, desiredOutput);

			// update nablaB, nablaW
			Vector<Matrix> deltaNablaB = dn.getDeltaNablaB();
			Vector<Matrix> deltaNablaW = dn.getDeltaNablaW();

			for(int j = 0; j < nablaB.size(); ++j)
				nablaB.set(j, nablaB.get(j).plusEquals(deltaNablaB.get(j)));

			for(int j = 0; j < nablaW.size(); ++j)
				nablaW.set(j, nablaW.get(j).plusEquals(deltaNablaW.get(j)));
		}

		// update weights, biases
		
		// weight decay and regularization
		for(Matrix w : weights)
			w.timesEquals(1 - eta*(lambda/n));

		Vector<Matrix> weightsTempVec = new Vector<Matrix>();
		for(Matrix nW : nablaW)
			weightsTempVec.add(nW.times(eta/miniBatch.getRowDimension()));

		for(int i = 0; i < weights.size(); ++i)
			weights.set(i, weights.get(i).minus(weightsTempVec.get(i)));


		for(Matrix nB : nablaB)
			nB.timesEquals(eta/miniBatch.getRowDimension());

		for(int i = 0; i < biases.size(); ++i)
			biases.set(i, biases.get(i).minus(nablaB.get(i)));
	}
	
	/*
	 * Sigmoid activation function
	 */
	public static Matrix sigmoid(Matrix z){
		Matrix returnMatrix = new Matrix(z.getRowDimension(), z.getColumnDimension());

		for(int i=0; i< z.getRowDimension(); ++i){
			for(int j=0 ; j< z.getColumnDimension(); ++j){
				returnMatrix.set(i, j, 1.0/(1.0 + Math.exp(-z.get(i, j))));
			}
		}

		return returnMatrix;
	}

	/*
	 * derivative of sigmoid function
	 * sigmoid(z) .* (1 - sigmoid(z)) in matlab
	 */
	public static Matrix sigmoidPrime(Matrix z){
		Matrix ones = new Matrix(z.getRowDimension(), z.getColumnDimension(), 1);

		Matrix sigmoidMatrix = sigmoid(z);
		ones.minusEquals(sigmoidMatrix);

		return sigmoidMatrix.arrayTimes(ones);
	}

	public List<Integer> getSizes() {
		return sizes;
	}

	public void setSizes(List<Integer> sizes) {
		this.sizes = sizes;
	}

	public Vector<Matrix> getWeights() {
		return weights;
	}

	public void setWeights(Vector<Matrix> weights) {
		this.weights = weights;
	}

	public Vector<Matrix> getBiases() {
		return biases;
	}

	public void setBiases(Vector<Matrix> biases) {
		this.biases = biases;
	}

	public ICostFunction getCostFunctionObj() {
		return costFunctionObj;
	}

	public void setCostFunctionObj(ICostFunction costFunctionObj) {
		this.costFunctionObj = costFunctionObj;
	}
}
