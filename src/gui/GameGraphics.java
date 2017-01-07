package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import action.AgreeTerritoryAction;
import action.ForfeitAction;
import action.PassAction;
import action.PutPieceAction;
import action.SelectTerritoryAction;
import main.Game;
import main.GoGameState;
import main.Log;
import players.GamePlayer;

public class GameGraphics extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = -508624359020760549L;
	private BoardGraphics board;
	
	private TurnIcon blackBowl;
	private TurnIcon whiteBowl;
	
	JLabel captured0;//stones captured by player 0
	JLabel captured1;//stones captured by player 1
	JButton buttonTop;
	JButton buttonBottom;
	
	private static final Dimension buttonSize = new Dimension(150, 30);
	
	//variables to send actions to game
	Game game;
	GamePlayer player;
	int playerNum;
	GoGameState state;
	private int[][] originalTerritoryProposal;
	
	public GameGraphics(String[] names, Game initGame, GamePlayer initPlayer, int initPlayerNum){
		super("Go2go - " + names[initPlayerNum]);

		try {
			whiteBowl = new TurnIcon(ImageIO.read(getClass().getResource("/whitebowl.png")));
			blackBowl = new TurnIcon(ImageIO.read(getClass().getResource("/blackbowl.png")));
		} catch (IOException e) {
			Log.i("GameGraphics", "Bowl images not found");
		}

		game = initGame;
		player = initPlayer;
		playerNum = initPlayerNum;

		board = new BoardGraphics(this);
		JPanel panel = new JPanel();//exterior JPanel to setup layout
		Box stats = Box.createHorizontalBox();
		Box p0Stat = Box.createVerticalBox();
		Box p1Stat = Box.createVerticalBox();
		Box buttons = Box.createVerticalBox();
		JLabel name0 = new JLabel(names[0]);
		JLabel name1 = new JLabel(names[1]);
		captured0 = new JLabel("Captured no stones");
		captured1 = new JLabel("Captured no stones");
		name0.setAlignmentX(Component.CENTER_ALIGNMENT);
		name1.setAlignmentX(Component.CENTER_ALIGNMENT);
		captured0.setAlignmentX(Component.CENTER_ALIGNMENT);
		captured1.setAlignmentX(Component.CENTER_ALIGNMENT);
		buttonTop = new JButton("Pass");
		buttonBottom = new JButton("Forfeit");

		buttonTop.setPreferredSize(buttonSize);
		buttonTop.setMinimumSize(buttonSize);
		buttonTop.setMaximumSize(buttonSize);
		buttonBottom.setPreferredSize(buttonSize);
		buttonBottom.setMinimumSize(buttonSize);
		buttonBottom.setMaximumSize(buttonSize);
		buttonTop.addActionListener(new TopButtonAction());
		buttonBottom.addActionListener(new BottomButtonAction());

		this.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(board);
		board.setPreferredSize(new Dimension(BoardGraphics.SIZE, BoardGraphics.SIZE));
		panel.add(stats);

		stats.add(Box.createHorizontalGlue());
		stats.add(blackBowl);
		stats.add(p0Stat);
		stats.add(buttons);
		stats.add(p1Stat);
		stats.add(whiteBowl);
		stats.add(Box.createHorizontalGlue());
		
		p0Stat.add(Box.createVerticalGlue());
		p0Stat.add(name0);
		p0Stat.add(Box.createVerticalGlue());
		p0Stat.add(captured0);
		p0Stat.add(Box.createVerticalGlue());
		
		buttons.add(Box.createVerticalGlue());
		buttons.add(buttonTop);
		buttons.add(Box.createVerticalGlue());
		buttons.add(buttonBottom);
		buttons.add(Box.createVerticalGlue());
		
		p1Stat.add(Box.createVerticalGlue());
		p1Stat.add(name1);
		p1Stat.add(Box.createVerticalGlue());
		p1Stat.add(captured1);
		p1Stat.add(Box.createVerticalGlue());

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
		this.state = state;
		originalTerritoryProposal = GoGameState.boardDeepCopy(state.getTerritoryProposal());
		
		board.setGoBoard(state.getBoard());
		board.setProposal(state.getTerritoryProposal());
		board.setPrevMove(state.getPrevX(), state.getPrevY());
		captured0.setText(capturedText(state.getWhiteCaptures()));
		captured1.setText(capturedText(state.getBlackCaptures()));
		
        buttonTop.setEnabled(state.getTurn()==playerNum);
        buttonBottom.setEnabled(state.getTurn()==playerNum);
        
        blackBowl.setShown(state.getTurn()==0);
        whiteBowl.setShown(state.getTurn()==1);
		
		//update gui for stage
        if(state.getStage() == GoGameState.SELECT_TERRITORY_STAGE) {
            //stage.setText("select territory");
            buttonTop.setText("submit proposal");
            buttonBottom.setText("");
            buttonBottom.setEnabled(false);
        } else if(state.getStage() == GoGameState.AGREE_TERRITORY_STAGE){
            //stage.setText("counter-proposal");
            buttonTop.setText("agree w/ proposal");
            buttonBottom.setText("refuse proposal");
        } else if(state.getStage() == GoGameState.MAKE_MOVE_STAGE) {
            //stage.setText("make move stage");
            buttonTop.setText("pass");
            buttonBottom.setText("forfeit");
        }
        
		revalidate();
		board.repaint();
	}
	
	private String capturedText(int score){
		switch (score){
		case 0:
			return "Captured no pieces";
		case 1:
			return "Captured 1 piece";
			default:
				return "Captured " + score + " pieces";
		}
	}
	
	/**
	 * relay the sendAction call from BoardGraphics to Game.sendAction
	 * with the proper GameAction, also use this info to update button
	 * text
	 * @param x
	 * @param y
	 */
	void sendAction(int x, int y){
		if(state==null){
            return;
        }
        //make move stage
        if(state.getStage() == GoGameState.MAKE_MOVE_STAGE)
        {
            PutPieceAction action = new PutPieceAction(player,x,y);
            game.sendAction(action);
        }//select/accept rightButton stage
        else
        {
            if(state.getTurn() == playerNum) {
                state.updateProposal(x, y);
                if( state.getStage() == GoGameState.AGREE_TERRITORY_STAGE ){
                    boolean diff = false;
                    for(int i = 0; i < GoGameState.boardSize; i++){
                        for(int j = 0; j < GoGameState.boardSize; j++){
                            if(originalTerritoryProposal[i][j] != state.getTerritoryProposal()[i][j]){
                                diff = true;
                            }
                        }
                    }
                    if(diff){
                        buttonTop.setText("counter proposal");
                    } else {
                        buttonTop.setText("agree w/ proposal");
                    }
                }
                buttonTop.invalidate();
                board.setProposal(state.getTerritoryProposal());
            }
        }
	}

	/* BUTTON ON CLICK LISTENERS */
	private class TopButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(state.getStage() == GoGameState.MAKE_MOVE_STAGE)
            {
                Log.i("onclick","sending leftButton action");
                PassAction action = new PassAction(player);
                game.sendAction(action);
                return;
            }
            if(state.getStage() == GoGameState.SELECT_TERRITORY_STAGE) //Acts as submit proposal
            {
                game.sendAction(new SelectTerritoryAction(player,state.getTerritoryProposal()));
            }
            if(state.getStage() == GoGameState.AGREE_TERRITORY_STAGE) //Acts as submit proposal
            {
                boolean diff = false;
                for(int i = 0; i < GoGameState.boardSize; i++){
                    for(int j = 0; j < GoGameState.boardSize; j++){
                        if(originalTerritoryProposal[i][j] != state.getTerritoryProposal()[i][j]){
                            diff = true;
                        }
                    }
                }
                if(diff){
                    game.sendAction(new SelectTerritoryAction(player,state.getTerritoryProposal()));
                } else {
                    game.sendAction(new AgreeTerritoryAction(player, true));
                }
            }
		}
	}

	private class BottomButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if(state.getStage() == GoGameState.MAKE_MOVE_STAGE) {
                game.sendAction(new ForfeitAction(player));
                return;
            }
            if(state.getStage() == GoGameState.AGREE_TERRITORY_STAGE) {
                game.sendAction(new AgreeTerritoryAction(player, false));
                return;
            }
		}
	}
}

