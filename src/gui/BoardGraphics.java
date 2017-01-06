package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.Game;
import main.GoGameState;
import main.Log;
import players.GamePlayer;

class BoardGraphics extends JPanel implements MouseListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1578137049156915447L;
	static final int SIZE = 600;
	//radius of a go piece 20 because there's 9 rows and columns and
	//half that to change diameter to radius
	private static final int radius = SIZE / (20);
	
	private Image texture;
	
	//variables from game state
	private int[][] goBoard;
	private int[][] proposal;
	private int prevX = -1;
	private int prevY = -1;
	
	//variables to send actions to game
	Game game;
	GamePlayer player;
	
	BoardGraphics( Game initGame, GamePlayer initPlayer ){
		game = initGame;
		player = initPlayer;

		try {
			texture = ImageIO.read(getClass().getResource("/goodwood.jpg"));
		} catch (IOException e) {
			Log.i("BoardGraphics", "goodwood.jpg not found");
		}
		addMouseListener(this);
	}
	
	public void paint(Graphics g){
		if (texture != null){
			Log.i("BoardGraphics", "drawing");
			g.drawImage(texture, 0 ,0, SIZE, SIZE, null);
			
			//board grid
			Graphics2D g2 = (Graphics2D) g;
			g2.setStroke(new BasicStroke(3));
			for(int i = 2*radius; i<SIZE-radius; i+=2*radius){
				g2.drawLine(2*radius, i, SIZE - 2*radius, i);
				g2.drawLine(i, 2*radius, i, SIZE - 2*radius);
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
						g.fillOval( (2*x+1)*radius, (2*y+1)*radius, 2*radius, 2*radius );
						break;
					case GoGameState.WHITE:
						g.setColor(Color.WHITE);
						g.fillOval( (2*x+1)*radius, (2*y+1)*radius, 2*radius, 2*radius );
						break;
					}
				}
			}
			
			//previous move
			if(prevX>=0){
				g.setColor(Color.gray);
				g.fillOval( (2*prevX+1)*radius+2*radius/3, (2*prevY+1)*radius+2*radius/3, 2*radius/3, 2*radius/3 );
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

	@Override
	public void mouseClicked(MouseEvent event) {
		//ignore top and left boarder clicks
		int x = event.getX();
		int y = event.getY();
		if (x < radius || y < radius){
			return;
		}
		
		//convert from screen coordinate to board coordinate
		x = ( (x/radius)-1 ) / 2;
		y = ( (y/radius)-1 ) / 2;
		
		//ignore bottom and right clicks
		if (x>=GoGameState.boardSize || y>=GoGameState.boardSize){
			return;
		}
		Log.i("BoardGraphics clicked", x+","+y);
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
