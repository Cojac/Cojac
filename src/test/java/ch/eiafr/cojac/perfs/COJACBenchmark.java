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

package ch.eiafr.cojac.perfs;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.CojacClassLoader;
import ch.eiafr.cojac.perfs.opcodes.*;
import ch.eiafr.cojac.perfs.scimark.SciMark;
import ch.eiafr.cojac.Agent;
import ch.eiafr.cojac.CojacReferences;
import ch.eiafr.cojac.CojacReferences.CojacReferencesBuilder;
import ch.eiafr.cojac.unit.AgentTest;
import com.wicht.benchmark.utils.Benchs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.Callable;

public class COJACBenchmark {
    private static final boolean BENCH_VARIABLES = false;
    private static final boolean BENCH_FRAMES = false;

    public static void main(String[] args) throws Exception {
        System.out.println("COJAC Benchmark");

        bench(52, "IADD Benchmark", new IADDCallable(), "ch.eiafr.cojac.perfs.opcodes.IADDCallable");
        bench(52, "ISUB Benchmark", new ISUBCallable(), "ch.eiafr.cojac.perfs.opcodes.ISUBCallable");
        bench(52, "IMUL Benchmark", new IMULCallable(), "ch.eiafr.cojac.perfs.opcodes.IMULCallable");
        bench(52, "IDIV Benchmark", new IDIVCallable(), "ch.eiafr.cojac.perfs.opcodes.IDIVCallable");

        bench(52, "FADD Benchmark", new FADDCallable(), "ch.eiafr.cojac.perfs.opcodes.FADDCallable");
        bench(52, "FSUB Benchmark", new FSUBCallable(), "ch.eiafr.cojac.perfs.opcodes.FSUBCallable");
        bench(52, "FMUL Benchmark", new FMULCallable(), "ch.eiafr.cojac.perfs.opcodes.FMULCallable");
        bench(52, "FDIV Benchmark", new FDIVCallable(), "ch.eiafr.cojac.perfs.opcodes.FDIVCallable");
        bench(52, "FREM Benchmark", new FREMCallable(), "ch.eiafr.cojac.perfs.opcodes.FREMCallable");

        bench(52, "LADD Benchmark", new LADDCallable(), "ch.eiafr.cojac.perfs.opcodes.LADDCallable");
        bench(52, "LSUB Benchmark", new LSUBCallable(), "ch.eiafr.cojac.perfs.opcodes.LSUBCallable");
        bench(29, "LMUL Benchmark", new LMULCallable(), "ch.eiafr.cojac.perfs.opcodes.LMULCallable");
        bench(52, "LDIV Benchmark", new LDIVCallable(), "ch.eiafr.cojac.perfs.opcodes.LDIVCallable");

        bench(52, "DADD Benchmark", new DADDCallable(), "ch.eiafr.cojac.perfs.opcodes.DADDCallable");
        bench(52, "DSUB Benchmark", new DSUBCallable(), "ch.eiafr.cojac.perfs.opcodes.DSUBCallable");
        bench(52, "DMUL Benchmark", new DMULCallable(), "ch.eiafr.cojac.perfs.opcodes.DMULCallable");
        bench(52, "DDIV Benchmark", new DDIVCallable(), "ch.eiafr.cojac.perfs.opcodes.DDIVCallable");
        bench(52, "DREM Benchmark", new DREMCallable(), "ch.eiafr.cojac.perfs.opcodes.DREMCallable");
        bench(52, "DCMP Benchmark", new DCMPCallable(), "ch.eiafr.cojac.perfs.opcodes.IADDCallable");

        bench(1, "Rabin Karp", new StringSearchingRunnable(), "ch.eiafr.cojac.perfs.StringSearchingRunnable");
        bench(1, "Sweeping Plane", new SweepingSorterRunnable(), "ch.eiafr.cojac.perfs.SweepingSorterRunnable");
        bench(1, "Traveling Salesman", new TravelingSalesmanRunnable(), "ch.eiafr.cojac.perfs.TravelingSalesmanRunnable");

        BufferedImage image1 = ImageIO.read(COJACBenchmark.class.getResource("/images/matthew2.jpg"));
        benchWithImages(1, "FFT", new FFTRunnable(), "ch.eiafr.cojac.perfs.FFTRunnable", image1);

        BufferedImage image2 = ImageIO.read(COJACBenchmark.class.getResource("/images/alessandra.jpg"));
        benchWithImages(1, "Box Blur", new BoxBlurRunnable(), "ch.eiafr.cojac.perfs.BoxBlurRunnable", image2);

        bench(1, "Linpack", new LinpackRunnable(), "ch.eiafr.cojac.perfs.LinpackRunnable");

        bench(1, "SciMark FFT", new SciMarkFFTRunnable(), "ch.eiafr.cojac.perfs.SciMarkFFTRunnable");
        bench(1, "SciMark LU", new SciMarkLURunnable(), "ch.eiafr.cojac.perfs.SciMarkLURunnable");
        bench(1, "SciMark Monte Carlo", new SciMarkMonteCarloRunnable(), "ch.eiafr.cojac.perfs.SciMarkMonteCarloRunnable");
        bench(1, "SciMark SOR", new SciMarkSORRunnable(), "ch.eiafr.cojac.perfs.SciMarkSORRunnable");
        bench(1, "SciMark Sparse Mat Mult", new SciMarkSparseMatmultRunnable(), "ch.eiafr.cojac.perfs.SciMarkSparseMatmultRunnable");

        benchSum();
        benchSort();

        linpackBenchmark();
        sciMarkBenchmark();
    }

