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



import java.util.Vector;
import java.util.Collections;

/**
 * Stats - stores agent's performance as reported by evaluation
 * 
 * P.A.Crook, 19th March 2003
 */

public class Stats implements Cloneable {

	public int learningStepsCompleted;

	public int perceptualStepsDuringEvaluation;

	public int physicalStepsDuringEvaluation;

	public int totalStepsDuringEvaluation;

	public int numberOfTimesGoalReached;

	public int totalStarts;

	public float cumulativeReward;

	public String listOfActnLearnArgs;

	public String valueOfActnLearnArgs;

	public Vector stochasticPolicyStates;

	//constructor
	Stats(int actionSteps, int perceptual, int physical, int total, int goals,
			int starts, float totalReward, String listOfArgs,
			String valueOfArgs, Vector stochasticStatesFollowingPolicy) {
		learningStepsCompleted = actionSteps;
		perceptualStepsDuringEvaluation = perceptual;
		physicalStepsDuringEvaluation = physical;
		totalStepsDuringEvaluation = total;
		numberOfTimesGoalReached = goals;
		totalStarts = starts;
		cumulativeReward = totalReward;
		listOfActnLearnArgs = listOfArgs;
		valueOfActnLearnArgs = valueOfArgs;
		stochasticPolicyStates = stochasticStatesFollowingPolicy;
		if (stochasticPolicyStates != null) {
			Collections.sort(stochasticPolicyStates);
		}
	}

	//toString
	public String toString() {
		return ("evaluation@" + learningStepsCompleted
				+ " act'n'learn steps:\n" + " - perceptual  : "
				+ perceptualStepsDuringEvaluation + "\n" + " - physical    : "
				+ physicalStepsDuringEvaluation + "\n" + " - totalSteps  : "
				+ totalStepsDuringEvaluation + "\n" + " - goals/starts: "
				+ numberOfTimesGoalReached + "/" + totalStarts + "\n"
				+ " - cum. Reward : " + cumulativeReward + "\n"
				+ " - stochastic policy states: "
				+ stochasticPolicyStates.size() + " " + stochasticPolicyStates
				+ "\n" + listOfActnLearnArgs + "\n" + valueOfActnLearnArgs + "\n");
	}

	//clone Stats.
	public Object clone() {
		try {
			Stats aClone = (Stats) super.clone();
			if (stochasticPolicyStates != null)
				aClone.stochasticPolicyStates = (Vector) stochasticPolicyStates
						.clone();
			return aClone;
		} catch (CloneNotSupportedException e) {
			throw new InternalError(e.toString());
		}
	}

}
