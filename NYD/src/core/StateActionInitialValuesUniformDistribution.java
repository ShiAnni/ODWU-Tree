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
 * All state action initiated from a uniform distribution
 *
 * P.A. Crook
 * 22nd September 2004
 *
 **/

class StateActionInitialValuesUniformDistribution extends StateActionInitialValue {

    private double min, max, spread;

    //constructor
    StateActionInitialValuesUniformDistribution(String arguments) throws Exception {
	//get arguments
	int startArgs = arguments.indexOf('[')+1;
	int endArgs = arguments.lastIndexOf(']');
	int argSeparator = arguments.indexOf(':');
	//if two arguments not given throw an exception
	if (startArgs == 0 || endArgs == -1 || argSeparator == -1)
	    throw new Exception();
	else {
	    min = Double.parseDouble(arguments.substring(startArgs, argSeparator));
	    max = Double.parseDouble(arguments.substring(argSeparator+1, endArgs));
	    spread = max - min;
	    if (spread < 0) throw new Exception("Spread of values in state action value is less than zero."); 
	}
    }


    public Double getValue() {
	return new Double(CentralRandomGenerator.nextDouble() * spread + min); 
    }

}  
