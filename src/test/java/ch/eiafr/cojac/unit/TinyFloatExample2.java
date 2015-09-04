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

package ch.eiafr.cojac.unit;
import java.util.ArrayList;
import java.util.LinkedList;

//TODO: a reasonable/coherent test example for FloatReplace

public class TinyFloatExample2 {

    static Double testDoubleWrapper(Double a, Double b){
        Double d1 = 1.5;
        Double d2 = new Double(d1);
        d1.compareTo(d2);
        return a;
    }
    
    public static void go() {
        Double fw1 = 8.6;
        Double fwres = testDoubleWrapper(fw1, fw1);
        System.out.println(fwres);
    }
    
    public static void main(String... args) {
        go();
    }
}

//double val = 5.2;
//float fval = 7.91f;
// Float creation

/*
======================== BEFORE testDoubleWrapper(Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;)Lch/eiafr/cojac/models/wrappers/BigDecimalDouble; ==================
name.visitCode();
Label l0 = new Label();
name.visitLabel(l0);
name.visitLineNumber(31, l0);
name.visitLdcInsn(new Double("1.5"));
name.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;", false);
name.visitVarInsn(ASTORE, 2);
Label l1 = new Label();
name.visitLabel(l1);
name.visitLineNumber(32, l1);
name.visitTypeInsn(NEW, "java/lang/Double");
name.visitInsn(DUP);
name.visitVarInsn(ALOAD, 2);
name.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D", false);
name.visitMethodInsn(INVOKESPECIAL, "java/lang/Double", "<init>", "(D)V", false);
name.visitVarInsn(ASTORE, 3);
Label l2 = new Label();
name.visitLabel(l2);
name.visitLineNumber(44, l2);
name.visitVarInsn(ALOAD, 2);
name.visitVarInsn(ALOAD, 3);
name.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "compareTo", "(Ljava/lang/Double;)I", false);
name.visitInsn(POP);
Label l3 = new Label();
name.visitLabel(l3);
name.visitLineNumber(46, l3);
name.visitVarInsn(ALOAD, 0);
name.visitInsn(ARETURN);
Label l4 = new Label();
name.visitLabel(l4);
name.visitLocalVariable("a", "Ljava/lang/Double;", null, l0, l4, 0);
name.visitLocalVariable("b", "Ljava/lang/Double;", null, l0, l4, 1);
name.visitLocalVariable("d1", "Ljava/lang/Double;", null, l1, l4, 2);
name.visitLocalVariable("d2", "Ljava/lang/Double;", null, l2, l4, 3);
name.visitMaxs(4, 4);
======================== FINAL  testDoubleWrapper(Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;)Lch/eiafr/cojac/models/wrappers/BigDecimalDouble; ==================
name.visitCode();
Label l0 = new Label();
name.visitLabel(l0);
name.visitLineNumber(31, l0);
name.visitLdcInsn(new Double("1.5"));
name.visitMethodInsn(INVOKESTATIC, "ch/eiafr/cojac/models/wrappers/BigDecimalDouble", "fromDouble", "(D)Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;", false);
name.visitVarInsn(ASTORE, 1);
Label l1 = new Label();
name.visitLabel(l1);
name.visitLineNumber(32, l1);
name.visitTypeInsn(NEW, "ch/eiafr/cojac/models/wrappers/BigDecimalDouble");
name.visitInsn(DUP);
name.visitVarInsn(ALOAD, 1);
name.visitMethodInsn(INVOKESPECIAL, "ch/eiafr/cojac/models/wrappers/BigDecimalDouble", "<init>", "(Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;)V", false);
name.visitVarInsn(ASTORE, 0);
Label l2 = new Label();
name.visitLabel(l2);
name.visitLineNumber(44, l2);
name.visitVarInsn(ALOAD, 1);
name.visitVarInsn(ALOAD, 0);
name.visitMethodInsn(INVOKESTATIC, "ch/eiafr/cojac/unit/TinyFloatExample2", "COJAC_TYPE_CONVERT", "(Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;)[Ljava/lang/Object;", false);
name.visitInsn(DUP2);
name.visitVarInsn(ASTORE, 4);
name.visitVarInsn(ASTORE, 5);
name.visitInsn(DUP);
name.visitLdcInsn(new Integer(0));
name.visitInsn(AALOAD);
name.visitTypeInsn(CHECKCAST, "[Ljava/lang/Object;");
name.visitLdcInsn(new Integer(0));
name.visitInsn(AALOAD);
name.visitTypeInsn(CHECKCAST, "ch/eiafr/cojac/models/wrappers/BigDecimalDouble");
name.visitInsn(SWAP);
name.visitInsn(POP);
Label l3 = new Label();
Label l4 = new Label();
Label l5 = new Label();
name.visitTryCatchBlock(l3, l4, l5, "java/lang/ClassCastException");
name.visitTryCatchBlock(l3, l4, l5, "java/lang/NoSuchMethodError");
name.visitTryCatchBlock(l3, l4, l5, "java/lang/AbstractMethodError");
name.visitLabel(l3);
name.visitTypeInsn(CHECKCAST, "java/lang/Double");
name.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "compareTo", "(Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;)I", false);
name.visitLabel(l4);
Label l6 = new Label();
name.visitJumpInsn(GOTO, l6);
name.visitLabel(l5);
name.visitFrame(Opcodes.F_NEW, 2, new Object[] {"ch/eiafr/cojac/models/wrappers/BigDecimalDouble", "ch/eiafr/cojac/models/wrappers/BigDecimalDouble"}, 1, new Object[] {"java/lang/ClassCastException"});
name.visitInsn(NOP);
name.visitInsn(POP);
name.visitVarInsn(ALOAD, 5);
name.visitTypeInsn(CHECKCAST, "ch/eiafr/cojac/models/wrappers/BigDecimalDouble");
name.visitVarInsn(ALOAD, 4);
name.visitInsn(NOP);
name.visitInsn(NOP);
name.visitInsn(SWAP);
name.visitMethodInsn(INVOKESTATIC, "ch/eiafr/cojac/models/wrappers/BigDecimalDouble", "toRealDoubleWrapper", "(Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;)Ljava/lang/Double;", false);
name.visitInsn(SWAP);
name.visitInsn(DUP_X1);
name.visitInsn(DUP);
name.visitLdcInsn(new Integer(0));
name.visitInsn(AALOAD);
name.visitTypeInsn(CHECKCAST, "[Ljava/lang/Object;");
name.visitLdcInsn(new Integer(1));
name.visitInsn(AALOAD);
name.visitTypeInsn(CHECKCAST, "java/lang/Double");
name.visitInsn(SWAP);
name.visitInsn(POP);
name.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "compareTo", "(Ljava/lang/Double;)I", false);
name.visitInsn(SWAP);
name.visitInsn(POP);
name.visitLabel(l6);
name.visitInsn(NOP);
name.visitFrame(Opcodes.F_NEW, 2, new Object[] {"ch/eiafr/cojac/models/wrappers/BigDecimalDouble", "ch/eiafr/cojac/models/wrappers/BigDecimalDouble"}, 3, new Object[] {null, null, Opcodes.INTEGER});
name.visitInsn(NOP);
name.visitInsn(POP);
Label l7 = new Label();
name.visitLabel(l7);
name.visitLineNumber(46, l7);
name.visitVarInsn(ALOAD, 0);
name.visitInsn(ARETURN);
Label l8 = new Label();
name.visitLabel(l8);
name.visitLocalVariable("a", "Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;", null, l0, l8, 0);
name.visitLocalVariable("b", "Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;", null, l0, l8, 0);
name.visitLocalVariable("d1", "Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;", null, l1, l8, 1);
name.visitLocalVariable("d2", "Lch/eiafr/cojac/models/wrappers/BigDecimalDouble;", null, l2, l8, 0);
name.visitMaxs(5, 6); 
 */