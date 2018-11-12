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
	Set<Mobile> mobs;
	
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
		mobs = new HashSet<Mobile>();
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
			List<String> dirsToStrings = new ArrayList<>();
			for(Position.Direction direction : directions) {
				dirsToStrings.add(direction.toString());
			}
			exitString = TextUtils.listToEnglish(dirsToStrings, "or");
		}
		
		return exitString;
	}
	
	public void enterRoom(Entity entity) {
		messageAllPlayers(entity.getName() + " wanders over.");
		if(entity instanceof Player) {
			Player player = (Player)entity;
			player.message(playersToString());
			players.add(player);
			player.message(toShortString());
		}
		else if(entity instanceof Mobile) {
			mobs.add((Mobile)entity);
		}
	}
	
	public void leaveRoom(Entity entity) {
		if(entity instanceof Player) {
			Player player = (Player)entity;
			players.remove(player);
			if(players.isEmpty()) {
				saveRoomState();
			}
		}
		else if(entity instanceof Mobile) {
			mobs.remove((Mobile)entity);
		}
		messageAllPlayers(entity.getName() + " saunters away.");
	}
	
	public List<Player> getPlayers(){
		return new ArrayList<Player>(players);
	}
	
	public void messageAllPlayers(String message) {
		for(Player player : getPlayers()) {
			player.message(message);
		}
	}
	
	private String playersToString() {
		if(players.size()==0) {
			return "Nobody else is here.";
		}
		else if(players.size()==1) {
			return ((Player)players.toArray()[0]).getName() + " is here.";
		}
		else {
			List<String> playerNames = new ArrayList<>();
			for(Player player : players) {
				playerNames.add(player.getName());
			}
			return TextUtils.listToEnglish(playerNames, "and") + " are here.";
		}
	}
	
	private void saveRoomState() {
		
	}
}
