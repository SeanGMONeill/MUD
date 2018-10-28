package Server;

import Server.Position.Direction;

public class Player {

	Map map;
	
	private String name;
	
	private Position position;
	
	
	public Player(String name, Position position, Map map) {
		this.name = name;
		this.position = position;
		this.map = map;
	}
	
	public boolean move(Direction direction) {
		
		Position newPos = position.copy();
		newPos.move(direction);
		
		if(map.isValidPosition(newPos)) {
			position = newPos;
			return true;
		}
		else {
			return false;
		}
		
	}
	
	public String getName() {
		return name;
	}
	
	public Position getPosition() {
		return position;
	}
}
