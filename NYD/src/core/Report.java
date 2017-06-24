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



import java.util.*; //for Vector

/**
 * Report on each action taken by the agent.
 * 
 * P.A.Crook, December 2002
 * 
 * 8th August 2003 added StateActionPair
 * 15th July 2005 changed finalX, finalY and constructor x,y to float from int
 *                 to accommodate reports from Webots Agents.
 * 
 */

public class Report implements Cloneable {

	public Action actionSelected;

	public StateActionPair stateActionPair;

	public boolean succeeded;

	public Float rewardReturned;

	public float finalX, finalY;

	public Orientate finalFace;

	public Vector observationsMade;

	//main constructor
	public Report(Action act, StateActionPair sa, boolean okay, Float reward, float x,
			float y, Orientate face, Vector observations) {
		actionSelected = act;
		stateActionPair = sa;
		succeeded = okay;
		rewardReturned = reward;
		finalX = x;
		finalY = y;
		finalFace = face;
		//if observations not null get a copy of the current values
		if (observations != null)
			observationsMade = (Vector) observations.clone();
	}
	
	//constructor with no arguments
	public Report() {
		this(null, null, false, null, -1, -1, null, null);
	}

	//clone this report
	public Object clone() {
		Report aClone;
		try {
			aClone = (Report) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
		if (observationsMade != null)
			aClone.observationsMade = (Vector) observationsMade.clone();
		return aClone;
	}

	//toString - output report contents as a string
	public String toString() {
		String report;
		if (actionSelected == null) { // report starting position (if known)
			if (finalX == -1 && finalY == -1 && finalFace == null)
				report = "Agent Started.";
			else
				report = "Agent started at (" + Strings.truncateFloat(finalX,2) +
							"," + Strings.truncateFloat(finalY,2) + ") " +
							finalFace + ".";
		} else { // report actions
			report = actionSelected.toString() + (succeeded ? " " : ",");
			while (report.length() < 10) {
				report = report + " ";
			}
			if (succeeded)
				report = report + " executed, agent location (" + 
							Strings.truncateFloat(finalX,2) + "," +
							Strings.truncateFloat(finalY,2) + ") " + finalFace + ".";
			else
				report = report + " way is blocked.";
		}
		if (rewardReturned != null)
			report = report + "  Reward of " + rewardReturned + " received.";
		return report;
	}

}
