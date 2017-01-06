package main;

import action.AgreeTerritoryAction;
import action.ForfeitAction;
import action.GameAction;
import action.PassAction;
import action.PutPieceAction;
import action.SelectTerritoryAction;
import players.GamePlayer;

/**
 * Created by qi on 10/29/16.
 */

public class GoLocalGame extends LocalGame {
	
	// the players in the game, in order of  player number
	protected GamePlayer[] players;
	// the players' names, paralleling the 'players' array
	protected String[] playerNames;

    GoGameState officialState;
    private boolean gameEnded;
    private boolean forfeit = false;
    private static boolean reset=false;
    
    public GoLocalGame(){
    	super();
    	officialState = new GoGameState();
    }
    
    @Override
    protected void sendUpdatedStateTo(GamePlayer p) {
        p.sendInfo(new GoGameState(officialState));
    }

    @Override
    protected boolean canMove(int playerIdx) {
        return officialState.getTurn()==playerIdx;
    }

    protected String checkIfGameOver() {
        if(forfeit){
            reset=true;
            return "Game ended: " + playerNames[officialState.getTurn()] + " has yielded the match!";
        }
        if(gameEnded){
            return playerNames[0] + " score: " + calcScore(0) + "\n" + playerNames[1] + " score: " + calcScore(1);
        }else {
            return null;
        }
    }

    private int calcScore(int playerIdx) {
        int score;
        int playerColor;
        if(playerIdx == 0)
        {
            score = officialState.getWhiteCaptures();
            playerColor = GoGameState.BLACK;
        } else {
            score = officialState.getBlackCaptures();
            playerColor = GoGameState.WHITE;
        }

        int[][] tempBoard = officialState.getTerritoryProposal();

        for(int i = 0; i < GoGameState.boardSize; i++){
            for(int j = 0; j < GoGameState.boardSize; j++){
                if(tempBoard[i][j] == playerColor){
                    score++;
                    if(officialState.getBoard()[i][j] == 0 - playerColor){
                        score++;
                    }
                }
            }
        }

        return score;
    }

    @Override
    protected boolean makeMove(GameAction action) {
        //reject if not that player's turn
        if(getPlayerIdx(action.getPlayer()) != officialState.getTurn()){
            return false;
        }

        if(action instanceof PutPieceAction){
            //reject if not in right state of game
            if(officialState.getStage() != GoGameState.MAKE_MOVE_STAGE){
                Log.i("Wrong stage", ""+officialState.getStage());
                return false;
            }

            officialState.resetTurnsPassed();

            PutPieceAction move = (PutPieceAction) action;
            Log.i("Put Piece", ""+getPlayerIdx(move.getPlayer())+","+move.getX()+","+move.getY());
            //if move is legal
            if(officialState.isLeagalMove(getPlayerIdx(action.getPlayer()),
                    move.getX(), move.getY())){
                //update board position
                boolean moveMade = officialState.updateBoard(getPlayerIdx(action.getPlayer()),
                    move.getX(), move.getY());
                //switch player
                officialState.changeTurn();
                Log.i(moveMade ? "Success":"Failed", ""+getPlayerIdx(move.getPlayer())+","+move.getX()+","+move.getY());
                return true;
            }else {
                Log.i("Illegal move", ""+getPlayerIdx(move.getPlayer())+","+move.getX()+","+move.getY());
                return false;
            }
        }

        if(action instanceof PassAction){
            //reject if not in right state of game
            if(officialState.getStage() != GoGameState.MAKE_MOVE_STAGE){
                return false;
            }
            Log.i("Pass", ""+getPlayerIdx(((PassAction) action).getPlayer()));
            officialState.changeTurn();
            officialState.incrementPasses();
            return true;
        }

        if(action instanceof SelectTerritoryAction){
            //determine if we are in the correct stage
            if(officialState.getStage() != GoGameState.SELECT_TERRITORY_STAGE && officialState.getStage() != GoGameState.AGREE_TERRITORY_STAGE){
                return false;
            }

            officialState.setStage(GoGameState.AGREE_TERRITORY_STAGE);

            SelectTerritoryAction proposal = (SelectTerritoryAction) action;
            //reject if proposal is empty
            if(proposal.getProposal() == null){
                return false;
            }

            Log.i("Select Territory", ""+getPlayerIdx(proposal.getPlayer()));
            officialState.changeTurn();
            officialState.setTerritoryProposal(proposal.getProposal());
            return true;
        }

        if(action instanceof AgreeTerritoryAction){
            //reject if not in right state of game
            if(officialState.getStage() != GoGameState.AGREE_TERRITORY_STAGE){
                return false;
            }

            boolean agreement = ((AgreeTerritoryAction) action).getAgreement();

            Log.i("agreement", ""+((AgreeTerritoryAction)action).getPlayer()+","+agreement);
            gameEnded = agreement;
            if(!agreement){
                officialState.setTerritoryProposal(null);
                officialState.resetTurnsPassed();
                officialState.setStage(GoGameState.MAKE_MOVE_STAGE);
            }
            return true;
        }

        if(action instanceof ForfeitAction){
            forfeit = true;
            return true;
        }


    //if it gets to the end without matching any actions, something is wrong
    return false;
}
    public static boolean getReset(){return reset;}//returns value of reset to reset the game after completion
}
