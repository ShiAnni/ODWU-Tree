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


import java.util.Hashtable;
import java.util.Observable;
import java.util.Vector;

/**
 *
 * PathSummary
 * -----------
 *
 * summarises the paths taken by the current policy,
 * showing only those states which have preference for the type
 * of actions specified in the argument passed to this viewer
 *
 * Note: implemented using Model-View-Control design pattern, of
 * which this is just one Viewer.
 *
 * Paul A. Crook, 10th July 2002
 *
 **/

public class PathSummary extends GridWorldObserver {

    private String regex;
    
    //constructor
    PathSummary(String arg) {
	regex = arg;
    }
  
    //uses GridWorldObserver register method


    //when passed a dossier contain path information then summarise it to stdout
    public void update(Observable model, Object data) {
	if ( ((Dossier) data).paths != null ) {
	    for (int i = 0; i < ((Vector) ((Dossier) data).paths[0]).size(); i++)
		{
		    //output squares number
		    String outString = (i+1) + "/";
		    //get squares co-ordinates
		    Position sqPos = (Position) ((Vector) ((Dossier) data).paths[0]).get(i);
		    Coordinates sqCoords = new Coordinates(sqPos.x, sqPos.y);
		    //use co-ordinates to paths and state-actions for that sq.
		    Object[] pathsAndSAs = (Object[]) ((Hashtable) ((Dossier) data).paths[1]).get(sqCoords);
		    if (pathsAndSAs != null) {
			Vector listSAs = (Vector) pathsAndSAs[1];
			for (int j = 0; j < listSAs.size(); j++) {
			    StateActionPair sa = (StateActionPair) listSAs.get(j);
			    outString = outString + sa.state + sa.actionName + ",";
			}
		    }
		    if (outString.matches(regex)) {
			System.out.print( outString.substring(0,outString.length()-1) + " " );
		    }
		}
	    System.out.println();
	}
    }
    
}



