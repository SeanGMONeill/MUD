package Server;

import Server.Position.Direction;

public class Player {

	
	private String name;
	
	private Server server;
	
	private Room currentRoom;
	
	
	public Player(String name, Room currentRoom, Server server) {
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
			server.messagePlayer(this, "You cannot move in this direction.");
		}
	}
	
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
}
