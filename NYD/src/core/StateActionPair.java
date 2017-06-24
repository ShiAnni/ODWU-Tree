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


import java.io.*; // for Serializable

/**
 * StateActionPair class
 *
 * class created to allow States and Actions to be combined in a
 * single key and thus used to index Hashtables such as elgibility
 *
 * Paul A. Crook 27th November 2002
 */
public class StateActionPair implements Comparable, Serializable {
    public State state;
    public String actionName; //store name of action rather than
    //pointer to action.  This avoids Serializing everything, which is
    //otherwise necessary because SimplePhysicalAction has pointer to
	//world.


    public StateActionPair(State state, Action action) {
        this.state = state;
        this.actionName = action.actionName();
    }

    //constructor using just actionName
    public StateActionPair(State state, String actionName) {
        this.state = state;
        this.actionName = actionName;
    }

    //hashCode - need this to generate hashCodes that are the same for
    //a state action pair that represent the same data even if they
    //are different instances; don't need the hashcode to be unique to
    //each possible state action combination (thought it's preferable) as
    //long as the equals method is properly defined Hashtable will keep
    //different keys separate even if they have the same hash code.
    public int hashCode() {
        return ((31 * actionName.hashCode()) + state.hashCode());
    }

    //equals
    public boolean equals(Object obj) {
        if (this == obj) { //same instance
            return true;
        }

        if ((obj == null) || (obj.getClass() != this.getClass())) {
            return false; //preference for getClass and not instanceof because
            //              getClass excludes subclass being seen as equals,
            // (see http://www.geocities.com/technofundo/tech/java/equalhash.html).

        }
        // object must be StateActionPair at this point
        return (state.equals(((StateActionPair) obj).state) &&
        actionName.equals(((StateActionPair) obj).actionName));
    }

    //Compare to another StateActionPair and return <i>natural</i> ordering.  In
    //this case sorted 1st by state, then by actionName 
    public int compareTo(Object o) {
        int comparison = state.compareTo(((StateActionPair) o).state);

        if (comparison == 0) {
            comparison = actionName.compareTo(((StateActionPair) o).actionName);
        }

        return comparison;
    }

    //toString - print something nice
    public String toString() {
        return ("StateActionPair(" + state + "," + actionName + ")");
    }
}
