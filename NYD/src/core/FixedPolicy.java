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



import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 * Implements action selection that follows a given fixed policy.
 * 
 * Created on May 25, 2005 by Paul A. Crook
 *  
 */

public class FixedPolicy extends ActionSelection {

	private Hashtable fixedPolicyStateActionValues; // assumes policy defined by a
													// table of state-action values

	private String filename;

	FixedPolicy(ReallyBasicStateActionLearner actNlearn, String arguments)
			throws Exception {
		super(actNlearn);

		int startArgs = arguments.indexOf('[') + 1;
		int endArgs = arguments.lastIndexOf(']');

		//if no arguments given throw an exception
		if ((startArgs == 0) || (endArgs == -1)) {
			throw new Exception();
		} else {
			//strip off argument brackets
			arguments = arguments.substring(startArgs, endArgs);
			//get file name
			filename = Args.getArgument(arguments, "policy=", ":");
		}

		FileInputStream fis = new FileInputStream(filename);
		ObjectInputStream ois = new ObjectInputStream(fis);
		fixedPolicyStateActionValues = (Hashtable) ois.readObject();
		ois.close();
		fis.close();

	}

	protected Action selectActionFromList(State s, Action[] list,
			boolean explore) {

		Vector act = new Vector();
		Double actValue = null;

		for (Enumeration e = fixedPolicyStateActionValues.keys(); e
				.hasMoreElements();) {
			StateActionPair sa = (StateActionPair) e.nextElement();
			if (sa.state.equals(s)) {
				Double saValue = (Double) fixedPolicyStateActionValues.get(sa);
				if (actValue == null || saValue.compareTo(actValue) > 0) {
					// if > replace
					act.clear();
					act.addElement(sa.actionName);
					actValue = saValue;
				} else if (saValue.equals(actValue)) {
					// if == add to list
					act.addElement(sa.actionName);
				}
			}
		}

		// fail of no action found for this state
		if (actValue == null) {
			System.err
					.println("Fixed policy doesn't define an action for state "
							+ s + ".");
			System.exit(1);
		}

		//if multiple actions listed in 'act' warn that fixed policy is
		// stochastic
		if (act.size() > 1)
			System.err
					.println("Warning: Fixed policy acts stochastically for state "
							+ s + ".\nSelecting at random between actions " + act + ".");

		//select randomly between those listed (who have equal value)
		String policyActionName = (String) act.get(CentralRandomGenerator
				.nextInt(act.size()));

		Action policyAction = null;

		int i = 0;
		while ((i < list.length)
				&& (!policyActionName.equals(list[i].actionName()))) {
			i++;
		}
		if (i == list.length) {
			System.err.println("Problem with fixed policy; policy actionName "
					+ policyActionName
					+ " not found in available list of actions.");
			System.exit(1);
		} else {
			policyAction = list[i];
		}
		
		return policyAction;
	}

	// report internal argument names
	public String listActionSelectionArgs() {
		return "filename";
	}

	// report current value of internal arguments
	public String currentValueActionSelectionArgs() {
		return filename;
	}

}
