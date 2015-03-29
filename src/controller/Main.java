package controller;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;

import java.net.InetAddress;
import java.util.ResourceBundle;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

	public static void main(String[] args) {

		try {

			ResourceBundle rbh = ResourceBundle.getBundle(
					"resources/myResources", new UTF8Control());
			
			Logger log = Logger.getLogger("logs","resources/myResources");
			FileHandler fh = new FileHandler("NetworkLabLogs.log");
			log.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
			log.setUseParentHandlers(false);

			InetAddress host = InetAddress.getLocalHost();
			int port = 3615;
			int client=1,server=0,nio=0;

			MulticastSocketClient msc;

			LongOpt[] longopts = new LongOpt[7];
			longopts[0] = new LongOpt("help", LongOpt.NO_ARGUMENT, null, 'h');
			longopts[1] = new LongOpt("address", LongOpt.REQUIRED_ARGUMENT,
					null, 'a');
			longopts[2] = new LongOpt("port", LongOpt.REQUIRED_ARGUMENT, null,
					'p');
			longopts[3] = new LongOpt("nio", LongOpt.NO_ARGUMENT, null, 'n');
			longopts[4] = new LongOpt("server", LongOpt.NO_ARGUMENT, null, 's');
			longopts[5] = new LongOpt("multicast", LongOpt.NO_ARGUMENT, null,
					'm');
			longopts[6] = new LongOpt("debug", LongOpt.NO_ARGUMENT, null,
					'd');

			Getopt g = new Getopt("testprog", args, "a:h::n::m::p:s::d::",
					longopts);

			int c;
			String arg;
			while ((c = g.getopt()) != -1) {
				System.out.printf("Options: %d , %c\n", c, (char) c);
				switch (c) {
				case 0:
					client=0;
					System.out.print(rbh.getString("helpKey"));
					break;

				case 1:
					arg = g.getOptarg().toString();
					host = InetAddress.getByName(arg);
					System.out.print(rbh.getString("serverAdressKey")
							+ host.getHostName() + "\n");
					break;

				case 2:
					arg = g.getOptarg();
					port = Integer.parseInt(arg);
					System.out.print(rbh.getString("portNumberKey") + port
							+ "\n");
					break;

				case 3:
					nio=1;
					break;

				case 4:
					server=1;
					client=0;
					break;

				case 5:
					client=0;
					System.out.print(rbh.getString("multicastKey"));
					msc = new MulticastSocketClient();
					msc.setHostPort(host, port);
					msc.start();
					break;
					
				case 6:
					log.setUseParentHandlers(true);
					break;

				case 'a':
					arg = g.getOptarg();
					host = InetAddress.getByName(arg);
					System.out.print(rbh.getString("serverAdressKey")
							+ host.getHostName() + "\n");
					break;

				case 'p':
					arg = g.getOptarg();
					port = Integer.parseInt(arg);
					System.out.print(rbh.getString("portNumberKey") + port
							+ "\n");
					break;

				case 'h':
					client=0;
					System.out.print(rbh.getString("helpKey"));
					break;

				case 'n':
					nio=1;
					break;

				case 'm':
					client=0;
					System.out.print(rbh.getString("multicastKey"));
					msc = new MulticastSocketClient();
					msc.setHostPort(host, port);
					msc.start();
					break;

				case 's':
					server=1;
					client=0;
					break;
					
				case 'd':
					log.setUseParentHandlers(true);
					break;

				case '?':
					System.out.print("Erreur");
					break;

				default:
					System.out.print("Erreur");
					break;
				}
			}
			if(client==1)
			{
				MessageDisplayerController view = new MessageDisplayerController();
				view.setHostAndPort(host, port);
				view.start();
			} else if (server==1 && nio==0)
			{
				System.out.print(rbh.getString("socketServerKey"));
				Server s = new Server(host, port);
				s.start();
			} else if (server==1 && nio==1)
			{
				System.out.print(rbh.getString("nioKey"));
				ServerNIO snio = new ServerNIO(host, port);
				snio.start();
			} else if (server==0 && nio==0)
			{
				System.out.print(rbh.getString("adviceKey"));
			}
			
		} catch (Exception e) {
			Logger log = Logger.getLogger("logs","resources/myResources");
			log.setLevel(Level.SEVERE);
			log.severe("hostKey");
			log.severe(e.getMessage());
			return;
		}
	}
}
