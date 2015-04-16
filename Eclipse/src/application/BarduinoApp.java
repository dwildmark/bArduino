package application;

import java.io.File;

import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.UIManager;

/**
 * 
 * @author Jonathan Böcker, Dennis Wildmark
 *
 */
public class BarduinoApp {
	public static void main(String[] args) throws IOException {
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {			
			e.printStackTrace();
		} 
		JFrame frame = new JFrame();
		GUI gui = new GUI("resources/Barduino.png", frame);
		gui.setIngredients(new String[]{"Vodka", "Contreau","Apelsinjuice", "Äppeljuice"});
		String absolutePath = new File("resources/bArduino_icon.png").getAbsolutePath();
		frame.setIconImage(new ImageIcon(absolutePath).getImage());
		frame.add(gui);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
