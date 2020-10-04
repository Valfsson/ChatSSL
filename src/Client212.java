import java.io.*;
import java.net.*;
import java.util.Scanner;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Code from 2.1.2 Stream sockets -client side with use of SSL
 * 
 */

//Implementation assumes the message to be of the format "message # recipient"


public class Client212 {

	static String defaultHost="127.0.0.1";
	static int defaultPort=2000;
	static String host;
	static int port;
	private static boolean alive=true;
	
	public Client212(int portIn) {
		host=defaultHost;
		port=portIn;
	}
	public static void main(String[] args) throws UnknownHostException, IOException {

		 if((args.length==0)) {
			 host=defaultHost;
			 port=defaultPort;
		 }
		 
/**
 * new SSL code
 */
		 
		System.setProperty("javax.net.ssl.trustStore", "myTrustStore.jts");
		System.setProperty("javax.net.ssl.trustStorePassword", "123"); //  !- not a real password
				
		// Establishing connection
		SSLSocketFactory sFactory=(SSLSocketFactory)SSLSocketFactory.getDefault();
		SSLSocket SSLsocket=(SSLSocket)sFactory.createSocket(host, port);
/**
 *  new SSL code ends
 */
		
		//obtaining Input and Output
		DataInputStream dis = new DataInputStream(SSLsocket.getInputStream());
		DataOutputStream dos = new DataOutputStream(SSLsocket.getOutputStream());
		Scanner scn = new Scanner(System.in);
		
		// creates Thread for sending messsage
		Thread sendMessage = new Thread(new Runnable() {

			@Override
			public synchronized void run() {
				while (alive) {
					// reads the message to deliver
					String msg = scn.nextLine();

					try {						
						// write on the output stream
						dos.writeUTF(msg);
						
						if (msg.equals("END")) {
							System.out.println("Connection : " + SSLsocket+ " is closed");
							alive=false;							
							break;
						}

					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}); 

		// Thread for reading message
		Thread readMessage = new Thread(new Runnable() {

			@Override
			public synchronized void run() {
			while (alive) {
				String msg;
					
					try {
						while ((msg = dis.readUTF()) != null) {
							System.out.println(msg);
						}

					} catch (IOException e) {}
				}  
			}

		}); // readMessage thread ends

		sendMessage.start();
		readMessage.start();

	}
	
}
