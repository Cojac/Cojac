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

public class InstrumentationDemoClass1 {

    private double myDouble1;
    private double myDouble2;

    public InstrumentationDemoClass1(double myDouble1, double myDouble2) {
        this.myDouble1 = myDouble1;
        this.myDouble2 = myDouble2;
    }

    public void add(InstrumentationDemoClass1 i) {
        this.myDouble1 += i.myDouble1;
        this.myDouble2 += i.myDouble2;
    }
    
    public void sub(InstrumentationDemoClass1 i) {
        this.myDouble1 -= i.myDouble1;
        this.myDouble2 -= i.myDouble2;
    }
    
    public double getFirstDouble() {
        return myDouble1;
    }
    
    public double getSecondDouble() {
        return myDouble2;
    }

}