    private static void benchSum() throws Exception {
        int arraySize = 10000000;

        benchWithArrays(1, "Sum (Foreach)", new IntForeachSumCallable(), "ch.eiafr.cojac.perfs.IntForeachSumCallable", arraySize);
        benchWithArrays(1, "Sum (For)", new IntForSumCallable(), "ch.eiafr.cojac.perfs.IntForSumCallable", arraySize);
    }

    private static void benchSort() throws Exception {
        int arraySize = 1000000;

        benchWithArrays(1, "Sort (Java)", new JavaSortRunnable(), "ch.eiafr.cojac.perfs.JavaSortRunnable", arraySize);
        benchWithArrays(1, "Quicksort", new QuickSortRunnable(), "ch.eiafr.cojac.perfs.QuickSortRunnable", arraySize);
        benchWithArrays(1, "Shell Sort", new ShellSortRunnable(), "ch.eiafr.cojac.perfs.ShellSortRunnable", arraySize);
    }

    private static void linpackBenchmark() throws Exception {
        System.out.println("Start linpack not instrumented");
        new Linpack().run_benchmark();

        System.out.println("Start linpack instrumented");
        Object linpack = getFromClassLoader("ch.eiafr.cojac.perfs.Linpack", false, false, false);
        Method m = linpack.getClass().getMethod("run_benchmark");

        m.invoke(linpack);
    }

    private static void sciMarkBenchmark() throws Exception {
        System.out.println("Start SciMMark2 not instrumented");
        new SciMark().run(false);

        System.out.println("Start SciMMark2 instrumented");
        Object scimark = getFromClassLoader("ch.eiafr.cojac.perfs.scimark.SciMark", false, false, false);
        Method m = scimark.getClass().getMethod("run", Boolean.TYPE);

        m.invoke(scimark, false);
    }

    private static void setArray(int[] array, Object instrumented) throws Exception {
        Class<?> cls = instrumented.getClass();

        Method m = cls.getMethod("setArray", int[].class);

        m.invoke(instrumented, array);
    }

    private static void setImage(BufferedImage image, Object instrumented) throws Exception {
        Class<?> cls = instrumented.getClass();

        Method m = cls.getMethod("setImage", BufferedImage.class);

        m.invoke(instrumented, image);
    }

    private static int[] generateIntRandomArray(int size) {
        int[] array = new int[size];

        Random random = new Random();

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(1000) - 500;
        }

