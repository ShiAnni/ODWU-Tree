package nyd;

import core.PhysicalAction;

public class TruckAction extends PhysicalAction {
	private String name;
	
	public TruckAction(String name){
		this.name = name;
	}
	
	public String toString(){
		return name;
	}
	
	public String actionName(){
		return name;
	}

	@Override
	public Object reference() {
		// TODO Auto-generated method stub
		return null;
	}

}
