package Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class MobLoader {

	public static Mobile loadMob(File file, Server server) throws CorruptFileException, NoSuchMobileException {

		if(!file.exists()) {
			throw new NoSuchMobileException();
		}
		
		FileInputStream stream;
		try {
			stream = new FileInputStream(file);
		}
		catch(FileNotFoundException e){
			throw new NoSuchMobileException();
		}

		JsonReader reader = Json.createReader(stream);
		JsonObject mobileObject = reader.readObject();
		
		
		String mobName;
		String currentRoomID;
		String startRoomID;
		int currentStep;
		List<String> routine;
		routine = new ArrayList<String>();
		
		try {
			mobName = mobileObject.getString("name");
			currentRoomID = mobileObject.getString("currentRoomID");
			startRoomID = mobileObject.getString("startRoomID");
			currentStep = mobileObject.getInt("currentStep");
			JsonArray routineArray = mobileObject.getJsonArray("routine");
			if(routineArray != null){
				for(int i = 0; i < routineArray.size(); i++) {
					routine.add(routineArray.getString(i));
				}
			}
		}
		catch(NullPointerException e) {
			throw new CorruptFileException(file.getAbsolutePath());
		}
		
		
		Room currentRoom, defaultRoom;
		
		try {
			currentRoom = server.getRoomByID(currentRoomID);
			defaultRoom = server.getRoomByID(startRoomID);
		}
		catch(InvalidRoomException e) {
			try {
				currentRoom = server.getRoomByID(startRoomID);
				defaultRoom = currentRoom;
				currentStep = 0;
			}
			catch(InvalidRoomException e1) {
				Server.logError("Mob file of: " + mobName + " claims they are in room: " + currentRoomID + ", which doesn't exist.");
				throw new CorruptFileException();
			}
		}
		
		return new Mobile(mobName, currentRoom, defaultRoom, currentStep, routine, server);
		
		
	}


}
