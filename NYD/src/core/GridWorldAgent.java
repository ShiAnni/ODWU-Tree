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
 * This is a abstract class for all the Grid World Agents
 * 
 * P.A. Crook 30th October 2002 (refactored 17 May 2005)
 *  
 */

abstract class GridWorldAgent extends Agent {

	protected World world; //a holder for the world the agent is in

	/** common methods for all grid world agents * */

	// constructor used by all GridWorld agents
	GridWorldAgent(World world) {
		this.world = world;
	}

	//get list of possible physical actions from world
	public PhysicalAction[] physicalActions() {
		return (world.physicalActions());
	}

	//execute physical actions in the world
	public Float executePhysicalAction(PhysicalAction act, State state) {
		return world.executeAction(act, state);
	}

	//send back a report on the state of the agent
	public Report report() {
		return (world.report()); //by default report what the world has to say
	}

	//get world -- returns a pointer to the world
	public World getWorld() {
		return world;
	}

	// Clone the agent (and the world it is in)
	public Object clone() {
		GridWorldAgent aClone = (GridWorldAgent) super.clone();
		aClone.world = (World) world.clone();
		return aClone;
	}

	//toString (say something about the agent and its world).
	//What this sends back will be okay but is a bit verbose for filenames,
	//I expect each agent to over write this method.
	public String toString() {
		return (getClass().getName() + " " + world);
	}

}

