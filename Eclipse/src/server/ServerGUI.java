package server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.imgscalr.Scalr;

import protocol.UserTools;
import net.miginfocom.swing.*;

/**
 * A graphical user interface for the Barduino App.
 * 
 * @author Jonathan Böcker 20015-04-27
 *
 */
public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 2486865764551934155L;
	private JTextField tfFluid1, tfFluid2, tfFluid3, tfFluid4, tfPortClient,
			tfPortArduino;
	private JTextArea taLog;
	private JLabel lblArduinoConnected, lblGrogInTheMaking;
	private JButton btnRestart, btnSave, btnQuit, btnEditUser, btnNewUser,
			btnDeleteUser, btnRefresh, btnCancelGrog, btnSuspendUser,
			btnDequeueGrog;
	private JPanel pnlSettings, pnlButtons, pnlStatus, pnlMain, pnlBarduinoConnection, pnlBarduinoStatus;
	private JTabbedPane tabbedPane;
	private JScrollPane logScrollPane, userScrollPane, connectedUserScroll,
			grogQueueScroll;
	private JTable userList;
	private JList<String> connectedUserList, grogQueueList;
	private ImageIcon iconConnected, iconDisconnected;
	private Logger logger;
	private Properties prop = null;
	private Controller controller;

	/**
	 * Creates a graphical user interface for the Barduino App.
	 * 
	 * @param logger
	 *            Where logs will be written
	 * @param controller
	 * @throws Exception
	 */
	public ServerGUI(Logger logger, Controller controller) throws Exception {
		this.logger = logger;
		this.controller = controller;
		TextAreaHandler tah = new TextAreaHandler();
		this.logger.addHandler(tah);
		taLog = tah.getTextArea();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			prop = controller.loadServerConfig();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}

		MigLayout buttonLayout = new MigLayout();
		// Button Panel
		btnSave = new JButton("Save", new ImageIcon(getClass().getResource(
				"/save.png")));
		btnRefresh = new JButton("Refresh", new ImageIcon(getClass()
				.getResource("/refresh.png")));
		btnRestart = new JButton("Restart", new ImageIcon(getClass()
				.getResource("/cancel.png")));
		btnQuit = new JButton("Quit", new ImageIcon(getClass().getResource(
				"/close.png")));

		pnlButtons = new JPanel();
		pnlButtons.setLayout(buttonLayout);
		pnlButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnlButtons.add(btnSave);
		pnlButtons.add(btnRefresh);
		pnlButtons.add(btnRestart);
		pnlButtons.add(btnQuit);

		// Network
		tfPortClient = new JTextField(prop.getProperty("clientport"));
		tfPortArduino = new JTextField(prop.getProperty("arduinoport"));

		// Settings
		tfFluid1 = new JTextField(prop.getProperty("fluid1"));
		tfFluid2 = new JTextField(prop.getProperty("fluid2"));
		tfFluid3 = new JTextField(prop.getProperty("fluid3"));
		tfFluid4 = new JTextField(prop.getProperty("fluid4"));

		// Users Panel
		userList = new JTable();
		userScrollPane = new JScrollPane(userList);
		btnEditUser = new JButton("Change User Password", new ImageIcon(
				getClass().getResource("/edit.png")));
		btnNewUser = new JButton("New User", new ImageIcon(getClass()
				.getResource("/add.png")));
		btnDeleteUser = new JButton("Delete User", new ImageIcon(getClass()
				.getResource("/delete.png")));

		pnlSettings = new JPanel(new MigLayout());
		pnlSettings.add(new JLabel("Fluids"), "center, span 2");
		pnlSettings.add(new JLabel("Network"), "center, wrap, span 2");
		pnlSettings.add(new JLabel("Fluid 1"));
		pnlSettings.add(tfFluid1, "grow, width 50:150:200");
		pnlSettings.add(new JLabel("Client Port"));
		pnlSettings.add(tfPortClient, "wrap, w 100!");
		pnlSettings.add(new JLabel("Fluid 2"));
		pnlSettings.add(tfFluid2, "grow");
		pnlSettings.add(new JLabel("Arduino Port"));
		pnlSettings.add(tfPortArduino, "wrap, w 100!");
		pnlSettings.add(new JLabel("Fluid 3"));
		pnlSettings.add(tfFluid3, "grow, wrap");
		pnlSettings.add(new JLabel("Fluid 4"));
		pnlSettings.add(tfFluid4, "grow, wrap");
		pnlSettings.add(userScrollPane, "wrap, span 4, grow");
		pnlSettings.add(btnEditUser, "span 2");
		pnlSettings.add(btnNewUser);
		pnlSettings.add(btnDeleteUser);

		// Log Panel
		logScrollPane = new JScrollPane(taLog);
		logScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Status panel
		iconConnected = new ImageIcon(getClass().getResource("/check.png"));
		iconDisconnected = new ImageIcon(getClass().getResource(
				"/information.png"));
		lblArduinoConnected = new JLabel(iconDisconnected);
		lblGrogInTheMaking = new JLabel("Nothing much");
		grogQueueList = new JList<String>(new DefaultListModel<String>());
		connectedUserList = new JList<String>(new DefaultListModel<String>());
		grogQueueScroll = new JScrollPane(grogQueueList);
		connectedUserScroll = new JScrollPane(connectedUserList);
		btnDequeueGrog = new JButton("Dequeue Grog");
		btnCancelGrog = new JButton("Cancel Grog", new ImageIcon(getClass()
				.getResource("/close.png")));
		btnSuspendUser = new JButton("Suspend User");
		pnlBarduinoConnection = new JPanel(new MigLayout());
		pnlBarduinoConnection.setBorder(BorderFactory
				.createTitledBorder("Barduino Connection"));
		pnlBarduinoConnection.add(lblArduinoConnected);
		pnlBarduinoStatus = new JPanel(new MigLayout());
		pnlBarduinoStatus.setBorder(BorderFactory
				.createTitledBorder("Barduino Status"));
		pnlBarduinoStatus.add(lblGrogInTheMaking);
		
		
		pnlStatus = new JPanel(new MigLayout());
		pnlStatus.add(pnlBarduinoConnection, "w 120");
		pnlStatus.add(pnlBarduinoStatus,"wrap");
		pnlStatus.add(btnCancelGrog, "wrap, span, gapleft, push, al right");
		pnlStatus.add(new JLabel("Grog Queue"), "wrap");
		pnlStatus.add(grogQueueScroll, "span, wrap, grow");
		pnlStatus.add(btnDequeueGrog, "wrap");
		pnlStatus.add(new JLabel("Connected Clients"), "wrap");
		pnlStatus.add(connectedUserScroll, "span, wrap, grow");
		pnlStatus.add(btnSuspendUser);

		// Tabbed Pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tabbedPane.add("Status", pnlStatus);
		tabbedPane.add("Log", logScrollPane);
		tabbedPane.add("Settings", pnlSettings);

		// Logo Label
		// File absolutePath = new File("images/Barduino.png");
		BufferedImage image = ImageIO.read(getClass().getResource(
				"/Barduino.png"));
		BufferedImage scaledImage = Scalr.resize(image, 320);
		JLabel lblLogo = new JLabel(new ImageIcon(scaledImage));

		// Main Panel
		pnlMain = new JPanel(new MigLayout());
		pnlMain.add(lblLogo, "wrap, center");
		pnlMain.add(pnlButtons, "wrap, center");
		pnlMain.add(tabbedPane, "grow, span, push");
		pnlMain.setPreferredSize(new Dimension(485, 555));
		;

		// String absoluteIconPath = new File("images/bArduino_icon.png")
		// .getAbsolutePath();
		setIconImage(new ImageIcon(getClass().getResource("/bArduino_icon.png"))
				.getImage());
		setLayout(new MigLayout());
		add(pnlMain, "grow, span, push");
		setTitle("Barduino Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);

		// actionlistners
		Listener btnlistner = new Listener();
		UsersListener usersListener = new UsersListener();

		btnQuit.addActionListener(btnlistner);
		btnRestart.addActionListener(btnlistner);
		btnSave.addActionListener(btnlistner);
		btnRefresh.addActionListener(btnlistner);

		btnNewUser.addActionListener(usersListener);
		btnDeleteUser.addActionListener(usersListener);
		btnEditUser.addActionListener(usersListener);

		// The log window moves with the log being printed for visibility
		logScrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						e.getAdjustable();
					}
				});

		printUsers();
	}

	/**
	 * Prints out users gathered from the database in the gui.
	 */
	public void printUsers() {
		ResultSet users = UserTools.getAllUsers();
		DefaultTableModel tableModel = null;
		try {
			tableModel = buildTableModel(users);
		} catch (SQLException e) {}
		
		userList.setModel(tableModel);
	}
	
	public static DefaultTableModel buildTableModel(ResultSet rs)
	        throws SQLException {

	    ResultSetMetaData metaData = rs.getMetaData();

	    // names of columns
	    Vector<String> columnNames = new Vector<String>();
	    int columnCount = metaData.getColumnCount();
	    for (int column = 1; column <= columnCount; column++) {
	        columnNames.add(metaData.getColumnName(column));
	    }

	    // data of the table
	    Vector<Vector<Object>> data = new Vector<Vector<Object>>();
	    while (rs.next()) {
	        Vector<Object> vector = new Vector<Object>();
	        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
	            vector.add(rs.getObject(columnIndex));
	        }
	        data.add(vector);
	    }

	    return new DefaultTableModel(data, columnNames);

	}

	public void setArduinoConnected(boolean b) {
		if (b)
			lblArduinoConnected.setIcon(iconConnected);
		else
			lblArduinoConnected.setIcon(iconDisconnected);
	}

	public void userLoggedIn(String username) {
		DefaultListModel<String> listModel = (DefaultListModel<String>) connectedUserList
				.getModel();
		listModel.addElement(username);
	}

	public void userLoggedOut(String username) {
		DefaultListModel<String> listModel = (DefaultListModel<String>) connectedUserList
				.getModel();
		listModel.removeElement(username);
	}
	
	private class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnRestart) {
				controller.restartServer();
				logger.info("Server is restarted");

			} else if (e.getSource() == btnRefresh) {
				try {
					prop = controller.loadServerConfig();
					printUsers();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage());
					e1.printStackTrace();
				}

			} else if (e.getSource() == btnQuit) {
				System.exit(0);

			} else if (e.getSource() == btnSave) {
				JOptionPane.showConfirmDialog(btnSave,
						"Are you sure that you want to save?"
								+ " Old settings will be lost!");

				prop.setProperty("fluid1", tfFluid1.getText());
				prop.setProperty("fluid2", tfFluid2.getText());
				prop.setProperty("fluid3", tfFluid3.getText());
				prop.setProperty("fluid4", tfFluid4.getText());
				prop.setProperty("clientport", tfPortClient.getText());
				prop.setProperty("arduinoport", tfPortArduino.getText());
				controller.saveServerConfig(prop);
			}
		}
	}

	private class UsersListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnNewUser) {
				JFrame frame = new JFrame();
				NewUserPane pane = new NewUserPane(frame, ServerGUI.this);
				frame.add(pane);
				frame.pack();
				frame.setVisible(true);
				frame.setLocationRelativeTo(ServerGUI.this);

			} else if (e.getSource() == btnDeleteUser) {
				int selectedRowIndex = userList.getSelectedRow();
				int selectedColumnIndex = userList.getSelectedColumn();
				String selectedObject = (String) userList.getModel().getValueAt(selectedRowIndex, selectedColumnIndex);
				if (selectedObject != null
						&& JOptionPane.showConfirmDialog(ServerGUI.this,
								"Are you sure you want to delete user '"
										+ selectedObject + "'") == JOptionPane.YES_OPTION)
					UserTools.removeUser(selectedObject);
				printUsers();

			} else if (e.getSource() == btnEditUser) {
				int selectedRowIndex = userList.getSelectedRow();
				int selectedColumnIndex = userList.getSelectedColumn();
				String selectedObject = (String) userList.getModel().getValueAt(selectedRowIndex, selectedColumnIndex);
				if (selectedObject != null) {
					JFrame frame = new JFrame();
					EditUserPane pane = new EditUserPane(frame,
							selectedObject);
					frame.add(pane);
					frame.pack();
					frame.setVisible(true);
					frame.setLocationRelativeTo(ServerGUI.this);
				}
			}
		}
	}
}
