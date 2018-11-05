package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Map;

public class ShutdownThread implements Runnable{

	ServerSocket mainSocket;
	Map<String, ClientConnection> onlinePlayers;
	
	public ShutdownThread(ServerSocket serverSocket, Map<String, ClientConnection> onlinePlayers) {
		mainSocket = serverSocket;
		this.onlinePlayers = onlinePlayers; 
	}
	
	public void run() {
		try {
			mainSocket.close();
			System.out.println("Shutting down...");
			
			for(ClientConnection conn : onlinePlayers.values()) {
				conn.exitPlayer("Server is going down. Check back soon!");
				System.out.println("Disconnected player.");
			}
			System.out.println(">>Disconnected all<<");
		}
		catch(IOException e) {
			Server.logError(e);
		}
	}


	
}
