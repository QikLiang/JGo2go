package action;

import players.GamePlayer;

/**
 * Created by qi on 10/29/16.
 */

public class AgreeTerritoryAction extends GameAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 335850741816698692L;
	private boolean agreement;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public AgreeTerritoryAction(GamePlayer player, boolean initAgreement) {
        super(player);
        agreement = initAgreement;
    }

    public boolean getAgreement(){
        return agreement;
    }
}
