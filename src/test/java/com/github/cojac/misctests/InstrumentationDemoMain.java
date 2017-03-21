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

package com.github.cojac.misctests;

import java.math.BigDecimal;

public class InstrumentationDemoMain {

    public static void main(String[] args) {
        InstrumentationDemoClass1 i1 = new InstrumentationDemoClass1(1, 1);
        InstrumentationDemoClass1 i2 = new InstrumentationDemoClass1(1e8, 1e5);
        i1.add(i2);
        i1.sub(i2);
        System.out.println(i1.getFirstDouble());
        System.out.println(i1.getSecondDouble());
        boolean fail = false;
        try {
            if (!BigDecimal.valueOf(i1.getFirstDouble()).equals(BigDecimal.valueOf(1.0)))
                fail = true;
            if (!BigDecimal.valueOf(i1.getSecondDouble()).equals(BigDecimal.valueOf(1.0)))
                fail = true;
            if (fail) {
                System.exit(1);
            }
        } catch (Exception e) {
            System.exit(1);
        }
    }

}
