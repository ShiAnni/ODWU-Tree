package nyd;

import java.util.*;
import core.*;

public class HighwayWorld extends World{
	private int num_lanes, num_positions;
	public int agent_lane, agent_position;
	public Truck[][] grid;
	private boolean last_action_illegal;
	
	private int max_truck_speed = 1;
	private int min_truck_speed = -1;
	private double new_truck_probability = 0.5;
	public double fast_truck_probability = 0.4; 
	public int gaze_lane;
	public int gaze_position;
	
	private double CRASH_REWARD = -5.0;
	private double HORN_REWARD = -1.0;
	private double NO_CRASH_REWARD = 0.1;
	
	
	private String[] action_set = 
		{
				"GAZE_FORWARD_LEFT",				"GAZE_FORWARD_CENTER",
				"GAZE_FORWARD_RIGHT",				"GAZE_BACKWARD_LEFT",
				"GAZE_BACKWARD_RIGHT",				"GAZE_BACKWARD_CENTER",
				"TURN_LEFT",						"TURN_RIGHT"
		};
	private String[] perception_dimensions = 
		{
				"HEAR HORN",				"GAZE_OBJECT",
				"GAZE_SIDE",				"GAZE_DIRECTION",
				"GAZE_SPEED",				"GAZE_DISTANCE",
				"GAZE_REFINED_DISTANCE",	"GAZE_COLOR",
		};
	private String[] hear_horn = 
		{
				"YES",				"NO"
		};
	private String[] gaze_object = 
		{
				"ROAD",				"TRUCK",				"SHOULDER"
		};
	private String[] gaze_side = 
		{
				"LEFT",				"CENTER",				"RIGHT"
		};
	private String[] gaze_direction = 
		{
				"FRONT",				"BACK"
		};
	private String[] gaze_speed = 
		{
				"LOOMING",				"RECEDING"
		};
	private String[] gaze_distance = 
		{
				"FAR",				"NEAR",				"NOSE"
		};
	private String[] gaze_refined_distance = 
		{
				"FARHALF",				"CLOSEHALF"
		};
	private String[] gaze_color = 
		{
				"RED",				"BLUE",				"YELLOW",
				"WHITE",				"GRAY",				"TAN"
		};

	public Hashtable getAllPerceptAndAction(){
		Hashtable all_percept_action = new Hashtable();
		
		all_percept_action.put("ACTION", action_set);
		all_percept_action.put(perception_dimensions[0], hear_horn);
		all_percept_action.put(perception_dimensions[1], gaze_object);
		all_percept_action.put(perception_dimensions[2], gaze_side);
		all_percept_action.put(perception_dimensions[3], gaze_direction);
		all_percept_action.put(perception_dimensions[4], gaze_speed);
		all_percept_action.put(perception_dimensions[5], gaze_distance);
		all_percept_action.put(perception_dimensions[6], gaze_refined_distance);
		all_percept_action.put(perception_dimensions[7], gaze_color);
		
		return all_percept_action;
	}
	
	public Hashtable getAllPerceptAndActionIndex(){
		Hashtable action_and_perspectual_index = new Hashtable();
		
		for (String percept : action_set) {
			action_and_perspectual_index.put(percept, 0);
		}
		for (String percept : hear_horn) {
			action_and_perspectual_index.put(percept, 1);
		}
		for (String percept : gaze_object) {
			action_and_perspectual_index.put(percept, 2);
		}
		for (String percept : gaze_side) {
			action_and_perspectual_index.put(percept, 3);
		}
		for (String percept : gaze_direction) {
			action_and_perspectual_index.put(percept, 4);
		}
		for (String percept : gaze_speed) {
			action_and_perspectual_index.put(percept, 5);
		}
		for (String percept : gaze_distance) {
			action_and_perspectual_index.put(percept, 6);
		}
		for (String percept : gaze_refined_distance) {
			action_and_perspectual_index.put(percept, 7);
		}
		for (String percept : gaze_color) {
			action_and_perspectual_index.put(percept, 8);
		}
		
		return action_and_perspectual_index;
	}
	
	public String[] getAllPerceptAndActionKeys(){
		String[] keys = new String[perception_dimensions.length + 1];
		keys[0] = "ACTION";
		for(int i = 0; i < perception_dimensions.length; i++){
			keys[i+1] = perception_dimensions[i];
		}
		
		return keys;
	}
	
	

	public HighwayWorld(){}
	
	public void init(int num_lanes, int num_positions){
		this.num_lanes = num_lanes;
		this.num_positions = num_positions;
		agent_lane = 1;
		agent_position = num_positions / 2;
		grid = new Truck[num_lanes][num_positions];
		for(int i = 0; i < num_lanes; i++)
			for(int j = 0; j < num_positions; j++)
				grid[i][j] = null;
		last_action_illegal = false;
		gaze_lane = 0;
		gaze_position = 0;
	}
	
