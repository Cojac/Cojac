/*
 * *
 *    Copyright 2011-2016 Valentin Gazzola & Frédéric Bapst
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

package com.github.cojac;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

/**
 * Class visitor specification using the @see com.github.cojac.NewMethodVisitor
 * 
 * @author Valentin
 *
 */
public class BehaviourClassVisitor extends CojacClassVisitor {
    private int instructionCounter = 0;

    public BehaviourClassVisitor(ClassVisitor cv, CojacReferences references, CojacAnnotationVisitor cav) {
        super(cv, references, cav);
    }

    /**
     * Method called each time a method is visited in an instrumented class.
     */
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        // boolean isNative = (access & Opcodes.ACC_NATIVE) > 0;
        MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
        String currentMethodID = crtClassName + '/' + name;
        if (cav.isClassAnnotated() || cav.isMethodAnnotated(currentMethodID)) {
            return mv;
        }
        return instrumentMethod(mv, access, desc, name);
    }

    /**
     * Calls the NewMethodVisitor for each instrumented method.
     */
    private MethodVisitor instrumentMethod(MethodVisitor parentMv, int access, String desc, String name) {
        // System.out.println("in NewClassVisitor.instrumentMethod");
        MethodVisitor mv;
        mv = new BehaviourMethodVisitor(access, desc, parentMv, stats, args, crtClassName, factory, references, name, this);
        return mv;
    }

    int getInstructionCounter() {
        return instructionCounter;
    }

    void incInstructionCounter() {
        this.instructionCounter++;
    }
}