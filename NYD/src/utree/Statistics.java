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


/**
 * Statistics class
 *
 * basic statistical functions; mean, variance and standard deviation
 *
 * Paul A. Crook, 18th January 2004
 **/

class Statistics {

    //mean
    public static double mean(float[] dataPoints) {
	double sum = 0.0;
	for(int i = 0; i < dataPoints.length; i++) {
	    sum += dataPoints[i];
	}
	return sum / dataPoints.length;
    }

    //biased estimate of sample: 
    // S^2 = \sum (dataPoint - mean)^2 / N
    public static double biasedVariance(float[] dataPoints) {
	double sum = 0.0;
	double mean = mean(dataPoints);
	for(int i = 0; i < dataPoints.length; i++) {
	    double temp = dataPoints[i] - mean;
	    sum += temp * temp;
	}
	return sum / dataPoints.length;
    }

    //unbiased estimate of sample:
    // s^2 = \sum (dataPoint - mean)^2 / (N-1)
    public static double unbiasedVariance(float[] dataPoints) {
    	double sum = 0.0;
	double mean = mean(dataPoints);
	for(int i = 0; i < dataPoints.length; i++) {
	    double temp = dataPoints[i] - mean;
	    sum += temp * temp;
	}
	return sum / (dataPoints.length - 1);
    }

    
    // standard deviation of sample; uses unbiased estimate of variance
    public static double standardDeviation(float[] dataPoints) {
	return Math.sqrt ( unbiasedVariance(dataPoints) );
    }

}
