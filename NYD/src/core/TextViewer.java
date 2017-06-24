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


import java.util.*; // for observer and observable
import java.io.*; //read StringBuffer

/**
 *
 * TextViewer
 * ----------
 *
 * Implements a text viewer which can be used by various models to
 * send text to the terminal.
 *
 * Note: implemented using Model-View-Control design pattern, of
 * which this is just one Viewer.
 *
 * Paul A. Crook, 10th July 2002
 *
 **/

public class TextViewer extends GridWorldObserver {


  //no constructor for TextViewer
  //relies on default no argument constructor


  //overwrite register method - to write out some details
  public void register(Observable model){
    // register with the model
    super.register(model);
    // tell everyone about the starting state of the model
    System.out.println("Text Viewer registered with " + model + ".");
    System.out.println();
  }


  public void update(Observable model, Object data) {
    System.out.print( data.toString() );
    //waitForReturnKey();
  }


  //wait for return to be presses - used for debugging	
  void waitForReturnKey() {
    BufferedReader input = new 
      BufferedReader(new InputStreamReader(System.in));
    try{
      while( !input.ready() ){} //hangs round waiting for return
      input.read();             // to be pressed
    }
    catch (IOException e) {}
  }

}




