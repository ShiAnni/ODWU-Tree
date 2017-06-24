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


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.Vector;

/**
 * really basic class for building Reinforcement Learning algorithms
 * doesn't include learning rate (alpha) or assume a monolithic state-action table
 *
 * does assume policy is given by max_a Q(s,a) for all s
 *
 * Paul A. Crook 20th November 2002 / 3rd January 2004
 **/
abstract public class ReallyBasicStateActionLearner extends ActionLearningProcess {
    protected String gamma; // select discount factor,
    protected String conduct; // select discount factor,
    protected String saValueArg; // select discount factor,
    protected StateActionInitialValue saInitValue; // algorithm for selecting
                                                   // actions & whether state-action
                                                   // values are initiated at zero
                                                   // or from some random
                                                   // distribution
    protected ActionSelection actionSelection; //class for selecting actions
    protected ReallyBasicStateActionLearner parent; // parent for when you've been cloned
     
    
    //only has a constructor with arguments
    public ReallyBasicStateActionLearner(Agent agent, String args)
        throws Exception {
        super(agent, args); // set up internal pointer to the agent 

        String file;
        try {
            // get arguments
            gamma = Args.getArgument(args, "gamma=");
            conduct = Args.getArgument(args, "conduct=");
            saValueArg = Args.getArgument(args, "initSAvs=");
            file = Args.getArgument(args, "file=");

            // check the formulae are valid
            evalFormula(gamma);

            //select which ActionSelection routine to use
            if (conduct.startsWith("e-greedy")) {
                actionSelection = new EpsilonGreedy(this, conduct);
            }
            else if (conduct.startsWith("softmax")) {
                actionSelection = new Softmax(this, conduct);
            }
            else if (conduct.startsWith("fixed")) {
            	actionSelection = new FixedPolicy(this, conduct);
            }
            else if (conduct.startsWith("e-fixed")) {
            	actionSelection = new EpsilonFixedPolicy(this, conduct);	
            }
            else if(conduct.startsWith("heuristic")){
            	actionSelection = new HeuristicExplore(this);
            }
            else
            {
                throw new Exception(usageString());
            }

            //select how state-action values are initiated
            if (saValueArg.equals("zeros")) {
                saInitValue = new StateActionValuesInitiallyZero();
            }
            else if (saValueArg.startsWith("uniform")) {
                saInitValue = new StateActionInitialValuesUniformDistribution(saValueArg);
            }
            else if (saValueArg.startsWith("gaussian")) {
                saInitValue = new StateActionInitialValuesGaussianDistribution(saValueArg);
            }
            else
            {
                throw new Exception(usageString());
            }
        }
         catch (Exception e) {
            //e.printStackTrace();
            throw new Exception(usageString(),e);
        }

        //new knowledge store required or is a file name given?
        if (file.equals("")) {
            createBlankTables();
        }
        else {
            try { //get file (if you can)

                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                readFile(ois);
                ois.close();
                fis.close();
            }
             catch (IOException e) {
                throw e;
            }
        }
    }

    //usage string - returned when errors occur in construction
    abstract protected String usageString();

    //add strings to the given arguments vector, each string is the
    // format of arguments expected by this class
    protected void usageArguments(UsageArgumentVector arguments) {
        arguments.add("gamma=N");
        arguments.add("conduct=e-greedy[N]/softmax[N]/" +
        		"fixed[policy=filename]/e-fixed[epsilon=N:policy=filename]");
        arguments.add("initSAvs=zeros/uniform[min:max]/gaussian[mean:std]");
        arguments.add("file=optional_file");
    }

    //list arguments; gamma, etc., and current values
    public String listActnLearnArguments() {
        return "gamma\t" + actionSelection.listActionSelectionArgs();
    }

    public String currentValueActnLearnArgs() {
        return (String.valueOf(evalFormula(gamma)) + "\t" + actionSelection.currentValueActionSelectionArgs());
    }

    // Clone the stuff that act'n'learn super-class doesn't deal with
    public Object clone() {
        ReallyBasicStateActionLearner aClone = (ReallyBasicStateActionLearner) super.clone();
        aClone.actionSelection = (ActionSelection) actionSelection.clone(aClone);
        aClone.parent = this;
        return aClone;
    }
     //no try and catch, as no exceptions from ActionLearningProcess or ActionSelection

