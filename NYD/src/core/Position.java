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
 *
 * Position Class
 * 
 * Store X, Y coordinates and Orientation in a single object.
 *
 * Paul A. Crook, 4th December 2002
 *
 * used as State by absolute position agent - therefore required to be Serializable
 *
 **/

class Position implements Serializable {

    public int x,y;
    public Orientate o;

    //constructor
    Position(int x, int y, Orientate o) {
	this.x = x;
	this.y = y;
	this.o = o;
    }

    //equals
    public boolean equals(Object obj) {
	if(this == obj) //same instance
	    return true;
	if((obj == null) || (obj.getClass() != this.getClass()))
	    return false; //preference for getClass and not instanceof because
	// getClass excludes subclass be seen as equals,
	// (see http://www.geocities.com/technofundo/tech/java/equalhash.html).
	// object must be Position at this point
	return ( ((Position) obj).x == x && ((Position) obj).y == y &&
		 ((Position) obj).o.equals(o));
    }
    
    //hashCode - start with arbitary number, say 4, multiply by 31 add
    //next variable and repeat.
    public int hashCode() {
	return ( 119164 + 961 * x + 31 * y + o.hashCode() );
    }
    
    //toString
    public String toString() {
	return ( "(" + x + "," + y + ") " + o );
    }

}
