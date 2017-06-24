package nyd;

import core.EpsilonGreedy;
import utree.UTree;

public class NYDModel {
	private HighwayWorld world;
	private TruckAgent agent;
	private UTree learner;

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
	String conduct = "e-greedy[1]";
	String saValueArg = "zeros";
	String file = "";

	public NYDModel(){
		world = new HighwayWorld();
		agent = new TruckAgent(world);

		String args = "kSteps=" + k_steps + ",minFringe=" + min_fringe_depth +
				",maxFringe=" + max_fringe_depth + ",maxHistory=" + max_history +
				",minIs=" + min_instance_4_ks_test + ",KSprob=" + ks_probability +
				",KScompareActs=" + actions_to_compare + ",subPercepts=" + yes_no +
				",gamma=" + gamma + ",conduct=" + conduct + ",initSAvs=" + saValueArg + 
				",file=" + file + ",max_order_observation=" + max_order_observation;
				
		try{
			learner = new UTree(agent, args);
		}catch(Exception e){
			System.out.println("learner error");
		}
	}
	
	public void start(){
		world.init(6, 17);
		agent.init();
		learner.init(false);
		learnPart();
	    testPart();
	}

	private void testPart() {
		agent.startRecord();

		for(int i = 0; i < test_iterations; i++){
			/*if(i == test_iterations - 100){
				highway_gui = new HighwayGUI(world);
				highway_gui.start();
				agent.startAnimation(highway_gui);
			}*/
			learner.mainLoop2();
		}
		
		agent.showResult();
		System.out.println("test_step:"+test_iterations);
		learner.showResult();
	}
	

	private void learnPart() {
		for(int i = 0; i < learn_iterations; i++){
			if(i % 1000 == 0){
				System.out.println(i);
			}
			
			if(i == 1999){
				try{
					learner.setActionSelection(new EpsilonGreedy(learner, "e-greedy[0.8]"));
				}catch(Exception e){};
			}
			if(i == 3999){
				try{
					learner.setActionSelection(new EpsilonGreedy(learner, "e-greedy[0.4]"));
				}catch(Exception e){};
			}
			if(i == 11999){
				try{
					learner.setActionSelection(new EpsilonGreedy(learner, "e-greedy[0.2]"));
				}catch(Exception e){};
			}
			if(i == 13999){
				try{
					learner.setActionSelection(new EpsilonGreedy(learner, "e-greedy[0.1]"));
				}catch(Exception e){};
			}
			learner.mainLoop();
		}
		
		
		
		try{
			learner.setActionSelection(new EpsilonGreedy(learner, "e-greedy[0]"));
		}catch(Exception e){};
	}

	

}
