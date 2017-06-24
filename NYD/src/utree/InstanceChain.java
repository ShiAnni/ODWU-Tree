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


import java.util.*; // for Vector
import core.*;

/**
 * InstanceChain class; 
 * just a wrapper for Vector to make this particular use stand out
 *
 * P.A.Crook, 4th January 2004
 *
 **/

public class InstanceChain extends Vector {
	
	//constructor with initial capacity and capacity increment
	public InstanceChain(int initialCapacity, int capacityIncrement) {
		super(initialCapacity, capacityIncrement);
	}

	//basic no arguments constructor
	public InstanceChain() {
		super();
	}

    // putInstance method to ensure only instances are stored
    public boolean addInstance(Instance o) {
	return this.add(o);
    }

    // similarly provide a getInstance that only returns instances (saves on casting)
    public Instance getInstance(int index) {
	Instance instance = null; //if index beyond start of chain return null
	if (index >= 0)
		instance = (Instance) this.get(index);
	return instance;
    }

    // version of getInstance that works with Integers
    public Instance getInstance(Integer index) {
	return getInstance(index.intValue());
    }


    // toString
    public String toString() {
	String string = new String("[");
	int numberOfInstances = this.size();
	for (int i = 0; i < numberOfInstances; ) {
	    String output = Strings.fixedLengthLeadingSpaces(String.valueOf(i),4) + ":" + this.get(i);
	    if (++i < numberOfInstances) 
		output = output + ", ";
	    else
		output = output + "  ]";
	    string = string + Strings.makeFixedLength(output, 36);
	    if (i % 3 == 0 && i < numberOfInstances) string = string + "\n ";
	}
	return string;
    }
		
}
