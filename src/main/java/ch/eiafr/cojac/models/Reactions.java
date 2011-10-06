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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Reactions {
    //Do not inline, used by reflection, do not make final
    private static boolean filtering;

    private Reactions() {
        throw new AssertionError();
    }

    //Public to enable to display stats at the end of the application
    public static final ConcurrentMap<String, Long> EVENTS = new ConcurrentHashMap<String, Long>(25);

    public static void react(int reaction, String instructionName, String logFileName) {
        switch (ReactionType.get(reaction)) {
            case PRINT:
                printOverflow(instructionName);
                break;
            case PRINT_SMALLER:
                printOverflowSmaller(instructionName);
                break;
            case LOG:
                logOverflow(instructionName, logFileName);
                break;
            case LOG_SMALLER:
                logOverflowSmaller(instructionName, logFileName);
                break;
            case EXCEPTION:
                throwOverflow(instructionName);
                break;
            case CALLBACK:
                callbackOverflow(instructionName, logFileName);
                break;
        }
    }

    public static boolean filter(String location) {
        if (!filtering) {
            return true;
        }

        Long old = EVENTS.putIfAbsent(location, 1L);

        if (old != null) {
            EVENTS.put(location, old + 1);

            return false;
        }

        return true;
    }

    // identifier must match Methods.PRINT definition
    public static void printOverflow(String instructionName) {
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = 1;
        if (t.length > 1 && t[1].getMethodName().startsWith("cojacCheck")) {
            i = 2;
        } else if (t.length > 1 && t[1].toString().startsWith("ch.eiafr.cojac.models.")) {
            i = 3;
        }

        String location = "COJAC: " + instructionName + ' ' + t[i++].toString();

        if (filter(location)) {
            System.err.println(location);

            do {
                System.err.print('\t');
                System.err.println(t[i++].toString());
            } while (i < t.length);
        }
    }

    // identifier must match Methods.PRINT_SMALLER definition
    public static void printOverflowSmaller(String instructionName) {
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = 1;
        if (t.length > 1 && t[1].getMethodName().startsWith("cojacCheck")) {
            i = 2;
        } else if (t.length > 1 && t[1].toString().startsWith("ch.eiafr.cojac.models.")) {
            i = 3;
        }

        String location = "COJAC: " + instructionName + ' ' + t[i].toString();

        if (filter(location)) {
            System.err.println(location);
        }
    }

    // identifier must match Methods.LOG definition
    public static void logOverflow(String instructionName, String logFileName) {
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = 1;
        if (t.length > 1 && t[1].getMethodName().startsWith("cojacCheck")) {
            i = 2;
        } else if (t.length > 1 && t[1].toString().startsWith("ch.eiafr.cojac.models.")) {
            i = 3;
        }

        String location = "COJAC: " + instructionName + ' ' + t[i++].toString();

        if (filter(location)) {
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new FileWriter(logFileName, true));

                out.write(location);
                out.newLine();

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
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = 1;
        if (t.length > 1 && t[1].getMethodName().startsWith("cojacCheck")) {
            i = 2;
        } else if (t.length > 1 && t[1].toString().startsWith("ch.eiafr.cojac.models.")) {
            i = 3;
        }

        String location = "COJAC: " + instructionName + ' ' + t[i].toString();

        if (filter(location)) {
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
        StackTraceElement[] t = new Throwable().getStackTrace();

        int i = 1;
        if (t.length > 1 && t[1].getMethodName().startsWith("cojacCheck")) {
            i = 2;
        } else if (t.length > 1 && t[1].toString().startsWith("ch.eiafr.cojac.models.")) {
            i = 3;
        }

        String location = "COJAC: " + instructionName + ' ' + t[i].toString();

        if (filter(location)) {
            throw new ArithmeticException("COJAC: " + instructionName);
        }
    }

    // There is a very nasty dependence between the Cojac Eclipse Plugin
    // ch.eiafr.ecojac_core.AnnontationMgr.getRelevantElt()
    // and the precise path leading to the invocation of the callback... F. Bapst
    public static void callbackOverflow(String instructionName, String callbackName) {
        ReflectionUtils.invokeCallback(callbackName, instructionName);
    }

}