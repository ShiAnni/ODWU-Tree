package utree;
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


import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Iterator;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import core.*;


/**
 * Implements UTree act'n'learn method. McCallum SAB'96 / PhD Thesis, University
 * of Rochester, 1995
 * 
 * An issue with the algorithms as described in both of the above works is that
 * the tuple stored in the tree stores <T_t-1, a_t-1, o_t, r_t> storing this in
 * the leaf node 's_t' whose conjunction is satisfied by this instance creates a
 * problem when later trying to calculate the average reward at each node (t is
 * time). This tuple stores the reward resulting from executing action a_t-1
 * when in state s_t-1 (represented by tuple T_t-1). So either (i) the formula
 * for R(s,a) need to be re-written, (ii) different values need to be stored in
 * the tuple, or (iii) the above tuple needs to be stored in state s_t-1 not
 * s_t. In this implementation I've done the latter as it is easier to implement
 * algorithmically.
 * 
 * In the view point that I've adopted here each node represents a set of
 * state-action values and new nodes are created when the distribution of
 * rewards associated with the 'policy action' suggest it would be advantageous
 * to do so.
 * 
 * An alternative view can be envisaged where the above tuple *is* store in node
 * s_t. In this view the distribution of rewards represent the reward for being
 * in that state (as opposed to executing some action from that state). In this
 * view each leaf node represents a state, and the value of that state. R(s,a)
 * would become R(s). States would be split if the distribution of rewards
 * indicated that the state would be better represented as two states.
 * 
 * The relative merit of either approach is not obvious to me.
 * 
 * Paul A. Crook, 3rd January 2004
 * 
 * 
 * 
 * 31st March, added minimum fringe depth argument
 * 
 * 
 * 1-12th August 2005 
 * 
 * - added flag to allow selection of actions compared for KS test.
 * Select between 'all' (previous default) and just 'policy' actions the latter
 * as used in McCallums code.
 * 
 *  - change selection of fringe to add such that if there are more than one
 * significant candidate for a given leaf the fringe with the highest KS
 * d-statistic is selected. Again as per McCallums code.
 *  
 *  - added flag to select whether observation should be broken down across
 * perceptual dimensions. Checks agent's perceptual dimensions are fixed and
 * only allow it to be broken down if they are.
 *  
 *  - changed recursion to iteration in method listExpansionsOfLeaf. Hopefully
 * will now avoid stack overflow problems.
 *  
 *  - added option to prefer the shortest fringe extension which produces a
 * utile distinction for an existing leaf. Secondary criteria to prefer max KS
 * d-statistic if several extensions of same length found.
 *  
 *  - bring out MIN_INSTANCES_FOR_KS_TEST as an argument.
 *  
 *  
 *  14th August 2005
 *  
 *  - removed preference option; always prefer shortest with KS d-statisitic
 *  as secondary preference.  This speeds things up and McCallum's code
 *  appears to do the same. 
 *
 *
 *
 * 
 * REMINDER: probability measure return by KS is the probability that both
 * data sets come from the *same* distribution.
 * 
 *  KS probability = 1 -> same distribution.
 *  KS probability = 0 -> strongly different distributions.
 *  
 *  therefore the smaller the value used for 'KSprob' the more distinct the
 *  distributions need to be.
 *
 * default value used by McCallum's code KSprob=0.2
 * value used in SAB'96 & PhD for NY driving problem = 0.0001
 * 
 * 
 * (default value used by McCallum for minInstances4KsTest = 10)
 */

public class UTree extends ReallyBasicStateActionLearner {

	protected String kSteps, minFringeDepth, maxFringeDepth, maxHistory;

	protected String ksProbability, actionsToCompare;

	protected int minInstances4KsTest;

	protected boolean subDividePerceptions;

	protected InstanceChain instanceChain;

	protected TreeNode tree;

