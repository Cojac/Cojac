/*
 * *
 *    Copyright 2014 Romain Monnard & Frédéric Bapst
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

package ch.eiafr.cojac.instrumenters;

import java.util.Objects;

public class MethodSignature {
    
    String owner;
    String name;
    String desc;
    
    public MethodSignature(String owner, String name, String desc) {
        this.owner = owner;
        this.name = name;
        this.desc = desc;
    }
    
    
    @Override
    public boolean equals(Object o){
        if(o instanceof MethodSignature){
            MethodSignature ms = (MethodSignature)o;
            return ms.owner.equals(owner) && ms.name.equals(name) && ms.desc.equals(desc);
        }
        return false;    
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 31 * hash + Objects.hashCode(this.owner);
        hash = 31 * hash + Objects.hashCode(this.name);
        hash = 31 * hash + Objects.hashCode(this.desc);
        return hash;
    }

    
}