package players;

import java.util.concurrent.LinkedBlockingQueue;

import action.GameOverAckAction;
import action.MyNameIsAction;
import action.ReadyAction;
import gameMsg.BindGameInfo;
import gameMsg.GameInfo;
import gameMsg.GameOverInfo;
import gameMsg.StartGameInfo;
import main.Game;
import main.Log;

/**
 * A player who plays a (generic) game. Each class that implements a player for
 * a particular game should implement this interface.
 * 
 * @author Steven R. Vegdahl
 * @author Andrew M. Nuxoll
 * @author Qi Liang
 * @version Jan 2017
 */

public abstract class GamePlayer {
	
	protected Game game;
	protected String name;
	protected int playerNum; // my player ID
	protected String[] allPlayerNames;
	private LinkedBlockingQueue<GameInfo> queue;
	private PlayerThread pt;
	public boolean gameOver;
	protected boolean hasGui;
	
	public GamePlayer(String name, boolean initGui){
		this.name = name;
		hasGui = initGui;
		queue = new LinkedBlockingQueue<>();
		pt = new PlayerThread();
	}
	
	// sends a message to the player
	public final void sendInfo(GameInfo info){
		queue.add(info);
	}
	
	// the method that processes information sent from game
	protected abstract void receiveInfo(GameInfo info);
	
	// start the player
	public void start(){
		pt.start();
	}
	
	// whether this player requires a GUI
	public abstract boolean requiresGui();
	
	// whether this player supports a GUI
	public abstract boolean supportsGui();

	protected void initAfterReady() {
		//overwritten by sub-class
		
	}
	
	protected void cleanUpAfterGameOver(){
		//overwritten by sub-class
	}

	/**
	 * require toString to be overriden so the start graphics can show it 
	 * properly in combo box
	 */
	public abstract String toString();

	/**
	 * Thread for dispatching info from game
	 * @author Qi Liang
	 *
	 */
	private class PlayerThread extends Thread{
		public void run(){
			while (!gameOver){
				GameInfo myInfo;
				try {
					myInfo = queue.take();
				} catch (InterruptedException e) {
					Log.i(name, "interrupted");
					continue;
				}
				if (game == null) {
					// game has not been bound: the only thing we're looking for is
					// BindGameInfo object; ignore everything else
					if (myInfo instanceof BindGameInfo) {
						Log.i("GamePlayer", "binding game");
						BindGameInfo bgs = (BindGameInfo)myInfo;
						game = bgs.getGame(); // set the game
						playerNum = bgs.getPlayerNum(); // set our player id
						
						// respond to the game, telling it our name
						game.sendAction(new MyNameIsAction(GamePlayer.this, name));
					}
				}
				else if (allPlayerNames == null) {
					// here, the only thing we're looking for is a StartGameInfo object;
					// ignore everything else
					if (myInfo instanceof StartGameInfo) {
						Log.i("GamePlayer", "notification to start game");
						
						// update our player-name array
						allPlayerNames = ((StartGameInfo)myInfo).getPlayerNames();

						// perform game-specific initialization
						initAfterReady();
						
						// tell the game we're ready to play the game
						game.sendAction(new ReadyAction(GamePlayer.this));
					}
				}
				else if (myInfo instanceof GameOverInfo) {
					// if we're being notified the game is over, finish up
					
					// perform the "gave over" behavior--by default, to show pop-up message
					//gameIsOver(((GameOverInfo)myInfo).getMessage());
					
					// acknowledge to the game that the game is over
					game.sendAction(new GameOverAckAction(GamePlayer.this));
					
					// set our instance variable, to indicate the game as over
					gameOver = true;
					
					cleanUpAfterGameOver();
				}
				else {
					// pass the state on to the subclass
					receiveInfo(myInfo);
				}
			}
		}
	}

}// interface GamePlayer
