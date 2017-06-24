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



import java.io.*; // for file commands

import java.util.*; // for Hashtable & Vector

import java.text.DecimalFormat;

/**
 * HashTableFileReader
 * 
 * a (hopefully) useful tool that displays what's in hash table files that have
 * been written out by various act'n'learn methods.
 * 
 * P.A.Crook 12 November 2002
 * 
 * 27 November 2002 - read new style tables that use StateActionPairs as keys
 * 
 * 4 December 2003 - flag to turn off highlighting of winning state-action
 * values
 * 
 * 24 September 2004 - added functionality to display Monte Carlo returns
 *  
 */

public class HashTableFileReader {

	private static String usageString() {
		return ("\nusage: java HashTableFileReader [-stateCount] [-stateCountOnly] [-nohighlights] [-a2ps] [-noheadings] "
				+ "[-cnsfile] [-MCreturns] [-mcesp]" + "FILENAME\n");
	}

	public static void main(String[] args) {
		boolean showStateCount = false;
		boolean stateCountOnly = false;
		boolean highlightsOn = true;
		boolean headings = true;
		boolean a2ps = false;
		boolean cnsFile = false;
		boolean mcReturns = false;
		boolean mcespPolicy = false;

		if (args.length < 1) {
			System.err.println(usageString());
			System.exit(1);
		}

		for (int i = 0; i < (args.length - 1); i++) {
			if (args[i].equals("-nohighlights")) {
				highlightsOn = false;
			} else if (args[i].equals("-stateCount")) {
				showStateCount = true;
			} else if (args[i].equals("-stateCountOnly")) {
				stateCountOnly = true;
			} else if (args[i].equals("-noheadings")) {
				headings = false;
			} else if (args[i].equals("-cnsfile")) {
				cnsFile = true;
			} else if (args[i].equals("-MCreturns")) {
				mcReturns = true;
			} else if (args[i].equals("-mcesp")) {
				mcespPolicy = true;
			} else if (args[i].equals("-a2ps")) {
				a2ps = true;
				highlightsOn = false;
			} else {
				System.err.println("\nargument not recognised: " + args[i]);
				System.err.println(usageString());
				System.exit(1);
			}
		}

		Hashtable hash = null;
		Hashtable cnsHash = null;
		Hashtable returns = null;
		Hashtable policy = null;
		Hashtable countSAupdates = null;
		ArithmeticInteger countPolicyUpdates = null;

		try {
			FileInputStream fis = new FileInputStream(args[args.length - 1]);
			ObjectInputStream ois = new ObjectInputStream(fis);
			hash = (Hashtable) ois.readObject();
			ois.readInt(); //read in actionSteps (not used or displayed)
			ois.readInt(); //read in runsCompleted (also not used)

			if (cnsFile) {
				cnsHash = (Hashtable) ois.readObject();
			}

			if (mcReturns) {
				returns = (Hashtable) ois.readObject();
			}

			if (mcespPolicy) {
				policy = (Hashtable) ois.readObject();
				countSAupdates = (Hashtable) ois.readObject();
				countPolicyUpdates = (ArithmeticInteger) ois.readObject();
			}

			ois.close();
			fis.close();
		} catch (Exception e) {
			System.err.println(e);
			System.exit(1);
		}

		Vector states = new Vector();
		Vector actions = new Vector();

		for (Enumeration en = hash.keys(); en.hasMoreElements();) {
			StateActionPair sa = (StateActionPair) en.nextElement();

			if (!states.contains(sa.state)) {
				states.add(sa.state);
			}

			if (!actions.contains(sa.actionName)) {
				actions.add(sa.actionName);
			}
		}

		Collections.sort(states);
		Collections.sort(actions);

		if (stateCountOnly)
			System.out.println(states.size());

		else {
			//print out action headers
			if (headings) {
				System.out.println();
				System.out.print(Strings.makeFixedLength("State", 28));

				for (int i = 0; i < actions.size(); i++) {
					System.out.print(Strings
							.makeFixedLength(actions.get(i), 23));
				}

				System.out.println();
			}

			//print out entries
			for (int i = 0; i < states.size(); i++) {
				System.out.print(Strings.makeFixedLength(states.get(i), 23));

				Vector entries = new Vector();
				Double topValue = null;

				for (int j = 0; j < actions.size(); j++) {
					StateActionPair sa = new StateActionPair((State) states
							.get(i), (String) actions.get(j));
					Double saValue = null;

					if (hash.containsKey(sa)) {
						saValue = (Double) hash.get(sa);
					}

					if ((saValue != null)
							&& ((topValue == null) || (saValue
									.compareTo(topValue) > 0))) {
						topValue = saValue;
					}

					entries.add(saValue);
				}

				for (Enumeration e = entries.elements(); e.hasMoreElements();) {
					String outString = Strings.makeFixedLength("--", 23);
					Object out = e.nextElement();

					if (out != null) {
						outString = out.toString();

						if (a2ps) {
							if (topValue.compareTo( (Double) out ) == 0)
								outString = "*" + outString + "*";
							else
								outString = " " + outString + " ";
						}

						outString = Strings.makeFixedLength(outString, 23);

						if ((topValue.compareTo( (Double) out ) == 0) && highlightsOn) {
							outString = "\033[1m" + outString + "\033[0m";
						}
					}

					System.out.print(outString);
				}

				System.out.println("\n");
			}

			if (showStateCount)
				System.out.println("Total Number of States: " + states.size()
						+ "\n");

			if (cnsFile) {
				System.out.print(Strings.makeFixedLength("State", 14));

				for (int i = 0; i < actions.size(); i++) {
					System.out.print(Strings
							.makeFixedLength(actions.get(i), 14));
				}

				System.out.println();

				//print out entries
				for (int i = 0; i < states.size(); i++) {
					System.out
							.print(Strings.makeFixedLength(states.get(i), 14));

					for (int j = 0; j < actions.size(); j++) {
						StateActionPair sa = new StateActionPair((State) states
								.get(i), (String) actions.get(j));
						State s = (State) cnsHash.get(sa);
						String outString = "--";

						if (s != null) {
							outString = s.toString();
						}

						outString = Strings.makeFixedLength(outString, 14);
						System.out.print(outString);
					}

					System.out.println();
				}
			}

			if (mcReturns) {
				System.out.println("Monte Carlo returns:");
				Vector sortedKeys = new Vector(returns.keySet());
				Collections.sort(sortedKeys); // sort stateActionPairs for
											  // display
				for (Iterator i = sortedKeys.iterator(); i.hasNext();) {
					StateActionPair sa = (StateActionPair) i.next();
					System.out.print(Strings.makeFixedLength(sa, 40));

					Vector returnSA = (Vector) returns.get(sa);
					ArithmeticDouble total = (ArithmeticDouble) returnSA
							.firstElement();
					ArithmeticInteger number = (ArithmeticInteger) returnSA
							.get(1);

					DecimalFormat df = new DecimalFormat();
					df.setMaximumFractionDigits(4);
					System.out.print(Strings.makeFixedLength(df
							.format(total.value), 15));
					System.out.print(Strings.makeFixedLength(number, 10));
					System.out.print("\n");

				}
			}

			if (mcespPolicy) {
				System.out.println("MCESP Policy: (Policy Updates "
						+ countPolicyUpdates + ")");
				Vector sortedKeys = new Vector(policy.keySet());
				Collections.sort(sortedKeys);
				for (Iterator i = sortedKeys.iterator(); i.hasNext();) {
					State s = (State) i.next();
					System.out.print(Strings.makeFixedLength(s, 28));
					System.out.print(Strings.makeFixedLength(policy.get(s), 28)
							+ "\n");
				}

				//print out action headers
				System.out.println("\nState Action update counts:");
				System.out.print(Strings.makeFixedLength("State", 28));

				for (int i = 0; i < actions.size(); i++) {
					System.out.print(Strings
							.makeFixedLength(actions.get(i), 23));
				}

				System.out.println();

				//print out entries
				for (int i = 0; i < states.size(); i++) {
					System.out
							.print(Strings.makeFixedLength(states.get(i), 23));

					for (int j = 0; j < actions.size(); j++) {
						StateActionPair sa = new StateActionPair((State) states
								.get(i), (String) actions.get(j));
						ArithmeticInteger count = null;

						if (countSAupdates.containsKey(sa)) {
							count = (ArithmeticInteger) countSAupdates.get(sa);
						}

						String outString = "--";

						if (count != null)
							outString = count.toString();

						outString = Strings.makeFixedLength(outString, 23);

						System.out.print(outString);
					}

					System.out.print("\n");
				}

			}
		}
	}
}
