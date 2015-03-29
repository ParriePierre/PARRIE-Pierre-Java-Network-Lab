package controller;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import view.MessageDisplayer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MulticastSocketClient extends Application {
	
	private Map<InetAddress, Integer> groups;
	
	private MulticastSocket s;
	
	private String buf;
	
	private MessageDisplayer v;
	
	private InetAddress host;
	
	private int port;
	
	/**
	 * Initialize the MulitcastSocketClient
	 * @param host
	 * @param port
	 * @throws IOException
	 */
	public void setHostPort(InetAddress host , int port)
	{
		this.host=host;
		this.port=port;
		groups = new HashMap<InetAddress, Integer>();
	}
	
	/**
	 * Add the requested host to the group of connected clients
	 * @param host
	 * @param port
	 */
	public void addToGroup(InetAddress host, int port)
	{
		try {
			s.joinGroup(host);
			groups.put(host, port);
		} catch (IOException e) {
			Logger log = Logger.getLogger("logs","resources/myResources");
			log.setLevel(Level.SEVERE);
			log.severe("addHostKey");
			log.severe(e.getMessage());
			return;
		}
	}
	
	/**
	 * Reads the message from the socket
	 */
	public void readNetworkMessages()
	{
		byte [] buffer =new byte[1000];
		DatagramPacket resp = new DatagramPacket(buffer, buffer.length);
		try {
			s.receive(resp);
			String answer =new String(buffer);
			Platform.runLater(()-> displayMessage(answer));
		} catch (IOException e) {
			Logger log = Logger.getLogger("logs","resources/myResources");
			log.setLevel(Level.SEVERE);
			log.severe("listenningKey");
			log.severe(e.getMessage());
			return;
		}
	}
	
	/**
	 * Spread the msg to each client connected
	 * @param msg
	 */
	public void sendNetworkMessage(String msg)
	{
		buf=msg;
		groups.forEach((x,y)->writeMessage(x,y));
	}
	
	/**
	 * Write the message to the desired host
	 * @param host
	 * @param port
	 */
	public void writeMessage(InetAddress host,int port)
	{
		DatagramPacket resp = new DatagramPacket(buf.getBytes(), buf.length(),host,port);
		try {
			s.send(resp);
		} catch (IOException e) {
			Logger log = Logger.getLogger("logs","resources/myResources");
			log.setLevel(Level.SEVERE);
			log.severe("writingKey");
			log.severe(e.getMessage());
			return;
		}
	}
	
	/**
	 * Prints msg to the client application
	 * @param msg
	 */
	public void displayMessage(String msg) {
		String tmp;
		tmp = v.getMessagesDisplayer().getText();
		if (!tmp.isEmpty())
			tmp=tmp.concat("\n").concat(msg);
		else
			tmp=msg;
		v.getMessagesDisplayer().setText(tmp);
		v.getSheet().clear();
	}
	
	public void start()
	{
		Application.launch(MulticastSocketClient.class,host.getHostName(),String.valueOf(port));
	}

	@Override
	public void start(Stage arg0) throws Exception {
		Group root = new Group();
		Scene scene = new Scene(root, 800, 600, Color.WHITE);
		arg0.setTitle("Chat Application.");
		
		host=InetAddress.getByName(this.getParameters().getRaw().get(0));
		port=Integer.parseInt(this.getParameters().getRaw().get(1));
		
		groups = new HashMap<InetAddress, Integer>();
		
		s = new MulticastSocket(port);
		s.joinGroup(host);
		groups.put(host, port);		
		
		v = new MessageDisplayer(this);
		v.launch();
		
		MulticastSocketReader msr = new MulticastSocketReader(this);
		msr.start();
		
		root.getChildren().add(v);
		arg0.setScene(scene);
		arg0.show();
		
	}
}
