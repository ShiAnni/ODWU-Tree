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


import java.util.*; // for observer and observable
import java.io.*; // for FilwWriter

/**
 *
 * StatsWriter
 * -----------
 *
 * Implements a stats writer that writes out evaluation data to the
 * given file.
 *
 * Note: implemented using Model-View-Control design pattern, of
 * which this is just one Viewer.
 *
 * Paul A. Crook, 19th March 2003
 *
 **/

public class StatsWriter extends GridWorldObserver {

    private String fileName;
    private boolean printHeader;
    
    // Constructor for StatsWriter - pass in file name to write data to.
    public StatsWriter(String fileName) {
	this.fileName = fileName;
	// initiate file
	try {
	    PrintWriter out = new PrintWriter( new BufferedWriter(
				               new FileWriter(fileName) ) );
	    out.close();
	}
	catch (IOException e) {
	    System.err.println("StatsWriter: " + e);
	}
	printHeader = true;
    }


    //use inherited register method


    //update
    public void update(Observable model, Object data) {
	Stats stats = ((Dossier) data).evaluationStatistics;
	if (stats != null) {
	    try {
		// FileWriter, append = 'true' (append if file exists)
		PrintWriter out = new PrintWriter( new BufferedWriter(
				       new FileWriter(fileName, true) ) );
		
		if (printHeader) {
		    out.println("Actions_When_Evaluated\tPerceptual_Steps\t" +
		    		"Physical_Steps\tTotal_Steps\tGoal_Reached\t" +
		    		"Total_Starts\tCumulative_Reward\t" + 
					stats.listOfActnLearnArgs +
					"\tNum_Stochastic_Policy_States\tStochastic_Policy_States");
		    printHeader = false;
		}

		out.println( stats.learningStepsCompleted + "\t" +
			     stats.perceptualStepsDuringEvaluation + "\t" +
			     stats.physicalStepsDuringEvaluation + "\t" +
			     stats.totalStepsDuringEvaluation + "\t" +
			     stats.numberOfTimesGoalReached + "\t" +
			     stats.totalStarts + "\t" +
			     stats.cumulativeReward + "\t" + 
			     stats.valueOfActnLearnArgs + "\t" +
				 stats.stochasticPolicyStates.size() + "\t" +
				 stats.stochasticPolicyStates );
		
		out.close();
	    }
	    catch (IOException e) {
		System.err.println("StatsWriter: " + e);
	    }
	}
    }
    
}




