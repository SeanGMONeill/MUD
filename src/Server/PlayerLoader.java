package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class PlayerLoader {

	public static Player loadPlayer(String name, Server server) throws NoSuchPlayerException, CorruptFileException {
		name = name.toLowerCase();
		File file = new File("res/players/"+name+".json");
		if(!file.exists()) {
			throw new NoSuchPlayerException();
		}
		
		FileInputStream stream;
		try {
			stream = new FileInputStream(file);
		}
		catch(FileNotFoundException e){
			throw new NoSuchPlayerException();
		}

		JsonReader reader = Json.createReader(stream);
		JsonObject playerObject = reader.readObject();
		
		
		String playerName;
		String currentRoomID;
		
		try {
			playerName = playerObject.getString("name");
			currentRoomID = playerObject.getString("currentRoomID");
		}
		catch(NullPointerException e) {
			throw new CorruptFileException();
		}
		
		
		Room currentRoom;
		
		try {
			currentRoom = server.getRoomByID(currentRoomID);
		}
		catch(InvalidRoomException e) {
			Server.logError("Player file of: " + playerName + " claims they are in room: " + currentRoomID + ", which doesn't exist.");
			throw new CorruptFileException();
		}
		
		return new Player(playerName, currentRoom, server);
		
		
	}


}
