package server;

import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import protocol.ServerProtocolParser;

public class ArduinoHandler extends Thread {
	private ServerProtocolParser parser;
	private ServerSocket arduinoServerSocket;
	private PrintWriter mOut;
	private BufferedReader in;
	public static final int SERVERPORT = 666;
	private Socket arduino;
	private Timer timer;
	

	public ArduinoHandler() {
		this.parser = ServerProtocolParser.getInstance();
		
		
	}
	class ToDoTask extends TimerTask {

		@Override
		public void run() {
			try {
				if(parser.getState()== ServerProtocolParser.VACANT && mOut != null && in != null){
					mOut.println("Q");
					if(!in.readLine().equals("ACK")){
						System.out.println(in.readLine());
					} else {
						
					}
				} else {
					parser.setState(ServerProtocolParser.MISSING_ARDUINO);
				}
					
			} catch (Exception e){
				System.out.println("Lost connection to Arduino");
				this.cancel();
				mOut = null;
				in = null;
				parser.setState(ServerProtocolParser.MISSING_ARDUINO);				
			}			
		}	
	}

	@Override
	public void run() {
		String message;
		String answer;

		try {
			arduinoServerSocket = new ServerSocket(SERVERPORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		while (true) {
			try {				
				while (true) {

					arduino = arduinoServerSocket.accept();
					
					mOut = new PrintWriter(new BufferedWriter(
							new OutputStreamWriter(arduino.getOutputStream())),
							true);
					in = new BufferedReader(new InputStreamReader(
							arduino.getInputStream()));

					System.out.println("Server: Arduino connected at "
							+ arduino.getInetAddress());
					timer = new Timer();
					timer.scheduleAtFixedRate(new ToDoTask(), 0, 1000);
					parser.setState(ServerProtocolParser.VACANT);						
					
					while (mOut != null && in != null) {
						if (parser.isGrogAvailable()) {
							message = parser.dequeueGrog();
							if (message != null) {
								mOut.println(message);
								System.out.println("Server to Arduino: "
										+ message);
								answer = in.readLine();
								System.out.println("Arduino said: " + answer);
								if (!(answer.equals("ACK"))) {
									// TODO
								}
							}
						}
					}
				}

			} catch (Exception e) {
				parser.setState(ServerProtocolParser.MISSING_ARDUINO);
				mOut = null;
				in = null;
				e.printStackTrace();
			}
		}
	}
}



