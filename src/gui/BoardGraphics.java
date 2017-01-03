package gui;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import main.Log;

class BoardGraphics extends JPanel {
	
	static final int SIZE = 400;
	
	private Image texture;
	
	BoardGraphics(){
		try {
			texture = ImageIO.read(getClass().getResource("/goodwood.jpg"));
		} catch (IOException e) {
			Log.i("BoardGraphics", "goodwood.jpg not found");
		}
	}
	
	public void paint(Graphics g){
		if (texture != null){
			g.drawImage(texture, 0 ,0, SIZE, SIZE, null);
		}
	}

}
