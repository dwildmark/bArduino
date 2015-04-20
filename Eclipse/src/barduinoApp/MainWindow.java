package barduinoApp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.imgscalr.Scalr;


public class MainWindow extends JFrame {

	private static final long serialVersionUID = 4141275260612234446L;
	private JLabel glassSizeLbl, lblNoConnection;
	private JPanel sliderPanel, optionsPanel;
	private ArrayList<JSlider> sliders = new ArrayList<JSlider>();
	private ArrayList<JLabel> ingredientLbls = new ArrayList<JLabel>();
	private ArrayList<JLabel> ratioLbls = new ArrayList<JLabel>();
	private JButton orderBtn, arrowUp, arrowDown;
	private int glassSize = 25;
	private Controller controller;

	public MainWindow(Controller controller) {
		this.controller = controller;
		setLayout(new MigLayout());

		try {
			BufferedImage image;
			image = ImageIO.read(getClass().getResource(
					BarduinoApp.logoFilePath));
			BufferedImage scaledImage = Scalr.resize(image, 320);
			JLabel lblLogo = new JLabel(new ImageIcon(scaledImage));
			add(lblLogo, "wrap, center");

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		glassSizeLbl = new JLabel(glassSize + " cl");
		arrowUp = new JButton(new ImageIcon(getClass().getResource(
				"/up.png")));
		arrowDown = new JButton(new ImageIcon(getClass().getResource(
				"/down.png")));
		orderBtn = new JButton("Place Order");
		
		IncOrDecListener incOrDecListener = new IncOrDecListener();
		arrowUp.addActionListener(incOrDecListener);
		arrowDown.addActionListener(incOrDecListener);
		
		optionsPanel = new JPanel(new MigLayout());
		optionsPanel.add(arrowUp);
		optionsPanel.add(glassSizeLbl);
		optionsPanel.add(arrowDown);
		optionsPanel.setBorder(BorderFactory.createTitledBorder(null,
				"Glass Size"));
		
		sliderPanel = new JPanel(new MigLayout());
		JLabel initLabel = new JLabel("Nothing here yet");		
		sliderPanel.add(initLabel);
		sliderPanel.setBorder(BorderFactory
				.createTitledBorder("Choose your drink"));
		orderBtn.setEnabled(false);
		
		add(optionsPanel,"wrap, center");
		add(sliderPanel, "wrap, center");
		add(orderBtn, "center");

		setIconImage(new ImageIcon(getClass().getResource(
				BarduinoApp.iconFilePath)).getImage());
		setTitle("Barduino");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
		setLocationRelativeTo(null);
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

			sliderPanel.add(tempPanel, "wrap");
		}
		repaint();
		pack();
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
					if (tempSlider.getValue() < (decreaseSum / nbrOfSlidersToShare)) {
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
	
	private class SliderListener implements ChangeListener {

		public void stateChanged(ChangeEvent e) {
			updateValues(e.getSource());
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
	
	private class ButtonListener implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == orderBtn) {				
					String message = "GROG";
					for (JLabel amount : ratioLbls) {
						message += " " + amount.getText().split(" ")[0];
					
					controller.sendGrog(message);
				}
			} 
		}
	}
}
