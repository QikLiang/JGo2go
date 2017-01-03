package action;

import players.GamePlayer;

/**
 * Created by qi on 10/29/16.
 */

public class PutPieceAction extends GameAction {

    //coordinates to put the piece
    private int x;
    private int y;

    /**
     * constructor for GameAction
     *
     * @param player the player who created the action
     */
    public PutPieceAction(GamePlayer player, int initX, int initY) {
        super(player);
        x = initX;
        y = initY;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }
}
