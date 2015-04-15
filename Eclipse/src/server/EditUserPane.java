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

import protocol.UserTools;
import net.miginfocom.swing.MigLayout;

public class EditUserPane extends JPanel {

	private static final long serialVersionUID = -8698004788538931737L;
	private JButton btnSave;
	private JPasswordField tfOldPass, tfNewPass, tfPassConfirm;
	private JLabel lblOldPass, lblNewPass, lblPassConfirm;

	public EditUserPane(JFrame frame, String user) {
		setLayout(new MigLayout());

		tfOldPass = new JPasswordField();
		tfNewPass = new JPasswordField();
		tfPassConfirm = new JPasswordField();

		lblOldPass = new JLabel("Old Password:");
		lblNewPass = new JLabel("New Password: ");
		lblPassConfirm = new JLabel("Confirm Password:");

		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(new String(tfNewPass.getPassword()).equals(":") || new String(tfNewPass.getPassword()).contains(" ")){
					JOptionPane.showMessageDialog(EditUserPane.this, "Password Can Not Contain Character ':' Or Blank Spaces");
					
				} else if (UserTools.confirmUser(user, tfOldPass.getPassword())
						&& Arrays.equals(tfNewPass.getPassword(),
								tfPassConfirm.getPassword())) {
					UserTools.removeUser(user);
					UserTools.addUser(user, tfNewPass.getPassword());
					JOptionPane.showMessageDialog(EditUserPane.this, "Password Changed!");
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					
				} else {
					JOptionPane.showMessageDialog(EditUserPane.this, "One Or More Passwords Are Not Correct");
				}

			}

		});
		
		add(lblOldPass);
		add(tfOldPass, "wrap, w 100!, grow");
		add(lblNewPass);
		add(tfNewPass, "wrap, grow");
		add(lblPassConfirm);
		add(tfPassConfirm, "wrap, grow");
		add(btnSave, "span 2, center");
	}
}
