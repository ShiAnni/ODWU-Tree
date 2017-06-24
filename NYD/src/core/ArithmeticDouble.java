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


import java.io.*; // for Serializable

/**
 * ArithmeticDouble
 *
 * an Object wrapper for the primitive type double which allows for basic
 * arithmetic methods.
 *
 * P.A.Crook, 14th Janauary 2004
 **/
public class ArithmeticDouble implements Serializable, Cloneable {
    public double value;

    //constructors
    public ArithmeticDouble(double value) {
        this.value = value;
    }

    public ArithmeticDouble(Double value) {
        this.value = value.doubleValue();
    }

    //increment
    public void increment() {
        value++;
    }

    //decrement
    public void decrement() {
        value--;
    }

    //plus
    public void plus(double increment) {
        value += increment;
    }

    public void plus(ArithmeticDouble increment) {
        value += increment.value;
    }

    public void plus(ArithmeticFloat increment) {
        value += increment.value;
    }

    public void plus(int increment) {
        value += increment;
    }

    public void plus(ArithmeticInteger increment) {
        value += increment.value;
    }

    public void plus(Number increment) {
        value += increment.floatValue();
    }

    //minus
    public void minus(double decrement) {
        value -= decrement;
    }

    public void minus(ArithmeticDouble decrement) {
        value -= decrement.value;
    }

    public void minus(ArithmeticFloat decrement) {
        value -= decrement.value;
    }

    public void minus(int decrement) {
        value -= decrement;
    }

    public void minus(ArithmeticInteger decrement) {
        value -= decrement.value;
    }

    public void minus(Number decrement) {
        value -= decrement.floatValue();
    }

    public String toString() {
        return String.valueOf(value);
    }

    public Object clone() {
        try {
            ArithmeticDouble aClone = (ArithmeticDouble) super.clone();
            return aClone;
        }
         catch (CloneNotSupportedException e) {
            throw new InternalError(e.toString());
        }
    }
}
