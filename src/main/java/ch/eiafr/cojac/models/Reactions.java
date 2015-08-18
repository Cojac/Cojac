/*
 * *
 *    Copyright 2011 Baptiste Wicht & Frédéric Bapst
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

package ch.eiafr.cojac.models;

import ch.eiafr.cojac.utils.ReflectionUtils;
import ch.eiafr.cojac.InstrumentationStats;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Reactions {
    //-------------------------------------------------------
    // These fields are used by reflection - Do not inline, do not make final
    private static boolean filtering;

    public static InstrumentationStats stats;

    public static ReactionType theReactionType=ReactionType.PRINT;
    public static String theLogFilename="cojac_log.txt";
    //-------------------------------------------------------
    
    public static final AtomicBoolean react = new AtomicBoolean(true);

    private Reactions() {
        throw new AssertionError();
    }

    //Public to enable to display stats at the end of the application
    public static final ConcurrentMap<String, Long> EVENTS = new ConcurrentHashMap<String, Long>(25);

    //TODO: check whether we can get rid of first/last parameter of react()
    public static void react(int reaction, String message, String logFileName) {
        switch (ReactionType.get(reaction)) {
            case PRINT:
                printOverflow(message);
                break;
            case PRINT_SMALLER:
                printOverflowSmaller(message);
                break;
            case LOG:
                logOverflow(message, logFileName);
                break;
            case LOG_SMALLER:
                logOverflowSmaller(message, logFileName);
                break;
            case EXCEPTION:
                throwOverflow(message);
                break;
            case CALLBACK:
                callbackOverflow(message, logFileName);
                break;
        }
    }

    private static boolean passesFilter(String location) {  //BAPST TODO: review use of 'filtering'
        Long old = EVENTS.putIfAbsent(location, 1L);

        if (old != null) {
            EVENTS.put(location, old + 1);
        }

        if (filtering && old == null) {
            return true;
        } else if (filtering) {
            return false;
        }

        if (stats!=null) stats.notifyChange(location);

        return true;
    }

    private static int reasonableIndex(StackTraceElement[] t) {
        int i = 0;
        while(true) {
            if (i==t.length-1) break; 
            String s=t[i].toString();
            if (!s.startsWith("ch.eiafr.cojac.models") &&
                !t[1].getMethodName().startsWith("cojacCheck")) break;
            i++;
        }
        return i;
    }
    
    // identifier must match Methods.PRINT definition
    public static void printOverflow(String instructionName) {
        if (!react.get())
            return;
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = reasonableIndex(t); // 1;

        String location = "COJAC: " + instructionName + ' ' + t[i++].toString();

        if (passesFilter(location)) {
            System.err.println(location);
            i=0;
            do {
                System.err.print('\t');
                System.err.println(t[i++].toString());
            } while (i < t.length);
        }
    }

    // identifier must match Methods.PRINT_SMALLER definition
    public static void printOverflowSmaller(String instructionName) {
        if (!react.get())
            return;
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = reasonableIndex(t);

        String location = "COJAC: " + instructionName + ' ' + t[i].toString();

        if (passesFilter(location)) {
            System.err.println(location);
        }
    }

    // identifier must match Methods.LOG definition
    public static void logOverflow(String instructionName, String logFileName) {
        if (!react.get())
            return;
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = reasonableIndex(t);

        String location = "COJAC: " + instructionName + ' ' + t[i++].toString();

        if (passesFilter(location)) {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(logFileName, true));

                out.write(location);
                out.newLine();
                i=0;
                do {
                    out.write('\t');
                    out.write(t[i++].toString());
                    out.newLine();
                } while (i < t.length);

                out.newLine();
            } catch (Exception e) {
                System.err.print("COJAC: Error while logging: ");
                System.err.println(e.getMessage());
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        System.err.print("COJAC: Error while logging: ");
                        System.err.println(e.getMessage());
                    }
                }
            }
        }
    }

    // identifier must match Methods.LOG_SMALLER definition
    public static void logOverflowSmaller(String instructionName, String logFileName) {
        if (!react.get())
            return;
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = reasonableIndex(t);

        String location = "COJAC: " + instructionName + ' ' + t[i].toString();

        if (passesFilter(location)) {
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(logFileName, true));

                out.write(location);
                out.newLine();
                out.close();
            } catch (Exception e) {
                System.err.print("COJAC: Error while logging: ");
                System.err.println(e.getMessage());
            }
        }
    }

    // identifier must match Methods.THROW definition
    public static void throwOverflow(String instructionName) {
        if (!react.get())
            return;
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = reasonableIndex(t);

        String location = "COJAC: " + instructionName + ' ' + t[i].toString();

        if (passesFilter(location)) {
            throw new ArithmeticException("COJAC: " + instructionName);
        }
    }

    // There is a very nasty dependence between the Cojac Eclipse Plugin
    // ch.eiafr.ecojac_core.AnnontationMgr.getRelevantElt()
    // and the precise path leading to the invocation of the callback... F. Bapst
    public static void callbackOverflow(String instructionName, String callbackName) {
        ReflectionUtils.invokeCallback(callbackName, instructionName);
    }
    
    public static void react(String message) {
        react(theReactionType.ordinal(), message, theLogFilename);
    }

}