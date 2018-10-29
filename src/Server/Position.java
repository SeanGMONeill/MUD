package Server;


public class Position {
	
	public static enum Direction { 
		
		NORTH("North"), SOUTH("South"), EAST("East"), WEST("West"), UP("up"), DOWN("down"); 
		
		String name;
		
		Direction(String name){
			this.name = name;
		}
		
		public String toString() {
			return name;
		}
	
	}
	
}
