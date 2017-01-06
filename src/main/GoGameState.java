package main;

import main.Log;
import java.util.ArrayList;
import gameMsg.GameInfo;

/**
 * Created by qi on 10/29/16.
 */

public class GoGameState extends GameInfo
{
    /* STATIC CONSTANTS */
    //for stage of game
    public static final int MAKE_MOVE_STAGE = 0;
    public static final int SELECT_TERRITORY_STAGE = 1;
    public static final int AGREE_TERRITORY_STAGE = 2;

    //for pieces on the board
    public static final int WHITE = -1;
    public static final int BLACK = 1;
    public static final int EMPTY = 0;
    public static final int WHITE_CAPTURED = 2;
    public static final int BLACK_CAPTURED = -2;

    //size of the board
    public static final int boardSize = 9;

    /* GAME STATE INFORMATION */
    private int stage;//stage of the game
    private int turnsPassed;//number of consecutive passes made
    private int whiteCaptures;//number of white pieces captured
    private int blackCaptures;//number of black pieces captured
    private int turn;//which player's turn it is
    private int board [][];//representation of the board
    private int territoryProposal[][];//representation of the most recent proposal
    private ArrayList<int[][]> pastBoards ;
    int pieceTakeInPast = 0;
    private int prevX=-1;
    private int prevY=-1;


    /* METHODS FOR INTERACTING WITH GAME STATE */

    /**
     * given what player to move and where that player wants to put a piece,
     * return whether or not such move will be legal.
     * @param player index of player to move
     * @param moveX the move's x coordinate
     * @param moveY the move's y coordinate
     * @return true if move is legal, false otherwise
     */
    //None of this method is tested yet
    public boolean isLeagalMove(int player, int moveX, int moveY){
        //add check for Kos
        //The idea is to create a hashtable with numbers for the move number, and a boolean area to save each board state history
        int piece[] = {BLACK, WHITE};
        //if the move was not on the board, return false
        if(moveX<0 || moveX>=board.length || moveY<0 || moveY>=board[0].length){return false;}
        //check if a piece is there
        if(board[moveX][moveY]!=EMPTY){return false;}
        //check if you're committing suicide
        if( isPieceNextTo(EMPTY, board, moveX, moveY) ){
            return true;
        }
        //checking if you took pieces
        int[][] newboard = boardDeepCopy(board);
        newboard[moveX][moveY]=piece[player];
        
        ArrayList<int[]> removedPieces = getTaken(newboard,-piece[player]);
        if(removedPieces.size()!=0){
            //if pieces would be removed, take them away from the new board
            for(int i=0;i<removedPieces.size();i++){
                newboard[removedPieces.get(i)[0]][removedPieces.get(i)[1]] = EMPTY;
            }
            //check to see if it's the same as a past board (check for Kos)
            for(int i=0;i<pastBoards.size();i++){
                outterloop:
                for(int j=0;j<board.length;j++){
                    for(int k=0;k<board[0].length;k++){
                        if(pastBoards.get(i)[j][k]!=newboard[j][k]){
                            break outterloop;
                        }
                        if(j==board.length-1 && k==board.length-1){
                            return false;
                        }
                    }
                }
            }
            return true;
        }
        
        int[][] newboard2 = boardDeepCopy(board);
        newboard2[moveX][moveY]=piece[player];
        
        //checks for suicide
        ArrayList<int[]> removedPieces2 = getTaken(newboard2,piece[player]);
        if(removedPieces2.size()!=0){
            return false;
        }
        return true;
    }

