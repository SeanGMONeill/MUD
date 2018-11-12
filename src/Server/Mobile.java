package Server;

import java.util.List;

public class Mobile extends Entity{

	int currentStep;
	Room defaultRoom;
	List<String> routine;
	int turnsToWait = 0;
	
	
	public Mobile(String name, Room currentRoom, Room defaultRoom, int currentStep, List<String> routine, Server server) {
		super(name, currentRoom, server);
		this.currentStep = currentStep;
		this.defaultRoom = defaultRoom;
		this.routine = routine;
	}

	@Override
	public void message(String message) {
		//Do nothing for now
	}
	
	@Override
	public boolean handleCommand(String input) {
		String[] splitInput = input.split(" ", 2);
		String command;
		String param = ""; 
		
		command = splitInput[0].toLowerCase();
		if(splitInput.length>1) {
			param = splitInput[1];
		}
		
		switch(command) {
			case "wait":
				wait(param);
				break;
			default:
				return super.handleCommand(input);
		}
		
		return true;
	}
	
	public void doTick() {
		if(turnsToWait > 0) {
			turnsToWait--;
		}
		else {
			if(routine.size()>0) {
				handleCommand(routine.get(currentStep));
				currentStep++;
				if(currentStep >= routine.size()) {
					currentStep = 0;
				}
			}
		}
	}
	
	private void wait(String param) {
		if(param != "") {
			try {
				turnsToWait = Integer.parseInt(param)-1;
			}
			catch(NumberFormatException e) {
				
			}
		}
		//Otherwise we're just waiting this 1 tick
	}
}
