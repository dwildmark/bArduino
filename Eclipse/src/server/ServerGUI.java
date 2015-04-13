package server;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.FutureTask;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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


public class ServerGUI extends JFrame {

	private static final long serialVersionUID = 2486865764551934155L;
	private JTextField tfFluid1, tfFluid2, tfFluid3, tfFluid4, tfPortClient,
	tfPortArduino;
	private JTextArea taLog;
	private JLabel lblFluid1, lblFluid2, lblFluid3, lblFluid4, lblPortClient,
	lblPortArduino;
	private JButton btnRestart, btnSave, btnQuit;
	private JPanel pnlNetwork, pnlNetworkLbls, pnlNetworkTfs, pnlFluids,
	pnlButtons, pnlStatus, pnlMain, pnlFluid1, pnlFluid2, pnlFluid3,
	pnlFluid4;
	private JTabbedPane tabbedPane;
	private JScrollPane logScrollPane;
	private Logger logger;
	private Server server;
	private FutureTask futureTask;

	public ServerGUI(Logger logger) {
		this.logger = logger;
		TextAreaHandler tah = new TextAreaHandler();
		this.logger.addHandler(tah);
		taLog = tah.getTextArea();

		try {
			UIManager.setLookAndFeel(
					UIManager.getSystemLookAndFeelClassName());
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

		// Button Panel
		btnSave = new JButton("Save");
		btnRestart = new JButton("Restart");
		btnQuit = new JButton("Quit");

		pnlButtons = new JPanel();
		pnlButtons.setLayout(new BoxLayout(pnlButtons, BoxLayout.X_AXIS));
		pnlButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnlButtons.add(Box.createHorizontalGlue());
		pnlButtons.add(btnSave);
		pnlButtons.add(btnRestart);
		pnlButtons.add(btnQuit);
		pnlButtons.add(Box.createHorizontalGlue());

		// Fluids Panel
		tfFluid1 = new JTextField(prop.getProperty("fluid1"));
		tfFluid2 = new JTextField(prop.getProperty("fluid2"));
		tfFluid3 = new JTextField(prop.getProperty("fluid3"));
		tfFluid4 = new JTextField(prop.getProperty("fluid4"));

		lblFluid1 = new JLabel("Fluid 1");
		lblFluid2 = new JLabel("Fluid 2");
		lblFluid3 = new JLabel("Fluid 3");
		lblFluid4 = new JLabel("Fluid 4");

		pnlFluid1 = new JPanel(new BorderLayout());
		pnlFluid1.add(lblFluid1, BorderLayout.WEST);
		pnlFluid1.add(tfFluid1, BorderLayout.CENTER);

		pnlFluid2 = new JPanel(new BorderLayout());
		pnlFluid2.add(lblFluid2, BorderLayout.WEST);
		pnlFluid2.add(tfFluid2, BorderLayout.CENTER);

		pnlFluid3 = new JPanel(new BorderLayout());
		pnlFluid3.add(lblFluid3, BorderLayout.WEST);
		pnlFluid3.add(tfFluid3, BorderLayout.CENTER);

		pnlFluid4 = new JPanel(new BorderLayout());
		pnlFluid4.add(lblFluid4, BorderLayout.WEST);
		pnlFluid4.add(tfFluid4, BorderLayout.CENTER);

		pnlFluids = new JPanel();
		pnlFluids.setLayout(new BoxLayout(pnlFluids, BoxLayout.PAGE_AXIS));
		pnlFluids.add(pnlFluid1);
		pnlFluids.add(pnlFluid2);
		pnlFluids.add(pnlFluid3);
		pnlFluids.add(pnlFluid4);
		pnlFluids.add(Box.createVerticalGlue());

		// Network Panel
		lblPortClient = new JLabel("Client Port");
		lblPortArduino = new JLabel("Arduino Port");

		pnlNetworkLbls = new JPanel(new GridLayout(2, 0));
		pnlNetworkLbls.add(lblPortClient);
		pnlNetworkLbls.add(lblPortArduino);

		tfPortClient = new JTextField(prop.getProperty("clientport"));
		tfPortArduino = new JTextField(prop.getProperty("arduinoport"));

		pnlNetworkTfs = new JPanel(new GridLayout(2, 0));
		pnlNetworkTfs.add(tfPortClient);
		pnlNetworkTfs.add(tfPortArduino);

		pnlNetwork = new JPanel(new BorderLayout());
		pnlNetwork.add(pnlNetworkLbls, BorderLayout.WEST);
		pnlNetwork.add(pnlNetworkTfs, BorderLayout.CENTER);

		// Status Panel
		logScrollPane = new JScrollPane(taLog);
		pnlStatus = new JPanel(new BorderLayout());
		pnlStatus.add(logScrollPane, BorderLayout.CENTER);

		// Tabbed Pane
		tabbedPane = new JTabbedPane();
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		tabbedPane.add("Status", logScrollPane);
		tabbedPane.add("Fluids", pnlFluids);
		tabbedPane.add("Network", pnlNetwork);
		

		// Main Panel
		pnlMain = new JPanel(new BorderLayout());
		pnlMain.add(pnlButtons, BorderLayout.NORTH);
		pnlMain.add(tabbedPane, BorderLayout.CENTER);

		add(pnlMain);
		setTitle("Barduino Server");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);	

		//actionlistners
		Listener btnlistner = new Listener();
		btnQuit.addActionListener(btnlistner);
		btnRestart.addActionListener(btnlistner);
		btnSave.addActionListener(btnlistner);

		startServer();
	}

	private void startServer(){
		server = new Server(this.logger);
		futureTask = new FutureTask<Void>(server, null);
		futureTask.run();
		
	}

	public class Listener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==btnRestart){
				server.close();
				futureTask.cancel(true);
				startServer();
				logger.info("Server is restarted");
			}else if (e.getSource()==btnQuit){
				System.exit(0);			
			}else if(e.getSource()==btnSave){
				JOptionPane.showConfirmDialog(btnSave, "Are you sure that you want to save?"
						+ " Old settings will be lost!");
				
				Properties prop = new Properties();
				prop = new Properties();
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
