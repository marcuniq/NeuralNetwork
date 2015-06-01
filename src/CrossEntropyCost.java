import Jama.Matrix;

public class CrossEntropyCost implements ICostFunction {

	@Override
	public double fn(Matrix a, Matrix y) {
		Matrix lnA = a.copy();
		for(int i = 0; i < lnA.getRowDimension(); ++i){
			for(int j = 0; j < lnA.getColumnDimension(); ++j){
				double d = Math.log(lnA.get(i, j));
				lnA.set(i, j, Double.isNaN(d) ? 0.0 : d);
			}
		}
		Matrix ln1minusA = a.copy();
		for(int i = 0; i < ln1minusA.getRowDimension(); ++i){
			for(int j = 0; j < ln1minusA.getColumnDimension(); ++j){
				double d = Math.log(1 - ln1minusA.get(i, j));
				ln1minusA.set(i, j, Double.isNaN(d) ? 0.0 : d);
			}
		}
		
		Matrix OneminusY = y.copy();
		for(int i = 0; i < OneminusY.getRowDimension(); ++i){
			for(int j = 0; j < OneminusY.getColumnDimension(); ++j){
				OneminusY.set(i, j, 1 - OneminusY.get(i, j));
			}
		}
		
		Matrix first = y.times(-1).arrayTimes(lnA);
		Matrix second = OneminusY.arrayTimes(ln1minusA);
		
		return first.minus(second).normF();
	}

	@Override
	public Matrix delta(Matrix z, Matrix a, Matrix y) {
		// TODO Auto-generated method stub
		return a.minus(y);
	}

}