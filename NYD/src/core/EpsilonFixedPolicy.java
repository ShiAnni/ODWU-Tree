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
 * Follow given fixed policy most of the time but allow some exploration.
 * 
 * Created on May 26, 2005 by Paul A. Crook
 *  
 */

public class EpsilonFixedPolicy extends FixedPolicy {

	private String epsilon;

	EpsilonFixedPolicy(ReallyBasicStateActionLearner actNlearn, String arguments)
			throws Exception {
		super(actNlearn, arguments);

		int startArgs = arguments.indexOf('[') + 1;
		int endArgs = arguments.lastIndexOf(']');

		//strip off argument brackets
		arguments = arguments.substring(startArgs, endArgs);
		
		//get epsilon value
		epsilon = Args.getArgument(arguments, "epsilon=", ":");

		//check epsilon formula is valid (throws exception)
		actNlearn.evalFormula(epsilon);
	}

	protected Action selectActionFromList(State s, Action[] list,
			boolean explore) {

		if (explore
				&& (CentralRandomGenerator.nextDouble() <= 
					actNlearn.evalFormula(epsilon))) {
			// select action at random
			return list[CentralRandomGenerator.nextInt(list.length)];
		} else {
			// follow fixed policy
			return super.selectActionFromList(s, list, explore);
		}
	}

	// report internal argument names
	public String listActionSelectionArgs() {
		return super.listActionSelectionArgs() + "epsilon";
	}

	// report current value of internal arguments
	public String currentValueActionSelectionArgs() {
		return super.currentValueActionSelectionArgs() + "\t"
				+ String.valueOf(actNlearn.evalFormula(epsilon));
	}

}
