package Server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MobManager implements Runnable{

	Server server;
	
	private List<Mobile> mobs;
	
	public MobManager(Server server) {
		this.server = server;
		mobs = new ArrayList<Mobile>();
		
	}
	
	public void run() {
		while(server.serverOnline()) {
			handleTick();
			try {
				Thread.sleep(5000);
			}
			catch(InterruptedException e) {
				
			}
		}
	}
	
	private void handleTick() {
		for(Mobile mob : mobs) {
			mob.doTick();
		}
	}
	
	public void loadMobsFromFolder(File folder) {
		for(File file : folder.listFiles(new JsonFilter())) {
			try {
				mobs.add(MobLoader.loadMob(file, server));
			} catch (CorruptFileException e) {
				Server.logError(e);
			} catch (NoSuchMobileException e) {
				Server.logError(e);
			}
		}
	}
	
}

