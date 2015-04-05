package client;

import javax.swing.JFrame;

public class ClientApp extends Thread {

	public static void main(String[] args) {
		new ClientApp().start();
	}
	
	@Override
	public void run() {
		// opens the window where the messages will be received and sent
		GUI frame = new GUI();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
