package players.goBoardDecisionTree;

import java.util.ArrayList;

import action.GameAction;
import action.PassAction;
import action.PutPieceAction;
import main.GoGameState;
import main.Log;

/**
 * this class represents one node on the possible decision
 * space of a go board. It is meant to be used and managed
 * by the Tree class in the same package
 * 
 * @author Qi Liang
 *
 */
class Node {

	Node parent;
	int index;//the index of this node in parent's outcome
	GoGameState state;
	ArrayList<Node> outcomes;
	GameAction prevMove;
	Node bestOutcome;
	double score;
	
	public Node(Node parent, GameAction prevMove, GoGameState state, int index){
		this.parent = parent;
		this.prevMove = prevMove;
		this.state = state;
		this.index = index;
		evaluateScore();
	}
	
    /**
     * evaluate the board to see how advantageous the position is for the player to move
     * @return positive if favorable to player to move, negative otherwise
     */
    private void evaluateScore(){
        score = state.getWhiteCaptures() - state.getBlackCaptures();
        if(state.getTurn()!=0){
            score *= -1;
        }
        
        double offset = 0; //offset score based on heuristics
        
        //discourage AI from randomly passing in the middle of the game
        if(prevMove instanceof PassAction){
        	offset-=0.8;
        }else if (prevMove instanceof PutPieceAction){
        	//value moves in the center of the board more
            int center = (GoGameState.boardSize)/2;
            PutPieceAction move = (PutPieceAction) prevMove;
			offset -= Math.sqrt( (move.getX()-center)*(move.getX()-center)+
					(move.getY()-center)*(move.getY()-center) ) /
					GoGameState.boardSize;
        }
        
        score += offset;
    }

    /**
     * populate the node's outcome array with all possible moves under the
     * node's game state
     */
	public void populate(){
		//make a list of all possible board positions after current turn
		outcomes = new ArrayList<>(GoGameState.boardSize*GoGameState.boardSize+1);
		
		//add the outcome of current player passing
		GoGameState pass = new GoGameState(state);
		pass.changeTurn();
		outcomes.add(new Node(this, new PassAction(null), pass, 0));

		//add the outcomes of current player making a move
		//search each possible place to make a move
		for(int i=0; i<GoGameState.boardSize; i++){
			for(int j=0; j<GoGameState.boardSize; j++){
				//make a copy of board
				GoGameState temp = new GoGameState(state);
				//if the move is possible
				if(temp.updateBoard(state.getTurn(), i, j)){
					temp.changeTurn();
					outcomes.add( new Node(this, new PutPieceAction(null, i, j),
							pass, outcomes.size()) );
				}
			}
		}

		updateScore();
	}
	
	/**
	 * @return the minimum node/leaf of the sub-tree with this node as root
	 */
	public Node getMin(){
		Node node = this;
		while(node.outcomes!=null){
			//don't need to outcomes.size because pass is always a legal move
			node = node.outcomes.get(0);
		}
		return node;
	}
	
	/**
	 * if this node has been populated, update its score to the best outcome out
	 *  of what moves are possible. If the score is changed, also recursively
	 * update and propagate the score up the tree
	 */
	public void updateScore(){
		if(outcomes == null){
			return;
		}
		
		//get best outcome's score
		double tempScore;
		bestOutcome = outcomes.get(0);
		score = -bestOutcome.score;
		for(int i = 0; i<outcomes.size(); i++){
			tempScore = -outcomes.get(i).score;
			if(tempScore < score){
				score = tempScore;
				bestOutcome = outcomes.get(i);
			}
		}
		
		//propagate new score up the tree
		Node node = this;
		while(node.parent != null && -node.score < node.parent.score){
			node.parent.score = -node.score;
			node = node.parent;
		}
	}
	
	/**
	 * this method is called by java's garbage collector when a object is being
	 * deleted. This method is being overriden to ensure there's no memory leaks
	 */
	@Override
	public void finalize(){
		//only log if node is root to avoid flooding the log
		if(parent == null){
			Log.i("Decision tree", "root being deleted");
		}
	}
}