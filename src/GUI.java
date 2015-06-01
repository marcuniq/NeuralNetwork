import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.*;

import Jama.Matrix;

public class GUI extends JFrame {

	private NeuralNetwork net;
	private DrawCanvas canvas;
	private ChartPanel chart;
	
	public GUI(){
		init();
	}
	
	private void init(){
		this.canvas = new DrawCanvas();
		JPanel panel = new JPanel(new FlowLayout());
		panel.add(canvas);
		
		
		JButton loadNetButton = new JButton("Load Net");
		loadNetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(GUI.this);

		        if (returnVal == JFileChooser.APPROVE_OPTION) {
		            File file = fc.getSelectedFile();
		            
		            try {
						net = NeuralNetworkLoader.loadNet(file.getAbsolutePath());
						
					} catch (Exception e) {
						e.printStackTrace();
						
						JOptionPane.showMessageDialog(GUI.this, "Failed to load neural network!");
					}

		        } else {
		            
		        }
			}
		});
		panel.add(loadNetButton);
		
		
		JButton classifyButton = new JButton("Classify");
		classifyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {

				Matrix input = getInputFromCanvas();
				double[][] output = net.classify(input).transpose().getArray();
				
				if(output.length > 0)
					chart.setValues(output[0]);
			}
		});
		panel.add(classifyButton);
		
		
		JButton clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				canvas.clearCells();
			}
		});
		panel.add(clearButton);
		
		
		double[] initValues = {0,0,0,0,0,0,0,0,0,0};
		String[] names = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
		chart = new ChartPanel(initValues, names, "Output");
		panel.add(chart);

		setVisible(true);
		setSize(900, 900);
		getContentPane().add(panel);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private Matrix getInputFromCanvas(){
		int[][] cells = canvas.getCells();
		
		// translate to matrix (1 by M*N)
		Matrix m = new Matrix(1, cells.length * cells.length);
		for(int row = 0; row < cells.length; ++row)
			for(int col = 0; col < cells.length; ++col){
				if(cells[row][col] > 0)
					m.set(0, (row * cells.length) + col, 1.0);
			}
		
		return m;
	}
}