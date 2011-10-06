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

package ch.eiafr.cojac.reactions;

import ch.eiafr.cojac.Arg;
import ch.eiafr.cojac.Args;
import ch.eiafr.cojac.Methods;
import ch.eiafr.cojac.Signatures;
import ch.eiafr.cojac.utils.BytecodeUtils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;

public final class ClassLoaderReaction implements Reaction {

    private final Args args;

    public ClassLoaderReaction(Args args) {
        super();

        this.args = args;
    }

    @Override
    public void insertReactionMethod(ClassVisitor cv, Methods methods) {
        //Nothing to insert
    }

    @Override
    public void insertReactionCall(MethodVisitor mv, String msg, Methods methods, String classPath) {
        mv.visitLdcInsn(msg);

        if (args.isSpecified(Arg.LOG_FILE)) {
            String logFileName = args.getValue(Arg.LOG_FILE);

            mv.visitLdcInsn(logFileName);
        }

        if (args.isSpecified(Arg.CALL_BACK)) {
            BytecodeUtils.callUserCallBack(mv, args.getValue(Arg.CALL_BACK));
        } else {
            mv.visitMethodInsn(INVOKESTATIC,
                "ch/eiafr/cojac/models/Reactions",
                getCurrentMethodReaction(),
                getCurrentSignatureReaction());
        }
    }

    private String getCurrentMethodReaction() {
        if (args.isSpecified(Arg.PRINT)) {
            return args.isSpecified(Arg.DETAILED_LOG) ? Methods.PRINT : Methods.PRINT_SMALLER;
        } else if (args.isSpecified(Arg.LOG_FILE)) {
            return args.isSpecified(Arg.DETAILED_LOG) ? Methods.LOG : Methods.LOG_SMALLER;
        } else if (args.isSpecified(Arg.EXCEPTION)) {
            return Methods.THROW;
        }

        throw new RuntimeException("System must be in one of this mode !");
    }

    private String getCurrentSignatureReaction() {
        return args.isSpecified(Arg.LOG_FILE) ? Signatures.REACT_LOG : Signatures.REACT;
    }
}