	private void update(){
		update_trucks();
		add_trucks();
	}
	
	private void add_trucks(){
		Random r = new Random();
		if(r.nextDouble() <= new_truck_probability){
			
			int lane = 0;
			int position = 0;
			int speed = min_truck_speed;
			Truck t;
			
			if(r.nextDouble() <= fast_truck_probability)
				speed = max_truck_speed;
			
			if(speed < 0)
				position = num_positions - 1;
			
			while(true){
				int laner = r.nextInt(10);
				
				if(speed == -1){
					switch(laner){
					case 0: case 1: case 2: case 6:
						lane = 4;
						break;
					case 4: case 5: case 7:
						lane = 3;
						break;
					case 8: case 3:
						lane = 2;
						break;
					case 9:
						lane = 1;
						break;
					}
				}else{
					switch(laner){
					case 0: case 1: case 2: case 7:
						lane = 1;
						break;
					case 4: case 5: case 6:
						lane = 2;
						break;
					case 8: case 3:
						lane = 3;
						break;
					case 9:
						lane = 4;
						break;
					}
				}
				
				if(grid[lane][position] != null)
					continue;
				
				String color = gaze_color[r.nextInt(gaze_color.length)];
				t = new Truck(speed, color);
				grid[lane][position] = t;
				
				return;
			}
		}
	}
	
	private void update_trucks(){
		int speed;
		
		speed = +1;
		for(int j = num_positions - 1; j >= 0; j--){
			for(int i = 0; i < num_lanes; i++){

				Truck t = grid[i][j];
				if(t == null || t.speed != speed)
					continue;
				
				int lane = i;
				int position = j;
				grid[i][j] = null;
				position += speed;
				
				if(position < num_positions && position >= 0){
					if(grid[lane][position] != null){
						if(grid[lane][position].speed == -1)
							t.speed = -1;
						grid[i][j] = t;
					}else if(lane == agent_lane
							&& position == agent_position){
						grid[i][j] = t;
					}else if(position + 1 < num_positions
							&& grid[lane][position+1] != null
							&& grid[lane][position+1].speed == -1){
						t.speed = -1;
						grid[lane][position] = t;
					}else{
						grid[lane][position] = t;
					}
				}
			}
		}
		
		speed = -1;
		for(int j = 0; j < num_positions; j++){
			for(int i = 0; i < num_lanes; i++){
				Truck t = grid[i][j];
				if(t == null || t.speed != speed)
					continue;
				
				int lane = i;
				int position = j;
				grid[i][j] = null;
				position += speed;
				
				if(position < num_positions && position >= 0){
					grid[lane][position] = t;
				}
			}
		}
	}
	
	public double reward(){
		double ret;
		
		if(last_action_illegal)
			ret = CRASH_REWARD;
		else if(horn_is_blowing())
			ret = HORN_REWARD;
		else
			ret = NO_CRASH_REWARD;
		
		return ret;
	}
	
	public void perform_action(String action){
		int forward_shoulder_gaze_position = num_positions - 1;
		int backward_shoulder_gaze_position = 0;

		update();
		
		switch(action){
		case("GAZE_FORWARD_CENTER"):
			gaze_forward_center();
			agent_straight_action();
			break;
		case("GAZE_FORWARD_RIGHT"):
			gaze_lane = agent_lane + 1;
			if(gaze_lane >= num_lanes - 1)
				gaze_position = forward_shoulder_gaze_position;
			else
				gaze_position = position_of_next_truck(gaze_lane, agent_position + 1);
			agent_straight_action();
			break;
		case("GAZE_FORWARD_LEFT"):
			gaze_lane = agent_lane - 1;
			if(gaze_lane <= 0)
				gaze_position = forward_shoulder_gaze_position;
			else
				gaze_position = position_of_next_truck(gaze_lane, agent_position + 1);
			agent_straight_action();
			break;
		case("GAZE_BACKWARD_CENTER"):
			gaze_lane = agent_lane;
			gaze_position = position_of_prev_truck(gaze_lane, agent_position - 1);
			agent_straight_action();
			break;
		case("GAZE_BACKWARD_RIGHT"):
			gaze_lane = agent_lane + 1;
			if(gaze_lane >= num_lanes - 1)
				gaze_position = backward_shoulder_gaze_position;
			else
				gaze_position = position_of_prev_truck(gaze_lane, agent_position - 1);
			agent_straight_action();
			break;
		case("GAZE_BACKWARD_LEFT"):
			gaze_lane = agent_lane - 1;
			if(gaze_lane <= 0)
				gaze_position = backward_shoulder_gaze_position;
			else
				gaze_position = position_of_prev_truck(gaze_lane, agent_position - 1);
			agent_straight_action();
			break;
		case("TURN_LEFT"):
			agent_left_action();
			gaze_forward_center();
			break;
		case("TURN_RIGHT"):
			agent_right_action();
			gaze_forward_center();
			break;
		default:
			error("unknown action: " + action);
		}
	}
	
