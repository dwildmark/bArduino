package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import protocol.ServerProtocolParser;
import protocol.UserTools;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Jonathan Böcker 20015-04-27
 *
 */
public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 8907081465715268386L;
	private JTextField tfServerAdress, tfDatabaseName, tfUserName;
	private JPasswordField pfPassword;
	private JButton logIn;
	private JCheckBox saveUser;
	private Properties prop;
	private ServerProtocolParser parser;
	private Controller controller;

	public LoginFrame(Controller controller) {
		this.controller = controller;
		try {
			prop = controller.loadServerConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parser = ServerProtocolParser.getInstance();
		
		setLayout(new MigLayout());

		add(new JLabel("Server Adress:"));
		tfServerAdress = new JTextField(prop.getProperty("server_adress"));
		add(tfServerAdress, "wrap, w 150!");

		add(new JLabel("Database Name:"));
		tfDatabaseName = new JTextField(prop.getProperty("database_name"));
		add(tfDatabaseName, "wrap, w 150!");

		add(new JLabel("Username:"));
		tfUserName = new JTextField(prop.getProperty("sql_username"));
		add(tfUserName, "wrap, w 150!");

		saveUser = new JCheckBox("Save Username");
		add(saveUser, "wrap");
		saveUser.setSelected(true);

		add(new JLabel("Password:"));
		pfPassword = new JPasswordField();
		add(pfPassword, "wrap, w 150!");
		pfPassword.addKeyListener(new KeyBoardListener());

		logIn = new JButton("Log In");
		logIn.addActionListener(new Listener());
		add(logIn, "span 2, center");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);

	}

	private void saveConfigs() {
		try {
			prop.setProperty("server_adress", tfServerAdress.getText());
			prop.setProperty("database_name", tfDatabaseName.getText());

			if (saveUser.isSelected()) {
				prop.setProperty("sql_username", tfUserName.getText());
			} else {
				prop.remove("sql_username");
			}
			controller.saveServerConfig(prop);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void login() {
		parser.setSQLCredentials(tfUserName.getText(),
				new String(pfPassword.getPassword()), tfServerAdress.getText(),
				tfDatabaseName.getText());
		if (UserTools.testConnection()) {
			saveConfigs();
			setVisible(false);
			controller.startServerGUI();
		}
	}

	private class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			login();
		}
	}

	private class KeyBoardListener implements KeyListener {

		@Override
		public void keyPressed(KeyEvent arg0) {}

		@Override
		public void keyReleased(KeyEvent e) {
			if (e.getKeyCode() == KeyEvent.VK_ENTER) {
				login();
			}
		}

		@Override
		public void keyTyped(KeyEvent e) {}

	}

}
