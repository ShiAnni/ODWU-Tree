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
 * Holder for state - action - reward triplets 
 *    
 * Paul A. Crook
 * Created on Sep 23, 2004
 *
 **/

public class StateActionRewardTriplet {

	private State state;
	private Action action;
	private Float reward;

	StateActionRewardTriplet(State s, Action a, Float r) {
		state = s;
		action = a;
		reward = r;
	}
	
	public State state() {
		return state;
	}
	
	public Action action() {
		return action;
	}
	
	public Float reward() {
		return reward;
	}
	
	public StateActionPair stateActionPair() {
		return new StateActionPair(state,action);
	}
	
	//toString - print something nice
	public String toString() {
		return ("StateActionRewardTriplet(" + state + "," + action +"," + reward + ")");
	}
}
