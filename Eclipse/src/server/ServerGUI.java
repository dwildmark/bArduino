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
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

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
	private JTextField tfPortClient, tfPortArduino, userSearch;
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
	private TableRowSorter<UserTableModel> sorter;
	private Logger logger;
	private PropertiesWrapper prop = null;
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
		tfPortClient = new JTextField(prop.getClientPort() + "");
		tfPortArduino = new JTextField(prop.getArduinoPort() + "");

		// Fluids
		fluidsTable = new JTable();
		fluidsTable.getTableHeader().setReorderingAllowed(false);
		populateFluidsTable();
		btnAddFluid = new JButton("Add fluid");
		btnRemoveFluid = new JButton("Remove Fluid");

		// Settings panel
		pnlSettings = new JPanel(new MigLayout());
		// pnlSettings.add(new JLabel("Fluids"));
		pnlSettings.add(btnRemoveFluid);
		pnlSettings.add(btnAddFluid, "wrap");
		pnlSettings.add(new JScrollPane(fluidsTable), "wrap, span 2");

		pnlSettings.add(new JLabel("Network"), "wrap");
		pnlSettings.add(new JLabel("Client Port"), "wrap");
		pnlSettings.add(tfPortClient, "wrap, w 100!");
		pnlSettings.add(new JLabel("Arduino Port"), "wrap");
		pnlSettings.add(tfPortArduino, "wrap, w 100!");

		// Users Panel
		userSearch = new JTextField();
		userSearch.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				newFilter();
			}

			public void insertUpdate(DocumentEvent e) {
				newFilter();
			}

			public void removeUpdate(DocumentEvent e) {
				newFilter();
			}
		});
		userTable = new JTable();
		userTable.getTableHeader().setReorderingAllowed(false);
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
		pnlUsers.add(new JLabel("Search"), "wrap");
		pnlUsers.add(userSearch, "wrap, w 100!");
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
		btnRemoveFluid.addActionListener(fluidListener);

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
		UserTableModel tableModel = null;
		try {
			tableModel = buildTableModel(users);
			sorter = new TableRowSorter<UserTableModel>(tableModel);
		} catch (SQLException e) {
		}

		userTable.setModel(tableModel);
		userTable.setRowSorter(sorter);
		userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						int viewRow = userTable.getSelectedRow();
						if (viewRow > 0) {
							// Selection got filtered away.
							userTable.convertRowIndexToModel(viewRow);
						}
					}
				});
	}

	public UserTableModel buildTableModel(ResultSet rs) throws SQLException {

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

		return new UserTableModel(data, columnNames);

	}

	public void setGrogInTheMaking(Grog grog) {
		if (grog == null) {
			lblGrogInTheMaking.setText("Nothing much");
		} else {
			String str = "Making " + grog.getClientHandler().getUsername()
					+ "s grog!";
			lblGrogInTheMaking.setText(str);
		}
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

	/**
	 * Update the row filter regular expression from the expression in the text
	 * box.
	 */
	private void newFilter() {
		RowFilter<UserTableModel, Object> rf = null;
		// If current expression doesn't parse, don't update.
		try {
			rf = RowFilter.regexFilter(userSearch.getText(), 0);
		} catch (java.util.regex.PatternSyntaxException e) {
			return;
		}
		sorter.setRowFilter(rf);
	}

	private void populateFluidsTable() {
		List<Fluid> list = prop.getFluidList();
		FluidsTableModel tableModel = new FluidsTableModel();

		// Add Values to table
		if (list.size() > 0) {
			Fluid fluid;
			for (int i = 0; i < list.size(); i++) {
				fluid = list.get(i);
				Vector<String> rowValue = new Vector<String>();

				rowValue.add(fluid.getId() + "");
				rowValue.add(fluid.getName());
				rowValue.add(fluid.getCost() + "");

				tableModel.addRow(rowValue);
			}
		}
		fluidsTable.setModel(tableModel);
		fluidsTable.repaint();
	}

	private void saveProperties() throws Exception {
		List<Fluid> list = prop.getFluidList();
		FluidsTableModel model = (FluidsTableModel) fluidsTable.getModel();
		Fluid tempFluid;

		for (int i = 0; i < model.getRowCount(); i++) {
			tempFluid = list.get(i);
			tempFluid.setId(Integer.parseInt((String) model.getValueAt(i, 0)));
			tempFluid.setName((String) model.getValueAt(i, 1));
			tempFluid
					.setCost(Integer.parseInt((String) model.getValueAt(i, 2)));
		}

		prop.setClientPort(Integer.parseInt(tfPortClient.getText()));
		prop.setArduinoPort(Integer.parseInt(tfPortArduino.getText()));
		controller.saveServerConfig(prop);

	}

	private void addFluid(String name, int price) throws Exception {
		List<Fluid> list = prop.getFluidList();
		Collections.sort(list);

		Fluid newFluid = new Fluid();
		if (list.size() > 0)
			newFluid.setId(list.get(list.size() - 1).getId() + 1);
		else
			newFluid.setId(1);
		newFluid.setName(name);
		newFluid.setCost(price);

		prop.addFluid(newFluid);

		// Save properties and refresh fluids table
		controller.saveServerConfig(prop);
		populateFluidsTable();
	}

	private void removeFluid(String fluid) throws Exception {
		List<Fluid> list = prop.getFluidList();

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getName().equals(fluid)) {
				prop.removeFluid(list.get(i));
				break;
			}
		}
		controller.saveServerConfig(prop);
		populateFluidsTable();
	}

	private class FluidsListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				if (e.getSource() == btnAddFluid) {
					addFluid("new fluid", 0);

				} else if (e.getSource() == btnRemoveFluid) {
					removeFluid((String) fluidsTable.getValueAt(
							fluidsTable.getSelectedRow(), 1));

				}
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, e1.getMessage());
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
				if (JOptionPane.showConfirmDialog(btnSave,
						"Are you sure that you want to save?"
								+ " Old settings will be lost!") == JOptionPane.YES_OPTION) {
					try {
						saveProperties();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage());
						e1.printStackTrace();
					}
				}
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
				if (selectedRowIndex >= 0) {
					String selectedObject = (String) userTable.getValueAt(
							selectedRowIndex, 0);
					if (selectedObject != null
							&& JOptionPane.showConfirmDialog(ServerGUI.this,
									"Are you sure you want to delete user '"
											+ selectedObject + "'") == JOptionPane.YES_OPTION)
						UserTools.removeUser(selectedObject);
					printUsers();
				}

			} else if (e.getSource() == btnAddCredits) {
				int selectedRowIndex = userTable.getSelectedRow();
				if (selectedRowIndex >= 0) {
					String selectedObject = (String) userTable.getValueAt(
							selectedRowIndex, 0);

					if (selectedObject != null) {
						JFrame frame = new JFrame();
						EditUserPane pane = new EditUserPane(frame,
								selectedObject, ServerGUI.this);
						frame.add(pane);
						frame.pack();
						frame.setVisible(true);
						frame.setLocationRelativeTo(ServerGUI.this);
					}
				}
			}
		}
	}

	private class FluidsTableModel extends DefaultTableModel {

		private static final long serialVersionUID = 3536620580454020553L;

		public FluidsTableModel() {
			addColumn("Id");
			addColumn("Name");
			addColumn("Price/cl");
		}
	}

	public class UserTableModel extends DefaultTableModel {
		private static final long serialVersionUID = -7402718922329474450L;

		public UserTableModel(Vector<Vector<Object>> data,
				Vector<String> columnNames) {
			super(data, columnNames);
		}

		// table is not editable
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	}
}
