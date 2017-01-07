package main;

import gui.SelectPlayer;
import players.GamePlayer;
import players.GoComputerPlayer0;
import players.GoHumanPlayer;

public class Framework {
	
	public static void main(String[] args){
		//new GoLocalGame().start(setupPlayers());
		new GoLocalGame().start(SelectPlayer.getPlayers());
	}

	private static GamePlayer[] setupPlayers(){
		String[] names = {"Human", "Computer"};
		GoHumanPlayer p1 = new GoHumanPlayer(names[0]);
		GoComputerPlayer0 p2 = new GoComputerPlayer0(names[1]);
		GamePlayer[] players = {p2, p1};
		return players;
	}
}
