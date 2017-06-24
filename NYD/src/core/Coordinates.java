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
 * Coordinates Class
 * 
 * Store X,Y coordinates in a single object.
 * Calculate difference between two sets of coordinates.
 * Give bearing of coordinates from 0,0
 *
 * Paul A. Crook, 4th December 2002
 *
 **/

class Coordinates implements Serializable {

  public int x,y;

  //constructor
  Coordinates(int x, int y) {
    this.x = x;
    this.y = y;
  }

  //return 'this' plus 'a' in Coordinates
  public Coordinates plus(Coordinates a) {
    return new Coordinates( x + a.x, y + a.y );
  }
  
  //return 'this' minus 'a' in Coordinates
  public Coordinates minus(Coordinates a) {
    return new Coordinates( x - a.x, y - a.y );
  }

  //bearing (in radians)
  public double bearing() {
    return Math.atan2(x,-y); //minus y because y inc. downwards in grid word
  }

  //equals
  public boolean equals(Object obj) {
    if(this == obj) //same instance
      return true;
    if((obj == null) || (obj.getClass() != this.getClass()))
      return false; //preference for getClass and not instanceof because
    // getClass excludes subclass be seen as equals,
    // (see http://www.geocities.com/technofundo/tech/java/equalhash.html).
    // object must be Coordinates at this point
    return ( ((Coordinates) obj).x == x && ((Coordinates) obj).y == y );
  }
  
  //hashCode - start with arbitary number, say 3, multiply by 31 add
  //next variable and repeat.
  public int hashCode() {
      return (2883 + 31 * x + y);
  }
 
  //toString
  public String toString() {
  	return "(" + x + "," + y + ")";
  }
  
}
