package woutree;
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


import java.util.*; // for Hashtable
import java.io.*; // for Serializable
import javax.swing.table.*; // for AbstractTableModel
import core.*;

/**
 *
 * UTree leaf data class
 *
 * Designed to store UTree data at the leaves of a TreeNode tree.
 * Created to replace Object[] so that the data instances can have
 * methods which nicely display the data.
 *
 * P.A.Crook 14th February 2004
 *
 * 3 August 2005; added getMaxQActionNames() return a list of 
 *                names of the action or actions that have
 *                the maximum Q value. i.e. would be policy
 *                actions if acting greedily.
 *
 * 15-16 August 2005; tried initiating Vector indexOfInstances
 *                    with capacity 1000 and increment 500, and
 *                    Hashtable actionValues with capacity 24,
 *                    to speed up fringe testing; but it only
 *                    slowed it down further; set back to
 *                    defaults.
 **/

public class UTreeLeafData extends AbstractTableModel implements Serializable {

    // index of instances is a list of indexes to instances in
    // the instance chain
    public Vector indexOfInstances;
    public Hashtable actionValues;
    private Vector columnNames;
    private Vector actionNames;
    
    //constructor
    //create an empty UTreeLeafData containing the basic structure
    public UTreeLeafData() {
	indexOfInstances = new Vector();
	actionValues     = new Hashtable();
    }


    public void addInstanceIndex(int index) {
	indexOfInstances.add(new Integer(index));
    }

    // return list action or actions that have max Q value 
   public Vector getMaxQActionNames() {
	   Vector listOfMaxQActions = new Vector();
	   Double actValue = null;
	   
	   for ( Enumeration e = actionValues.keys(); e.hasMoreElements(); ) {
		   String actionName = (String) e.nextElement();
		   Double temp = (Double) ( (Vector) actionValues.get(actionName) ).firstElement();
		   if (actValue == null || temp.compareTo(actValue) > 0) {
			   listOfMaxQActions.clear();
			   listOfMaxQActions.add(actionName);
			   actValue = temp;
		   }
		   else if (temp.compareTo(actValue) == 0)
			   listOfMaxQActions.add(actionName);
		}
	   return listOfMaxQActions;
   }
    
    public String toString() {
	// number of instances stored and condensed list of instance indexes
	return ( "#" + indexOfInstances.size() + " [" + 
		 Strings.condenseNumberList(indexOfInstances) + "]");
    }

    
    // methods of AbstractTableModel allows data to be displayed by a JTable
    public int getRowCount() {
		return actionValues.size();
    }
    
    public int getColumnCount() {
    	generateColumnNames();
		return columnNames.size();
    }

    public String getColumnName(int col) {
		generateColumnNames();
		return columnNames.get(col).toString();
    }


	private void generateColumnNames() {
		// vector of names not generated until required, then kept for future use
		if (columnNames == null) {
			columnNames = new Vector();
			for (Enumeration e = actionValues.elements(); e.hasMoreElements(); ) {
			for (Enumeration f = ( (Hashtable) ((Vector) e.nextElement()).elementAt(3) ).keys();
				 f.hasMoreElements(); ) {
				TreeNode treeNode = (TreeNode) f.nextElement();
				if (!columnNames.contains(treeNode))
				columnNames.add(treeNode);
			}
			}
			Collections.sort(columnNames);
			columnNames.add(0,"sum{R}");
			columnNames.add(0,"# instances");
			columnNames.add(0,"Q(s,a)");
			columnNames.add(0,"Action");
		} 
	}


    public Object getValueAt(int row, int column) {
	Object valueAt = null;
	// vector of actions generated when first required then kept
	if (actionNames == null) {
	    actionNames = new Vector( actionValues.keySet() );
	    Collections.sort(actionNames);
	}
	if (column == 0) // 1st row action names
	    valueAt = actionNames.get(row);
	else if (column > 3) { // transition counts
	    Hashtable transitions = (Hashtable) ( (Vector) actionValues.get(actionNames.get(row)) ).elementAt(3);
	    valueAt = transitions.get( columnNames.get(column) );
	}
	else // 0 < column <= 3: values for Q, N and sum{R}
	    valueAt = ( (Vector) actionValues.get(actionNames.get(row)) ).elementAt(column-1);

	return valueAt;
    }

    public boolean isCellEditable(int row, int col) {
	return false;
    }

}
	
