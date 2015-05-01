package application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class LoginPane extends JPanel{
	private JTextField userTF;
	private JPasswordField passwordTF;
	private JButton loginBtn;
	private GUI gui;
	
	public LoginPane( GUI gui) {
		this.gui = gui;
		userTF = new JTextField();
		userTF.setPreferredSize(new Dimension(100, 20));
		passwordTF = new JPasswordField();
		passwordTF.setPreferredSize(new Dimension(100, 20));
		loginBtn = new JButton("Logga in");
		loginBtn.addActionListener(new ButtonListener());
		setPreferredSize(gui.getPreferredSize());
		setLayout(new MigLayout());
		add(new JLabel("User Name:"), "wrap, width 50:150:200, align center");
		add(userTF, "wrap, width 50:150:200, center");
		add(new JLabel("Password:"), "wrap, width 50:150:200, align center");
		add(passwordTF, "wrap, width 50:150:200, align center");
		add(loginBtn, "wrap, align center");
	}

	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == loginBtn) {
				gui.login(userTF.getText(), new String(passwordTF.getPassword()));
			}
		}
		
	}
}
