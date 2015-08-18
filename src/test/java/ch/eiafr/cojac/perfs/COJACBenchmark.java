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
import ch.eiafr.cojac.perfs.opcodes.*;
import ch.eiafr.cojac.perfs.scimark.SciMark;
import ch.eiafr.cojac.Agent;
import ch.eiafr.cojac.CojacReferences.CojacReferencesBuilder;
import ch.eiafr.cojac.unit.AgentTest;
import com.wicht.benchmark.utils.Benchs;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Random;
import java.util.concurrent.Callable;

public class COJACBenchmark {
    public static final int ACTIONS = 52;
    private static Args theArgs = new Args();
    static {
        theArgs.specify(Arg.ALL);
        theArgs.specify(Arg.PRINT);
        theArgs.specify(Arg.FILTER); 
    }

    public static void main(String[] args) throws Exception {
        System.out.println("COJAC Benchmark");
        bench(1, "Traveling Salesman", new TravelingSalesmanRunnable(), "ch.eiafr.cojac.perfs.TravelingSalesmanRunnable");
        if (true) return;
        benchSum();
        benchSort();

//        bench(ACTIONS, "IADD Benchmark", new IADDCallable(), "ch.eiafr.cojac.perfs.opcodes.IADDCallable");
//        bench(ACTIONS, "ISUB Benchmark", new ISUBCallable(), "ch.eiafr.cojac.perfs.opcodes.ISUBCallable");
//        bench(ACTIONS, "IMUL Benchmark", new IMULCallable(), "ch.eiafr.cojac.perfs.opcodes.IMULCallable");
//        bench(ACTIONS, "IDIV Benchmark", new IDIVCallable(), "ch.eiafr.cojac.perfs.opcodes.IDIVCallable");

//        bench(ACTIONS, "FADD Benchmark", new FADDCallable(), "ch.eiafr.cojac.perfs.opcodes.FADDCallable");
//        bench(ACTIONS, "FSUB Benchmark", new FSUBCallable(), "ch.eiafr.cojac.perfs.opcodes.FSUBCallable");
//        bench(ACTIONS, "FMUL Benchmark", new FMULCallable(), "ch.eiafr.cojac.perfs.opcodes.FMULCallable");
//        bench(ACTIONS, "FDIV Benchmark", new FDIVCallable(), "ch.eiafr.cojac.perfs.opcodes.FDIVCallable");
//        bench(ACTIONS, "FREM Benchmark", new FREMCallable(), "ch.eiafr.cojac.perfs.opcodes.FREMCallable");
//
//        bench(ACTIONS, "LADD Benchmark", new LADDCallable(), "ch.eiafr.cojac.perfs.opcodes.LADDCallable");
//        bench(ACTIONS, "LSUB Benchmark", new LSUBCallable(), "ch.eiafr.cojac.perfs.opcodes.LSUBCallable");
//        bench(29,      "LMUL Benchmark", new LMULCallable(), "ch.eiafr.cojac.perfs.opcodes.LMULCallable");
//        bench(ACTIONS, "LDIV Benchmark", new LDIVCallable(), "ch.eiafr.cojac.perfs.opcodes.LDIVCallable");
//
//        bench(ACTIONS, "DADD Benchmark", new DADDCallable(), "ch.eiafr.cojac.perfs.opcodes.DADDCallable");
//        bench(ACTIONS, "DSUB Benchmark", new DSUBCallable(), "ch.eiafr.cojac.perfs.opcodes.DSUBCallable");
//        bench(ACTIONS, "DMUL Benchmark", new DMULCallable(), "ch.eiafr.cojac.perfs.opcodes.DMULCallable");
        bench(ACTIONS, "DDIV Benchmark", new DDIVCallable(), "ch.eiafr.cojac.perfs.opcodes.DDIVCallable");
//        bench(ACTIONS, "DREM Benchmark", new DREMCallable(), "ch.eiafr.cojac.perfs.opcodes.DREMCallable");
//        bench(ACTIONS, "DCMP Benchmark", new DCMPCallable(), "ch.eiafr.cojac.perfs.opcodes.IADDCallable");

        if(true) return;
        bench(1, "Rabin Karp", new StringSearchingRunnable(), "ch.eiafr.cojac.perfs.StringSearchingRunnable");
        bench(1, "Sweeping Plane", new SweepingSorterRunnable(), "ch.eiafr.cojac.perfs.SweepingSorterRunnable");

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


        linpackBenchmark();
        sciMarkBenchmark();
    }

    private static void benchSum() throws Exception {
        int arraySize = 10_000_000; //arraySize=1000;

        benchWithArrays(1, "Sum (Foreach)", new IntForeachSumCallable(), "ch.eiafr.cojac.perfs.IntForeachSumCallable", arraySize);
        benchWithArrays(1, "Sum (For)", new IntForSumCallable(), "ch.eiafr.cojac.perfs.IntForSumCallable", arraySize);
    }

    private static void benchSort() throws Exception {
        int arraySize = 1_000_000; //arraySize=1000;

        benchWithArrays(1, "Sort (Java)", new JavaSortRunnable(), "ch.eiafr.cojac.perfs.JavaSortRunnable", arraySize);
        benchWithArrays(1, "Quicksort", new QuickSortRunnable(), "ch.eiafr.cojac.perfs.QuickSortRunnable", arraySize);
        benchWithArrays(1, "Shell Sort", new ShellSortRunnable(), "ch.eiafr.cojac.perfs.ShellSortRunnable", arraySize);
    }

