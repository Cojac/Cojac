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

package demo;

public class HelloDoubleAsFloat {
    public static void main(String[] args) {

        String s = "2.32340";
        System.out.println("s= "+s+",   2s ="+2*Double.parseDouble(s));
        double a=10.000002, b=2.564654654654;
        Double c = Double.valueOf(3);
        System.out.println("a = "+Double.toString(a)+", b = "+Double.toString(b)+ ", c= "+Double.toString(c));
        System.out.println(Double.toString(c));
        
        System.out.println("a + b = "+Double.toString(a+b) + "  " + (a+b));
        System.out.println("a - b = "+Double.toString(a-b));
        System.out.println("a * b = "+Double.toString(a*b));
        System.out.println("a / b = "+Double.toString(a/b));
        System.out.println("a % b = "+Double.toString(a%b));

        System.out.println("a < b = "+(a<b));
        System.out.println("sqrt(a)   = "+Double.toString(Math.sqrt(a))+"\n in double: 3.1622779763961297");
        System.out.println("pow(4,2) = "+Double.toString(Math.pow(4,2)));

        double d = 1.0;
        double e = 1E-16;
        System.out.println(" d + e = " +(d+e));
        System.out.println(" d - e = " +(d-e));
        System.out.println("-d - e = " +(-d-e));
        System.out.println("-d + e = " +(-d+e));
    }
}
    
