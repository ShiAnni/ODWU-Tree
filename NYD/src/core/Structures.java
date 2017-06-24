package core;
/** A collection of static methods related to data structures.
 *
 * Code originated from Paul Wheaton, see copyright notice below.
 *
 * Modified by Paul Crook for incorporation RL-JTP:
 *   (i) Stripped out all but subArray and Structures class.
 *  (ii) Code change to differentiate between upper and lower case characters in file
 *       names and classes - case matters under UNIX-like operating systems.
 *   
 *  - - - - - - - - - - - - - - - - - 
 *   
 *   Copyright (c) 1998-2000 Paul Wheaton
 *   
 *   You are welcome to do whatever you want to with this source file provided
 *   that you maintain this comment fragment (between the dashed lines).
 *   Modify it, change the package name, change the class name ... personal or business 
 *   use ...  sell it, share it ... add a copyright for the portions you add ...
 *   
 *   My goal in giving this away and maintaining the copyright is to hopefully direct 
 *   developers back to JavaRanch.
 *   
 *   The original source can be found at href=http://www.javaranch.com>JavaRanch
 *   
 *  - - - - - - - - - - - - - - - - - 
 *   
 * Modifications Copyright 2006 Paul A. Crook
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



public class Structures
{
    private Structures(){} // just to make sure nobody tries to instantiate this. <p>

    /** Get a sub array from a byte array. <P>

        @param b The original array.  It will not be modified. <p>

        @param index Where the new array is to be copied from. If index is 0, the very first byte
                     from b will be included.  If first is beyond the last byte in b, this
                     method will return null. <p>

        @param length The number of bytes to be copied. If there are fewer than length bytes left,
                      how ever many are left will be returned. <p>

        @return A new byte array or null if index is greater than the last element of b. <p>

    */
    public static byte[] subArray( byte[] b , int index , int length )
    {
        byte[] returnValue = null ;
        if ( index < b.length )
        {
            length = Math.min( length , b.length - index );
            returnValue = new byte[ length ] ;
            int i ;
            for( i = 0 ; i < length ; i++ )
            {
                returnValue[ i ] = b[ index + i ] ;
            }
        }
        return returnValue ;
    }

    /** Get a shallow sub array from an object array. <P>

        @param obj The original array.  It will not be modified. <p>

        @param index Where the new array is to be copied from. If index is 0, the very first object reference
                     from obj will be included.  If first is beyond the last object reference in obj, this
                     method will return null. <p>

        @param length The number of object references to be copied. If there are fewer than length references left,
                      how ever many are left will be returned. <p>

        @return A new object array or null if index is greater than the last element of obj. <p>

    */
    public static Object[] subArray( Object[] obj , int index , int length )
    {
        Object[] returnValue = null ;
        if ( index < obj.length )
        {
            length = Math.min( length , obj.length - index );
            returnValue = new Object[ length ] ;
            int i ;
            for( i = 0 ; i < length ; i++ )
            {
                returnValue[ i ] = obj[ index + i ] ;
            }
        }
        return returnValue ;
    }

}
