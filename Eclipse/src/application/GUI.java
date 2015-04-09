package application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.imgscalr.Scalr;

/**
 * 
 * @author Jonathan Böcker, Dennis Wildmark
 *
 */
public class GUI extends JPanel {
	private static final long serialVersionUID = 306520565940671737L;
	private JLabel lblLogo, glassSizeLbl, lblNoConnection;
	private JPanel drinkPanel, sliderPanel, optionsPanel, overallPanel;
	private JScrollPane scrollSuggestionPanel;
	private JList<String> drinkList;
	private ArrayList<JSlider> sliders = new ArrayList<JSlider>();
	private ArrayList<JLabel> ingredientLbls = new ArrayList<JLabel>();
	private ArrayList<JLabel> ratioLbls = new ArrayList<JLabel>();
	private JButton orderBtn, arrowUp, arrowDown;
	private int glassSize = 25;
	private TCPClient tcpClient;
	private JTextArea hiddenLog;
	private Timer timer;

	public GUI(String relPath) throws IOException {
		File absolutePath = new File(relPath);
		BufferedImage image = ImageIO.read(absolutePath);
		BufferedImage scaledImage = Scalr.resize(image, 320);
		lblLogo = new JLabel(new ImageIcon(scaledImage));
		lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNoConnection = new JLabel("No connection!");
		lblNoConnection.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNoConnection.setVisible(false);
		glassSizeLbl = new JLabel(glassSize + " cl");
		glassSizeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		arrowUp = new JButton("More");
		arrowUp.setAlignmentX(Component.CENTER_ALIGNMENT);
		arrowDown = new JButton("Less");
		arrowDown.setAlignmentX(Component.CENTER_ALIGNMENT);
		orderBtn = new JButton("Place Order");
		orderBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		// OptionsPanel
		optionsPanel = new JPanel();
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.add(Box.createVerticalGlue());
		optionsPanel.add(arrowUp);
		optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		optionsPanel.add(glassSizeLbl);
		optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		optionsPanel.add(arrowDown);
		optionsPanel.add(Box.createVerticalGlue());
		optionsPanel.setBorder(BorderFactory.createTitledBorder(null, "Size"));
		// sliderPanel
		sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		JLabel initLabel = new JLabel("Nothing here yet");
		initLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		sliderPanel.add(initLabel);
		sliderPanel.setBorder(BorderFactory
				.createTitledBorder("Choose your drink"));
		// drinkPanel
		drinkPanel = new JPanel(new BorderLayout());
		drinkPanel.add(sliderPanel, BorderLayout.CENTER);
		drinkPanel.add(optionsPanel, BorderLayout.EAST);
		// scrollSuggestionPanel
		drinkList = new JList<String>();
		scrollSuggestionPanel = new JScrollPane(drinkList);
		scrollSuggestionPanel.setBorder(BorderFactory
				.createTitledBorder("Suggestions"));
		// overallPanel
		overallPanel = new JPanel(new BorderLayout());
		overallPanel.add(scrollSuggestionPanel, BorderLayout.EAST);
		overallPanel.add(drinkPanel, BorderLayout.CENTER);

		// main panel
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(lblLogo);
		add(lblNoConnection);
		add(overallPanel);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(orderBtn);

		hiddenLog = new JTextArea();
		addActionListeners();
		tcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
			@Override
			// this method declared in the interface from TCPServer
			// class is implemented here
			// this method is actually a callback method, because it
			// will run every time when it will be called from
			// TCPServer class (at while)
			public void messageReceived(String message) {
				if(message.split(" ")[0].equals("ERROR")) {
					orderBtn.setEnabled(false);
					if(message.split(" ")[1].equals("NOCONNECTION")) {
						orderBtn.setText("Barduino not connected!");
					} else {
						orderBtn.setText("Barduino busy!");
					}
				} else {
					orderBtn.setText("Place Order");
					orderBtn.setEnabled(true);
				}
				hiddenLog.append("/n" + message);
			}
		}, 4444, "192.168.1.53");
		tcpClient.start();
		timer = new Timer();
		timer.scheduleAtFixedRate(new ToDoTask(), 0, 1000);
	}

	public void setIngredients(String[] ingredients) {
		JLabel tempNameLabel, tempRatioLbl;
		JSlider tempSlider;
		JPanel tempPanel;
		SliderListener sliderListener = new SliderListener();

		sliderPanel.removeAll();
		ingredientLbls.clear();
		ratioLbls.clear();
		sliders.clear();

		for (int i = 0; i < ingredients.length; i++) {
			tempNameLabel = new JLabel(ingredients[i]);
			tempNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			ingredientLbls.add(tempNameLabel);
			sliderPanel.add(tempNameLabel);

			tempSlider = new JSlider(0, glassSize);
			tempSlider.setValue(0);
			tempSlider.addChangeListener(sliderListener);
			sliders.add(tempSlider);

			tempRatioLbl = new JLabel("0 cl");
			ratioLbls.add(tempRatioLbl);

			tempPanel = new JPanel(new BorderLayout());
			tempPanel.add(tempSlider, BorderLayout.CENTER);
			tempPanel.add(tempRatioLbl, BorderLayout.EAST);

			sliderPanel.add(tempPanel);
			sliderPanel.add(Box.createRigidArea(new Dimension(0, 10)));

		}
	}

	private void addActionListeners() {
		IncOrDecListener incOrDecListener = new IncOrDecListener();
		arrowUp.addActionListener(incOrDecListener);
		arrowDown.addActionListener(incOrDecListener);

		orderBtn.addActionListener(new OrderButtonListener());
	}

	private void updateValues(Object object) {
		int sumSliders = 0;
		Iterator<JLabel> iterLabel;
		Iterator<JSlider> iterSlider = sliders.iterator();
		JSlider tempSlider;
		JLabel tempLabel;

		while (iterSlider.hasNext()) {
			tempSlider = iterSlider.next();
			tempSlider.setMaximum(glassSize);
			sumSliders += tempSlider.getValue();
		}

		iterSlider = sliders.iterator();
		iterLabel = ratioLbls.iterator();
		int sliderValue;
		double percentageDouble;
		int percentage;

		if (sumSliders > glassSize) {
			int decreaseSum = sumSliders - glassSize;
			int nbrOfSlidersToShare = 0;

			while (iterSlider.hasNext()) {
				tempSlider = iterSlider.next();
				if (object != tempSlider) {
					if (tempSlider.getValue() != 0) {
						nbrOfSlidersToShare++;
					}
				}
			}

			iterSlider = sliders.iterator();

			while (iterSlider.hasNext()) {
				tempSlider = iterSlider.next();
				if (object != tempSlider) {
					if (tempSlider.getValue() < (decreaseSum
							/ nbrOfSlidersToShare)) {
						decreaseSum -= tempSlider.getValue();
						tempSlider.setValue(0);
					} else {
						tempSlider.setValue(tempSlider.getValue()
								- (decreaseSum / nbrOfSlidersToShare));
					}
				}
			}

		}

		iterSlider = sliders.iterator();

		while (iterSlider.hasNext()) {
			tempLabel = iterLabel.next();
			sliderValue = iterSlider.next().getValue();
			percentageDouble = ((double) sliderValue / sumSliders) * glassSize;
			percentage = (int) percentageDouble;
			if (percentageDouble - percentage > 0.5)
				percentage++;

			tempLabel.setText(percentage + " cl");
		}
	}

	private class IncOrDecListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == arrowUp) {
				glassSize++;
				
			} else if (e.getSource() == arrowDown) {
				if (glassSize > 0) {
					glassSize--;
				}
			}
			glassSizeLbl.setText(glassSize + " cl");
			updateValues(null);
		}
	}

	private class OrderButtonListener implements ActionListener {
		
		public void actionPerformed(ActionEvent e) {
			if(tcpClient != null) {
				String message = "GROG";
				for(JLabel amount : ratioLbls) {
					message += " " + amount.getText().split(" ")[0];
				}
				tcpClient.sendMessage(message);
				hiddenLog.append("\n" + message);
			}
		}
	}

	private class SliderListener implements ChangeListener {
		
		public void stateChanged(ChangeEvent e) {
			updateValues(e.getSource());
		}

	}
	
	private class ToDoTask extends TimerTask {

		public void run() {
			try {
				tcpClient.sendMessage("AVAREQ");
				lblNoConnection.setVisible(false);
			} catch( Exception e) {
				lblNoConnection.setVisible(true);
			}
		}
		
	}

}