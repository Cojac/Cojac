/*
 * *
 *    Copyright 2011-2014 Frédéric Bapst & HEIA-FR students
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package ch.eiafr.cojac.unit.interval;

import java.util.Random;

import static java.lang.Double.*;

/**
 * Use to generate random value for the DoubleInterval Test
 */
public class DoubleUtils
{
    private static Random r = new Random();

    /**
     * @return a random positive double that's part of the interval [0.0;Double.MAX_VALUE]
     */
    public static double rndDouble()
    {
        return r.nextDouble() * MAX_VALUE;
    }

    /**
     * @param value reference fot the computation
     *              PRE : value is part of [0.0,Double.MAX_VALUE[
     *
     * @return a double bigger than value and positive
     *
     * For example with a value 20.0 the return could be 30.0
     */
    public static double getBiggerRndDouble(double value)
    {
        assert(value >= 0.0);
        assert(value < MAX_VALUE);
        if(value >= 0.0 && value < MAX_VALUE)
        {
            return  value + r.nextDouble() * (MAX_VALUE - value);
        }
        return NaN;
    }

    /**
     * @param value reference fot the computation
     *              PRE : value is part of [0.0,Double.MAX_VALUE[
     *
     * @return a double smaller than value and positive
     *
     * For example with a value 20.0 the return could be 10.0
     */
    public static double getSmallerRndDouble(double value)
    {
        assert(value >= 0.0);
        assert(value < MAX_VALUE);
        if(value >= 0.0 && value < MAX_VALUE)
        {
            return r.nextDouble() * value;
        }
        return NaN;
    }

    /**
     * @param value reference fot the computation
     *              PRE : value is part of ]-Double.MAX_VALUE,Double.MAX_VALUE[
     *
     * @return a double that's is bigger (in absolute) than value and negative
     *
     * For example with a value -20.0 the return could be -30.0
     */
    public static double getBiggerRndNegativeDouble(double value)
    {
        assert(value > -MAX_VALUE && value < MAX_VALUE);
        if(value > -MAX_VALUE && value < MAX_VALUE)
        {
            return - getBiggerRndDouble(Math.abs(value));
        }
        return NaN;
    }

    /**
     * @param value reference fot the computation
     *              PRE : value is part of ]-Double.MAX_VALUE,Double.MAX_VALUE[
     *
     * @return a double that's is smaller (in absolute) than value and negative
     *
     * For example with a value -20.0 the return could be -10.0
     */
    public static double getSmallerRndNegativeDouble(double value)
    {
        assert(value >= -MAX_VALUE && value < MAX_VALUE);
        if(value >= -MAX_VALUE && value < MAX_VALUE)
        {
            return - getSmallerRndDouble(Math.abs(value));
        }
        return NaN;
    }
}
