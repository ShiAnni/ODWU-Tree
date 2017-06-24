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
 * Abstract class to the various ActionSelection routines available, i.e.
 * e-greedy, softmax, etc.
 *
 * Paul A. Crook 13th October 2003
 *
 **/

abstract public class ActionSelection implements Cloneable {

	// pointer to act'n'learn algorithm that called this routine
	protected ReallyBasicStateActionLearner actNlearn;

	// constructor
	ActionSelection(ReallyBasicStateActionLearner actNlearn) {
		this.actNlearn = actNlearn;
	}

	// core method - selects action using selected routine
	abstract protected Action selectActionFromList(State s, Action[] list, boolean explore);

	// report arguments such as epsilon, tau, etc...
	abstract public String listActionSelectionArgs();

	abstract public String currentValueActionSelectionArgs();

	// clone
	public Object clone(ReallyBasicStateActionLearner actNlearnClone) {
		ActionSelection aClone;
		try {
			aClone = (ActionSelection) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
		aClone.actNlearn = actNlearnClone;
		return aClone;
	}

}
