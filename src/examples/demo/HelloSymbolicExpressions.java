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

// Run with "Symbolic" Wrapper: ... cojac.jar="-Rsymb"

package demo;

public class HelloSymbolicExpressions {
    public static String COJAC_MAGIC_toString(double n) { return "-"; }
    
    static float sum(float[] t) {
        float r=0.0f;
        for(float e:t) r += e;
        return r;
    }

    public static void main(String[] args) {
        double a = 3.1, b = a - 0.1;
        double c = b * (a / (a - 0.1));
        System.out.println(c);
        System.out.println(COJAC_MAGIC_toString(c));
        float[] t={23.0f, 0.0000008f, 0.0000009f};
        System.out.println(sum(t));
    }
}
