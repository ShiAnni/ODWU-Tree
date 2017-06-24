package core;

import nyd.TruckAction;

public class HeuristicExplore extends ActionSelection {
	private static final int LINE = 4;
	private int currentPosition = 1;
	private int currentPerception = 0;
	private String[] lookAroundRight = {
			"GAZE_BACKWARD_LEFT",
			"GAZE_BACKWARD_CENTER",
			"GAZE_BACKWARD_RIGHT"
	};
	private String[] lookAroundLeft = {
			"GAZE_FORWARD_LEFT",
			"GAZE_FORWARD_CENTER",
			"GAZE_FORWARD_RIGHT"
	};
	private String currentDirection = "right";
	HeuristicExplore(ReallyBasicStateActionLearner actNlearn) {
		super(actNlearn);
	}
//	private String[] action_set = 
//		{
//				"GAZE_FORWARD_LEFT",				"GAZE_FORWARD_CENTER",
//				"GAZE_FORWARD_RIGHT",				"GAZE_BACKWARD_LEFT",
//				"GAZE_BACKWARD_RIGHT",				"GAZE_BACKWARD_CENTER",
//				"TURN_LEFT",						"TURN_RIGHT"
//		};
	@Override
	protected Action selectActionFromList(State s, Action[] list, boolean explore) {
		/*System.out.println("********print explore action********");
		for (Object ob : s.observations) {
			System.out.print(ob+" ");
		}
		System.out.println("\n************************************");*/
		if(currentPosition<LINE&&currentDirection.equals("right")){
			++currentPosition;
			return list[7];
		}
		if(currentPosition>1&&currentDirection.equals("left")){
			--currentPosition;
			return list[6];
		}
		if(currentPosition==1&&currentPerception<lookAroundRight.length){
			Action action = new TruckAction(lookAroundRight[currentPerception]);
			++currentPerception;
			if(currentPerception==lookAroundRight.length){
				currentPerception=0;
				currentDirection="right";
			}
			return action;
		}
		if(currentPosition==LINE&&currentPerception<lookAroundLeft.length){
			Action action = new TruckAction(lookAroundLeft[currentPerception]);
			++currentPerception;
			if(currentPerception==lookAroundLeft.length){
				currentPerception=0;
				currentDirection="left";
			}
			return action;
		}
		//should not excute here
		Action action = list[1];
		return action;
	}

	@Override
	public String listActionSelectionArgs() {
		return "nullArgs";
	}

	@Override
	public String currentValueActionSelectionArgs() {
		return null;
	}

}