        return array;
    }

    private static void bench(int actions, String title, Callable<?> runnable, String cls) throws Exception {
        Benchs benchs = new Benchs(title);

        initBench(actions, benchs);

        benchs.bench("Instrumented", COJACBenchmark.<Callable<?>>getFromClassLoader(cls, false, false, false));
        benchs.bench("WASTE_SIZE", COJACBenchmark.<Callable<?>>getFromClassLoader(cls, true, false, false));
        benchAgentCallable("Agent", benchs, cls, false, false, false);
        benchAgentCallable("Agent WASTE_SIZE", benchs, cls, true, false, false);
        if (BENCH_FRAMES)
            benchs.bench("Frames", COJACBenchmark.<Callable<?>>getFromClassLoader(cls, false, true, false));
        if (BENCH_VARIABLES)
            benchs.bench("Variables", COJACBenchmark.<Callable<?>>getFromClassLoader(cls, false, false, true));
        benchs.bench("Not instrumented", runnable);

        benchs.generateCharts(false);
        benchs.printResults();
    }

    private static void bench(int actions, String title, Runnable runnable, String cls) throws Exception {
        Benchs benchs = new Benchs(title);

        initBench(actions, benchs);

        benchs.bench("Instrumented", COJACBenchmark.<Runnable>getFromClassLoader(cls, false, false, false));
        benchs.bench("WASTE_SIZE", COJACBenchmark.<Runnable>getFromClassLoader(cls, true, false, false));
        benchAgentRunnable("Agent",            benchs, cls, false, false, false);
        benchAgentRunnable("Agent WASTE_SIZE", benchs, cls, true, false, false);
        if (BENCH_FRAMES)
            benchs.bench("Frames", COJACBenchmark.<Runnable>getFromClassLoader(cls, false, true, false));
        if (BENCH_VARIABLES)
            benchs.bench("Variables", COJACBenchmark.<Runnable>getFromClassLoader(cls, false, false, true));
        benchs.bench("Not instrumented", runnable);

        benchs.generateCharts(false);
        benchs.printResults();
    }

    private static void benchWithArrays(int actions, String title, Runnable runnable, String cls, int size) throws Exception {
        Benchs benchs = new Benchs(title);

        initBench(actions, benchs);

        Runnable run = getFromClassLoader(cls, false, false, false);
        setArray(generateIntRandomArray(size), run);
        benchs.bench("Instrumented", run);

        benchAgentArrayRunnable("Agent", benchs, cls, false, false, false, size);

        run = getFromClassLoader(cls, true, false, false);
        setArray(generateIntRandomArray(size), run);
        benchs.bench("WASTE_SIZE", run);

        benchAgentArrayRunnable("Agent WASTE_SIZE", benchs, cls, true, false, false, size);

        if (BENCH_FRAMES) {
            run = getFromClassLoader(cls, false, true, false);
            setArray(generateIntRandomArray(size), run);
            benchs.bench("Frames", run);
        }

        if (BENCH_VARIABLES) {
            run = getFromClassLoader(cls, false, false, true);
            setArray(generateIntRandomArray(size), run);
            benchs.bench("Variables", run);
        }

        setArray(generateIntRandomArray(size), runnable);
        benchs.bench("Not instrumented", runnable);

        benchs.generateCharts(false);
        benchs.printResults();
    }

    private static void benchWithArrays(int actions, String title, Callable<?> runnable, String cls, int size) throws Exception {
        Benchs benchs = new Benchs(title);

        initBench(actions, benchs);

        Callable<?> run = getFromClassLoader(cls, false, false, false);
        setArray(generateIntRandomArray(size), run);
        benchs.bench("Instrumented", run);

        benchAgentArrayCallable("Agent", benchs, cls, false, false, false, size);

        run = getFromClassLoader(cls, true, false, false);
        setArray(generateIntRandomArray(size), run);
        benchs.bench("WASTE_SIZE", run);

        benchAgentArrayCallable("Agent WASTE_SIZE", benchs, cls, true, false, false, size);

        if (BENCH_FRAMES) {
            run = getFromClassLoader(cls, false, true, false);
            setArray(generateIntRandomArray(size), run);
            benchs.bench("Frames", run);
        }

        if (BENCH_VARIABLES) {
            run = getFromClassLoader(cls, false, false, true);
            setArray(generateIntRandomArray(size), run);
            benchs.bench("Variables", run);
        }

        setArray(generateIntRandomArray(size), runnable);
        benchs.bench("Not instrumented", runnable);

        benchs.generateCharts(false);
        benchs.printResults();
    }

    private static void benchWithImages(int actions, String title, Runnable runnable, String cls, BufferedImage bufferedImage) throws Exception {
        Benchs benchs = new Benchs(title);

        initBench(actions, benchs);

        setImage(bufferedImage, runnable);

        Runnable run = getFromClassLoader(cls, false, false, false);
        setImage(bufferedImage, run);
        benchs.bench("Instrumented", run);

        benchAgentImage("Agent", benchs, cls, false, false, false, bufferedImage);

        run = getFromClassLoader(cls, true, false, false);
        setImage(bufferedImage, run);
        benchs.bench("WASTE_SIZE", run);

        benchAgentImage("Agent WASTE_SIZE", benchs, cls, true, false, false, bufferedImage);

        if (BENCH_FRAMES) {
            run = getFromClassLoader(cls, false, true, false);
            setImage(bufferedImage, run);
            benchs.bench("Frames", run);
        }

        if (BENCH_VARIABLES) {
            run = getFromClassLoader(cls, false, false, true);
            setImage(bufferedImage, run);
            benchs.bench("Variables", run);
        }
        benchs.bench("Not instrumented", runnable);

        benchs.generateCharts(false);
        benchs.printResults();
    }

    private static void initBench(int actions, Benchs benchs) {
        //benchs.getParams().setNumberMeasurements(1);
        benchs.getParams().setNumberActions(actions);
        benchs.setExclusionFactor(1000D);
        benchs.setConsoleResults(false);
        benchs.setGraphDimension(800, 600);

        File graphFolder = new File(System.getProperty("user.dir"), "graphs");

        if (!((graphFolder.exists() && graphFolder.isDirectory()) || graphFolder.mkdirs())) {
            System.err.println("Unable to create the folder for the graph, use \"user.dir\" as graph folder");
            graphFolder = new File(System.getProperty("user.dir"));
        }

        benchs.setFolder(graphFolder.getAbsolutePath());
    }

    private static <T> T getFromClassLoader(String cls, boolean wasteSize, boolean frames, boolean variables) throws Exception {

        CojacReferencesBuilder builder = new CojacReferencesBuilder(getArgs(wasteSize, frames, variables));
        ClassLoader classLoader = new CojacClassLoader(new URL[0], builder);
        Class<?> instanceClass = classLoader.loadClass(cls);
        return (T) instanceClass.newInstance();
    }

    private static <T> T getFromAgentClassLoader(String cls, boolean wasteSize, boolean frames, boolean variables) throws Exception {
        Class<?> instanceClass = ClassLoader.getSystemClassLoader().loadClass(cls);
        AgentTest.instrumentation.retransformClasses(instanceClass);
        return (T) instanceClass.newInstance();
    }

    private static Agent benchAgent(String cls, boolean wasteSize, boolean frames, boolean variables) throws Exception {
        CojacReferencesBuilder builder = new CojacReferencesBuilder(getArgs(wasteSize, frames, variables));
        builder.setSplitter(new CojacReferences.AgentSplitter());
        Agent agent = new Agent(builder.build());
        AgentTest.instrumentation.addTransformer(agent, true);
        return agent;
    }

    private static void benchAgentCallable(String name, Benchs benchs, String cls, boolean wasteSize, boolean frames, boolean variables) throws Exception {
        Agent agent = benchAgent(cls, wasteSize, frames, variables);
        benchs.bench(name, COJACBenchmark.<Callable<?>> getFromAgentClassLoader(cls, false, false, false));
        AgentTest.instrumentation.removeTransformer(agent);
    }

    private static void benchAgentRunnable(String name, Benchs benchs, String cls, boolean wasteSize, boolean frames, boolean variables) throws Exception {
        Agent agent = benchAgent(cls, wasteSize, frames, variables);
        benchs.bench(name, COJACBenchmark.<Runnable> getFromAgentClassLoader(cls, false, false, false));
        AgentTest.instrumentation.removeTransformer(agent);
    }

    private static void benchAgentArrayCallable(String name, Benchs benchs, String cls, boolean wasteSize, boolean frames, boolean variables, int size) throws Exception {
        Agent agent = benchAgent(cls, wasteSize, frames, variables);
        Callable<?> callable = getFromAgentClassLoader(cls, wasteSize, frames, variables);
        setArray(generateIntRandomArray(size), callable);
        benchs.bench(name, callable);
        AgentTest.instrumentation.removeTransformer(agent);
    }

    private static void benchAgentArrayRunnable(String name, Benchs benchs, String cls, boolean wasteSize, boolean frames, boolean variables, int size) throws Exception {
        Agent agent = benchAgent(cls, wasteSize, frames, variables);
        Runnable run = getFromAgentClassLoader(cls, wasteSize, frames, variables);
        setArray(generateIntRandomArray(size), run);
        benchs.bench(name, run);
        AgentTest.instrumentation.removeTransformer(agent);
    }

    private static void benchAgentImage(String name, Benchs benchs, String cls, boolean wasteSize, boolean frames, boolean variables, BufferedImage bufferedImage) throws Exception {
        Agent agent = benchAgent(cls, wasteSize, frames, variables);
        Runnable run = getFromAgentClassLoader(cls, wasteSize, frames, variables);
        setImage(bufferedImage, run);
        benchs.bench(name, run);
        AgentTest.instrumentation.removeTransformer(agent);
    }

    private static Args getArgs(boolean wasteSize, boolean frames, boolean variables) {
        Args args = new Args();
        args.specify(Arg.ALL);
        // args.specify(Arg.PRINT);
        args.specify(Arg.FILTER);
        args.specify(Arg.EXCEPTION);

        if (wasteSize) {
            args.specify(Arg.WASTE_SIZE);
        }

        if (frames) {
            args.specify(Arg.FRAMES);
        }

        if (variables) {
            args.specify(Arg.VARIABLES);
        }
        return args;
    }
}