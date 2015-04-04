package client;

import javax.swing.JFrame;

public class ClientApp {

	public static void main(String[] args) {

        //opens the window where the messages will be received and sent
        GUI frame = new GUI();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }
}
