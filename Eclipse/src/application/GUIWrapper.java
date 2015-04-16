package application;

import java.awt.CardLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class GUIWrapper extends JPanel{
	private GUI gui;
	private LoginPane loginPane;
	private CardLayout cl;
	
	public GUIWrapper(String imagePath, JFrame frame) throws IOException {
		cl = new CardLayout();
		gui = new GUI(imagePath, frame, this);
		loginPane = new LoginPane(gui);
		setLayout(cl);
		add(gui, "gui");
		add(loginPane, "loginPane");		
		cl.show(this, "loginPane");
	}
	
	public void showLoginPane(boolean b) {
		if(b)
			cl.show(this, "loginPane");
		else
			cl.show(this, "gui");
	}
}
