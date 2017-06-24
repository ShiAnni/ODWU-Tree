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
 * Collection of common static methods for dealing with string arguments.
 * 
 * (i) getArguments - get specified argument's value from args
 * 
 * P. A. Crook, 24th June 2003
 *  
 */

public class Args {

	//get specified argument's value from args (default: use comma as
	// separator)
	public static String getArgument(String args, String tag) throws Exception {
		return getArgument(args, tag, ",");
	}

	//get arguments values, specify separator to look out for
	public static String getArgument(String args, String tag, String separator)
			throws Exception {
		int start = args.indexOf(tag) + tag.length();
		if (start < tag.length())
			throw new Exception("Args.getArgument: bad tag `" + tag + "'.");
		int end = args.indexOf(separator, start);
		if (end == -1)
			end = args.length();
		return args.substring(start, end);
	}

}
