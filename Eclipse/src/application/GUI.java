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

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
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
	
	public GUI( String relPath) throws IOException {
		File absolutePath = new File(relPath);
		BufferedImage image = ImageIO.read(absolutePath);
		BufferedImage scaledImage = Scalr.resize(image, 320);
		lblLogo = new JLabel(new ImageIcon(scaledImage));
		lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNoConnection = new JLabel("No connection!");
		lblNoConnection.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNoConnection.setVisible(false);
		glassSizeLbl = new JLabel(glassSize + "");
		glassSizeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
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
		optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
		optionsPanel.add(glassSizeLbl);
		optionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
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
		//scrollSuggestionPanel
		drinkList = new JList<String>();
		scrollSuggestionPanel = new JScrollPane(drinkList);
		scrollSuggestionPanel.setBorder(BorderFactory.createTitledBorder("Suggestions"));
		//overallPanel
		overallPanel = new JPanel(new BorderLayout());
		overallPanel.add(scrollSuggestionPanel, BorderLayout.EAST);
		overallPanel.add(drinkPanel, BorderLayout.CENTER);
		
		//main panel
		setLayout( new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(lblLogo);
		add(lblNoConnection);
		add(overallPanel);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(orderBtn);
		
		addActionListeners();		
	}
	
	public void setIngredients(String[] ingredients){
		JLabel tempNameLabel, tempRatioLbl;
		JSlider tempSlider;
		JPanel tempPanel;
		SliderListener sliderListener = new SliderListener();
		
		sliderPanel.removeAll();
		ingredientLbls.clear();
		ratioLbls.clear();
		sliders.clear();
		
		for(int i = 0; i < ingredients.length; i++){
			tempNameLabel = new JLabel(ingredients[i]);
			tempNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			ingredientLbls.add(tempNameLabel);
			sliderPanel.add(tempNameLabel);
			
			tempSlider = new JSlider(0, 100);
			tempSlider.addChangeListener(sliderListener);
			sliders.add(tempSlider);
			
			tempRatioLbl = new JLabel("n/a");
			ratioLbls.add(tempRatioLbl);
			
			tempPanel = new JPanel(new BorderLayout());
			tempPanel.add(tempSlider, BorderLayout.CENTER);
			tempPanel.add(tempRatioLbl, BorderLayout.EAST);
			
			sliderPanel.add(tempPanel);			
			sliderPanel.add(Box.createRigidArea(new Dimension(0, 10)));
			
		}
	}
	
	private void addActionListeners(){
		IncOrDecListener incOrDecListener = new IncOrDecListener();
		arrowUp.addActionListener(incOrDecListener);
		arrowDown.addActionListener(incOrDecListener);
		
		orderBtn.addActionListener(new OrderButtonListener());
		
		Iterator<JSlider> iter = sliders.iterator();
	}
	
	private class IncOrDecListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == arrowUp) {
				glassSize++;
				glassSizeLbl.setText(glassSize + "");
				
			} else if(e.getSource() == arrowDown) {
				glassSize--;
				glassSizeLbl.setText(glassSize + "");
			}
		}		
	}
	
	private class OrderButtonListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(orderBtn, "Children are not allowed to order");
			
		}		
	}
	
	private class SliderListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent e) {
			int sumSliders = 0;
			Iterator<JLabel> iterLabel;
			Iterator<JSlider> iterSlider = sliders.iterator();
			JSlider tempSlider;
			JLabel tempLabel;
			
			while(iterSlider.hasNext()){
				tempSlider = iterSlider.next();
				sumSliders += tempSlider.getValue();
			}
			
			iterSlider = sliders.iterator();
			iterLabel = ratioLbls.iterator();
			int sliderValue;
			double percentageDouble;
			int percentage;
			
			while(iterSlider.hasNext()){
				tempLabel = iterLabel.next();
				sliderValue = iterSlider.next().getValue();
				percentageDouble = ((double)sliderValue / sumSliders) * 100;
				percentage = (int) percentageDouble;
				
				tempLabel.setText(percentage + "%");
			}
		}
		
	}
	
}
