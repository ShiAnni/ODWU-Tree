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
 * Implements epsilon greedy action selection routine.
 *
 * Paul A. Crook 13th October 2003
 *
 * modified 29th September 2004 to follow policy and not assume policy = max_a
 * Q(s,a)
 *
 **/
public class EpsilonGreedy extends ActionSelection {

	private String epsilon;

	// constructor
	public EpsilonGreedy(ReallyBasicStateActionLearner actNlearn, String arguments) throws Exception {
		// set up pointer to act'n'learn method
		// (needed to get at values like action-learning
		// steps and runs completed)
		super(actNlearn);

		// get arguments
		int startArgs = arguments.indexOf('[') + 1;
		int endArgs = arguments.lastIndexOf(']');

		// if argument not given throw an exception
		if ((startArgs == 0) || (endArgs == -1)) {
			throw new Exception();
		} else {
			epsilon = arguments.substring(startArgs, endArgs);
		}

		// check epsilon formula is valid (throws exception)
		actNlearn.evalFormula(epsilon);
	}

	public String listActionSelectionArgs() {
		return "epsilon";
	}

	public String currentValueActionSelectionArgs() {
		return String.valueOf(actNlearn.evalFormula(epsilon));
	}

	// Select Action from Given List of actions.
	protected Action selectActionFromList(State s, Action[] list, boolean explore) {
		// select random action
		// System.out.print( "this " + this + ", ActNl " + actNlearn + ",
		// actions " +
		// actNlearn.actionSteps + ", runs " + actNlearn.runsCompleted +
		// ", epsilon " + actNlearn.evalFormula(epsilon) + ", random act ");
		if (explore && (CentralRandomGenerator.nextDouble() <= actNlearn.evalFormula(epsilon))) {
			// select action at random
			// System.out.println(1);
			return list[CentralRandomGenerator.nextInt(list.length)];
		} else {
			// follow policy action
			// System.out.println(0);
			return actNlearn.policyAction(s, list);
		}
	}
}
