package server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.imgscalr.Scalr;

import application.UserTools;
import net.miginfocom.swing.*;

public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 2486865764551934155L;
	private JTextField tfFluid1, tfFluid2, tfFluid3, tfFluid4, tfPortClient,
			tfPortArduino;
	private JTextArea taLog;
	private JLabel lblFluid1, lblFluid2, lblFluid3, lblFluid4, lblPortClient,
			lblPortArduino;
	private JButton btnRestart, btnSave, btnQuit, btnEditUser, btnNewUser,
			btnDeleteUser, btnRefresh;
	private JPanel pnlNetwork, pnlFluids, pnlButtons, pnlStatus, pnlMain,
			pnlUsers;
	private JTabbedPane tabbedPane;
	private JScrollPane logScrollPane, userScrollPane;
	private JList<String> userList;
	private Logger logger;
	private Server server;
	private Properties prop = null;
	private Properties users = null;

	public ServerGUI(Logger logger) throws Exception {
		this.logger = logger;
		TextAreaHandler tah = new TextAreaHandler();
		this.logger.addHandler(tah);
		taLog = tah.getTextArea();

		prop = new Properties();
		// users = new Properties();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			loadServerConfig();
		} catch (Exception e) {
			e.printStackTrace();
		}

		MigLayout buttonLayout = new MigLayout();
		// Button Panel
		btnSave = new JButton("Save");
		btnRefresh = new JButton("Refresh");
		btnRestart = new JButton("Restart");
		btnQuit = new JButton("Quit");

		pnlButtons = new JPanel();
		pnlButtons.setLayout(buttonLayout);
		pnlButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnlButtons.add(btnSave);
		pnlButtons.add(btnRefresh);
		pnlButtons.add(btnRestart);
		pnlButtons.add(btnQuit);

		// Fluids Panel
		tfFluid1 = new JTextField(prop.getProperty("fluid1"));
		tfFluid2 = new JTextField(prop.getProperty("fluid2"));
		tfFluid3 = new JTextField(prop.getProperty("fluid3"));
		tfFluid4 = new JTextField(prop.getProperty("fluid4"));

		lblFluid1 = new JLabel("Fluid 1");
		lblFluid2 = new JLabel("Fluid 2");
		lblFluid3 = new JLabel("Fluid 3");
		lblFluid4 = new JLabel("Fluid 4");

		pnlFluids = new JPanel(new MigLayout());
		pnlFluids.add(lblFluid1);
		pnlFluids.add(tfFluid1, "grow, wrap, width 50:150:200");
		pnlFluids.add(lblFluid2);
		pnlFluids.add(tfFluid2, "grow, wrap");
		pnlFluids.add(lblFluid3);
		pnlFluids.add(tfFluid3, "grow, wrap");
		pnlFluids.add(lblFluid4);
		pnlFluids.add(tfFluid4, "grow, wrap");

		// Network Panel
		lblPortClient = new JLabel("Client Port");
		lblPortArduino = new JLabel("Arduino Port");
		tfPortClient = new JTextField(prop.getProperty("clientport"));
		tfPortArduino = new JTextField(prop.getProperty("arduinoport"));

		pnlNetwork = new JPanel(new MigLayout());
		pnlNetwork.add(lblPortClient);
		pnlNetwork.add(tfPortClient, "wrap, w 100!");
		pnlNetwork.add(lblPortArduino);
		pnlNetwork.add(tfPortArduino, "wrap, w 100!");

		// Status Panel
		logScrollPane = new JScrollPane(taLog);
		logScrollPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnlStatus = new JPanel();
		pnlStatus.add(logScrollPane);

		// Users Panel
		userList = new JList<String>(new DefaultListModel<String>());
		userScrollPane = new JScrollPane(userList);
		btnEditUser = new JButton("Change User Password");
		btnNewUser = new JButton("New User");
		btnDeleteUser = new JButton("Delete User");
		pnlUsers = new JPanel(new MigLayout());
		pnlUsers.add(userScrollPane, "wrap, span 3, grow, push");
		pnlUsers.add(btnEditUser);
		pnlUsers.add(btnNewUser);
		pnlUsers.add(btnDeleteUser);

		// Tabbed Pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tabbedPane.add("Status", logScrollPane);
		tabbedPane.add("Fluids", pnlFluids);
		tabbedPane.add("Network", pnlNetwork);
		tabbedPane.add("Users", pnlUsers);

		// Logo Label
		File absolutePath = new File("src/application/Barduino.png");
		BufferedImage image = ImageIO.read(absolutePath);
		BufferedImage scaledImage = Scalr.resize(image, 320);
		JLabel lblLogo = new JLabel(new ImageIcon(scaledImage));

		// Main Panel
		pnlMain = new JPanel(new MigLayout());
		pnlMain.add(lblLogo, "wrap, center");
		pnlMain.add(pnlButtons, "wrap, center");
		pnlMain.add(tabbedPane, "grow, span, push");
		pnlMain.setPreferredSize(new Dimension(500, 300));
		;

		String absoluteIconPath = new File("src/application/bArduino_icon.png")
				.getAbsolutePath();
		setIconImage(new ImageIcon(absoluteIconPath).getImage());
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
		// Scroller hänger med när händelse sker.
		logScrollPane.getVerticalScrollBar().addAdjustmentListener(
				new AdjustmentListener() {

					@Override
					public void adjustmentValueChanged(AdjustmentEvent e) {
						e.getAdjustable();
					}
				});

		loadUsers();
		printUsers();
		startServer();
	}

	public void printUsers() {
		Set<Object> keys = users.keySet();
		DefaultListModel<String> listModel = (DefaultListModel<String>) userList
				.getModel();
		listModel.clear();

		for (Object k : keys) {
			String key = (String) k;
			listModel.addElement(key);
		}
	}

	public void loadUsers() {
		File initialFile = new File(ServerApp.usersFileName);
		InputStream inputStream;
		users = new Properties();
		try {
			inputStream = new FileInputStream(initialFile);
			users.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			FileOutputStream out;
			try {
				out = new FileOutputStream(ServerApp.usersFileName);
				out.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}

		}
	}

	private void startServer() {
		server = new Server(this.logger);
		server.start();

	}

	private void loadServerConfig() throws IOException {
		File initialFile = new File(ServerApp.propFileName);
		InputStream inputStream;
		try {
			inputStream = new FileInputStream(initialFile);
			prop.load(inputStream);
			inputStream.close();
		} catch (IOException e) {
			File dir = new File("./resources");
			dir.mkdir();
			FileOutputStream out = new FileOutputStream(ServerApp.propFileName);
			prop.setProperty("fluid1", "Fluid 1");
			prop.setProperty("fluid2", "Fluid 2");
			prop.setProperty("fluid3", "Fluid 3");
			prop.setProperty("fluid4", "Fluid 4");
			prop.setProperty("clientport", "4444");
			prop.setProperty("arduinoport", "8008");
			prop.store(out, "Default values");
			out.close();
		}
	}

	public class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnRestart) {
				server.close();
				startServer();
				logger.info("Server is restarted");
			} else if (e.getSource() == btnRefresh) {
				try {
					loadServerConfig();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				updateFluids();
			} else if (e.getSource() == btnQuit) {
				System.exit(0);
			} else if (e.getSource() == btnSave) {

				JOptionPane.showConfirmDialog(btnSave,
						"Are you sure that you want to save?"
								+ " Old settings will be lost!");

				FileOutputStream out;

				try {
					out = new FileOutputStream(ServerApp.propFileName);
					prop.setProperty("fluid1", tfFluid1.getText());
					prop.setProperty("fluid2", tfFluid2.getText());
					prop.setProperty("fluid3", tfFluid3.getText());
					prop.setProperty("fluid4", tfFluid4.getText());
					prop.setProperty("clientport", tfPortClient.getText());
					prop.setProperty("arduinoport", tfPortArduino.getText());
					prop.store(out, null);
					out.close();

				} catch (Exception a) {
					a.printStackTrace();
				}

			}
		}
	}

	public class UsersListener implements ActionListener {

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

				if (userList.getSelectedValue() != null
						&& JOptionPane.showConfirmDialog(ServerGUI.this,
								"Are you sure you want to delete user '"
										+ userList.getSelectedValue() + "'") == JOptionPane.YES_OPTION)
					UserTools.removeUser(userList.getSelectedValue());
				loadUsers();
				printUsers();

			} else if (e.getSource() == btnEditUser) {
				if (userList.getSelectedValue() != null) {
					JFrame frame = new JFrame();
					EditUserPane pane = new EditUserPane(frame,
							userList.getSelectedValue());
					frame.add(pane);
					frame.pack();
					frame.setVisible(true);
					frame.setLocationRelativeTo(ServerGUI.this);
				}
			}

		}

	}

	public void updateFluids() {
		tfFluid1.setText(prop.getProperty("fluid1"));
		tfFluid2.setText(prop.getProperty("fluid2"));
		tfFluid3.setText(prop.getProperty("fluid3"));
		tfFluid4.setText(prop.getProperty("fluid4"));
	}
}
