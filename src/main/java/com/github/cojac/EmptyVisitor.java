/*
 * *
 *    Copyright 2011-2014 Baptiste Wicht, Frédéric Bapst & Romain Monnard
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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import static com.github.cojac.CojacCommonConstants.ASM_VERSION;

class EmptyVisitor extends ClassVisitor {
    AnnotationVisitor av = new AnnotationVisitor(ASM_VERSION) {

        @SuppressWarnings("unused")
        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            return this;
        }

        @SuppressWarnings("unused")
        @Override
        public AnnotationVisitor visitArray(String name) {
            return this;
        }
    };

    public EmptyVisitor() {
        super(ASM_VERSION);
    }

    @SuppressWarnings("unused")
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return av;
    }

    @SuppressWarnings("unused")
    @Override
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        return new FieldVisitor(ASM_VERSION) {

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return av;
            }
        };
    }

    @SuppressWarnings("unused")
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        return new MethodVisitor(ASM_VERSION) {

            @Override
            public AnnotationVisitor visitAnnotationDefault() {
                return av;
            }

            @Override
            public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
                return av;
            }

            @Override
            public AnnotationVisitor visitParameterAnnotation(int parameter,
                                               String desc, boolean visible) {
                return av;
            }
        };
    }
  }

