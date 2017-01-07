package players;

import main.GoGameState;
import gameMsg.GameInfo;
import gameMsg.IllegalMoveInfo;
import gameMsg.NotYourTurnInfo;
import gui.GameGraphics;

/**
 * Created by qi on 10/29/16.
 */

public class GoHumanPlayer extends GamePlayer{

    boolean firstTime = true;
    GameGraphics graphics;

    public GoHumanPlayer(String name, boolean useGui) {
        super(name, useGui);
    }

    @Override
    public void receiveInfo(GameInfo info)
    {
        if (info instanceof IllegalMoveInfo || info instanceof NotYourTurnInfo) {
            // if the move was out of turn or otherwise illegal, flash the screen
            //flash(Color.rgb(255,0,0), 300);
            return;
        }
        else if (!(info instanceof GoGameState)) {
            // if we do not have a GoGameState, ignore
            return;
        }

        GoGameState state = (GoGameState) info;

        if(state.getStage() == GoGameState.SELECT_TERRITORY_STAGE || state.getStage() == GoGameState.AGREE_TERRITORY_STAGE){
            if(state.getTerritoryProposal() == null){
                state.setTerritoryProposal(state.getTerritorySuggestion());
            }
        }
        
        if(graphics!=null){
			graphics.setState(state);
        }
    }

	@Override
    protected void initAfterReady(){
    	graphics = new GameGraphics(allPlayerNames, game, this, playerNum);
    }

	@Override
	public boolean requiresGui() {
		return true;
	}

	@Override
	public boolean supportsGui() {
		return true;
	}
	
	@Override
	public String toString(){
		return "Human Player";
	}
}