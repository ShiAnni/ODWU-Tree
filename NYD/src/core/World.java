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



/**
 *
 * This is a abstract class for all the Worlds
 * that the agents operate in
 *
 * P.A. Crook
 * 30th October 2002
 *
 **/

abstract public class World implements Cloneable {

  /** common variables **/

  public int worldSizeX, worldSizeY;
  public Orientate[] availableOrientations;

  /** common methods **/

  //Say which world you are.  String returned is a bit long for
  //filenames - I expect most worlds to over write this method.
  public String toString() {
    return getClass().getName();
  }

  // Clone the world
  public Object clone() {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException e) {
      throw new InternalError(e.toString());
    }
  }

  /** abstract methods (i.e. to defined (differently) by each sub-class) **/
  
  abstract public void init(); //initialize world
  abstract public void init(int x, int y); //initialize world
  

  abstract public PhysicalAction[] physicalActions(); //return an
                                                      //array of possible
                                                      //physical actions
  
  abstract public String[] observation();

  abstract public Float executeAction(PhysicalAction act, State state); //carry out action
                               //and return reward.  State included for reporting purposes.


  abstract public Report report(); //send back a report on the state of the world

  

}









