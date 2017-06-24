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


import java.io.Serializable;

/**
 * Goal class
 * 
 * A token returned by World.observation() when goals are visible. Used by
 * Wilson's Woods 7
 * 
 * P.A.Crook, 22nd February 2005
 *  
 */

class Goal implements Serializable {

	//	equals
	public boolean equals(Object obj) {
		boolean sameType = false;
		if ((obj != null) && (obj.getClass() == this.getClass()))
			sameType = true; //preference for getClass and not instanceof
							 // because
		// getClass excludes subclass be seen as equals,
		// (see http://www.geocities.com/technofundo/tech/java/equalhash.html).
		return sameType;
	}

	//hashCode - equal objects must return the same hashcode;
	// but doesn't have to be unique, i.e. doesn't matter if it matches non
	// equal objects' hashcodes
	public int hashCode() {
		return 11119;
	}

	//toString
	public String toString() {
		return this.getClass().getName();
	}
}
