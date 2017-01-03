package action;

import players.GamePlayer;
import main.GoGameState;

/**
 * Created by qi on 10/29/16.
 */

public class SelectTerritoryAction extends GameAction {
    private int proposal[][];
    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public SelectTerritoryAction(GamePlayer player, int initProposal[][]) {
        super(player);
        proposal = GoGameState.boardDeepCopy(initProposal);
    }

    /**
     * returns a deep copy of the proposal
     * @return
     */
    public int[][] getProposal(){
        return GoGameState.boardDeepCopy(proposal);
    }
}
