package Server;

@SuppressWarnings("serial")
public class NoSuchPlayerException extends Exception{

	String message;
	public NoSuchPlayerException(String message) {
		this.message = message;
	}
	
	public NoSuchPlayerException() {};
	
	public String getMessage() {
		if(message != null) {
			return "No such player " + message;
		}
		else {
			return "No such player";
		}
	}
	
	
	
}
