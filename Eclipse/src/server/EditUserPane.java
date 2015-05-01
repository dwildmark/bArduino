package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import protocol.UserTools;
import net.miginfocom.swing.MigLayout;

/**
 * 
 * @author Jonathan Böcker
 *
 */

public class EditUserPane extends JPanel {

	private static final long serialVersionUID = -8698004788538931737L;
	private JButton btnAdd;
	private JTextField tfSum;
	private String user;
	private JFrame frame;
	private ServerGUI serverGUI;

	public EditUserPane(JFrame frame, String user, ServerGUI serverGUI) {
		this.user = user;
		this.frame = frame;
		this.serverGUI = serverGUI;

		setLayout(new MigLayout());
		btnAdd = new JButton("Add credits");
		tfSum = new JTextField("");
		add(tfSum, "w 100!");
		add(btnAdd);

		btnAdd.addActionListener(new ButtonListener());
	}

	private class ButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String sum = tfSum.getText();
			try {
				UserTools.alterCredits(user, Double.parseDouble(sum));
				serverGUI.printUsers();
				frame.dispatchEvent(new WindowEvent(frame,
						WindowEvent.WINDOW_CLOSING));
			} catch (Exception a) {

			}
		}

	}
}