	// constructor
	public UTree(Agent agent, String args) throws Exception {
		super(agent, args); // set up internal pointer to the agent.
		// Also interpret formula for gamma, conduct
		// and (if required) read data from file.

		/***********************************************************************
		 * ***** do we need to save instance chain? can it be reset on re-init
		 * of each episode?
		 **********************************************************************/

		if (!(saInitValue instanceof StateActionValuesInitiallyZero))
			throw new WorldSetUpException(
					"UTree only works with \"initSAvs=zeros\".");

		kSteps = Args.getArgument(args, "kSteps=");
		minFringeDepth = Args.getArgument(args, "minFringe=");
		maxFringeDepth = Args.getArgument(args, "maxFringe=");
		maxHistory = Args.getArgument(args, "maxHistory=");
		ksProbability = Args.getArgument(args, "KSprob=");
		actionsToCompare = Args.getArgument(args, "KScompareActs=");
		String yesNo = Args.getArgument(args, "subPercepts=");
		
		minInstances4KsTest = Integer
				.parseInt(Args.getArgument(args, "minIs="));

		if (yesNo.equals("yes"))
			subDividePerceptions = true;
		else if (yesNo.equals("no"))
			subDividePerceptions = false;
		else
			throw new Exception(usageString());

		if (subDividePerceptions && !agent.observationDimensionsFixed())
			throw new WorldSetUpException(
					"Can't subdivide perceptions for specified agent.");

		if (!actionsToCompare.equals("all")
				&& !actionsToCompare.equals("policy"))
			throw new Exception(usageString());



		evalFormula(kSteps); // check values evaluate okay
		evalFormula(minFringeDepth);
		evalFormula(maxFringeDepth);
		evalFormula(maxHistory);
		evalFormula(ksProbability);
	}

	// list arguments, e.g. kSteps, alpha, gamma, etc., and current values
	public String listActnLearnArguments() {
		return super.listActnLearnArguments()
				+ "\tkSteps\tminFringe\tmaxFringe\tmaxHistory\tKSprob";
	}

	public String currentValueActnLearnArgs() {
		return (super.currentValueActnLearnArgs() + "\t"
				+ String.valueOf(evalFormula(kSteps)) + "\t"
				+ String.valueOf(evalFormula(minFringeDepth)) + "\t\t"
				+ String.valueOf(evalFormula(maxFringeDepth)) + "\t\t"
				+ String.valueOf(evalFormula(maxHistory)) + "\t\t" + String
				.valueOf(evalFormula(ksProbability)));
	}

	// usage string - to give user some clue what to do
	protected String usageString() {
		UsageArgumentVector arguments = new UsageArgumentVector();
		usageArguments(arguments);
		return ("Error in format of arguments.\n\nUsage: u-tree" + 
				arguments.toString() + "\n" );
	}

	// add strings to the given arguments vector, each string is the
	// format of arguments expected by this class
	protected void usageArguments(UsageArgumentVector arguments) {
		arguments.add("kSteps=N");
		arguments.add("minFringe=N");
		arguments.add("maxFringe=N");
		arguments.add("maxHistory=N");
		arguments.add("minIs=INTEGER");
		arguments.add("KSprob=N");
		arguments.add("KScompareActs=all/policy");
		arguments.add("subPercepts=yes/no");
		super.usageArguments(arguments);
	}

	// overwrite init to add an instance to the instanceChain
	// which flags the start of a new run
	public void init(boolean evaluation) {
		super.init(evaluation);
		Action a = new ActionWorldReset();
		Float r = new Float(0.0);
		// get observation
		State o = agent.getState();
		// create new instance
		Instance tee = new Instance(a.toString(), o, r);
		// add instance to instance chain
		instanceChain.addInstance(tee);
	}

	// Clone the stuff that ReallyBasicStateActionLearner super-class doesn't
	// deal with
	public Object clone() {
		UTree aClone = (UTree) super.clone();
		aClone.instanceChain = (InstanceChain) instanceChain.clone();
		aClone.tree = (TreeNode) tree.clone();
		return aClone;
	} // no Exceptions thrown by ReallyBasicStateActionLearner or Hashtable

	// agent evaluation - evaluate agent's performance if it were to act
	// 'greedy' from current position.
	public void evaluate() {
		// clear dossier that reports actions
		clearDossier();
		// get current state based on instance chain.
		// allow for leaves to be added as parameter values not seen before
		// could be encountered while carrying out the evaluation
		TreeNode nodeS = getNodeS(true);
		// wrap nodeS in State class
		State s = new State(new Object[] { nodeS });
		// select action greedily
		Action a = selectAction(s, false);
		// execute action, obtain reward
		Float r = executeAction(a, s);
		// get new observation
		State o = agent.getState();
		// create new instance
		Instance tee = new Instance(a.toString(), o, r);
		// add instance to instance chain
		instanceChain.addInstance(tee);
	}
	
	public void mainLoop2(){
		TreeNode nodeS = getNodeS(true);
		State s = new State(new Object[]{ nodeS });
		Action a = selectAction(s, true);
		Float r = executeAction(a, s);
		State o = agent.getState();
		Instance tee = new Instance(a.toString(), o, r);
		instanceChain.addInstance(tee);
	}