    //main loop - implemented slightly differently in each method
    abstract public void mainLoop();

    //Select action -- selects between all possible actions
    //Explore flag controls exploration.
    protected Action selectAction(State s, boolean explore) {
        // call selectActionFromList with
        // list = array of all possible actions - consult agent to see what's currently possible
        return actionSelection.selectActionFromList(s, allAvailableActions(), explore);
    }

    //Select Physical Action
    protected Action selectPhysicalAction(State s, boolean explore) {
        return actionSelection.selectActionFromList(s, availablePhysicalActions(), explore);
    }

    //Select Perceptual Action
    protected Action selectPerceptualAction(State s, boolean explore) {
        return actionSelection.selectActionFromList(s, availablePerceptualActions(), explore);
    }

    //abstract method which should set the value of a give 'sa' pair
    abstract protected void setStateActionValue(StateActionPair sa, Double value);

    //abstract method which should look up the value of given 'sa' pair
    // or return null if the 'sa' pair has not had a value assigned yet 
    abstract protected Double lookupStateActionValue(StateActionPair sa);

    //get state action value; return value for every 'sa' pair
    //initiating entries as required
    protected double getStateActionValue(StateActionPair sa) {
        Double value = lookupStateActionValue(sa);
        if (value == null) { // initiate 'sa' pair's value
            value = saInitValue.getValue();
            setStateActionValue(sa, value);
            if (parent != null) {
				// if cloned (for evaluation) reflect initialized value back to parent
                parent.setStateActionValue(sa, value);
            }
        }
        return value.doubleValue();
    }

    // policy action; return the policy action (from the given list) for the given state
    // this version assumes the policy update is for the form pi <-- max_a Q(s,a) for all s
    // (i.e. select greedily)
    protected Action policyAction(State s, Action[] list) {
        Vector act = new Vector();
        act.addElement(list[0]); //initiate `act' vector with 1st action

        double actValue = getStateActionValue(new StateActionPair(s, list[0]));
        for (int i = 1; i < list.length; i++) {
            double ithValue = getStateActionValue(new StateActionPair(s, list[i]));

            if (ithValue > actValue) {
                act.clear();
                act.addElement(list[i]);
                actValue = ithValue;
            }
            else if (ithValue == actValue) { // if equal add to list
                act.addElement(list[i]); //  to break randomly
            }
        }

        //if multiple actions listed in 'act' note state as one where policy acted stochastically
        if (act.size()>1) {
        	lastStateWherePolicyStochastic = s; 
        }
        //select randomly between those listed (who have equal value)
        return (Action) act.get(CentralRandomGenerator.nextInt(act.size()));
    }
    
    //overwrite method to save the knowledge learnt by agent
    public void saveKnowledge(String filePrefix, String filePostfix, Integer maxTrainingSteps) {
        try {
            File file = new File(filePrefix + knowledgeFileName().replace('/', '|') +
                    ((maxTrainingSteps != null) ? (" aRun<=" + maxTrainingSteps) : "") + filePostfix);
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            writeFile(oos);
            oos.flush();
            oos.close();
            fos.close();
        }
         catch (Exception e) {
            System.err.println("Problem writing out knowledge file:" + e);
            System.exit(1);
        }
    }

    //file name to save knowledge in - defined by each sub-class
    abstract protected String knowledgeFileName();

    protected String knowledgeFileNameParameters() {
        return (agent + " gamma" + gamma + " " + conduct + " " + saValueArg + " runs" + runsCompleted + " actions" +
        actionSteps);
    }

    //create empty knowledge store
    abstract protected void createBlankTables();

    //read file - read in the data you need - basic functionality given, can
    // be extended by sub-classes over writing this method
    protected void readFile(ObjectInputStream ois) throws Exception {
        actionSteps = ois.readInt();
        runsCompleted = ois.readInt();
    }

    //write file - write out the data to be save
    protected void writeFile(ObjectOutputStream oos) throws IOException {
        oos.writeInt(actionSteps);
        oos.writeInt(runsCompleted);
    }
}
