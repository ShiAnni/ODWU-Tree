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


/**
 * 
 * Top level class for both GridWorld and Webots agents
 * 
 * Created on May 17, 2005 by Paul A. Crook
 *  
 */

abstract public class Agent implements Cloneable {

	public Agent() {
		super();
	}

	// init - for variables that need to be initiated
	//        i.e. perceptual action flag in oracle agents
	abstract public void init();

	// return current state (i.e. current observation)
	abstract public State getState();

	// is the state (observation) composed of a fix number of elements.
	// this method and numberOfObservationDimensions() are used soley
	// by UTree to determine if its safe to sub-divide observations
	public boolean observationDimensionsFixed() {
		return false; // as a default assume observations can vary in number of elements.
	}
	
	// number of observation dimensions if fixed
	public Integer numberOfObservationDimensions() {
		return null; // see observationDimensionsFixed() above
	}
	
	// list physical actions available
	abstract public PhysicalAction[] physicalActions();

	// list perceptual actions available
	abstract public PerceptualAction[] perceptualActions();

	//return a list of _all_ the possible actions
	abstract public Action[] allActions();
	// execute physical action
	abstract public Float executePhysicalAction(PhysicalAction act, State state);

	// execute perceptual action
	abstract public Float executePerceptualAction(PerceptualAction act,
			State state);

	// cost a perceptual action
	abstract public Float costPerceptualAction(PerceptualAction perceptualAction);

	//execute an action - don't matter what type it is, this method will
	//figure that out
	public Float executeAction(Action act, State state) {
		Float reward = new Float(0.0);
		if (act instanceof PhysicalAction)
			reward = executePhysicalAction((PhysicalAction) act, state);
		else if (act instanceof PerceptualAction)
			reward = executePerceptualAction((PerceptualAction) act, state);
		else {
			System.err.println(act
					+ " is neither a Perceptual or Physical Action!");
			System.exit(1); //crash the process - something is very wrong
		}
		return reward;
	}

	//	send back a report on the state of the agent
	abstract public Report report();

	// Clone the agent
	public Object clone() {
		try {
			Agent aClone = (Agent) super.clone();
			return aClone;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

	//toString; name the agent _and_ its world.
	abstract public String toString();

}
