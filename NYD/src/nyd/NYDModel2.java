package nyd;

import core.EpsilonGreedy;
import woutree.ODWUTree;

public class NYDModel2 {
	private HighwayWorld world;
	private TruckAgent agent;
	private ODWUTree learner2;

	int learn_iterations = 50000;
	int test_iterations = 5000;
	
	private HighwayGUI highway_gui;
	
	String k_steps = "1000";
	String min_fringe_depth = "1";
	String max_fringe_depth = "3";
	String max_order_observation = "5";
	String max_history = "1";
	String ks_probability = "0.0001";
	String actions_to_compare = "policy";
	String yes_no = "yes";
	String min_instance_4_ks_test = "4";
	String gamma = "0.9";
	String conduct = "heuristic";
	String saValueArg = "zeros";
	String file = "";

	public NYDModel2(){
		world = new HighwayWorld();
		agent = new TruckAgent(world);

		String args = "kSteps=" + k_steps + ",minFringe=" + min_fringe_depth +
				",maxFringe=" + max_fringe_depth + ",maxHistory=" + max_history +
				",minIs=" + min_instance_4_ks_test + ",KSprob=" + ks_probability +
				",KScompareActs=" + actions_to_compare + ",subPercepts=" + yes_no +
				",gamma=" + gamma + ",conduct=" + conduct + ",initSAvs=" + saValueArg + 
				",file=" + file + ",max_order_observation=" + max_order_observation;
				
		try{
			learner2 = new ODWUTree(agent, args);
		}catch(Exception e){
			System.out.println("learner error");
		}
	}
	
	public void start(){
		world.init(6, 17);
		agent.init();
		learner2.init(false);
		explorePart();
		learnPart();
	    testPart();
	}

	private void testPart() {
		agent.startRecord();

		for(int i = 0; i < test_iterations; i++){
			if(i == test_iterations - 100){
				highway_gui = new HighwayGUI(world);
				highway_gui.start();
				agent.startAnimation(highway_gui);
			}
			learner2.mainLoop2();
		}
		
		agent.showResult();
		System.out.println("test_step:"+test_iterations);
		learner2.showResult();
	}
	
	private void explorePart() {
		/*highway_gui = new HighwayGUI(world);
		highway_gui.start();
		agent.startAnimation(highway_gui);*/
		while(!learner2.canStopHeuristicExploration()){
			learner2.exploreLoop();
		}
		learner2.dump_observationRecord();
	}

	private void learnPart() {
		/*highway_gui = new HighwayGUI(world);
		highway_gui.start();
		agent.startAnimation(highway_gui);*/
		try{
			learner2.setActionSelection(new EpsilonGreedy(learner2, "e-greedy[1]"));
		}catch(Exception e){};
		for(int i = 0; i < learn_iterations; i++){
			if(i % 1000 == 0){
				System.out.println(i);
			}
			if(i % 10000 == 9000){
				learner2.clearObservationRecord();
			}
			
			if(i == 1999){
				try{
					learner2.setActionSelection(new EpsilonGreedy(learner2, "e-greedy[0.8]"));
				}catch(Exception e){};
			}
			if(i == 3999){
				try{
					learner2.setActionSelection(new EpsilonGreedy(learner2, "e-greedy[0.4]"));
				}catch(Exception e){};
			}
			if(i == 11999){
				try{
					learner2.setActionSelection(new EpsilonGreedy(learner2, "e-greedy[0.2]"));
				}catch(Exception e){};
			}
			if(i == 13999){
				try{
					learner2.setActionSelection(new EpsilonGreedy(learner2, "e-greedy[0.1]"));
				}catch(Exception e){};
			}
			learner2.mainLoop();
		}
		
		
		
		try{
			learner2.setActionSelection(new EpsilonGreedy(learner2, "e-greedy[0]"));
		}catch(Exception e){};
	}

	

}
