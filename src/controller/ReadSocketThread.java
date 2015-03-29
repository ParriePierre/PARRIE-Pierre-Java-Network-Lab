package controller;

import java.io.BufferedReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Platform;

public class ReadSocketThread extends Thread {
	private BufferedReader in;

	private MessageDisplayerController viewController;
	
	private String buf;

	public ReadSocketThread(BufferedReader in,
			MessageDisplayerController viewController) {
		super();
		this.in = in;
		this.viewController = viewController;
	}
	
	/**
	 * Thread used to read the messages from the socket
	 */
	public void run() {
		
		boolean stop = false;
		try {
			do {
				buf = in.readLine();
				if (buf != null)
				{
					System.out.print(buf);
					Platform.runLater(()->viewController.displayMessage(buf));
				}
			} while (!stop);
		} catch (Exception e) {
			Logger log = Logger.getLogger("logs","resources/myResources");
			log.setLevel(Level.SEVERE);
			log.severe("listenningKey");
			log.severe(e.getMessage());
			return;
		}
	}
}
