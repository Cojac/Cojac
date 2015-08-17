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

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import ch.eiafr.cojac.models.ReactionType;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class Args {
    private final Options options = Arg.createOptions();
    private final Map<Arg, ArgValue> values = new EnumMap<>(Arg.class);
    private final List<String> files = new ArrayList<>(5);
    private String[] appArgs;

    static final String DEFAULT_LOG_FILE_NAME = "COJAC_Report.log";
    static final String DEFAULT_OUTPUT_DIR = "." + File.separatorChar + "COJAC_Output" + File.separatorChar;
    static final String DEFAULT_JMX_HOST = "localhost";
    static final String DEFAULT_JMX_PORT = "5217";
    static final String DEFAULT_JMX_NAME = "COJAC";

    private static String USAGE =
             "java -javaagent:cojac.jar=\"[OPTIONS]\" YourApp [appArgs]\n"
           + "(version 1.4 - 2015 Aug 16)";

    public Args() {
        super();

        for (Arg arg : Arg.values()) {
            values.put(arg, new ArgValue(false, ""));
        }
    }

    public boolean parse(String[] args) {
        int index = -1;

        for (int i = 0; i < args.length; i++) {
            if ("--".equalsIgnoreCase(args[i])) {
                index = i;
                break;
            }
        }

        String[] cojacArgs;
        if (index >= 0) {
            int takeNext = index == args.length - 1 ? 0 : 1;
            cojacArgs = new String[index + takeNext];
            appArgs = new String[args.length - index - 1 - takeNext];
            System.arraycopy(args, 0, cojacArgs, 0, index);
            if (takeNext == 1) {
                cojacArgs[index] = args[index + 1];
            }
            System.arraycopy(args, index + 1 + takeNext, appArgs, 0, appArgs.length);
        } else {
            cojacArgs = args;
            appArgs = new String[0];
        }

        try {
            CommandLine commandLine = new GnuParser().parse(options, cojacArgs);
            for (Arg arg : Arg.values()) {
                if (commandLine.hasOption(arg.shortOpt())) {
                    values.get(arg).setSpecified();
                    values.get(arg).setValue(commandLine.getOptionValue(arg.shortOpt()));
                }
            }
            String[] remainingArgs = commandLine.getArgs();
            if (remainingArgs.length > 0) {
                files.add(remainingArgs[0]);
            }
            int nAppArgs = appArgs.length + remainingArgs.length - files.size();
            String[] auxAppArgs = appArgs;
            appArgs = new String[nAppArgs];
            int i = 0;
            for (; i + 1 < remainingArgs.length; i++) {
                appArgs[i] = remainingArgs[i + 1];
            }
            for (String auxAppArg : auxAppArgs) {
                appArgs[i++] = auxAppArg;
            }
        } catch (ParseException e) {
            System.out.println("Invalid command line.  Reason: " + e.getMessage());
            return false;
        }
        setDefaults();
        return true;
    }

    private void setDefaults() {
        if (!isSpecified(Arg.PATH)) {
            setValue(Arg.PATH, DEFAULT_OUTPUT_DIR);
        }

        if (isSpecified(Arg.NONE)) {
            disableAll();
        } else if (!areSomeCategoriesSelected() && !areSomeOpcodesSelected()) {
            specifyDefaultsOpCode();
        }

        if (isSpecified(Arg.LOG_FILE)) {
            if (values.get(Arg.LOG_FILE).getValue() == null) {
                values.get(Arg.LOG_FILE).setValue(DEFAULT_LOG_FILE_NAME);
            }
        }


        if (!(isSpecified(Arg.LOG_FILE) || isSpecified(Arg.EXCEPTION) || isSpecified(Arg.CALL_BACK))) {
            specify(Arg.PRINT);
        }

        if (isSpecified(Arg.NO_CANCELLATION)) {
            //TODO: consider -XnoCancellation
        }

        if (isOperationEnabled(Arg.JMX_ENABLE)) {
            if (values.get(Arg.JMX_HOST).getValue().equals("")) {
                values.get(Arg.JMX_HOST).setValue(DEFAULT_JMX_HOST);
            }
            if (values.get(Arg.JMX_PORT).getValue().equals("")) {
                values.get(Arg.JMX_PORT).setValue(DEFAULT_JMX_PORT);
            }
            if (values.get(Arg.JMX_NAME).getValue().equals("")) {
                values.get(Arg.JMX_NAME).setValue(DEFAULT_JMX_NAME);
            }
        }
    }

    private void disableAll() {
        for (Arg arg : Arg.values()) {
            if (arg.isOperator()) {
                unspecify(arg);
            }
        }
        unspecify(Arg.ALL);
        unspecify(Arg.INTS);
        unspecify(Arg.DOUBLES);
        unspecify(Arg.FLOATS);
        unspecify(Arg.LONGS);
        unspecify(Arg.MATHS);
        unspecify(Arg.CASTS);
    }

    private void specifyDefaultsOpCode() {
        specify(Arg.INTS);
        specify(Arg.LONGS);
        specify(Arg.CASTS);
        specify(Arg.FLOATS);
        specify(Arg.DOUBLES);
        specify(Arg.MATHS);
        specify(Arg.CASTS);
    }

    private boolean areSomeCategoriesSelected() {
        return isSpecified(Arg.INTS) || isSpecified(Arg.DOUBLES) || isSpecified(Arg.FLOATS) ||
            isSpecified(Arg.LONGS) || isSpecified(Arg.MATHS) || isSpecified(Arg.CASTS);
    }

    private boolean areSomeOpcodesSelected() {
        for (Arg arg : Arg.values()) {
            if (arg.isOperator() && isSpecified(arg)) {
                return true;
            }
        }
        return false;
    }

    public void printHelpAndExit() {
        new HelpFormatter().printHelp(USAGE, options);
        System.exit(0);
    }

    public boolean isSpecified(Arg arg) {
        return values.get(arg).isSpecified();
    }

    public boolean specify(Arg arg) {
        return values.get(arg).setSpecified();
    }

    public boolean unspecify(Arg arg) {
        return values.get(arg).setSpecified(false);
    }

    public String getValue(Arg arg) {
        return values.get(arg).getValue();
    }

    public void setValue(Arg arg, String value) {
        specify(arg);

        values.get(arg).setValue(value);
    }

    public boolean isOperationEnabled(Arg arg) {
        if (arg.ordinal() >= Arg.INTS.ordinal()) {
            return isSpecified(arg) || isSpecified(Arg.ALL) || arg.getParent() != null && isSpecified(arg.getParent());
        } 
        return isSpecified(arg) || arg.getParent() != null && isSpecified(arg.getParent());
    }

    public List<String> getFiles() {
        return files;
    }

    public String[] getAppArgs() {
        return appArgs;
    }
    
    public ReactionType getReactionType() {
        if (isSpecified(Arg.PRINT)) {
            return isSpecified(Arg.DETAILED_LOG) ? ReactionType.PRINT : ReactionType.PRINT_SMALLER;
        } else if (isSpecified(Arg.LOG_FILE)) {
            return isSpecified(Arg.DETAILED_LOG) ? ReactionType.LOG : ReactionType.LOG_SMALLER;
        } else if (isSpecified(Arg.EXCEPTION)) {
            return ReactionType.EXCEPTION;
        } else if (isSpecified(Arg.CALL_BACK)) {
            return ReactionType.CALLBACK;
        }

        throw new RuntimeException("no reaction is defined!");
    }


    private static final class ArgValue {
        private boolean specified;
        private String value;

        private ArgValue(boolean specified, String value) {
            super();

            this.specified = specified;
            this.value = value;
        }

        public boolean isSpecified() {
            return specified;
        }

        public String getValue() {
            return value;
        }

        public boolean setSpecified() {
            return setSpecified(true);
        }

        public boolean setSpecified(boolean specified) {
            this.specified = specified;

            return this.specified;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}