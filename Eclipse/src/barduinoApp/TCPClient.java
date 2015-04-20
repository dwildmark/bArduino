package barduinoApp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPClient extends Thread {
	private PrintWriter mOut;
	private BufferedReader in;
	private int SERVERPORT;
	private String ipAdress;
	private Socket client;
	private Controller controller;
	private boolean connected = false;
	private boolean connectionFailed = false;

	public TCPClient(Controller controller, int SERVERPORT, String ipAdress) {
		this.SERVERPORT = SERVERPORT;
		this.ipAdress = ipAdress;
		this.controller = controller;
	}
	
	public boolean isConnected(){
		return connected;
	}
	
	public boolean isConnectionFailed(){
		return connectionFailed;
	}
	
	public int getServerPort() {
		return SERVERPORT;
	}
	
	public String getIP() {
		return ipAdress;
	}

	public String sendMessage(String message) throws IOException {
		if (mOut != null && !mOut.checkError()) {
			mOut.println(message);
			mOut.flush();
			return in.readLine();
		} else {
			return null;
		}
	}
	
	public void stopClient(){
		mOut.println("STOP");
	}
	
	@Override
    public void run() {
        super.run();
        
        try {
        	System.out.println("S: Connecting...");
            //create a server socket. A server socket waits for requests to come in over the network.
            client = new Socket(ipAdress, SERVERPORT);            

            try {

                //sends the message to the client
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

                //read the message received from client
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                
                connected = true;
                System.out.println("S: Connected!");
                
            } catch (Exception e) {
            	connectionFailed = true;
            	connected = false;
                e.printStackTrace();
            }

        } catch (Exception e) {
        	connectionFailed = true;
        	connected = false;
            e.printStackTrace();
        }
    }
}
