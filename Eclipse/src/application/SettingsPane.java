package application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import application.TCPClient.OnMessageReceived;

public class SettingsPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private JPanel ipPanel, portPanel, ingrFieldsPanel, ipSettingsPanel,
			ingrPanel;
	private JLabel header, serverIp, port;
	private JButton closeBtn, applyIngr, applyIp;
	private JTextField serverInput, portInput;
	private ArrayList<JLabel> ingredientsLbls;
	private ArrayList<JTextField> ingredientsFields;
	private GUI gui;
	private TCPClient tcpClient;
	private OnMessageReceived messageListener;
	private JFrame frame;
	private int SERVERPORT;
	private String IPADDRESS;

	public SettingsPane(GUI gui, TCPClient tcpClient, JFrame frame) {
		this.gui = gui;
		this.tcpClient = tcpClient;
		this.frame = frame;
		messageListener = tcpClient.getOMR();
		IPADDRESS = tcpClient.getIP();
		SERVERPORT = tcpClient.getServerPort();
		init();
	}

	private void init() {
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		ButtonListener bln = new ButtonListener();

		// header
		header = new JLabel("Inställningar");
		header.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(header);

		// ipSettingsPanel
		ipSettingsPanel = new JPanel(new GridLayout(3, 1));
		ipSettingsPanel.setBorder(BorderFactory.createTitledBorder(null,
				"Server"));

		add(ipSettingsPanel);

		// ipPanel
		ipPanel = new JPanel(new BorderLayout());
		serverInput = new JTextField(tcpClient.getIP());
		serverInput.setPreferredSize(new Dimension(200, 20));
		serverIp = new JLabel("Server-IP: ");
		ipPanel.add(serverInput, BorderLayout.EAST);
		ipPanel.add(serverIp, BorderLayout.CENTER);
		ipSettingsPanel.add(ipPanel);

		// portPanel
		portPanel = new JPanel(new BorderLayout());
		portInput = new JTextField("" + tcpClient.getServerPort());
		portInput.setPreferredSize(new Dimension(200, 20));
		port = new JLabel("Port: ");
		portPanel.add(portInput, BorderLayout.EAST);
		portPanel.add(port, BorderLayout.CENTER);
		ipSettingsPanel.add(portPanel);
		applyIp = new JButton("Verkställ");
		applyIp.setAlignmentX(Component.CENTER_ALIGNMENT);
		applyIp.addActionListener(bln);
		ipSettingsPanel.add(applyIp);
		add(ipSettingsPanel);

		// ingrPanel
		ingrPanel = new JPanel(new BorderLayout());
		ingrPanel.setBorder(BorderFactory.createTitledBorder(null,
				"Ingredienser"));

		// ingredientsPanel
		ingredientsLbls = new ArrayList<JLabel>();
		ingredientsFields = new ArrayList<JTextField>();
		for (int i = 0; i < gui.getIngredients().size(); i++) {
			String text = gui.getIngredients().get(i).getText();
			ingredientsLbls.add(new JLabel("Ingrediens " + (i + 1)));
			ingredientsFields.add(new JTextField(text));
		}
		ingrFieldsPanel = new JPanel(new GridLayout(ingredientsLbls.size(), 2));
		for (int i = 0; i < ingredientsLbls.size(); i++) {
			ingrFieldsPanel.add(ingredientsLbls.get(i));
			ingrFieldsPanel.add(ingredientsFields.get(i));
		}
		applyIngr = new JButton("Verkställ");
		applyIngr.setAlignmentX(Component.CENTER_ALIGNMENT);
		applyIngr.addActionListener(bln);
		ingrPanel.add(ingrFieldsPanel, BorderLayout.CENTER);
		ingrPanel.add(applyIngr, BorderLayout.SOUTH);
		add(ingrPanel);

		// log
		// add(gui.hiddenLog);

		// apply-button
		closeBtn = new JButton("Stäng");
		closeBtn.addActionListener(bln);
		closeBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(closeBtn);

	}

	private void setServerIngredients() {
		String ingredients = "SETINGREDIENTS:";
		for (int i = 0; i < ingredientsFields.size(); i++) {
			ingredients += ingredientsFields.get(i).getText();
			if (i < ingredientsFields.size() - 1) {
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
			if (e.getSource() == closeBtn) {
				frame.dispatchEvent(new WindowEvent(frame,
						WindowEvent.WINDOW_CLOSING));
			} else if (e.getSource() == applyIngr) {
				setServerIngredients();
				gui.updateIngredients();
			} else if (e.getSource() == applyIp) {
				reconnect();
			}

		}
	}
}