	// main loop - UTree algorithm
	public void mainLoop() {
		// clear dossier that reports actions
		clearDossier();
		// increment action count
		actionSteps++;
		// get current node "s" of tree based on instance chain.
		// allows for leaves to be added as parameter values not seen before
		// could be encountered following an init
		TreeNode nodeS = getNodeS(true);
		// wrap nodeS in State class
		State s = new State(new Object[] { nodeS });
		// select action (allowing exploration),
		Action a = selectAction(s, true);
		// execute action, obtain reward
		Float r = executeAction(a, s);
		// get new observation
		State o = agent.getState();
		// create new instance.
		// NB don't need to store T-1 as instanceChain
		// records relationships between instances.
		Instance tee = new Instance(a.toString(), o, r);
		// add instance to instance chain
		instanceChain.addInstance(tee);

		// get the new state s' based on extended instance chain.
		// allow for a leaf to be added if
		// parameter value not seen before
		TreeNode nodeSdash = getNodeS(true);

		// each leaf node contains a UTreeLeafData. These hold an
		// index to the lists of matching Instances held as a Vector
		// and a Hashtable of actions and associated Q, N, sum r &
		// transition values
		((UTreeLeafData) nodeS.data).addInstanceIndex(instanceChain
				.indexOf(tee)); // add the current instance

		// update R(s,a) and Pr(s'|s,a)
		{ // (use block to limit scope of variables)
			Hashtable actionValues = ((UTreeLeafData) nodeS.data).actionValues;
			// Actions are keys to the actionValues hashtable. Hashtable
			// contains
			// vectors. Each vector's entries are: Q values, N number of
			// matching instances, sum of rewards for matching instances,
			// transition probabilities
			Vector values;
			if (actionValues.containsKey(a.toString())) {
				values = (Vector) actionValues.get(a.toString());
			}
			else {
				// if doesn't exist create new Vector
				values = new Vector();
				// and add to actionValues
				actionValues.put(a.toString(), values);
				// initialize Q value
				values.add(new Double(0.0));
				// init. number of entries (N)
				values.add(new ArithmeticInteger(0));
				// init. sum of rewards (sum r)
				values.add(new ArithmeticFloat(0.0f));
				// init. table of transition probabilities
				values.add(new Hashtable());
			}
			// increment N
			((ArithmeticInteger) values.elementAt(1)).increment();
			// increment sum of r
			((ArithmeticFloat) values.elementAt(2)).plus(r);
			Hashtable transitionCounts = (Hashtable) values.elementAt(3);
			if (transitionCounts.containsKey(nodeSdash)) {
				((ArithmeticInteger) transitionCounts.get(nodeSdash))
						.increment();
			}
			else {
				transitionCounts.put(nodeSdash, new ArithmeticInteger(1));
			}
		}

		// one "sweep" of "value iteration" (one step of dynamic
		// programming on the Q-values:
		//
		// Q(s,a) <-- R(s,a) + \gamma * \sum{s'} Pr{s'|s,a).U(s')
		//
		// where U(s') = max_{a \in A} Q(s',a)

		Vector allLeaves = tree.allLeaves(); // get all leaf nodes

		for (Enumeration e = allLeaves.elements(); e.hasMoreElements();) {
			Hashtable actionValues = ((UTreeLeafData) ((TreeNode) e
					.nextElement()).data).actionValues;
			for (Enumeration f = actionValues.elements(); f.hasMoreElements();) {
				Vector values = (Vector) f.nextElement();
				int N = ((ArithmeticInteger) values.elementAt(1)).value;
				if (N == 0) {
					System.err
							.print("Division by zero (N=0) in UTree, mainLoop(), "
									+ "one sweep of dynamic programming.");
					System.exit(1);
				}
				float R = ((ArithmeticFloat) values.elementAt(2)).value
						/ ((float) N);
				float sumPrU = 0;
				Hashtable transitionCounts = (Hashtable) values.elementAt(3);
				for (Enumeration g = transitionCounts.keys(); g
						.hasMoreElements();) {
					// each key is a nodeSdash so contains the Q values we want
					// to maximize over
					TreeNode nodeSdash2 = (TreeNode) g.nextElement();
					Hashtable aDashValues = ((UTreeLeafData) nodeSdash2.data).actionValues;
					Double U = null;
					// get max Q(s',a') --> U(s')
					for (Enumeration h = aDashValues.elements(); h
							.hasMoreElements();) {
						Double uTemp = (Double) ((Vector) h.nextElement())
								.firstElement();
						if (U == null || uTemp.compareTo(U) > 0) {
							U = uTemp;
						}
					}
					// if U equals null; then no instances stored at nodeSdash2.
					// Therefore this is either a
					// terminal state node (terminal state of the task) or the
					// first time this instance has
					// ever been visited and we don't know where it goes next.
					// Either way assuming a
					// U value of zero seems reasonable.
					if (U == null)
						U = new Double(0.0);
					sumPrU += U.floatValue()
							* (float) ((ArithmeticInteger) transitionCounts
									.get(nodeSdash2)).value / ((float) N);
				}
				values.set(0, new Double(R + evalFormula(gamma) * sumPrU));
			}
		}

		// every 'k' steps check if new distinctions are required
		if (actionSteps % evalFormula(kSteps) == 0) {
			boolean fixTransitions = false;

			// 1. work through all leaves
			// --------------------------
			for (int e = 0; e < allLeaves.size(); e++) {
				TreeNode leaf = (TreeNode) allLeaves.get(e);
				UTreeLeafData leafValue = (UTreeLeafData) leaf.data;
				Vector indexOfInstances = leafValue.indexOfInstances;

				// 2. extend fringe at each leaf.
				// -----------------------------
				// As I only carry out a KS test if minimum number of
				// data points exist, say 10, no need to bother if a
				// leaf contains less than this minimum number.
				if (indexOfInstances.size() >= minInstances4KsTest) {

					// find the Q value for each instance in instanceList (using
					// the current tree)
					Hashtable instanceListQvalues = new Hashtable();
					for (Enumeration f = indexOfInstances.elements(); f
							.hasMoreElements();) {
						Instance instance = instanceChain
								.getInstance((Integer) f.nextElement());
						// fixTransitions - when re-passing though a
						//                  tree that has just been extended
						TreeNode sDashed = getNodeS(instance, fixTransitions);
						// certain parameter values might not be represented so
						// need to allow leaves to be added
						Hashtable aDashValues = ((UTreeLeafData) sDashed.data).actionValues;
						Double U = null;
						// get max Q(s',a') --> U(s')
						for (Enumeration h = aDashValues.elements(); h
								.hasMoreElements();) {
							Double uTemp = (Double) ((Vector) h.nextElement())
									.firstElement();
							if (U == null || uTemp.compareTo(U) > 0) {
								U = uTemp;
							}
						}
						// if U equals null; then no instances stored at node S.
						// Therefore this is either a
						// terminal state node (terminal state of the task) or
						// the first time this instance has
						// ever been visited and we don't know where it goes
						// next. Either way a U value of
						// zero seems reasonable.
						if (U == null)
							U = new Double(0.0);
						// formula 7.10 in McCallum's thesis; but as each
						// instance is unique Pr(L(T_{i+1})) = 1
						Double Qvalue = new Double(instance.reward()
								.floatValue()
								+ evalFormula(gamma) * U.doubleValue());
						instanceListQvalues.put(instance, Qvalue);
					}

					// Total possible splits = number of dimensions * max.
					// history index.

					// number of dimensions fixed by whether agent's
					// perception can be broken down. Use
					// ordering to reduce search space as per
					// McCallum. Dimensions numbered by Instance class
					// so use that numeric ordering. Order history
					// index from current (0) to max. history index
					// (-maxHistory).

					// select fringe (if any) that satisfies the
					// required level of probability and is the "strongest"
					// i.e. has the highest KS D statistic. [McCallum's
					// code rlkit0.3.3, class implementation UTNodePA.m
					// method -doTryAllSplitsToDepth: (int) depth]
					TreeNode strongestFringe = null;
					Double strongestKSd = null;
					Integer strongestFringeDepth = null;

					// set up generator of expansions for current leaf.
					// the list returned groups expansions together in
					// order of depth, starting with the shortest
					// expansions, then longer versions up to the
					// specified max fringe depth. As soon the shortest
					// expansion that is utile and has the maximum
					// Kolmogorov Smirnov D statistic is found, that
					// fringe is promoted and the remaining expansions
					// are not requested. The process then restarts from
					// the beginning of the tree. The ordering 
					// (shortest-longest expansions) will ensure that
					// shorter expansion are preferentially selected.
					
					//int count = 0;

					for (LeafExpansionGenerator f = new LeafExpansionGenerator(
							leaf.conjunctionOfParametersToSplitOn(),
							(int) evalFormula(maxFringeDepth),
							(int) evalFormula(maxHistory),
							agent, subDividePerceptions );
							f.hasNextExpansion() && ( 
								strongestFringe == null ||
								f.nextExpansionLength() <= 
									strongestFringeDepth.intValue()
								);
							) {
						
						//count++;
						
						// go through each possible expansion, take each
						// expansion list and use the parameters selected
						// to split the leafs instances between different
						// branches according to values stored in the
						// instances (look using getParameterValue with
						// index). When reached bottom of expansion see
						// if minimum number of instances for a KS test
						// exists in any of the fringe leaves.
						Vector expansion = (Vector) f.getNextExpansion();
						
						
						/*
						for(int i = 0; i < expansion.size(); i++){
							System.out.print(((ParameterSelected)expansion.elementAt(i)).historyIndex);
							System.out.print(((ParameterSelected)expansion.elementAt(i)).dimension + " ");
						}
						System.out.println();
						*/
						
						
						// check minimum fringe depth met. (this would be
						// better done in the generator but I don't need
						// to  optimize this at the moment)
						if (expansion.size() >= evalFormula(minFringeDepth)) {
							// start a new tree to store fringe.
							// root of the fringe starts
							// with current leaf data.
							TreeNode fringe = new TreeNode(leafValue);

							for (Enumeration g = expansion.elements(); g
									.hasMoreElements();) {
								
								
								ParameterSelected parameter = (ParameterSelected) g
										.nextElement();

								for (Enumeration h = fringe.allLeaves()
										.elements(); h.hasMoreElements();) {
									TreeNode fringeLeaf = (TreeNode) h
											.nextElement();
									UTreeLeafData fringeLeafValue = (UTreeLeafData) fringeLeaf.data;
									Vector fringeLeafIndexOfInstances = fringeLeafValue.indexOfInstances;
									// turn fringe 'leaf' into a 'tree node';
									// i.e. a point where the tree splits.
									// To do this set parameter to split on
									// and clear out data.
									fringeLeaf.setParameterToSplitOn(parameter);
									fringeLeaf.clearData();

									for (Enumeration i = fringeLeafIndexOfInstances
											.elements(); i.hasMoreElements();) {
										int instanceIndex = ((Integer) i
												.nextElement()).intValue();
										Instance instance = instanceChain
												.getInstance(instanceIndex);
										// each instance is stored in the node
										// it came from (node S(T-1)) therefore
										// need to look at the instance's
										// predecessor to see where to store it
										// in the fringe.
										Object parameterValue = parameter
												.getParameterValue(
														instanceIndex - 1,
														instanceChain);
										TreeNode subFringeLeaf;
										if (fringeLeaf
												.branchExistsForParameterValue(parameterValue))
											subFringeLeaf = fringeLeaf
													.getSubTreeNode(parameterValue);
										else {
											subFringeLeaf = new TreeNode(
													new UTreeLeafData());
											fringeLeaf.addSubTreeNode(
													parameterValue,
													subFringeLeaf);
										}
										// add instance to sub fringe leaf
										((UTreeLeafData) subFringeLeaf.data)
												.addInstanceIndex(instanceIndex);
										// update or populate actionValue table
										Hashtable subFringeActionValues = ((UTreeLeafData) subFringeLeaf.data).actionValues;
										Vector values;
										if (subFringeActionValues
												.containsKey(instance
														.actionName())) {
											values = (Vector) subFringeActionValues
													.get(instance.actionName());
										}
										else {
											// if doesn't exist create new Vector
											values = new Vector();
											// and add to actionValues
											subFringeActionValues.put(instance
													.actionName(), values);
											// initiate Q value
											values.add(new Double(0.0));
											// initiate number of entries (N)
											values.add(new ArithmeticInteger(0));
											// initiate sum of rewards (sum r)
											values.add(new ArithmeticFloat(0.0f));
											// initiate table of transition probabilities
											values.add(new Hashtable());
										}
										Double Q = ((Double) values
												.elementAt(0));
										ArithmeticInteger N = ((ArithmeticInteger) values
												.elementAt(1));
										Q = new Double(
												(Q.doubleValue() * N.value + ((Double) instanceListQvalues
														.get(instance))
														.doubleValue())
														/ (N.value + 1));
										values.set(0, Q); // update Q value
										N.increment(); // increment N
										// increment sum of r
										((ArithmeticFloat) values.elementAt(2))
												.plus(instance.reward());

										// don't worry about transition counts;
										// the whole tree will need updating
										// if this fringe is added.

									}
								}
							}

							// expansion complete, start doing K-S tests on
							// leaves

							// 3. test extended fringe nodes back against
							// existing leaf they come from:
							// (i) test distribution of future discounted reward
							// values for the *same action*
							// between fringe node and leaf node; distribution
							// calculated for each
							// instance using:
							// Q(T_i) = r_i + \gamma \sum_{L(T_{i+1})}
							// Pr(L(T_{i+1})).U(L(T_{i+1}))
							// * \sum only required if T_{i+1} can be more than
							// one state node ??? *
							// * I believe each instance to be unique so
							// Pr(L(T_{i+1})) = 1 *

							// *NOTE*: Q values of instances ( specifically
							// U(s') = max_a Q(s',a) ) is
							// calculated above using *only* existing tree and
							// not this fringe expansion.
							// It would be possible to take this fringe
							// expansion into account but would
							// need to recalculate transition probabilities for
							// the whole tree and do
							// several sweeps of dynamic programming before we
							// could have any confidence
							// in the values.

							// get policy action(s) for the parent
							Vector leafPolicyActions = leafValue
									.getMaxQActionNames();

							// Q values for (parent) leaf node to this fringe

							// comparison should be for *same action* from
							// different nodes...
							// (keys to actionValues hashtable are actionNames)
							for (Enumeration g = leafValue.actionValues.keys(); g
									.hasMoreElements();) {
								String actionName = (String) g.nextElement();
								Vector leafQvaluesGivenAction = new Vector();
								for (Enumeration h = indexOfInstances
										.elements(); h.hasMoreElements();) {
									Instance instance = instanceChain
											.getInstance((Integer) h
													.nextElement());
									if (instance.actionName()
											.equals(actionName)) {
										leafQvaluesGivenAction
												.add(instanceListQvalues
														.get(instance));
									}
								}

								// test all fringe leaves
								for (Enumeration h = fringe.allLeaves()
										.elements(); h.hasMoreElements();) {
									TreeNode fringeLeaf = (TreeNode) h
											.nextElement();

									// get fringe leaf policy action(s)
									Vector fringeLeafPolicyActions = ((UTreeLeafData) fringeLeaf.data)
											.getMaxQActionNames();

									// if KS to test on policy actions only then
									// skip actions that aren't policy
									// for either parent or fringe leaf. This is
									// in line with McCallum's code
									// (rlkit0.3.3) and thesis "In keeping with
									// the proof in section 4.2, the agent
									// only tests the distributions of the leaf
									// node's policy action and the fringe
									// node's policy action."
									if (actionsToCompare.equals("all")
											|| leafPolicyActions
													.contains(actionName)
											|| fringeLeafPolicyActions
													.contains(actionName)) {

										Vector fringeLeafQvaluesGivenAction = new Vector();
										// get Q values for relevant fringe
										// instances
										for (Enumeration i = ((UTreeLeafData) fringeLeaf.data).indexOfInstances
												.elements(); i
												.hasMoreElements();) {
											Instance instance = instanceChain
													.getInstance((Integer) i
															.nextElement());
											if (instance.actionName().equals(
													actionName)) {
												fringeLeafQvaluesGivenAction
														.add(instanceListQvalues
																.get(instance));
											}
										}
										// test fringe leaf has a minimum number
										// of instances for this action.
										if (fringeLeafQvaluesGivenAction.size() >= minInstances4KsTest) {

											// do KS comparison
											double[] results = KolmogorovSmirnov
													.testTwoDataSets(
															leafQvaluesGivenAction,
															fringeLeafQvaluesGivenAction);
											// If probability P is small (i.e. <
											// ksProb) then a utile fringe found.
											// If its also the strongest found
											// then make a note of it.
											if ( results[1] < evalFormula(ksProbability) &&
													( strongestFringe == null || 
														( expansion.size() <= strongestFringeDepth.intValue()
															&& results[0] > strongestKSd.doubleValue()
														)
													)
												) {
												strongestFringe = fringe;
												strongestKSd = new Double(
														results[0]);
												strongestFringeDepth = new Integer(
														expansion.size());
											}
										}
									}
								} // end loop over all fringe leaves
							} // end loop over actions for testing fringe leaves
						}
					} // end of loop through list of expansions.
					
					//System.out.println("total count: " + count);
					

					// to arrive here will have exhausted list of
					// expansions for this leaf or found shortest
					// expansion with highest KS d-statistic.

					// attach stongestFringe to tree proper if a utile expansion
					// was found
					if (strongestFringe != null) {
						leaf.replaceWith(strongestFringe);
						// set flat to indicate need to fix transitions
						fixTransitions = true;
						// get all *new* leaf nodes and
						allLeaves = tree.allLeaves();
						// restart search at start of tree (NB e++ in
						// loop will increment this to zero)
						e = -1;
					}
				}
			} // end of loop over all leaves

			// if a fringe has been added the transition probabilities for the
			// whole tree need updating here...
			if (fixTransitions) {
				for (Iterator i = allLeaves.iterator(); i.hasNext();) {
					TreeNode leaf = (TreeNode) i.next();
					UTreeLeafData leafValue = (UTreeLeafData) leaf.data;
					Vector indexOfInstances = leafValue.indexOfInstances;
					Hashtable actionValues = leafValue.actionValues;
					// re-initiate transition hash tables (keys are actionNames)
					for (Enumeration j = actionValues.keys(); j
							.hasMoreElements();) {
						String actionName = (String) j.nextElement();
						((Vector) actionValues.get(actionName)).set(3,
								new Hashtable());
					}
					// find out where the instances now end up and count
					// transitions
					for (Iterator j = indexOfInstances.iterator(); j.hasNext();) {
						Instance instance = instanceChain
								.getInstance((Integer) j.next());
						// allow addition of leaves to the tree;
						TreeNode nodeS2 = getNodeS(instance, true);
						// if the number of instances < minimum for KS test then
						// some of the parameter values of instances in this leaf
						// may not have been seen before.
						Vector values = (Vector) actionValues.get(instance
								.actionName());
						Hashtable transitionCounts = (Hashtable) values
								.elementAt(3);
						if (transitionCounts.containsKey(nodeS2)) {
							((ArithmeticInteger) transitionCounts.get(nodeS2))
									.increment();
						}
						else {
							transitionCounts.put(nodeS2, new ArithmeticInteger(
									1));
						}
					}
				}
			}

		}
		// at this point tree update has been skipped or completed...
	}


