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


import java.util.*; // for Arrays (sort)
import core.*;

/**
 * Kolmogorov-Smirnov class
 *
 * Contains methods for doing Kolmogorov-Smirnov tests.
 *
 * Kolmogorov-Smirnov implementation from McCallum's PhD thesis (notes
 * starting '//' are his.)  Converted from C to Java.
 *
 * Paul A. Crook, 16th January 2004
 **/

// The implementation of the Kolmogorov-Smirnov test in "Recipes in C"
// has an error [Press et al., 1988]. It does not correctly handle
// multiple data points with the same value. Here is a corrected version.


class KolmogorovSmirnov {

    private static final double EPS1=0.001, EPS2=1.0e-8;

    // Kolmogorov-Smirnov probability function

    public static double ksProbabilityFunction(double alam) {
	double fac = 2.0, sum = 0.0, termbf = 0.0;
	double a2 = -2.0 * alam * alam;
	for (int j = 1; j <= 100; j++) {
	    double term = fac * Math.exp(a2 * j * j);
	    sum += term;
	    if (Math.abs(term) <= EPS1 * termbf || Math.abs(term) <= EPS2 * sum)
		return sum;
	    fac = -fac;
	    termbf = Math.abs(term);
	}
	return 1.0;
    }


    // Given an array data1[1..n1], and an array data2[1..n2],
    // this routine returns the K-S statistic d, and the significance
    // level probability for the null hypothesis that the data sets are drawn
    // from the same distribution. Small values of probability show that the
    // cumulative distribution function of data1 is significantly
    // different from that of data2. Assumes that data1 and data2 are
    // sorted.

    public static double[] twoSortedData(float[] data1, float[] data2) {
	int n1 = data1.length;
	int n2 = data2.length;
	int j1=0, j2=0;
	double fn1=0.0, fn2=0.0;
	double en1 = n1;
	double en2 = n2;
	double d = 0.0;

	while (j1 < n1 && j2 < n2) {
	    while ( (j1 < n1-1) && (data1[j1] == data1[j1+1]) ) j1++;
	    while ( (j2 < n2-1) && (data2[j2] == data2[j2+1]) ) j2++;
	    double d1 = data1[j1];
	    double d2 = data2[j2];
	    if (d1 <= d2) fn1 = ++j1 / en1;
	    if (d2 <= d1) fn2 = ++j2 / en2;
	    double dt = Math.abs(fn2-fn1);
	    if (dt > d)	d = dt;
	}
	double en = Math.sqrt(en1 * en2 / (en1 + en2));
	double prob = ksProbabilityFunction((en + 0.12 + 0.11/en) * d);
	return new double[] {d, prob};
    }



    /** PAC: add this method to deal with an array of Doubles (instead of an array of floats)**/
    /** identical to above but for the Double.floatValue() and .equals(...) methods **/
    /** also replaced float d1,d2 with doubles **/
    public static double[] twoSortedData(Double[] data1, Double[] data2) {
	int n1 = data1.length;
	int n2 = data2.length;
	int j1=0, j2=0;
	double fn1=0.0, fn2=0.0;
	double en1 = n1;
	double en2 = n2;
	double d = 0.0;

	while (j1 < n1 && j2 < n2) {
	    while ( (j1 < n1-1) && data1[j1].equals(data1[j1+1]) ) j1++;
	    while ( (j2 < n2-1) && data2[j2].equals(data2[j2+1]) ) j2++;
	    double d1 = data1[j1].doubleValue();
	    double d2 = data2[j2].doubleValue();
	    if (d1 <= d2) fn1 = ++j1 / en1;
	    if (d2 <= d1) fn2 = ++j2 / en2;
	    double dt = Math.abs(fn2-fn1);
	    if (dt > d)	d = dt;
	}
	double en = Math.sqrt(en1 * en2 / (en1 + en2));
	double prob = ksProbabilityFunction((en + 0.12 + 0.11/en) * d);
	return new double[] {d, prob};
    }


    /** PAC: added method below to do sorting required by the original method **/
    /** compare two unsorted data sets **/
    public static double[] testTwoDataSets(float[] dataA, float[] dataB) {
	// copy data arrays so the original arrays aren't sorted
	float[] data1 = new float[dataA.length];
	float[] data2 = new float[dataB.length];
	System.arraycopy(dataA, 0, data1, 0, dataA.length);
	System.arraycopy(dataB, 0, data2, 0, dataB.length);
	Arrays.sort(data1);
	Arrays.sort(data2);
	return twoSortedData(data1, data2);
    }

    /** PAC: ditto for this **/
    /** compare two unsorted Double data sets **/
    public static double[] testTwoDataSets(Double[] dataA, Double[] dataB) {
	// copy data arrays so the original arrays aren't sorted
	Double[] data1 = new Double[dataA.length];
	Double[] data2 = new Double[dataB.length];
	System.arraycopy(dataA, 0, data1, 0, dataA.length);
	System.arraycopy(dataB, 0, data2, 0, dataB.length);
	Arrays.sort(data1);
	Arrays.sort(data2);
	return twoSortedData(data1, data2);
    }

    /** this method designed to provide a Vectors interface. **/
    public static double[] testTwoDataSets(Vector dataA, Vector dataB) {
	return testTwoDataSets( (Double[]) dataA.toArray(new Double[dataA.size()]), 
				(Double[]) dataB.toArray(new Double[dataB.size()]) );
    }

}
