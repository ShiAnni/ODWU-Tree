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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * 
 * unwrap state-action value data dependency on StateActionPair class
 * thereby allowing older data to be re-wrapped using newer
 * version of StateActionPair class
 *  
 * Created on Oct 14, 2004 by Paul A. Crook
 *
 **/

public class UnwrapStateActionData {
	
	private static String usageString() {
		return( "\nusage: java UnwrapStateActionData OLD_FILE UNWRAPPED_FILE\n" );
	}
    
    public static void main(String[] args) {
    
        if (args.length != 2) {
            System.err.println( usageString() );           
            System.exit(1);
        }

        Hashtable hash = null;
        int actionSteps = 0;
        int runsCompleted = 0;

        try {
            FileInputStream fis = new FileInputStream(args[args.length - 2]);
            ObjectInputStream ois = new ObjectInputStream(fis);
            hash = (Hashtable) ois.readObject();
            actionSteps = ois.readInt();
            runsCompleted = ois.readInt();
            ois.close();
            fis.close();
        }
        catch (Exception e) {
            System.err.println(e);
            System.exit(1);
        }

        try {
            File file = new File(args[args.length - 1]);
            if (file.exists()) {
                file.delete();
            }
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            
            oos.writeInt(actionSteps);
            oos.writeInt(runsCompleted);
            
            for (Enumeration en = hash.keys(); en.hasMoreElements();) {
                StateActionPair sa = (StateActionPair) en.nextElement();
                Double value = (Double) hash.get(sa);
                
                Vector temp = new Vector();
                temp.add(sa.state);
                temp.add(sa.actionName);
                temp.add(value);
                oos.writeObject(temp);
            }
            
            oos.flush();
            oos.close();
            fos.close();
        }
        catch (Exception e) {
        	System.err.println("Problem writing out unwrapped state-action file:" + e);
            System.exit(1);
        }
         
         
    }
}
