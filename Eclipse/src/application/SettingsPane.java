package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import application.TCPClient.OnMessageReceived;

public class SettingsPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel ipPanel, portPanel;
	private JLabel header, serverIp, port;
	private JButton apply;
	private JTextField serverInput, portInput;
	private GUI gui;
	private TCPClient tcpClient;
	private OnMessageReceived messageListener;
	private JFrame frame;
	
	public SettingsPane( GUI gui, TCPClient tcpClient, JFrame frame) {
		this.gui = gui;
		this.tcpClient = tcpClient;
		this.frame = frame;
		messageListener = tcpClient.getOMR();
		init();
	}
	
	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		//header
		header = new JLabel("Inställningar");
		add(header);
		
		//ipPanel
		ipPanel = new JPanel(new BorderLayout());
		serverInput = new JTextField(tcpClient.getIP());
		serverInput.setPreferredSize(new Dimension(200, 20));
		serverIp = new JLabel("Server-IP: ");
		ipPanel.add(serverInput, BorderLayout.EAST);
		ipPanel.add(serverIp, BorderLayout.CENTER);
		add(ipPanel);
		
		//portPanel
		portPanel = new JPanel(new BorderLayout());
		portInput = new JTextField("" + tcpClient.getServerPort());
		portInput.setPreferredSize(new Dimension(200, 20));
		port = new JLabel("Port: ");
		portPanel.add(portInput, BorderLayout.EAST);
		portPanel.add(port, BorderLayout.CENTER);
		add(portPanel);
		
		//log
		add(gui.hiddenLog);
		
		//apply-button
		apply = new JButton("OK");
		apply.addActionListener(new ButtonListener());
		add(apply);
		
	}
	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String ipAddr = serverInput.getText();
			int portNr = Integer.parseInt(portInput.getText());
			gui.setTCPClient(new TCPClient(messageListener, portNr, ipAddr));
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
		
	}
}
