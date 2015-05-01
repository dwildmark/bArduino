package testers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import protocol.ServerProtocolParser;

/**
 * Test application for {@link ServerProtocolParser}.
 * 
 * The source code demonstrates how to use the parser
 * 
 * @author Jonathan B�cker
 * @version 0.1
 *  
 * 2015-04-02
 *
 */
public class ServerProtocolTester extends JPanel{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4425679692644920564L;
	private ServerProtocolParser parser;
	private JButton btnAvaReq, btnGrogReq;
	private JRadioButton rbVacant, rbBusy, rbNoCon;
	private JLabel lblServerState, lblClientMessage, lblArduinoMessage;
	private JPanel pnlRadioButtons;
	private JTextField tfGrog;
	private JTextArea taClient, taArduino;
	private Listener listener;
	
	
	public ServerProtocolTester(ServerProtocolParser parser){
		this.parser = parser;		
		
		lblServerState = new JLabel("Server state");
				
		rbVacant = new JRadioButton("Vacant");
		rbBusy = new JRadioButton("Busy");
		rbNoCon = new JRadioButton("No Arduino Connection");
		rbVacant.setSelected(true);
		
		ButtonGroup btnGroup = new ButtonGroup();
		btnGroup.add(rbVacant);
		btnGroup.add(rbBusy);
		btnGroup.add(rbNoCon);
		
		pnlRadioButtons = new JPanel();
		pnlRadioButtons.setLayout(new BoxLayout(pnlRadioButtons, BoxLayout.X_AXIS));
		pnlRadioButtons.add(rbVacant);
		pnlRadioButtons.add(rbBusy);
		pnlRadioButtons.add(rbNoCon);
		
		btnAvaReq = new JButton("Send AVAREQ");
		btnGrogReq = new JButton("Send GROG");
		tfGrog = new JTextField("Enter grog message here: Integers with spaces between");
		lblClientMessage = new JLabel("Client response:");
		taClient = new JTextArea("Message that will be sent back to client \nwill appear here");
		lblArduinoMessage = new JLabel("Messages for Arduino");
		taArduino = new JTextArea("Message that will be sent to Arduino \nwill appear here");
		
		listener = new Listener();
		rbVacant.addActionListener(listener);
		rbBusy.addActionListener(listener);
		rbNoCon.addActionListener(listener);
		btnAvaReq.addActionListener(listener);
		btnGrogReq.addActionListener(listener);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(lblServerState);
		add(pnlRadioButtons);
		add(btnAvaReq);
		add(btnGrogReq);
		add(tfGrog);
		add(lblClientMessage);
		add(taClient);
		add(lblArduinoMessage);
		add(taArduino);
		
	}
	
	private class Listener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == btnAvaReq){
				taClient.setText(parser.processClientMessage("AVAREQ"));
				
			} else if (e.getSource() == btnGrogReq) {
				taClient.setText(parser.processClientMessage("GROG " + tfGrog.getText()));
				
				if(parser.isGrogAvailable()){
					String str = "";
					
					while(parser.isGrogAvailable()){
						str += parser.dequeueGrog() + "\n";
					}
					
					taArduino.setText(str);
				}
				
			} else if (e.getSource() == rbVacant) {
				parser.setState(ServerProtocolParser.VACANT);
				
			} else if (e.getSource() == rbBusy) {
				parser.setState(ServerProtocolParser.BUSY);
				
			} else if (e.getSource() == rbNoCon) {
				parser.setState(ServerProtocolParser.MISSING_ARDUINO);
			}
			
		}
		
	}

	public static void main(String[] args) {
		ServerProtocolParser parser = ServerProtocolParser.getInstance();
		JFrame frame = new JFrame();
		
		frame.add(new ServerProtocolTester(parser));
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
}
