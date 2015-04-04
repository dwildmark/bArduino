package client;

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
	private boolean running = false;
	private OnMessageReceived messageListener;
	private int SERVERPORT;
	private String ipAdress;
	private Socket client;

	public TCPClient(OnMessageReceived messageListener, int SERVERPORT, String ipAdress) {
		this.messageListener = messageListener;
		this.SERVERPORT = SERVERPORT;
		this.ipAdress = ipAdress;		
	}

	public void sendMessage(String message) {
		if (mOut != null && !mOut.checkError()) {
			mOut.println(message);
			mOut.flush();
		}
	}
	
	public void stopClient(){
		mOut.println("STOP");
	}
	
	@Override
    public void run() {
        super.run();

        running = true;

        try {
        	System.out.println("S: Connecting...");
            //create a server socket. A server socket waits for requests to come in over the network.
            client = new Socket(ipAdress, SERVERPORT);            

            try {

                //sends the message to the client
                mOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);

                //read the message received from client
                in = new BufferedReader(new InputStreamReader(client.getInputStream()));

                System.out.println("S: Connected!");
                
                //in this while we wait to receive messages from client (it's an infinite loop)
                //this while it's like a listener for messages
                while (running) {
                    String message = in.readLine();
                    if (message != null && messageListener != null) {
                        //call the method messageReceived from ServerBoard class
                        messageListener.messageReceived(message);
                        if(message.equals("STOP"))
                        	running = false;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            	in.close();
            	mOut.close();
                client.close();
            }

        } catch (Exception e) {
            System.out.println("S: Error");
            e.printStackTrace();
        }

    }

	public interface OnMessageReceived {
		public void messageReceived(String message);
	}

}
