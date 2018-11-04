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
		
		out.println("Please tell me your player name");
		
		String input = promptPlayerName();
		
		if(input == null) {
			//Abort thread, we've already disconnected the user.
			return;
		}
		
		
		console("Name: " + input);
			
		while(player == null) {
			try {
				player = PlayerLoader.loadPlayer(input, server);
			}
			catch(NoSuchPlayerException e) {
				out.println("Invalid player name, please try again:");
				input = getLoopingInput();
				if(input != null) {
					handleCommand(input);
				}
			}
			catch(CorruptFileException e) {
				out.println("Your player record is damaged. Please contact a developer.");
				try {
					console("Corrupt player file. Exiting.");
					socket.close();
					Server.logError("Corrupt player file for: " + input);
					return;
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					Server.logError(e1);
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
				handleCommand(input);
			}
			
			sendStuffToClient();
		}
		
		if(player != null) {
			server.setOffline(player.getName());
			player.getRoom().leaveRoom(player);
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
	
	private void handleCommand(String input) {
		String[] splitInput = input.split(" ", 2);
		String command;
		String param = ""; 
		
		command = splitInput[0].toLowerCase();
		if(splitInput.length>1) {
			param = splitInput[1];
		}
		
		switch(command) {
			case "shout":
				player.shout(param);
				break;
			case "say":
				player.say(param);
				break;
			case "north":
			case "n":
				player.move(Position.Direction.NORTH);
				break;
			case "south":
			case "s":
				player.move(Position.Direction.SOUTH);
				break;
			case "east":
			case "e":
				player.move(Position.Direction.EAST);
				break;
			case "west":
			case "w":
				player.move(Position.Direction.WEST);
				break;
			case "up":
			case "u":
				player.move(Position.Direction.UP);
				break;
			case "down":
			case "d":
				player.move(Position.Direction.DOWN);
				break;
			case "look":
				server.messagePlayer(player, player.getRoom().toLongString());
				break;
			case "exit":
				exitPlayer("Thanks for playing!");
				break;
				
			default:
				out.println("No such command.");
		}
		
	}
	
}
