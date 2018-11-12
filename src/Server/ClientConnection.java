package Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
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
		
		out.println("\033[31;1;4mHello\033[0m");
		
		out.println("Please tell me your player name");
		
		String input = TextUtils.removeNonAlphabet(promptPlayerName());
		
		if(input == null) {
			//Abort thread, we've already disconnected the user.
			return;
		}
		
		
		console("Name: " + input);
			
		if(tryLoadPlayer(input) == true) {
			
		}
		else
		{
			//False return means tryLoadPlayer found some issue, and wants to disconnect the user.
			return;
		}
		
		
		server.setOnline(player.getName(), this);
		out.println("Loaded player " + player.getName() + " at " + player.getRoom().getName());
		player.getRoom().enterRoom(player);
		console("Loaded player.");
		
		try {
			//Setting timeout to 500ms, because we are not looping to handle timeouts intentionally, for asynchronous comms.
			socket.setSoTimeout(500);
		} catch (SocketException e) {
			Server.logError(e);
		}
		
		while(!socket.isClosed()) {
			//Do game stuff
			input = getLoopingInput();
			
			if(input != null) {
				player.handleCommand(this, input);
			}
			
			sendStuffToClient();
		}
		
		if(player != null) {
			server.setOffline(player.getName());
			player.getRoom().leaveRoom(player);
			PlayerSaver.savePlayer(player);
			console("Player disconnected.");
		}
		return; //Return to kill the thread, because the connection is closed.
		
	}

	private boolean tryLoadPlayer(String playerName) {
		while(player == null) {
			try {
				player = PlayerLoader.loadPlayer(playerName, server);
			}
			catch(NoSuchPlayerException e) {
				out.println("Invalid player name, please try again:");
				playerName = getLoopingInput();
				if(playerName != null) {
					player.handleCommand(this, playerName);
				}
			}
			catch(CorruptFileException e) {
				out.println("Your player record is damaged. Please contact a developer.");
				try {
					console("Corrupt player file. Exiting.");
					socket.close();
					Server.logError("Corrupt player file for: " + playerName);
					return false;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Server.logError(e1);
				}
			}
		}
		
		if(server.isOnline(playerName)) {
			out.println(playerName + " is already connected. Disconnecting this session.");
			console(playerName + " already connected.");
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Server.logError(e);
			}
			return false;
		}
		
		return true; //Success
	}
	
	private void console(String text) {
		String playerName = "No player";
		if(player != null) {
			playerName = player.getName();
		}
		System.out.println(Thread.currentThread().getId() + "["+ playerName +"]> " + text);
	}
	
	private String getLoopingInput() {
		String input;
				
		
		try {
			input = in.readLine();
			
			if(input == null) {
				//console("Client timed out.");
				try {
					socket.close();
					return null;
				} catch (IOException e1) {
					Server.logError(e1);
				}
			}
			
			
			return input;
		} 
		catch(SocketTimeoutException e) {
			//This is just to give us a wee chance to send stuff to the client, because readLine() is a blocking method.
		}
		catch (IOException e) {
			Server.logError(e);
		}
		
		return null;
	}
	
	private String promptPlayerName() {
		String input;
				
		
		try {
			input = in.readLine();
			
			if(input == null) {
				//console("Client timed out.");
				try {
					socket.close();
					return null;
				} catch (IOException e1) {
					Server.logError(e1);
				}
			}
			
			
			return input;
		} 
		catch(SocketTimeoutException e) {
			out.println("Timed out. Please enter a player name within 60 seconds next time.");
			try {
				socket.close();
			} catch (IOException e1) {
				Server.logError(e1);
			}
			return null;
			
		}
		catch (IOException e) {
			Server.logError(e);
		}
		
		return null;
	}
	
	private void sendStuffToClient() {
		String message;
		while((message = waitingMessages.poll()) != null) {
			out.println(message);
		}
	}
	
	public synchronized void queueMessage(String message) {
		waitingMessages.add(message);
	}
	
	public void exitPlayer(String message) {
		server.messagePlayer(player, message);
		try { socket.close(); }catch(Exception e) {Server.logError(e);}
	}
	
}
