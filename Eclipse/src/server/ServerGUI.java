package server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

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
	private JTextField tfPortClient, tfPortArduino;
	private JTextArea taLog;
	private JLabel lblArduinoConnected, lblGrogInTheMaking;
	private JButton btnRestart, btnSave, btnQuit, btnRefund, btnNewUser,
			btnDeleteUser, btnRefresh, btnCancelGrog, btnSuspendUser,
			btnDequeueGrog, btnAddCredits, btnAddFluid, btnRemoveFluid;
	private JPanel pnlSettings, pnlButtons, pnlStatus, pnlUsers, pnlMain,
			pnlBarduinoConnection, pnlBarduinoStatus;
	private JTabbedPane tabbedPane;
	private JScrollPane logScrollPane, userScrollPane, connectedUserScroll,
			grogQueueScroll;
	private JTable userTable, fluidsTable;
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

		// Fluids
		fluidsTable = new JTable(new FluidsTableModel());
		populateFluidsTable(fluidsTable.getModel());
		btnAddFluid = new JButton("Add fluid");
		btnRemoveFluid = new JButton("Remove Fluid");

		// Settings panel
		pnlSettings = new JPanel(new MigLayout());
		pnlSettings.add(new JLabel("Fluids"), "wrap");
		pnlSettings.add(new JScrollPane(fluidsTable), "spany 2");
		pnlSettings.add(btnRemoveFluid,"wrap");
		pnlSettings.add(btnAddFluid, "wrap");
		pnlSettings.add(new JLabel("Network"), "wrap");
		pnlSettings.add(new JLabel("Client Port"), "wrap");
		pnlSettings.add(tfPortClient, "wrap, w 100!");
		pnlSettings.add(new JLabel("Arduino Port"), "wrap");
		pnlSettings.add(tfPortArduino, "wrap, w 100!");

		// Users Panel
		userTable = new JTable();
		userScrollPane = new JScrollPane(userTable);
		btnRefund = new JButton("Refund", new ImageIcon(getClass().getResource(
				"/cash.png")));
		btnNewUser = new JButton("New User", new ImageIcon(getClass()
				.getResource("/add.png")));
		btnDeleteUser = new JButton("Delete User", new ImageIcon(getClass()
				.getResource("/delete.png")));
		btnAddCredits = new JButton("Add credits", new ImageIcon(getClass()
				.getResource("/addcredits.png")));

		pnlUsers = new JPanel(new MigLayout());
		pnlUsers.add(userScrollPane, "wrap, span 4, grow");
		pnlUsers.add(btnAddCredits);
		pnlUsers.add(btnRefund);
		pnlUsers.add(btnNewUser);
		pnlUsers.add(btnDeleteUser);

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
		pnlStatus.add(pnlBarduinoStatus, "wrap");
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
		tabbedPane.add("Users", pnlUsers);
		tabbedPane.add("Log", logScrollPane);
		tabbedPane.add("Settings", pnlSettings);

		// Logo Label
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
		FluidsListener fluidListener = new FluidsListener();

		btnAddFluid.addActionListener(fluidListener);

		btnQuit.addActionListener(btnlistner);
		btnRestart.addActionListener(btnlistner);
		btnSave.addActionListener(btnlistner);
		btnRefresh.addActionListener(btnlistner);

		btnNewUser.addActionListener(usersListener);
		btnDeleteUser.addActionListener(usersListener);
		btnAddCredits.addActionListener(usersListener);

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
		} catch (SQLException e) {
		}

		userTable.setModel(tableModel);
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

	private void populateFluidsTable(TableModel tableModel) throws IOException {
		List<String> list = controller.getFluidKeys();
		prop = controller.loadServerConfig();

		// Clear table
		int rows = ((DefaultTableModel) tableModel).getRowCount();
		for (int i = 0; i < rows; i++) {
			((DefaultTableModel) tableModel).removeRow(i);
		}

		// Add Values to table
		if (list.size() > 0) {
			String fluid;
			for (int i = 0; i < list.size(); i++) {
				fluid = list.get(i);
				Vector<String> rowValue = new Vector<String>();
				rowValue.add(prop.getProperty(fluid));
				String[] splitStr = fluid.split("_");
				rowValue.add(prop.getProperty(splitStr[0] + "_price"));

				((DefaultTableModel) tableModel).addRow(rowValue);
			}
		}
	}

	private void saveProperties() throws IOException {
		DefaultTableModel model = (DefaultTableModel) fluidsTable.getModel();
		List<String> list = controller.getFluidKeys();
		prop = controller.loadServerConfig();
		
		for (int i = 0; i < model.getRowCount(); i++) {
			prop.setProperty(list.get(i), (String) model.getValueAt(i, 0));
			prop.setProperty(list.get(i).split("_")[0] + "_price",
					(String) model.getValueAt(i, 1));
		}

		prop.setProperty("clientport", tfPortClient.getText());
		prop.setProperty("arduinoport", tfPortArduino.getText());
		controller.saveServerConfig(prop);
	}

	private void addFluid(String name, int price) throws IOException {
		List<String> list = controller.getFluidKeys();
		prop = controller.loadServerConfig();
		
		// Figure out property key for the new fluid
		String fluidOrder;
		if (list.size() > 0)
			fluidOrder = "fluid"
					+ (Integer.parseInt(list.get(list.size() - 1).split("_")[0]
							.substring("fluid".length())) + 1);
		else
			fluidOrder = "fluid1";

		// Save the fluid
		prop.setProperty(fluidOrder + "_name", name);
		prop.setProperty(fluidOrder + "_price", "" + price);

		// Save properties and refresh fluids table
		controller.saveServerConfig(prop);
		try {
			populateFluidsTable(fluidsTable.getModel());
		} catch (IOException e) {}
	}
	
	private void removeFluid(int selectedRow) throws IOException{
		DefaultTableModel model = (DefaultTableModel) fluidsTable.getModel();
		List<String> list = controller.getFluidKeys();
		prop = controller.loadServerConfig();
		
		for (int i = 0; i < list.size(); i++) {
			if(model.getValueAt(selectedRow, 0).equals(list.get(i))){
				prop.remove(list.get(i));
				prop.remove(list.get(i).split("_")[0] + "_price");
			}
		}
		controller.saveServerConfig(prop);
		try {
			populateFluidsTable(fluidsTable.getModel());
		} catch (IOException e) {}
	}

	private class FluidsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnAddFluid) {
				try {
					addFluid("new fluid", 0);
				} catch (IOException e1) {}
			} else if (e.getSource() == btnRemoveFluid){
				try {
					removeFluid(fluidsTable.getSelectedRow());
				} catch (IOException e1) {}
			}
		}
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
				try {
					saveProperties();
				} catch (IOException e1) {}
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
				int selectedRowIndex = userTable.getSelectedRow();
				int selectedColumnIndex = userTable.getSelectedColumn();
				String selectedObject = (String) userTable.getModel()
						.getValueAt(selectedRowIndex, selectedColumnIndex);
				if (selectedObject != null
						&& JOptionPane.showConfirmDialog(ServerGUI.this,
								"Are you sure you want to delete user '"
										+ selectedObject + "'") == JOptionPane.YES_OPTION)
					UserTools.removeUser(selectedObject);
				printUsers();

			} else if (e.getSource() == btnAddCredits) {
				int selectedRowIndex = userTable.getSelectedRow();
				int selectedColumnIndex = userTable.getSelectedColumn();
				String selectedObject = (String) userTable.getModel()
						.getValueAt(selectedRowIndex, selectedColumnIndex);
				if (selectedObject != null) {
					JFrame frame = new JFrame();
					EditUserPane pane = new EditUserPane(frame, selectedObject);
					frame.add(pane);
					frame.pack();
					frame.setVisible(true);
					frame.setLocationRelativeTo(ServerGUI.this);
				}
			}
		}
	}

	private class FluidsTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 3536620580454020553L;

		public FluidsTableModel() {
			addColumn("Name");
			addColumn("Price/cl");
		}
	}
}
