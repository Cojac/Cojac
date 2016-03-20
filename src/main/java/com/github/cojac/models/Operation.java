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

package com.github.cojac.models;

public class Operation {
    public final int opCodeVal;
    public final String opCodeName;
    public final String signature;
    public final Class<?>[] parameters;
    
    public Operation(int opCodeVal,String opCodeName, String signature, Class<?>[] parameters ) {
       this.opCodeVal=opCodeVal;
       this.opCodeName=opCodeName;
       this.signature = signature;
       this.parameters = parameters;
    }
}