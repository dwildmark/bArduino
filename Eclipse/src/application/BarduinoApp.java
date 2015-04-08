package application;

import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class BarduinoApp {
	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {			
			e.printStackTrace();
		} 
		GUI gui = new GUI("src/application/Barduino.png");
		gui.setIngredients(new String[]{"Vodka","Juice,Orange"});
		JFrame frame = new JFrame();
		String absolutePath = new File("src/application/bArduino_icon.png").getAbsolutePath();
		frame.setIconImage(new ImageIcon(absolutePath).getImage());
		frame.add(gui);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
