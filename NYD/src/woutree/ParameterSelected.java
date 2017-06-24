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


import java.io.*; // for Serializable

/**
 * Parameter Selected class - objects of this class specify what parameter value
 * of an Instance Chain to obtain.
 * 
 * The getParameterValue(.) method returns the parameter values (observation or
 * action) for the instance which is inquired after. THIS IS CORRECT. We need
 * this find our current node 's' *given* an instance 'T'. Given that the
 * instances stored in each node are the *outgoing* instances [McCallum's code
 * rlkit0.3.3], when given an instance T and asked for 's' we need to find T's
 * observation/action to find out which 's' we have gone to.
 * 
 * Paul A. Crook, 4th January 2004
 * 
 */

class ParameterSelected implements Serializable {

	public int historyIndex, dimension;
	private boolean subDivideObservations;
	
	private String[] perception_dimensions = 
		{
				"ACTION",
				"HEAR HORN",				"GAZE_OBJECT",
				"GAZE_SIDE",				"GAZE_DIRECTION",
				"GAZE_SPEED",				"GAZE_DISTANCE",
				"GAZE_REFINED_DISTANCE",	"GAZE_COLOR",
		};

	// constructor
	ParameterSelected(int historyIndex, int dimension, boolean subDivideObservations) {
		// sanity check on values
		if (historyIndex < 0 || dimension < 0)
			throw new NumberFormatException();
		this.historyIndex = historyIndex; // 0 for 'latest' instance, 1 for 1
		// step ago, etc.
		this.dimension = dimension;
		this.subDivideObservations = subDivideObservations;
	}

	// get the value of this parameter for the *current* instance,
	// i.e. start at the last instance in the instance chain
	public Object getParameterValue(InstanceChain instanceChain) {
		return getParameterValue(instanceChain.size() - 1, instanceChain);
	}

	// get the value of this parameter from the instance chain,
	// starting at the given instance's location in the chain
	public Object getParameterValue(Instance instance,
			InstanceChain instanceChain) {
		return getParameterValue(instanceChain.indexOf(instance), instanceChain);
		// if instance not in chain indexOf will return -1
		// this will result in an ArrayIndexOutOfBoundsException being raised
	}

	// get the value of this parameter from the instance chain,
	// starting at the instance in the chain indicated by index
	public Object getParameterValue(int index, InstanceChain instanceChain) {
		if (index < 0)
			throw new ArrayIndexOutOfBoundsException();
		Object parameterValue = new Unknown(); // return Unknown object if
		// index-history beyond
		// start of chain (NB return Unknown as null
		// can't be used as a key in TreeNode branches)
		Instance instance = instanceChain.getInstance(index - historyIndex);
		if (instance != null)
			parameterValue = instance.dimension(dimension, subDivideObservations);
		return parameterValue;
	}

	// equals
	public boolean equals(Object obj) {
		boolean same = false;
		// preference for getClass and not instanceof because
		// getClass excludes subclass be seen as equals,
		// (see http://www.geocities.com/technofundo/tech/java/equalhash.html).
		if ((obj != null)
				&& (obj.getClass() == this.getClass())
				&& (this.historyIndex == ((ParameterSelected) obj).historyIndex)
				&& (this.dimension == ((ParameterSelected) obj).dimension)) {
			same = true;
		}
		return same;
	}

	// hashCode - equal objects must return the same hash code;
	// but doesn't have to be unique, i.e. doesn't matter if it matches non
	// equal objects' hash codes
	public int hashCode() {
		return 14356 + historyIndex * 373 + dimension; 
		// return some arbitrary number related to historyIndex and dimension
	}

	// toString
	public String toString() {
		return (Instance.dimensionName(dimension, subDivideObservations) + "\\n" + historyIndex + ((historyIndex == 1) ? " step ago."
				: " steps ago."));
	}
	
	public String dimension_name(){
		return perception_dimensions[dimension];
	}
}
