package Server;

import Server.Position.Direction;

public abstract class Entity {
	
	String name;
	
	Server server;
	
	Room currentRoom;
	
	
	public Entity(String name, Room currentRoom, Server server) {
		this.name = name;
		this.currentRoom = currentRoom;
		this.server = server;
		
	}
	
	public void move(Direction direction) {
		if(currentRoom.canGo(direction)) {
			currentRoom.leaveRoom(this);
			try {
				currentRoom = currentRoom.getRoom(direction);
			}
			catch(Exception e) {
				Server.logError(e);
			}
			currentRoom.enterRoom(this);
		}
		else {
			message("You cannot move in this direction.");
		}
	}
	
	public abstract void message(String message);
	
	
	public String getName() {
		return name;
	}
	
	public Room getRoom() {
		return currentRoom;
	}
	
	//Say to current room
	public void say(String message) {
		if(message != null && message.length()>0){
			currentRoom.messageAllPlayers(name + " says: " + message);
		}
	}
	
	//Shout to the world!
	public void shout(String message) {
		if(message != null && message.length()>0) {
			server.messageAllPlayers(name + " shouts: " + message);
		}
	}
	
	//Emote to current room
	public void emote(String message) {
		if(message != null && message.length()>0){
			currentRoom.messageAllPlayers("\033[3m*" + name + " " + message + "*\033[0m");
		}
	}
	
	boolean handleCommand(String input) {
		String[] splitInput = input.split(" ", 2);
		String command;
		String param = ""; 
		
		command = splitInput[0].toLowerCase();
		if(splitInput.length>1) {
			param = splitInput[1];
		}
		
		switch(command) {
			case "shout":
				shout(param);
				break;
			case "say":
				say(param);
				break;
			case "emote":
			case "me":
				emote(param);
				break;
			case "north":
			case "n":
				move(Position.Direction.NORTH);
				break;
			case "south":
			case "s":
				move(Position.Direction.SOUTH);
				break;
			case "east":
			case "e":
				move(Position.Direction.EAST);
				break;
			case "west":
			case "w":
				move(Position.Direction.WEST);
				break;
			case "up":
			case "u":
				move(Position.Direction.UP);
				break;
			case "down":
			case "d":
				move(Position.Direction.DOWN);
				break;
			default:
				return false;
		}
		
		return true;
		
	}
}
