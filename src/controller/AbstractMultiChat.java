package controller;

import java.io.IOException;
import java.net.InetAddress;

public abstract class AbstractMultiChat implements MultiChatServer {
	
	protected InetAddress host;
	
	protected int port;

	public AbstractMultiChat(InetAddress host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public abstract void start() throws IOException;

}
