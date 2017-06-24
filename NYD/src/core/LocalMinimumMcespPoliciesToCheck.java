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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * 
 * Generate set of files that contain policies which differ by one possible
 * action from the policy file given to this processes
 * 
 * Policy defined by policy hashtable and not by state-action values.
 * State-action values are *not* updated in generating alternative policies.
 * 
 * Created on Nov 27, 2004 by Paul A. Crook
 *  
 */

public class LocalMinimumMcespPoliciesToCheck {

	static int policiesGenerated;

	private static String usageString() {
		return ("\nusage: java LocalMinimumMcespPoliciesToCheck FILENAME OUTPUT_DIR\n");
	}

	public static void main(String[] args) {

		if (args.length != 2) {
			System.err.println(usageString());
			System.exit(1);
		}

		Hashtable stateActionValues = null;
		Hashtable policy = null;
		int actionSteps = 0;
		int runsCompleted = 0;
		Hashtable countSAupdates = null;
		ArithmeticInteger countPolicyUpdates = null;

		policiesGenerated = 0;

		try {
			FileInputStream fis = new FileInputStream(args[args.length - 2]);
			ObjectInputStream ois = new ObjectInputStream(fis);

			stateActionValues = (Hashtable) ois.readObject();
			actionSteps = ois.readInt();
			runsCompleted = ois.readInt();
			policy = (Hashtable) ois.readObject();
			countSAupdates = (Hashtable) ois.readObject();
			countPolicyUpdates = (ArithmeticInteger) ois.readObject();
					
			ois.close();
			fis.close();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}


		//get list of states from policy
		Vector states = new Vector(policy.keySet());
		
		// get complete list actions from state-action table
		Vector actions = new Vector();
		for (Enumeration en = stateActionValues.keys(); en.hasMoreElements();) {
			StateActionPair sa = (StateActionPair) en.nextElement();

			if (!actions.contains(sa.actionName)) {
				actions.add(sa.actionName);
			}
		}

		Collections.sort(states);
		Collections.sort(actions);

		// loop over all state and actions
		// output new policies when they are generated
		for (Iterator i = states.iterator(); i.hasNext();) {
			State s = (State) i.next();

			for (Iterator j = actions.iterator(); j.hasNext();) {
				String a = (String) j.next(); // action name
				
				if ( !policy.get(s).equals(a) ) {
					Hashtable newPolicy = (Hashtable) policy.clone();
					newPolicy.put(s,a);

					try {
						File file = new File(args[args.length - 1]
								+ "/PolicyMinimumCheck-promoted-" + s + "-" + a);
						if (file.exists()) {
							file.delete();
						}

						FileOutputStream fos = new FileOutputStream(file);
						ObjectOutputStream oos = new ObjectOutputStream(fos);
						oos.writeObject(stateActionValues);
						oos.writeInt(actionSteps);
						oos.writeInt(runsCompleted);
						oos.writeObject(newPolicy);
						oos.writeObject(countSAupdates);
						oos.writeObject(countPolicyUpdates);
						oos.flush();
						oos.close();
						fos.close();
						policiesGenerated++;
					} catch (Exception e) {
						System.err
								.println("Problem writing out state-action file:"
										+ e);
						System.exit(1);
					}

				}

			}
		}
		System.err.println("\nNumber of ordinary policies generated: "
				+ policiesGenerated + "\nTotal States " + states.size()
				+ ", Total Actions " + actions.size() + "\n" );
	}

}
