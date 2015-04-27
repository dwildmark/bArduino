package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import protocol.UserTools;
import net.miginfocom.swing.MigLayout;

public class NewUserPane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7331639029674473338L;
	private JButton btnAdd;
	private JTextField tfUserName;
	private JPasswordField tfPass, tfPassConfirm;
	private JLabel lblUserName, lblPass, lblPassConfirm;

	public NewUserPane(JFrame frame, ServerGUI serverGUI) {
		setLayout(new MigLayout());

		tfUserName = new JTextField();
		tfPass = new JPasswordField();
		tfPassConfirm = new JPasswordField();
		lblUserName = new JLabel("User Name: ");
		lblPass = new JLabel("Password: ");
		lblPassConfirm = new JLabel("Confirm Password:");
		btnAdd = new JButton("Add User");
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (tfUserName.getText().length() != 0
						&& tfPass.getPassword().length != 0
						&& tfPassConfirm.getPassword().length != 0) {
					if(new String(tfPass.getPassword()).equals(":") || new String(tfPass.getPassword()).contains(" ")){
						JOptionPane.showMessageDialog(NewUserPane.this, "Password Can Not Contain Character ':' Or Blank Spaces");
						
					} else if(Arrays.equals(tfPass.getPassword(),tfPassConfirm.getPassword())){
						UserTools.addUser(tfUserName.getText(), tfPass.getPassword());
						serverGUI.printUsers();
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						
					} else {
						JOptionPane.showMessageDialog(NewUserPane.this, "The Passwords Are Not Equal!");
					}
					
				} else {
					JOptionPane.showMessageDialog(NewUserPane.this, "All Fields Must Be Used!");
				}

			}
		});

		add(lblUserName);
		add(tfUserName, "wrap, w 100!, grow");
		add(lblPass);
		add(tfPass, "wrap, grow");
		add(lblPassConfirm);
		add(tfPassConfirm, "wrap, grow");
		add(btnAdd, "span 2, center");

	}

}
