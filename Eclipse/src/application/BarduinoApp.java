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
		GUIWrapper guiWrap = new GUIWrapper("resources/Barduino.png", frame);
		//GUI gui = new GUI("resources/Barduino.png", frame);
		String absolutePath = new File("resources/bArduino_icon.png").getAbsolutePath();
		frame.setIconImage(new ImageIcon(absolutePath).getImage());
		frame.add(guiWrap);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
