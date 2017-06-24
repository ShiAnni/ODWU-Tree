package nyd;

import java.util.Hashtable;

import core.Action;
import core.Agent;
import core.PerceptualAction;
import core.PhysicalAction;
import core.Report;
import core.State;

public class TruckAgent extends Agent {
	private HighwayWorld world;
	
	private HighwayGUI highway_gui;
	
	private int hear_horn = 0;
	private int crash = 0;
	private boolean RECORD = false;
	private boolean ANIMATION = false;
	
	

	public TruckAgent(HighwayWorld world){
		this.world = world;
		init();
	}
	
	public void init() {
	}
	
	

	public State getState() {
		String[] observation = world.observation();
		
		return new State(observation);
	}
	
	public boolean observationDimensionsFixed(){
		return true;
	}
	
	public Integer numberOfObservationDimensions() {
		return new Integer(8);
	}

	@Override
	public PhysicalAction[] physicalActions() {
		return world.physicalActions();
	}
	@Override
	public Action[] allActions() {
		return world.allActions();
	}
	public Hashtable getAllPerceptAndAction(){
		return world.getAllPerceptAndAction();
	}
	
	public String[] getAllPerceptAndActionKeys(){
		return world.getAllPerceptAndActionKeys();
	}

	@Override
	public PerceptualAction[] perceptualActions() {
		return null;
	}

	@Override
	public Float executePhysicalAction(PhysicalAction act, State state) {
		Float reward = world.executeAction(act, state);
		
		if(RECORD){
			if(reward.floatValue() <= -4.0)
				crash++;
			else if(reward.floatValue() <= 0)
				hear_horn++;
		}
		
		if(ANIMATION){
			highway_gui.update();
		}
		
		return reward;
	}
	
	public void startRecord(){
		RECORD = true;
		crash = 0;
		hear_horn = 0;
	}
	
	public void showResult(){
		System.out.println("total crash num: " + crash);
		System.out.println("total hear horn num: " + hear_horn);
	}
	
	public void startAnimation(HighwayGUI highway){
		this.highway_gui = highway;
		ANIMATION = true;
	}

	@Override
	public Float executePerceptualAction(PerceptualAction act, State state) {
		return null;
	}

	@Override
	public Float costPerceptualAction(PerceptualAction perceptualAction) {
		return null;
	}

	@Override
	public Report report() {
		return world.report();
	}
	
	public Hashtable getPerceptAndActionIndex(){
		return world.getAllPerceptAndActionIndex();
	}

	@Override
	public String toString() {
		return null;
	}

}
