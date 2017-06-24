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


import java.util.*;


class TestKS {

    public static void main(String[] args) {

        double[] inputDataA = new double[] {
                8.676638889312745, 8.676638889312745, 18.676638889312745,
                8.676638889312745, 8.676638889312745, 8.676638889312745,
                8.676638889312745, 18.676638889312745, 8.676638889312745,
                8.676638889312745
            };
        //{0.01, 0.022, 0.023, 0.027, 0.03, 0.035, 0.05, 0.075, 0.079, 0.08, 0.081};
        //{0.01, 0.08, 0.03, 0.05, 0.022, 0.023, 0.075, 0.081, 0.079, 0.027, 0.035};
        //{0.0151, 0.0152, 0.048, 0.047, 0.0153, 0.0157, 0.043, 0.0492, 0.0488, 0.0522, 0.0521, 0.0154, 0.0156, 0.0512};
        
        double[] inputDataB = new double[] {
                8.676638889312745, 8.676638889312745, 8.676638889312745,
                8.676638889312745, 18.676638889312745, 8.676638889312745,
                8.676638889312745, 8.676638889312745, 8.676638889312745,
                8.676638889312745, 18.676638889312745, 8.676638889312745,
                8.676638889312745, 8.676638889312745
            };
        //{0.0151, 0.0152, 0.0153, 0.0154, 0.0156, 0.0157, 0.043, 0.047, 0.048, 0.0488, 0.0492, 0.0512, 0.0521, 0.0522};
        //{0.0151, 0.0152, 0.048, 0.047, 0.0153, 0.0157, 0.043, 0.0492, 0.0488, 0.0522, 0.0521, 0.0154, 0.0156, 0.0512};
        float[] dataA = new float[inputDataA.length];
        float[] dataB = new float[inputDataB.length];

        System.out.print("dataA");

        for (int i = 0; i < inputDataA.length; i++) {
            dataA[i] = (float) inputDataA[i];
            System.out.print("\t" + dataA[i]);
        }

        System.out.println();
        System.out.println("mean: " + Statistics.mean(dataA) +
            ", variance (biased): " + Statistics.biasedVariance(dataA) +
            ", (unbiased): " + Statistics.unbiasedVariance(dataA) + ", std: " +
            Statistics.standardDeviation(dataA));
        System.out.println();

        System.out.print("dataB");

        for (int i = 0; i < inputDataB.length; i++) {
            dataB[i] = (float) inputDataB[i];
            System.out.print("\t" + dataB[i]);
        }

        System.out.println();
        System.out.println("mean: " + Statistics.mean(dataB) +
            ", variance (biased): " + Statistics.biasedVariance(dataB) +
            ", (unbiased): " + Statistics.unbiasedVariance(dataB) + ", std: " +
            Statistics.standardDeviation(dataB));
        System.out.println();

        double[] results = KolmogorovSmirnov.testTwoDataSets(dataA, dataB);

        Arrays.sort(dataA);
        Arrays.sort(dataB);

        System.out.println("results: d " + results[0] + ", prob " + results[1]);
        System.out.println();

        System.out.print("dataA");

        for (int i = 0; i < dataA.length; i++) {
            System.out.print("\t" + dataA[i]);
        }

        System.out.println();

        System.out.print("dataB");

        for (int i = 0; i < dataB.length; i++) {
            System.out.print("\t" + dataB[i]);
        }

        System.out.println();
    }
}
