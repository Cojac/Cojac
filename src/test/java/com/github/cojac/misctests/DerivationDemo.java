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

// Run with AutoDiff Wrapper: 
//  java -javaagent:cojac.jar="-Ra"


package com.github.cojac.misctests;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.TreeMap;

public class DerivationDemo{
    private static int nbrTestPassed = 0;
    private static final double epsilon = 0.1;
    private static Map<String, Method> methods;

    static{
        methods = new TreeMap<>();
        for( Method method : DerivationDemo.class.getDeclaredMethods() ){
            methods.put( method.getName(), method );
        }//end for
    }

    // ----------------------------------------------------

    // TODO: printf ...

    public static void main( String[] args ){
        main1( args );
        runDerivationTest();

        System.out.println( "\n" );
        if( nbrTestPassed == 13 ){
            System.out.println( "All test passed successfully !" );
        }
    }

    // ----------------------------------------------------
    static double someFunction( double x, double a, double b ){
        double res = a * x * x;
        res = res + b * x;
        res = res + 1;
        return res;
    }

    public static void main1( String[] args ){
        // TODO : why the args ??
        double x = 2;
        x = asDerivativeVar( x );
        double y = someFunction( x, 3, 4 );
        System.out.println( "f(2):  " + y );
        System.out.println( "f'(2): " + COJAC_MAGIC_derivative( y ) );
    }

    public static void runDerivationTest(){
        // run the derivation functions 1 to 13
        double[] xs = new double[]{ 4.0, 4.0, 4.0, 1.0, 4.0, 4.0, 4.0, 4.0, 0.4, 4.0, 4.0, 4.0, 4.0 };
        for( int i = 1; i <= 13; i++ ){
            try{
                runFx( i, xs[ i - 1 ] );
            }catch( InvocationTargetException | IllegalAccessException | NoSuchMethodException e ){
                e.printStackTrace();
            }
        }//end for

    }

    // ----------------------------------------------------
    public static void runFx( int fx, double x ) throws InvocationTargetException, IllegalAccessException,
            NoSuchMethodException{

        Method f = methods.get( "f" + fx );
        Method df = methods.get( "df" + fx );

        if( f == null || df == null ){
            throw new NoSuchMethodException( fx + " not found..." );
        }

        System.out.printf( "Function %d\n", fx );
        x = asDerivativeVar( x );
        double res = ( double ) f.invoke( DerivationDemo.class, x );
        double dfRes = ( double ) df.invoke( DerivationDemo.class, x );
        double dRes = COJAC_MAGIC_derivative( res );
        System.out.printf( "f%d(x) = %s\n", fx, res );
        System.out.printf( "f%d'(x) = %s should be %s\n", fx, COJAC_MAGIC_derivative( res ), dfRes );

        if( Math.abs( dRes - dfRes ) < epsilon ){
            System.out.println( "Test ok" );
            nbrTestPassed++;
        }
    }

    // ----------------------------------------------------

    /*
     * Magic method, see cojac implementation of those in the DerivationDouble
     */
    public static double COJAC_MAGIC_derivative( double a ){
        return 0;
    }

    public static double asDerivativeVar( double a ){
        return a;
    }

    public static double f1( double x ){
        return 4.0 * Math.pow( x, 3.0 );
    }

    public static double df1( double x ){
        return 12.0 * Math.pow( x, 2.0 );
    }

    public static double f2( double x ){
        return Math.pow( x, x );
    }

    public static double df2( double x ){
        return Math.pow( x, x ) * ( Math.log( x ) + 1.0 );
    }

    public static double f3( double x ){
        return Math.sin( x ) + 4.0 * x;
    }

    public static double df3( double x ){
        return Math.cos( x ) + 4.0;
    }

    public static double f4( double x ){
        return Math.cos( Math.pow( x, 2.0 ) ) - 4.0 * x + 3;
    }

    public static double df4( double x ){
        return -Math.sin( Math.pow( x, 2.0 ) ) * 2.0 * x - 4.0;
    }

    public static double f5( double x ){
        return 4.0 * x + Math.tan( x );
    }

    public static double df5( double x ){
        return 1.0 + Math.pow( Math.tan( x ), 2.0 ) + 4.0;
    }

    public static double f6( double x ){
        return 1 / Math.sqrt( x );
    }

    public static double df6( double x ){
        return -1.0 / ( 2.0 * Math.pow( x, 3.0 / 2.0 ) );
    }

    public static double f7( double x ){
        return -Math.log( x );
    }

    public static double df7( double x ){
        return -1.0 / x;
    }

    public static double f8( double x ){
        return Math.sinh( x ) + Math.cosh( x ) + Math.tanh( x );
    }

    public static double df8( double x ){
        return Math.sinh( x ) + Math.cosh( x ) + 1.0 - Math.pow( Math.tanh( x ), 2.0 );
    }

    public static double f9( double x ){
        return Math.asin( x ) + Math.acos( x ) + Math.atan( x );
    }

    public static double df9( double x ){
        return 1.0 / ( Math.pow( x, 2.0 ) + 1.0 );
    }

    public static double f10( double x ){
        return Math.exp( Math.pow( x, 2.0 ) );
    }

    public static double df10( double x ){
        return Math.exp( Math.pow( x, 2.0 ) ) * 2.0 * x;
    }

    public static double f11( double x ){
        return 3.0 % x;
    }

    public static double df11( double x ){
        return Double.NaN;
    }

    public static double f12( double x ){
        return x % 3.0;
    }

    public static double df12( double x ){
        return 1.0;
    }

    public static double f13( double x ){
        return Math.abs( x );
    }

    public static double df13( double x ){
        return x < 0.0 ? -1.0 : 1.0;
    }

}
