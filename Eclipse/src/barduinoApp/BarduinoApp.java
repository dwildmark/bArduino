package barduinoApp;

import javax.swing.UIManager;

public class BarduinoApp {
	public final static String iconFilePath = "/bArduino_icon.png";
	public final static String logoFilePath = "/Barduino.png";

	public static void main(String[] args){
		try {
			UIManager.setLookAndFeel(
			        UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {			
			e.printStackTrace();
		} 
		
		new Controller();
	}

}
