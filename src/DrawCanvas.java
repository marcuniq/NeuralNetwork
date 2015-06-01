import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class DrawCanvas extends JPanel{

	private int numberOfCells;
	private int cellSize;
	private int[][] cells;
	
	public DrawCanvas(){
		addMouseListener(new MouseListener(){
			
			public void mouseClicked(MouseEvent e) {

				// which cell was clicked?
				int row = e.getY() / cellSize;
				int column = e.getX() / cellSize;
				
				cells[row][column] += 1;
				
				repaint();
				
			}
			public void mouseEntered(MouseEvent arg0) {}
            public void mouseExited(MouseEvent arg0) {}
            public void mousePressed(MouseEvent arg0) {}
            public void mouseReleased(MouseEvent arg0) {}
        });
		
		addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent arg0) {}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				
				// which cell was clicked?
				int row = e.getY() / cellSize;
				int column = e.getX() / cellSize;
				
				cells[row][column] = 1;
				
				repaint();
			}
		});
		
		numberOfCells = 28;
		cells = new int[numberOfCells][numberOfCells];
		cellSize = 20;
		
		setPreferredSize(new Dimension(numberOfCells * cellSize, numberOfCells * cellSize));
		setSize(numberOfCells * cellSize, numberOfCells * cellSize);
		
		setVisible(true);
	}
	
	
	@Override
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		// draw background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		
		// draw grid
		g.setColor(Color.black);
		for(int row = 0; row < getHeight(); row+=cellSize)
			for(int col = 0; col < getWidth(); col+=cellSize)
				g.drawRect(row, col, cellSize, cellSize);
		
		// draw cells
		for(int row = 0; row < numberOfCells; ++row)
			for(int col = 0; col < numberOfCells; ++col){
				if(cells[row][col] > 0)
					g.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);
			}
	}
	
	public int[][] getCells(){
		return cells;
	}
	
	public void clearCells(){
		cells = new int[numberOfCells][numberOfCells];
		repaint();
	}
}