package application;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

public class LoginPane extends JPanel{
	private JTextField userTF, passwordTF;
	private JButton loginBtn;
	private GUI gui;
	
	public LoginPane( GUI gui) {
		this.gui = gui;
		userTF = new JTextField();
		userTF.setPreferredSize(new Dimension(100, 20));
		passwordTF = new JTextField();
		passwordTF.setPreferredSize(new Dimension(100, 20));
		loginBtn = new JButton("Logga in");
		loginBtn.addActionListener(new ButtonListener());
		setPreferredSize(gui.getPreferredSize());
		setLayout(new MigLayout());
		add(new JLabel("User Name:"), "wrap, width 50:150:200, center");
		add(userTF, "wrap, width 50:150:200, center");
		add(new JLabel("Password:"), "wrap, width 50:150:200, center");
		add(passwordTF, "wrap, width 50:150:200, center");
		add(loginBtn, "wrap, center");
	}

	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == loginBtn) {
				gui.login(userTF.getText(), passwordTF.getText());
			}
		}
		
	}
}
