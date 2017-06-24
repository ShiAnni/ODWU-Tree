package core;
/**
 * 
 * Copyright 2006 Paul A. Crook
 *
 *
 * This file is part of the Reinforcement Learning - Java Test Platform 
 * (RL-JTP).
 *
 * RL-JTP is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * RL-JTP is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with RL-JTP; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 *
 **/



import java.io.*;

public class ManualControl extends ActionLearningProcess {

	//constructor
	public ManualControl(Agent agent, String args) {
		super(agent, args); // set up internal pointer to agent.
	}

	//main loop
	public void mainLoop() {
		clearDossier(); // clear dossier that reports actions
		actionSteps++; //increment action count
		State state = agent.getState();
		System.out.print("State observation: ");
		for (int i = 0; i < state.observations.length; i++) {
			System.out.print(state.observations[i] + " ");
		}
		System.out.println();
		System.out.println("State hash code: " + state.hashCode());
		System.out.println("State toString : " + state.toString() + "\n");
		// manual control of action
		int valueOfOne = (int) '1';
		Action[] actions = allAvailableActions(); //get current range of
												  // available actions
		System.out.println("Actions available:");
		if (actions != null) {
			for (int i = 0; i < actions.length; i++) {
				System.out.println("  " + ((char) (i + valueOfOne)) + " - "
						+ actions[i]);
			}
			BufferedReader input = new BufferedReader(new InputStreamReader(
					System.in));
			char key = ' ';
			do {
				System.out.print("> ");
				try {
					key = (char) input.read();
				} catch (IOException e) {
					System.out.println(e);
				}
			} while (key < '1' || key >= (char) (actions.length + valueOfOne));
			int act = (int) key - valueOfOne;
			Float reward = executeAction(actions[act], state);
			System.out.println("Action executed, reward of " + reward
					+ " received.");
		} else
			System.out.println("none.");
	}

	//no arguments
	public String listActnLearnArguments() {
		return new String();
	}

	public String currentValueActnLearnArgs() {
		return new String();
	}

}

