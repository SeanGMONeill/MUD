package Server;

import java.util.List;

public class Mobile extends Entity{

	int currentStep;
	Room defaultRoom;
	List<String> routine;
	
	
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
		
		command = splitInput[0].toLowerCase();
		
		switch(command) {
			case "wait":
				//do nothing this tick
				break;
			default:
				return super.handleCommand(input);
		}
		
		return true;
	}
	
	public void doTick() {
		handleCommand(routine.get(currentStep));
		currentStep++;
		if(currentStep >= routine.size()) {
			currentStep = 0;
		}
	}
}
