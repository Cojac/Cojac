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
/**
 * Enum representing all used opcode signatures in Strings
 * 
 * format: "([param]*)[return]?" 
 * Where param and return is of the following types:
 * <ul>
 * <li>'I' for int</li>
 * <li>'J' for long</li>
 * <li>'F' for float</li>
 * <li>'D' for double</li>
 * </ul>
 * 
 * @author Valentin
 *
 */
public enum Signatures {
    INTEGER_BINARY("(II)I"),
    INTEGER_UNARY("(I)I"),
    INTEGER_VOID("()I"),
   
    LONG_BINARY("(JJ)J"),
    LONG_UNARY("(J)J"),
    LONG_VOID("()J"),
    
    DOUBLE_BINARY("(DD)D"),
    DOUBLE_UNARY("(D)D"),
    DOUBLE_CMP("(DD)I"),
    DOUBLE_VOID("()D"),
    
    FLOAT_BINARY("(FF)F"),
    FLOAT_UNARY("(F)F"),
    FLOAT_CMP("(FF)I"),
    FLOAT_VOID("()F"),
    
    I2S_CAST("(I)I"),
    I2C_CAST("(I)I"),
    I2B_CAST("(I)I"),
    I2F_CAST("(I)F"),
    I2L_CAST("(I)L"),
    I2D_CAST("(I)D"),
    
    L2I_CAST("(J)I"),
    L2D_CAST("(J)D"),
    L2F_CAST("(J)F"),
    
    F2I_CAST("(F)I"),
    F2L_CAST("(F)J"),
    F2D_CAST("(F)D"),
    
    D2F_CAST("(D)F"),
    D2I_CAST("(D)I"),
    D2L_CAST("(D)J");
    
    public final String description;
    private Signatures(String descr){
        description = descr;
    }
}