    public void updateProposal(int x, int y){
        //althernate the color of a spot between blank, black, and white
        int newColor = (territoryProposal[x][y]+2)%3 -1;
        //an array for which spots need to be updated based on the new input
        boolean[][] change = new boolean[boardSize][boardSize];
        change[x][y]=true;//the spot user picked is definite going to change
        boolean flag = true;
        while(flag){
            flag=false;
            for(int i=0; i<boardSize; i++){
                for(int j=0; j<boardSize; j++){
                    //mark for change if it is next to a changing piece and it's not already
                    //that color
                    //don't mark for change if it's already marked
                    if(board[i][j]!=board[x][y] || change[i][j]){
                        continue;
                    }

                    /* agressive modification version
                    if(territoryProposal[i][j]==newColor || change[i][j]){
                        continue;
                    }//uncaptured pieces are empty instead of in their own color
                    if(board[i][j]!=0 && board[i][j]==newColor){
                        continue;
                    }*/
                    if(i>0 && change[i-1][j] ){
                        flag=true;
                        change[i][j]=true;
                    }else if(j>0 && change[i][j-1] ){
                        flag=true;
                        change[i][j]=true;
                    }else if(i<boardSize-1 && change[i+1][j] ){
                        flag=true;
                        change[i][j]=true;
                    }else if(j<boardSize-1 && change[i][j+1] ){
                        flag=true;
                        change[i][j]=true;
                    }
                }
            }
        }

        for(int i=0; i<boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if(change[i][j]){
                    //empty squares alternate between empty, black, and white
                    if(board[i][j]==0){
                        territoryProposal[i][j] = newColor;
                    }else{//pieces squares alternate between captured and not captured
                        if(territoryProposal[i][j]==0){
                            territoryProposal[i][j]=-board[i][j];
                        }else{
                            territoryProposal[i][j]=0;
                        }
                    }
                }
            }
        }
    }

    /**
     * alternate whose turn it is to move.
     */
    public void changeTurn(){
        Log.i("change of turn","");turn=1-turn;
    }

    /**
     * update the state of the board given a player's move.
     * The piece is added on to the board, any captured pieces are removed from the board,
     * and the players' pieces captured count would be updated.
     * @param player index of player to move
     * @param moveX the move's x coordinate
     * @param moveY the move's y coordinate
     * @return true if updated successfully
     */
    public boolean updateBoard(int player, int moveX, int moveY){
        int piece[] = {BLACK, WHITE};
        if(isLeagalMove(player, moveX, moveY)){
            board[moveX][moveY]= piece[player];
            
            //this section deletes surrounded tiles
            ArrayList<int[]> removedPieces = getTaken(board,-piece[player]);
            for(int i=0;i<removedPieces.size();i++){
                //resets the past boards if there were pieces taken

                board[removedPieces.get(i)[0]][removedPieces.get(i)[1]] = EMPTY;
                if(piece[player]==WHITE){
                    blackCaptures++;
                }
                else{
                    whiteCaptures++;
                }
            }
            if(removedPieces.size()>0) {
                for(int j=0;j<pieceTakeInPast;j++){
                    pastBoards.remove(0);
                    pastBoards.trimToSize();
                }
                pieceTakeInPast = pastBoards.size();
            }
            //adds this board to the past boards
            pastBoards.add(boardDeepCopy(board));
            //the 'in danger' section surrounded by these comments is not tested

            prevX = moveX;
            prevY = moveY;
            return true;
        }

        return false;
    }

    /**
     * returns whether a coordinate is next to a certain piece
     * @param piece the piece to check for in the surrounding
     * @param board the game board
     * @param x the coordinate to check the surrounding of
     * @param y the coordinate to check the surrounding of
     * @return true if piece exist surrounding coordinate, false otherwise
     */
    private static boolean isPieceNextTo( int piece, final int board[][], int x, int y){
        if(x>0 && board[x-1][y]==piece){
            return true;
        }
        if(y>0 && board[x][y-1]==piece){
            return true;
        }
        if(x<board.length-1 && board[x+1][y]==piece){
            return true;
        }
        if(y<board[0].length-1 && board[x][y+1]==piece){
            return true;
        }

        return false;
    }

    //This method gets returns deleted pieces
    //This method is also not tested
    public ArrayList<int[]> getTaken(int[][] boardToCheck, int piecesToCheck){
        ArrayList<int[]> piecesToDelete = new ArrayList<int[]>();
        int IN_DANGER = 4;
        //Loop determines pieces as in Danger or not
        for(int i=0;i<boardToCheck.length;i++){
            for(int j=0;j<boardToCheck[0].length;j++){
                if(boardToCheck[i][j]==piecesToCheck){
                    if(!isPieceNextTo(EMPTY,boardToCheck,i,j)){
                        boardToCheck[i][j]=IN_DANGER;
                    }
                }
            }
        }
        boolean changing = true;
        while(changing){
        changing=false;
            for(int i=0;i<boardToCheck.length;i++){
                for(int j=0;j<boardToCheck[0].length;j++){
                    if(boardToCheck[i][j]==IN_DANGER){
                        if(isPieceNextTo(piecesToCheck,boardToCheck,i,j)){
                            boardToCheck[i][j]=piecesToCheck;
                            changing=true;
                        }
                    }
                }
            }
        }
        for(int i=0;i<boardToCheck.length;i++){
            for(int j=0;j<boardToCheck[0].length;j++){
                if(boardToCheck[i][j]==IN_DANGER){
                    int[] deletePiece = new int[2];
                    deletePiece[0]=i;
                    deletePiece[1]=j;
                    piecesToDelete.add(deletePiece);
                }
            }
        }
        return piecesToDelete;
    }

    /* CONSTRUCTORS */
    /**
     * constructor
     */
    public GoGameState(){
        super();
        pastBoards = new ArrayList<int[][]>();
        stage = MAKE_MOVE_STAGE;
        turnsPassed = 0;
        whiteCaptures = 0;
        blackCaptures = 0;
        turn = 0;
        board = new int[boardSize][boardSize];
        territoryProposal = null;
    }

    /**
     * copy constructor
     */
    public GoGameState( GoGameState original){
        stage = original.stage;
        turnsPassed = original.turnsPassed;
        whiteCaptures = original.whiteCaptures;
        blackCaptures = original.blackCaptures;
        turn = original.turn;
        territoryProposal = boardDeepCopy(original.territoryProposal);
        board = boardDeepCopy(original.board);
        prevX = original.prevX;
        prevY = original.prevY;
        pastBoards = new ArrayList<int[][]>();
        for(int i=0; i<original.pastBoards.size(); i++){
            pastBoards.add( boardDeepCopy(original.pastBoards.get(i)) );
        }
    }

    /* GETTERS */

    public int getStage() {
        return stage;
    }

    public int getTurnsPassed() {
        return turnsPassed;
    }

    public int getWhiteCaptures() {
        return whiteCaptures;
    }

    public int getBlackCaptures() {
        return blackCaptures;
    }

    public int getPrevX() {
        if( turnsPassed>0 || stage!=MAKE_MOVE_STAGE ){
            return -1;
        }
        return prevX;
    }

    public int getPrevY() {
        if( turnsPassed>0 || stage!=MAKE_MOVE_STAGE ){
            return -1;
        }
        return prevY;
    }

    public int getTurn() {
        return turn;
    }

    public int[][] getBoard() {
        return boardDeepCopy(board);
    }

    public int[][] getTerritoryProposal() {
        return boardDeepCopy(territoryProposal);
    }

    /* SETTERS */

    public void setTerritoryProposal(int proposal[][]){
        this.territoryProposal = boardDeepCopy(proposal);
    }

    public void setStage(int stage){
        this.stage = stage;
    }

    /* RESETTER */

    public void resetTurnsPassed(){ turnsPassed = 0; }

    /* helper methods */

    /**
     * @param original
     * @return a deep copy of a game board (or any 2d int array)
     */
    public static int[][] boardDeepCopy(int original[][]){
        if(original==null){
            return null;
        }

        int temp[][] = new int[original.length][];
        for(int i=0; i<original.length; i++){
            if(original[i]!=null){
                temp[i] = new int[original[i].length];
                for(int j=0; j<original[i].length; j++){
                    temp[i][j] = original[i][j];
                }
            }
        }
        return temp;
    }

    public void incrementPasses(){
        turnsPassed++;
        if(turnsPassed >= 2){
            stage = SELECT_TERRITORY_STAGE;
        }
    }

    /**
     * creates a suggested territory proposal.
     *
     * THIS METHOD IS AS YET UNTESTED AND SHOULD BE TREATED AS POTENTIALLY VERY BUGGY!
     *
     * @return suggested array
     */
    public int[][] getTerritorySuggestion(){
        int[][] suggestion = new int[boardSize][boardSize];

        final int undecided = 100;
        final int deciding = 101;
        int knownColor;
        ArrayList<Integer[]> currSquares = new ArrayList<Integer[]>();
        //int[][][] currSquares = new int[boardSize * boardSize][boardSize][boardSize];

        //Initialize the array
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                if(board[i][j] == EMPTY) {
                    suggestion[i][j] = undecided;
                } else {
                    suggestion[i][j] = EMPTY;
                }
            }
        }
        //Determine what the other spots should be
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                if(suggestion[i][j] == undecided)
                {
                    knownColor = undecided;
                    Integer[] spot = {i,j};
                    currSquares.add(spot);
                    suggestion[i][j] = deciding;
                    int counter = 0;
                    while(counter < currSquares.size()){
                        Integer[] curr = currSquares.get(counter);
                        try{
                            Integer[] spot2 = {curr[0] - 1,curr[1]};
                            if(suggestion[spot2[0]][spot2[1]] != deciding){
                                if(suggestion[spot2[0]][spot2[1]] == undecided) {
                                    suggestion[spot2[0]][spot2[1]] = deciding;
                                    currSquares.add(spot2);
                                } else if (knownColor == undecided || knownColor == board[spot2[0]][spot2[1]]){
                                    knownColor = board[spot2[0]][spot2[1]];
                                } else if (knownColor != board[spot2[0]][spot2[1]]){
                                    knownColor = EMPTY;
                                }
                            }
                        }catch (ArrayIndexOutOfBoundsException e){}
                        try{
                            Integer[] spot2 = {curr[0] + 1,curr[1]};
                            if(suggestion[spot2[0]][spot2[1]] != deciding){
                                if(suggestion[spot2[0]][spot2[1]] == undecided) {
                                    suggestion[spot2[0]][spot2[1]] = deciding;
                                    currSquares.add(spot2);
                                } else if (knownColor == undecided || knownColor == board[spot2[0]][spot2[1]]){
                                    knownColor = board[spot2[0]][spot2[1]];
                                } else if (knownColor != board[spot2[0]][spot2[1]]){
                                    knownColor = EMPTY;
                                }
                            }
                        }catch (ArrayIndexOutOfBoundsException e){}
                        try{
                            Integer[] spot2 = {curr[0],curr[1] - 1};
                            if(suggestion[spot2[0]][spot2[1]] != deciding){
                                if(suggestion[spot2[0]][spot2[1]] == undecided) {
                                    suggestion[spot2[0]][spot2[1]] = deciding;
                                    currSquares.add(spot2);
                                } else if (knownColor == undecided || knownColor == board[spot2[0]][spot2[1]]){
                                    knownColor = board[spot2[0]][spot2[1]];
                                } else if (knownColor != board[spot2[0]][spot2[1]]){
                                    knownColor = EMPTY;
                                }
                            }
                        }catch (ArrayIndexOutOfBoundsException e){}
                        try{
                            Integer[] spot2 = {curr[0],curr[1] + 1};
                            if(suggestion[spot2[0]][spot2[1]] != deciding){
                                if(suggestion[spot2[0]][spot2[1]] == undecided) {
                                    suggestion[spot2[0]][spot2[1]] = deciding;
                                    currSquares.add(spot2);
                                } else if (knownColor == undecided || knownColor == board[spot2[0]][spot2[1]]){
                                    knownColor = board[spot2[0]][spot2[1]];
                                } else if (knownColor != board[spot2[0]][spot2[1]]){
                                    knownColor = EMPTY;
                                }
                            }
                        }catch (ArrayIndexOutOfBoundsException e){}

                        counter++;
                    }

                    while(!currSquares.isEmpty()){
                        Integer[] curr = currSquares.remove(0);
                        suggestion[curr[0]][curr[1]] = knownColor;
                    }
                }
            }
        }

        //Error checking
        for(int i = 0; i < boardSize; i++){
            for(int j = 0; j < boardSize; j++){
                if(suggestion[i][j] != EMPTY && suggestion[i][j] != WHITE && suggestion[i][j] != BLACK){
                    Log.i("Suggestion", "Bad suggestion");
                }
            }
        }

        return suggestion;
    }
}
