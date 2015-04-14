package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import net.miginfocom.swing.*;

public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 2486865764551934155L;
	private JTextField tfFluid1, tfFluid2, tfFluid3, tfFluid4, tfPortClient,
			tfPortArduino;
	private JTextArea taLog;
	private JLabel lblFluid1, lblFluid2, lblFluid3, lblFluid4, lblPortClient,
			lblPortArduino;
	private JButton btnRestart, btnSave, btnQuit;
	private JPanel pnlNetwork, pnlFluids,
			pnlButtons, pnlStatus, pnlMain;
	private JTabbedPane tabbedPane;
	private JScrollPane logScrollPane;
	private Logger logger;
	private Server server;

	public ServerGUI(Logger logger) {
		this.logger = logger;
		TextAreaHandler tah = new TextAreaHandler();
		this.logger.addHandler(tah);
		taLog = tah.getTextArea();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		Properties prop = null;
		try {
			prop = new Properties();
			InputStream inputStream = getClass().getClassLoader()
					.getResourceAsStream("config.properties");
			if (inputStream != null) {
				prop.load(inputStream);
				inputStream.close();
			}
		} catch (Exception e) {

		}

		MigLayout buttonLayout = new MigLayout();
		// Button Panel
		btnSave = new JButton("Save");
		btnRestart = new JButton("Restart");
		btnQuit = new JButton("Quit");

		pnlButtons = new JPanel();
		pnlButtons.setLayout(buttonLayout);
		pnlButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnlButtons.add(btnSave);
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
		logScrollPane.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		pnlStatus = new JPanel();
		pnlStatus.add(logScrollPane);

		// Tabbed Pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tabbedPane.add("Status", logScrollPane);
		tabbedPane.add("Fluids", pnlFluids);
		tabbedPane.add("Network", pnlNetwork);

		// Main Panel
		pnlMain = new JPanel(new MigLayout());
		pnlMain.add(pnlButtons, "wrap");
		pnlMain.add(tabbedPane, "grow, span, push");

		setLayout(new MigLayout());
		add(pnlMain, "grow, span, push");
		setTitle("Barduino Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);

		// actionlistners
		Listener btnlistner = new Listener();
		btnQuit.addActionListener(btnlistner);
		btnRestart.addActionListener(btnlistner);
		btnSave.addActionListener(btnlistner);

		startServer();
	}

	private void startServer() {
		server = new Server(this.logger);
		server.start();

	}

	public class Listener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnRestart) {
				server.close();
				startServer();
				logger.info("Server is restarted");
			} else if (e.getSource() == btnQuit) {
				System.exit(0);
			} else if (e.getSource() == btnSave) {
				
				JOptionPane.showConfirmDialog(btnSave,
						"Are you sure that you want to save?"
								+ " Old settings will be lost!");

				Properties prop = new Properties();
				String propFileName = "./resources/config.properties";

				FileOutputStream out;

				try {

					out = new FileOutputStream(propFileName);
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
}
