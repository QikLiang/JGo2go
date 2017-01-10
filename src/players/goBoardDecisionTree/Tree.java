package players.goBoardDecisionTree;

import action.GameAction;
import action.PutPieceAction;
import main.GoGameState;

public class Tree {

	private Node root;
	//node to keep track of progress while performing breath first search
	//through the decision space of the root game state
	private Node currentNode;
	//the child node of root that the currentNode is located in
	private Node currentBranch;
	
	public Tree(GoGameState init){
		root = new Node(null, null, init, 0);
		currentNode = null;
	}
	
	/**
	 * expand one leaf node one level and move currentNode in a
	 * breath-first-search fashion
	 */
	public synchronized void compute(){
		if(root.outcomes==null){
			root.populate();
			currentNode = null;
			return;
		}

		if(currentNode == null){
			currentNode = root.getMin();
			currentBranch = root.outcomes.get(0);
		}else{
			moveCurrentNode();
		}
		currentNode.populate();
	}
	
	/**
	 * gets the best move from root's game state and trims the tree
	 * assuming the the move returned is played
	 * @return a GameAction object where the player is null
	 */
	public synchronized GameAction pollBestMove(){
		if(root.bestOutcome == null){
			root.populate();
		}
		//if currentNode is trimmed out of the tree, set it to null
		if(root.bestOutcome != currentBranch){
			currentNode = null;
		}
		root = root.bestOutcome;
		root.parent = null;
		return root.prevMove;
	}
	
	/**
	 * used by player to notify tree of opponent's move. the tree is trimmed
	 * accordingly so that the branch containing the opponent's move becomes the
	 * new root
	 * @param action the opponent's move
	 */
	public synchronized void moveMade(GoGameState state){
		//if tree has not been populated yet, set root game state as new state
		if(root.outcomes==null){
			root = new Node(null, null, state, 0);
			currentNode = null;
			return;
		}

		//search for action in outcomes
		int moveIndex = -1;
		if (state.getTurnsPassed() > 0){
			moveIndex = 0;
		}else{
			for(int i=1; i<root.outcomes.size(); i++){
				PutPieceAction ppa = (PutPieceAction) root.outcomes.get(i).prevMove;
				if(ppa.getX()==state.getPrevX() && ppa.getY()==state.getPrevY()){
					moveIndex = i;
				}
			}
		}
		
		//if currentNode is trimmed out of the tree, set it to null
		Node moveMade = root.outcomes.get(moveIndex);
		if(moveMade != currentBranch){
			currentNode = null;
		}

		//update root
		root = moveMade;
		root.parent = null;
	}
	
	/**
	 * move currentNoce to the next leaf in the tree
	 */
	private void moveCurrentNode(){
		//move currentNode up while parent is completed
		while(currentNode.parent!=null && 
				currentNode.index>=currentNode.parent.outcomes.size()-1){
			currentNode = currentNode.parent;
		}
		
		//if currentNode reached the top
		if(currentNode.parent==null){
			//move to the next branch otherwise
			if(currentBranch.index<root.outcomes.size()-1){
				currentBranch = root.outcomes.get(currentBranch.index+1);
				currentNode = currentBranch;
			}//go to the next level if all branches are completed
			else{
				currentBranch = root.outcomes.get(0);
				currentNode = currentBranch.getMin();
			}
		}//move node to next branch of parent otherwise
		else{
			//if currentNode is the branch, move the branch also
			if(currentNode==currentBranch){
				currentBranch = root.outcomes.get(currentBranch.index+1);
				currentNode = currentBranch;
			}else{//otherwise, just move currentNode
				currentNode = currentNode.parent.outcomes.get(currentNode.index+1);
			}
		}
		
		//move to leaf of the sub-tree
		currentNode = currentNode.getMin();
	}
	
	/**
	 * calculate the depth based on how deep is the min node
	 */
	public int getDepth(){
		Node node = root;
		int depth = 0;
		while(node.outcomes!=null){
			depth++;
			node = node.outcomes.get(0);
		}
		return depth;
	}
}
