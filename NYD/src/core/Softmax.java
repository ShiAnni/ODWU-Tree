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
 * Implements softmax action selection routine.
 *
 * Paul A. Crook
 * 14th October 2003
 *
 **/
class Softmax extends ActionSelection {
    private String temperature;

    //constructor
    Softmax(ReallyBasicStateActionLearner actNlearn, String arguments)
        throws Exception {
        //set up pointer to act'n'learn method
        // (needed to get at values like action-learning
        // steps, runs completed and state-action 'Q' values)
        super(actNlearn);

        //get arguments
        int startArgs = arguments.indexOf('[') + 1;
        int endArgs = arguments.lastIndexOf(']');

        //if argument not given throw an exception
        if ((startArgs == 0) || (endArgs == -1)) {
            throw new Exception();
        }
        else {
            temperature = arguments.substring(startArgs, endArgs);
        }

        //check temperature formula is valid (throws execption)
        double temp = actNlearn.evalFormula(temperature);
        if (temp <= 0.001) {
            throw new Exception(
                "This implementation of softmax doesn't handle numbers close to infinity very well.  Use a temperature value > 0.001. Alternatively use 'e-greedy[0]' to ensure greedy action selection.");
        }
    }

    public String listActionSelectionArgs() {
        return "tau";
    }

    public String currentValueActionSelectionArgs() {
        return String.valueOf(actNlearn.evalFormula(temperature));
    }

    //Select Action from Given List of actions.
    protected Action selectActionFromList(State s, Action[] list, boolean explore) {
        //System.out.print("state " + s + " ");
        //if exploration allowed select using softmax
        if (explore) {
            // probability of an action being selected:
            // \frac{ \exp{ Q_t(a) / \tau } }{ \sum^n_{b=1} \exp{ Q_t(b) / \tau } }
            double tau = actNlearn.evalFormula(temperature);

            //cumulative value of uncorrected 'probabilities'
            double[] cumulative = new double[list.length];

            //value of 1st action
            cumulative[0] = Math.exp(actNlearn.getStateActionValue(new StateActionPair(s, list[0])) / tau);

            //calc. value of rest...
            for (int i = 1; i < list.length; i++) {
                cumulative[i] = cumulative[i - 1] +
                    Math.exp(actNlearn.getStateActionValue(new StateActionPair(s, list[i])) / tau);
            }

            //random value selected from total span of cumulative values
            double randomValue = CentralRandomGenerator.nextDouble() * cumulative[list.length - 1];

            //use above random value to select the action
            int i = 0;
            for (; cumulative[i] < randomValue; i++) {
            }

            //System.out.print( "softmax: " );
            //for(int j = 0; j < list.length; j++) {
            //System.out.print( cumulative[j] + " " );
            //}
            //System.out.print("random " + randomValue + ", i " + i + ", tau " + tau );
            return list[i];
        }
        else {
            //else follow policy
            //System.out.print( "following policy: " );
            return actNlearn.policyAction(s, list);
        }
    }
}
