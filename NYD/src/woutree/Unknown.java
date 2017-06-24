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
 * Unknown class
 *
 * just a token returned by ParameterSelected class
 * when history index extends beyond start of
 * instance chain.  Returned in preference to null
 * as not possible to use null as key in Hashtable
 * or as key to subTreeNodes in TreeNode
 *
 * P.A.Crook, 12th February 2004
 *
 **/

class Unknown implements Serializable {

//equals
 public boolean equals(Object obj) {
 	boolean sameType = false;
	if((obj != null) && (obj.getClass() == this.getClass()))
		sameType = true; //preference for getClass and not instanceof because
   // getClass excludes subclass be seen as equals,
   // (see http://www.geocities.com/technofundo/tech/java/equalhash.html).
   return sameType;
 }
 
 //hashCode - equal objects must return the same hashcode; 
 // but doesn't have to be unique, i.e. doesn't matter if it matches non equal objects' hashcodes
 public int hashCode() {
 	return 22101;
 }
 
 //toString
 public String toString() {
 	return this.getClass().getName();
 }
}
