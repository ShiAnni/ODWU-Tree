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
 * This is a abstract class for all the ActionLearningProcess
 * (reinforcement learning) that I define for the agents to use.
 *
 * P.A. Crook
 * 30th October 2002
 *
 **/

public abstract class ActionLearningProcess implements Cloneable {
    
    /** common variables for all learning processes **/
    
    protected Agent agent; //local copy of pointer to the agent
    protected int actionSteps; //count of actions (steps) taken
    protected int runsCompleted; //count of completed runs (start to goal)
    protected float cumulativeReward; //cumulative total of reward received  
    protected Dossier dossier; //dossier kept on agents recent actions
    protected State lastStateWherePolicyStochastic;  //record of state where policy was stochastic
    
    /** common methods for learning processes **/
    
    //constructor. allows for extra arguments - basic case ignore the arguments
    ActionLearningProcess(Agent agent, String args) {
	this.agent = agent;
	cumulativeReward = 0;
	dossier = new Dossier(); //set up empty dossier to store reports in
    }

    //act'n'learn init - for stuff that has to wait until after a world.init()
    // boolean flag to indicate whether this is an evaluation run
    public void init(boolean evaluation) {
      clearDossier();
      dossier.add( agent.report() ); //initial report; i.e. "where are you?"
    }  

    //execute action and add report to dossier
    protected Float executeAction(Action act, State state) {
      Float reward = agent.executeAction(act, state);
      cumulativeReward += reward.floatValue(); // add to cumulative reward
      dossier.add(agent.report());
      return reward;
    }

    //get all available actions
    protected Action[] allAvailableActions() {
	return agent.allActions();
    }

    //get available physical actions
    protected PhysicalAction[] availablePhysicalActions() {
	return agent.physicalActions();
    }

    //get available perceptual actions
    protected PerceptualAction[] availablePerceptualActions() {
	return agent.perceptualActions();
    }

    //clearDossier - clears dossier ready for new list of actions
    protected void clearDossier() {
      dossier.clear();
    }

    //send back a report on the actions executed by the act'n'learn process
    public Dossier report() {
	return dossier; 
    }
    
    //get agent -- returns a pointer to the agent
    public Agent getAgent() {
        return agent;
    }

    //evaluate - evaluate learnt performance - actually just act greedily
    public void evaluate() {
        clearDossier();         // default method returns dossier containing 
        dossier.add( new Report() ); // a null report.  null report required
    }                                // to increment step count.

    public void resetLastStateWherePolicyStochastic() {
    	lastStateWherePolicyStochastic = null;
    }
    
    public State getLastStateWherePolicyStochastic() {
    	return lastStateWherePolicyStochastic;
    }
    
    //increment runs completed completed (start to goal)
    public void runCompleted() {
    	runsCompleted++;
    }
    
    //return number of runs completed
    public int getRunsCompleted() {
    	return runsCompleted;
    }
    
    //return action steps count
    public int getActionSteps() {
        return actionSteps;
    }
    
    //return cumulative reward
    public float getCumulativeReward() {
	return cumulativeReward;
    }

    //This bits for cloning the act'n'learn algorithm
    //Possibly slightly OTT but safe
    public Object clone() {
        try {
	  ActionLearningProcess aClone = (ActionLearningProcess) super.clone();
	  aClone.agent = (Agent) agent.clone();
	  aClone.dossier = (Dossier) dossier.clone();
	  return aClone;
	}
	catch (CloneNotSupportedException e) {
	  throw new InternalError(e.toString());
	}
    }
    
    //save knowledge - by default does nothing, expects to be
    //     overwritten by any sub-class that has data to save.
    public void saveKnowledge(String filePrefix, String filePostfix, Integer maxTrainingStep) {}


    /** abstract methods (i.e. to defined (differently) by each sub-class) **/
    
    abstract public String listActnLearnArguments(); //alpha, gamma, etc.
    abstract public String currentValueActnLearnArgs(); //eval. alpha, gamma...

    abstract public void mainLoop();

    
    /****************** below this bit is evalFormula ********************/