	// get node S based on the current instance, i.e. last entry in the instance
	// chain
	// ** 3rd August 2005 ** commented out as not used; always tend to call
	// versions that fix extra leaves to the tree as required.
	// private TreeNode getNodeS() {
	// return getNodeS( (Instance) instanceChain.lastElement() );
	// }

	// get node S (approximates internal state) for the given instance (part of
	// the instance chain)
	// ** 3rd August 2005 ** commented out as not used; always tend to call
	// versions that fix extra leaves to the tree as required.
	// private TreeNode getNodeS(Instance instance) {
	// return getNodeS( instance, false ); // the addition of leaves not allowed
	// by default
	// }

	// get node S based on the current instance, allowing for leaves to be added
	// to the tree
	private TreeNode getNodeS(boolean addLeavesToTree) {
		return getNodeS((Instance) instanceChain.lastElement(), addLeavesToTree);
	}

	// get node S for the given instance
	// boolean flag to indicate whether leaves can be added if parameter value
	// doesn't match existing values
	private TreeNode getNodeS(Instance instance, boolean addLeavesToTree) {
		TreeNode nodeS = tree; // start at top node of tree
		while (!nodeS.isLeaf()) {
			ParameterSelected selector = (ParameterSelected) nodeS.parameterToSplitOn;
			// the following is correct as instances stored in a node 's' of the
			// tree
			// represent the outgoing transitions [McCallum rlkit0.3.3], i.e. if
			// T_t
			// is stored in 's', where T_t = < T_{t-1}, a_{t-1}. o_t, r_t >, the
			// action a_{t-1} is the action executed in 's' and o_t and r_t the
			// observation and reward obtained leaving 's'.
			// In this method we are given T_t and we are asked to to find the
			// destination
			// state where we currently are and *not* the state where T_t is
			// stored.
			Object parameterValue = selector.getParameterValue(instance,
					instanceChain);
			// selector.getParameterValue returns 'null' if instance not in
			// instance chain
			// this will cause getSubTreeNode to throw a NullPointerExecption,
			// however this
			// should never happen as new instance always added to
			// instanceChain, and chain
			// is not truncated.
			TreeNode newNodeS = nodeS.getSubTreeNode(parameterValue);
			// if newNodeS = null, then parameter value not encountered before
			// add a new subTreeNode if you're allowed to, else stop dead
			// (something is wrong!)
			if (newNodeS == null) {
				if (addLeavesToTree) {
					// create leaf newNodeS
					newNodeS = new TreeNode(new UTreeLeafData());
					nodeS.addSubTreeNode(parameterValue, newNodeS);
					// * just need to create an empty node; instances that would
					// be stored *
					// * in this node are the instances relating to the next
					// state s(T+1), not *
					// * this instance which is stored else where. *
					// ((UTreeLeafData) newNodeS.data).addInstanceIndex(
					// instanceChain.indexOf(instance) );
					// Hashtable newActionValues = ((UTreeLeafData)
					// newNodeS.data).actionValues;
					// Vector values = new Vector();
					// newActionValues.put(instance.actionName(),values);
					// values.add( new Double(instance.reward().doubleValue())
					// ); // initial Q value equal to reward
					// values.add(new ArithmeticInteger(1)); // init. number of
					// entries (N)
					// values.add(new ArithmeticFloat(instance.reward())); //
					// init. sum of rewards (sum r)
					// values.add(new Hashtable()); //init. table of transition
					// probabilities
				}
				else {
					System.err
							.println("Tree Node: "
									+ nodeS
									+ "/nParameter Value: "
									+ parameterValue
									+ " not seen before.\nNo permission to add new leaf.");
					System.exit(1); // halt on error
				}
			}
			nodeS = newNodeS;
		} // end of while (!nodeS.isleaf())
		return nodeS;
	}

