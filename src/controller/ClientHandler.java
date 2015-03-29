package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

	private Socket client;

	private Server server;

	private String buf;

	private BufferedWriter out;

	public ClientHandler(Socket client, Server server) {
		this.client = client;
		this.server = server;
	}
	
	/**
	 * Read from the socket until the client stop the connection and spread its messages
	 */
	public void run() {
		try {
			BufferedReader readClient = new BufferedReader(
					new InputStreamReader(client.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					client.getOutputStream()));

			server.register(client, out);

			out.write("Welcome to room chat.\n Type  \"/nick\" followed by your nickname to set your nickname.\n");
			out.newLine();
			out.flush();

			boolean stop = false;
			do {
				buf = readClient.readLine();
				if (buf == null)
					stop = true;
				else if (buf.contains("/nick")) {
					buf = buf.substring(6);
					server.register(client, buf);
				} else {
					insertNick();
					server.getMapConnexions().forEach(
							(outsocket, socket) -> spreadMessage(outsocket));
				}
			} while (!stop);
			server.delete(client);
			client.close();
		} catch (Exception e) {
			Logger log = Logger.getLogger("logs","resources/myResources");
			log.setLevel(Level.SEVERE);
			log.severe("listenningKey");
			log.severe(e.getMessage());
			return;
		}
	}
	
	/**
	 * Write the message in the desired socket
	 * @param p The buffered writer of the socket
	 */
	private void spreadMessage(BufferedWriter p) {
		try {
			if (!p.equals(out)) {
				p.write(buf);
				p.newLine();
				p.flush();
				System.out.println(buf);
			}
		} catch (Exception e) {
			Logger log = Logger.getLogger("logs","resources/myResources");
			log.setLevel(Level.SEVERE);
			log.severe("writingKey");
			log.severe(e.getMessage());
			return;
		}
	}
	
	/**
	 * Insert the nickname of the client in the message to be send
	 */
	private void insertNick() {
		if (server.getMapNicks().containsKey(client)) {
			buf = server.getMapNicks().get(client).concat(": ").concat(buf);
		}
	}

}
