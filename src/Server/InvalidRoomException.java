package Server;

@SuppressWarnings("serial")
public class InvalidRoomException extends Exception{
	
	String message;
	public InvalidRoomException(String message) {
		this.message = message;
	}
	
	public InvalidRoomException() {};
	
	public String getMessage() {
		if(message != null) {
			return "Invalid room " + message;
		}
		else {
			return "Invalid room";
		}
	}
	
}
