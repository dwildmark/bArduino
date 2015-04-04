package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class GUI extends JFrame {
	
	private static final long serialVersionUID = 6893254733282945476L;
	private JTextArea messagesArea;
    private JButton sendButton;
    private JLabel lblPort, lblIP;
    private JTextField message, tfPort, tfIP;
    private JButton connectServer;
    private TCPClient mServer;

	public GUI() {
		JPanel panelIP = new JPanel();
		panelIP.setLayout(new BoxLayout(panelIP, BoxLayout.X_AXIS));
		
		JPanel panelPort = new JPanel();
		panelPort.setLayout(new BoxLayout(panelPort, BoxLayout.X_AXIS));
		
		JPanel panelFields = new JPanel();
        panelFields.setLayout(new BoxLayout(panelFields,BoxLayout.X_AXIS));

        JPanel panelFields2 = new JPanel();
        panelFields2.setLayout(new BoxLayout(panelFields2,BoxLayout.X_AXIS));
        
        lblIP = new JLabel("Server IP");
        lblPort = new JLabel("Server Port");
        tfIP = new JTextField();
        tfPort = new JTextField();

        //here we will have the text messages screen
        messagesArea = new JTextArea();
        messagesArea.setColumns(30);
        messagesArea.setRows(10);
        messagesArea.setEditable(false);
        
        sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // get the message from the text view
                String messageText = message.getText();
                // add message to the message area
                messagesArea.append("\n" + messageText);
                // send the message to the client
                mServer.sendMessage(messageText);
                // clear text
                message.setText("");
            }
        });
        
        connectServer = new JButton("Connect to server");
        connectServer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // disable the start button
                connectServer.setEnabled(false);

                //creates the object OnMessageReceived asked by the TCPServer constructor
                mServer = new TCPClient(new TCPClient.OnMessageReceived() {
                    @Override
                    //this method declared in the interface from TCPServer class is implemented here
                    //this method is actually a callback method, because it will run every time when it will be called from
                    //TCPServer class (at while)
                    public void messageReceived(String message) {
                        messagesArea.append("\n "+message);
                    }
                }, Integer.parseInt(tfPort.getText()), tfIP.getText());
                mServer.start();

            }
        });
        
      //the box where the user enters the text (EditText is called in Android)
        message = new JTextField();
        message.setSize(200, 20);

        //add the buttons and the text fields to the panel
        panelIP.add(lblIP);
        panelIP.add(tfIP);
        
        panelPort.add(lblPort);
        panelPort.add(tfPort);
        
        panelFields.add(messagesArea);
        panelFields.add(connectServer);

        panelFields2.add(message);
        panelFields2.add(sendButton);

        getContentPane().add(panelIP);
        getContentPane().add(panelPort);
        getContentPane().add(panelFields);
        getContentPane().add(panelFields2);

        getContentPane().setLayout(new BoxLayout(getContentPane(),BoxLayout.Y_AXIS));

        setSize(300, 170);
        setVisible(true);
	}

}
