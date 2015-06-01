import Jama.Matrix;


public class QuadraticCost implements ICostFunction {

	@Override
	public double fn(Matrix a, Matrix y) {
		return 0.5 * Math.pow(a.minus(y).normF(), 2);
	}

	@Override
	public Matrix delta(Matrix z, Matrix a, Matrix y) {
		return a.minus(y).arrayTimes(NeuralNetwork.sigmoidPrime(z));
	}

}
