package woutree;
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


import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;

import core.Agent;
import nyd.TruckAgent;

/**
 * 
 * Leaf Expansion Generator
 * 
 * Replaces iterative method in UTree that generated a list of *all* possible
 * expansion of a given leaf. This generator return one expansion at a time and
 * thus avoid the processing time overhead of generating all expansion straight
 * off, and also avoid the substantial memory overhead of storing them.
 * 
 * 
 * Each distinction is ranked in order to enforce an order on the elements of
 * expansion. This stops duplication of expansions and ordering of elements is
 * not important when considering alternate fringes. The ordering is enforced by
 * the while loop in hasNextExpansion() which adds elements to make up the
 * length of the expansion, and also by how nextElement(...) generates elements.
 * 
 * Created on 14th August 2005 by Paul A. Crook
 * 
 */
@SuppressWarnings({"unchecked","rawtypes"})
public class LeafExpansionGenerator {

	private Vector expansion, conjunctionOfParametersAtLeaf;

	private int maxDepth, maxHistory, currentDepth, max_order_observation;

	private Agent agent;
	
	private Hashtable<String,Integer> observationRecord;
	private Hashtable observationReward;
	private Hashtable indexTable;
	private int[] observation_order = new int[9];
	private int[] reverse_observation_order = new int[9];

	private boolean subDividePerceptions;

	// constructor; initiate generator with a conjunction of parameters to split
	// on that describes the leaf. This is used to avoid repeating existing
	// conjunctions when generating expansions.
	public LeafExpansionGenerator(Vector conjunctionOfParametersAtLeaf,
			int maxDepth, int maxHistory, int max_order_observation, Agent agent,
			boolean subDividePerceptions,Hashtable<String,Integer> observationRecord,
			Hashtable observationReward) {
		this.conjunctionOfParametersAtLeaf = conjunctionOfParametersAtLeaf;
		this.maxDepth = maxDepth;
		this.maxHistory = maxHistory;
		this.max_order_observation = max_order_observation;
		this.agent = agent;
		this.subDividePerceptions = subDividePerceptions;
		expansion = new Vector(); // initiate starting point
		expansion.add(null);
		currentDepth = 1;
		this.observationRecord=observationRecord;
		this.observationReward=observationReward;
		for (int i = 0; i < observation_order.length; i++) {
			observation_order[i]=-1;
			reverse_observation_order[i]=-1;
		}
		this.indexTable = ((TruckAgent)agent).getPerceptAndActionIndex();
		Queue<DimensionKeyWeightPair> priorityQueue = new PriorityQueue<>(new Comparator<DimensionKeyWeightPair>() {
			@Override
			public int compare(DimensionKeyWeightPair o1, DimensionKeyWeightPair o2) {
				return (int) (o2.weight-o1.weight);
			}
		});
		int totalObservations = 0;
		for (Iterator iterator = observationReward.keySet().iterator(); iterator.hasNext();) {
			String dimension_key = (String) iterator.next();
			int occur_num = observationRecord.get(dimension_key);
			totalObservations+=occur_num;
		}
		for (Iterator iterator = observationReward.keySet().iterator(); iterator.hasNext();) {
			String dimension_key = (String) iterator.next();
			Float avgreward = (Float) observationReward.get(dimension_key);
			int occur_num = observationRecord.get(dimension_key);
			Double weight = Math.abs(avgreward)*1.0*totalObservations/occur_num;
			DimensionKeyWeightPair pair = new DimensionKeyWeightPair(dimension_key,weight);
			priorityQueue.add(pair);
		}
		DimensionKeyWeightPair pair = null;
		int order=0;
		while((pair = priorityQueue.poll())!=null){
			int index = (int) indexTable.get(pair.dimensionKey);
			if(reverse_observation_order[index]==-1){
				observation_order[order]=index;
				reverse_observation_order[index]=order;
				++order;
			}
        }
		/*System.out.print("{");
		for (int i : observation_order) {
			System.out.print(i+",");
		}
		System.out.println("}");*/
	}
	
	class DimensionKeyWeightPair{
		String dimensionKey;
		Double weight;
		
		public DimensionKeyWeightPair(String dimensionKey, Double weight) {
			this.dimensionKey = dimensionKey;
			this.weight = weight;
		}
	}

	// hasNextExpansion(); return true if it can generate another expansion.
	// Mimics a breadth 1st search to return shortest first. Stops when
	// combinations available, given existing leaf, maxDepth and maxHistory,
	// is exhausted.
	public boolean hasNextExpansion() {
		
		ParameterSelected lastElement;

		do {

			do {
				lastElement = (ParameterSelected) expansion.lastElement();
				expansion.remove(lastElement);
				lastElement = nextElement(lastElement);
				if (lastElement != null)
					expansion.add(lastElement);
			} while (lastElement == null && !expansion.isEmpty());

			if (lastElement == null && expansion.isEmpty()) {
				currentDepth++;
				if (currentDepth > maxDepth)
					return false; // all over
				else {
					lastElement = nextElement(null);
					if (lastElement != null)
						expansion.add(lastElement);
				}
			}

			// if expansion is too short the add additional elements
			while (expansion.size() < currentDepth && lastElement != null) {
				// use previous element in expansion to get next element
				// in order to enforce ranking.
				lastElement = nextElement(lastElement);
				if (lastElement != null)
					expansion.add(lastElement);
			}

		} while (lastElement == null);// end while loop;
		// if lastElement == null ran out of 'next elements' while
		// completing extension.  Give up on this extension and try
		// next.

		return true;
	}

	// generate next element that follows on from the last element.
	// rank of elements as described by McCallum; enforced by increments
	private ParameterSelected nextElement(ParameterSelected lastElement) {

		ParameterSelected newElement = null;

		int d = -1; // will be incremented to 0 by d++ below.
		int h = 0;

		if (lastElement != null) {
			d = reverse_observation_order[lastElement.dimension];
			h = lastElement.historyIndex;
		}

		// loop until element generated isn't part of the
		// conjunction of parameters that describes the leaf
		do {
			// try next perceptual dimension
			d++;
			if (d >= max_order_observation) {
				// if number of perceptual dimensions is exceed
				// then try increase history index and reset
				// perceptual dimension
				h++;
				d = 0;
				// if maxHistory reached then give up
				if (h > maxHistory)
					return null;
			}
			newElement = new ParameterSelected(h, observation_order[d], subDividePerceptions);
		} while (conjunctionOfParametersAtLeaf.contains(newElement));

		return newElement;
	}

	// getNextExpansion(); returns expansion generated by last call to
	// hasNextExpansion
	public Vector getNextExpansion() {
		return expansion;
	}

	// nextExpansionLength; length of next expansion
	public int nextExpansionLength() {
		return expansion.size();
	}
	
	// number of perceptual dimensions
	private int numberPerceptualDimentions() {
		int number = 2; // default unless perceptions can be sub-divided is
		// two dimensions, one for observations and one for actions
		if (subDividePerceptions)
			number = agent.numberOfObservationDimensions().intValue() + 1;
		// plus one for actions
		return number;
	}

}
