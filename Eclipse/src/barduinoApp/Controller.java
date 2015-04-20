package barduinoApp;

import java.io.IOException;

public class Controller {
	private LoginWindow loginWindow;
	private MainWindow mainWindow;
	private TCPClient client;

	public Controller() {
		loginWindow = new LoginWindow(this);
	}

	public void connect(String ip, int port) {
		if (client == null || client.isConnectionFailed()) {
			client = new TCPClient(this, port, ip);
			client.start();
			while (!client.isConnected()) {
				if (client.isConnectionFailed()) {
					loginWindow.setConnectFailed();
				}
			}
			logIn(loginWindow.getUserName(), loginWindow.getPassword());
		} else {
			logIn(loginWindow.getUserName(), loginWindow.getPassword());
		}
	}

	public void logIn(String userName, char[] password) {
		try {
			if (client.sendMessage(
					"LOGIN " + userName + ":" + new String(password)).equals(
					"LOGIN OK")) {

				loginWindow.setVisible(false);
				mainWindow = new MainWindow(this);
				mainWindow.setIngredients(new String[]{"bajs", "kiss", "fis", "mens"});

			} else {
				loginWindow.setLoginFailed();
			}
		} catch (IOException e) {
			e.printStackTrace();
			loginWindow.setLoginFailed();
		}
	}
	
	public void sendGrog(String grog){
		try {
			client.sendMessage(grog);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
