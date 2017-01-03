package players;

import action.GameAction;
import main.Game;
import main.GoGameState;
import gameMsg.GameInfo;
import gameMsg.IllegalMoveInfo;
import gameMsg.NotYourTurnInfo;

/**
 * Created by qi on 10/29/16.
 */

public class GoHumanPlayer extends GamePlayer{// implements View.OnTouchListener, View.OnClickListener {

    boolean firstTime = true;
    //GoSurfaceView surfaceView;
    GoGameState state;

    private int[][] originalTerritoryProposal;


    public GoHumanPlayer(String name) {
        super(name);
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

        state = (GoGameState) info;

        originalTerritoryProposal = GoGameState.boardDeepCopy(state.getTerritoryProposal());

        if(state.getStage() == GoGameState.SELECT_TERRITORY_STAGE || state.getStage() == GoGameState.AGREE_TERRITORY_STAGE){
            if(state.getTerritoryProposal() == null){
                state.setTerritoryProposal(state.getTerritorySuggestion());
            }
        }
    }


	@Override
	public boolean requiresGui() {
		return true;
	}

	@Override
	public boolean supportsGui() {
		return true;
	}
}
