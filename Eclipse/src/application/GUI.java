package application;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;

import org.imgscalr.Scalr;

public class GUI extends JPanel {
	private JLabel lblLogo, glassSizeLbl1, glassSizeLbl2, lblNoConnection;
	private JPanel drinkPanel, sliderPanel, optionsPanel;
	private ArrayList<JSlider> sliders = new ArrayList<JSlider>();
	private ArrayList<JLabel> sliderLbls = new ArrayList<JLabel>();
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
		optionsPanel.add(Box.createVerticalGlue());
		optionsPanel.add(arrowUp);
		optionsPanel.add(glassSizeLbl2);
		optionsPanel.add(arrowDown);
		optionsPanel.add(Box.createVerticalGlue());
		optionsPanel.setBorder(BorderFactory.createTitledBorder(null, "Size [ CL ]"));
		//sliderPanel
		sliderPanel = new JPanel();
		sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.Y_AXIS));
		JLabel initLabel = new JLabel("Nothing here yet");
		initLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		sliderPanel.add(initLabel);
		sliderPanel.setBorder(BorderFactory.createTitledBorder("Choose your drink"));
		//drinkPanel
		drinkPanel = new JPanel(new BorderLayout());
		drinkPanel.add(sliderPanel, BorderLayout.CENTER);
		drinkPanel.add(optionsPanel, BorderLayout.EAST);
		//main panel
		setLayout( new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(lblLogo);
		add(lblNoConnection);
		add(drinkPanel);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(orderBtn);
	}
	
	public void setIngredients(String[] ingredients){
		JLabel tempLabel;
		JSlider tempSlider;
		
		sliderPanel.removeAll();
		
		for(int i = 0; i < ingredients.length; i++){
			tempLabel = new JLabel(ingredients[i]);
			tempLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			sliderLbls.add(tempLabel);
			sliderPanel.add(tempLabel);
			
			tempSlider = new JSlider(0, Integer.parseInt(glassSizeLbl2.getText()));
			tempSlider.setAlignmentX(Component.CENTER_ALIGNMENT);
			sliders.add(tempSlider);
			sliderPanel.add(tempSlider);
			
			sliderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			
		}
	}
	
}
