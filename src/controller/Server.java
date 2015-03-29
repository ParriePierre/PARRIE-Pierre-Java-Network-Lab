package controller;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Create a multi thread server that uses sockets.
 * @author parrie
 *
 */
public class Server extends AbstractMultiChat {

	private Map<BufferedWriter, Socket> connexions;

	private Map<Socket, String> nicks;

	public Server(InetAddress host, int port) {
		super(host, port);
		connexions = new HashMap<BufferedWriter, Socket>();
		nicks = new HashMap<Socket, String>();
	}

	@Override
	public void start() {
		ServerSocket socket;
		try {
			socket = new ServerSocket(port, 10, host);
			ExecutorService es = Executors.newCachedThreadPool();
			while (true) {
				Socket client = socket.accept();
				System.out.print("New client\n");
				es.submit(new ClientHandler(client, this));
			}
		} catch (Exception e) {
			Logger log = Logger.getLogger("logs","resources/myResources");
			log.setLevel(Level.SEVERE);
			log.severe("connectionKey");
			log.severe(e.getMessage());
			return;
		}
	}

	/**
	 * Register socket into map object of connexions
	 */
	public synchronized void register(Socket client, BufferedWriter output) {
		connexions.put(output, client);
	}

	/**
	 * Delete client from the map connexions
	 * @param client
	 */
	public synchronized void delete(Socket client) {
		connexions.remove(client);
		nicks.remove(client);
	}

	public synchronized Map<BufferedWriter, Socket> getMapConnexions() {
		return connexions;
	}

	/**
	 * register nicks into map object
	 */
	public synchronized void register(Socket client, String nick) {
		nicks.put(client, nick);
	}

	public synchronized Map<Socket, String> getMapNicks() {
		return nicks;
	}

}
