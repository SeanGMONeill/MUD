package Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;

public class PlayerSaver {

	public static void savePlayer(Player player) {
		
		JsonObjectBuilder playerObjectBuilder = Json.createBuilderFactory(null).createObjectBuilder();
			playerObjectBuilder.add("name", player.getName());
			playerObjectBuilder.add("currentRoomID", player.getRoom().getID());
		
		JsonObject playerObject = playerObjectBuilder.build();
		
		File file = new File("res/players/" + player.getName().toLowerCase() + ".json");
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Server.logError(e);
			}
		}
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(file);
			JsonWriter jsonWriter = Json.createWriter(fileWriter);
			
			jsonWriter.write(playerObject);
			
			jsonWriter.close();
			fileWriter.close();
			
		} catch (IOException e) {
			Server.logError("Error when writing player " + player.getName() + " to file. Aborted.");
			return;
		}
		
	}
	
}
