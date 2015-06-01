import java.util.Vector;

import Jama.*;

public class DeltaNabla {

	private Vector<Matrix> deltaNablaB;
	private Vector<Matrix> deltaNablaW;
	
	public DeltaNabla(Vector<Matrix> deltaNablaB, Vector<Matrix> deltaNablaW){
		this.setDeltaNablaB(deltaNablaB);
		this.setDeltaNablaW(deltaNablaW);
	}

	public Vector<Matrix> getDeltaNablaB() {
		return deltaNablaB;
	}

	public void setDeltaNablaB(Vector<Matrix> deltaNablaB) {
		this.deltaNablaB = deltaNablaB;
	}

	public Vector<Matrix> getDeltaNablaW() {
		return deltaNablaW;
	}

	public void setDeltaNablaW(Vector<Matrix> deltaNablaW) {
		this.deltaNablaW = deltaNablaW;
	}
}
