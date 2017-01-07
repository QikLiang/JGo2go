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
}
