package utree;
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
import core.*;
import java.io.*; // for Serializable

/**
 * TreeNode used to build simple trees
 *
 * P.A.Crook, 3rd January 2004
 *
 * (leaves are tree nodes where parameterToSplitOn == null)
 *
 * 
 * 16th August 2005; tried initiating capacity of Hashtable
 *                   branches to 24 to speed up fringe test;
 *                   but didn't work; reset to default values
 **/

public class TreeNode implements Cloneable, Serializable, Comparable{
    
    public Object parameterToSplitOn;
    public Object data;
    public Hashtable branches;
    public TreeNode parentNode;
     
	private static int id; // used to enumerate
	private String nodeID; // leaves for display
	
	 
    // construct a leaf
	public TreeNode(Object dataToStore) {
	this(null, dataToStore);
    }

    // construct a full blown tree node
    TreeNode(Object param, Object dataToStore) {
		parameterToSplitOn = param;
		data = dataToStore;
		parentNode = null;
		branches = new Hashtable();
    }

    public void setParameterToSplitOn(Object param) {
	parameterToSplitOn = param;
    }

    public void setParent(TreeNode parent) {
	parentNode = parent;
    }

    public void clearData() {
	data = null;
    }
    
    public void addSubTreeNode(Object key, TreeNode subTreeNode) {
	branches.put(key, subTreeNode); // add new subTreeNode to list of branches
	subTreeNode.setParent(this); // set subTreeNode's parent to this TreeNode
    }


    public void replaceWith(TreeNode replacement) {
	//copy across all the replacements details (except parent)
	parameterToSplitOn = replacement.parameterToSplitOn;
	data = replacement.data;
	branches = replacement.branches;
	//change all sub tree nodes belonging to the replacement so
	//that this node becomes their parent.
	for (Enumeration e = branches.elements(); e.hasMoreElements(); ) {
	    ( (TreeNode) e.nextElement() ).setParent(this);
	}
    }

    public Enumeration enumerateParameterValues() {
	return branches.keys();
    }

    public boolean branchExistsForParameterValue(Object parameterValue) {
		return branches.containsKey(parameterValue);
    }


    public TreeNode getSubTreeNode(Object parameterValue) {
		// throws NullPointerException if parameterValue is 'null'
		return (TreeNode) branches.get(parameterValue);
		// returns null if parameter value not found in branches
    }
    
    
    public void deleteSubTreeNode(TreeNode node){
    	for(Enumeration e = branches.keys(); e.hasMoreElements();){
    		Object key = e.nextElement();
    		if(branches.get(key) == node){
    			branches.remove(key);
    			break;
    		}
    	}
    	if(branches.isEmpty()){
    		parameterToSplitOn = null;
    	}
    }
    
    //return conjunction of decision parameters that lead to this node
    //(NB this doesn't include this node's parameterToSplitOn as that leads onwards to some sub-node)
    public Vector conjunctionOfParametersToSplitOn() {
		Vector conjunctionOfParameters = new Vector();
		if (parentNode != null) {
	    	conjunctionOfParameters.addAll( parentNode.conjunctionOfParametersToSplitOn() );
			conjunctionOfParameters.add( parentNode.parameterToSplitOn );
			}
		return conjunctionOfParameters;
    }

    //is this a leaf?
    public boolean isLeaf() {
		return (parameterToSplitOn == null);
    }

	//is this the root of the tree
	public boolean isRoot() {
		return (parentNode == null);
	}
	
	//is this has no instance stored
	public boolean isEmpty(){
		return ((UTreeLeafData)data).indexOfInstances.isEmpty();
	}

    //recurse down tree to find all the leaves
    public Vector allLeaves() {
		Vector listOfLeaves = new Vector();
		if ( this.isLeaf() )
		listOfLeaves.add(this);
		else {
		for (Enumeration e = branches.elements(); e.hasMoreElements() ;) {
	    	TreeNode node = (TreeNode) e.nextElement();
			listOfLeaves.addAll( node.allLeaves() );
		}
		}
		return listOfLeaves;
    }

    //do cloning of tree
    public Object clone() {
     	try {
	  		TreeNode aClone = (TreeNode) super.clone();
	  		//shallow copy only; same as Hashtable; can't invoke clone() on Object
	  		// (actually it can be, but need to cast Object to Interface cloneable)
	  		//aClone.parameterToSplitOn = (Object) parameterToSplitOn.clone();
	  		//aClone.data = (Object) data.clone();
	  		aClone.branches = new Hashtable();
	  		for (Enumeration e = branches.keys(); e.hasMoreElements(); ) {
	      		Object key = e.nextElement();
	      		TreeNode cloneSubTreeNode = (TreeNode) ((TreeNode) branches.get(key)).clone();
	      		aClone.addSubTreeNode(key, cloneSubTreeNode);
	  		}
	  	return aClone;
		}
		catch (CloneNotSupportedException e) {
	  	throw new InternalError(e.toString());
		}
    }

	//compareTo (Interface Comparable) allows sorting of tree nodes; used for display in UTreeFileReader;
	// returns:  -1  if this object less than specified object (o),
	//                 0  if equal to specified object,
	//               +1  if greater than the specified object.
	public int compareTo(Object o) {
		int result = 0;
		if ( this.hashCode() < o.hashCode() )
			result = -1;
		else if ( this.hashCode() > o.hashCode() )
			result = 1;
		return result;  
	}


	// toString - if nodeID set returns it, otherwise
	// return class and hash number of this node
	public String toString(){
		try {
		      StringBuffer text = new StringBuffer();

		      if (isLeaf()) {
		        text.append("leaf");
		      } else {
		        dumpTree(0, text);
		      }

		      return text.toString();
		    } catch (Exception e) {
		      return "Can't print classification tree.";
		    }
	}
	
	private void dumpTree(int depth, StringBuffer text) throws Exception {

	    int i, j;

	    Iterator iter = branches.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry entr = (Map.Entry)iter.next();
			TreeNode son = (TreeNode)entr.getValue();

			text.append("\n");
			for (j = 0; j < depth; j++) {
				text.append("|   ");
			}
			text.append(((ParameterSelected)parameterToSplitOn).historyIndex + "-" 
					+((ParameterSelected)parameterToSplitOn).dimension_name() + " : ");
			text.append(entr.getKey());
			if (!son.isLeaf()) {
				son.dumpTree(depth + 1, text);
			}
		}
	}

}
