package controller;

public class MulticastSocketReader extends Thread {
	private MulticastSocketClient server;

	public MulticastSocketReader(MulticastSocketClient server) {
		super();
		this.server = server;
	}
	
	/**
	 * Thread used to read the message from the socket
	 */
	public void run()
	{
		while(true)
		{
			server.readNetworkMessages();
		}
	}
}
