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

/*
 * State class
 * 
 * Holder for the observations that make up the agent's state. Implements a
 * sensible equals and hashCode methods for State.
 * 
 * P.A.Crook 11th November 2002
 *  
 */

public class State implements Comparable, Serializable {

	public Object[] observations; //store observations that make up this state

	//constructor
	public State(Object[] observations) {
		this.observations = observations;
	}

	//equals method
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false; //preference for getClass and not instanceof because
		// getClass excludes subclass be seen as equals,
		// (see http://www.geocities.com/technofundo/tech/java/equalhash.html).
		// object must be State at this point
		if (((State) obj).observations.length != this.observations.length)
			return false;
		boolean same = true;
		for (int i = 0; same && i < this.observations.length; i++) {
			if (!((State) obj).observations[i].equals(this.observations[i]))
				same = false;
		}
		return same;
	}

	//hashCode method
	public int hashCode() {
		int hash = 0;
		if (observations == null) {
			System.err
					.print("Shouldn't have null observations in State class.");
			System.exit(1);
		} else {
			for (int i = 0; i < observations.length; i++) {
				if (observations[i] == null) {
					System.err
							.print("Shouldn't have null observations[i] in State class.");
					System.exit(1);
				} else {
					hash = 2 * hash + observations[i].hashCode();
				}
			}
		}
		return hash;
	}

	//Compare to another State and return <i>natural</i> ordering. In
	// this case, if binary representation exists sorted by that
	// otherwise compare by hashcode values.
	public int compareTo(Object o) {
		boolean thisBool = false;
		int thisBinary = 0;
		int thisBits = 0;
		for (int i = 0; i < observations.length; i++) {
			if (observations[i] instanceof Boolean) {
				thisBool = true;
				thisBinary = 2 * thisBinary
						+ (((Boolean) observations[i]).booleanValue() ? 1 : 0);
				thisBits++;
			} else if (observations[i] instanceof Goal) {
				thisBool = true;
				thisBinary = 2 * thisBinary; //keep position of binary elements
											 // correct
				thisBits++;
			}
		}

		boolean oBool = false;
		int oBinary = 0;
		int oBits = 0;
		for (int i = 0; i < ((State) o).observations.length; i++) {
			if (((State) o).observations[i] instanceof Boolean) {
				oBool = true;
				oBinary = 2
						* oBinary
						+ (((Boolean) ((State) o).observations[i])
								.booleanValue() ? 1 : 0);
				oBits++;
			} else if (((State) o).observations[i] instanceof Goal) {
				oBool = true;
				oBinary = 2 * oBinary; //keep position of binary elements
									   // correct
				oBits++;
			}
		}
		if (thisBool && oBool) {
			if (thisBits != oBits) return (thisBits - oBits);
			if (thisBinary != oBinary) return (thisBinary - oBinary);
		}
		return (hashCode() - ((State) o).hashCode());
	}

	//toString; too allow easy recognition:
	//  (i) for binary observations give value in decimal (with number of bits)
	// (ii) for Goals give position in bit string
	public String toString() {
		boolean bool = false;
		int binary = 0;
		int bits = 0;
		String string = "";
		for (int i = 0; i < observations.length; i++) {
			if (observations[i] instanceof Boolean) {
				bool = true;
				binary = 2 * binary
						+ (((Boolean) observations[i]).booleanValue() ? 1 : 0);
				bits++;
			} else if (observations[i] instanceof Goal) {
				bool = true;
				binary = 2 * binary; //keep position of binary elements correct
				bits++;
				string = string + "goal" + bits;
			} else
				string = string + observations[i] + " ";
		}
		if (bool) {
			if (string.equals(""))
				string = binary + "[" + bits + "]";
			else
				string = binary + "[" + bits + "] " + string;
		}
		return string;
	}

}
