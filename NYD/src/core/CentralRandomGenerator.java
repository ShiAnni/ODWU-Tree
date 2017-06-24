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


import java.util.*;

/**
 *
 * Central Random Generator
 *
 * Allows centralisation of random number generation, rather than
 * each object managing thier own random number generator, this
 * simplfies fixing the seed in one place to allow repeatability.
 *
 * Paul A. Crook, 8th April 2003
 *
 *
 * added nextGaussian 21st September 2004
 **/

class CentralRandomGenerator {

    static private Random r = new Random();
    
    static public void setSeed(long seed) {
	r.setSeed(seed);
    }
    
    static public int nextInt(int n) {
	int q = r.nextInt(n);
	// System.out.println("nextInt="+q);
	return q;
    }

    static public double nextDouble() {
	double q = r.nextDouble();
	// System.out.println("nextDouble="+q);
	return q;
    }
    
    static public double nextGaussian() {
	double q = r.nextGaussian();
	// System.out.println("nextGaussian="+q);
	return q;
    }
}
