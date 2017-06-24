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


import java.io.*; // for file commands
import java.util.*; // for Hashtable

/**
 * OldHashTableFileReader
 * 
 * a (hopefully) useful tool that displays what's in hash table files
 * that have been written out by various act'n'learn methods.
 *
 * P.A.Crook 12 November 2002
 *
 * 27 November 2002 - demoted to 'OLD' when hashtables reformatted from
 * using separate State and Action keys and instead use
 * StateActionPair keys. This version kept to read old file.
 *
 **/

public class OldHashTableFileReader{

  public static void main(String[] args) {
    Hashtable hash = null;
    try {
      FileInputStream fis = new FileInputStream(args[0]);
      ObjectInputStream ois = new ObjectInputStream(fis);
      hash = (Hashtable) ois.readObject();
      ois.close();
      fis.close();
    }
    catch(Exception e) {
      System.err.println(e);
      System.exit(1);
    }
    boolean firstLine = true;
    for (Enumeration en = hash.keys(); en.hasMoreElements(); ) {
      State key = (State) en.nextElement();
      Hashtable values = (Hashtable) hash.get(key);
      if (firstLine) {
	System.out.println();
	System.out.print(makeFixedLength("State", 12));
	for(Enumeration en2 = values.keys(); en2.hasMoreElements(); ) {
	  String actionName = (String) en2.nextElement();
	  System.out.print( makeFixedLength(actionName,22) );
	}
	System.out.println();
	System.out.println();
	firstLine = false;
      }
      System.out.print(makeFixedLength(new Integer(key.hashCode()),7));
      for(Enumeration en2 = values.keys(); en2.hasMoreElements(); ) {
	String actionName = (String) en2.nextElement();
	System.out.print( makeFixedLength(values.get(actionName), 22) );
      }
      System.out.println();
    }
    System.out.println();
  }
  
  //make strings of a fixed length
  static String makeFixedLength(Object obj, int length) {
    String string = obj.toString();
    while (string.length() < length) {
      string = string + " ";
    }
    return string;
  }
}
