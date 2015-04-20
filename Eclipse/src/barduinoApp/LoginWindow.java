package barduinoApp;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class LoginWindow extends JFrame {

	private static final long serialVersionUID = -8057972418292829460L;
	private JTextField userTF, IPTF, portTF;
	private JPasswordField passwordTF;
	private JButton loginBtn;
	private JLabel status;
	private Controller controller;

	public LoginWindow(Controller controller) {
		this.controller = controller;
		setLayout(new MigLayout());

		status = new JLabel("Enter credentials");
		IPTF = new JTextField("localhost");
		portTF = new JTextField("4444");
		userTF = new JTextField();
		passwordTF = new JPasswordField();
		loginBtn = new JButton("Log in");
		loginBtn.addActionListener(new Listener());

		add(status, "wrap");
		add(new JLabel("Server IP"), "wrap");
		add(IPTF, "wrap, w 100!");
		add(new JLabel("Server Port"), "wrap");
		add(portTF, "wrap, w 100!");
		add(new JLabel("User Name:"), "wrap");
		add(userTF, "wrap, w 100!");
		add(new JLabel("Password:"), "wrap, w 100!");
		add(passwordTF, "wrap, w 100!");
		add(loginBtn);

		setPreferredSize(new Dimension(200,300));
		setIconImage(new ImageIcon(getClass().getResource(
				BarduinoApp.iconFilePath)).getImage());
		setTitle("Barduino");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
	}

	public void setConnectFailed() {
		status.setText("Server Not Available!");
		status.setForeground(Color.RED);
	}
	
	public void setLoginFailed() {
		status.setText("Wrong Username Or Password!");
		status.setForeground(Color.RED);
	}
	
	public String getUserName() {		
		return userTF.getText();
	}
	
	public char[] getPassword() {
		return passwordTF.getPassword();
	}	
	
	public class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			controller.connect(IPTF.getText(),
					Integer.parseInt(portTF.getText()));
		}
	}

	
}
