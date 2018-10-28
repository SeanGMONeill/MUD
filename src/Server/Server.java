package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

	final int port = 53490;
	
	public static void main(String[] args) throws IOException {
			
		new Server();
		
	}
		
	
	ThreadGroup connections;
	Map<String, ClientConnection> onlinePlayers;
	
	
	public Server() throws IOException {
		
		//This is the actual socket we're listening on
		ServerSocket serverSocket = new ServerSocket(port);
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownThread(serverSocket)));
		
		//Keeps track of active players and connections
		connections = new ThreadGroup("Connections");
		onlinePlayers = new HashMap<String, ClientConnection>();
		
		//Client socket
		Socket socket = null;
		
		
		System.out.println("Server started.");
	
		
		while(true) {
			
			socket = serverSocket.accept();
			socket.setSoTimeout(100);
			ClientConnection clientConn = new ClientConnection(socket,  this);
			Thread clientThread = new Thread(connections, clientConn);
			clientThread.start();
		}
	}
	
	public Boolean isOnline(String player) {
		player = player.toLowerCase();
		return onlinePlayers.containsKey(player);
	}
	
	public void setOnline(String player, ClientConnection connection) {
		player = player.toLowerCase();
		onlinePlayers.put(player, connection);
	}
	
	public void setOffline(String player) {
		player = player.toLowerCase();
		onlinePlayers.remove(player);
	}
	
	
}
