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
 * Action token for moving and manipulating a memory bit
 * 
 * Created on Aug 9, 2005 by Paul A. Crook
 *
 **/

public class MoveAndFlipBitAction extends PhysicalAction {

	private PhysicalAction action;
	private int memoryBitNumber;
	
	public MoveAndFlipBitAction(PhysicalAction action, int bit) {
		this.action = action;
		this.memoryBitNumber = bit;
	}

	public PhysicalAction actionToken() {
		return action;
	}
	
	public int bitToFlip() {
		return memoryBitNumber;
	}
	
	public Object reference() {
		return new Object[] {("MoveAndFlipBit" + memoryBitNumber), action.reference()};
	}

	public String toString() {
		return (action.toString() + "FlipBit" + memoryBitNumber);
	}
	
	// over written as this class represents multiple actions
	public String actionName() {
		return (action.toString() + "FlipBit" + memoryBitNumber);
	}
}
