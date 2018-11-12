package Server;

public class Player extends Entity {


	public Player(String name, Room currentRoom, Server server) {
		super(name, currentRoom, server);
	}

	
	public void message(String message) {
		server.messagePlayer(this, message);
	}


	void handleCommand(ClientConnection clientConnection, String input) {
		String[] splitInput = input.split(" ", 2);
		String command;
		
		command = splitInput[0].toLowerCase();
		
		switch(command) {
			case "look":
			case "l":
				clientConnection.server.messagePlayer(this, getRoom().toLongString());
				break;
			case "exit":
				clientConnection.exitPlayer("Thanks for playing!");
				break;
			default:
				if(!handleCommand(input)) { //If the super method also doesn't match a [generic] command
					clientConnection.out.println("No such command.");
				}
		}
		
	}

}
