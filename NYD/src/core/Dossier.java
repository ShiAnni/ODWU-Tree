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


import java.util.*; //Vector

/**
 * Dossier - collection of `Reports' on the actions taken by the agent.
 *           extends vector by adding an object to store evaluation paths
 *           and restricts add to Report objects
 *
 * P.A.Crook, 3rd March 2003
 **/

public class Dossier extends Vector{

  public Object[] paths; // paths agent took during evaluation
  public String freeFormReport; //free form stuff for text viewer to display
  public Stats  evaluationStatistics; //store stats. from evaluation

  //main constructor
  Dossier() {
    super();
    clear(); //set all to null
  }

  //add reports to the dossier
  public void add(Report report) {
    super.add(report);
  }

  //clear - overwrite Vector clear method to clear path and freeFormReport
  public void clear() {
    super.clear(); //clear Vector part
    paths = null;
    freeFormReport = null;
    evaluationStatistics = null;
  }
    
  //clone the dossier
  public Object clone() {
    Dossier aClone = (Dossier) super.clone(); //calls Vector's clone method
    if (paths != null) 
      aClone.paths = (Object[]) paths.clone();
    if (evaluationStatistics != null) 
      aClone.evaluationStatistics = (Stats) evaluationStatistics.clone();
    return aClone;
  } //try and catch not required as CloneNotSupportedException not thrown by Vector.

  //toString - return reports with new line between each
  public String toString() {
    String reportList = "";
    for(int i = 0; i < super.elementCount; i++) {
      reportList = reportList + super.get(i).toString() + "\n";
    }
    if (evaluationStatistics != null)
      reportList = reportList + "\n" + evaluationStatistics + "\n";
    if (freeFormReport != null)
      reportList = reportList + freeFormReport + "\n";
    return reportList;
  }

}

