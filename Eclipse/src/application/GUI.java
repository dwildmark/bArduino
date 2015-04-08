package application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import org.imgscalr.Scalr;

public class GUI extends JPanel {
	private JLabel lblLogo, glassSizeLbl1, glassSizeLbl2, lblNoConnection;
	private JPanel drinkPanel, sliderPanel, optionsPanel;
	private ArrayList<JSlider> sliders;
	private ArrayList<JLabel> sliderLbls;
	private JButton orderBtn, arrowUp, arrowDown;
	private int glassSize;
	public GUI( String relPath) throws IOException {
		File absolutePath = new File(relPath);
		BufferedImage image = ImageIO.read(absolutePath);
		BufferedImage scaledImage = Scalr.resize(image, 320);
		lblLogo = new JLabel(new ImageIcon(scaledImage));
		lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNoConnection = new JLabel("No connection!");
		lblNoConnection.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNoConnection.setVisible(false);
		glassSizeLbl1 = new JLabel("Drink Size");
		glassSizeLbl1.setAlignmentX(Component.CENTER_ALIGNMENT);
		glassSizeLbl2 = new JLabel("25");
		glassSizeLbl2.setAlignmentX(Component.CENTER_ALIGNMENT);
		arrowUp = new JButton("More");
		arrowUp.setAlignmentX(Component.CENTER_ALIGNMENT);
		arrowDown = new JButton("Less");
		arrowDown.setAlignmentX(Component.CENTER_ALIGNMENT);
		orderBtn = new JButton("Place Order");
		orderBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		//OptionsPanel
		optionsPanel = new JPanel();		
		optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));
		optionsPanel.add(glassSizeLbl1);
		optionsPanel.add(arrowUp);
		optionsPanel.add(glassSizeLbl2);
		optionsPanel.add(arrowDown);
		//sliderPanel
		sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		JLabel initLabel = new JLabel("Nothing here yet");
		initLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		sliderPanel.add(initLabel);
		//drinkPanel
		drinkPanel = new JPanel(new BorderLayout());
		drinkPanel.add(sliderPanel, BorderLayout.CENTER);
		drinkPanel.add(optionsPanel, BorderLayout.EAST);
		//main panel
		setLayout( new BoxLayout(this, BoxLayout.Y_AXIS));
		add(lblLogo);
		add(lblNoConnection);
		add(drinkPanel);
		add(orderBtn);
	}
	
}
