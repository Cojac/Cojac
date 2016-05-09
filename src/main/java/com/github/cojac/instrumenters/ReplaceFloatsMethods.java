/*
 * *
 *    Copyright 2014 Frédéric Bapst & Romain Monnard
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

package com.github.cojac.instrumenters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.MethodVisitor;

import static com.github.cojac.instrumenters.InvokableMethod.*;
import static com.github.cojac.instrumenters.ReplaceFloatsInstrumenter.DN_NAME;
import static com.github.cojac.models.FloatReplacerClasses.*;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.Type;

import com.github.cojac.CojacReferences;

public class ReplaceFloatsMethods {
    
	private final CojacReferences references;
    
    private final Map<MethodSignature, String> suppressions = new HashMap<>(50);
    private final Map<MethodSignature, InvokableMethod> invocations = new HashMap<>(50);
    private final ArrayList<String> allMethodsConversions = new ArrayList<>(50);

    private static final String FL_NAME = Type.getType(Float.class).getInternalName();
    public  static final String FL_DESCR = Type.getType(Float.class).getDescriptor();
    
    private static final String DL_NAME = Type.getType(Double.class).getInternalName();
    public  static final String DL_DESCR = Type.getType(Double.class).getDescriptor();
    
    private static final String MATH_NAME = Type.getType(Math.class).getInternalName();

    private static String CFW_N;
    private static String CFW;
    private static String CDW_N;
    private static String CDW;
    
    private final FloatProxyMethod fpm;
	private static final String COJAC_MAGIC_CALL_DOUBLE_PREFIX = "COJAC_MAGIC_DOUBLE_";
	private static final String COJAC_MAGIC_CALL_FLOAT_PREFIX = "COJAC_MAGIC_FLOAT_";
    private static final String COJAC_MAGIC_CALL_NG_PREFIX = "COJAC_MAGIC_";

    public ReplaceFloatsMethods(FloatProxyMethod fpm, String crtClassName, CojacReferences references) {
		CFW_N = COJAC_FLOAT_WRAPPER_INTERNAL_NAME;
		CFW = COJAC_FLOAT_WRAPPER_TYPE_DESCR;
		CDW_N = COJAC_DOUBLE_WRAPPER_INTERNAL_NAME;
		CDW = COJAC_DOUBLE_WRAPPER_TYPE_DESCR;
        this.fpm = fpm;
		this.references = references;
		fillMethods();
    }

    private void fillMethods() {
        
        // Floats replacements
        suppressions.put(new MethodSignature(FL_NAME, "valueOf", "(F)"+FL_DESCR), CFW_N); // delete if the value is already a FloatWrapper
        suppressions.put(new MethodSignature(FL_NAME, "floatValue", "()F"), null); // delete in every case (keep FloatWrapper)

        // WRAPPER SPEC: FW.fromFloat(float) -> FW
        invocations.put(new MethodSignature(FL_NAME, "valueOf", "(F)"+FL_DESCR), new InvokableMethod(CFW_N, "fromFloat", "(F)"+CFW, INVOKESTATIC));

        // TODO: strengthen tests for "new Float/Double(...)"
        // (I just suppressed these public constructors in wrappers, and the test still passes...)
        // TODO: in general, measure test coverage
        
        // WRAPPER SPEC: FW(FW), FW(String), FW(DW)
        invocations.put(new MethodSignature(FL_NAME, "<init>", "(F)V"), new InvokableMethod(CFW_N, "<init>", "("+CFW+")V", INVOKESPECIAL));
        invocations.put(new MethodSignature(FL_NAME, "<init>", "(Ljava/lang/String;)V"), new InvokableMethod(CFW_N, "<init>", "(Ljava/lang/String;)V", INVOKESPECIAL));
        invocations.put(new MethodSignature(FL_NAME, "<init>", "(D)V"), new InvokableMethod(CFW_N, "<init>", "("+CDW+")V", INVOKESPECIAL));
        
        // WRAPPER SPEC: FW.f2d(FW)->DW, FW.f2i(FW)->int, FW.f2l(FW)->long, FW.fromString(String)->FW
        invocations.put(new MethodSignature(FL_NAME, "doubleValue", "()D"), new InvokableMethod(CFW_N, "f2d", "("+CFW+")"+CDW, INVOKESTATIC));
        invocations.put(new MethodSignature(FL_NAME, "intValue", "()I"), new InvokableMethod(CFW_N, "f2i", "("+CFW+")I", INVOKESTATIC));
        invocations.put(new MethodSignature(FL_NAME, "longValue", "()J"), new InvokableMethod(CFW_N, "f2l", "("+CFW+")J", INVOKESTATIC));

        invocations.put(new MethodSignature(FL_NAME, "parseFloat", "(Ljava/lang/String;)F"), new InvokableMethod(CFW_N, "fromString", "(Ljava/lang/String;)"+CFW, INVOKESTATIC));
        
        invocations.put(new MethodSignature(FL_NAME, "equals", "(Ljava/lang/Object;)Z"), new InvokableMethod(CFW_N, "equals", "(Ljava/lang/Object;)Z", INVOKEVIRTUAL));
        invocations.put(new MethodSignature(FL_NAME, "compareTo", "(Ljava/lang/Float;)I"), new InvokableMethod(CFW_N, "compareTo", "("+CFW+")I", INVOKEVIRTUAL));
        invocations.put(new MethodSignature("java/util/Arrays", "sort", "([F)V"), new InvokableMethod("java/util/Arrays", "sort", "([Ljava/lang/Object;)V", INVOKESTATIC));

        allMethodsConversions.add(FL_NAME); // use proxy to call every other methods from Float
               
        // Doubles replacements
        suppressions.put(new MethodSignature(DL_NAME, "valueOf", "(D)"+DL_DESCR), CDW_N); // delete if the value is already a DoubleWrapper
        suppressions.put(new MethodSignature(DL_NAME, "doubleValue", "()D"), null); // delete in every case (keep DoubleWrapper)
                
        // WRAPPER SPEC: DW.fromDouble(double) -> DW
        invocations.put(new MethodSignature(DL_NAME, "valueOf", "(D)"+DL_DESCR), new InvokableMethod(CDW_N, "fromDouble", "(D)"+CDW, INVOKESTATIC));

        // WRAPPER SPEC: DW(DW), DW(String), DW(FW)
        invocations.put(new MethodSignature(DL_NAME, "<init>", "(D)V"), new InvokableMethod(CDW_N, "<init>", "("+CDW+")V", INVOKESPECIAL));
        invocations.put(new MethodSignature(DL_NAME, "<init>", "(Ljava/lang/String;)V"), new InvokableMethod(CDW_N, "<init>", "(Ljava/lang/String;)V", INVOKESPECIAL));
        invocations.put(new MethodSignature(DL_NAME, "<init>", "(F)V"), new InvokableMethod(CDW_N, "<init>", "("+CFW+")V", INVOKESPECIAL));
        
        // WRAPPER SPEC: FW.d2f(DW)->FW, DW.d2i(DW)->int, DW.d2l(DW)->long, DW.fromString(String)->DW
        invocations.put(new MethodSignature(DL_NAME, "floatValue", "()F"), new InvokableMethod(CFW_N, "d2f", "("+CDW+")"+CFW, INVOKESTATIC)); //Bapst: was CDW_N
        invocations.put(new MethodSignature(DL_NAME, "intValue", "()I"), new InvokableMethod(CDW_N, "d2i", "("+CDW+")I", INVOKESTATIC));
        invocations.put(new MethodSignature(DL_NAME, "longValue", "()J"), new InvokableMethod(CDW_N, "d2l", "("+CDW+")J", INVOKESTATIC));

        invocations.put(new MethodSignature(DL_NAME, "parseDouble", "(Ljava/lang/String;)D"), new InvokableMethod(CDW_N, "fromString", "(Ljava/lang/String;)"+CDW, INVOKESTATIC));
        
        invocations.put(new MethodSignature(DL_NAME, "equals", "(Ljava/lang/Object;)Z"), new InvokableMethod(CDW_N, "equals", "(Ljava/lang/Object;)Z", INVOKEVIRTUAL));
        invocations.put(new MethodSignature(DL_NAME, "compareTo", "(Ljava/lang/Double;)I"), new InvokableMethod(CDW_N, "compareTo", "("+CDW+")I", INVOKEVIRTUAL));
        invocations.put(new MethodSignature("java/util/Arrays", "sort", "([D)V"), new InvokableMethod("java/util/Arrays", "sort", "([Ljava/lang/Object;)V", INVOKESTATIC));
        // TODO: consider handling other Arrays.xyz methods (binarySearch, fill...)
        // TODO: consider handling other Double/Float.compare(), maybe some others...
        allMethodsConversions.add(DL_NAME); // use proxy to call every other methods from Double

        // Math Library
        // WRAPPER SPEC: DW.min/max/pow(DW,DW) -> DW
        // WRAPPER SPEC: DW.sqrt/sin/sinh/asin/cos/cosh/acos/tan/atan/tanh(DW) -> DW
        // WRAPPER SPEC: DW.toRadians/toDegrees/exp/log/log10abs(DW) -> DW

        final String dwUnOp  = "(" + CDW + ")" + CDW;
        final String dwBinOp = "(" + CDW  + CDW + ")" + CDW;

        invocations.put(new MethodSignature(MATH_NAME, "sqrt", "(D)D"),
                new InvokableMethod(CDW_N, "math_sqrt", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "sin", "(D)D"),
                new InvokableMethod(CDW_N, "math_sin", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "sinh", "(D)D"),
                new InvokableMethod(CDW_N, "math_sinh", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "asin", "(D)D"),
                new InvokableMethod(CDW_N, "math_asin", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "cos", "(D)D"),
                new InvokableMethod(CDW_N, "math_cos", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "cosh", "(D)D"),
                new InvokableMethod(CDW_N, "math_cosh", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "acos", "(D)D"),
                new InvokableMethod(CDW_N, "math_acos", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "tan", "(D)D"),
                new InvokableMethod(CDW_N, "math_tan", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "atan", "(D)D"),
                new InvokableMethod(CDW_N, "math_atan", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "tanh", "(D)D"),
                new InvokableMethod(CDW_N, "math_tanh", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "toRadians", "(D)D"),
                new InvokableMethod(CDW_N, "math_toRadians", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "toDegrees", "(D)D"),
                new InvokableMethod(CDW_N, "math_toDegrees", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "exp", "(D)D"),
                new InvokableMethod(CDW_N, "math_exp", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "log", "(D)D"),
                new InvokableMethod(CDW_N, "math_log", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "log10", "(D)D"),
                new InvokableMethod(CDW_N, "math_log10", dwUnOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "abs", "(D)D"),
                new InvokableMethod(CDW_N, "math_abs", dwUnOp, INVOKESTATIC));

        invocations.put(new MethodSignature(MATH_NAME, "max", "(DD)D"),
                new InvokableMethod(CDW_N, "math_max", dwBinOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "min", "(DD)D"),
                new InvokableMethod(CDW_N, "math_min", dwBinOp, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME, "pow", "(DD)D"),
                new InvokableMethod(CDW_N, "math_pow", dwBinOp, INVOKESTATIC));

        // math functions put in FloatWrapper
        // WRAPPER SPEC: FW.min/max(FW,FW) -> FW
        // WRAPPER SPEC: FW.abs(FW) -> FW

        invocations.put(new MethodSignature(MATH_NAME,"abs","(F)F"),
                new InvokableMethod(CFW_N, "math_abs","(" + CFW +")" + CFW, INVOKESTATIC));

        invocations.put(new MethodSignature(MATH_NAME,"min","(FF)F"),
                new InvokableMethod(CFW_N, "math_min","(" + CFW + CFW +")" + CFW, INVOKESTATIC));
        invocations.put(new MethodSignature(MATH_NAME,"max","(FF)F"),
                new InvokableMethod(CFW_N, "math_max","(" + CFW + CFW +")" + CFW, INVOKESTATIC));

        allMethodsConversions.add(MATH_NAME);
        
        /* Special cases, where a method should be redirected. It would be 
         * another means to cope with parameters declared as Object should 
         * be unwrapped (the general case is to keep our enriched numbers,
         * especially needed for collections). 
         * Here is just an example with printf() - commented because we now 
         * handle that in the proxy (createConvertMethod).
         * 
        invocations.put(new MethodSignature("java/io/PrintWriter","printf",
                "(Ljava/lang/String;[Ljava/lang/Object;)V"),
                new InvokableMethod(DN_NAME, "myPrintWriterPrintf",
                        "(Ljava/io/PrintWriter;Ljava/lang/String;[Ljava/lang/Object;)V", INVOKESTATIC));

        invocations.put(new MethodSignature("java/io/PrintStream","printf",
                "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;"),
                new InvokableMethod(DN_NAME, "myPrintStreamPrintf",
                        "(Ljava/io/PrintStream;Ljava/lang/String;[Ljava/lang/Object;)Ljava/io/PrintStream;", INVOKESTATIC));
         */
    }
    
    /** method call instrumentation; returns true if it was instrumented 
     *  (suppressed, replaced, proxied, redirected as magic call) 
     *  @param desc
     *   the original descriptor (before replacing floats/double) */
    public boolean instrumentCall(MethodVisitor mv, int opcode, String owner, String name, String desc, Object stackTop){
        MethodSignature ms = new MethodSignature(owner, name, desc);
        
        // When we upgrade for sure to the "NewGeneration" wrappers, 
        // maybe define only one prefix...

		if(name.startsWith(COJAC_MAGIC_CALL_DOUBLE_PREFIX)) {
			cojacMagicCall(mv, name, desc, CDW_N);
			return true;
		}
		if(name.startsWith(COJAC_MAGIC_CALL_FLOAT_PREFIX)) {
			cojacMagicCall(mv, name, desc, CFW_N);
			return true;
		}
        if(name.startsWith(COJAC_MAGIC_CALL_NG_PREFIX)) {
            String magicLocation=COJAC_WRAPPER_NG_INTERNAL_NAME;
            if (name.equals("COJAC_MAGIC_toString") || 
                name.equals("COJAC_MAGIC_wrapperName") )
                magicLocation=CDW_N;
            cojacMagicCall(mv, name, desc, magicLocation);
            return true;
        }
		
        if(suppressions.containsKey(ms)) {
            Object supressionMethod = suppressions.get(ms);
            if(supressionMethod == null)
                return true;
            if(stackTop != null && stackTop.equals(supressionMethod))
                return true;
        }
        InvokableMethod replacementMethod = invocations.get(ms);
        if(replacementMethod != null) {
            replacementMethod.invoke(mv);
            return true;
        }
        if(allMethodsConversions.contains(owner)) {
            ConversionContext cc=new ConversionContext(opcode, owner, name, desc);
            if (!fpm.needsConversion(cc)) return false;
            fpm.proxyCall(mv, cc, false);
            return true;
        }
		
		if(references.hasToBeInstrumented(owner) == false) {
            ConversionContext cc=new ConversionContext(opcode, owner, name, desc);
            if (!fpm.needsConversion(cc)) return false;
			fpm.proxyCall(mv, cc, true);
			return true;
		}
		// fix for invokevirtual-of-javalib-inherited-but-not-redefined bug
		// TODO: suspected strong performance penalty... verify/add an option
		if(opcode==INVOKEVIRTUAL) {
            ConversionContext cc=new ConversionContext(opcode, owner, name, desc);
            if (!fpm.needsConversion(cc)) return false;
            fpm.proxyCall(mv, cc, true);
            return true;
		}
		
        return false;
    }
	
	private void cojacMagicCall(MethodVisitor mv, String name, String desc, String wrapper){
		String newDesc = replaceFloatMethodDescription(desc);
		InvokableMethod im = new InvokableMethod(wrapper, name, newDesc);
		im.invokeStatic(mv);
	}
}
