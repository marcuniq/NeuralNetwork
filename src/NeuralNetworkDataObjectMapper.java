import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import Jama.Matrix;

import com.google.common.primitives.Doubles;


public class NeuralNetworkDataObjectMapper {

	private NeuralNetworkDataObject data;
	
	public NeuralNetworkDataObjectMapper(NeuralNetworkDataObject data){
		this.data = data;
	}
	
	public static NeuralNetworkDataObject create(List<Integer> sizes, Vector<Matrix> wv, Vector<Matrix> bv){
		
		List<List<List<Double>>> weights = new ArrayList<>();
		for(Matrix w: wv)
			weights.add(twoDArrayToList(w.getArray()));
			
		List<List<List<Double>>> biases = new ArrayList<>();
		for(Matrix b: bv)
			biases.add(twoDArrayToList(b.getArray()));
		
		return new NeuralNetworkDataObject(sizes, weights, biases);
	}
	

	public Vector<Matrix> getWeights(){
		Vector<Matrix> weights = new Vector<>();
		
		for(List<List<Double>> w : data.weights){
			weights.add(new Matrix(ListToTwoDArray(w)));
		}
		
		return weights;
	}
	
	public Vector<Matrix> getBiases(){
		Vector<Matrix> biases = new Vector<>();
		
		for(List<List<Double>> b : data.biases){
			biases.add(new Matrix(ListToTwoDArray(b)));
		}
		
		return biases;
	}
	
	public List<Integer> getSizes(){
		return data.sizes;
	}
	
	/*
	 * Helper function to convert 2d array to List of Lists
	 */
	private static List<List<Double>> twoDArrayToList(double[][] twoDArray) {
		List<List<Double>> list = new ArrayList<>();
	    for (double[] array : twoDArray) {
	        list.add(Doubles.asList(array));
	    }
	    return list;
	}
	
	/*
	 * Helper function to convert List of Lists to 2d array
	 */
	private static double[][] ListToTwoDArray(List<List<Double>> lists) {
		double[][] array = new double[lists.size()][];
	    for (int i = 0; i < lists.size(); ++i) {
	        array[i] = Doubles.toArray(lists.get(i));
	    }
	    return array;
	}
}
