/*
 *    Copyright 2017 Frédéric Bapst et al.
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
 */

package com.github.cojac.utils;

public class Instruction {
    public int opCode;
    public String opName;
    public int lineNumber;
    public int globalInstructionNumber;
    public int localInstructionNumber;
    public String invokedMethod;
    public String behaviour;

    public Instruction() {
    }

    public Instruction(int opCode, String opName, int lineNumber, int globalInstructionNumber, int localInstructionNumber, String invokedMethod, String behaviour) {
        this.opCode = opCode;
        this.opName = opName;
        this.lineNumber = lineNumber;
        this.globalInstructionNumber = globalInstructionNumber;
        this.localInstructionNumber = localInstructionNumber;
        this.invokedMethod = invokedMethod;
        this.behaviour = behaviour;
    }

    public int getOpCode() {
        return opCode;
    }

    public void setOpCode(int opCode) {
        this.opCode = opCode;
    }

    public String getOpName() {
        return opName;
    }

    public void setOpName(String opName) {
        this.opName = opName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public int getGlobalInstructionNumber() {
        return globalInstructionNumber;
    }

    public void setGlobalInstructionNumber(int globalInstructionNumber) {
        this.globalInstructionNumber = globalInstructionNumber;
    }

    public int getLocalInstructionNumber() {
        return localInstructionNumber;
    }

    public void setLocalInstructionNumber(int localInstructionNumber) {
        this.localInstructionNumber = localInstructionNumber;
    }

    public String getInvokedMethod() {
        return invokedMethod;
    }

    public void setInvokedMethod(String invokedMethod) {
        this.invokedMethod = invokedMethod;
    }

    public String getBehaviour() {
        return behaviour;
    }

    public void setBehaviour(String behaviour) {
        this.behaviour = behaviour;
    }

}
