package Server;

@SuppressWarnings("serial")
public class NoSuchMobileException extends Exception{

	String message;
	public NoSuchMobileException(String message) {
		this.message = message;
	}
	
	public NoSuchMobileException() {};
	
	public String getMessage() {
		if(message != null) {
			return "No such player " + message;
		}
		else {
			return "No such player";
		}
	}
	
	
	
}
