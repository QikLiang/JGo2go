package gui;

import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main.GoGameState;

public class GameGraphics extends JFrame {
	private GoGameState state;
	private BoardGraphics board;
	
	public GameGraphics(){
		super("Go2go");
		board = new BoardGraphics();
		JPanel panel = new JPanel();//exterior JPanel to setup layout
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(board);
		this.add(panel);
	}
	
	/**
	 * display the GUI when it's ready
	 */
	public void startGraphics(){
		setSize(400,600);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * update the game state displayed by the graphics
	 * @param state
	 */
	public void setState(GoGameState state) {
		synchronized (this.state){
			this.state = new GoGameState(state);
		}
		revalidate();
	}
	
}
