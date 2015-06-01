import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NeuralNetworkLoader {

	/*
	 * load neural network from json file and return it
	 */
	public static NeuralNetwork loadNet(String filename) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		NeuralNetwork nn = new NeuralNetwork();
		
		try {
			NeuralNetworkDataObject data = mapper.readValue(new File(filename), NeuralNetworkDataObject.class);
			
			NeuralNetworkDataObjectMapper creator = new NeuralNetworkDataObjectMapper(data);
			nn.setSizes(creator.getSizes());
			nn.setWeights(creator.getWeights());
			nn.setBiases(creator.getBiases());
			
			return nn;
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception();
		}
	}
}