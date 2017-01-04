package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.GoGameState;
import main.Log;

class BoardGraphics extends JPanel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1578137049156915447L;
	static final int SIZE = 600;
	//radius of a go piece 20 because there's 9 rows and columns and
	//half that to change diameter to radius, +2 to add margin to edge
	private static final int radius = SIZE / (20);
	
	private Image texture;
	
	//variables from game state
	private int[][] goBoard;
	public int[][] getGoBoard() {
		return goBoard;
	}

	private int[][] proposal;
	private int prevX = -1;
	private int prevY = -1;
	
	BoardGraphics(){
		try {
			texture = ImageIO.read(getClass().getResource("/goodwood.jpg"));
		} catch (IOException e) {
			Log.i("BoardGraphics", "goodwood.jpg not found");
		}
	}
	
	public void paint(Graphics g){
		if (texture != null){
			Log.i("BoardGraphics", "drawing");
			g.drawImage(texture, 0 ,0, SIZE, SIZE, null);
			
			//board grid
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			for(int i = radius; i<SIZE; i+=2*radius){
				g2.drawLine(radius, i, SIZE - radius, i);
				g2.drawLine(i, radius, i, SIZE - radius);
			}
			
			//pieces
			if(goBoard == null){
				return;
			}
			for(int x = 0; x<GoGameState.boardSize; x++){
				for (int y=0; y<GoGameState.boardSize; y++){
					switch (goBoard[x][y]){
					case GoGameState.BLACK:
						g.setColor(Color.BLACK);
						g.fillOval( (2*x)*radius, (2*y)*radius, 2*radius, 2*radius );
						break;
					case GoGameState.WHITE:
						g.setColor(Color.WHITE);
						g.fillOval( (2*x)*radius, (2*y)*radius, 2*radius, 2*radius );
						break;
					}
				}
			}
		}
	}

	public void setGoBoard(int[][] goBoard) {
		this.goBoard = GoGameState.boardDeepCopy(goBoard);
	}

	public void setProposal(int[][] proposal) {
		this.proposal = GoGameState.boardDeepCopy(proposal);
	}

	public void setPrevMove(int x, int y) {
		this.prevX = x;
		this.prevY = y;
	}

}