	// look up state action value for given 'sa'.
	// As the state 's' is the leaf node, can obtain action values from
	// the data stored in the give state/leaf node object.
	protected Double lookupStateActionValue(StateActionPair sa) {
		TreeNode sNode = (TreeNode) sa.state.observations[0];
		String actionName = sa.actionName;
		// each leaf node contains an Object Array which contains a Vector
		// of instances (T's) and a Hashtable of actions and associated values
		Hashtable actionValues = ((UTreeLeafData) sNode.data).actionValues;
		// each hashtable entry contains a vector the first element of which is
		// the Q value
		Vector values = ((Vector) actionValues.get(actionName));
		Double saValue = new Double(0.0); // return 0 by default if sa doesn't
										// exist - see setStateActionValue below.
		if (values != null) {
			saValue = (Double) values.firstElement();
		}
		return saValue;
	}

	protected void setStateActionValue(StateActionPair sa, Double value) {
		// there's a problem here that if a perception that has
		// not been encountered before is seen during evaluation
		// then the parent and evaluation copies trees will differ
		// and setStateActionValue can not reflect the initialized
		// action-value generated during evaluation back to the parent
		// as it can't easily match the sNode passed in as the state
		// to this method (sa.state == sNode) back onto the parent's
		// tree

		// we need a function here that gets sNode (for this tree)
		// given an sNode; i.e. if sNode exists in this tree then
		// return it, if sNode doesn't find/create an equivalent object
		// for this tree.

		// ** The above is do able, but there's a further issue
		// that when an single sweep of dynamic programming is done
		// the Q-value for any action initiated here that has no
		// associated instances will be reset to zero as the
		// sumR (sum of rewards) is zero and number of transitions
		// are zero, so weighted sum of utility values (sumPrU in
		// main loop) is zero. **

		// Because of the above issues I'm only allowing state
		// action values to be initiated to zero; i.e. unknown
		// 'new' state-action pairs reported as having value zero by
		// lookupStateActionValue(sa) above.

		System.err.println("Call to setStateActionValue(.) method in UTree\n"
				+ "This shouldn't happen.");
		System.exit(1);
	}

