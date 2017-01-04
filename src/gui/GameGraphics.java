package gui;

import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import main.GoGameState;

public class GameGraphics extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -508624359020760549L;
	private BoardGraphics board;
	
	JLabel captured0;//stones captured by player 0
	JLabel captured1;//stones captured by player 1
	
	public GameGraphics(String[] names){
		super("Go2go");
		board = new BoardGraphics();
		JPanel panel = new JPanel();//exterior JPanel to setup layout
		Box stats = Box.createHorizontalBox();
		Box p0Stat = Box.createVerticalBox();
		Box p1Stat = Box.createVerticalBox();
		JLabel name0 = new JLabel(names[0]);
		JLabel name1 = new JLabel(names[1]);
		captured0 = new JLabel("Captured no stones");
		captured1 = new JLabel("Captured no stones");

		this.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(board);
		board.setPreferredSize(new Dimension(BoardGraphics.SIZE, BoardGraphics.SIZE));
		panel.add(stats);

		stats.add(Box.createHorizontalGlue());
		stats.add(p0Stat);
		stats.add(Box.createHorizontalGlue());
		stats.add(p1Stat);
		stats.add(Box.createHorizontalGlue());
		
		p0Stat.add(Box.createVerticalGlue());
		p0Stat.add(name0);
		p0Stat.add(Box.createVerticalGlue());
		p0Stat.add(captured0);
		p0Stat.add(Box.createVerticalGlue());
		
		p1Stat.add(Box.createVerticalGlue());
		p1Stat.add(name1);
		p1Stat.add(Box.createVerticalGlue());
		p1Stat.add(captured1);
		p1Stat.add(Box.createVerticalGlue());
	}
	
	/**
	 * display the GUI when it's ready
	 */
	public void startGraphics(){
		setSize(BoardGraphics.SIZE,BoardGraphics.SIZE+100);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	
	/**
	 * update the game state displayed by the graphics
	 * @param state
	 */
	public void setState(GoGameState state) {
		board.setGoBoard(state.getBoard());
		board.setProposal(state.getTerritoryProposal());
		board.setPrevMove(state.getPrevX(), state.getPrevY());
		
		revalidate();
		board.repaint();
	}
	
}
