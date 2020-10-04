import java.io.*;
import java.util.*;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import java.net.*;

/**
 * Code from 2.1.2 Stream sockets - server side with use of SSL
 * 
 */

public class Server212 {

/*
 * TO REMEMBER: HashMap works quicker with huge amount of users
 */	
static Vector <ClientHandler> activeClientsVector= new Vector<>(); //stores active clients
static int activeClientsNumber= 0; //counts active clients
	
	public static void main(String[] args) throws IOException {
	/**
	  * new SSL code */
		
		System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
		System.setProperty("javax.net.ssl.keyStorePassword", "123"); //  !- not a real password
		
		SSLServerSocketFactory sFactory=(SSLServerSocketFactory)SSLServerSocketFactory.getDefault();
				SSLServerSocket serverSocket=(SSLServerSocket)(sFactory).createServerSocket(2000);
				System.out.println("Server is started and is ready to accept the clients..");
				serverSocket.setNeedClientAuth(false);
	/**
	 *SSL code ends
	  */			
						
		while (true) {
			SSLSocket socket=null;	
			
			try {
			//accepts incoming request
			socket=(SSLSocket) serverSocket.accept();
			System.out.println ("New client requests received :"+socket);
			
			//obtains input and output streams
			DataInputStream dis=new DataInputStream(socket.getInputStream());
			DataOutputStream dos= new DataOutputStream(socket.getOutputStream());
			
			System.out.println("Creating a new handler for this client...");
			
			//creates new ClientHandler object for this request
			ClientHandler clientObject= new ClientHandler(socket,"client "+ activeClientsNumber, dis, dos);
			
			//creates new thread for Client Object
			Thread t= new Thread(clientObject);
			
			System.out.println("Adding this client to active client list. Name: Client "+ activeClientsNumber);
			
			//adds client object to the clients list (vector)
			activeClientsVector.add(clientObject);
			
			//starts the thread
			t.start();
			
			//increments numberof clients
			activeClientsNumber++;
			
			} catch(Exception e) {
			//socket.close();
			e.printStackTrace();
			}
		}

	}

}

/*
 * Class for handling requests
 */
class ClientHandler implements Runnable{

	Scanner scn= new Scanner(System.in);
	private String name;
	final DataInputStream dis;
	final DataOutputStream dos;
	Socket socket;
	boolean isLoggedIn;
	
	//constructor
	public ClientHandler(Socket socket, String name ,DataInputStream dis, DataOutputStream dos) {
		this.socket=socket;
		this.name=name;
		this.dis=dis;
		this.dos=dos;
		this.isLoggedIn=true;
	}
	
	@Override
	public void run() {
		
		String received;
		
		while(true) {
			
			try {
				received=dis.readUTF();				
				System.out.println (received);
				
				if(received.equals("END")){
					this.isLoggedIn=false;
					//this.dos.close();
					//this.dis.close();
					this.socket.close();
					System.out.println("Connection closed"); 
					break;
				}
				
				//break the String into message and recipient
				StringTokenizer st= new StringTokenizer(received, "#");
				String msgToSend = st.nextToken();
				String recipient= st.nextToken();
				
				//searches for the recipient in the "activeClientsVector" in Server class
				
				for(ClientHandler client: Server212.activeClientsVector) {
					//if the recipient is found, write on its output stream
					if(client.name.equals(recipient) && client.isLoggedIn==true) {
						
						client.dos.writeUTF(this.name+" (IP: " + socket.getInetAddress()+" ) : "+ msgToSend);
						break;
					}
				}
				
			}catch(IOException e) {				
				e.printStackTrace();
				
			}//catch ends
			
		}// infinitive while loop ends
	
		
		
		try
        { 
            // closing resources 
            this.dis.close(); 
            this.dos.close();
           // this.socket.close();
              
        }catch(IOException e){ 
            e.printStackTrace(); 
        } 
		
	}
	


} //CLIENTHANDLER ENDS

	

