package client;

import javax.swing.JPanel;

public class SettingsPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private GUI gui;
	private TCPClient tcpClient;
	
	public SettingsPane( GUI gui, TCPClient tcpClient) {
		this.gui = gui;
		this.tcpClient = tcpClient;
	}
	
	private void init() {
		
	}
}
