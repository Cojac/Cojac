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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;

import static org.objectweb.asm.util.Printer.OPCODES;

import ch.eiafr.cojac.models.Reactions;

public final class InstrumentationStats extends NotificationBroadcasterSupport implements ICojacMXBean {
    private final Map<Integer, Counter> counters = new HashMap<Integer, Counter>();
    private long startTime;
    private final Object BLACKLIST_LOCK = new Object();
    private List<String> blacklist = new ArrayList<String>();
    private long changes = 0;

    @Override
    public Map<String, Integer> getCountersMBean() {
        Map<String, Integer> ctrs = new HashMap<String, Integer>();

        synchronized (counters) {
            for (Entry<Integer, Counter> ctr : counters.entrySet()) {
                ctrs.put(stringFromOpcode(ctr.getKey()), ctr.getValue().getValue());
            }
        }

        return ctrs;
    }

    @Override
    public List<String> getBlacklist() {
        synchronized (blacklist) {
            return blacklist;
        }
    }

    @Override
    public Map<String, Long> getEvent() {
        return Reactions.EVENTS;
    }

    @Override
    public void start() {
        Reactions.react.set(true);
    }

    @Override
    public void stop() {
        Reactions.react.set(false);
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        String[] ntfTypes = new String[]{AttributeChangeNotification.ATTRIBUTE_CHANGE};
        String ntfClassName = AttributeChangeNotification.class.getName();
        String ntfDescription = "A COJAC event occured !";
        MBeanNotificationInfo ntfInfo = new MBeanNotificationInfo(ntfTypes, ntfClassName, ntfDescription);
        return new MBeanNotificationInfo[]{ntfInfo};
    }

    public void notifyChange(String location) {
        AttributeChangeNotification notification = new AttributeChangeNotification(this, changes++, System.currentTimeMillis(), location, "EVENT", "Map<String, Long>", null, Reactions.EVENTS);
        sendNotification(notification);
    }

    protected void addBlackList(String annotated) {
        synchronized (BLACKLIST_LOCK) {
            blacklist.add(annotated);
        }
    }

    int getCounterValue(int arg) {
        check(arg);

        return counters.get(arg).getValue();
    }

    public void incrementCounterValue(int arg) {
        check(arg);

        counters.get(arg).increment();
    }

    private void check(int opcode) {
        if (!counters.containsKey(opcode)) {
            counters.put(opcode, new Counter());
        }
    }

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public long getDuration() {
        return System.nanoTime() - startTime;
    }

    public void printInstrumentationStats(Args args) {
        StringBuilder builder = new StringBuilder(2500);

        if (startTime > 0) {
            builder.append("COJAC Instrumentation duration : ").append(getDuration()).append(" ns").append('\n');
        }

        builder.append("COJAC Number of instructions checked: \n");

        for (int opcode=0; opcode<OPCODES.length; opcode++) {
            if (!counters.containsKey(opcode))
                continue;
            int count=counters.get(opcode).getValue();
            if (count==0)
                continue;
            builder.append('\t').append(stringFromOpcode(opcode)).append(" : ").append(count).append('\n');
        }

        write(args, builder);
    }

    public static void printRuntimeStats(Args args, Map<String, Long> events) {
        StringBuilder builder = new StringBuilder(2500);

        if (!events.isEmpty()) {
            builder.append("COJAC Summary: Problematic instructions: \n");

            List<Entry<String, Long>> sortedEvents = new ArrayList<Entry<String, Long>>(events.entrySet());

            Collections.sort(sortedEvents, new CounterComparator());

            for (Map.Entry<String, Long> instructions : sortedEvents) {
                builder.append('\t').append(instructions.getValue()).
                    append(" times -> ").append(instructions.getKey()).append('\n');
            }
        }

        write(args, builder);
    }

    private static void write(Args args, StringBuilder builder) {
        if (args.isSpecified(Arg.LOG_FILE)) {
            String logFileName = args.getValue(Arg.LOG_FILE);

            writeToFile(builder, logFileName);
        } else {
            writeToConsole(builder);
        }
    }

    public static void writeToFile(StringBuilder builder, String logFileName) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(logFileName, true));
            out.write(builder.toString());
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

    public static void writeToConsole(StringBuilder builder) {
        System.out.println(builder.toString());
    }

    protected static final class Counter {
        private int value;

        private int getValue() {
            return value;
        }

        public void increment() {
            value++;
        }
    }

    private static final class CounterComparator implements Comparator<Entry<String, Long>> {
        @Override
        public int compare(Entry<String, Long> o1, Entry<String, Long> o2) {
            return -1 * o1.getValue().compareTo(o2.getValue());
        }
    }
    
    private static String stringFromOpcode(int opcode) {
        if (opcode < 0 || opcode >= OPCODES.length)
            return "REALLY_UNKNOWN_OPCODE...!!! "+opcode;
        return OPCODES[opcode];
    }
}