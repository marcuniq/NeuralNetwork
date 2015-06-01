import Jama.Matrix;;

public interface ICostFunction {

	/*
	 * Return the cost associated with an output ``a`` and desired output ``y``
	 */
	public double fn(Matrix a, Matrix y);
	
	/*
	 * Return the error delta from the output layer
	 */
	public Matrix delta(Matrix z, Matrix a, Matrix y);	
}