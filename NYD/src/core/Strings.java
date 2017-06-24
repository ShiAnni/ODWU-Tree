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


import java.util.*; //for Collections and Vector classes

/**
 *
 * Strings class
 * -------------
 *
 * static methods for doing stuff with strings
 *
 * Paul A.Crook, Friday 13th February 2004 
 * 
 **/

public class Strings {

    //make strings of a fixed length
    public static String makeFixedLength(Object obj, int length) {
	String string = obj.toString();
	while (string.length() < length) {
	    string = string + " ";
	}
	return string;
    }

    //make fixed length leading spaces
    public static String fixedLengthLeadingSpaces(Object obj, int length) {
	String string = obj.toString();
	while (string.length() < length) {
	    string = " " + string;
	}
	return string;
    }


    //Condense a Vector containing a list of numbers into as short a
    //String as possible
    public static String condenseNumberList(Vector list) {
	Vector aCopy = (Vector) list.clone();
	Collections.sort(aCopy);
	return condenseSortedNumberList(aCopy);
    }


    //Condense a Vector containing a *sorted* list of numbers into as
    //short a String as possible.
    public static String condenseSortedNumberList(Vector list) {
	
	if (list.isEmpty()) // no elements
		return ""; // return empty string
	
	int i = ((Integer) list.firstElement()).intValue(); //only one element
	if (list.size() == 1)                          //left then return
	    return(String.valueOf(i));                   //it as a string
	
	int j = ((Integer) list.get(1)).intValue(); //two elements left
	if (list.size() == 2)                  //return as comma pair
	    return(i + "," + j);
	
	//1st and second elements not in sequence, return 1st "," then rest
	if (j != i+1) 
	    return i + "," + 
		condenseSortedNumberList( new Vector( list.subList(1,list.size()) ) );
	
	//1st two elements in sequence but not third, return 1st,2nd then rest
	int k = ((Integer) list.get(2)).intValue(); //3rd element
	if (k != j+1)
	    return i + "," + j + "," + 
		condenseSortedNumberList( new Vector( list.subList(2,list.size()) ) );
	
	//three or more elements in sequence
	int l;
	for (l = 3; l < list.size() && ((Integer) list.get(l)).intValue()
		 == k+1; l++) { k++; }
	if ( l == list.size() )
	    return i + "-" + list.get(l-1);  //end of list reached
	return i + "-" + list.get(l-1) + "," + 
	    condenseSortedNumberList( new Vector( list.subList(l,list.size()) ) );
    }
    
    
    // truncate a float to the required number of significant digits
    // and return as a string.
    public static String truncateFloat( float f, int digits ) {
    	double power = Math.pow(10,digits);
    	return String.valueOf( Math.round(f * power) / power );
    }
    
    // truncate a double to the required number of significant digits
    // and return as a string.
    public static String truncateDouble( double d, int digits ) {
    	double power = Math.pow(10,digits);
    	return String.valueOf( Math.round(d * power) / power );
    }
}
