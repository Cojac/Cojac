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

package ch.eiafr.cojac;

import org.objectweb.asm.Opcodes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class Methods {
    private String print = "cojacNotifyPrint";
    private String log = "cojacNotifyLog";
    private String exception = "cojacNotifyException";

    // those definitions must match identifiers in cojac.models.Reactions
    public static final String PRINT = "printOverflow";
    public static final String PRINT_SMALLER = "printOverflowSmaller";
    public static final String LOG = "logOverflow";
    public static final String LOG_SMALLER = "logOverflowSmaller";
    public static final String THROW = "throwOverflow";
    //public static final String CALLBACK      = "callbackOverflow";

    private final Map<Integer, String> methods = new HashMap<Integer, String>(50);

    public Methods(Collection<String> classMethods) {
        super();

        log = uniqueMethodName(log, classMethods);
        print = uniqueMethodName(print, classMethods);
        exception = uniqueMethodName(exception, classMethods);

        for (Arg arg : Arg.values()) {
            if (arg.isOperator()) {
                methods.put(arg.opCode(), uniqueMethodName("cojacCheck" + arg, classMethods));
            }
        }

        methods.put(Opcodes.DCMPG, uniqueMethodName("cojacCheckDCMPG", classMethods));
        methods.put(Opcodes.DCMPL, uniqueMethodName("cojacCheckDCMPL", classMethods));
        methods.put(Opcodes.FCMPG, uniqueMethodName("cojacCheckFCMPG", classMethods));
        methods.put(Opcodes.FCMPL, uniqueMethodName("cojacCheckFCMPL", classMethods));
    }

    private static String uniqueMethodName(String rootName, Collection<String> methods) {
        String name = rootName;

        int suffix = 0;

        boolean change;
        do {
            change = false;

            if (methods.contains(name)) {
                name = rootName + suffix;
                suffix++;
                change = true;
            }
        } while (change);

        return name;
    }

    public String getMethod(int arg) {
        return methods.get(arg);
    }

    public String getPrint() {
        return print;
    }

    public String getLog() {
        return log;
    }

    public String getException() {
        return exception;
    }
}
