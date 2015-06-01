import java.util.List;

public class NeuralNetworkDataObject {

	public List<Integer> sizes;
	public List<List<List<Double>>> weights;
	public List<List<List<Double>>> biases;
	
	public NeuralNetworkDataObject(List<Integer> sizes, List<List<List<Double>>> weights, List<List<List<Double>>> biases){
		this.sizes = sizes;
		this.weights = weights;
		this.biases = biases;
	}
	
	public NeuralNetworkDataObject(){}
}
