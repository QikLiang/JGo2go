package players;

import action.AgreeTerritoryAction;
import action.GameAction;
import action.PassAction;
import action.PutPieceAction;
import action.SelectTerritoryAction;
import gameMsg.GameInfo;
import main.GoGameState;
import main.Log;
import players.goBoardDecisionTree.Tree;

public class GoComputerPlayer2 extends GamePlayer {

	Tree tree;
	
    public GoComputerPlayer2(String name, boolean useGui) {
        super(name, useGui);
        tree = new Tree(new GoGameState());
    }

	@Override
	protected void receiveInfo(GameInfo info) {

        //returns if there's incorrect input
        if (!(info instanceof GoGameState)) {
            Log.i( "message", info.toString() );
            return;
        }

        GoGameState state = (GoGameState) info;

        //returns if it's not the computer's turn
        if (state.getTurn() != playerNum) {
            return;
        }

        //Determines the move if it's time in the game to place a stone
        if(state.getStage() == GoGameState.MAKE_MOVE_STAGE) {
        	//pass if other player passes
        	if(state.getTurnsPassed()>0){
        		game.sendAction(new PassAction(this));
        	}else{
				tree.moveMade(state);
				long startTime = System.currentTimeMillis();
				while(System.currentTimeMillis()-startTime < 2000){
					tree.compute();
				}
				GameAction action =tree.pollBestMove();
				if(action instanceof PassAction){
					game.sendAction(new PassAction(this));
				}else{
					PutPieceAction move = (PutPieceAction) action;
					game.sendAction(new PutPieceAction(this, move.getX(), move.getY()));
				}
        	}
        }

        //always agree to other player's proposal
        else if(state.getStage() == GoGameState.SELECT_TERRITORY_STAGE){
            game.sendAction(new SelectTerritoryAction(this, state.getTerritorySuggestion()));
        }
        else {
            game.sendAction(new AgreeTerritoryAction(this, true));
        }

	}

	@Override
	public boolean requiresGui() {
		return false;
	}

	@Override
	public boolean supportsGui() {
		return false;
	}

	@Override
	public String toString() {
		return "better smart player";
	}

}
