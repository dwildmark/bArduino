package testers;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.*;

/**
 * 
 * @author Jonathan Böcker 2015-04-4
 *
 */
public class ScreenEmulator extends JFrame{

	private static final long serialVersionUID = 2747429611864023738L;
	private JLabel lblConnect, lblMessage;
	private JPanel pnlMain;
	
	private ArduinoConnection connection;
	
	public ScreenEmulator(){
		lblConnect = new JLabel("Server Connection");
		lblConnect.setForeground(Color.RED);
		lblConnect.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));
		lblMessage = new JLabel("BARDUINO");
		
		pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
		pnlMain.add(lblConnect);
		pnlMain.add(lblMessage);
		
		add(pnlMain);
		
		connection = new ArduinoConnection(new OnMessageReceived() {
			
			@Override
			public void messageReceived(String message) {
				System.out.println(message);
				lblMessage.setText(message);
				pack();
			}
		});
		connection.start();
	}
	
	public static void main(String[] args) {
		ScreenEmulator emulator = new ScreenEmulator();
		emulator.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		emulator.pack();
		emulator.setVisible(true);
	}
	
	public interface OnMessageReceived {
		public void messageReceived(String message);
	}
	
	private class ArduinoConnection extends Thread{
		private OnMessageReceived messageListener;
		private PrintWriter mOut;
		private BufferedReader in;
		
		public ArduinoConnection(OnMessageReceived messageListener) {
			this.messageListener = messageListener;
		}
		
		public void sendMessage(String message) {
			if (mOut != null && !mOut.checkError()) {
				mOut.println(message);
				mOut.flush();
			}
		}
		
		@Override
		public void run(){
			Socket client = null;
			while(true){
				try{
					client = new Socket("localhost", 8006);
					mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
					in = new BufferedReader(new InputStreamReader(client.getInputStream()));
					lblConnect.setForeground(Color.GREEN);
					while(true){
						messageListener.messageReceived(in.readLine());
					}
					
				} catch (Exception e) {
					
				} finally{
					try {
						lblConnect.setForeground(Color.RED);
						client.close();
					} catch (Exception e) {
						
					}
				}
			}
		}
	}

}
