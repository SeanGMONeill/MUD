package Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Server {

	final int port = 53490;
	private boolean serverOnline = true;
	
	public static void main(String[] args) throws IOException {
			
		new Server();
		
	}
		
	
	ThreadGroup connections;
	Map<String, ClientConnection> onlinePlayers;
	Map<String, Room> rooms;
	MobManager mobManager;
	
	
	public Server() throws IOException {
		
		//Keeps track of active players and connections
		connections = new ThreadGroup("Connections");
		onlinePlayers = new HashMap<String, ClientConnection>();
		
		//Load all rooms before we start letting players join, otherwise they'll all get InvalidRoomExceptions
		loadAllRooms();
		
		//Start all the mobs on their merry way
		 mobManager = new MobManager(this);
		 mobManager.loadMobsFromFolder(new File("res/mobs"));
		 new Thread(mobManager).start();
		
		
		//This is the actual socket we're listening on
		ServerSocket serverSocket = new ServerSocket(port);
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownThread(this, serverSocket, onlinePlayers)));
		

		
		//Client socket
		Socket socket = null;
		
		
		System.out.println("Server started.");
	
		
		while(serverOnline()) {
			
			socket = serverSocket.accept();
			socket.setSoTimeout(60000); //60 second timeout on connect, for logging in.
			ClientConnection clientConn = new ClientConnection(socket,  this);
			Thread clientThread = new Thread(connections, clientConn);
			clientThread.start();
		}
	}
	
	public Boolean serverOnline() {
		return serverOnline;
	}
	
	public void setServerOffline() {
		serverOnline = false;
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

	
	private void loadAllRooms() {
		rooms = new HashMap<>();
		File roomFolder = new File("res/rooms");
		File[] listOfRoomFiles = roomFolder.listFiles();
		
		for(File roomFile : listOfRoomFiles) {
			Room newRoom;
			try {
				newRoom = new Room(roomFile, this);
				rooms.put(newRoom.getID(), newRoom);
			}
			catch(Exception e) {
				Server.logError(e);
			}
		}
	}
	
	public Room getRoomByID(String roomID) throws InvalidRoomException {
		
		try {
			Room room = rooms.get(roomID);
			return room;
		}
		catch(NullPointerException e) {
			throw new InvalidRoomException("Room does not exist: " + roomID);
		}
	}
	
	
	public static void logError(String error) {
		if(error ==  null || error.length() == 0) {
			return;
		}
		System.out.println(error);
		String dateTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		File log;
		try {
			String logName = "res/logs/log_" + dateTime + "_"+ Integer.toString(error.hashCode()) + ".txt";
			log = new File(logName);
			if(!log.exists()) {
				log.createNewFile();
			}
			
			FileWriter fileWriter = new FileWriter(log);
			fileWriter.write(error);
			fileWriter.close();
			
		} catch (IOException e) {
			System.out.println("Error logging is throwing errors. The end of times has surely arrived.");
			e.printStackTrace();
		}
	}
	
	public static void logError(Exception e) {
		logError(e.getMessage());
	}
	
	public void messagePlayer(Player player, String message) {
		ClientConnection playerClient;
		if((playerClient = onlinePlayers.get(player.getName().toLowerCase())) != null) {
			playerClient.queueMessage(message);
		}
	}
	
	public void messageAllPlayers(String message) {
		for(ClientConnection client : onlinePlayers.values()) {
			client.queueMessage(message);
		}
	}
	
}
