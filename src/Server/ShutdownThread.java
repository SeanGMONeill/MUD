package Server;

import java.io.IOException;
import java.net.ServerSocket;

public class ShutdownThread implements Runnable{

	ServerSocket mainSocket;
	
	public ShutdownThread(ServerSocket serverSocket) {
		mainSocket = serverSocket;
	}
	
	public void run() {
		try {
			mainSocket.close();
			System.out.println("Shutting down...");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}


	
}
