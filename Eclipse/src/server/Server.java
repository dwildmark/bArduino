package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


public class Server extends Thread {
	public static final int SERVERPORT = 4444;
	private boolean running = false;
	private ArrayList<ClientHandler> clientArray = new ArrayList<ClientHandler>();

	public static void main(String[] args) {
		Server server = new Server();
		server.run();

	}

	public void run() {
		super.run();

		ClientHandler handler;
		running = true;
			try{
				System.out.println("Server: Running");

				//create a server socket. A server socket waits for requests to come in over the network.
				ServerSocket serverSocket = new ServerSocket(SERVERPORT);
				while(running){
				//create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
				Socket client = serverSocket.accept();
				System.out.println("Server: Client connected");
				handler = new ClientHandler(client);
				handler.run();
				clientArray.add(handler);
				
				
				}
			} catch (Exception e) {
				System.out.println("Server: Error");
			} finally {

			}
		}
	}