    // evaluate strings containing formula
    protected double evalFormula(String formula) throws NumberFormatException {

	//System.out.print( formula.toString() );
	int len = formula.length();
	
	//look for function operators or bracketed terms of form "[fn](..."
	// (bracket requires a double escape)
	if ( formula.matches("[a-z]*\\(.*") ) {
	    // System.out.println("[a-z]*\\(.* matches");
	    double bracketResult;
	    int openingBracket = formula.indexOf("(");
	    int closingBracket = findMatchingBracket(formula,openingBracket);
	    //simple brackets
	    if ( formula.startsWith("(") )
		 bracketResult = evalFormula( formula.substring(1,closingBracket) );
	    //function: bound(lower;upper;formula)
	    else if ( formula.startsWith("bound(") ) {
		String[] args = formula.substring(6,closingBracket).split(";", 3);
		double lowerBound = evalFormula(args[0]);
		double upperBound = evalFormula(args[1]);
		bracketResult = evalFormula(args[2]);
		if (bracketResult < lowerBound) bracketResult = lowerBound;
		else if (bracketResult > upperBound) bracketResult = upperBound;
	    }
	    //catch unknown functions
	    else throw new NumberFormatException();

	    //nothing left after the closed bracket then return result
	    if (closingBracket == len-1) return bracketResult;
	    //else split on operator
	    int i = closingBracket + 1;
	    char ch = formula.charAt(i);
	    if (ch == '+')
		return ( evalFormula(formula.substring(0,i)) +
			 evalFormula(formula.substring(i+1)) );
	    if (ch == '-')
		return ( evalFormula(formula.substring(0,i)) -
			 evalFormula(formula.substring(i+1)) );
	    if ( ch == '*' )
		return ( evalFormula(formula.substring(0,i)) *
			 evalFormula(formula.substring(i+1)) );
	    if ( ch == '/' )
		return ( evalFormula(formula.substring(0,i)) /
			 evalFormula(formula.substring(i+1)) );
	    if ( ch == '^' )
		return ( Math.pow( evalFormula(formula.substring(0,i)),
				   evalFormula(formula.substring(i+1)) ) );
	    //unrecognised operator
	    throw new NumberFormatException();
	}
 
	//ordering reflects operator precidence ('+' == '-') < ('*' == '/') < '^'
	
	// not an operator if prefixing string (i.e. <= 0) or preceeded by
	// another operator

	//start at i=1 to skip prefixes
	int i = 1;
	for( ; i < len && 
		 ( ( formula.charAt(i) != '+' && formula.charAt(i) != '-' ) ||
		   ( formula.charAt(i-1) == '*' || formula.charAt(i-1) == '/' 
		     || formula.charAt(i-1) == '^' ) ); i++)
	    {
		//jump over bracketed term, i.e. treat it as one term
		if ( formula.charAt(i) == '(' ) i = findMatchingBracket( formula, i );
	    }

	// i now indexes the 1st + or - operator or equals len if no operator
	
	if ( i < len ) {
	    if (formula.charAt(i) == '+')
		return ( evalFormula(formula.substring(0,i)) +
			 evalFormula(formula.substring(i+1)) );
	    else if (formula.charAt(i) == '-')
		return ( evalFormula(formula.substring(0,i)) -
			 evalFormula(formula.substring(i+1)) );
	    else {
		System.err.println("Error in formula evaluation; around + or -.");
		System.exit(1);
	    }
	}
	
	i = 0;
	for( ; i < len && 
		 (formula.charAt(i) != '*' & formula.charAt(i) != '/'); i++ )
	    {
		//jump over bracketed term, i.e. treat it as one term
		if ( formula.charAt(i) == '(' ) i = findMatchingBracket( formula, i );
	    }

	// i indexes the 1st * or / operator or equals len if no operator
	
	if ( i < len ) {
	    if (formula.charAt(i) == '*')
		return ( evalFormula(formula.substring(0,i)) *
			 evalFormula(formula.substring(i+1)) );
	    else if (formula.charAt(i) == '/')
		return ( evalFormula(formula.substring(0,i)) /
			 evalFormula(formula.substring(i+1)) );
	    else {
		System.err.println("Error in formula evaluation; around * or /.");
		System.exit(1);
	    }
	}

	i = 0;
	for( ; i < len && formula.charAt(i) != '^'; i++);
	// i indexes the 1st * or / operator or equals len if no operator
	
	if ( i < len ) {
	    if (formula.charAt(i) == '^')
		return ( Math.pow( evalFormula(formula.substring(0,i)),
				   evalFormula(formula.substring(i+1)) ) );
	    else {
		System.err.println("Error in formula evaluation; around ^.");
		System.exit(1);
	    }
	}
	

	//through to this bit - therefore no 'in-fix' operators (i.e. A op B)
	
	//catch + or - prefix
	if (formula.startsWith("-")) 
	    return ( - evalFormula(formula.substring(1)) );
	if (formula.startsWith("+"))
	    return evalFormula(formula.substring(1));

	//parse numbers or variable names to get their value
	return getNumberOrVariableValue(formula);
    }

    
	//get number or variable value; returns numeric value of recognised variables or number
	//throws number format exception if variable not recognised or number can't be parsed 
	double getNumberOrVariableValue(String numberOrVariable) throws NumberFormatException {
		//if a recognised variable name
		if (numberOrVariable.equals("actions")) return (double) actionSteps;
		if (numberOrVariable.equals("runs")) return (double) runsCompleted;
		//otherwise assume its a number
		return Double.parseDouble(numberOrVariable);		
	}

    //finds closing bracket that matches open bracket, starts search from i
    // where i is normally the character that is the open bracket
    int findMatchingBracket(String formula, int i) throws NumberFormatException {
	int count = 1;
	int len = formula.length();
	for( ; count != 0 && ++i < len; ) {
	    char ch = formula.charAt(i);
	    if (ch == '(') count++;
	    if (ch == ')') count--;
	}
	// System.out.println("count: "+ count + ", len: " + len + ", i: " +i);
	if (count != 0) throw new NumberFormatException();
	return i;
    }
    
}


