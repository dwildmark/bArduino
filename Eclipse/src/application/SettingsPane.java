package application;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import application.TCPClient.OnMessageReceived;

public class SettingsPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel ipPanel, portPanel, ingredientsPanel;
	private JLabel header, serverIp, port;
	private JButton apply;
	private JTextField serverInput, portInput;
	private ArrayList<JLabel> ingredientsLbls;
	private ArrayList<JTextField> ingredientsFields;
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
		
		//ingredientsPanel
		ingredientsLbls = new ArrayList<JLabel>();
		ingredientsFields = new ArrayList<JTextField>();
		for(int i = 0; i < gui.getIngredients().size(); i++) {
			String text = gui.getIngredients().get(i).getText();
			ingredientsLbls.add(new JLabel("Ingrediens " + (i + 1)));
			ingredientsFields.add(new JTextField(text));
		}
		ingredientsPanel = new JPanel(new GridLayout(2, ingredientsLbls.size()));
		for(int i = 0; i < ingredientsLbls.size(); i++) {
			ingredientsPanel.add(ingredientsLbls.get(i));
			ingredientsPanel.add(ingredientsFields.get(i));
		}
		add(ingredientsPanel);
		
		//log
		add(gui.hiddenLog);
		
		//apply-button
		apply = new JButton("OK");
		apply.addActionListener(new ButtonListener());
		add(apply);
		
	}
	
	private void setServerIngredients() {
		String ingredients = "SETINGREDIENTS ";
		for(int i = 0; i < ingredientsFields.size(); i++) {
			ingredients += ingredientsFields.get(i).getText();
			if(i < ingredientsFields.size() - 1) {
				ingredients += ",";
			}
		}
		tcpClient.sendMessage(ingredients);
	}
	
	private void reconnect() {
		String ipAddr = serverInput.getText();
		int portNr = Integer.parseInt(portInput.getText());
		gui.setTCPClient(new TCPClient(messageListener, portNr, ipAddr));
	}
	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			//reconnect();
			setServerIngredients();
			gui.updateIngredients();
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		}
		
	}
}