	public String[] get_percept_vector(){
		String[] perception = new String[8];
		

		perception[0] = get_hear_hore();
		perception[1] = get_gaze_object();
		perception[2] = get_gaze_side();
		perception[3] = get_gaze_direction();
		perception[4] = get_gaze_speed();
		perception[5] = get_gaze_distance();
		perception[6] = get_gaze_refined_distance();
		perception[7] = get_gaze_color();
		return perception;
	}
	
	private String get_hear_hore(){
		if(horn_is_blowing())
			return hear_horn[0];
		else
			return hear_horn[1];
	}
	
	private String get_gaze_object(){
		if(gaze_lane <= 0 || gaze_lane >= num_lanes - 1)
			return gaze_object[2];
		else if(grid[gaze_lane][gaze_position] != null)
			return gaze_object[1];
		else
			return gaze_object[0];
	}
	
	private String get_gaze_side(){
		return gaze_side[1 + gaze_lane - agent_lane];
	}
	
	private String get_gaze_direction(){
		if(gaze_position > agent_position)
			return gaze_direction[0];
		else
			return gaze_direction[1];
	}
	
	private String get_gaze_speed(){
		int speed = 0;
		if(grid[gaze_lane][gaze_position] != null){
			Truck t = grid[gaze_lane][gaze_position];
			if(t.speed < 0)
				speed = 0;
			else
				speed = 1;
		}else{
				speed = 0;
		}

		if(gaze_position < agent_position)
			speed = (speed == 0) ? 1 : 0;

		return gaze_speed[speed];
	}
	
	private String get_gaze_distance(){
		int distance = Math.abs(gaze_position - agent_position);
		
		if(distance <= 2)
			return gaze_distance[2];
		else if(distance <= 4)
			return gaze_distance[1];
		else
			return gaze_distance[0];
	}
	
	private String get_gaze_refined_distance(){
		int distance = Math.abs(gaze_position - agent_position);
		
		if(distance < 6){
			if(distance % 2 == 0){
				return gaze_refined_distance[0];
			}else{
				return gaze_refined_distance[1];
			}
		}else{
			return gaze_refined_distance[0];
		}
	}
	
	private String get_gaze_color(){
		if(grid[gaze_lane][gaze_position] != null){
			return grid[gaze_lane][gaze_position].color;
		}
		else{
			Random r = new Random();
			return gaze_color[2 + r.nextInt(4)];
		}
	}
	
	private void agent_straight_action(){
		last_action_illegal = false;
		if(grid[agent_lane][agent_position] != null)
			last_action_illegal = true;
	}
	
	private void agent_right_action(){
		last_action_illegal = false;
		if(agent_lane >= num_lanes - 2
				|| grid[agent_lane+1][agent_position] != null)
			last_action_illegal = true;
		else
			agent_lane++;
	}
	
	private void agent_left_action(){
		last_action_illegal = false;
		if(agent_lane <= 1
				|| grid[agent_lane-1][agent_position] != null)
			last_action_illegal = true;
		else
			agent_lane--;
	}
	
	private void gaze_forward_center(){
		gaze_lane = agent_lane;
		
		gaze_position = position_of_next_truck(gaze_lane, agent_position + 1);
	}
	
	private int position_of_next_truck(int lane, int position){
		for(int p = position; p < num_positions; p++)
			if(grid[lane][p] != null)
				return p;
		
		return num_positions - 1;
	}
	
	private int position_of_prev_truck(int lane, int position){
		for(int p = position; p >= 0; p--)
			if(grid[lane][p] != null)
				return p;
		
		return 0;
	}
	
	private boolean horn_is_blowing(){
		Truck t = grid[agent_lane][agent_position-1];
		
		return t != null && t.speed > 0;
	}
	
	private void error(String msg){
		System.out.println("Error: "+msg);
		System.exit(0);
	}
	
	public Float executeAction(PhysicalAction act, State state){
		String action = act.actionName();
		
		perform_action(action);
		
		return new Float(reward());		
	}
	
	public PhysicalAction[] physicalActions(){
		PhysicalAction[] phy_action = new PhysicalAction[]{new TruckAction("TURN_LEFT"),new TruckAction("TURN_RIGHT")};
		return phy_action;
	}
	
	public PhysicalAction[] allActions(){
		PhysicalAction[] phy_action = new PhysicalAction[action_set.length];
		
		for(int i = 0; i < action_set.length; i++){
			phy_action[i] = new TruckAction(action_set[i]);
		}
		
		return phy_action;
	}
	
	public String[] observation(){
		return get_percept_vector();
	}

	public Report report(){
		return null;
	}
	
	public void init(){
		init(6, 17);
	}
}













