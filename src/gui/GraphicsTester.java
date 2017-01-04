package gui;

import main.GoGameState;
import main.Log;

/**
 * a dummy class that instantiates a GameGraphics object to test it
 * @author Qi Liang
 *
 */
public class GraphicsTester {
	public static void main(String[] args){
		String[] asdf = {"asdf", "fdsa"};
		GameGraphics gg = new GameGraphics(asdf);
		gg.startGraphics();
		GoGameState state = new GoGameState();
		gg.setState(state);
		Log.i("Graphics Tester", "empty board set");
		try { Thread.sleep(2000); } catch (InterruptedException e) { }
		state.updateBoard(0, 0, 0);
		gg.setState(state);
		Log.i("Graphics Tester", "1st piece set");
		try { Thread.sleep(2000); } catch (InterruptedException e) { }
		state.updateBoard(1, 0, 1);
		gg.setState(state);
		Log.i("Graphics Tester", "2nd piece set");
	}

}