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
 * This is a abstract class for the two action classes that I define for the
 * agents to use; i.e. physical actions and perceptual actions
 * 
 * P.A. Crook 30th October 2002
 * 
 */

abstract public class Action {

	// toString, say what you are nicely
	public String toString() {
		return getClass().getName();
	}

	// how to refer to this kind of action
	abstract public Object reference();

	// actionName; name used in state action tables, etc.
	// This method should only be over written in special cases
	// where one class is used to represent several different actions
	// (the assumed default is a separate class is created for each
	// action type).
	public String actionName() {
		return getClass().getName();
	}

}
