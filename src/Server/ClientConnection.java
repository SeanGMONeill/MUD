package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.PriorityQueue;
import java.util.Queue;

public class ClientConnection implements Runnable {
	
	Socket socket;
	Server server;
	
	PrintWriter out;
	BufferedReader in;
	
	Player player;
	
	Queue<String> waitingMessages;
	
	
	public ClientConnection(Socket socket, Server server) {
		this.socket = socket;
		this.server = server;
		player = null;
		waitingMessages = new PriorityQueue<String>();
	}

	public void run() {
		

		
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		}
		catch(IOException e) {
			return;
		}
		
		console("Connected to " + socket.getPort());
		
		
		out.println(" __                                 ___ ");
		out.println("/ _\\ ___  __ _ _ __   /\\/\\  /\\ /\\  /   \\");
		out.println("\\ \\ / _ \\/ _` | '_ \\ /    \\/ / \\ \\/ /\\ /");
		out.println("_\\ \\  __/ (_| | | | / /\\/\\ \\ \\_/ / /_// ");
		out.println("\\__/\\___|\\__,_|_| |_\\/    \\/\\___/___,'  ");
		
		out.println("Please tell me your player name");
		
		String input = getInput();
		
		
		
		console("Name: " + input);
			
		while(player == null) {
			try {
				player = PlayerLoader.loadPlayer(input, null);
			}
			catch(NoSuchPlayerException e) {
				out.println("Invalid player name, please try again:");
				input = getInput();
			}
			catch(CorruptFileException e) {
				out.println("Your player record is damaged. Please contact a developer.");
				try {
					console("Corrupt player file. Exiting.");
					socket.close();
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		
		if(server.isOnline(input)) {
			out.println(input + " is already connected. Disconnecting this session.");
			console(input + " already connected.");
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		
		
		server.setOnline(player.getName(), this);
		out.println("Loaded player " + player.getName() + " at co-ordinates [" + player.getPosition().getX() + ", " + player.getPosition().getY() + "]");
		console("Loaded player.");
		
		
		
		while(!socket.isClosed()) {
			//Do game stuff
			input = getInput();
			sendStuffToClient();
			
			if(input != null) {
				//console(input);
			}
		}
		
		if(player != null) {
			server.setOffline(player.getName());
			console("Player disconnected.");
		}
		return; //Return to kill the thread, because the connection is closed.
		
	}
	
	private void console(String text) {
		String playerName = "No player";
		if(player != null) {
			playerName = player.getName();
		}
		System.out.println(Thread.currentThread().getId() + "["+ playerName +"]> " + text);
	}
	
	private String getInput() {
		String input;
				
		
		try {
			input = in.readLine();
			
			if(input == null) {
				//console("Client timed out.");
				try {
					socket.close();
					return null;
				} catch (IOException e1) {
					console("Oopsie.");
				}
			}
			
			
			return input;
		} 
		catch(SocketTimeoutException e) {
			//This is just to give us a wee chance to send stuff to the client, because readLine() is a blocking method.
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private void sendStuffToClient() {
		String message;
		while((message = waitingMessages.poll()) != null) {
			out.println(message);
		}
	}
	
	public void queueMessage(String message) {
		waitingMessages.add(message);
	}
	
}
