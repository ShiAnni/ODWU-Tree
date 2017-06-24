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
 * orientation Classes, deal nicely with orientation 
 *
 * Paul A. Crook, November 2002
 *
 * Position object used as State by absolute position agent - therefore required to be Serializable
 *
 **/

public abstract class Orientate implements Serializable {
  
    //returns direction facing as a value - angle from North in radians
    abstract public double value();

    //toString - say what you are nicely
    public String toString() {
	return getClass().getName();
    }
    
    //equals
    public boolean equals(Object obj) {
	if(this == obj) //same instance
	    return true;
	if((obj == null) || (obj.getClass() != this.getClass()))
	    return false; //preference for getClass and not instanceof because
	// getClass excludes subclass be seen as equals,
	// (see http://www.geocities.com/technofundo/tech/java/equalhash.html).
	// Same class of object, i.e. both FacingNorth, so return true.
	return true;
    }

    //hashCode - a bit of a hack but not too important
    public int hashCode() {
	return 0;
    }

}


//////////////////////////////////

class FacingNorth extends Orientate {
    public double value() {return 0;}}

//////////////////////////////////

class FacingSouth extends Orientate {
    public double value() {return Math.PI;}}

//////////////////////////////////

class FacingEast extends Orientate {
    public double value() {return Math.PI/2;}}

//////////////////////////////////

class FacingWest extends Orientate {
    public double value() {return 3/2*Math.PI;}}

//////////////////////////////////



