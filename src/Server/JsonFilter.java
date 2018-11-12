package Server;

import java.io.File;
import java.io.FilenameFilter;

public class JsonFilter implements FilenameFilter{

	@Override
	public boolean accept(File dir, String name) {
		if(name.endsWith(".json")) {
			return true;
		}
		else {
			return false;
		}
	}

}
