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
 * Generate set of state-action value files that define policies
 * which differ by one possible action from the policy defined by
 * the state-action value file given to this processes
 *
 * assumes policy = arg max_a Q(s,a)
 *
 * Created on Oct 14, 2004 by Paul A. Crook
 *
 **/

public class LocalMinimumMaxQPoliciesToCheck {

	static int policiesGenerated;
	static int equalPoliciesGenerated;
	
	private static String usageString() {
		return( "\nusage: java LocalMinimumMaxQPoliciesToCheck FILENAME OUTPUT_DIR\n" );
	}
	
	public static void main(String[] args) {
		
        if (args.length != 2) {
            System.err.println( usageString() );           
            System.exit(1);
        }
        
        Hashtable stateActionValues = null;
        Hashtable maxStateValues = new Hashtable();
        Hashtable maxValuedActions = new Hashtable();
        ArithmeticInteger nonExistantStateActionPairs = new ArithmeticInteger(0);
        int equalValuedSApairs = 0;
        int statesContainingEqualValuedActions = 0;
        int checkEqualPoliciesGenerated = 0;
        int actionSteps = 0;
        int runsCompleted = 0;

        policiesGenerated = 0;
        equalPoliciesGenerated = 0;

        try {
            FileInputStream fis = new FileInputStream(args[args.length - 2]);
            ObjectInputStream ois = new ObjectInputStream(fis);

            stateActionValues = (Hashtable) ois.readObject();
            actionSteps = ois.readInt();
            runsCompleted = ois.readInt();
            ois.close();
            fis.close();
        }
         catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

         Vector states = new Vector();
         Vector actions = new Vector();

         for (Enumeration en = stateActionValues.keys(); en.hasMoreElements();) {
             StateActionPair sa = (StateActionPair) en.nextElement();

             // get complete list of states and actions
             if (!states.contains(sa.state)) {
                 states.add(sa.state);
             }

             if (!actions.contains(sa.actionName)) {
                 actions.add(sa.actionName);
             }
             
             // get maximum valued action for each state
             Double value = (Double) stateActionValues.get(sa);
             Double maxStateV = (Double) maxStateValues.get(sa.state);
             if (maxStateV == null || value.compareTo(maxStateV) > 0) {
             	maxStateValues.put(sa.state, value);
             	Vector actionNames = new Vector();
             	actionNames.add(sa.actionName);
             	maxValuedActions.put(sa.state, actionNames);
             }
             else if ( value.compareTo(maxStateV) == 0 ) {
             	Vector actionNames = (Vector) maxValuedActions.get(sa.state);
             	actionNames.add(sa.actionName);
             	maxValuedActions.put(sa.state, actionNames);
             }
         }

         Collections.sort(states);
         Collections.sort(actions);
         
         // loop over all states recursively such that all combinations
         // are generated where equal action values
         /** commented out; too many cases generated; will handle this manually
	     recurseOverStates(states, maxValuedActions, stateActionValues, 
	                       actionSteps, runsCompleted, args[args.length - 1], 0);
	 **/

         // loop over all state and actions
         // output new policies when they are generated
         for (Iterator i = states.iterator(); i.hasNext(); ) {
         	State s = (State) i.next();
         	
         	if ( ((Vector) maxValuedActions.get(s)).size() > 1 ) {
         		statesContainingEqualValuedActions++;
         		int numberActs = ((Vector) maxValuedActions.get(s)).size();
         		equalValuedSApairs = equalValuedSApairs + numberActs;
         		checkEqualPoliciesGenerated = checkEqualPoliciesGenerated * numberActs;
         	}

         	for (Iterator j = actions.iterator(); j.hasNext(); ) {
         		String a = (String) j.next(); // action name
         		StateActionPair sa = new StateActionPair(s,a);
				Double value = (Double) stateActionValues.get(sa);
				if (value == null)
					nonExistantStateActionPairs.increment();
				else if ( value.compareTo( (Double) maxStateValues.get(s) ) < 0 ) {
					Hashtable newSAVs = (Hashtable) stateActionValues.clone();
					newSAVs.put(sa, new Double( 1.0+((Double) maxStateValues.get(s)).doubleValue()));
					
					try {
			            File file = new File( args[args.length - 1] + "/PolicyMinimumCheck-promoted-" + s + "-" + a );
			            if (file.exists()) {
			                file.delete();
			            }

			            FileOutputStream fos = new FileOutputStream(file);
			            ObjectOutputStream oos = new ObjectOutputStream(fos);
			            oos.writeObject(newSAVs);
			            oos.writeInt(actionSteps);
			            oos.writeInt(runsCompleted);
			            oos.flush();
			            oos.close();
			            fos.close();
			            policiesGenerated++;
			        }
					catch (Exception e) {
						System.err.println("Problem writing out state-action file:" + e);
			            System.exit(1);
			        }
					
				}
         	
         	}
         }
         System.err.println("Number of equal policies generated (& check value): " +
         		equalPoliciesGenerated + " (" + checkEqualPoliciesGenerated +")" +
         		"\nNumber of ordinary policies generated: " + policiesGenerated +
         		"\nTotal States " + states.size() + ", Total Actions " + actions.size() +
         		"\nNumber of states with equal valued actions: " + statesContainingEqualValuedActions + 
				"\nNumber of equal valued state-action pairs:" + equalValuedSApairs +
				"\nNumber of unexplored state-action combinations: " + nonExistantStateActionPairs);
	}

	
	static void recurseOverStates(Vector states, Hashtable maxValuedActions,
								  Hashtable stateActionValues, int steps, int runs,
								  String dir, int depth) {
		depth++;
		Vector copyStates = (Vector) states.clone();
		while ( copyStates.size() > 0 && ((Vector) maxValuedActions.get(copyStates.firstElement())).size() == 1 ) {
			copyStates.remove(0);
		}
		if ( copyStates.size() > 0 ) {
			State s = (State) copyStates.firstElement();
			copyStates.remove(0);
			Vector actionList = (Vector) maxValuedActions.get(s);
         	for (Iterator j = actionList.iterator(); j.hasNext(); ) {
         		String a = (String) j.next(); // action name
         		StateActionPair sa = new StateActionPair(s,a);
         		Double value = (Double) stateActionValues.get(sa);
         		Hashtable newSAVs = (Hashtable) stateActionValues.clone();
         		newSAVs.put( sa, new Double(1.0+value.doubleValue()) );
         		recurseOverStates(copyStates, maxValuedActions, newSAVs, steps, runs, dir, depth);
         	}
		}
		else if (depth > 1){
			try {
	            File file = new File( dir + "/PolicyMinimumCheck-Equal." + equalPoliciesGenerated  );
	            if (file.exists()) {
	                file.delete();
	            }

	            FileOutputStream fos = new FileOutputStream(file);
	            ObjectOutputStream oos = new ObjectOutputStream(fos);
	            oos.writeObject(stateActionValues);
	            oos.writeInt(steps);
	            oos.writeInt(runs);
	            oos.flush();
	            oos.close();
	            fos.close();
	            equalPoliciesGenerated++;
	        }
			catch (Exception e) {
				System.err.println("Problem writing out state-action file:" + e);
	            System.exit(1);
	        }
		}
	}
}
