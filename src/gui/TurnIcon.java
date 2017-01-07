package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;

class TurnIcon extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1073189194007240257L;
	
	public static final int WIDTH = 90;
	public static final int HEIGHT = (int)(WIDTH*0.8);
	private Image icon;
	private boolean show = false;

	public TurnIcon(Image icon){
		this.icon = icon;
		setPreferredSize(new Dimension(WIDTH, HEIGHT));
		setMinimumSize(new Dimension(WIDTH, HEIGHT));
		setMaximumSize(new Dimension(WIDTH, HEIGHT));
	}
	
	public void paint(Graphics g){
		if (show){
			g.drawImage(icon, 0, 0, WIDTH, HEIGHT, null);
		}
	}
	
	public void setShown(boolean shown){
		show = shown;
		repaint();
	}
}
