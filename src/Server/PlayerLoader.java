package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class PlayerLoader {

	public static Player loadPlayer(String name, Map map) throws NoSuchPlayerException, CorruptFileException {
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
		int playerX;
		int playerY;
		
		try {
			playerName = playerObject.getString("name");
			playerX = playerObject.getInt("posX");
			playerY = playerObject.getInt("posY");
		}
		catch(NullPointerException e) {
			throw new CorruptFileException();
		}
		
		
		Position playerPos = new Position(playerX, playerY);
		
		return new Player(playerName, playerPos, map);
	}


}