	// create tables required from scratch when no filename give
	// overwrites method in ReallyBasicStateActionLearner to add instanceChain
	// and tree
	protected void createBlankTables() {
		instanceChain = new InstanceChain(10000, 1000); // stores the instances
														// seen;
		// each instance is a tuple of action, observation and reward
		// initiated with 10,000 entries, and capacity increment of 1000
		// to hopefully speed things up
		// tree used to define states; initially a leaf containing an empty
		// object array
		tree = new TreeNode(new UTreeLeafData());
	}

	// read file - read in the data you need
	// overwrites method in ReallyBasicStateActionLearner to read in
	// instanceChain & tree
	protected void readFile(ObjectInputStream ois) throws Exception {
		super.readFile(ois); // read in actionSteps & runsCompleted
		instanceChain = (InstanceChain) ois.readObject();
		tree = (TreeNode) ois.readObject();
	}

	// write file - write out the data to be save
	// overwrites method in ReallyBasicStateActionLearner to write out
	// instanceChain & tree
	protected void writeFile(ObjectOutputStream oos) throws IOException {
		super.writeFile(oos); // write out actionSteps & runsCompleted
		oos.writeObject(instanceChain);
		oos.writeObject(tree);
	}

	// file names to save UTree trees and instance chains in
	protected String knowledgeFileName() {
		return ("UTree " + knowledgeFileNameParameters());
	}

	protected String knowledgeFileNameParameters() {
		return ("k" + kSteps + " fringe" + minFringeDepth + "-"
				+ maxFringeDepth + " maxH" + maxHistory + " KSP"
				+ ksProbability + (subDividePerceptions ? " sub " : " whole ")
				+ actionsToCompare + " " + super.knowledgeFileNameParameters());
	}

	public void setActionSelection(ActionSelection as){
		this.actionSelection = as;
	}
	
	public void showResult(){
	    System.out.println(tree.toString());
		System.out.println("total tree leaves: " + tree.allLeaves().size()); 
	}
}
