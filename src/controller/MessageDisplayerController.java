package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;

import view.MessageDisplayer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class MessageDisplayerController extends Application {

	private MessageDisplayer v;
	
	private InetAddress host;
	
	private int port;
	
	private Socket server;
	
	private BufferedWriter out;
	
	private BufferedReader in;

	public MessageDisplayerController() {
		super();
	}
	
	/**
	 * 
	 * @param host
	 * @param port
	 */
	public void setHostAndPort(InetAddress host, int port)
	{
		this.host=host;
		this.port=port;
	}

	public void start() {
		Application.launch(MessageDisplayerController.class,host.getHostName(),String.valueOf(port));
	}

	@Override
	public void start(Stage arg0) throws Exception {
		Group root = new Group();
		Scene scene = new Scene(root, 800, 600, Color.WHITE);
		arg0.setTitle("Chat Application.");
		host=InetAddress.getByName(this.getParameters().getRaw().get(0));
		port=Integer.parseInt(this.getParameters().getRaw().get(1));
		server=new Socket(host,port);
		in = new BufferedReader(new InputStreamReader(server.getInputStream()));
		out = new BufferedWriter(new OutputStreamWriter(
				server.getOutputStream()));
		v = new MessageDisplayer(this);
		v.launch();
		ReadSocketThread rst = new ReadSocketThread(in, this);
		rst.start();
		root.getChildren().add(v);
		arg0.setScene(scene);
		arg0.show();
	}
	
	/**
	 * Function in JavaFX thread time that writes the message on the client screen.
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
	}
	
	/**
	 * Followed the message to the server, by writing in the socket
	 * @param msg
	 */
	public void writeSocket(String msg) {
		try {
			out.write(msg);
			out.newLine();
			out.flush();
		} catch (IOException e) {
			System.out.print("Error while writting in socket\n");
		}
	}
}
