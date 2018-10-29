package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import Server.Position.Direction;

public class Room {

	String roomID;
	
	String roomName;
	
	String roomDescription;
	
	Map<Direction, String> exits;
	
	Set<Player> players;
	
	Server server;
	
	public Room(File roomFile, Server server) throws CorruptFileException, FileNotFoundException {
		
		FileInputStream stream;
			
		stream = new FileInputStream(roomFile);

		JsonReader reader = Json.createReader(stream);
		JsonObject roomObject = reader.readObject();
		
		
		//Load vital room components first. If they're absent, just throw a CorruptFileException.
		try {
			roomID = roomObject.getString("roomID");
			roomName = roomObject.getString("roomName");
		}
		catch(NullPointerException e) {
			throw new CorruptFileException("File: " + roomFile.getName() + " is corrupt.");
		}
		
		roomDescription = roomObject.getString("roomDescription", "");
		
		exits = new HashMap<>();
		
		addExit(roomObject.getString("north", null), Position.Direction.NORTH);
		addExit(roomObject.getString("south", null), Position.Direction.SOUTH);
		addExit(roomObject.getString("east", null), Position.Direction.EAST);
		addExit(roomObject.getString("west", null), Position.Direction.WEST);
		addExit(roomObject.getString("up", null), Position.Direction.UP);
		addExit(roomObject.getString("down", null), Position.Direction.DOWN);
		
		
		players = new HashSet<Player>();
		this.server = server;

	}
	
	public String getName() {
		return this.roomName;
	}
	
	public String getID() {
		return roomID;
	}
	
	public String getDescription() {
		return roomDescription;
	}
	
	private void addExit(String roomID, Position.Direction direction) {
		if(roomID != null && roomID.length()>0) {
			exits.put(direction, roomID);
		}
	}
	
	public Boolean canGo(Position.Direction direction) {
		return exits.containsKey(direction);
	}
	
	public Room getRoom(Position.Direction direction) throws InvalidRoomException {
		String room;
		try {
			room = exits.get(direction);
		}
		catch(NullPointerException e) {
			throw new InvalidRoomException("No room in this direction.");
		}
		
		return server.getRoomByID(room);
	}
	
	public String toShortString() {
		return roomName + ".\n" + roomDescription;
	}
	
	public String toLongString() {
		return toShortString() + "\nYou can go " + exitsToString() + ".";
	}
	
	public String exitsToString() {
		String exitString = "";
		
		List<Position.Direction> directions = new ArrayList<>(exits.keySet());
		if(directions.size() == 0) {
			exitString = "nowhere";
		}
		else {
			for(int i = 0; i < directions.size(); i++) {
				exitString = exitString + directions.get(i).toString();
				if(i == directions.size()-2) {
					if(directions.size() == 2) {
						exitString = exitString + " or ";
					}
					else if(directions.size() > 2) {
						exitString = exitString + ", or ";
					}
				}
				else if(i != directions.size()-1) {
					exitString = exitString + ", ";
				}
			}
		}
		
		return exitString;
	}
	
	public void enterRoom(Player player) {
		players.add(player);
		server.messagePlayer(player, toShortString());
	}
	
	public void leaveRoom(Player player) {
		players.remove(player);
	}
	
	public List<Player> getPlayers(){
		return new ArrayList<Player>(players);
	}
	
	public void messageAllPlayers(String message) {
		for(Player player : getPlayers()) {
			server.messagePlayer(player, message);
		}
	}
}