    private static void linpackBenchmark() throws Exception {
        System.out.println("Start linpack not instrumented");
        new Linpack().run_benchmark();

        System.out.println("Start linpack instrumented");
        Object linpack = null; //TODO getFromClassLoader("ch.eiafr.cojac.perfs.Linpack", false);
        Method m = linpack.getClass().getMethod("run_benchmark");

        m.invoke(linpack);
    }

    private static void sciMarkBenchmark() throws Exception {
        System.out.println("Start SciMMark2 not instrumented");
        new SciMark().run(false);

        System.out.println("Start SciMMark2 instrumented");
        Object scimark = null; //TODO getFromClassLoader("ch.eiafr.cojac.perfs.scimark.SciMark", false);
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
        benchs.bench("Not instrumented", runnable);
        benchAgentVariant("Agent",            benchs, cls, false, -1, null);
        benchs.generateCharts(false);
        benchs.printResults();
    }

    private static void bench(int actions, String title, Runnable runnable, String cls) throws Exception {
        Benchs benchs = new Benchs(title);
        initBench(actions, benchs);
        benchs.bench("Not instrumented", runnable);
        benchAgentVariant("Agent",            benchs, cls,  true, -1, null);
        benchs.generateCharts(false);
        benchs.printResults();
    }

    private static void benchWithArrays(int actions, String title, Runnable runnable, String cls, int size) throws Exception {
        Benchs benchs = new Benchs(title);
        initBench(actions, benchs);
        benchWithArrays1(benchs, "Not instrumented", runnable, size);
        benchAgentVariant("Agent", benchs, cls,  true, size, null);
        benchs.generateCharts(false);
        benchs.printResults();
    }
    
    private static void benchWithArrays1(Benchs benchs, String item, Runnable runnable, int size) throws Exception {
        setArray(generateIntRandomArray(size), runnable);
        benchs.bench(item, runnable);
    }

    private static void benchWithArrays(int actions, String title, Callable<?> runnable, String cls, int size) throws Exception {
        Benchs benchs = new Benchs(title);
        initBench(actions, benchs);
        benchWithArrays2(benchs, "Not instrumented", runnable, size);
        benchAgentVariant("Agent", benchs, cls, false, size, null);
        benchs.generateCharts(false);
        benchs.printResults();
    }

    private static void benchWithArrays2(Benchs benchs, String item, Callable<?>  runnable, int size) throws Exception {
        setArray(generateIntRandomArray(size), runnable);
        benchs.bench(item, runnable);
    }

    private static void benchWithImages(int actions, String title, Runnable runnable, String cls, BufferedImage bufferedImage) throws Exception {
        Benchs benchs = new Benchs(title);
        initBench(actions, benchs);
        benchWithImages1(benchs, "Not instrumented", runnable, bufferedImage);
        benchAgentVariant("Agent", benchs, cls, true, -1, bufferedImage);
        benchs.generateCharts(false);
        benchs.printResults();
    }

    private static void benchWithImages1(Benchs benchs, String item, Runnable runnable, BufferedImage bufferedImage) throws Exception {
        setImage(bufferedImage, runnable);
        benchs.bench(item, runnable);
    }

    private static void initBench(int actions, Benchs benchs) {
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

    @SuppressWarnings("unchecked")
    private static <T> T getFromAgentClassLoader(String cls) throws Exception {
        Class<?> instanceClass = ClassLoader.getSystemClassLoader().loadClass(cls);
        // TODO: retransforming classes fails with our Wrapping mechanism, which modifies signatures...
        AgentTest.instrumentation.retransformClasses(instanceClass);
        return (T) instanceClass.newInstance();
    }

    private static Agent benchAgent() throws Exception {
        CojacReferencesBuilder builder = new CojacReferencesBuilder(getArgs());
        Agent agent = new Agent(builder.build());
        AgentTest.instrumentation.addTransformer(agent, true);
        return agent;
    }

    private static void benchAgentVariant(String name, Benchs benchs, String cls,
             boolean runnable, int size, BufferedImage bufImg) throws Exception {
        Agent agent = benchAgent();
        Runnable run=null;
        Callable<?> callable = null;
        Object runnableOrCallable=null;
        if (runnable){
            runnableOrCallable = run = getFromAgentClassLoader(cls);   //COJACBenchmark.<Runnable> getFromAgentClassLoader(cls)
        } else {
            runnableOrCallable = callable = getFromAgentClassLoader(cls);
        }
        if (size>=0) 
            setArray(generateIntRandomArray(size), runnableOrCallable);
        if (bufImg != null) {
            setImage(bufImg, runnableOrCallable);
        }
        if (runnable) {
            benchs.bench(name, run);
        } else {
            benchs.bench(name, callable);
        }
        AgentTest.instrumentation.removeTransformer(agent);
        COJACBenchmark.<Callable<?>> getFromAgentClassLoader(cls); // gets back to an uninstrumented class definition
    }

    private static Args getArgs() {
        return theArgs;
    }
    
    public static void setArgs(Args args) {
        theArgs=args;
    }
    // ===================================================================
    // TODO: retransforming classes fails with our Wrapping mechanism, which modifies signatures...
    public static class COJACBenchmarkIntervalWrapper {
        public static void main(String[] t) throws Exception {
            Args a=new Args();
            a.specify(Arg.INTERVAL);
            a.specify(Arg.PRINT);
            a.specify(Arg.FILTER); 
            setArgs(a);
            COJACBenchmark.main(t);
        }
    }
}