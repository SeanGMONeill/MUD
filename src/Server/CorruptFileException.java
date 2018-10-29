package Server;

@SuppressWarnings("serial")
public class CorruptFileException extends Exception {
	
	String message;
	public CorruptFileException(String message) {
		this.message = message;
	}
	
	public CorruptFileException() {};
	
	public String getMessage() {
		if(message != null) {
			return "Corrupt file " + message;
		}
		else {
			return "Corrupt file";
		}
	}
	
}
