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

package com.github.cojac;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;

import com.github.cojac.models.NoCojacInstrumentation;

public final class CojacAnnotationVisitor extends EmptyVisitor {
    private static final String ANNOTATION_NAME = NoCojacInstrumentation.class.getSimpleName() + ";";
    private final InstrumentationStats stats;
    private boolean first = true;
    private boolean classAnnotated = false;
    private String classPath;
    private List<String> blacklistedMethods = new ArrayList<String>(10);
    private String lastVisitedMethod;

    public CojacAnnotationVisitor(final InstrumentationStats stats) {
        this.stats = stats;
    }

    @SuppressWarnings("unused")
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        classPath = name;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        first = false;
        lastVisitedMethod = classPath + "/" + name;

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, boolean visible) {
        if (first) {
            if (name.endsWith(ANNOTATION_NAME)) {
                classAnnotated = true;
            }
        } else if (!classAnnotated) {
            if (name.endsWith(ANNOTATION_NAME)) {
                blacklistedMethods.add(lastVisitedMethod);
                stats.addBlackList(lastVisitedMethod);
            }
        }

        return super.visitAnnotation(name, visible);
    }

    public boolean isClassAnnotated() {
        return classAnnotated;
    }

    public boolean isMethodAnnotated(String method) {
        return blacklistedMethods.contains(method);
    }
}
