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


import java.io.File;
import java.io.FileOutputStream;

public class RandomAction extends ActionLearningProcess {

	// constructor
	public RandomAction(Agent agent, String args) {
		super(agent, args); // set up internal pointer to agent.
	}

	// main loop
	public void mainLoop() { // pick from range of actions at random
		clearDossier(); // clear dossier that reports actions
		actionSteps++; // increment action count
		State state = agent.getState(); // don't need this but gets reported
		Action[] actions = allAvailableActions(); // get current range of
													// available actions
		executeAction(actions[CentralRandomGenerator.nextInt(actions.length)],
				state);
	}

	// evaluate - same as mainLoop but don't increment actionSteps
	public void evaluate() {
		clearDossier(); // clear dossier that reports actions
		State state = agent.getState(); // don't need this but gets reported
		Action[] actions = allAvailableActions(); // get current range of
													// available actions
		executeAction(actions[CentralRandomGenerator.nextInt(actions.length)],
				state);
	}

	// no arguments
	public String listActnLearnArguments() {
		return new String();
	}

	public String currentValueActnLearnArgs() {
		return new String();
	}

	// overwritten method to save the knowledge learnt by agent.
	// RandomAction doesn't really learn anything but sometimes its useful to
	// write out an empty file where the filename contains information
	public void saveKnowledge(String filePrefix, String filePostfix,
			Integer maxTrainingSteps) {
		try {
			File file = new File( filePrefix + "Random " + agent +
					                " runs" + runsCompleted +
									" actions" + actionSteps +
									( (maxTrainingSteps != null) ? 
											(" aRun<=" + maxTrainingSteps)
											: "" ) + filePostfix );
			if (file.exists()) {
				file.delete();
			}

			FileOutputStream fos = new FileOutputStream(file);
			fos.close();
		} catch (Exception e) {
			System.err.println("Problem writing out knowledge file:" + e);
			System.exit(1);
		}
	}

}
