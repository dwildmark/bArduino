package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 8907081465715268386L;
	JTextField tfServerAdress, tfDatabaseName, tfUserName;
	JPasswordField pfPassword;
	JButton logIn;
	JCheckBox saveUser;
	Properties prop = new Properties();
	ServerProtocolParser parser = ServerProtocolParser.getInstance();
	ServerApp serverApp;

	public LoginFrame(ServerApp serverApp) {
		this.serverApp = serverApp;
		loadConfigs();

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

		add(new JLabel("Password:"));
		pfPassword = new JPasswordField();
		add(pfPassword, "wrap, w 150!");

		logIn = new JButton("Log In");
		logIn.addActionListener(new Listener());
		add(logIn, "span 2, center");

		pack();
		setVisible(true);
		setLocationRelativeTo(null);
	}

	private void loadConfigs() {
		File initialFile = new File(ServerApp.propFileName);
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(initialFile);
			prop.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			try {
				File dir = new File("./resources");
				dir.mkdir();
				FileOutputStream out = new FileOutputStream(
						ServerApp.propFileName);
				prop.setProperty("fluid1", "Fluid 1");
				prop.setProperty("fluid2", "Fluid 2");
				prop.setProperty("fluid3", "Fluid 3");
				prop.setProperty("fluid4", "Fluid 4");
				prop.setProperty("clientport", "4444");
				prop.setProperty("arduinoport", "8008");
				prop.store(out, "Default values");
				out.close();
			} catch (Exception e2) {

			}
		}
	}

	private void saveConfigs() {
		try {
			FileOutputStream out = new FileOutputStream(ServerApp.propFileName);
			prop.setProperty("server_adress", tfServerAdress.getText());
			prop.setProperty("database_name", tfDatabaseName.getText());

			if (saveUser.isSelected()) {
				prop.setProperty("sql_username", tfUserName.getText());
			} else {
				prop.remove("sql_username");
			}

			prop.store(out, null);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			parser.setSQLCredentials(tfUserName.getText(), new String(
					pfPassword.getPassword()), tfServerAdress.getText(),
					tfDatabaseName.getText());
			if(UserTools.testConnection()){
				saveConfigs();
				setVisible(false);
				serverApp.startServer();
			}
		}

	}

}
