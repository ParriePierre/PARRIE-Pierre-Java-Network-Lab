package controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerNIO extends AbstractMultiChat {
	
	private List<SocketChannel> userList;

	public ServerNIO(InetAddress host, int port) {
		super(host, port);
		userList=new ArrayList<SocketChannel>();
	}
	
	/**
	 * Writes the message to each client connected  
	 * @param bbuf Buffer that contains the message
	 */
	public void broadcastMessage(ByteBuffer bbuf)
	{
		ByteBuffer tmp=bbuf.duplicate();
		for(SocketChannel user : userList)
		{
			try {
				user.write(tmp);
				tmp=bbuf.duplicate();
			} catch (IOException e) {
				Logger log = Logger.getLogger("logs","resources/myResources");
				log.setLevel(Level.SEVERE);
				log.severe("writingKey");
				log.severe(e.getMessage());
			}
		}
	}

	@Override
	public void start() throws IOException {
		ServerSocketChannel server = ServerSocketChannel.open();
		server.bind(new InetSocketAddress(host, port));
		
		Selector selector = Selector.open();
		
		server.configureBlocking(false);
		server.register(selector, SelectionKey.OP_ACCEPT);
		
		while (true) {
			selector.select();
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> keyIterator = keys.iterator();
			
			while (keyIterator.hasNext()) {
				SelectionKey key = keyIterator.next();
				ByteBuffer bbuf = ByteBuffer.allocate(8192);
				
				if (key.isAcceptable()) {
					SocketChannel client = server.accept();
					userList.add(client);
					client.configureBlocking(false);
					client.register(selector, SelectionKey.OP_READ);
					System.out.print("New Client accepted\n");
				}
				
				if (key.isReadable() && key.isValid()) {
					if( ((SocketChannel) key.channel()).read(bbuf) == -1)
					{
						((SocketChannel) key.channel()).close();
						System.out.print("Client disconnected.\n");
					}
					else
					{
						Charset charset = Charset.defaultCharset();
						bbuf.flip();
						CharBuffer cbuf = charset.decode(bbuf);
						broadcastMessage(charset.encode(cbuf));
						System.out.println(cbuf);
						bbuf.compact();
					}
				}
				keyIterator.remove();
			}
		}
	}

}
