package gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import main.Log;
import players.*;

public class SelectPlayer {

	public static GamePlayer[] getPlayers(){
		JFrame window = new JFrame("JGo2go");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setSize(300, 200);

		Box layoutPanel = Box.createVerticalBox();
		window.add(Box.createVerticalGlue());
		window.add(layoutPanel);
		
		Box playerPanel = Box.createVerticalBox();
		layoutPanel.add(playerPanel);

		Box labels = Box.createHorizontalBox();
		playerPanel.add(labels);
		labels.add(new JLabel("  Name"));
		labels.add(Box.createHorizontalGlue());
		labels.add(new JLabel("Player Type        GUI "));
		
		GuiRow row1 = new GuiRow();
		GuiRow row2 = new GuiRow();
		playerPanel.add(row1.getRow());
		playerPanel.add(row2.getRow());
		
		layoutPanel.add(Box.createVerticalGlue());
		
		JPanel buttonRow = new JPanel();
		buttonRow.setLayout(new FlowLayout(FlowLayout.RIGHT));
		buttonRow.setPreferredSize(new Dimension(300, 30));
		buttonRow.setMaximumSize(new Dimension(300, 30));
		buttonRow.setMinimumSize(new Dimension(300, 30));

		layoutPanel.add(buttonRow);
		JButton start = new JButton("Start");
		buttonRow.add(start);
		
		window.setVisible(true);
		
		//stop thread until start is pressed
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				synchronized(start){
					start.notifyAll();
				}
			}
		});
		try {
			synchronized(start){
				start.wait();
			}
		} catch (InterruptedException e) {
			Log.i("SelectPlayer", "graphics interruppted befure start is pressed");
		}

		window.setVisible(false);
		GamePlayer[] players = {row1.makePlayer(), row2.makePlayer()};
		return players;
	}
	
	/**
	 * create a graphical object for user to give information necessary
	 * for creating a GamePlayer and creates said player when makePlayer is
	 * called
	 * @author Qi Liang
	 *
	 */
	private static class GuiRow implements ActionListener{
		private Box row;
		private JTextField name;
		private JComboBox<GamePlayer> type;
		private JCheckBox gui;
		
		private final GamePlayer[] playerTypes = { new GoHumanPlayer("", false),
				new GoComputerPlayer0("", false), new GoComputerPlayer1("", false) };

		public GuiRow(){
			row = Box.createHorizontalBox();
			row.setPreferredSize(new Dimension(300, 30));
			row.setMaximumSize(new Dimension(300, 30));
			row.setMinimumSize(new Dimension(300, 30));
			
			name = new JTextField(20);
			name.setPreferredSize(new Dimension(70, 30));
			name.setMaximumSize(new Dimension(70, 30));
			name.setMinimumSize(new Dimension(70, 30));
			type = new JComboBox<GamePlayer>(playerTypes);
			type.addActionListener(this);
			gui = new JCheckBox();
			
			//ensure default option match gui checkbox restrictions
			type.setSelectedIndex(0);
			
			row.add(name);
			row.add(Box.createHorizontalGlue());
			row.add(type);
			row.add(Box.createHorizontalGlue());
			row.add(gui);
		}

		/**
		 * change availability of gui checkbox based on what player type
		 * is selected
		 */
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//if player doesn't support gui
			if(!((GamePlayer) type.getSelectedItem()).supportsGui()){
				gui.setSelected(false);
				gui.setEnabled(false);
			}//if player supports gui but don't require it
			else if(!((GamePlayer) type.getSelectedItem()).requiresGui()){
				gui.setEnabled(true);
			}//if player requires gui
			else{
				gui.setSelected(true);
				gui.setEnabled(false);
			}
		}
		
		public GamePlayer makePlayer(){
			GamePlayer player = (GamePlayer)type.getSelectedItem();
			if(player instanceof GoHumanPlayer){
				return new GoHumanPlayer(name.getText(), gui.isSelected());
			}else if(player instanceof GoComputerPlayer0){
				return new GoComputerPlayer0(name.getText(), gui.isSelected());
			}else{
				return new GoComputerPlayer1(name.getText(), gui.isSelected());
			}
		}
		
		public Box getRow(){
			return row;
		}
	}
}