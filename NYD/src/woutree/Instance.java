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
import core.*;

/**
 * Instance class; defines how data is held for a give 'instance'
 *  -- see UTree algorithm
 *
 * P.A.Crook, 4th January 2004
 *
 **/

class Instance implements Serializable {

    private String actionName;
    private State  observation;
    private Float  reward;
    
    //constructors
    Instance(String actionName, State observation, Float reward) {
	this.actionName  = actionName;
	this.observation = observation;
	this.reward      = reward;
    }

    Instance(Action action, State observation, Float reward) {
	this.actionName  = action.toString();
	this.observation = observation;
	this.reward      = reward;
    }


    public Float reward() {
	return reward;
    }


    public String actionName() {
	return actionName;
    }


	public Object dimension(int index, boolean subDivideObservation) {
		Object dimension = null;
		switch (index) {
			case 0:
				dimension = actionName;
				break;
			default:
				if (subDivideObservation) {
					dimension = observation.observations[index-1];
				}
				else
					dimension = observation;
				break;
		}
		return dimension;
	}


	public static String dimensionName(int index, boolean subDivideObservation) {
		String name = null;
		switch(index) {
			case 0:
				name = "action";
				break;
			default:
				name = "observation";
				if (subDivideObservation) {
					name = name + (index-1);
				}
				break;
			}
		return name;
	}

    
    //display instance information nicely
    public String toString() {
	return( "(" + actionName + "," + 
		Strings.fixedLengthLeadingSpaces(observation,6) +
		"," + reward +")" );
    } 

}
