/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.eiafr.cojac.instrumenters;

import java.util.Objects;

/**
 *
 * @author romain
 */
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