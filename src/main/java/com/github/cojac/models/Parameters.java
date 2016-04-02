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

public enum Parameters {
    INTEGER_BINARY_PARAMS(int.class, int.class),
    INTEGER_UNARY_PARAMS(int.class),
    LONG_BINARY_PARAMS(long.class, long.class),
    LONG_UNARY_PARAMS(long.class),
    FLOAT_BINARY_PARAMS(float.class, float.class),
    FLOAT_UNARY_PARAMS(float.class),
    DOUBLE_BINARY_PARAMS(double.class, double.class),
    DOUBLE_UNARY_PARAMS(double.class);
   
    
    public Class<?>[] params;
    private Parameters(Class<?>... params) {
        this.params = params;
    }
}
