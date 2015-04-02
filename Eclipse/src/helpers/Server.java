package helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * This class creates a very simple server which lets a client connect to it. 
 * It prints a message if the client sends one at moment connected. 
 * @author David
 *
 */
public class Server {
	
	 public static void main(String[] args) {
		 final int SERVERPORT = 4444;
		 System.out.println("S: Connecting...");
		 try {
			ServerSocket serverSocket = new ServerSocket(SERVERPORT);
			 Socket client = serverSocket.accept();
	         System.out.println("S: Receiving...");
	         InputStreamReader ir = new InputStreamReader(client.getInputStream());
	         BufferedReader br = new BufferedReader(ir);
	         String message = br.readLine();
	         System.out.println(message);
	         
	         if(message != null) {
	        	 PrintStream ps = new PrintStream(client.getOutputStream());
	        	 ps.println("Message received!!!");
	         }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		while(true) {
			
		}
	 }

}
